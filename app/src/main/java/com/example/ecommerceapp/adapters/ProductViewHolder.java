package com.example.ecommerceapp.adapters;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;

public class ProductViewHolder extends RecyclerView.ViewHolder {
    RelativeLayout productLayout;
    ImageView productImage;
    TextView productLabel;
    RatingBar ratingBar;
    TextView productEvaluation;
    TextView productDescription;
    TextView productPrice;
    TextView productOrders;
    ImageButton deleteProduct;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        productLayout = itemView.findViewById(R.id.product_layout);
        productImage = itemView.findViewById(R.id.product_list_image);
        productLabel = itemView.findViewById(R.id.product_list_label);
        ratingBar = itemView.findViewById(R.id.product_list_ratingBar);
        productEvaluation = itemView.findViewById(R.id.product_list_evaluation);
        productDescription = itemView.findViewById(R.id.product_list_description);
        productPrice = itemView.findViewById(R.id.product_list_price);
        productOrders = itemView.findViewById(R.id.product_list_orders);
        deleteProduct = itemView.findViewById(R.id.delete_product_button);
    }

}
