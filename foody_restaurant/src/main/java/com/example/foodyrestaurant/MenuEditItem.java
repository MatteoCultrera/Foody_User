package com.example.foodyrestaurant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class MenuEditItem extends AppCompatActivity {

    private TextView title;

    private RecyclerView recyclerMenu;
    private RVAdapterEditItem recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_edit_item);

        title = findViewById(R.id.textView);

        title.setText("Edit "+getIntent().getExtras().getString("MainName"));

        /*
        recyclerAdapter = new RVAdapterEditItem(cards);
        recyclerMenu.setAdapter(recyclerAdapter);*/

    }

    public void backToEditMenu(View view) {
        super.onBackPressed();
    }
}
