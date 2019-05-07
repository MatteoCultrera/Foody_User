package com.example.foodybiker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private enum TabState{
        MAP,
        ORDERS,
        USER,
    }

    private Fragment map = new MapFragment();
    private Fragment reservations = new ReservationFragment();
    private Fragment user = new UserFragment();
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment active;
    TabState stateApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_bar);
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
        reservations = new ReservationFragment();
        user = new UserFragment();
        fm.beginTransaction().add(R.id.mainFrame, user, "3").hide(user).commit();
        fm.beginTransaction().add(R.id.mainFrame, reservations, "2").hide(reservations).commit();
        fm.beginTransaction().add(R.id.mainFrame, map, "1").show(map).commit();
        init();
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
    protected void onRestart() {
        super.onRestart();
        try {
            fm.beginTransaction().detach(active).attach(active).commit();
        } catch (IllegalStateException e){
            e.getMessage();
        }
    }

    private void init(){
        BottomNavigationView bottomBar = findViewById(R.id.bottom_navigation);
        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.map && active != map){
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right);
                    transaction.hide(active).show(map).commit();
                    active = map;
                    return true;
                }else if(id == R.id.orders && active != reservations){
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
    }
}
