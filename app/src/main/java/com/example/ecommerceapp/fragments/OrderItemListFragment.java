package com.example.ecommerceapp.fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.activities.MainActivity;
import com.example.ecommerceapp.adapters.OrderAdapter;
import com.example.ecommerceapp.adapters.OrderItemAdapter;
import com.example.ecommerceapp.models.Order;
import com.example.ecommerceapp.models.OrderItem;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.services.OrderService;
import com.example.ecommerceapp.services.ProductService;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderItemListFragment extends Fragment {

    private Order order;
    private RecyclerView recyclerView;
    private FrameLayout orderItemsList;
    private OrderItemAdapter orderItemAdapter;
    private TextView noOrderItems;
    private OrderAdapter orderAdapter;
    private List<OrderItem> orderItems;

    private int count;

    public OrderItemListFragment() {
        // Required empty public constructor
    }

    public OrderItemListFragment(Order order, List<OrderItem> orderItems, OrderAdapter orderAdapter) {
        this.order = order;
        if (orderItems == null) this.orderItems = new ArrayList<>();
        else {
            this.orderItems = orderItems;
        }
        this.orderAdapter = orderAdapter;
    }

    private List<Product> constructProduct() {
        List<Product> products = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            products.add(orderItem.getProduct());
        }
        return products;
    }

    private void downloadImagesFirebase() {
        MainActivity mainActivity = (MainActivity) getContext();
        count = 0;
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading products...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        List<Product> products = constructProduct();
        Map<String, File> localFiles = ProductService.getInstance().getLocalFiles();
        if (products.isEmpty() && progressDialog.isShowing()) {
            progressDialog.dismiss();
            orderItemsList.setVisibility(View.VISIBLE);
        } else {
            for (Product product : products) {
                if (localFiles.get(product.getLabel()) != null && localFiles.get(product.getLabel()).length() != 0) {
                    count++;
                    if (count == products.size() && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        orderItemsList.setVisibility(View.VISIBLE);
                    }
                    continue;
                }
                StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + product.getLabel());
                try {
                    File localFile = File.createTempFile(product.getLabel(), ".jpeg");
                    storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                        count++;
                        localFiles.put(product.getLabel(), localFile);
                        if (count == products.size() && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            orderItemsList.setVisibility(View.VISIBLE);
                        }
                        Fragment fragmentProducts = mainActivity.getSupportFragmentManager().findFragmentByTag("Products");
                        if (fragmentProducts != null) {
                            ((ProductListFragment) fragmentProducts).getProductAdapter().notifyDataSetChanged();
                        }
                        Fragment fragmentCategory = mainActivity.getSupportFragmentManager().findFragmentByTag(product.getCategory().getLabel());
                        if (fragmentCategory != null) {
                            ((ProductListFragment) fragmentCategory).getProductAdapter().notifyDataSetChanged();
                        }
                        Fragment fragmentOrderItems = mainActivity.getSupportFragmentManager().findFragmentByTag("OrderItems");
                        if (fragmentOrderItems != null) {
                            ((OrderItemListFragment) fragmentOrderItems).getOrderItemAdapter().notifyDataSetChanged();
                        }
                        Fragment fragmentCart = mainActivity.getSupportFragmentManager().findFragmentByTag("Cart");
                        if (fragmentCart != null) {
                            ((CartItemListFragment) fragmentCart).getCartItemAdapter().notifyDataSetChanged();
                        }
                        Fragment fragmentProduct = mainActivity.getSupportFragmentManager().findFragmentByTag("Product");
                        if (fragmentProduct != null) {
                            if (fragmentProduct instanceof AdminProductFragment && ((AdminProductFragment) fragmentCart).getProduct() != null && ((AdminProductFragment) fragmentCart).getProduct().getLabel().equalsIgnoreCase(product.getLabel())) {
                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                ((AdminProductFragment) fragmentCart).getProductImageAdmin().setImageBitmap(bitmap);
                            } else if (((ProductFragment) fragmentCart).getProductLabel().getText().toString().equalsIgnoreCase(product.getLabel())) {
                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                ((ProductFragment) fragmentCart).getProductImage().setImageBitmap(bitmap);
                            }
                        }
                    }).addOnFailureListener(e -> {
                        count++;
                        if (count == products.size() && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            orderItemsList.setVisibility(View.VISIBLE);
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_item_list, container, false);
        orderItemsList = view.findViewById(R.id.order_items_list);
        noOrderItems = view.findViewById(R.id.no_order_items);
        recyclerView = view.findViewById(R.id.order_items_recycler);
        orderItemsList.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Thread thread = new Thread(() -> {
            boolean ref = OrderService.getInstance().checkRefundable(order.getId());
            orderItemAdapter = new OrderItemAdapter(getContext(), noOrderItems, ref, order, orderItems, orderAdapter);
            MainActivity mainActivity = (MainActivity) getContext();
            mainActivity.runOnUiThread(() -> recyclerView.setAdapter(orderItemAdapter));
        });
        thread.start();
        if (this.orderItems.isEmpty()) {
            orderItemsList.setVisibility(View.VISIBLE);
            noOrderItems.setVisibility(View.VISIBLE);
        } else downloadImagesFirebase();
        return view;
    }

    public OrderItemAdapter getOrderItemAdapter() {
        return orderItemAdapter;
    }
}