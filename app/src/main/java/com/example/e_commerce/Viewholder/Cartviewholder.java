package com.example.e_commerce.Viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.e_commerce.ItemClickListener;
import com.example.e_commerce.R;

public class Cartviewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public LinearLayout linearLayout;
    public TextView productName, totalPrice, cartProductId;
    public ElegantNumberButton elegantNumberButton;
    public Button SubmitChanges;
//    public ImageButton deleteBtn;
    public ItemClickListener listener; //interface

    public Cartviewholder(@NonNull View itemView) {
        super(itemView);
        productName = itemView.findViewById(R.id.cartitemname);
        totalPrice = itemView.findViewById(R.id.priceTotal);
        elegantNumberButton = itemView.findViewById(R.id.cartitemchangenobutton);
        SubmitChanges = itemView.findViewById(R.id.submitChangesToCartBtn);
//        deleteBtn = itemView.findViewById(R.id.deleteCartBtn);
        linearLayout = itemView.findViewById(R.id.linearLayoutCart);
        cartProductId = itemView.findViewById(R.id.cartProductId);

    }


    @Override
    public void onClick(View v) {

            listener.onClick(v, getAbsoluteAdapterPosition(), false);
    }
    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }
}
