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
    private final DatabaseReference WALLETS_DB= FirebaseDatabase.getInstance().getReference("WALLETS");

    private EditText a_p_searchView;
    private Button a_p_scanQR ,a_p_userWallet ,a_p_cash;
    private TextView a_p_orderDetails;
    private String orderPhoneNumber,orderPrice,orderId;
    private String userWallet;
    private int orderPoints;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        orderPhoneNumber = getIntent().getStringExtra("orderPhoneNumber");
        orderPrice       = getIntent().getStringExtra("orderPrice");
        orderId          = getIntent().getStringExtra("orderId");

        a_p_searchView   = findViewById(R.id.a_p_searchView);
        a_p_scanQR       = findViewById(R.id.a_p_scanQR);
        a_p_userWallet   = findViewById(R.id.a_p_userWallet);
        a_p_cash         = findViewById(R.id.a_p_cash);
        a_p_orderDetails = findViewById(R.id.a_p_orderDetails);


    }

    @Override
    protected void onStart() {
        super.onStart();
        int orderPoints = Integer.parseInt(orderPrice)*100;
        WALLETS_DB.child(orderPhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                WalletModel walletModel = snapshot.getValue(WalletModel.class);
                userWallet = walletModel.getPoints();
                int x = Integer.parseInt(userWallet);
                if(x >= orderPoints){ a_p_userWallet.setClickable(true); }
                else{ a_p_userWallet.setClickable(false); }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getBaseContext(), error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        a_p_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { cashBack(orderPoints);}
        });

        a_p_userWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { }
        });
        a_p_scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(PaymentActivity.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                intentIntegrator.setCameraId(0);
                intentIntegrator.initiateScan();
            }
        });
    }

    private void cashBack(int orderPoints) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) { scanResult(intentResult.getContents()); }
        else{ super.onActivityResult(requestCode, resultCode, data); }
    }

    private void scanResult(String scanResult) {
        if(scanResult.equals(orderPhoneNumber)){ a_p_orderDetails.setText("OrderId:"+orderId+"\nPhoneNumber"+orderPhoneNumber+"\nOrderPrice"+orderPrice); }
        else{ Toast.makeText(getBaseContext(), "من فضلك تاكد من الحساب الذي قمت بعمل الطلب منه", Toast.LENGTH_SHORT).show(); }
    }
}