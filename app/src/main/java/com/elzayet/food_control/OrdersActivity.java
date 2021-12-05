package com.elzayet.food_control;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class OrdersActivity extends AppCompatActivity {

    private TextView a_o_allOrders ;
    private RecyclerView a_o_ordersRecyclerView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        a_o_allOrders = findViewById(R.id.a_o_allOrders);
        a_o_ordersRecyclerView = findViewById(R.id.a_o_ordersRecyclerView);
        a_o_allOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext() , OrderActivity.class));
            }
        });
    }
}