package com.example.ecommerceapp.fragments;

import android.content.Intent;
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
import com.example.ecommerceapp.activities.CheckoutActivity;
import com.example.ecommerceapp.activities.MainActivity;
import com.example.ecommerceapp.adapters.CartItemAdapter;
import com.example.ecommerceapp.models.CartItem;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.services.CartService;
import com.example.ecommerceapp.services.ProductService;
import com.example.ecommerceapp.services.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CartItemListFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartItemAdapter cartItemAdapter;
    private TextView noCartItems;
    private Button checkoutButton;
    private List<CartItem> cartItems;


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        noCartItems = view.findViewById(R.id.no_cart_items);
        checkoutButton = view.findViewById(R.id.checkout_button);
        recyclerView = view.findViewById(R.id.cart_items_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        cartItemAdapter = new CartItemAdapter(getContext(), noCartItems, this, cartItems);
        recyclerView.setAdapter(cartItemAdapter);
        if (this.cartItems.isEmpty()) {
            noCartItems.setVisibility(View.VISIBLE);
            disableCheckout();
        } else
            checkoutButton.setOnClickListener(v -> checkout());
        return view;
    }

    public CartItemAdapter getCartItemAdapter() {
        return cartItemAdapter;
    }
}