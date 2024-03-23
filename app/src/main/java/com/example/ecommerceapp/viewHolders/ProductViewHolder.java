package com.example.ecommerceapp.viewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;

public class ProductViewHolder extends RecyclerView.ViewHolder {
    private ImageView productImage;
    private TextView productName;
    private RatingBar ratingBar;
    private TextView productEvaluation;
    private TextView productDescription;
    private TextView productPrice;
    private TextView productOrders;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        productImage = itemView.findViewById(R.id.product_image);
        productName = itemView.findViewById(R.id.product_name);
        ratingBar = itemView.findViewById(R.id.ratingBar);
        productEvaluation = itemView.findViewById(R.id.product_evaluation);
        productDescription = itemView.findViewById(R.id.product_description);
        productPrice = itemView.findViewById(R.id.product_price);
        productOrders = itemView.findViewById(R.id.product_orders);
    }
}
