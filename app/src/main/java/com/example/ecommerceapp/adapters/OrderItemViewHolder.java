package com.example.ecommerceapp.adapters;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;

public class OrderItemViewHolder extends RecyclerView.ViewHolder {
    RelativeLayout relativeLayout;
    ImageView orderItemImage;
    TextView orderItemLabel;
    TextView orderItemQuantity;
    TextView orderItemTotal;
    Button refundButton;

    public OrderItemViewHolder(@NonNull View itemView) {
        super(itemView);
        relativeLayout = itemView.findViewById(R.id.order_item_layout);
        orderItemImage = itemView.findViewById(R.id.order_item_image);
        orderItemLabel = itemView.findViewById(R.id.order_item_label);
        orderItemQuantity = itemView.findViewById(R.id.order_item_quantity);
        orderItemTotal = itemView.findViewById(R.id.order_item_total);
        refundButton = itemView.findViewById(R.id.refund_button);
    }
}
