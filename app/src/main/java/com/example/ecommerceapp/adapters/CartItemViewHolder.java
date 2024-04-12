package com.example.ecommerceapp.adapters;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;

public class CartItemViewHolder extends RecyclerView.ViewHolder {
    RelativeLayout relativeLayout;
    ImageView cartItemImage;
    TextView orderItemLabel;
    TextView orderItemQuantity;
    TextView orderItemTotal;
    ImageButton removeCartItemButton;

    public CartItemViewHolder(@NonNull View itemView) {
        super(itemView);
        relativeLayout = itemView.findViewById(R.id.cart_item_layout);
        cartItemImage = itemView.findViewById(R.id.cart_item_image);
        orderItemLabel = itemView.findViewById(R.id.order_item_label);
        orderItemQuantity = itemView.findViewById(R.id.order_item_quantity);
        orderItemTotal = itemView.findViewById(R.id.order_item_total);
        removeCartItemButton = itemView.findViewById(R.id.remove_cart_item_button);
    }
}
