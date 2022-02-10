package com.example.e_commerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.e_commerce.Model.Cart;
import com.example.e_commerce.Model.Products;
import com.example.e_commerce.Prevalent.Prevalent;
import com.example.e_commerce.Viewholder.Cartviewholder;
import com.example.e_commerce.Viewholder.Productviewholder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CartActivity extends AppCompatActivity {
    private DatabaseReference CartsRef , ProductsRef, OrderRef;
    private RecyclerView rv;
    private ImageButton goBack;
    private Toolbar toolbar;
    FirebaseRecyclerOptions<Cart> options;
    FirebaseRecyclerAdapter<Cart, Cartviewholder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        rv = findViewById(R.id.recyclerCart);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        CartsRef = FirebaseDatabase.getInstance().getReference().child("Carts").child(Prevalent.currentOnlineUser.getPhone());
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        OrderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rv);


        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this, HomeActivity.class));
            }
        });


        options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(CartsRef, Cart.class).build();

        DatabaseReference productref = FirebaseDatabase.getInstance().getReference().child("Products");
        adapter = new FirebaseRecyclerAdapter<Cart, Cartviewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull Cartviewholder holder, int position, @NonNull Cart model) {

                holder.elegantNumberButton.setNumber(model.getNoOfItems());
                holder.cartProductId.setText(model.getProductId());

                holder.elegantNumberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                    @Override
                    public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                        productref.addListenerForSingleValueEvent(new ValueEventListener() {
                              @Override
                              public void onDataChange(@NonNull DataSnapshot snapshot) {

                                  holder.totalPrice.setText("$" + String.valueOf(
                                          (Float.valueOf(snapshot.child(model.getProductId()).child("price").getValue().toString()).floatValue() *
                                                  Integer.parseInt(holder.elegantNumberButton.getNumber()))
                                  ));
                              }

                              @Override
                              public void onCancelled(@NonNull DatabaseError error) {

                              }
                          }

                        );

                    }
                });


                holder.SubmitChanges.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Integer.parseInt(holder.elegantNumberButton.getNumber()) < 1){
                            CartsRef.child(model.getProductId()).removeValue();
                            Toast.makeText(CartActivity.this, "Removed from cart", Toast.LENGTH_SHORT).show();

                        }
                        else{

                            CartsRef.child(model.getProductId()).child("NoOfItems").setValue(holder.elegantNumberButton.getNumber());
                        }

                    }
                });

                productref.addListenerForSingleValueEvent(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot snapshot) {

                          holder.elegantNumberButton.setRange(0, Integer.parseInt(model.getNoOfItems()) + Integer.parseInt(snapshot.child(model.getProductId()).child("stock").getValue().toString()));

                          holder.productName.setText(snapshot.child(model.getProductId()).child("name").getValue().toString());

                          holder.totalPrice.setText("$" + String.valueOf(
                                  (Float.valueOf(snapshot.child(model.getProductId()).child("price").getValue().toString()).floatValue() *
                                          Integer.parseInt(holder.elegantNumberButton.getNumber()))
                          ));
                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError error) {

                      }
                  }


                );


            }

            @NonNull
            @Override
            public Cartviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cartviewitems, parent,
                        false);
                Cartviewholder cartviewholder = new Cartviewholder(v);
                return cartviewholder;
            }
        };
        rv.setAdapter(adapter);
        adapter.startListening();




    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            Prevalent.CartBeingSwiped = isCurrentlyActive;
//            Prevalent.position = viewHolder.getAbsoluteAdapterPosition();
            TextView cartText = viewHolder.itemView.findViewById(R.id.cartProductId);
            Prevalent.currentCartProductID = cartText.getText().toString();
//            Log.d("TAG2345", "onChildDraw: " + Prevalent.CartBeingSwiped + Prevalent.position + Prevalent.currentCartProductID);

        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//            Prevalent.CartBeingSwiped = false;
            switch (direction){
                case ItemTouchHelper.LEFT:
//                    //TODO: need to change the stock remaining accordingly as well.
//                    In order to be able to restore NumItems in the Product database
                    // we need to first get the access to that particular point


                    ProductsRef.child(Prevalent.currentCartProductID).child("stock").setValue("55");
                    CartsRef.child(Prevalent.currentCartProductID).removeValue();

//                    adapter.notifyItemRemoved(Prevalent.position);
                    Toast.makeText(CartActivity.this, "Removed from cart", Toast.LENGTH_SHORT).show();

                    break;
                case ItemTouchHelper.RIGHT:

                    HashMap<String, Object> orderMap = new HashMap<>();
                    ElegantNumberButton elegantNumberButton = viewHolder.itemView.findViewById(R.id.cartitemchangenobutton);
                    String numItem = elegantNumberButton.getNumber();
                    Calendar calendar = Calendar.getInstance();

                    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                    String saveCurrentDate = currentDate.format(calendar.getTime());
                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
                    String saveCurrentTime = currentTime.format(calendar.getTime());
                    orderMap.put("NoOfItems", numItem);

                    TextView price =  viewHolder.itemView.findViewById(R.id.priceTotal);
//                    orderMap.put("TotalPrice", price);
//                    orderMap.put("Date", saveCurrentDate);
//                    orderMap.put("Time", saveCurrentTime);
//                    Log.d("22222", "onSwiped: " +Prevalent.currentOnlineUser.getName());

                    OrderRef.child(Prevalent.currentCartProductID).updateChildren(orderMap);

                    CartsRef.child(Prevalent.currentCartProductID).removeValue();

                    Toast.makeText(CartActivity.this, "Redirect to Make Payment Activity", Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CartActivity.this, HomeActivity.class));
    }




}