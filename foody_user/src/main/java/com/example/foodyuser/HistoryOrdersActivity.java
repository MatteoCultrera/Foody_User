package com.example.foodyuser;

import android.content.SharedPreferences;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class HistoryOrdersActivity extends AppCompatActivity {


    private TextView time;
    private SpinKitView loading;
    private ImageButton back;
    private RecyclerView recyclerView;
    private boolean isPaused;
    private boolean isReady;
    private String date, month, year;
    private File storage;
    private SharedPreferences shared;
    private final String HISTORY_DIRECTORY= "historyDirectory";
    private ArrayList<Reservation> reservations;
    int imagesToFetch, imagesFetched;
    private boolean addAfter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_orders);

        time = findViewById(R.id.time_history);
        loading = findViewById(R.id.loading_history);
        back = findViewById(R.id.back_history);
        recyclerView = findViewById(R.id.recycler_history);
        Bundle extras = getIntent().getExtras();
        if(extras.containsKey("date")){
        date = extras.getString("date","");
        String[] parts = date.split("-");
        month = parts[0];
        year = parts[1];
        }else {
            finish();
        }
        time.setText(String.format("%s %s", getMonthLong(month), year));
        shared = getSharedPreferences("myPreference", MODE_PRIVATE);
        isReady = false;

        addAfter = false;
    }


    private void init(){

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        File root = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        storage = new File(root.getPath()+File.separator+HISTORY_DIRECTORY);

        if(!storage.exists()){
            storage.mkdir();
        }


       fetchReservationsFromDB();

        if(!isReady){
            loading.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else if(addAfter){
            loading.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            RVAdapterHistory history = new RVAdapterHistory(reservations);
            recyclerView.setAdapter(history);
            addAfter = false;
        }


    }

    private void fetchReservationsFromDB(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        Query query = database.child("archive").child("user").child(shared.getString("id","")).child(date);
        reservations = new ArrayList<>();
        final Map<String, String> restaurantsWithImage = new HashMap<>();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    if(!ds.child("date").exists()){
                        Log.d("SUPERAUTO","No Date, No Fetch");
                        continue;
                    }

                    ReservationDBUser reservationDBUser = ds.getValue(ReservationDBUser.class);
                    ArrayList<Dish> dishes = new ArrayList<>();
                    for(OrderItem o : reservationDBUser.getDishesOrdered()){
                        Dish dish = new Dish();
                        dish.setQuantity(o.getPieces());
                        dish.setDishName(o.getOrderName());
                        dish.setPrice(o.getPrice());
                        dishes.add(dish);
                    } Reservation.prepStatus status;
                    String orderID = reservationDBUser.getReservationID().substring(28);
                    if (reservationDBUser.getStatus().toLowerCase().equals("pending")){
                        status = Reservation.prepStatus.PENDING;
                    } else if (reservationDBUser.getStatus().toLowerCase().equals("doing")){
                        status = Reservation.prepStatus.DOING;
                    } else if(reservationDBUser.getStatus().toLowerCase().equals("rejected")){
                        status = Reservation.prepStatus.REJECTED;
                    }else{
                        status = Reservation.prepStatus.DONE;
                    }
                    Reservation reservation = new Reservation(orderID, dishes, status,
                            reservationDBUser.isAccepted(), reservationDBUser.getOrderTime(), shared.getString("name", ""),
                            shared.getString("phoneNumber", ""), reservationDBUser.getResNote(), "",
                            shared.getString("email", ""), shared.getString("address", ""));
                    reservation.setRestaurantID(reservationDBUser.getRestaurantID());
                    reservation.setDeliveryTime(reservationDBUser.getOrderTime());
                    reservation.setRestaurantName(reservationDBUser.getRestaurantName());
                    reservation.setRestaurantAddress(reservationDBUser.getRestaurantAddress());
                    reservation.setDate(ds.child("date").getValue(String.class));
                    reservation.setISOdate(getISODate(ds.child("date").getValue(String.class),reservationDBUser.getOrderTime()));
                    reservation.setTotalCost(reservationDBUser.getTotalCost());
                    reservation.setImagePathLocale(storage.getPath()+File.separator+reservationDBUser.getRestaurantID()+".jpg");
                    reservations.add(reservation);

                    if(!restaurantsWithImage.containsKey(reservationDBUser.getRestaurantID()))
                        restaurantsWithImage.put(reservationDBUser.getRestaurantID(),"");

                }

                reservations.sort( new Comparator<Reservation>() {
                    public int compare(Reservation r1, Reservation r2){
                        return  r1.getISOdate().compareTo(r2.getISOdate());
                    }
                });

                if(isReady && !isPaused){
                    //ALL SET UP
                    RVAdapterHistory history = new RVAdapterHistory(reservations);
                    recyclerView.setAdapter(history);
                    loading.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }else if(isPaused){
                    addAfter = true;
                    getRestaurantsDB(restaurantsWithImage);
                }else{
                    //Fetch Images From DB
                    getRestaurantsDB(restaurantsWithImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                finish();
            }
        });

    }

    private String getISODate(String date, String time){
        String[] nums = date.split("-");
        int day = Integer.valueOf(nums[2]);
        int month = Integer.valueOf(nums[1]);
        int year = Integer.valueOf(nums[0]);
        String times[] = time.split(":");
        int hour = Integer.valueOf(times[0]);
        int minute = Integer.valueOf(times[1]);
        return String.format("%d-%02d-%02dT%02d:%02d:00.000",year,month,day,hour,minute);
    }

    private void getRestaurantsDB(final Map<String, String> restaurants){
        imagesToFetch = 0;
        imagesFetched = 0;
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        Query query = database.child("restaurantsInfo");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, String> restaurantsWithImages = new HashMap<>();
                for(String s: restaurants.keySet()){
                    if(dataSnapshot.child(s).child("info").child("imagePath").getValue(String.class)!=null){
                        if(dataSnapshot.child(s).child("info").child("imagePath").getValue(String.class).length() > 0){
                            imagesToFetch++;
                            restaurantsWithImages.put(s, dataSnapshot.child(s).child("info").child("imagePath").getValue(String.class));
                        }
                    }
                }

                saveImagesRestaurants(restaurantsWithImages, imagesToFetch);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                finish();
            }
        });
    }

    private void saveImagesRestaurants(final Map<String, String> restaurants, final int imagesToFetch){
        for(String s: restaurants.keySet()){
            final File image = new File(storage.getPath()+File.separator+s+".jpg");
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
            mStorageRef.child(restaurants.get(s)).getFile(image)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            imagesFetched++;
                            if(imagesFetched == imagesToFetch && !isPaused){
                                RVAdapterHistory history = new RVAdapterHistory(reservations);
                                recyclerView.setAdapter(history);
                                loading.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                isReady=true;
                            }else{
                                addAfter = true;
                                isReady = true;
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    imagesFetched++;
                    if(imagesFetched == imagesToFetch && !isPaused){
                        //ALL Images Added
                        RVAdapterHistory history = new RVAdapterHistory(reservations);
                        recyclerView.setAdapter(history);
                        loading.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        isReady=true;
                    }else{
                        addAfter = true;
                        isReady = true;
                    }
                }
            });



        }
    }

    private String getMonthLong(String value){
        String toRet = null;
        switch (value){
            case "0":
                toRet = getString(R.string.january);
                break;
            case "1":
                toRet = getString(R.string.february);
                break;
            case "2":
                toRet = getString(R.string.march);
                break;
            case "3":
                toRet = getString(R.string.april);
                break;
            case "4":
                toRet = getString(R.string.mayL);
                break;
            case "5":
                toRet = getString(R.string.june);
                break;
            case "6":
                toRet = getString(R.string.july);
                break;
            case "7":
                toRet = getString(R.string.august);
                break;
            case "8":
                toRet = getString(R.string.september);
                break;
            case "9":
                toRet = getString(R.string.october);
                break;
            case "10":
                toRet = getString(R.string.november);
                break;
            case "11":
                toRet = getString(R.string.december);
                break;

        }
        return toRet;
    }


    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
    }
}
