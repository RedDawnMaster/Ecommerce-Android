package com.example.ecommerceapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.activities.CheckoutActivity;
import com.example.ecommerceapp.activities.MainActivity;
import com.example.ecommerceapp.adapters.CartItemAdapter;
import com.example.ecommerceapp.models.CartItem;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.services.CartService;
import com.example.ecommerceapp.services.ProductService;
import com.example.ecommerceapp.services.UserService;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CartItemListFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartItemAdapter cartItemAdapter;
    private FrameLayout cartItemsList;
    private TextView noCartItems;
    private Button checkoutButton;
    private List<CartItem> cartItems;

    private int count;


    public CartItemListFragment() {
        // Required empty public constructor
    }

    public CartItemListFragment(List<CartItem> cartItems) {
        if (cartItems == null) this.cartItems = new ArrayList<>();
        else
            this.cartItems = cartItems;
    }

    private double calculateTotal() {
        double total = 0;
        for (CartItem cartItem : cartItems) {
            total += cartItem.getQuantity() * cartItem.getProduct().getPrice();
        }
        return total;
    }

    private void checkout() {
        MainActivity mainActivity = (MainActivity) getContext();
        Intent intent = new Intent(mainActivity, CheckoutActivity.class);
        double total = calculateTotal();
        int size = cartItems.size();
        String username = UserService.getInstance().getUser().getUsername();
        intent.putExtra("total", total);
        intent.putExtra("size", size);
        intent.putExtra("username", username);
        List<Product> products = ProductService.getInstance().getBoughProducts();
        List<Product> allProducts = ProductService.getInstance().getProducts();
        List<Integer> quantities = ProductService.getInstance().getQuantities();
        for (CartItem cartItem : CartService.getInstance().getCart().getCartItems()) {
            products.add(allProducts.stream().filter(pro -> pro.getLabel().equals(cartItem.getProduct().getLabel())).collect(Collectors.toList()).get(0));
            quantities.add(cartItem.getQuantity());
        }
        mainActivity.setRequestCode(3);
        mainActivity.getActivityResultLauncher().launch(intent);
    }

    public void disableCheckout() {
        checkoutButton.setAlpha(0.5f);
        checkoutButton.setClickable(false);
    }

    private List<Product> constructProduct() {
        List<Product> products = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            products.add(cartItem.getProduct());
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
            cartItemsList.setVisibility(View.VISIBLE);
        } else {
            for (Product product : products) {
                if (localFiles.get(product.getLabel()) != null && localFiles.get(product.getLabel()).length() != 0) {
                    count++;
                    if (count == products.size() && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        cartItemsList.setVisibility(View.VISIBLE);
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
                            cartItemsList.setVisibility(View.VISIBLE);
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
                            cartItemsList.setVisibility(View.VISIBLE);
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        cartItemsList = view.findViewById(R.id.cart_items_list);
        noCartItems = view.findViewById(R.id.no_cart_items);
        checkoutButton = view.findViewById(R.id.checkout_button);
        recyclerView = view.findViewById(R.id.cart_items_recycler);
        cartItemsList.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        cartItemAdapter = new CartItemAdapter(getContext(), noCartItems, this, cartItems);
        recyclerView.setAdapter(cartItemAdapter);
        if (UserService.getInstance().getUser().getRole().equals("ADMIN"))
            checkoutButton.setVisibility(View.GONE);
        else checkoutButton.setOnClickListener(v -> checkout());
        if (this.cartItems.isEmpty()) {
            cartItemsList.setVisibility(View.VISIBLE);
            noCartItems.setVisibility(View.VISIBLE);
            disableCheckout();
        } else {
            downloadImagesFirebase();
        }

        return view;
    }

    public CartItemAdapter getCartItemAdapter() {
        return cartItemAdapter;
    }
}