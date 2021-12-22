package com.elzayet.food_control;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OrdersActivity extends AppCompatActivity {
    private final DatabaseReference KITCHEN_DB  = FirebaseDatabase.getInstance().getReference("KITCHEN");
    private final DatabaseReference ORDERS_DB   = FirebaseDatabase.getInstance().getReference("ORDERS");

    private TextView a_o_allOrders ;
    private RecyclerView a_o_ordersRecyclerView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        a_o_allOrders = findViewById(R.id.a_o_allOrders);
        a_o_ordersRecyclerView = findViewById(R.id.a_o_ordersRecyclerView);
        a_o_ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        a_o_ordersRecyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        KITCHEN_DB.orderByChild("orderStatus").equalTo("processed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { a_o_allOrders.setText("All Orders = "+ snapshot.getChildrenCount()); }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { Toast.makeText(getBaseContext(), error.getCode(), Toast.LENGTH_SHORT).show(); }
        });

        showOrders();
    }

    private void showOrders() {
        FirebaseRecyclerOptions<OrderModel> options =
                new FirebaseRecyclerOptions.Builder<OrderModel>().setQuery(KITCHEN_DB.orderByChild("orderStatus").equalTo("processed"), OrderModel.class).setLifecycleOwner(this).build();
        FirebaseRecyclerAdapter<OrderModel, OrdersAdapter> adapter =
                new FirebaseRecyclerAdapter<OrderModel, OrdersAdapter>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull OrdersAdapter holder, int position, @NonNull OrderModel model) {
                        String date        = model.getDate();
                        String time        = model.getTime();
                        String orderId     = model.getOrderId();
                        String phoneNumber = model.getPhoneNumber();
                        String orderPrice  = model.getOrderPrice();
                        holder.showOrders(phoneNumber,date,time,orderId);
                        holder.itemView.setOnClickListener(view -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(OrdersActivity.this);
                            builder.setTitle(R.string.options);
                            builder.setIcon(R.drawable.ic_photo_24);
                            builder.setPositiveButton(R.string.show_this_order, (dialog, which) -> showOrder(phoneNumber,orderId));
                            builder.setNeutralButton(R.string.complite_this_order, (dialog, which) -> compliteOrder(phoneNumber,orderPrice,orderId,date,time));
                            builder.show();
                        });
                    }

                    private void compliteOrder(String phoneNumber, String orderPrice, String orderId, String date, String time) {
                        KITCHEN_DB.child(orderId).setValue(new OrderModel(phoneNumber,orderPrice,orderId,date,time, "Done"));
                        ORDERS_DB.child(phoneNumber).child(orderId).setValue(new OrderModel(orderId,orderPrice,date, time,"Done","Not Paid",""));
                    }

                    private void showOrder(String phoneNumber,String orderId) {
                        Intent intent = new Intent(getBaseContext(),SingleOrderActivity.class);
                        intent.putExtra("orderId",orderId);
                        intent.putExtra("phoneNumber",phoneNumber);
                        startActivity(intent);
                    }

                    @NonNull
                    @Override
                    public OrdersAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        return new OrdersAdapter(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_orders_item, parent, false));
                    }
                };
        a_o_ordersRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }

    ///////////////////////////////////
    /////////Orders Adapter///////////
    ///////////////////////////////////
    private static class OrdersAdapter extends RecyclerView.ViewHolder {

        public OrdersAdapter(@NonNull View itemView) {
            super(itemView);
        }

        public void showOrders(String phoneNumber,String date, String time, String orderId) {
            TextView c_o_i_phoneNumber= itemView.findViewById(R.id.c_o_i_phoneNumber);
            TextView c_o_i_dateTime   = itemView.findViewById(R.id.c_o_i_dateTime);
            TextView c_o_i_orderId    = itemView.findViewById(R.id.c_o_i_orderId);
            c_o_i_phoneNumber.setText(phoneNumber);
            c_o_i_dateTime.setText(date+"\n"+time);
            c_o_i_orderId.setText("Order Num : "+orderId);
        }
    }

}