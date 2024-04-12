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
import com.example.ecommerceapp.adapters.ProductAdapter;
import com.example.ecommerceapp.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private TextView noProducts;
    private List<Product> products;

    public ProductListFragment() {
        // Required empty public constructor
    }

    public ProductListFragment(List<Product> products) {
        if (products == null) this.products = new ArrayList<>();
        else
            this.products = products;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        noProducts = view.findViewById(R.id.no_products);
        recyclerView = view.findViewById(R.id.products_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        productAdapter = new ProductAdapter(getContext(), products);
        recyclerView.setAdapter(productAdapter);
        if (products.isEmpty()) noProducts.setVisibility(View.VISIBLE);
        return view;
    }

    public ProductAdapter getProductAdapter() {
        return productAdapter;
    }
}