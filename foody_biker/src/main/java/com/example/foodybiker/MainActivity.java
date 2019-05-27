package com.example.foodybiker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private View notificationBadgeOne, notificationBadgeTwo, notificationBadgeThree;
    BottomNavigationView bottomBar;

    private Fragment map;
    private Fragment reservations;
    private Fragment user;
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment active;
    private SharedPreferences sharedPref;
    private Boolean bool = true;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        } else {
            //here there is an internet connection
            setContentView(R.layout.bottom_bar);
            sharedPref = getSharedPreferences("myPreference", MODE_PRIVATE);
            if (savedInstanceState != null) {
                String lastFragment = savedInstanceState.getString("lastFragment", null);
                if (lastFragment != null) {
                    if (lastFragment.compareTo("map") == 0) {
                        active = map;
                    } else if (lastFragment.compareTo("reservations") == 0) {
                        active = reservations;
                    } else if (lastFragment.compareTo("user") == 0) {
                        active = user;
                    }
                }
                init();
                return;
            }
            map = new MapFragment();
            ((MapFragment) map).setFather(this);
            reservations = new ReservationFragment();
            ((ReservationFragment) reservations).setFather(this);
            user = new UserFragment();
            fm.beginTransaction().add(R.id.mainFrame, user, "3").hide(user).commit();
            fm.beginTransaction().add(R.id.mainFrame, reservations, "2").hide(reservations).commit();
            fm.beginTransaction().add(R.id.mainFrame, map, "1").show(map).commit();
            init();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        if (active == map){
            outState.putString("lastFragment", "map");
        } else if (active == reservations){
            outState.putString("lastFragment", "reservations");
        } else if (active == user){
            outState.putString("lastFragment", "user");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(map.isVisible())
                    ((MapFragment)map).permissionsGrantedVis();
                else
                    ((MapFragment)map).permissionsGranted();
            }
        }
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

    private void init(){
        bottomBar = findViewById(R.id.bottom_navigation);
        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.map && active != map){
                    clearNotification(0);
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right);
                    transaction.hide(active).show(map).commit();
                    active = map;
                    return true;
                }else if(id == R.id.orders && active != reservations){
                    clearNotification(1);
                    if(active == map){
                        FragmentTransaction transaction =fm.beginTransaction();
                        transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left);
                        transaction.hide(active).show(reservations).commit();
                    }else if(active == user) {
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                        transaction.hide(active).show(reservations).commit();
                    }
                    active = reservations;
                    return true;
                }else if(id == R.id.prof && active != user){
                    clearNotification(2);
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left);
                    transaction.hide(active).show(user).commit();
                    active = user;
                    return true;
                }
                return false;
            }
        });

        active = map;
        if(notificationBadgeOne == null)
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

    public void thereisActive(Reservation res) {
        ((MapFragment) map).thereIsAnActiveRes(res);
    }

    public void noActiveReservation(Reservation res) {
        ((MapFragment) map).noActive(res);
    }

    public void newReservation(Reservation res) {
        ((MapFragment) map).newReservationToDisplay(res);
    }

    public void nothingActive() {
        Log.d("PROVA", "nothingActive()");
        ((MapFragment) map).reservations.clear();
        ((MapFragment) map).clearMap();
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

    public void notification() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference bikerReservations = FirebaseDatabase.getInstance().getReference().child("reservations")
                .child("Bikers").child(firebaseUser.getUid());
        bikerReservations.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(!dataSnapshot.child("status").exists()) {
                    setNotification(1);
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
}
