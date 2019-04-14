package com.example.foodyrestaurant;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private enum  TabState {
        MENU,
        ORDERS,
        USER,
    }

    private final Fragment menu = new MenuFragment();
    private final Fragment reservations = new ReservationFragment();
    private final Fragment user = new UserFragment();
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment active = menu;
    TabState stateApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_bar);

        init();

    }

    private void init(){

        BottomNavigationView bottomBar = findViewById(R.id.bottom_navigation);

        /*menu = new MenuFragment();
        user = new UserFragment();
        reservation = new ReservationFragment();
*/
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
                }else if(id == R.id.orders && active != reservations){
                    if(active == menu){
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

        fm.beginTransaction().add(R.id.mainFrame, user, "3").hide(user).commit();
        fm.beginTransaction().add(R.id.mainFrame, reservations, "2").hide(reservations).commit();
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
