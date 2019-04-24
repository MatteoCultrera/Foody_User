package com.example.foodyuser;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private enum TabState {
        MENU,
        ORDERS,
        USER,
    }

    private Fragment discover = new DiscoverFragment();
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
        user = new UserFragment();
        fm.beginTransaction().add(R.id.mainFrame, user, "3").show(user).commit();
        fm.beginTransaction().add(R.id.mainFrame, reservations, "2").commit();
        fm.beginTransaction().add(R.id.mainFrame, discover, "1").commit();
        active = user;
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
        BottomNavigationView bottomBar = findViewById(R.id.bottom_navigation);
        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.discover && active != discover){
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right);
                    transaction.replace(R.id.mainFrame, discover).commit();
                    active = discover;
                    return true;
                }else if(id == R.id.orders && active != reservations){
                    if(active == discover){
                        FragmentTransaction transaction =fm.beginTransaction();
                        transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left);
                        transaction.replace(R.id.mainFrame, reservations).commit();
                    }else if(active == user) {
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                        transaction.replace(R.id.mainFrame, reservations).commit();
                    }
                    active = reservations;
                    return true;
                }else if(id == R.id.prof && active != user){
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left);
                    transaction.replace(R.id.mainFrame, user).commit();
                    active = user;
                    return true;
                }
                return false;
            }
        });
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
}
