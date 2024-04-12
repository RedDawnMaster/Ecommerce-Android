package com.example.ecommerceapp.adapters;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;

public class OrderViewHolder extends RecyclerView.ViewHolder {

    RelativeLayout orderLayout;
    TextView orderReference;
    TextView orderDate;
    TextView orderDeliveryDate;
    TextView orderDelivered;
    TextView orderTotal;
    TextView orderSize;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        orderLayout = itemView.findViewById(R.id.order_layout);
        orderReference = itemView.findViewById(R.id.order_reference);
        orderDate = itemView.findViewById(R.id.order_date);
        orderDeliveryDate = itemView.findViewById(R.id.order_delivery_date);
        orderDelivered = itemView.findViewById(R.id.order_delivered);
        orderTotal = itemView.findViewById(R.id.order_total);
        orderSize = itemView.findViewById(R.id.order_size);
    }
}
