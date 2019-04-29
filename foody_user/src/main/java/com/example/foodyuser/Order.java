package com.example.foodyuser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;

public class Order extends AppCompatActivity {

    ArrayList<OrderItem> orders;
    RVAdapterOrder adapter;
    RecyclerView ordersList;
    TextView total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        init();
    }

    public void init(){
        orders = new ArrayList<>();

        orders.add(new OrderItem(3, "Pizza Margherita", 4.50f));
        orders.add(new OrderItem(2, "Patatine Fritte", 2.50f));
        orders.add(new OrderItem(3,"Coca Cola", 2.00f));



        total = findViewById(R.id.total_price);
        ordersList = findViewById(R.id.order_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        ordersList.setLayoutManager(llm);


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
