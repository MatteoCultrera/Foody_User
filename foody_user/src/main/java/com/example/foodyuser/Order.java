package com.example.foodyuser;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class Order extends AppCompatActivity {

    ArrayList<OrderItem> orders;
    RVAdapterOrder adapter;
    RecyclerView ordersList;
    TextView total;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        init();
    }

    public void init(){
        backButton = findViewById(R.id.backButton);
        JsonHandler handler =  new JsonHandler();
        File directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File orderFile = new File(directory, getString(R.string.order_file_name));

        orders = handler.getOrders(orderFile);


        total = findViewById(R.id.total_price);
        ordersList = findViewById(R.id.order_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        ordersList.setLayoutManager(llm);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        adapter = new RVAdapterOrder(orders, this);
        ordersList.setAdapter(adapter);

        updatePrice();

    }

    public void removeItem(int index){
        orders.remove(index);
        adapter.notifyItemRemoved(index);
        adapter.notifyItemRangeChanged(index, orders.size());
    }

    public void closeActivity(){
        finish();
    }

    public void updatePrice(){
        float tp=0;

        for(int i = 0; i < orders.size(); i++)
            tp += orders.get(i).getTotal();

        total.setText(String.format("%.2f €", tp));
    }
}
