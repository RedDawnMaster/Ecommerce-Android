package com.example.ecommerceapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.activities.MainActivity;
import com.example.ecommerceapp.fragments.CartItemListFragment;
import com.example.ecommerceapp.fragments.ProductFragment;
import com.example.ecommerceapp.models.CartItem;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.models.Review;
import com.example.ecommerceapp.services.CartItemService;
import com.example.ecommerceapp.services.CartService;
import com.example.ecommerceapp.services.ReviewService;
import com.example.ecommerceapp.services.UserService;

import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemViewHolder> {
    private Context context;
    private TextView noCartItems;
    private CartItemListFragment cartItemListFragment;
    private List<CartItem> cartItems;

    public CartItemAdapter(Context context, TextView noCartItems, CartItemListFragment cartItemListFragment, List<CartItem> cartItems) {
        this.context = context;
        this.noCartItems = noCartItems;
        this.cartItemListFragment = cartItemListFragment;
        this.cartItems = cartItems;
    }

    private void removeCartItem(CartItem cartItem, int postion) {
        {
            Thread thread = new Thread(() -> {
                CartItemService.getInstance().deleteByProductLabelAndCartUserUsername(cartItem.getProduct().getLabel(), UserService.getInstance().getUser().getUsername());
                CartService.getInstance().getCart().getCartItems().remove(cartItem);
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.runOnUiThread(() -> {
                    this.notifyItemRemoved(postion);
                    if (getItemCount() == 0) {
                        noCartItems.setVisibility(View.VISIBLE);
                        cartItemListFragment.disableCheckout();
                    }

                    StyleableToast.makeText(context, "Product removed", R.style.Success).show();
                });
            });
            thread.start();
        }
    }

    private void showProduct(Product product) {
        MainActivity mainActivity = (MainActivity) context;
        Thread thread = new Thread(() -> {
            List<Review> reviews = ReviewService.getInstance().findByProductLabel(product.getLabel());
            mainActivity.runOnUiThread(() -> mainActivity.replaceFragment(new ProductFragment(product, reviews), "Product", true));
        });
        thread.start();
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.cartItemImage.setImageResource(R.drawable.avatar_1);
        holder.orderItemLabel.setText(cartItem.getProduct().getLabel());
        holder.orderItemQuantity.setText("Quantity : " + cartItem.getQuantity());
        holder.orderItemTotal.setText("$" + cartItem.getQuantity() * cartItem.getProduct().getPrice());
        holder.removeCartItemButton.setOnClickListener(v -> {
            removeCartItem(cartItem, position);
        });
        holder.relativeLayout.setOnClickListener(v -> showProduct(cartItem.getProduct()));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }
}
