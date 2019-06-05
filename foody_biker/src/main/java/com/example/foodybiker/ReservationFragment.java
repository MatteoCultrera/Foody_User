package com.example.foodybiker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class ReservationFragment extends Fragment {

    TextView restaurantName, restaurantAddress, userName,
            userAddress, notes, orderDelivered, primaryText, secondaryText, pickupTime, deliveryTime;
    ConstraintLayout orderDeliveredLayout, mainLayout, noteLayout;
    private CircleImageView deliverImage;
    boolean canClick;
    CardView card;
    private ArrayList<Reservation> reservations;
    private Reservation activeReservation;
    private RecyclerView orderList;
    private ImageButton switchButton, navigationRest, navigationUser;
    private RVAdapterReservation adapter;
    private boolean toAdd;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private MainActivity father;
    private SharedPreferences sharedPreferences;
    private int pending;
    LinearLayout callRestaurant, callUser;
    private Geocoder geocoder;
    Double distance = 0.0;
    Double totalDistance = 0.0;
    Double currLatitude = 0.0;
    Double currLongitude = 0.0;
    private File storageDir;
    private String MAIN_DIR = "user_utils";

    public ReservationFragment() {
    }

    public void setFather(MainActivity father) {
        this.father = father;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    public void init(final View view) {
        final ReservationFragment ref = this;
        toAdd = true;
        storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        sharedPreferences = view.getContext().getSharedPreferences("myPreference", MODE_PRIVATE);
        pending = sharedPreferences.getInt("pending", 0);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        activeReservation = null;
        reservations = new ArrayList<>();
        geocoder = new Geocoder(this.getContext(), Locale.getDefault());
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("reservations").child("Bikers");
        Query query = database.child(firebaseUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ReservationDBBiker reservationDB = ds.getValue(ReservationDBBiker.class);
                    if (reservationDB.getStatus() == null) {
                        Reservation reservation = new Reservation(reservationDB.getRestaurantName(), reservationDB.getRestaurantAddress(),
                                reservationDB.getOrderTimeBiker(), reservationDB.getUserName(), reservationDB.getUserAddress(),
                                reservationDB.getOrderTime(), reservationDB.getRestaurantID(), reservationDB.getNotes(), false);
                        reservation.setReservationID(ds.getKey());
                        reservation.setUserId();
                        reservation.setUserPhone(reservationDB.getUserPhone());
                        reservation.setRestPhone(reservationDB.getRestPhone());
                        reservations.add(reservation);
                    } else if (reservationDB.getStatus().equals("accepted")) {
                        Reservation reservation = new Reservation(reservationDB.getRestaurantName(), reservationDB.getRestaurantAddress(),
                                reservationDB.getOrderTimeBiker(), reservationDB.getUserName(), reservationDB.getUserAddress(),
                                reservationDB.getOrderTime(), reservationDB.getRestaurantID(), reservationDB.getNotes(), true);
                        reservation.setReservationID(ds.getKey());
                        reservation.setUserId();
                        reservation.setUserPhone(reservationDB.getUserPhone());
                        reservation.setRestPhone(reservationDB.getRestPhone());
                        activeReservation = reservation;
                    }
                }
                adapter = new RVAdapterReservation(reservations, ref, activeReservation != null);
                setActiveReservation(activeReservation);
                setInterface(activeReservation != null);

                orderList.setAdapter(adapter);
                notes.setMovementMethod(new ScrollingMovementMethod());
                LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
                orderList.setLayoutManager(llm);

                //notification
                DatabaseReference bikerReservations = FirebaseDatabase.getInstance().getReference().child("reservations")
                        .child("Bikers").child(firebaseUser.getUid());
                bikerReservations.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        ReservationDBBiker reservationDB = dataSnapshot.getValue(ReservationDBBiker.class);
                        if (reservationDB.getStatus() == null) {
                            toAdd = true;
                            if (reservations != null) {
                                for (Reservation r : reservations) {
                                    if (r.getReservationID().equals(reservationDB.getReservationID())) {
                                        toAdd = false;
                                    }
                                }
                            }
                            if (toAdd) {
                                Reservation reservation = new Reservation(reservationDB.getRestaurantName(), reservationDB.getRestaurantAddress(),
                                        reservationDB.getOrderTimeBiker(), reservationDB.getUserName(), reservationDB.getUserAddress(),
                                        reservationDB.getOrderTime(), reservationDB.getRestaurantID(), reservationDB.getNotes(), false);
                                reservation.setReservationID(dataSnapshot.getKey());
                                reservation.setUserId();
                                reservation.setUserPhone(reservationDB.getUserPhone());
                                reservation.setRestPhone(reservationDB.getRestPhone());

                                int index;
                                for (index = 0; index < reservations.size(); index++) {
                                    if (reservation.getUserDeliveryTime().compareTo(reservations.get(index).getUserDeliveryTime()) < 0)
                                        break;
                                }
                                reservations.add(index, reservation);
                                adapter.notifyItemInserted(index);
                                adapter.notifyItemRangeChanged(index, reservations.size());
                                updateTitles();
                                father.newReservation(reservation);
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        restaurantName = view.findViewById(R.id.pickup_restaurant_name);
        restaurantAddress = view.findViewById(R.id.pickup_restaurant_address);
        userName = view.findViewById(R.id.deliver_user_name);
        userAddress = view.findViewById(R.id.deliver_user_address);
        notes = view.findViewById(R.id.notes_box);
        orderDeliveredLayout = view.findViewById(R.id.order_delivered_layout);
        orderDelivered = view.findViewById(R.id.order_delivered);
        mainLayout = view.findViewById(R.id.main_layout);
        card = view.findViewById(R.id.card_order);
        card.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        orderList = view.findViewById(R.id.order_list);
        primaryText = view.findViewById(R.id.string_up);
        secondaryText = view.findViewById(R.id.string_down);
        switchButton = view.findViewById(R.id.switch_button);
        noteLayout = view.findViewById(R.id.note_layout);
        pickupTime = view.findViewById(R.id.pickup_time);
        deliveryTime = view.findViewById(R.id.deliver_time);
        callRestaurant = view.findViewById(R.id.call_restaurant);
        callUser = view.findViewById(R.id.call_user);
        navigationRest = view.findViewById(R.id.navigation_to_rest);
        navigationUser = view.findViewById(R.id.navigation_to_user);
        deliverImage = view.findViewById(R.id.deliver_image);
        orderDeliveredLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        canClick = false;

        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderDelivered.setText("");
                canClick = false;
            }
        });

        orderDeliveredLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick && card.getVisibility() == View.VISIBLE) {
                    orderDelivered.setText(getString(R.string.order_delivered));
                    canClick = true;
                } else if (card.getVisibility() == View.VISIBLE) {
                    //Deleting the user picture of the order just delivered
                    if(sharedPreferences.contains("customerProfileImage")){
                        File profileImage = new File(sharedPreferences.getString("customerProfileImage",""));
                        profileImage.delete();
                        sharedPreferences.edit().remove("customerProfileImage").apply();
                        Log.d("PROVAIMAGE", "l'immagine è stata rimossa");
                    }

                    DatabaseReference databaseB = FirebaseDatabase.getInstance().getReference()
                            .child("Bikers").child(firebaseUser.getUid());
                    HashMap<String, Object> childB = new HashMap<>();
                    childB.put("status", "free");
                    databaseB.updateChildren(childB).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(father, R.string.error_order, Toast.LENGTH_SHORT).show();
                        }
                    });

                    calculateDistance(activeReservation);

                    Calendar calendar = Calendar.getInstance();
                    String monthYear = calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.YEAR);
                    DatabaseReference databaseRest = FirebaseDatabase.getInstance().getReference()
                            .child("archive").child("Bikers").child(firebaseUser.getUid()).child(monthYear);
                    ReservationDBBiker reservation = new ReservationDBBiker(activeReservation.getReservationID(),
                            activeReservation.getUserDeliveryTime(), activeReservation.getRestaurantPickupTime(),
                            activeReservation.getRestaurantName(), activeReservation.getUserName(),
                            activeReservation.getRestaurantAddress(), activeReservation.getUserAddress(),
                            activeReservation.getRestaurantID());
                    reservation.setStatus("delivered");
                    HashMap<String, Object> childSelf = new HashMap<>();
                    childSelf.put(activeReservation.getReservationID(), reservation);
                    databaseRest.updateChildren(childSelf).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(father, R.string.error_order, Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            DatabaseReference databaseDelete = FirebaseDatabase.getInstance().getReference()
                                    .child("reservations").child("Bikers").child(firebaseUser.getUid())
                                    .child(activeReservation.getReservationID());
                            databaseDelete.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    father.noActiveReservation(activeReservation);
                                    setInterface(false);
                                    canClick = false;
                                    setActiveReservation(null);
                                    adapter.setOrderActive(false);
                                    orderDelivered.setText("");
                                    updateTitles();
                                }
                            });
                        }
                    });
                    String userUID = reservation.getReservationID().substring(0, 28);
                    DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference()
                            .child("reservations").child("users").child(userUID).child(reservation.getReservationID());
                    databaseUser.child("delivered").setValue(true);

                    final DatabaseReference databaseDelivered = FirebaseDatabase.getInstance().getReference()
                            .child("archive").child("Bikers").child(firebaseUser.getUid()).child("delivered");
                    databaseDelivered.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                int count = dataSnapshot.getValue(int.class);
                                count ++;
                                databaseDelivered.setValue(count);
                            } else {
                                databaseDelivered.setValue(1);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    final DatabaseReference databaseFreq = FirebaseDatabase.getInstance().getReference()
                            .child("archive").child("Bikers").child(firebaseUser.getUid()).child("frequency");
                    databaseFreq.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                HashMap<String, Object> frequencies = new HashMap<>();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    frequencies.put(ds.getKey(), ds.getValue(Integer.class));
                                }

                                String hour = activeReservation.getUserDeliveryTime().split(":")[0];
                                Integer count = (Integer) frequencies.get(hour) + 1;
                                frequencies.put(hour, count);
                                databaseFreq.updateChildren(frequencies);
                            } else {
                                HashMap<String, Object> frequencies = new HashMap<>();
                                for (int i = 0; i < 24; i ++){
                                    frequencies.put(String.valueOf(i), 0);
                                }

                                String hour = activeReservation.getUserDeliveryTime().split(":")[0];
                                Integer count = (Integer) frequencies.get(hour) + 1;
                                frequencies.put(hour, count);
                                databaseFreq.updateChildren(frequencies);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderDelivered.setText("");
                canClick = false;
                if (card.getVisibility() == View.VISIBLE)
                    setInterface(false);
                else
                    setInterface(true);
                updateTitles();
            }
        });

        callRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + activeReservation.getRestPhone()));
                startActivity(intent);
            }
        });

        callUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + activeReservation.getUserPhone()));
                startActivity(intent);
            }
        });

        boolean mapsFound = true;
        String packageName = "com.google.android.apps.maps";
        ApplicationInfo appInfo = null;
        try {
            appInfo = getActivity().getPackageManager().getApplicationInfo(packageName, 0);

        } catch (PackageManager.NameNotFoundException e) {
            mapsFound = false;
            navigationUser.setVisibility(View.GONE);
            navigationRest.setVisibility(View.GONE);
        }

        if (mapsFound) {
            navigationRest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String address = activeReservation.getRestaurantAddress();
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + address + "&mode=b");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            });

            navigationUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String address = activeReservation.getUserAddress();
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + address + "&mode=b");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            });
        }
    }

    public void updateTitles() {
        if (card.getVisibility() == View.GONE) {
            primaryText.setText(String.format(Locale.getDefault(), "%d %s", reservations.size(),
                    getActivity().getString(R.string.pending_orders)));
            //primaryText.setText(reservations.size()+" "+getString(R.string.pending_orders));
            if (activeReservation == null) {
                secondaryText.setText(getString(R.string.no_order_deliver));
            } else {
                secondaryText.setText(getString(R.string.delivering_order));
            }
        } else {
            if (activeReservation == null) {
                primaryText.setText(getString(R.string.no_order_deliver));
            } else {
                primaryText.setText(getString(R.string.delivering_order));
            }
            secondaryText.setText(reservations.size() + " " + getString(R.string.pending_orders));
        }
        if (reservations.size() == 0)
            father.nothingActive();
    }

    public void setInterface(Boolean deliveringOrder) {
        updateTitles();
        int shortAnimationDuration = 600;
        if (deliveringOrder) {
            orderList.setAlpha(1f);
            card.setAlpha(0f);
            card.setVisibility(View.VISIBLE);
            card.animate().alpha(1f)
                    .setDuration(shortAnimationDuration)
                    .setListener(null);
            orderList.animate()
                    .alpha(0f)
                    .setDuration(shortAnimationDuration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            orderList.setVisibility(View.GONE);
                        }
                    });
            //orderList.setVisibility(View.GONE);
            orderDeliveredLayout.setBackgroundResource(R.drawable.order_delivered_background);
        } else {
            card.setVisibility(View.GONE);
            orderList.setVisibility(View.VISIBLE);
            orderDeliveredLayout.setBackgroundResource(R.drawable.order_delivered_background_dis);
        }
    }

    public void setActiveReservation(Reservation reservation) {
        this.activeReservation = reservation;
        if (reservation == null) {
            switchButton.setImageResource(R.drawable.swap_dis);
            switchButton.setClickable(false);
            adapter.setOrderActive(false);
            for (int i = 0; i < reservations.size(); i++) {
                adapter.notifyItemChanged(i);
            }
        } else {
            switchButton.setImageResource(R.drawable.swap_white);
            switchButton.setClickable(true);
            adapter.setOrderActive(true);
            for (int i = 0; i < reservations.size(); i++) {
                adapter.notifyItemChanged(i);
            }

            //Saving the image of the customer to which deliver the order
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("endUsers");
            Query query = ref.child(activeReservation.getUserId()).child("info");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String imagePath = dataSnapshot.child("imagePath").getValue(String.class);
                    Log.d("PROVAIMAGE", imagePath);
                    if(imagePath != null){
                        if(imagePath.length() > 0){
                            Log.d("PROVAIMAGE", "Sorpassati i controlli");
                            File directory = new File(storageDir.getPath()+File.separator+MAIN_DIR);
                            if(!directory.exists()){
                                directory.mkdirs();
                                Log.d("PROVAIMAGE", "Creata la cartella di destinazione");
                            }
                            final File customerProfileImage = new File(directory,activeReservation.getUserId()+".jpg");
                            sharedPreferences.edit().putString("customerProfileImage",customerProfileImage.getPath()).apply();
                            FirebaseStorage.getInstance().getReference().child(imagePath).getFile(customerProfileImage)
                                    .addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful())
                                                setImage();
                                            else
                                                sharedPreferences.edit().remove("customerProfileImage").apply();
                                        }
                                    });
                        }else
                            sharedPreferences.edit().remove("customerProfileImage").apply();
                    }else
                        sharedPreferences.edit().remove("customerProfileImage").apply();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    sharedPreferences.edit().remove("customerProfileImage").apply();
                }
            });

            restaurantName.setText(activeReservation.getRestaurantName());
            restaurantAddress.setText(activeReservation.getRestaurantAddress());
            userName.setText(activeReservation.getUserName());
            userAddress.setText(activeReservation.getUserAddress());
            pickupTime.setText(activeReservation.getRestaurantPickupTime());
            deliveryTime.setText(activeReservation.getUserDeliveryTime());
            if (activeReservation.getNotes() == null) {
                noteLayout.setVisibility(View.GONE);
            } else {
                noteLayout.setVisibility(View.VISIBLE);
                notes.setText(activeReservation.getNotes());
            }

            setInterface(true);

            father.thereisActive(activeReservation);
        }
    }

    private void setImage(){
        Log.d("PROVAIMAGE", "sto per fare il controllo se l'immagine è stata scaricata o no");
        File customerImage = new File(sharedPreferences.getString("customerProfileImage", storageDir.getPath()+ File.separator+MAIN_DIR+activeReservation.getUserId()+".jpg"));
        if(customerImage.exists()){
            Log.d("PROVAIMAGE", "Sto settando l'immagine");
            RequestOptions options = new RequestOptions();
            options.signature(new ObjectKey(customerImage.getName()+" "+customerImage.lastModified()));
            Glide
                    .with(deliverImage.getContext())
                    .setDefaultRequestOptions(options)
                    .load(customerImage.getPath())
                    .into(deliverImage);
        }else{
            Log.d("PROVAIMAGE", "L'immagine non è stata trovata");
            sharedPreferences.edit().remove("customerProfileImage").apply();
            Glide
                    .with(deliverImage.getContext())
                    .load(R.drawable.profile_placeholder)
                    .into(deliverImage);
        }
    }

    public void removeItem(int pos) {
        reservations.remove(pos);
        adapter.notifyItemRemoved(pos);
        adapter.notifyItemRangeChanged(pos, reservations.size());
    }

    public void removeAllItem() {
        reservations.clear();
        adapter.notifyDataSetChanged();
    }

    public double haversineDistance(double initialLat, double initialLong,
                                    double finalLat, double finalLong) {
        int R = 6371; // km (Earth radius)
        double dLat = toRadians(finalLat - initialLat);
        double dLon = toRadians(finalLong - initialLong);
        initialLat = toRadians(initialLat);
        finalLat = toRadians(finalLat);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(initialLat) * Math.cos(finalLat);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public double toRadians(double deg) {
        return deg * (Math.PI / 180);
    }

    public void calculateDistance(Reservation activeReservation) {
        distance = 0.0;
        List<Address> lista = new ArrayList<>();

        String addressUser = activeReservation.getUserAddress();
        Log.d("PROVA", "user: " + addressUser);
        try {
            lista = geocoder.getFromLocationName(addressUser, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final LatLng latLngUser = new LatLng(lista.get(0).getLatitude(), lista.get(0).getLongitude());

        String addressRest = activeReservation.getRestaurantAddress();
        Log.d("PROVA", "restaurant: " + addressRest);
        try {
            lista = geocoder.getFromLocationName(addressRest, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final LatLng latLngRestaurant = new LatLng(lista.get(0).getLatitude(), lista.get(0).getLongitude());

        DatabaseReference databaseLocation = FirebaseDatabase.getInstance().getReference()
                .child("Bikers").child(firebaseUser.getUid()).child("location");
        databaseLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().compareTo("latitude") == 0) {
                        currLatitude = ds.getValue(Double.class);
                        Log.d("PROVA", "currLatitude: " + currLatitude);
                    }
                    if (ds.getKey().compareTo("longitude") == 0) {
                        currLongitude = ds.getValue(Double.class);
                        Log.d("PROVA", "currLongitude: " + currLongitude);
                    }
                }

                distance += haversineDistance(currLatitude, currLongitude,
                        latLngRestaurant.latitude, latLngRestaurant.longitude);
                Log.d("PROVA", "biker-rest: " + distance);

                final DatabaseReference databaseDistance = FirebaseDatabase.getInstance().getReference().
                        child("archive").child("Bikers").child(firebaseUser.getUid());
                databaseDistance.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Double dbDistance = 0.0;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.getKey().compareTo("totalDistance") == 0) {
                                dbDistance = ds.getValue(Double.class);
                            }
                        }
                        totalDistance = dbDistance;
                        distance += haversineDistance(latLngRestaurant.latitude, latLngRestaurant.longitude,
                                latLngUser.latitude, latLngUser.longitude);
                        Log.d("PROVA", "rest-user: " + distance);

                        totalDistance += distance;
                        HashMap<String, Object> childDistance = new HashMap<>();
                        childDistance.put("totalDistance", totalDistance);
                        databaseDistance.updateChildren(childDistance).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(father, R.string.error_order, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
