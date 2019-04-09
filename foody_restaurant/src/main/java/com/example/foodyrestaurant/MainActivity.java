package com.example.foodyrestaurant;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomBar;
    MenuFragment menu;
    UserFragment user;
    ReservationFragment reservation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_bar);

        Log.d("CAPISCI","onCreate");

        init(false);

    }

    private void init(boolean startOver){

        bottomBar = findViewById(R.id.bottom_navigation);

        menu = new MenuFragment();
        user = new UserFragment();
        reservation = new ReservationFragment();

        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.menu){
                    setFragment(menu);
                    return true;
                }else if(id == R.id.orders){
                    setFragment(reservation);
                    return true;
                }else if(id == R.id.prof){
                    setFragment(user);
                    return true;
                }
                return false;
            }
        });

        if( this.getSupportFragmentManager().findFragmentById(R.id.frameMenu) == null)
            bottomBar.setSelectedItemId(R.id.menu);
        else
            updateFragment(this.getSupportFragmentManager().findFragmentById(R.id.frameMenu));

    }


    private void setFragment(Fragment f){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameMenu, f);
        transaction.commit();

    }

    private void updateFragment(Fragment f){
        getSupportFragmentManager().beginTransaction().detach(f).attach(f).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("CAPISCI","onResume");

        init(false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d("CAPISCI","onREstart");

        init(false);
    }
}
