package com.elzayet.food_control;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission_group.CAMERA;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddProductActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final int GALLERY_REQUEST = 3 ;

    private Uri imageUri ;
    private ImageView a_a_p_productImage ;
    private TextInputEditText a_a_p_productName,a_a_p_productDescription,a_a_p_productPrice;
    private Button a_a_p_submit;
    private Spinner a_a_p_categorySpinner ;
    private TextView a_a_p_categoryText ;
    //update product
    private String productId,productImage,productName,productDescription,productPrice;
    //new product
    private String menuName ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        menuName     = getIntent().getStringExtra("menuName");
        productId    = getIntent().getStringExtra("productId");
        productName  = getIntent().getStringExtra("productName");
        productDescription= getIntent().getStringExtra("productDescription");
        productPrice = getIntent().getStringExtra("productPrice");

        a_a_p_productImage       = findViewById(R.id.a_a_p_productImage);
        a_a_p_productName        = findViewById(R.id.a_a_p_productName);
        a_a_p_productDescription = findViewById(R.id.a_a_p_productDescription);
        a_a_p_productPrice       = findViewById(R.id.a_a_p_productPrice);
        a_a_p_submit             = findViewById(R.id.a_a_p_submit);
        a_a_p_categoryText       = findViewById(R.id.a_a_p_categoryText);
        a_a_p_categorySpinner    = findViewById(R.id.a_a_p_categorySpinner);

        a_a_p_categorySpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> languageSpinnerAdapter = ArrayAdapter.createFromResource(this,R.array.categorySpinner, android.R.layout.simple_spinner_item);
        languageSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        a_a_p_categorySpinner.setAdapter(languageSpinnerAdapter);
//        createNewData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(productName == null) {
            a_a_p_productImage.setOnClickListener(v -> {
                if (checkPermission()) { openGallery(); }
                else { requestPermission(); }
            });
            if(productName != null) {
                a_a_p_categorySpinner.setVisibility(View.GONE);
                a_a_p_categoryText.setText("Categoty = "+menuName);
            }
        } else {
            a_a_p_productImage.setVisibility(View.GONE);
            a_a_p_categorySpinner.setVisibility(View.GONE);
            a_a_p_productName.setText(productName);
            a_a_p_productDescription.setText(productDescription);
            a_a_p_productPrice.setText(productPrice);
            a_a_p_categoryText.setText("Categoty = "+menuName);
        }
        a_a_p_submit.setOnClickListener(v -> validation());
    }

    public void validation() {
        productName        = a_a_p_productName.getText().toString().trim();
        productDescription = a_a_p_productDescription.getText().toString().trim();
        productPrice       = a_a_p_productPrice.getText().toString().trim();
        if(!TextUtils.isEmpty(productName)){
            a_a_p_productName.setError(null);
            if(!TextUtils.isEmpty(productDescription)){
                a_a_p_productDescription.setError(null);
                if(productDescription.length() <= 100){
                    a_a_p_productDescription.setError(null);
                    if(!TextUtils.isEmpty(productPrice)){
                        a_a_p_productPrice.setError(null);
                        if (imageUri != null) { uploadImage(); }
                        else{
                            Toast.makeText(this, "Select a Product Image", Toast.LENGTH_SHORT).show();
                            a_a_p_productPrice.setText("");
                            a_a_p_productPrice.requestFocus();
                        }
                    }else{
                        a_a_p_productPrice.setError(getString(R.string.product_price));
                        a_a_p_productPrice.requestFocus();
                    }
                }else{
                    a_a_p_productDescription.setError("The letters Are Too Match");
                    a_a_p_productDescription.requestFocus();
                }
            }else{
                a_a_p_productDescription.setError(getString(R.string.product_name));
                a_a_p_productDescription.requestFocus();
            }
        }else{
            a_a_p_productName.setError(getString(R.string.product_name));
            a_a_p_productName.requestFocus();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent , GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && requestCode == GALLERY_REQUEST) {
            imageUri = data.getData();
            a_a_p_productImage.setImageURI(imageUri);
        }
    }
    private void uploadImage() {
        ProgressDialog progressDialog = new ProgressDialog(AddProductActivity.this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageReference productImageRef = FirebaseStorage.getInstance().getReference().child("PRODUCTS") ;
        final StorageReference filePath  = productImageRef.child(imageUri.getLastPathSegment() + System.currentTimeMillis() + ".jpg");
        final UploadTask uploadTask      = filePath.putFile(imageUri);
        uploadTask.addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(AddProductActivity.this, " ...حدث خطأ اثناء تحميل الصورة ..." + e.getMessage(), Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) { throw task.getException(); }
            productImage = filePath.getDownloadUrl().toString();
            return filePath.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                productImage = task.getResult().toString();
                Toast.makeText(getBaseContext(), "تم تحميل الصورة ", Toast.LENGTH_SHORT).show();
                createNewData();
            }
        }));
    }
//
    private void createNewData() {
        DatabaseReference PRODUCTS_DB = FirebaseDatabase.getInstance().getReference("PRODUCTS");
        if (productName == null) { productId = PRODUCTS_DB.push().getKey(); }
        PRODUCTS_DB.child(productId).setValue(new ProductModel(menuName,productId,productImage,productName,productDescription,productPrice));
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        menuName = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

//    ////////////////////////////////////
//    ///////////checkPermission//////////
//    ////////////////////////////////////
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED ;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                            showMessageOKCancel("You need to allow access to both the permissions",
                                    (dialog, which) -> {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA}, GALLERY_REQUEST);
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(AddProductActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}