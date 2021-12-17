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

public class AccounterActivity extends AppCompatActivity {
    private final DatabaseReference ACCOUNTER_DB= FirebaseDatabase.getInstance().getReference("ACCOUNTER");
    private final DatabaseReference ORDERS_DB   = FirebaseDatabase.getInstance().getReference("ORDERS");

    private TextView a_a_allOrders;
    private RecyclerView a_a_ordersRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounter);

        a_a_allOrders = findViewById(R.id.a_a_allOrders);
        a_a_ordersRecyclerView = findViewById(R.id.a_a_ordersRecyclerView);
        a_a_ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        a_a_ordersRecyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ACCOUNTER_DB.orderByChild("orderStatus").equalTo("Not_yet").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { a_a_allOrders.setText("All Orders = "+ snapshot.getChildrenCount()); }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { Toast.makeText(getBaseContext(), error.getCode(), Toast.LENGTH_SHORT).show(); }
        });
        showOrders();
    }

    private void showOrders() {
        FirebaseRecyclerOptions<AccounterModel> options =
                new FirebaseRecyclerOptions.Builder<AccounterModel>().setQuery(ACCOUNTER_DB.orderByChild("orderStatus").equalTo("Not_yet"), AccounterModel.class).setLifecycleOwner(this).build();
        FirebaseRecyclerAdapter<AccounterModel,AccounterAdapter> adapter =
                new FirebaseRecyclerAdapter<AccounterModel,AccounterAdapter>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AccounterAdapter holder, int position, @NonNull AccounterModel model) {
                        String date        = model.getDate();
                        String time        = model.getTime();
                        String orderId     = model.getOrderId();
                        String phoneNumber = model.getPhoneNumber();
                        String orderPrice  = model.getOrderPrice();
                        holder.showOrders(phoneNumber,date,time,orderId,orderPrice);
                        holder.itemView.setOnClickListener(view -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AccounterActivity.this);
                            builder.setTitle(R.string.options);
                            builder.setIcon(R.drawable.ic_photo_24);
                            builder.setPositiveButton(R.string.show_this_order, (dialog, which) -> showOrder(phoneNumber,orderId));
//                            builder.setNeutralButton(R.string.payment, (dialog, which) -> compliteOrder(phoneNumber,orderPrice,orderId,date,time,"Paied" ));
                            builder.setNegativeButton(R.string.payment, (dialog, which) -> paymentMethod(phoneNumber,orderPrice,orderId));
                            builder.show();
                        });
                    }

                    private void paymentMethod(String phoneNumber, String orderPrice,String orderId) {
                        Intent intent = new Intent(AccounterActivity.this,PaymentActivity.class);
                        intent.putExtra("orderPhoneNumber",phoneNumber);
                        intent.putExtra("orderPrice",orderPrice);
                        intent.putExtra("orderId",orderId);
                        startActivity(intent);
//                        ORDERS_DB.child(phoneNumber).child(orderId).setValue(new OrderModel(phoneNumber,orderPrice,orderId,date,time,orderStatus));
                    }

                    private void showOrder(String phoneNumber,String orderId) {
                        Intent intent = new Intent(getBaseContext(),SingleOrderActivity.class);
                        intent.putExtra("orderId",orderId);
                        intent.putExtra("phoneNumber",phoneNumber);
                        startActivity(intent);
                    }

                    @NonNull
                    @Override
                    public AccounterAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        return new AccounterAdapter(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_orders_item, parent, false));
                    }
                };
        a_a_ordersRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }

    ///////////////////////////////////
    /////////Accounter Adapter/////////
    ///////////////////////////////////
    private static class AccounterAdapter extends RecyclerView.ViewHolder {

        public AccounterAdapter(@NonNull View itemView) {
            super(itemView);
        }

        public void showOrders(String phoneNumber,String date, String time, String orderId,String orderPrice) {
            TextView c_o_i_phoneNumber= itemView.findViewById(R.id.c_o_i_phoneNumber);
            TextView c_o_i_dateTime   = itemView.findViewById(R.id.c_o_i_dateTime);
            TextView c_o_i_orderId    = itemView.findViewById(R.id.c_o_i_orderId);
            TextView c_o_i_orderPrice = itemView.findViewById(R.id.c_o_i_orderPrice);
            c_o_i_phoneNumber.setText(phoneNumber);
            c_o_i_dateTime.setText(date+"\n"+time);
            c_o_i_orderId.setText("Order Num : "+orderId);
            c_o_i_orderPrice.setText("Order Price : "+orderPrice);
        }
    }
}