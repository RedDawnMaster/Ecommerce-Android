package com.example.ecommerceapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.activities.MainActivity;
import com.example.ecommerceapp.fragments.OrderItemListFragment;
import com.example.ecommerceapp.models.Order;
import com.example.ecommerceapp.services.OrderItemService;

import java.text.SimpleDateFormat;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {
    private Context context;
    private List<Order> orders;

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    private void showOrderItems(Order order) {
        MainActivity mainActivity = (MainActivity) context;
        OrderItemService.getInstance().setOrderItems(order.getOrderItems());
        mainActivity.replaceFragment(new OrderItemListFragment(order, order.getOrderItems(), this), "OrderItems", true);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Order order = orders.get(position);
        holder.orderReference.setText(order.getReference());
        holder.orderDate.setText("Order Date : " + sdf.format(order.getOrderDate()));
        holder.orderDeliveryDate.setText("Delivery Date : " + sdf.format(order.getDeliveryDate()));
        boolean delivered = order.isDelivered();
        if (delivered) {
            Drawable background = ContextCompat.getDrawable(context, R.drawable.shape_delivered);
            Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_check_circle);
            holder.orderDelivered.setBackground(background);
            holder.orderDelivered.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, icon, null);
            holder.orderDelivered.setText("Delivered");
        } else {
            Drawable background = ContextCompat.getDrawable(context, R.drawable.shape_undelivered);
            Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_time_filled);
            holder.orderDelivered.setBackground(background);
            holder.orderDelivered.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, icon, null);
            holder.orderDelivered.setText("Shipping");
        }
        holder.orderTotal.setText("$" + order.getTotal());
        holder.orderSize.setText(order.getOrderItems().size() + " item(s)");
        holder.orderLayout.setOnClickListener(v -> showOrderItems(order));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
