package com.example.foodyrestaurant;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private View notificationBadgeKitchen, notificationBadgeDeliv;
    BottomNavigationView bottomBar;
    private Fragment menu;
    private Fragment reservations;
    private Fragment user;
    private Fragment biker;
    private Fragment history;
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment active;
    private SharedPreferences sharedPref;
    private Boolean bool = true;
    private Uri photoProfile;
    private HashMap<String, Uri> photosMap = new HashMap<>();
    private HashMap<String, Integer> dishes = new HashMap<>();
    private List<Map.Entry<String, Integer>> top3;
    private HashMap<Integer, Integer> frequency = new HashMap<>();
    private Float amount;
    private Integer accepted, rejected;

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
        sharedPref = getSharedPreferences("myPreference", MODE_PRIVATE);

        setContentView(R.layout.bottom_bar);
        if (savedInstanceState != null) {
            String lastFragment = savedInstanceState.getString("lastFragment", null);
            if (lastFragment != null) {
                if (lastFragment.compareTo("menu") == 0) {
                    active = menu;
                } else if (lastFragment.compareTo("reservations") == 0) {
                    active = reservations;
                } else if (lastFragment.compareTo("user") == 0) {
                    active = user;
                }
            }
            init();
            return;
        }
        menu = new MenuFragment();
        ((MenuFragment) menu).setFather(this);
        reservations = new ReservationFragment();
        ((ReservationFragment) reservations).setFather(this);
        user = new UserFragment();
        ((UserFragment) user).setFather(this);
        biker = new BikerFragment();
        ((BikerFragment) biker).setFather(this);
        history = new HistoryFragment();
        ((HistoryFragment) history).setFather(this);
        fm.beginTransaction().add(R.id.mainFrame, history, "5").hide(history).commit();
        fm.beginTransaction().add(R.id.mainFrame, user, "4").hide(user).commit();
        fm.beginTransaction().add(R.id.mainFrame, biker, "3").hide(biker).commit();
        fm.beginTransaction().add(R.id.mainFrame, reservations, "2").hide(reservations).commit();
        fm.beginTransaction().add(R.id.mainFrame, menu, "1").show(menu).commit();
        active = menu;
        init();
    }

    public Uri getPhotoProfile(){
        return photoProfile;
    }

    public void setPhotoProfile(Uri photoProfile){
        this.photoProfile = photoProfile;
    }

    public Uri getPhotoDish(String name){
        if (photosMap.containsKey(name))
            return photosMap.get(name);
        return null;
    }

    public void clearHashMap(){
        photosMap = new HashMap<>();
    }

    public void setPhotoDish(String name, Uri photo){
        photosMap.put(name, photo);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        if (active == menu){
            outState.putString("lastFragment", "menu");
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
                if(active == reservations && menuItem.getItemId() != R.id.orders
                && ((ReservationFragment) reservations).hasNotification()){
                    ((ReservationFragment) reservations).clearNotification();
                    notificationBadgeKitchen.setVisibility(View.VISIBLE);
                }
                int id = menuItem.getItemId();
                if(id == R.id.menu && active != menu){
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right);
                    transaction.hide(active).show(menu).commit();
                    active = menu;
                    return true;
                }else if(id == R.id.orders && active != reservations){
                    clearNotification(true);
                    if(active == menu){
                        FragmentTransaction transaction =fm.beginTransaction();
                        transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left);
                        transaction.hide(active).show(reservations).commit();
                    }else{
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                        transaction.hide(active).show(reservations).commit();
                    }
                    active = reservations;
                    return true;
                }else if(id == R.id.delivery_biker && active != biker){
                    clearNotification(false);
                    if(active == menu || active == reservations){
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                        transaction.hide(active).show(biker).commit();
                    }else{
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                        transaction.hide(active).show(biker).commit();
                    }
                    active = biker;
                    return true;
                }else if(id == R.id.prof && active != user){

                    if(active == history){
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right);
                        transaction.hide(active).show(user).commit();
                    }else{
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left);
                        transaction.hide(active).show(user).commit();
                    }

                    active = user;
                    return true;
                }else if(id == R.id.orders_done && active != history){
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                    transaction.hide(active).show(history).commit();
                    active = history;
                    return true;
                }
                return false;
            }
        });

        if(notificationBadgeKitchen == null){
            addBadgeView();
        }

        notification();
    }

    public void setNotification(boolean isKitchen){
        if(isKitchen){
            if(active!=reservations)
                notificationBadgeKitchen.setVisibility(View.VISIBLE);
            else
                ((ReservationFragment) reservations).addNotification();
        }else{
            if(active!=biker)
                notificationBadgeDeliv.setVisibility(View.VISIBLE);
        }
    }

    public void clearNotification(boolean isKitchen){
        if(isKitchen){
            if(notificationBadgeKitchen.getVisibility() == View.VISIBLE) {
                notificationBadgeKitchen.setVisibility(View.GONE);
            }
        }else{
            if(notificationBadgeDeliv.getVisibility() == View.VISIBLE) {
                notificationBadgeDeliv.setVisibility(View.GONE);
            }
        }
    }

    private void addBadgeView() {

        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomBar.getChildAt(0);
        BottomNavigationItemView itemViewKitchen = (BottomNavigationItemView) menuView.getChildAt(1);
        BottomNavigationItemView itemViewDeliv = (BottomNavigationItemView) menuView.getChildAt(2);

        notificationBadgeKitchen = LayoutInflater.from(this).inflate(R.layout.view_notification_badge, menuView, false);
        notificationBadgeDeliv = LayoutInflater.from(this).inflate(R.layout.view_notification_badge, menuView, false);

        itemViewKitchen.addView(notificationBadgeKitchen);
        itemViewDeliv.addView(notificationBadgeDeliv);

        notificationBadgeKitchen.setVisibility(View.GONE);
        notificationBadgeDeliv.setVisibility(View.GONE);
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
    }

    public void notification(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference restaurantReservations = FirebaseDatabase.getInstance().getReference().child("reservations")
                .child("restaurant").child(firebaseUser.getUid());
        restaurantReservations.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(!dataSnapshot.child("accepted").getValue(boolean.class)) {
                    String status = dataSnapshot.child("status").getValue(String.class);
                    if (status.equals("Pending")) {
                        ((ReservationFragment) reservations).addPendingOrder(dataSnapshot);
                        setNotification(true);
                    }
                } else if (dataSnapshot.child("accepted").getValue(boolean.class)){
                    String status = dataSnapshot.child("status").getValue(String.class);
                    if(status.equals("Doing")){
                        ((ReservationFragment) reservations).addDoingOrder(dataSnapshot);
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

        DatabaseReference restaurantBikers = FirebaseDatabase.getInstance().getReference().child("reservations")
                .child("restaurant").child(firebaseUser.getUid());
        restaurantBikers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //new order without biker assigned
                if(dataSnapshot.child("accepted").getValue(boolean.class)) {
                    if(!dataSnapshot.child("biker").getValue(boolean.class)) {
                        setNotification(false);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //new order without biker assigned
                if(dataSnapshot.child("accepted").getValue(boolean.class)) {
                    if(!dataSnapshot.child("biker").getValue(boolean.class)) {
                        setNotification(false);
                    }
                }
                //biker has accepted my order
                if(dataSnapshot.child("biker").getValue(boolean.class)) {
                    String bikerID = dataSnapshot.child("bikerID").getValue(String.class);
                    if(!bikerID.equals("")) {
                        setNotification(false);
                    }
                }
                //biker has rejected my order

                if(!dataSnapshot.child("biker").getValue(boolean.class)) {
                    if(dataSnapshot.child("waitingBiker").exists()) {
                        if(dataSnapshot.child("waitingBiker").getValue(boolean.class)) {
                            setNotification(false);
                        }
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

    public List<Map.Entry<String, Integer>> getTop3() {
        return top3;
    }

    public HashMap<Integer, Integer> getFrequency() {
        return frequency;
    }

    public Float getAmount() {
        return amount;
    }

    public Integer getAccepted() {
        return accepted;
    }

    public Integer getRejected() {
        return rejected;
    }

    public void setTop3(List<Map.Entry<String, Integer>> top3) {
        this.top3 = top3;
    }

    public void setFrequency(HashMap<Integer, Integer> frequency) {
        this.frequency = frequency;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public void setAccepted(Integer accepted) {
        this.accepted = accepted;
    }

    public void setRejected(Integer rejected) {
        this.rejected = rejected;
    }
}
