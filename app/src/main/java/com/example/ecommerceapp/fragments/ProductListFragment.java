package com.example.ecommerceapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.adapters.ProductAdapter;
import com.example.ecommerceapp.controllers.UserController;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.retrofit.RetrofitService;

import java.util.List;

public class ProductListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> products;

    public ProductListFragment() {
        // Required empty public constructor
    }

    public ProductListFragment(List<Product> products) {
        this.products = products;
        RetrofitService retrofitService = new RetrofitService();
        UserController userController = retrofitService.getRetrofit().create(UserController.class);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        recyclerView = view.findViewById(R.id.products_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        productAdapter = new ProductAdapter(products);
        recyclerView.setAdapter(productAdapter);
        return view;
    }
}