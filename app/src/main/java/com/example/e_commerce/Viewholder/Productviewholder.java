package com.example.e_commerce.Viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerce.ItemClickListener;
import com.example.e_commerce.R;

public class Productviewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView productName, productDes, productPrice;
    public CardView cardViewPop;
    public ProgressBar loading;
    public ImageView productImage;
    public ItemClickListener listener; //interface

    public Productviewholder(@NonNull View itemView) {
        super(itemView);
        productImage = (ImageView) itemView.findViewById(R.id.product_image);
        productDes = (TextView) itemView.findViewById(R.id.product_description);
        productName = (TextView) itemView.findViewById(R.id.product_name);
        productPrice = (TextView) itemView.findViewById(R.id.product_price);
        cardViewPop = (CardView) itemView.findViewById(R.id.cardViewProduct);
        loading = itemView.findViewById(R.id.loadingImg);
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAbsoluteAdapterPosition(), false);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }


}

