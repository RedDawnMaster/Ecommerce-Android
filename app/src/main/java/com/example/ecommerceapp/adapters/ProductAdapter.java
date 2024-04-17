package com.example.ecommerceapp.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.activities.MainActivity;
import com.example.ecommerceapp.fragments.AdminProductFragment;
import com.example.ecommerceapp.fragments.ProductFragment;
import com.example.ecommerceapp.fragments.ProductListFragment;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.models.Review;
import com.example.ecommerceapp.services.ProductService;
import com.example.ecommerceapp.services.ReviewService;
import com.example.ecommerceapp.services.UserService;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

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
                    mainActivity.replaceFragment(new AdminProductFragment(product, reviews, this), "Product", true);
                else
                    mainActivity.replaceFragment(new ProductFragment(product, reviews, ProductAdapter.this), "Product", true);
            });
        });
        thread.start();
    }

    private void displayImage(ProductViewHolder holder, Product product) {
        File localFile = ProductService.getInstance().getLocalFiles().get(product.getLabel());
        if (localFile == null || localFile.length() == 0) {
            holder.productImage.setImageResource(R.drawable.error_loading_image);
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
            holder.productImage.setImageBitmap(bitmap);
        }
    }

    private void deleteProduct(Product product) {
        MainActivity mainActivity = (MainActivity) context;
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to delete this product?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Thread thread = new Thread(() -> {
                ProductService.getInstance().deleteByLabel(product.getLabel());
                List<Product> allProducts = ProductService.getInstance().getProducts();
                allProducts.remove(product);
                mainActivity.runOnUiThread(() -> {
                    deleteImageFirebase(product.getLabel());
                    Fragment fragmentProducts = mainActivity.getSupportFragmentManager().findFragmentByTag("Products");
                    if (fragmentProducts != null) {
                        ((ProductListFragment) fragmentProducts).getProductAdapter().getProducts().remove(product);
                        ((ProductListFragment) fragmentProducts).getProductAdapter().notifyDataSetChanged();
                    }
                    Fragment fragmentCategory = mainActivity.getSupportFragmentManager().findFragmentByTag(product.getCategory().getLabel());
                    if (fragmentCategory != null) {
                        ((ProductListFragment) fragmentCategory).getProductAdapter().getProducts().remove(product);
                        ((ProductListFragment) fragmentCategory).getProductAdapter().notifyDataSetChanged();
                    }
                    Fragment fragmentOrderItems = mainActivity.getSupportFragmentManager().findFragmentByTag("OrderItems");
                    if (fragmentOrderItems != null) {
                        FragmentTransaction fragmentTransaction = mainActivity.getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.remove(fragmentOrderItems);
                        fragmentTransaction.commit();
                    }
                    Fragment fragmentCart = mainActivity.getSupportFragmentManager().findFragmentByTag("Cart");
                    if (fragmentCart != null) {
                        FragmentTransaction fragmentTransaction = mainActivity.getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.remove(fragmentCart);
                        fragmentTransaction.commit();
                    }
                    Fragment fragmentProduct = mainActivity.getSupportFragmentManager().findFragmentByTag("Product");
                    if (fragmentProduct != null) {
                        FragmentTransaction fragmentTransaction = mainActivity.getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.remove(fragmentProduct);
                        fragmentTransaction.commit();
                    }
                    StyleableToast.makeText(mainActivity, "Product deleted", R.style.Success).show();
                });
            });
            thread.start();
            dialog.dismiss();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteImageFirebase(String label) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Processing...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + label);
        storageReference.delete().addOnSuccessListener(unused -> {
            if (progressDialog.isShowing()) progressDialog.dismiss();
        }).addOnFailureListener(e -> {
            if (progressDialog.isShowing()) progressDialog.dismiss();
        });
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
        displayImage(holder, product);
        holder.productLabel.setText(product.getLabel());
        holder.ratingBar.setRating((float) product.getStars());
        holder.productEvaluation.setText("(" + product.getEvaluationCount() + ")");
        holder.productDescription.setText(product.getDescription());
        holder.productPrice.setText("$" + product.getPrice());
        holder.productOrders.setText("Orders : " + product.getNumberOfOrders());
        holder.productLayout.setOnClickListener(v -> {
            showProduct(product);
        });
        if (UserService.getInstance().getUser() != null && UserService.getInstance().getUser().getRole().equals("ADMIN")) {
            holder.deleteProduct.setVisibility(View.VISIBLE);
            holder.deleteProduct.setOnClickListener(v -> deleteProduct(product));
        }
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
