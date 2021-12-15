package com.elzayet.food_control;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CategoryActivity extends AppCompatActivity {
    private final DatabaseReference PRODUCTS_DB = FirebaseDatabase.getInstance().getReference("PRODUCTS");

    private TextView a_c_allProducts ;
    private RecyclerView a_c_recyclerView ;
    private FloatingActionButton a_c_addProduct ;
    private String menuName ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        menuName = getIntent().getStringExtra("menuName");

        a_c_allProducts = findViewById(R.id.a_c_allProducts);
        a_c_addProduct  = findViewById(R.id.a_c_addProduct);
        a_c_recyclerView = findViewById(R.id.a_c_recyclerView);
        a_c_recyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(),2));
        a_c_recyclerView.setHasFixedSize(true);

        a_c_addProduct.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext() ,AddProductActivity.class );
            intent.putExtra("menuName",menuName);
            startActivity(intent);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        PRODUCTS_DB.orderByChild("menuName").startAt(menuName).endAt(menuName+"\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    a_c_allProducts.setText("All"+menuName+" = "+ snapshot.getChildrenCount());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        showProducts();
    }

    private void showProducts() {
        DatabaseReference PRODUCTS_DB = FirebaseDatabase.getInstance().getReference("PRODUCTS");
        FirebaseRecyclerOptions<ProductModel> options =
                new FirebaseRecyclerOptions.Builder<ProductModel>().setQuery(PRODUCTS_DB.orderByChild("menuName").equalTo(menuName) , ProductModel.class).setLifecycleOwner((LifecycleOwner) this).build();

        FirebaseRecyclerAdapter<ProductModel, productAdapter> adapter =
                new FirebaseRecyclerAdapter<ProductModel, productAdapter>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull productAdapter holder, int position, @NonNull ProductModel model) {
                        String menuName    = model.getMenuName();
                        String productId    = model.getProductId();
                        String productImage = model.getProductImage();
                        String productName  = model.getProductName();
                        String productDescription  = model.getProductDescription();
                        holder.showProduct(productImage,productName);
                        holder.c_p_i_edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getBaseContext(),AddProductActivity.class);
                                intent.putExtra("menuName",menuName);
                                intent.putExtra("productId",productId);
                                intent.putExtra("productImage",productImage);
                                intent.putExtra("productName",productName);
                                intent.putExtra("productDescription",productDescription);
                                startActivity(intent);
                            }
                        });

                        holder.c_p_i_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PRODUCTS_DB.child(productId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            PRODUCTS_DB.child(productId).removeValue();                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                    @NonNull
                    @Override
                    public productAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        return new productAdapter(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_product_item, parent, false));
                    }
                };
        a_c_recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }


    private static class productAdapter extends RecyclerView.ViewHolder {

        public ImageView c_p_i_edit ;
        public ImageView c_p_i_delete ;
        public productAdapter(@NonNull View itemView) {
            super(itemView);
        }

        public void showProduct( String productImage,String  productName) {
            c_p_i_edit   = itemView.findViewById(R.id.c_p_i_edit);
            c_p_i_delete = itemView.findViewById(R.id.c_p_i_delete);
            ImageView c_p_i_productImage= itemView.findViewById(R.id.c_p_i_productImage);
            TextView c_p_i_productName  = itemView.findViewById(R.id.c_p_i_productdetails);

            Picasso.get().load(productImage).placeholder(R.drawable.ic_photo_24).error(R.drawable.ic_photo_24).into(c_p_i_productImage);
            c_p_i_productName.setText(productName + "\n"  + "\n" );
        }

    }


}