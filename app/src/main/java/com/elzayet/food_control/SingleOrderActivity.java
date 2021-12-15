package com.elzayet.food_control;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SingleOrderActivity extends AppCompatActivity {
    private final DatabaseReference ARCHIVE_DB  = FirebaseDatabase.getInstance().getReference("ARCHIVE");

    private String phoneNumber ,orderId;
    private RecyclerView a_s_o_recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_order);

        phoneNumber = getIntent().getStringExtra("phoneNumber");
        orderId = getIntent().getStringExtra("orderId");

        a_s_o_recyclerView = findViewById(R.id.a_s_o_recyclerView);
        a_s_o_recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        a_s_o_recyclerView.setHasFixedSize(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        showOrder();
    }

    private void showOrder() {
        FirebaseRecyclerOptions<CartModel> options =
                new FirebaseRecyclerOptions.Builder<CartModel>().setQuery(ARCHIVE_DB.child(phoneNumber).child(orderId) , CartModel.class).setLifecycleOwner(this).build();
        FirebaseRecyclerAdapter<CartModel,SingelOrdersAdapter> adapter =
                new FirebaseRecyclerAdapter<CartModel,SingelOrdersAdapter>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull SingelOrdersAdapter holder, int position, @NonNull CartModel model) {
                        String productId       = model.getProductId();
                        String productQuantity = model.getProductQuantity();
                        String productSize     = model.getProductSize();
                        String orderTopping    = model.getOrderTopping();
                        String orderPrice      = model.getOrderPrice();
                        holder.showList(productId,productQuantity,productSize,orderTopping,orderPrice);
                    }
                    @NonNull
                    @Override
                    public SingelOrdersAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        return new SingelOrdersAdapter(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_singel_order_item, parent, false));
                    }
                };
        a_s_o_recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }

    ///////////////////////////////////
    ///////CartFragmentAdapter/////////
    ///////////////////////////////////
    private static class SingelOrdersAdapter extends RecyclerView.ViewHolder {

        public SingelOrdersAdapter(@NonNull View itemView) {
            super(itemView);
        }

        public void showList(String productId, String productQuantity, String productSize, String orderTopping, String orderPrice) {
            ImageView c_s_o_i_productImage  = itemView.findViewById(R.id.c_s_o_i_productImage);
            TextView c_s_o_i_productName    = itemView.findViewById(R.id.c_s_o_i_productName);
            TextView c_s_o_i_cartDescription= itemView.findViewById(R.id.c_s_o_i_cartDescription);
            if(orderTopping == null){ orderTopping = "لا يوجد "; }
            String finalOrderTopping = orderTopping;
            FirebaseDatabase.getInstance().getReference("PRODUCTS").child(productId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                ProductModel productModel = snapshot.getValue(ProductModel.class);
                                Picasso.get().load(productModel.getProductImage()).placeholder(R.drawable.ic_photo_24).error(R.drawable.ic_photo_24).into(c_s_o_i_productImage);
                                c_s_o_i_productName.setText(productQuantity + "|"+productModel.getProductName()+"|"+productSize);
                                c_s_o_i_cartDescription.setText( "Topping:"+ finalOrderTopping +"\n"+"Order Price:"+orderPrice);
                            } else {   Toast.makeText(itemView.getContext(), "لا يوجد هذا المنتج في الوقت الحالي", Toast.LENGTH_SHORT).show();  }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(itemView.getContext(), error.getCode(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}