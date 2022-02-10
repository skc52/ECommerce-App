package com.example.e_commerce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity {
    private ImageView tShirts, sportshirts, femaleDresses, sweaters, glasses, hats;
    private ImageView purses, shoes, headphones, laptops, watches, mobiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        tShirts = (ImageView) findViewById(R.id.t_shirts);
        sportshirts = (ImageView) findViewById(R.id.sports);
        femaleDresses = (ImageView) findViewById(R.id.femaleDresses);
        sweaters = (ImageView) findViewById(R.id.sweaters);
        glasses = (ImageView) findViewById(R.id.glasses);
        hats = (ImageView) findViewById(R.id.caps);
        purses = (ImageView) findViewById(R.id.purses);
        shoes = (ImageView) findViewById(R.id.shoes);
        headphones = (ImageView) findViewById(R.id.headphones);
        laptops = (ImageView) findViewById(R.id.laptop);
        watches = (ImageView) findViewById(R.id.watches);
        mobiles = (ImageView) findViewById(R.id.mobiles);

        tShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCategory("Tshirts");
            }
        });
        sportshirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCategory("SportsShirts");
            }
        });
        femaleDresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCategory("Female Dresses");
            }
        });
        sweaters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCategory("Sweaters");
            }
        });
        glasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCategory("Glasses");
            }
        });
        hats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCategory("Hats");
            }
        });
        purses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCategory("Purses");
            }
        });
        shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCategory("Shoes");
            }
        });
        headphones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCategory("Headphones");
            }
        });
        laptops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCategory("Laptops");
            }
        });
        watches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCategory("Watches");
            }
        });
        mobiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCategory("Mobiles");
            }
        });
    }

    private void AddCategory(String item) {
        Intent intent = new Intent(AdminCategoryActivity.this, AddProductAdminActivity.class);
        intent.putExtra("category", item);
        startActivity(intent);
    }
}