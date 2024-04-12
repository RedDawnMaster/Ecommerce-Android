package com.example.ecommerceapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.activities.MainActivity;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.models.Review;
import com.example.ecommerceapp.models.Statistic;
import com.example.ecommerceapp.services.ProductService;
import com.example.ecommerceapp.services.ReviewService;
import com.example.ecommerceapp.services.StatisticService;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView deliveryDays;
    private TextView refundDays;

    private FrameLayout bestSeller1;
    private ImageView bestSellerImage1;
    private TextView bestSellerLabel1;
    private TextView bestSellerPrice1;

    private FrameLayout bestSeller2;
    private ImageView bestSellerImage2;
    private TextView bestSellerLabel2;
    private TextView bestSellerPrice2;

    private FrameLayout bestSeller3;
    private ImageView bestSellerImage3;
    private TextView bestSellerLabel3;
    private TextView bestSellerPrice3;

    private FrameLayout bestSeller4;
    private ImageView bestSellerImage4;
    private TextView bestSellerLabel4;
    private TextView bestSellerPrice4;

    private FrameLayout mostReviewed1;
    private ImageView mostReviewedImage1;
    private TextView mostReviewedLabel1;
    private TextView mostReviewedPrice1;

    private FrameLayout mostReviewed2;
    private ImageView mostReviewedImage2;
    private TextView mostReviewedLabel2;
    private TextView mostReviewedPrice2;

    private List<FrameLayout> bestSellers = new ArrayList<>();
    private List<ImageView> bestSellerImages = new ArrayList<>();
    private List<TextView> bestSellerLabels = new ArrayList<>();
    private List<TextView> bestSellerPrices = new ArrayList<>();

    private List<FrameLayout> mostReviews = new ArrayList<>();
    private List<ImageView> mostReviewedImages = new ArrayList<>();
    private List<TextView> mostReviewedLabels = new ArrayList<>();
    private List<TextView> mostReviewedPrices = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    private void initComponents(View view) {
        deliveryDays = view.findViewById(R.id.delivery_days);
        refundDays = view.findViewById(R.id.refund_days);

        bestSeller1 = view.findViewById(R.id.best_seller_1);
        bestSellerImage1 = view.findViewById(R.id.best_seller_image_1);
        bestSellerLabel1 = view.findViewById(R.id.best_seller_label_1);
        bestSellerPrice1 = view.findViewById(R.id.best_seller_price_1);

        bestSeller2 = view.findViewById(R.id.best_seller_2);
        bestSellerImage2 = view.findViewById(R.id.best_seller_image_2);
        bestSellerLabel2 = view.findViewById(R.id.best_seller_label_2);
        bestSellerPrice2 = view.findViewById(R.id.best_seller_price_2);

        bestSeller3 = view.findViewById(R.id.best_seller_3);
        bestSellerImage3 = view.findViewById(R.id.best_seller_image_3);
        bestSellerLabel3 = view.findViewById(R.id.best_seller_label_3);
        bestSellerPrice3 = view.findViewById(R.id.best_seller_price_3);

        bestSeller4 = view.findViewById(R.id.best_seller_4);
        bestSellerImage4 = view.findViewById(R.id.best_seller_image_4);
        bestSellerLabel4 = view.findViewById(R.id.best_seller_label_4);
        bestSellerPrice4 = view.findViewById(R.id.best_seller_price_4);

        mostReviewed1 = view.findViewById(R.id.most_reviewed_1);
        mostReviewedImage1 = view.findViewById(R.id.most_reviewed_image_1);
        mostReviewedLabel1 = view.findViewById(R.id.most_reviewed_label_1);
        mostReviewedPrice1 = view.findViewById(R.id.most_reviewed_price_1);

        mostReviewed2 = view.findViewById(R.id.most_reviewed_2);
        mostReviewedImage2 = view.findViewById(R.id.most_reviewed_image_2);
        mostReviewedLabel2 = view.findViewById(R.id.most_reviewed_label_2);
        mostReviewedPrice2 = view.findViewById(R.id.most_reviewed_price_2);

        bestSellers.add(bestSeller1);
        bestSellers.add(bestSeller2);
        bestSellers.add(bestSeller3);
        bestSellers.add(bestSeller4);

        mostReviews.add(mostReviewed1);
        mostReviews.add(mostReviewed2);

        bestSellerImages.add(bestSellerImage1);
        bestSellerImages.add(bestSellerImage2);
        bestSellerImages.add(bestSellerImage3);
        bestSellerImages.add(bestSellerImage4);

        bestSellerLabels.add(bestSellerLabel1);
        bestSellerLabels.add(bestSellerLabel2);
        bestSellerLabels.add(bestSellerLabel3);
        bestSellerLabels.add(bestSellerLabel4);

        bestSellerPrices.add(bestSellerPrice1);
        bestSellerPrices.add(bestSellerPrice2);
        bestSellerPrices.add(bestSellerPrice3);
        bestSellerPrices.add(bestSellerPrice4);

        mostReviewedImages.add(mostReviewedImage1);
        mostReviewedImages.add(mostReviewedImage2);

        mostReviewedLabels.add(mostReviewedLabel1);
        mostReviewedLabels.add(mostReviewedLabel2);

        mostReviewedPrices.add(mostReviewedPrice1);
        mostReviewedPrices.add(mostReviewedPrice2);
    }

    @SuppressLint("SetTextI18n")
    private void bindData() {
        Statistic statistic = StatisticService.getInstance().getStatistic();
        deliveryDays.setText(statistic.getDeliveryTime() + " Days Delivery");
        refundDays.setText(statistic.getRefundPeriod() + " Days Refund");
        List<Product> bestSellerProducts = ProductService.getInstance().getBestSellerProducts();
        List<Product> mostReviewedProducts = ProductService.getInstance().getMostReviewedProducts();
        int i;
        for (i = 0; i < bestSellerProducts.size(); i++) {
            Product product = bestSellerProducts.get(i);
            bestSellerImages.get(i).setImageResource(R.drawable.avatar_1);
            bestSellerLabels.get(i).setText(product.getLabel());
            bestSellerPrices.get(i).setText("$" + product.getPrice());
            bestSellers.get(i).setOnClickListener(v -> {
                showProduct(product);
            });
        }

        for (int j = 3; j >= i; j--) {
            bestSellers.get(j).setVisibility(View.GONE);
        }

        for (i = 0; i < mostReviewedProducts.size(); i++) {
            Product product = mostReviewedProducts.get(i);
            mostReviewedImages.get(i).setImageResource(R.drawable.avatar_1);
            mostReviewedLabels.get(i).setText(product.getLabel());
            mostReviewedPrices.get(i).setText("$" + product.getPrice());
            mostReviews.get(i).setOnClickListener(v -> showProduct(product));
        }

        for (int j = 1; j >= i; j--) {
            mostReviews.get(j).setVisibility(View.GONE);
        }

    }

    private void showProduct(Product product) {
        MainActivity mainActivity = (MainActivity) getContext();
        Thread thread = new Thread(() -> {
            List<Review> reviews = ReviewService.getInstance().findByProductLabel(product.getLabel());
            mainActivity.runOnUiThread(() -> mainActivity.replaceFragment(new ProductFragment(product, reviews), "Product", true));
        });
        thread.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initComponents(view);
        bindData();
        return view;
    }
}