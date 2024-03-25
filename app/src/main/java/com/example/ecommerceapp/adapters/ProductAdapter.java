package com.example.ecommerceapp.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {

    private List<Product> products;

    public ProductAdapter(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.productImage.setImageResource(R.drawable.avatar_1);
        holder.productLabel.setText(products.get(position).getLabel());
        holder.ratingBar.setRating((float) products.get(position).getStars());
        holder.productEvaluation.setText("(" + products.get(position).getEvaluationCount() + ")");
        holder.productDescription.setText(products.get(position).getDescription());
        holder.productPrice.setText("$" + products.get(position).getPrice());
        holder.productOrders.setText("Orders : " + products.get(position).getNumberOfOrders());

    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
