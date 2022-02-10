package com.example.e_commerce;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.e_commerce.Model.Products;
import com.example.e_commerce.Model.Users;
import com.example.e_commerce.Prevalent.Prevalent;
import com.example.e_commerce.Viewholder.Productviewholder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import org.w3c.dom.Text;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {


    private CircleImageView circleImageView;
    private TextView username, phone;
    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    private boolean click = true;
    private PopupWindow popupWindow;
    private ImageView popupimg, cancelPopup;
    private TextView popuptitle;
    TextView tv;//popup textview
    private TextView stocksRem, price, itemsInCart;
    ConstraintLayout popupLayout;
    int width, height;
    private ProgressBar popUpPrgBar;
    String numOfItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //pop up layout
        popupWindow = new PopupWindow(this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        popupLayout = (ConstraintLayout) inflater.inflate(R.layout.popup, null, false);
        popupWindow.setContentView(popupLayout);

        setContentView(R.layout.activity_navigation);
        width= getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        height=getApplicationContext().getResources().getDisplayMetrics().heightPixels;
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        Paper.init(this);

        //yo tala ko tin line code le chai Home vanera toolbar ma lekhxa
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        //yo tala ko line haru le chai toggle navigation ko option dinxa
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        //yo tala ko line le chai tyo cart icon jun chai float vairaxa tyo sanaga k garne vanera ho
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch (id){
                    case R.id.nav_cart:
                        Toast.makeText(HomeActivity.this, "Carts", Toast.LENGTH_SHORT).show();
                        Intent intentCart = new Intent(HomeActivity.this, CartActivity.class);
                        startActivity(intentCart);
                        break;
                    case R.id.nav_orders:
                        Toast.makeText(HomeActivity.this, "Orders", Toast.LENGTH_SHORT).show();

                        break;
                    case R.id.nav_category:
                        Toast.makeText(HomeActivity.this, "Categories", Toast.LENGTH_SHORT).show();

                        break;
                    case R.id.nav_settings:
                        Intent intent = new Intent(HomeActivity.this, SettingsAcitivity.class);
                        startActivity(intent);
                        Toast.makeText(HomeActivity.this, "Settings", Toast.LENGTH_SHORT).show();

                        break;
                }


                //tala ko dui line le kunai nabigation item ma thichisake paxi navigation lai retract garxa
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.START);

                return true;

            }
        });





        View headerView = navigationView.getHeaderView(0);
        circleImageView = headerView.findViewById(R.id.user_profile_image);
        username =  headerView.findViewById(R.id.username);
        phone = headerView.findViewById(R.id.number);
        if (!Prevalent.currentOnlineUser.getName().isEmpty()){
            username.setText(Prevalent.currentOnlineUser.getName());
        }
        if (!Prevalent.currentOnlineUser.getPhone().isEmpty()){
            phone.setText(Prevalent.currentOnlineUser.getPhone());
        }
        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(circleImageView);

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));





    }

    //yo override le chai back thicdha navigation lai close garxa
    //yedi override thena vane back thichda pahila ko activity ma janthyo
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
            finishAffinity();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //yo chai recyclerview lai arraylist pass garera options va jasto ho
        //Products mero class ho

        //adapter banaune kam yo code le
        //holder banaune kam chai Productviewholder le garyo
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef, Products.class).build();

        FirebaseRecyclerAdapter<Products, Productviewholder> adapter = new FirebaseRecyclerAdapter<Products, Productviewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull Productviewholder holder, int position, @NonNull Products model) {
                holder.productName.setText(model.getName());
                holder.productDes.setText(model.getDescription());
                holder.productPrice.setText("Price - $" + model.getPrice());
                holder.loading.setVisibility(View.VISIBLE);

                Picasso.get().load(model.getImage()).into(holder.productImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.loading.setVisibility(View.GONE);

                    }

                    @Override
                    public void onError(Exception e) {
                        holder.loading.setVisibility(View.GONE);

                    }


                });

                holder.cardViewPop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO
                        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Carts").child(Prevalent.currentOnlineUser.getPhone()).child(model.getPid());
                        ElegantNumberButton button = (ElegantNumberButton) popupLayout.findViewById(R.id.noOfItems);
                        itemsInCart = popupLayout.findViewById(R.id.itemsInCart);
                        cartRef.child("NoOfItems").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists()){
                                    itemsInCart.setText("Items in Cart: 0");
                                }
                                else{
                                    itemsInCart.setText("Items in Cart: " + snapshot.getValue().toString());

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        cancelPopup = popupLayout.findViewById(R.id.cancelPopup);
                        cancelPopup.setImageResource(R.drawable.ic_baseline_cancel_24);
                        cancelPopup.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupWindow.dismiss();
                            }
                        });

                        tv = popupLayout.findViewById(R.id.descriptionPopup);
                        tv.setText("Description : " + holder.productDes.getText().toString());

                        popuptitle = popupLayout.findViewById(R.id.titlePopUp);
                        popuptitle.setText(holder.productName.getText().toString());


                        Button addToCart = popupLayout.findViewById(R.id.addToCartPopupbtn);
                        addToCart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                numOfItem = button.getNumber();
                                addItemToCart(numOfItem, Prevalent.currentOnlineUser, model);
                            }
                        });

                        popupimg = popupLayout.findViewById(R.id.imageView);
                        popUpPrgBar = popupLayout.findViewById(R.id.popUpPrg);
                        popUpPrgBar.setVisibility(View.VISIBLE);
                        Picasso.get().load(model.getImage()).into(popupimg, new Callback() {
                            @Override
                            public void onSuccess() {
                                popUpPrgBar.setVisibility(View.GONE);

                            }

                            @Override
                            public void onError(Exception e) {
                                popUpPrgBar.setVisibility(View.GONE);

                            }


                        });


                        price = popupLayout.findViewById(R.id.price);
                        price.setText(holder.productPrice.getText().toString());

                        stocksRem = popupLayout.findViewById(R.id.remainingStocks);
                        stocksRem.setText("In Stock :" + model.getStock() );


                        popupWindow.setBackgroundDrawable( getResources().getDrawable(R.drawable.popupdesign) );
                        popupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
                        popupWindow.setFocusable(true);
                        popupWindow.setOutsideTouchable(true);
                        popupWindow.showAtLocation(popupLayout, Gravity.CENTER_HORIZONTAL, 0, 0);

                        popupWindow.update(0,0,width-40, height-100);
                        click = false;

                    }
                });
            }

            @NonNull
            @Override
            public Productviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent,
                        false);
                Productviewholder productviewholder = new Productviewholder(v);
                return productviewholder;

            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    private void addItemToCart(String numOfItem, Users currentOnlineUser, Products product) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String,Object> cartMap = new HashMap<>();
                cartMap.put("ProductId", product.getPid());
                cartMap.put("NoOfItems", numOfItem);
//                reference.child("Carts").child(currentOnlineUser.getPhone() + pid).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                Integer remstock = Integer.parseInt(product.getStock()) - Integer.parseInt(numOfItem);


                if (Integer.parseInt(product.getStock()) >= Integer.parseInt(numOfItem)) {
                    reference.child("Carts").child(currentOnlineUser.getPhone()).child(product.getPid()).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Integer remainingStocks = Integer.parseInt(product.getStock()) - Integer.parseInt(numOfItem);
                                ProductsRef.child(product.getPid()).child("stock").setValue(String.valueOf(remainingStocks));
                                Toast.makeText(HomeActivity.this, "Added to Cart!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(HomeActivity.this, "Network Error! Please try Again", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }

                else{
                    Toast.makeText(HomeActivity.this, "We're sorry! We don't have that many items in our stock. You can see the " +
                            "stock available written below the description button", Toast.LENGTH_SHORT).show();
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void LogOut(MenuItem item) {
        Paper.book().destroy();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}