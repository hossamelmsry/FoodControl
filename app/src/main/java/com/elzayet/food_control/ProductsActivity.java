package com.elzayet.food_control;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
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

public class ProductsActivity extends AppCompatActivity {
    private TextView a_p_allProducts;
    private RecyclerView a_p_productsRecycler ,a_p_categoryRecycler;
    private FloatingActionButton a_p_addProduct ;
    private final DatabaseReference PRODUCTS_DB = FirebaseDatabase.getInstance().getReference("PRODUCTS");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_products);
        a_p_allProducts      = findViewById(R.id.a_p_allProducts);
        a_p_addProduct       = findViewById(R.id.a_p_addProduct);

        a_p_categoryRecycler = findViewById(R.id.a_p_categoryRecycler);
        a_p_categoryRecycler.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
        a_p_categoryRecycler.setHasFixedSize(true);

        a_p_productsRecycler = findViewById(R.id.a_p_productsRecycler);
        a_p_productsRecycler.setLayoutManager(new GridLayoutManager(getBaseContext(),2));
        a_p_productsRecycler.setHasFixedSize(true);

        a_p_addProduct.setOnClickListener(v -> startActivity(new Intent(getBaseContext(),AddProductActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        PRODUCTS_DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                a_p_allProducts.setText("All Products = "+ snapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        showCategory();
        showProducts();
    }

    ////////////////////////////////////
    ///////////showCategory/////////////
    ////////////////////////////////////
    private void showCategory() {
        DatabaseReference MENU_DB = FirebaseDatabase.getInstance().getReference("MENU");
        FirebaseRecyclerOptions<MenuModel> options =
                new FirebaseRecyclerOptions.Builder<MenuModel>().setQuery(MENU_DB , MenuModel.class).setLifecycleOwner( this).build();
        FirebaseRecyclerAdapter<MenuModel, productsAdapter> adapter =
                new FirebaseRecyclerAdapter<MenuModel, productsAdapter>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull productsAdapter holder, int position, @NonNull MenuModel model) {
                        String menuImage = model.getMenuImage();
                        String menuName  = model.getMenuName();

                        holder.showMenu(menuImage,menuName);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getBaseContext() , CategoryActivity.class);
                                intent.putExtra("menuName",menuName);
                                startActivity(intent);
                            }
                        });
                    }
                    @NonNull
                    @Override
                    public productsAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        return new productsAdapter(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_category_item, parent, false));
                    }
                };
        a_p_categoryRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }

    private void showProducts() {
        FirebaseRecyclerOptions<ProductModel> options =
                new FirebaseRecyclerOptions.Builder<ProductModel>().setQuery(PRODUCTS_DB , ProductModel.class).setLifecycleOwner(this).build();

        FirebaseRecyclerAdapter<ProductModel, productsAdapter> adapter =
                new FirebaseRecyclerAdapter<ProductModel, productsAdapter>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull productsAdapter holder, int position, @NonNull ProductModel model) {
                        String menuName    = model.getMenuName();
                        String productId    = model.getProductId();
                        String productImage = model.getProductImage();
                        String productName  = model.getProductName();
                        String productDescription  = model.getProductDescription();
                        holder.showProduct(productImage,productName);
                        holder.c_p_i_edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getBaseContext() , AddProductActivity.class);
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
                    public productsAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        return new productsAdapter(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_product_item, parent, false));
                    }
                };
        a_p_productsRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }

    private static class productsAdapter extends RecyclerView.ViewHolder {
        private final DatabaseReference PRODUCTS_DB = FirebaseDatabase.getInstance().getReference("PRODUCTS");
        private String count ;

        public ImageView c_p_i_edit ;
        public ImageView c_p_i_delete ;
        public productsAdapter(@NonNull View itemView) {
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

        public void showMenu(String menuImage,String menuName){
            ImageView c_c_i_menuImage= itemView.findViewById(R.id.c_c_i_menuImage);
            TextView c_c_i_menuName  = itemView.findViewById(R.id.c_c_i_menuName);

            PRODUCTS_DB.orderByChild("menuName").startAt(menuName).endAt(menuName+"\uf8ff").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Picasso.get().load(menuImage).placeholder(R.drawable.ic_photo_24).error(R.drawable.ic_photo_24).into(c_c_i_menuImage);
                        c_c_i_menuName.setText(menuName+"("+snapshot.getChildrenCount()+")");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }
    }


}