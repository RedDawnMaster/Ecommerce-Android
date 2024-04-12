package com.example.ecommerceapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.activities.MainActivity;
import com.example.ecommerceapp.fragments.ProductFragment;
import com.example.ecommerceapp.models.Order;
import com.example.ecommerceapp.models.OrderItem;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.models.Review;
import com.example.ecommerceapp.services.OrderItemService;
import com.example.ecommerceapp.services.ReviewService;

import java.util.List;
import java.util.stream.Collectors;

import io.github.muddz.styleabletoast.StyleableToast;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemViewHolder> {
    private Context context;
    private TextView noOrderItems;
    private boolean refundable;
    private Order order;
    private List<OrderItem> orderItems;
    private OrderAdapter orderAdapter;

    public OrderItemAdapter(Context context, TextView noOrderItems, boolean refundable, Order order, List<OrderItem> orderItems, OrderAdapter orderAdapter) {
        this.context = context;
        this.noOrderItems = noOrderItems;
        this.refundable = refundable;
        this.order = order;
        this.orderItems = orderItems;
        this.orderAdapter = orderAdapter;
    }

    private void updateTotal(Order order) {
        double total = 0;
        for (OrderItem orderItem : order.getOrderItems()) {
            total += orderItem.getQuantity() * orderItem.getProduct().getPrice();
        }
        order.setTotal(total);
    }

    private Order findOrder(Order order) {
        return orderAdapter.getOrders().stream().filter(o -> o.getReference().equals(order.getReference())).collect(Collectors.toList()).get(0);
    }

    private void refund(OrderItem orderItem, int position) {
        Thread thread = new Thread(() -> {
            OrderItemService.getInstance().refund(orderItem.getProduct().getLabel(), order.getReference());
            order.getOrderItems().remove(orderItem);
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.runOnUiThread(() -> {
                OrderItemAdapter.this.notifyItemRemoved(position);
                if (order.getOrderItems().isEmpty()) {
                    orderAdapter.getOrders().remove(order);
                    mainActivity.getSupportFragmentManager().popBackStackImmediate();
                } else {
                    Order foundOrder = findOrder(order);
                    updateTotal(foundOrder);
                }
                orderAdapter.notifyDataSetChanged();
                StyleableToast.makeText(context, "Product refunded", R.style.Success).show();
            });
        });
        thread.start();
    }

    private void showProduct(Product product) {
        MainActivity mainActivity = (MainActivity) context;
        Thread thread = new Thread(() -> {
            List<Review> reviews = ReviewService.getInstance().findByProductLabel(product.getLabel());
            mainActivity.runOnUiThread(() -> mainActivity.replaceFragment(new ProductFragment(product, reviews), "Product", true));
        });
        thread.start();
    }


    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem orderItem = orderItems.get(position);
        holder.orderItemImage.setImageResource(R.drawable.avatar_1);
        holder.orderItemLabel.setText(orderItem.getProduct().getLabel());
        holder.orderItemQuantity.setText("Quantity : " + orderItem.getQuantity());
        holder.orderItemTotal.setText("$" + orderItem.getQuantity() * orderItem.getProduct().getPrice());
        if (refundable)
            holder.refundButton.setOnClickListener(v -> refund(orderItem, position));
        else {
            holder.refundButton.setAlpha(0.5f);
            holder.refundButton.setClickable(false);
        }
        holder.relativeLayout.setOnClickListener(v -> showProduct(orderItem.getProduct()));
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
