package com.example.ecommerceapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.ecommerceapp.services.OrderService;

import java.util.ArrayList;
import java.util.List;

public class OrderItemListFragment extends Fragment {

    private Order order;
    private RecyclerView recyclerView;
    private OrderItemAdapter orderItemAdapter;
    private TextView noOrderItems;
    private OrderAdapter orderAdapter;
    private List<OrderItem> orderItems;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_item_list, container, false);
        noOrderItems = view.findViewById(R.id.no_order_items);
        recyclerView = view.findViewById(R.id.order_items_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Thread thread = new Thread(() -> {
            boolean ref = OrderService.getInstance().checkRefundable(order.getId());
            orderItemAdapter = new OrderItemAdapter(getContext(), noOrderItems, ref, order, orderItems, orderAdapter);
            MainActivity mainActivity = (MainActivity) getContext();
            mainActivity.runOnUiThread(() -> recyclerView.setAdapter(orderItemAdapter));
        });
        thread.start();
        if (this.orderItems.isEmpty()) noOrderItems.setVisibility(View.VISIBLE);
        return view;
    }

    public OrderItemAdapter getOrderItemAdapter() {
        return orderItemAdapter;
    }
}