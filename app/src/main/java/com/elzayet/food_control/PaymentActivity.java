package com.elzayet.food_control;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class PaymentActivity extends AppCompatActivity {
    private final DatabaseReference WALLETS_DB  = FirebaseDatabase.getInstance().getReference("WALLETS");
    private final DatabaseReference ACCOUNTER_DB= FirebaseDatabase.getInstance().getReference("ACCOUNTER");
    private final DatabaseReference ORDERS_DB   = FirebaseDatabase.getInstance().getReference("ORDERS");

    private EditText a_p_searchView;
    private Button a_p_scanQR ,a_p_userWallet ,a_p_cash;
    private TextView a_p_orderDetails;
    private String phoneNumber,orderPrice,orderId,time,date,orderStatus,userPoints;
    private int int_userPoints,int_orderPoints;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        phoneNumber = getIntent().getStringExtra("phoneNumber");
        orderPrice  = getIntent().getStringExtra("orderPrice");
        orderId     = getIntent().getStringExtra("orderId");
        date        = getIntent().getStringExtra("date");
        time        = getIntent().getStringExtra("time");
        orderStatus = getIntent().getStringExtra("orderStatus");

        a_p_searchView   = findViewById(R.id.a_p_searchView);
        a_p_scanQR       = findViewById(R.id.a_p_scanQR);
        a_p_userWallet   = findViewById(R.id.a_p_userWallet);
        a_p_cash         = findViewById(R.id.a_p_cash);
        a_p_orderDetails = findViewById(R.id.a_p_orderDetails);

    }

    @Override
    protected void onStart() {
        super.onStart();
        WALLETS_DB.child(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PointsModel pointsModel = snapshot.getValue(PointsModel.class);
                userPoints = pointsModel.getPoints();
                a_p_cash.setOnClickListener(view -> useCash());
                a_p_userWallet.setOnClickListener(view -> useWallet());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { Toast.makeText(getBaseContext(), error.getCode(), Toast.LENGTH_SHORT).show(); }
        });

        a_p_scanQR.setOnClickListener(view -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(PaymentActivity.this);
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            intentIntegrator.setCameraId(0);
            intentIntegrator.initiateScan();
        });
    }

    private void scanResult(String scanResult) {
        if(scanResult.equals(phoneNumber)){
            a_p_orderDetails.setText("OrderId:"+orderId+"\nPhoneNumber:"+phoneNumber+"\nOrderPrice:"+orderPrice);
            a_p_cash.setEnabled(true);
            int_userPoints  = Integer.parseInt(userPoints);
            int_orderPoints = Integer.parseInt(orderPrice);
            if(int_userPoints >= int_orderPoints){ a_p_userWallet.setEnabled(true); }
            else{ a_p_userWallet.setEnabled(false); }
        }
        else{ Toast.makeText(getBaseContext(), "من فضلك تاكد من الحساب الذي قمت بعمل الطلب منه", Toast.LENGTH_SHORT).show(); }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) { scanResult(intentResult.getContents()); }
        else{ super.onActivityResult(requestCode, resultCode, data); }
    }

    private void useWallet() {
        userPoints = Integer.toString(int_userPoints-int_orderPoints);
        WALLETS_DB.child(phoneNumber).setValue(new PointsModel(userPoints));
        ACCOUNTER_DB.child(orderId).setValue(new AccounterModel(phoneNumber,orderId,orderPrice,date,time,"Paid"));
        ORDERS_DB.child(phoneNumber).child(orderId).setValue(new OrderModel(orderId,orderPrice,date, time,orderStatus,"Paid",""));
        finish();
    }

    private void useCash() {
        userPoints = Integer.toString(int_userPoints+5);
        WALLETS_DB.child(phoneNumber).setValue(new PointsModel(userPoints));
        ACCOUNTER_DB.child(orderId).setValue(new AccounterModel(phoneNumber,orderId,orderPrice,date,time,"Paid"));
        ORDERS_DB.child(phoneNumber).child(orderId).setValue(new OrderModel(orderId,orderPrice,date, time,orderStatus,"Paid",""));
        finish();
    }



}