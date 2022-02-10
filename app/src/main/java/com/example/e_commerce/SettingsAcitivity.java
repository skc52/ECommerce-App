package com.example.e_commerce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerce.Model.Users;
import com.example.e_commerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.nio.channels.Pipe;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsAcitivity extends AppCompatActivity {


    private CircleImageView profileImage;
    private EditText fullName, address, phoneNum;
    private TextView changePP, close, update;
    private Uri imageUri;
    private String myUrl = "";
    private StorageReference storageReference;
    private StorageTask uploadTask;
    private String checker = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_acitivity);
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Pictures");
        profileImage = findViewById(R.id.profile_image_settings);
        fullName = findViewById(R.id.fullname_settings);
        address = findViewById(R.id.address_settings);
        phoneNum = findViewById(R.id.phone_number_settings);
        changePP = findViewById(R.id.change_profile_settings);
        update = findViewById(R.id.update_settings);
        close = findViewById(R.id.close_settings);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicked")){
                    userInfoSaved();
                }else{
                    updateOnlyUserInfo();
                }
            }
        });

        changePP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsAcitivity.this);
            }
        });


        displayUserInfo(profileImage, fullName, address, phoneNum);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data!= null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImage.setImageURI(imageUri);

        }
        else{
            Toast.makeText(SettingsAcitivity.this, "Error, Please Try Again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsAcitivity.this, SettingsAcitivity.class));
            finish();
        }
    }

    private void updateOnlyUserInfo() {
        if (TextUtils.isEmpty(phoneNum.getText().toString())){
            Toast.makeText(SettingsAcitivity.this,"Phone Number is mandatory!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(fullName.getText().toString())){
            Toast.makeText(SettingsAcitivity.this,"Name is mandatory!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(address.getText().toString())){
            Toast.makeText(SettingsAcitivity.this,"Address is mandatory!", Toast.LENGTH_SHORT).show();
        }
        else{
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
            HashMap<String , Object> userMap = new HashMap<>();
            userMap.put("name", fullName.getText().toString());
            userMap.put("phoneNum", phoneNum.getText().toString());

            userMap.put("address", address.getText().toString());

            reference.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

            reference.child(Prevalent.currentOnlineUser.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users usersdata = snapshot.getValue(Users.class);
                    Prevalent.currentOnlineUser = usersdata;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            startActivity(new Intent(SettingsAcitivity.this, HomeActivity.class));
            Toast.makeText(SettingsAcitivity.this, "Profile Info Updated Successfully!!!", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(phoneNum.getText().toString())){
            Toast.makeText(SettingsAcitivity.this,"Phone Number is mandatory!", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(fullName.getText().toString())){
            Toast.makeText(SettingsAcitivity.this,"Name is mandatory!", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(address.getText().toString())){
            Toast.makeText(SettingsAcitivity.this,"Address is mandatory!", Toast.LENGTH_SHORT).show();
        }
        uploadImage();


    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile Pic");
        progressDialog.setMessage("Please wait while we are updating your account info...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        if (imageUri != null) {
            final StorageReference fileRef =storageReference.child(Prevalent.currentOnlineUser.getPhone() +
                    ".jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();

                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadurl = task.getResult();
                        myUrl = downloadurl.toString();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String , Object> userMap = new HashMap<>();
                        userMap.put("name", fullName.getText().toString());
                        userMap.put("phoneNum", phoneNum.getText().toString());

                        userMap.put("address", address.getText().toString());
                        userMap.put("image", myUrl);
                        reference.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);
                        reference.child(Prevalent.currentOnlineUser.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Users usersdata = snapshot.getValue(Users.class);
                                Prevalent.currentOnlineUser = usersdata;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        progressDialog.dismiss();
                        startActivity(new Intent(SettingsAcitivity.this, HomeActivity.class));
                        Toast.makeText(SettingsAcitivity.this, "Profile Info update successfully", Toast.LENGTH_SHORT).show();
                        finish();



                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(SettingsAcitivity.this, "Error", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
        else{
            Toast.makeText(SettingsAcitivity.this, "Image is not selected", Toast.LENGTH_SHORT).show();

        }

    }

    private void displayUserInfo(CircleImageView profileImage, EditText fullName, EditText address, EditText phoneNum) {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("image").exists()){
                        String image = snapshot.child("image").getValue().toString();
                        String name = snapshot.child("name").getValue().toString();
                        String password = snapshot.child("password").getValue().toString();

                        String num = snapshot.child("phoneNum").getValue().toString();
                        String addres = snapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImage);
                        fullName.setText(name);
                        if (!num.isEmpty()){
                            phoneNum.setText(num);

                        }
                        address.setText(addres);

                    }
                    else{
                        if (snapshot.child("phoneNum").exists()){
                            String num = snapshot.child("phoneNum").getValue().toString();
                            phoneNum.setText(num);

                        }
                        else{
                            String num = snapshot.child("phone").getValue().toString();
                            phoneNum.setText(num);

                        }

                        if (snapshot.child("name").exists()){
                            String name = snapshot.child("name").getValue().toString();
                            fullName.setText(name);
                        }


                        if (snapshot.child("address").exists()){
                            String addres = snapshot.child("address").getValue().toString();
                            address.setText(addres);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}