package com.example.foodyrestaurant;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_bar);

        init();

    }

    private void init(){

        bottomBar = findViewById(R.id.bottom_navigation);

        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.menu){
                    fm.beginTransaction().hide(active).show(menu).commit();
                    active = menu;
                    return true;
                }else if(id == R.id.orders){
                    fm.beginTransaction().hide(active).show(orders).commit();
                    active = orders;
                    return true;
                }else if(id == R.id.prof){
                    fm.beginTransaction().hide(active).show(user).commit();
                    active = user;
                    return true;
                }
                return false;
            }
        });

        fm.beginTransaction().add(R.id.mainFrame, user, "3").hide(user).commit();
        fm.beginTransaction().add(R.id.mainFrame, orders, "2").hide(orders).commit();
        fm.beginTransaction().add(R.id.mainFrame, menu, "1").commit();

        /*
        loadFragments();



        if( this.getSupportFragmentManager().findFragmentById(R.id.frameMenu) == null){
            bottomBar.setSelectedItemId(R.id.menu);
            stateApp = TabState.MENU;
        }

        else
            updateFragment(this.getSupportFragmentManager().findFragmentById(R.id.frameMenu));


        if(stateApp == null) {
            bottomBar.setSelectedItemId(R.id.menu);
        }
*/

    }


    private void loadFragments(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.mainFrame, menu);
        transaction.add(R.id.mainFrame, orders);
        transaction.add(R.id.mainFrame,user);
        transaction.hide(menu);
        transaction.hide(orders);
        transaction.hide(user);
        transaction.commit();


    }


    private void setTabState(TabState state){

        //getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.hide(menu);
        transaction.hide(orders);
        transaction.hide(user);


        switch (state){
            case MENU:
                transaction.show(menu);
            break;
            case ORDERS:
                transaction.show(orders);
            break;
            case USER:
                transaction.show(user);
            break;
        }


        transaction.commit();

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
