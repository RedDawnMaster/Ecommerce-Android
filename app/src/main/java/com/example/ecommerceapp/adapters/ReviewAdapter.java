package com.example.ecommerceapp.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.activities.MainActivity;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.models.Review;
import com.example.ecommerceapp.services.ProductService;
import com.example.ecommerceapp.services.ReviewService;
import com.example.ecommerceapp.services.UserService;

import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewViewHolder> {
    private Context context;
    private TextView noReviewsAdmin;
    private Product product;
    private RatingBar productRatingBarAdmin;
    private TextView productEvaluationAdmin;
    private ProductAdapter productAdapter;
    private List<Review> reviews;

    public ReviewAdapter(Context context, TextView noReviewsAdmin, Product product, RatingBar productRatingBarAdmin, TextView productEvaluationAdmin, ProductAdapter productAdapter, List<Review> reviews) {
        this.context = context;
        this.noReviewsAdmin = noReviewsAdmin;
        this.product = product;
        this.productRatingBarAdmin = productRatingBarAdmin;
        this.productEvaluationAdmin = productEvaluationAdmin;
        this.productAdapter = productAdapter;
        this.reviews = reviews;
    }

    private void removeReview(Review review, int position) {
        MainActivity mainActivity = (MainActivity) context;
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to remove this review?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Thread thread = new Thread(() -> {
                ReviewService.getInstance().deleteByUserUsernameAndProductLabel(review.getUser().getUsername(), review.getProduct().getLabel());
                reviews.remove(review);
                mainActivity.runOnUiThread(() -> {
                    this.notifyItemRemoved(position);
                    if (getItemCount() == 0) {
                        noReviewsAdmin.setVisibility(View.VISIBLE);
                    }
                    StyleableToast.makeText(context, "Review removed", R.style.Success).show();
                    ProductService.getInstance().getProducts().stream().filter(pro -> pro.getId() == this.product.getId()).forEach(pro -> {
                        pro.setEvaluationCount(pro.getEvaluationCount() - 1);
                        float sum = 0;
                        for (Review reviewLoop : this.reviews) {
                            sum += reviewLoop.getStars();
                        }
                        sum /= pro.getEvaluationCount();
                        pro.setStars(sum);
                        mainActivity.runOnUiThread(() -> {
                            productRatingBarAdmin.setRating((float) pro.getStars());
                            productEvaluationAdmin.setText("(" + pro.getEvaluationCount() + ")");
                            if (productAdapter != null) productAdapter.notifyDataSetChanged();
                        });
                    });
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


    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReviewViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.reviewFullname.setText(review.getUser().getFirstName() + " " + review.getUser().getLastName());
        holder.reviewRating.setRating((float) review.getStars());
        holder.reviewComment.setText(review.getDescription());
        if (UserService.getInstance().getUser() != null && UserService.getInstance().getUser().getRole().equals("ADMIN")) {
            holder.removeReviewButton.setVisibility(View.VISIBLE);
            holder.removeReviewButton.setOnClickListener(v -> removeReview(review, position));
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public List<Review> getReviews() {
        return reviews;
    }
}
