package com.example.ecommerceapp.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;

public class ProductViewHolder extends RecyclerView.ViewHolder {
    ImageView productImage;
    TextView productLabel;
    RatingBar ratingBar;
    TextView productEvaluation;
    TextView productDescription;
    TextView productPrice;
    TextView productOrders;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        productImage = itemView.findViewById(R.id.product_list_image);
        productLabel = itemView.findViewById(R.id.product_list_label);
        ratingBar = itemView.findViewById(R.id.product_list_ratingBar);
        productEvaluation = itemView.findViewById(R.id.product_list_evaluation);
        productDescription = itemView.findViewById(R.id.product_list_description);
        productPrice = itemView.findViewById(R.id.product_list_price);
        productOrders = itemView.findViewById(R.id.product_list_orders);
    }

}
