package com.example.e_commerce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddProductAdminActivity extends AppCompatActivity {

    private String CategoryName, Description, Price, Name, saveCurrentDate, saveCurrentTime, Stock;
    private Button addProductBtn;
    private ImageView ProductImage;
    private EditText ProductName, ProductDes, ProductPrice, ProductStock;
    private static final int GalleryPick = 1;
    Uri ImageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_admin);

        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        addProductBtn = (Button) findViewById(R.id.add_new_product);
        ProductName = (EditText) findViewById(R.id.product_name);
        ProductDes = (EditText) findViewById(R.id.product_description);
        ProductPrice = (EditText) findViewById(R.id.product_price);
        ProductImage = (ImageView) findViewById(R.id.select_product_image);
        ProductStock = (EditText) findViewById(R.id.product_stock);



        loadingBar = new ProgressDialog(this);

        CategoryName = getIntent().getExtras().get("category").toString();

        ProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductdata();
            }
        });

        Toast.makeText(AddProductAdminActivity.this, CategoryName, Toast.LENGTH_SHORT).show();
    }

    private void ValidateProductdata() {
        Description = ProductDes.getText().toString().trim();
        Name = ProductName.getText().toString().trim();
        Price = ProductPrice.getText().toString();
        Stock = ProductStock.getText().toString();
        if (ImageUri == null){
            Toast.makeText(AddProductAdminActivity.this, "Please choose an image...", Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(Name)){
            Toast.makeText(AddProductAdminActivity.this, "Product Name cannot be empty...", Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(Description)){

            Toast.makeText(AddProductAdminActivity.this, "Product Description cannot be empty...", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(Price)){
            Toast.makeText(AddProductAdminActivity.this, "Product Price cannot be empty...", Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(Stock)){
            Toast.makeText(AddProductAdminActivity.this, "Product Stock cannot be empty...", Toast.LENGTH_SHORT).show();

        }
        else{
            StoreProductInformation();
        }
    }

    private void StoreProductInformation() {

        loadingBar.setTitle("Add New Product");
        loadingBar.setMessage("Dear Admin, please wait while we are adding your product...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;
        StorageReference filepath = ProductImagesRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = filepath.putFile(ImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                String message = e.toString();
                Toast.makeText(AddProductAdminActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                loadingBar.dismiss();

                Toast.makeText(AddProductAdminActivity.this, "Image uploaded successfully...", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            loadingBar.dismiss();
                            throw task.getException();
                        }
                        downloadImageUrl= filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful() ){
                            loadingBar.dismiss();
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AddProductAdminActivity.this, "Product image saved to database...", Toast.LENGTH_SHORT).show();
                            SaveProductInfoToDatabase();
                        }
                    }
                });


            }
        });

    }

    private void SaveProductInfoToDatabase() {
        HashMap<String , Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", Description);
        productMap.put("category", CategoryName);
        productMap.put("name", Name);
        productMap.put("price", Price);
        productMap.put("image", downloadImageUrl);
        productMap.put("stock", Stock);


        ProductsRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(AddProductAdminActivity.this, AdminCategoryActivity.class);
                    startActivity(intent);

                    loadingBar.dismiss();

                    Toast.makeText(AddProductAdminActivity.this, "Product is added to database...", Toast.LENGTH_SHORT).show();

                }
                else{
                    loadingBar.dismiss();
                    String error = task.getException().toString();
                    Toast.makeText(AddProductAdminActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data!=null){
            ImageUri =  data.getData();
            ProductImage.setImageURI(ImageUri);
        }
    }
}