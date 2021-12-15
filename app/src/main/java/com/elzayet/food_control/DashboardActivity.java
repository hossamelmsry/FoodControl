package com.elzayet.food_control;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DashboardActivity extends AppCompatActivity {
    private Button a_d_products ,a_d_users,a_d_orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button a_d_products = findViewById(R.id.a_d_products);
        Button a_d_users    = findViewById(R.id.a_d_users);
        Button a_d_orders   = findViewById(R.id.a_d_orders);
        Button a_d_coupons  = findViewById(R.id.a_d_coupons);
        Button a_d_accounter= findViewById(R.id.a_d_accounter);

        a_d_products.setOnClickListener(v -> startActivity(new Intent(getBaseContext(),ProductsActivity.class)));
        a_d_users.setOnClickListener(v -> startActivity(new Intent(getBaseContext(),UsersActivity.class)));
        a_d_orders.setOnClickListener(v -> startActivity(new Intent(getBaseContext(),OrdersActivity.class)));
        a_d_coupons.setOnClickListener(v -> startActivity(new Intent(getBaseContext(),CouponActivity.class)));
        a_d_accounter.setOnClickListener(v -> startActivity(new Intent(getBaseContext(),AccounterActivity.class)));

    }
}