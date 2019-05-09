package com.example.foodybiker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private View notificationBadgeOne, notificationBadgeTwo, notificationBadgeThree;
    BottomNavigationView bottomBar;


    private enum TabState{
        MAP,
        ORDERS,
        USER,
    }

    private Fragment map;
    private Fragment reservations;
    private Fragment user;
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
        ((ReservationFragment) reservations).setFather(this);
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
        addBadgeView();
        setNotification(0);
        setNotification(1);
        setNotification(2);
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

        clearNotification(4);
    }
}
