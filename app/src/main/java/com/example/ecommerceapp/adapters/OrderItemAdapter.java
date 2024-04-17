package com.example.ecommerceapp.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.activities.MainActivity;
import com.example.ecommerceapp.fragments.AdminProductFragment;
import com.example.ecommerceapp.fragments.ProductFragment;
import com.example.ecommerceapp.fragments.ProductListFragment;
import com.example.ecommerceapp.models.Order;
import com.example.ecommerceapp.models.OrderItem;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.models.Review;
import com.example.ecommerceapp.services.OrderItemService;
import com.example.ecommerceapp.services.ProductService;
import com.example.ecommerceapp.services.ReviewService;
import com.example.ecommerceapp.services.UserService;

import java.io.File;
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
        total += order.getDeletedProductsTotal();
        order.setTotal(total);
    }

    private Order findOrder(Order order) {
        return orderAdapter.getOrders().stream().filter(o -> o.getReference().equals(order.getReference())).collect(Collectors.toList()).get(0);
    }

    private Product findProduct(OrderItem orderItem) {
        return ProductService.getInstance().getProducts().stream().filter(p -> p.getLabel().equals(orderItem.getProduct().getLabel())).collect(Collectors.toList()).get(0);
    }

    private void refund(OrderItem orderItem, int position) {
        MainActivity mainActivity = (MainActivity) context;
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to refund this product?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Thread thread = new Thread(() -> {
                OrderItemService.getInstance().refund(orderItem.getProduct().getLabel(), order.getReference());
                order.getOrderItems().remove(orderItem);
                if (!orderItem.getProduct().isDeleted()) {
                    Product product = findProduct(orderItem);
                    product.setNumberOfOrders(product.getNumberOfOrders() - orderItem.getQuantity());
                }
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
                    Fragment fragment = mainActivity.getSupportFragmentManager().findFragmentByTag(orderItem.getProduct().getCategory().getLabel());
                    if (fragment != null) {
                        ((ProductListFragment) fragment).getProductAdapter().notifyDataSetChanged();
                    }
                    StyleableToast.makeText(context, "Product refunded", R.style.Success).show();
                });
            });
            thread.start();
            dialog.dismiss();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showProduct(Product product) {
        MainActivity mainActivity = (MainActivity) context;
        Thread thread = new Thread(() -> {
            List<Review> reviews = ReviewService.getInstance().findByProductLabel(product.getLabel());
            if (UserService.getInstance().getUser().getRole().equals("ADMIN"))
                mainActivity.runOnUiThread(() -> mainActivity.replaceFragment(new AdminProductFragment(product, reviews, null), "Product", true));
            else
                mainActivity.runOnUiThread(() -> mainActivity.replaceFragment(new ProductFragment(product, reviews), "Product", true));
        });
        thread.start();
    }

    private void displayImage(OrderItemViewHolder holder, Product product) {
        File localFile = ProductService.getInstance().getLocalFiles().get(product.getLabel());
        if (localFile == null || localFile.length() == 0) {
            holder.orderItemImage.setImageResource(R.drawable.error_loading_image);
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
            holder.orderItemImage.setImageBitmap(bitmap);
        }
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
        displayImage(holder, orderItem.getProduct());
        if (orderItem.getProduct().isDeleted())
            holder.orderItemLabel.setText(orderItem.getProduct().getLabel() + " (DELETED)");
        else
            holder.orderItemLabel.setText(orderItem.getProduct().getLabel());
        holder.orderItemQuantity.setText("Quantity : " + orderItem.getQuantity());
        holder.orderItemTotal.setText("$" + orderItem.getQuantity() * orderItem.getProduct().getPrice());
        if (UserService.getInstance().getUser().getRole().equals("ADMIN"))
            holder.refundButton.setVisibility(View.GONE);
        else if (refundable)
            holder.refundButton.setOnClickListener(v -> refund(orderItem, position));
        else {
            holder.refundButton.setAlpha(0.5f);
            holder.refundButton.setClickable(false);
        }
        if (!orderItem.getProduct().isDeleted())
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
