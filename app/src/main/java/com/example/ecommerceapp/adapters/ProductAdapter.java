package com.example.ecommerceapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.activities.MainActivity;
import com.example.ecommerceapp.fragments.AdminProductFragment;
import com.example.ecommerceapp.fragments.ProductFragment;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.models.Review;
import com.example.ecommerceapp.services.ReviewService;
import com.example.ecommerceapp.services.UserService;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    private Context context;
    private List<Product> products;

    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    private void showProduct(Product product) {
        MainActivity mainActivity = (MainActivity) context;
        Thread thread = new Thread(() -> {
            List<Review> reviews = ReviewService.getInstance().findByProductLabel(product.getLabel());
            mainActivity.runOnUiThread(() -> {
                if (UserService.getInstance().getUser() != null && UserService.getInstance().getUser().getRole().equals("ADMIN"))
                    mainActivity.replaceFragment(new AdminProductFragment(product, reviews, this), "AdminProduct", true);
                else
                    mainActivity.replaceFragment(new ProductFragment(product, reviews, ProductAdapter.this), "Product", true);
            });
        });
        thread.start();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.productImage.setImageResource(R.drawable.avatar_1);
        holder.productLabel.setText(product.getLabel());
        holder.ratingBar.setRating((float) product.getStars());
        holder.productEvaluation.setText("(" + product.getEvaluationCount() + ")");
        holder.productDescription.setText(product.getDescription());
        holder.productPrice.setText("$" + product.getPrice());
        holder.productOrders.setText("Orders : " + product.getNumberOfOrders());
        holder.productLayout.setOnClickListener(v -> {
            showProduct(product);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
