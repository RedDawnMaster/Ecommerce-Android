package com.example.ecommerceapp.fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.activities.MainActivity;
import com.example.ecommerceapp.adapters.ProductAdapter;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.services.ProductService;
import com.example.ecommerceapp.services.UserService;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private RelativeLayout productsList;
    private TextView noProducts;
    private Button addProduct;
    private Button addCategory;
    private List<Product> products;
    private int count;

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
        productsList = view.findViewById(R.id.products_list);
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

    private void downloadImagesFirebase() {
        MainActivity mainActivity = (MainActivity) getContext();
        count = 0;
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading products...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Map<String, File> localFiles = ProductService.getInstance().getLocalFiles();
        if (products.isEmpty() && progressDialog.isShowing()) {
            progressDialog.dismiss();
            productsList.setVisibility(View.VISIBLE);
        } else {
            for (Product product : products) {
                if (localFiles.get(product.getLabel()) != null && localFiles.get(product.getLabel()).length() != 0) {
                    count++;
                    if (count == products.size() && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        productsList.setVisibility(View.VISIBLE);
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
                            productsList.setVisibility(View.VISIBLE);
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
                            if (fragmentProduct instanceof AdminProductFragment && ((AdminProductFragment) fragmentProduct).getProduct() != null && ((AdminProductFragment) fragmentProduct).getProduct().getLabel().equalsIgnoreCase(product.getLabel())) {
                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                ((AdminProductFragment) fragmentProduct).getProductImageAdmin().setImageBitmap(bitmap);
                            } else if (((ProductFragment) fragmentProduct).getProductLabel().getText().toString().equalsIgnoreCase(product.getLabel())) {
                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                ((ProductFragment) fragmentProduct).getProductImage().setImageBitmap(bitmap);
                            }
                        }
                    }).addOnFailureListener(e -> {
                        count++;
                        if (count == products.size() && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            productsList.setVisibility(View.VISIBLE);
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
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        initComponents(view);
        productsList.setVisibility(View.GONE);
        recyclerView = view.findViewById(R.id.products_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        productAdapter = new ProductAdapter(getContext(), products);
        recyclerView.setAdapter(productAdapter);
        bindData();
        if (!this.products.isEmpty()) downloadImagesFirebase();
        else productsList.setVisibility(View.VISIBLE);
        return view;
    }

    public ProductAdapter getProductAdapter() {
        return productAdapter;
    }
}