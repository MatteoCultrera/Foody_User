package com.example.foodyrestaurant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MenuEditItem extends AppCompatActivity {

    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_edit_item);

        title = findViewById(R.id.textView);

        title.setText("Edit "+savedInstanceState.getString("MainName"));
    }
}
