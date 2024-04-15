package com.example.ecommerceapp.adapters;

import android.view.View;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;

public class ReviewViewHolder extends RecyclerView.ViewHolder {
    TextView reviewFullname;
    RatingBar reviewRating;
    TextView reviewComment;
    ImageButton removeReviewButton;

    public ReviewViewHolder(@NonNull View itemView) {
        super(itemView);
        reviewFullname = itemView.findViewById(R.id.review_fullname);
        reviewRating = itemView.findViewById(R.id.review_ratingBar);
        reviewComment = itemView.findViewById(R.id.review_comment);
        removeReviewButton = itemView.findViewById(R.id.remove_review);
    }
}
