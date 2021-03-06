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
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddProductActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final DatabaseReference PRODUCTS_DB = FirebaseDatabase.getInstance().getReference("PRODUCTS");
    private final StorageReference storageRef   = FirebaseStorage.getInstance().getReference("PRODUCTS");
    public static final int GALLERY_REQUEST = 3 ;

    private Uri imageUri ;
    private ImageView a_a_p_productImage ;
    private TextInputEditText a_a_p_productName,a_a_p_productDescription,a_a_p_smallSize,a_a_p_mediumSize,a_a_p_largeSize;
    private Button a_a_p_submit;
    private Spinner a_a_p_categorySpinner ;
    private TextView a_a_p_categoryText ;
    //update product
    private String productId,productName,productDescription,smallSize,mediumSize,largeSize,productImage;
    //new product
    private String menuName ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        menuName       = getIntent().getStringExtra("menuName");
        productId      = getIntent().getStringExtra("productId");
        productName    = getIntent().getStringExtra("productName");
        productDescription= getIntent().getStringExtra("productDescription");
        smallSize      = getIntent().getStringExtra("smallSize");
        mediumSize     = getIntent().getStringExtra("mediumPrice");
        largeSize      = getIntent().getStringExtra("largePrice");
        productImage   = getIntent().getStringExtra("productImage");

        a_a_p_productImage       = findViewById(R.id.a_a_p_productImage);
        a_a_p_productName        = findViewById(R.id.a_a_p_productName);
        a_a_p_productDescription = findViewById(R.id.a_a_p_productDescription);
        a_a_p_smallSize          = findViewById(R.id.a_a_p_smallSize);
        a_a_p_mediumSize         = findViewById(R.id.a_a_p_mediumSize);
        a_a_p_largeSize          = findViewById(R.id.a_a_p_largeSize);
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
        if(productId == null){
            productId = PRODUCTS_DB.push().getKey();
        }
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
            a_a_p_smallSize.setText(smallSize);
            a_a_p_mediumSize.setText(mediumSize);
            a_a_p_largeSize.setText(largeSize);
            a_a_p_categoryText.setText("Categoty = "+menuName);
        }
        a_a_p_submit.setOnClickListener(v -> validation());
    }

    public void validation() {
        productName        = a_a_p_productName.getText().toString().trim();
        productDescription = a_a_p_productDescription.getText().toString().trim();
        smallSize          = a_a_p_smallSize.getText().toString().trim();
        mediumSize         = a_a_p_mediumSize.getText().toString().trim();
        largeSize          = a_a_p_largeSize.getText().toString().trim();
        if(!TextUtils.isEmpty(productName)){
            a_a_p_productName.setError(null);
            if(!TextUtils.isEmpty(productDescription)){
                a_a_p_productDescription.setError(null);
                if(productDescription.length() <= 100){
                    a_a_p_productDescription.setError(null);
                    if(!TextUtils.isEmpty(smallSize)){
                        a_a_p_smallSize.setError(null);
                        if(!TextUtils.isEmpty(mediumSize)){
                            a_a_p_mediumSize.setError(null);
                            if(!TextUtils.isEmpty(largeSize)){
                                a_a_p_largeSize.setError(null);
                                if (imageUri != null) { uploadImage(); }
                                else{
                                    Toast.makeText(this, "Select a Product Image", Toast.LENGTH_SHORT).show();
                                    a_a_p_largeSize.setText("");
                                    a_a_p_largeSize.requestFocus();
                                }
                            }else {
                                a_a_p_largeSize.setError(getString(R.string.large_size));
                                a_a_p_largeSize.requestFocus();
                            }
                        }else {
                            a_a_p_mediumSize.setError(getString(R.string.medium_size));
                            a_a_p_mediumSize.requestFocus();
                        }
                    }else {
                        a_a_p_smallSize.setError(getString(R.string.small_size));
                        a_a_p_smallSize.requestFocus();
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

    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mm = MimeTypeMap.getSingleton();
        return mm.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadImage() {
        ProgressDialog progressDialog = new ProgressDialog(AddProductActivity.this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final StorageReference reference= storageRef.child(productId+"."+getFileExtension(imageUri));
        final UploadTask uploadTask    = reference.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) { throw task.getException(); }
            productImage = reference.getDownloadUrl().toString();
            return reference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                productImage = task.getResult().toString();
                Toast.makeText(getBaseContext(), "???? ?????????? ???????????? ", Toast.LENGTH_SHORT).show();
                createNewData();
            }
        })).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(AddProductActivity.this, " ...?????? ?????? ?????????? ?????????? ???????????? ..." + e.getMessage(), Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
            progressDialog.setMessage("Uploading "+ (int)progress+" %");
        });
    }
//
    private void createNewData() {

        PRODUCTS_DB.child(productId).setValue(new ProductModel(menuName,productId,productImage,productName,productDescription,smallSize,mediumSize,largeSize));
        finish();
//        for(int z = 1 ; z <= 5 ; z++){
//            productId = PRODUCTS_DB.push().getKey();
//            PRODUCTS_DB.child(productId).setValue(new ProductModel("??????????",productId,productImage,productName,productDescription,smallSize,mediumSize,largeSize,productImageUrl));
//        }
//        for(int z = 1 ; z <= 5 ; z++){
//            productId = PRODUCTS_DB.push().getKey();
//            PRODUCTS_DB.child(productId).setValue(new ProductModel("????????",productId,productImage,productName,productDescription,smallSize,mediumSize,largeSize,productImageUrl));
//        }
//        for(int z = 1 ; z <= 5 ; z++){
//            productId = PRODUCTS_DB.push().getKey();
//            PRODUCTS_DB.child(productId).setValue(new ProductModel("???????????? ??????????",productId,productImage,productName,productDescription,smallSize,mediumSize,largeSize,productImageUrl));
//        }
//        for(int z = 1 ; z <= 5 ; z++){
//            productId = PRODUCTS_DB.push().getKey();
//            PRODUCTS_DB.child(productId).setValue(new ProductModel("?????????? ??????????",productId,productImage,productName,productDescription,smallSize,mediumSize,largeSize,productImageUrl));
//        }
//        for(int z = 1 ; z <= 5 ; z++){
//            productId = PRODUCTS_DB.push().getKey();
//            PRODUCTS_DB.child(productId).setValue(new ProductModel("???????????? ??????",productId,productImage,productName,productDescription,smallSize,mediumSize,largeSize,productImageUrl));
//        }
//        for(int z = 1 ; z <= 5 ; z++){
//            productId = PRODUCTS_DB.push().getKey();
//            PRODUCTS_DB.child(productId).setValue(new ProductModel("????????????????",productId,productImage,productName,productDescription,smallSize,mediumSize,largeSize,productImageUrl));
//        }
//        for(int z = 1 ; z <= 5 ; z++){
//            productId = PRODUCTS_DB.push().getKey();
//            PRODUCTS_DB.child(productId).setValue(new ProductModel("?????????? ????????",productId,productImage,productName,productDescription,smallSize,mediumSize,largeSize,productImageUrl));
//        }
//        if (productName == null) { productId = PRODUCTS_DB.push().getKey(); }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        menuName = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    ////////////////////////////////////
    ///////////checkPermission//////////
    ////////////////////////////////////
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED ;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_REQUEST);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
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