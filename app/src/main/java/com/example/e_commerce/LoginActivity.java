package com.example.e_commerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerce.Model.Users;
import com.example.e_commerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText InputNumber, Inputpassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private TextView AdminLink, NotAdminLink;
    private String parentDbName = "Users";
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InputNumber = (EditText) findViewById(R.id.login_phone_number_input);
        Inputpassword = (EditText) findViewById(R.id.login_password_input);
        LoginButton = (Button) findViewById(R.id.login_btn);
        loadingBar = new ProgressDialog(this);
        checkBox =  findViewById(R.id.remember_me_checkbox);
        AdminLink = (TextView) findViewById(R.id.admin_panel_link);
        NotAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);

        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                checkBox.setChecked(false);
                checkBox.setVisibility(View.INVISIBLE);
                parentDbName = "Admins";
            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login");
                NotAdminLink.setVisibility(View.INVISIBLE);
                AdminLink.setVisibility(View.VISIBLE);
                checkBox.setVisibility(View.VISIBLE);
                parentDbName = "Users";
            }
        });

    }

    private void LoginUser() {
        String phone = InputNumber.getText().toString();
        String password = Inputpassword.getText().toString();
        if (TextUtils.isEmpty(phone)){
            Toast.makeText(LoginActivity.this, "Please input phone number...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, "Please input password...", Toast.LENGTH_SHORT).show();

        }
        else{
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait while we are checking your credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(phone, password);
        }
    }

    public void AllowAccessToAccount(String phone, String password) {

        if (checkBox.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey ,password);

        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(parentDbName).child(phone).exists()){
                    Users usersData = snapshot.child(parentDbName).child(phone).getValue(Users.class);
                    if (usersData.getPhone().equals(phone)){
                        if(usersData.getPassword().equals(password)){

                            if (parentDbName.equals("Admins")){
                                Toast.makeText(LoginActivity.this, "Welcome Admin, You are logged In successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);
                            }
                            else if (parentDbName.equals("Users")){
                                Toast.makeText(LoginActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();


                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);
                            }

                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Password is incorrect! ", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                        }
                    }
                }
                else{
                    if (parentDbName.equals("Admins")){
                        Toast.makeText(LoginActivity.this, "Admin with this " + phone + " does not exist ", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    else if (parentDbName.equals("Users")){
                        Toast.makeText(LoginActivity.this, "Account with this " + phone + " does not exist ", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}