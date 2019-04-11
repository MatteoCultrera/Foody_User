package com.example.foodyrestaurant;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private enum  TabState {
        MENU,
        ORDERS,
        USER,
    };

    BottomNavigationView bottomBar;
    final Fragment menu = new MenuFragment();
    final Fragment orders = new OrdersFragment();
    final Fragment user = new UserFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = menu;
    TabState stateApp;
    //TODO _ to be checked
    MenuFragment menu;
    UserFragment user;
    ReservationFragment reservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_bar);

        init();

    }

    private void init(){

        bottomBar = findViewById(R.id.bottom_navigation);

        menu = new MenuFragment();
        user = new UserFragment();
        reservation = new ReservationFragment();

        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.menu && active != menu){
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right);
                    transaction.hide(active).show(menu).commit();
                    active = menu;
                    return true;
                }else if(id == R.id.orders && active != orders){
                    if(active == menu){
                        FragmentTransaction transaction =fm.beginTransaction();
                        transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left);
                        transaction.hide(active).show(orders).commit();
                    }else if(active == user) {
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                        transaction.hide(active).show(orders).commit();
                    }
                    active = orders;
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

        fm.beginTransaction().add(R.id.mainFrame, user, "3").hide(user).commit();
        fm.beginTransaction().add(R.id.mainFrame, orders, "2").hide(orders).commit();
        fm.beginTransaction().add(R.id.mainFrame, menu, "1").commit();



    }

    private void updateFragment(Fragment f){
        getSupportFragmentManager().beginTransaction().detach(f).attach(f).commit();
    }


    @Override
    protected void onResume() {
        super.onResume();

        updateFragment(active);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        updateFragment(active);
    }


}
