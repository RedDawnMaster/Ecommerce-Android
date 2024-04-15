package com.example.ecommerceapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.activities.MainActivity;
import com.example.ecommerceapp.adapters.ProductAdapter;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.services.UserService;

import java.util.ArrayList;
import java.util.List;

public class ProductListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private TextView noProducts;
    private Button addProduct;
    private Button addCategory;
    private List<Product> products;

    public ProductListFragment() {
        // Required empty public constructor
    }

    public ProductListFragment(List<Product> products) {
        if (products == null) this.products = new ArrayList<>();
        else
            this.products = products;
    }

    private void initComponents(View view) {
        noProducts = view.findViewById(R.id.no_products);
        addProduct = view.findViewById(R.id.add_product);
        addCategory = view.findViewById(R.id.add_category);
    }

    private void bindData() {
        if (products.isEmpty()) noProducts.setVisibility(View.VISIBLE);
        if (UserService.getInstance().getUser() != null && UserService.getInstance().getUser().getRole().equals("ADMIN")) {
            MainActivity mainActivity = (MainActivity) getContext();
            addProduct.setVisibility(View.VISIBLE);
            addCategory.setVisibility(View.VISIBLE);
            addProduct.setOnClickListener(v -> {
                mainActivity.replaceFragment(new AdminProductFragment(productAdapter), "AdminProduct", true);
            });
            addCategory.setOnClickListener(v -> {
                mainActivity.replaceFragment(new CategoryFragment(), "Category", true);
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        initComponents(view);
        recyclerView = view.findViewById(R.id.products_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        productAdapter = new ProductAdapter(getContext(), products);
        recyclerView.setAdapter(productAdapter);
        bindData();
        return view;
    }

    public ProductAdapter getProductAdapter() {
        return productAdapter;
    }
}