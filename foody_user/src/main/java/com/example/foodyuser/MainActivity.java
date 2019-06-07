package com.example.foodyuser;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.foody_library.NetworkCheck;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {


    private View notificationBadgeOne, notificationBadgeTwo, notificationBadgeThree;
    private BottomNavigationView bottomBar;
    private Fragment discover;
    private Fragment reservations;
    private Fragment user;
    private Fragment history;
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment active;
    private SharedPreferences sharedPref;
    private Boolean bool = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        /*
        try {
            bool = new NetworkCheck().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException i) {
            i.printStackTrace();
        }

        if(!bool) {
            //No Internet
            Intent i = new Intent(getApplicationContext(), NoInternetActivity.class);
            startActivity(i);
            finish();
        }
        */

        //here there is an internet connection
        setContentView(R.layout.bottom_bar);
        sharedPref = getSharedPreferences("myPreference", MODE_PRIVATE);
        if (savedInstanceState != null) {
            String lastFragment = savedInstanceState.getString("lastFragment", null);
            if (lastFragment != null) {
                if (lastFragment.compareTo("discover") == 0) {
                    active = discover;
                } else if (lastFragment.compareTo("reservations") == 0) {
                    active = reservations;
                } else if (lastFragment.compareTo("user") == 0) {
                    active = user;
                }
            }
            init();
            return;
        }
        discover = new DiscoverFragment();
        reservations = new ReservationFragment();
        ((ReservationFragment) reservations).setFather(this);
        user = new UserFragment();
        history = new HistoryFragment();
        fm.beginTransaction().add(R.id.mainFrame, history, "4").hide(history).commit();
        fm.beginTransaction().add(R.id.mainFrame, user, "3").hide(user).commit();
        fm.beginTransaction().add(R.id.mainFrame, reservations, "2").hide(reservations).commit();
        fm.beginTransaction().add(R.id.mainFrame, discover, "1").show(discover).commit();
        active = discover;
        init();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        if (active == discover){
            outState.putString("lastFragment", "discover");
        } else if (active == reservations){
            outState.putString("lastFragment", "reservations");
        } else if (active == user){
            outState.putString("lastFragment", "user");
        }
    }

    private void init(){
        bottomBar = findViewById(R.id.bottom_navigation);
        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.discover && active != discover){
                    clearNotification(0);
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right);
                    transaction.hide(active).show(discover).commit();
                    active = discover;
                    return true;
                }else if(id == R.id.orders && active != reservations){
                    clearNotification(1);
                    if(active == discover){
                        FragmentTransaction transaction =fm.beginTransaction();
                        transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left);
                        transaction.hide(active).show(reservations).commit();
                    }else {
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                        transaction.hide(active).show(reservations).commit();
                    }
                    active = reservations;
                    return true;
                }else if(id == R.id.prof && active != user){
                    clearNotification(2);
                    if(active == reservations || active == discover){
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left);
                        transaction.hide(active).show(user).commit();
                    } else {
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right);
                        transaction.hide(active).show(user).commit();
                    }
                    active = user;
                    return true;
                }else if(id == R.id.history && active != history){
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left);
                    transaction.hide(active).show(history).commit();
                    active = history;
                    return true;
                }
                return false;
            }
        });
        addBadgeView();
        if (sharedPref.getBoolean("hasNotification",false)){
            setNotification(1);
        }

        notification();
    }

    public void setNotification(int pos){
        Menu menu = bottomBar.getMenu();
        if(menu.getItem(pos).isChecked())
            return;

        switch (pos){
            case 0:
                notificationBadgeOne.setVisibility(View.VISIBLE);
                break;
            case 1:
                notificationBadgeTwo.setVisibility(View.VISIBLE);
                break;
            case 2:
                notificationBadgeThree.setVisibility(View.VISIBLE);
                break;
        }

    }

    public void clearNotification(int pos){
        switch (pos){
            case 0:
                notificationBadgeOne.setVisibility(View.GONE);
                break;
            case 1:
                notificationBadgeTwo.setVisibility(View.GONE);
                break;
            case 2:
                notificationBadgeThree.setVisibility(View.GONE);
                break;
            default:
                notificationBadgeOne.setVisibility(View.GONE);
                notificationBadgeTwo.setVisibility(View.GONE);
                notificationBadgeThree.setVisibility(View.GONE);
                break;
        }
        if(notificationBadgeTwo.getVisibility() == View.GONE){
            sharedPref.edit().putBoolean("hasNotification", false).apply();
        }
    }

    private void addBadgeView() {

        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomBar.getChildAt(0);
        BottomNavigationItemView itemViewOne = (BottomNavigationItemView) menuView.getChildAt(0);
        BottomNavigationItemView itemViewTwo = (BottomNavigationItemView) menuView.getChildAt(1);
        BottomNavigationItemView itemViewThree = (BottomNavigationItemView) menuView.getChildAt(2);

        notificationBadgeOne = LayoutInflater.from(this).inflate(R.layout.view_notification_badge, menuView, false);
        notificationBadgeTwo = LayoutInflater.from(this).inflate(R.layout.view_notification_badge, menuView, false);
        notificationBadgeThree = LayoutInflater.from(this).inflate(R.layout.view_notification_badge, menuView, false);

        itemViewOne.addView(notificationBadgeOne);
        itemViewTwo.addView(notificationBadgeTwo);
        itemViewThree.addView(notificationBadgeThree);

        notificationBadgeOne.setVisibility(View.GONE);
        notificationBadgeTwo.setVisibility(View.GONE);
        notificationBadgeThree.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            fm.beginTransaction().detach(active).attach(active).commit();
        } catch (IllegalStateException e){
            e.getMessage();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(notificationBadgeTwo.getVisibility() == View.VISIBLE){
            sharedPref.edit().putBoolean("hasNotification", true).apply();
        }else{
            sharedPref.edit().putBoolean("hasNotification", false).apply();
        }
    }

    public void notification(){
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference restaurantBikers = FirebaseDatabase.getInstance().getReference().child("reservations")
                .child("users").child(firebaseUser.getUid());
        restaurantBikers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final DatabaseReference databaseArchive= FirebaseDatabase.getInstance().getReference()
                        .child("archive").child("user").child(firebaseUser.getUid());

                //order has been accepted
                if(dataSnapshot.child("accepted").getValue(boolean.class) &&
                        dataSnapshot.child("status").getValue(String.class).compareTo("Doing") == 0) {
                    setNotification(1);

                    final DatabaseReference databaseDelivered = databaseArchive.child("delivered");
                    databaseDelivered.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                int count = dataSnapshot.getValue(int.class);
                                count++;
                                databaseDelivered.setValue(count);
                            } else {
                                databaseDelivered.setValue(1);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                //order has been rejected
                if(!dataSnapshot.child("accepted").getValue(boolean.class) &&
                        dataSnapshot.child("status").getValue(String.class).compareTo("Done") == 0) {
                    setNotification(1);

                    final DatabaseReference databaseRejected = databaseArchive.child("rejected");
                    databaseRejected.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                int count = dataSnapshot.getValue(int.class);
                                count++;
                                databaseRejected.setValue(count);
                            } else {
                                databaseRejected.setValue(1);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                if(dataSnapshot.child("delivered").exists()){
                    if(dataSnapshot.child("delivered").getValue(boolean.class)){
                        final ReservationDBUser reservationDBUser = dataSnapshot.getValue(ReservationDBUser.class);
                        DatabaseReference databaseRest = FirebaseDatabase.getInstance().getReference()
                                .child("reservations").child("users").child(firebaseUser.getUid())
                                .child(reservationDBUser.getReservationID());
                        databaseRest.removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                Calendar calendar = Calendar.getInstance();
                                String monthYear = calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.YEAR);
                                String date = calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" +
                                        calendar.get(Calendar.DAY_OF_YEAR);
                                DatabaseReference databaseMonth = databaseArchive.child(monthYear);
                                HashMap<String, Object> childSelf = new HashMap<>();
                                reservationDBUser.setDate(date);
                                childSelf.put(reservationDBUser.getReservationID(), reservationDBUser);
                                databaseMonth.updateChildren(childSelf);
                                ((ReservationFragment) reservations).removeOrder(reservationDBUser.getReservationID());
                            }
                        });

                        final DatabaseReference databaseAmount = databaseArchive.child("amount");
                        databaseAmount.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    Float amount = dataSnapshot.getValue(Float.class);
                                    String[] price = reservationDBUser.getTotalCost().split("\\s+");
                                    Float toAdd = Float.parseFloat(price[0]);
                                    amount += toAdd;
                                    databaseAmount.setValue(amount);
                                } else {
                                    String[] price = reservationDBUser.getTotalCost().split("\\s+");
                                    Float amount = Float.parseFloat(price[0]);
                                    databaseAmount.setValue(amount);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
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
}
