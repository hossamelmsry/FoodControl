package com.elzayet.food_control;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity {

    private TextView a_u_allUsers ,a_u_addNotification,a_u_allPoints;
    private RecyclerView a_u_usersRecyclerView ;
    private FloatingActionButton a_u_addUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        a_u_allUsers        = findViewById(R.id.a_u_allUsers);
        a_u_addNotification = findViewById(R.id.a_u_addNotification);
        a_u_allPoints       = findViewById(R.id.a_u_allPoints);
        a_u_addUser         = findViewById(R.id.a_u_addUser);
        a_u_usersRecyclerView=findViewById(R.id.a_u_usersRecyclerView);
        a_u_usersRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL,false));
        a_u_usersRecyclerView.setHasFixedSize(true);

        a_u_addNotification.setOnClickListener(v -> newNotification());
        a_u_addUser.setOnClickListener(v -> addUser());
    }
    long x = 0 ;
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase.getInstance().getReference("ACCOUNTS")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {
                        a_u_allUsers.setText(""+snap.getChildrenCount());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        FirebaseDatabase.getInstance().getReference("WALLETS")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot d : snapshot.getChildren()) {
                            FirebaseDatabase.getInstance().getReference("WALLETS").child(d.getKey())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            PointsModel pointsModel = snapshot.getValue(PointsModel.class);
                                            int x = Integer.parseInt(pointsModel.getPoints()) + 100;
                                            a_u_allPoints.setText(Integer.toString(x));
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) { }
                                    });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

//        showAllUsers();
    }

    private void addUser() {
        Toast.makeText(getBaseContext(), "Clicked", Toast.LENGTH_SHORT).show();
    }

    private void newNotification() {
        Toast.makeText(getBaseContext(), "Clicked", Toast.LENGTH_SHORT).show();
    }

    private String userPassword, userPinCode, userEmail, userName, phoneNumber, userRefellar, signupDate;
    private void showAllUsers() {
        DatabaseReference ACCOUNTS_DB = FirebaseDatabase.getInstance().getReference("ACCOUNTS");
        FirebaseRecyclerOptions<UserModel> options =
                new FirebaseRecyclerOptions.Builder<UserModel>().setQuery(ACCOUNTS_DB , UserModel.class).setLifecycleOwner( UsersActivity.this).build();
        FirebaseRecyclerAdapter<UserModel, usersAdapter> adapter =
                new FirebaseRecyclerAdapter<UserModel, usersAdapter>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull usersAdapter holder, int position, @NonNull UserModel model) {
                        phoneNumber  = model.getPhoneNumber();
                        userName     = model.getUserName();
                        userPassword = model.getUserPassword();
                        userEmail = model.getUserEmail();
                        userPinCode  = model.getUserPinCode();
                        userRefellar = model.getUserRefellar();
                        signupDate   = model.getSignupDate();

//                        holder.showUsers(phoneNumber,userName,userPassword,userEmail,userPinCode,userRefellar,signupDate,"500");
                    }
                    @NonNull
                    @Override
                    public usersAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        return new usersAdapter(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_user_item, parent, false));
                    }
                };
        a_u_usersRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();

    }

    private static class usersAdapter extends RecyclerView.ViewHolder {

        public usersAdapter(@NonNull View itemView) {
            super(itemView);
        }

        public void showUsers( String phoneNumber,String userName,String userPassword,String userEmail,String userPinCode,String userRefellar,String signupDate,String userPoints) {
            TextView userDetails = itemView.findViewById(R.id.userDetails);
            userDetails.setText("phoneNumber : "+phoneNumber+"\nuserName : "+userName+"\nuserPassword : "+userPassword+"\nuserEmail : "+userEmail+"\nuserPinCode : "+userPinCode+"\nuserRefellar : "+userRefellar+"\nsignupDate : "+signupDate+"\nuserPoints : "+userPoints);
        }

    }

}