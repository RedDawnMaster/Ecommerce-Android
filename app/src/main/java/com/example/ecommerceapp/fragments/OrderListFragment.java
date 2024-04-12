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
import com.example.ecommerceapp.adapters.OrderAdapter;
import com.example.ecommerceapp.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderListFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private TextView noOrders;
    private List<Order> orders;

    public OrderListFragment() {
    }

    public OrderListFragment(List<Order> orders) {
        if (orders == null) this.orders = new ArrayList<>();
        else
            this.orders = orders;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        noOrders = view.findViewById(R.id.no_orders);
        recyclerView = view.findViewById(R.id.orders_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        orderAdapter = new OrderAdapter(getContext(), orders);
        recyclerView.setAdapter(orderAdapter);
        if (this.orders.isEmpty())
            noOrders.setVisibility(View.VISIBLE);
        return view;
    }

    public OrderAdapter getOrderAdapter() {
        return orderAdapter;
    }
}