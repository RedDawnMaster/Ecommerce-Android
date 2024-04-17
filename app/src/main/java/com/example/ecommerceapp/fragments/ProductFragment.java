package com.example.ecommerceapp.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.activities.AuthenticationActivity;
import com.example.ecommerceapp.activities.MainActivity;
import com.example.ecommerceapp.adapters.ProductAdapter;
import com.example.ecommerceapp.adapters.ReviewAdapter;
import com.example.ecommerceapp.models.Cart;
import com.example.ecommerceapp.models.CartItem;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.models.Review;
import com.example.ecommerceapp.services.CartItemService;
import com.example.ecommerceapp.services.CartService;
import com.example.ecommerceapp.services.ProductService;
import com.example.ecommerceapp.services.ReviewService;
import com.example.ecommerceapp.services.UserService;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import io.github.muddz.styleabletoast.StyleableToast;

public class ProductFragment extends Fragment {

    private Product product;
    private ProductAdapter productAdapter;
    private TextView noReviews;
    private List<Review> reviews;

    private ImageView productImage;
    private TextView productLabel;
    private RatingBar productRating;
    private TextView productEvaluation;
    private TextView productDescription;
    private TextView productOrders;
    private TextView productPrice;
    private Button addToCartButton;
    private EditText productQuantity;
    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;

    private EditText writeReview;
    private RatingBar rateProduct;
    private Button postReviewButton;
    private MainActivity mainActivity;

    public ProductFragment() {
        // Required empty public constructor
    }

    public ProductFragment(Product product, List<Review> reviews) {
        this.product = product;
        if (reviews == null) this.reviews = new ArrayList<>();
        else this.reviews = reviews;
    }

    public ProductFragment(Product product, List<Review> reviews, ProductAdapter productAdapter) {
        this.product = product;
        if (reviews == null) this.reviews = new ArrayList<>();
        else this.reviews = reviews;
        this.productAdapter = productAdapter;
    }

    private void initComponents(View view) {
        productImage = view.findViewById(R.id.product_image);
        productLabel = view.findViewById(R.id.product_label);
        productRating = view.findViewById(R.id.product_ratingBar);
        productEvaluation = view.findViewById(R.id.product_evaluation);
        productDescription = view.findViewById(R.id.product_description);
        productOrders = view.findViewById(R.id.product_orders);
        productPrice = view.findViewById(R.id.product_price);
        addToCartButton = view.findViewById(R.id.add_to_cart_button);
        productQuantity = view.findViewById(R.id.product_quantity);
        writeReview = view.findViewById(R.id.write_review);
        rateProduct = view.findViewById(R.id.rate_review);
        postReviewButton = view.findViewById(R.id.post_review);
        noReviews = view.findViewById(R.id.no_reviews);
        mainActivity = (MainActivity) getContext();
    }

    private CartItem findInCart(Cart cart, String label) {
        List<CartItem> cartItems = cart.getCartItems().stream().filter(cartItem -> cartItem.getProduct().getLabel().equals(label)).collect(Collectors.toList());
        if (cartItems.isEmpty()) return null;
        return cartItems.get(0);
    }

    private void addToCart() {
        MainActivity mainActivity = (MainActivity) getContext();
        if (UserService.getInstance().getUser() != null) {
            Thread thread = new Thread(() -> {
                Cart cart = CartService.getInstance().getCart();
                String quantityString = productQuantity.getText().toString();
                int quantity;
                if (!quantityString.isEmpty()) {
                    try {
                        int parsedQuantity = Integer.parseInt(quantityString);
                        if (parsedQuantity > 0) {
                            quantity = parsedQuantity;
                        } else {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException e) {
                        mainActivity.runOnUiThread(() -> StyleableToast.makeText(mainActivity, "Please enter a valid quantity", R.style.Failure).show());
                        return;
                    }
                } else {
                    mainActivity.runOnUiThread(() -> StyleableToast.makeText(mainActivity, "Please enter a valid quantity", R.style.Failure).show());
                    return;
                }
                CartItem foundCartItem = findInCart(cart, product.getLabel());
                if (foundCartItem != null) {
                    int finalQuantity = foundCartItem.getQuantity() + quantity;
                    foundCartItem.setQuantity(quantity);
                    CartItemService.getInstance().save(foundCartItem, cart.getId());
                    foundCartItem.setQuantity(finalQuantity);
                } else {
                    CartItem cartItem = new CartItem(quantity, product, cart);
                    cartItem = CartItemService.getInstance().save(cartItem, cart.getId());
                    cart.getCartItems().add(cartItem);
                }
                mainActivity.runOnUiThread(() -> {
                    StyleableToast.makeText(mainActivity, "Product added", R.style.Success).show();
                    mainActivity.replaceFragment(new CartItemListFragment(CartItemService.getInstance().getCartItems()), "Home", true);
                });
            });
            thread.start();
        } else {
            Intent intent = new Intent(mainActivity, AuthenticationActivity.class);
            mainActivity.setRequestCode(10);
            mainActivity.getActivityResultLauncher().launch(intent);
        }
    }

    private void postReview() {
        String writeReviewString = writeReview.getText().toString();
        float stars = rateProduct.getRating();

        if (writeReviewString.isEmpty())
            StyleableToast.makeText(mainActivity, "All fields are required", R.style.Failure).show();
        else {
            @SuppressLint("SetTextI18n") Thread thread = new Thread(() -> {
                Review review = new Review(writeReviewString, Double.valueOf(Float.toString(stars)), new Date(), UserService.getInstance().getUser(), product);
                review = ReviewService.getInstance().save(review);
                if (review != null) {
                    Review finalReview = review;
                    reviewAdapter.getReviews().add(finalReview);
                    mainActivity.runOnUiThread(() -> {
                        reviewAdapter.notifyDataSetChanged();
                        writeReview.setText("");
                        rateProduct.setRating(0);
                        noReviews.setVisibility(View.GONE);
                        StyleableToast.makeText(mainActivity, "Review posted", R.style.Success).show();
                    });
                    ProductService.getInstance().getProducts().stream().filter(pro -> pro.getId() == this.product.getId()).forEach(pro -> {
                        pro.setEvaluationCount(pro.getEvaluationCount() + 1);
                        float sum = 0;
                        for (Review reviewLoop : reviewAdapter.getReviews()) {
                            sum += reviewLoop.getStars();
                        }
                        sum /= pro.getEvaluationCount();
                        pro.setStars(sum);
                        float finalSum = sum;
                        mainActivity.runOnUiThread(() -> {
                            productRating.setRating(finalSum);
                            productEvaluation.setText("(" + pro.getEvaluationCount() + ")");
                            if (productAdapter != null) productAdapter.notifyDataSetChanged();
                            productRating.setRating(finalSum);
                        });
                    });

                } else {
                    mainActivity.runOnUiThread(() -> {
                        writeReview.setText("");
                        rateProduct.setRating(0);
                        StyleableToast.makeText(mainActivity, "Review already posted", R.style.Failure).show();
                    });
                }
            });
            thread.start();
        }
    }

    private void displayImage() {
        File localFile = ProductService.getInstance().getLocalFiles().get(product.getLabel());
        if (localFile == null || localFile.length() == 0) {
            downloadImageFirebase();
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
            productImage.setImageBitmap(bitmap);
        }
    }

    private void downloadImageFirebase() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading product...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + product.getLabel());
        try {
            File localFile = File.createTempFile(product.getLabel(), ".jpeg");
            storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                ProductService.getInstance().getLocalFiles().put(product.getLabel(), localFile);
                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                productImage.setImageBitmap(bitmap);
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
            }).addOnFailureListener(e -> {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                StyleableToast.makeText(mainActivity, "Fetching Product failed", R.style.Failure).show();
                final Handler handler = new Handler();
                handler.postDelayed(() -> mainActivity.getSupportFragmentManager().popBackStackImmediate(), 500);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressLint("SetTextI18n")
    private void bindData() {
        displayImage();
        productLabel.setText(product.getLabel());
        productRating.setRating((float) product.getStars());
        productEvaluation.setText("(" + product.getEvaluationCount() + ")");
        productDescription.setText(product.getDescription());
        productOrders.setText("Orders : " + product.getNumberOfOrders());
        productPrice.setText("$" + product.getPrice());
        addToCartButton.setOnClickListener(v -> {
            addToCart();
        });
        productQuantity.setText("1");
        postReviewButton.setOnClickListener(v -> {
            postReview();
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        initComponents(view);
        bindData();
        recyclerView = view.findViewById(R.id.reviews_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewAdapter = new ReviewAdapter(getContext(), null, null, null, null, null, this.reviews);
        recyclerView.setAdapter(reviewAdapter);
        if (this.reviews.isEmpty()) {
            ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            recyclerView.setLayoutParams(layoutParams);
            noReviews.setVisibility(View.VISIBLE);
        }
        return view;
    }

    public ImageView getProductImage() {
        return productImage;
    }

    public TextView getProductLabel() {
        return productLabel;
    }
}