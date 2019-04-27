package com.example.foodyuser;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.SharedElementCallback;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RestaurantShow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_show);
        ImageView image = findViewById(R.id.restaurant_image);
        image.setImageResource(R.drawable.user_static_background);


    }
}
