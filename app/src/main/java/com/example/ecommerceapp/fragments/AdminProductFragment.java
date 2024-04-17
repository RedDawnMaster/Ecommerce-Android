package com.example.ecommerceapp.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.activities.MainActivity;
import com.example.ecommerceapp.adapters.ProductAdapter;
import com.example.ecommerceapp.adapters.ReviewAdapter;
import com.example.ecommerceapp.models.Category;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.models.Review;
import com.example.ecommerceapp.services.CategoryService;
import com.example.ecommerceapp.services.ProductService;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;


public class AdminProductFragment extends Fragment {
    private Product product;
    private ReviewAdapter reviewAdapter;
    private TextView noReviewsAdmin;
    private ProductAdapter productAdapter;
    private List<Review> reviews;

    private ImageView productImageAdmin;
    private Button uploadImage;
    private RatingBar productRatingBarAdmin;
    private TextView productEvaluationAdmin;
    private EditText productLabelValueAdmin;
    private EditText productDescriptionValueAdmin;
    private EditText productCategoryValueAdmin;
    private EditText productPriceValueAdmin;
    private EditText productStockValueAdmin;
    private TextView productOrdersValueAdmin;
    private RecyclerView recyclerView;
    private Button saveProductButton;

    private MainActivity mainActivity;
    private Uri selectedImageUri;
    boolean imageUploadResult;
    Drawable oldDrawable;
    private File localFile;


    ActivityResultLauncher<Intent> imageChooserActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null
                            && data.getData() != null) {
                        selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    getContext().getContentResolver(),
                                    selectedImageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        productImageAdmin.setImageBitmap(
                                selectedImageBitmap);
                    }
                }
            });


    public AdminProductFragment() {
        this.reviews = new ArrayList<>();
        this.productAdapter = null;
    }

    public AdminProductFragment(ProductAdapter productAdapter) {
        this.reviews = new ArrayList<>();
        this.productAdapter = productAdapter;
    }

    public AdminProductFragment(Product product, List<Review> reviews, ProductAdapter productAdapter) {
        this.product = product;
        if (reviews != null) this.reviews = reviews;
        else this.reviews = new ArrayList<>();
        this.productAdapter = productAdapter;
    }

    private void initComponents(View view) {
        productImageAdmin = view.findViewById(R.id.product_image_admin);
        uploadImage = view.findViewById(R.id.upload_image);
        productRatingBarAdmin = view.findViewById(R.id.product_ratingBar_admin);
        productEvaluationAdmin = view.findViewById(R.id.product_evaluation_admin);
        productLabelValueAdmin = view.findViewById(R.id.product_label_value_admin);
        productDescriptionValueAdmin = view.findViewById(R.id.product_description_value_admin);
        productCategoryValueAdmin = view.findViewById(R.id.product_category_value_admin);
        productPriceValueAdmin = view.findViewById(R.id.product_price_value_admin);
        productStockValueAdmin = view.findViewById(R.id.product_stock_value_admin);
        productOrdersValueAdmin = view.findViewById(R.id.product_orders_value_admin);
        recyclerView = view.findViewById(R.id.reviews_recycler_admin);
        saveProductButton = view.findViewById(R.id.save_product);
        noReviewsAdmin = view.findViewById(R.id.no_reviews_admin);

        mainActivity = (MainActivity) getContext();
    }

    @SuppressLint("SetTextI18n")
    private void bindData() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewAdapter = new ReviewAdapter(getContext(), noReviewsAdmin, product, productRatingBarAdmin, productEvaluationAdmin, productAdapter, this.reviews);
        recyclerView.setAdapter(reviewAdapter);
        if (product != null) {
//            downloadImageFirebase();
            displayImage();
            productRatingBarAdmin.setRating((float) product.getStars());
            productEvaluationAdmin.setText("(" + product.getEvaluationCount() + ")");
            productLabelValueAdmin.setText(product.getLabel());
            productDescriptionValueAdmin.setText(product.getDescription());
            productCategoryValueAdmin.setText(product.getCategory().getLabel());
            productPriceValueAdmin.setText(String.valueOf(product.getPrice()));
            productStockValueAdmin.setText(String.valueOf(product.getStock()));
            productOrdersValueAdmin.setText(String.valueOf(product.getNumberOfOrders()));
        }
        if (this.reviews.isEmpty()) {
            ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            recyclerView.setLayoutParams(layoutParams);
            noReviewsAdmin.setVisibility(View.VISIBLE);
        }
        uploadImage.setOnClickListener(v -> imageChooser());
        saveProductButton.setOnClickListener(v -> saveProduct());
    }

    private boolean checkCategory(String oldCategory, String newCategory) {
        boolean bool = true;
        if (!oldCategory.equalsIgnoreCase(newCategory)) {
            bool = false;
            List<Category> categories = CategoryService.getInstance().getCategories();
            for (Category category : categories) {
                if (category.getLabel().equals(newCategory)) {
                    bool = true;
                    product.setCategory(category);
                    break;
                }
            }
            if (!bool) {
                StyleableToast.makeText(mainActivity, "Category does not exist", R.style.Failure).show();
            }
        }
        return bool;
    }

    private Category findCategory(String label) {
        List<Category> categories = CategoryService.getInstance().getCategories();
        for (Category category : categories) {
            if (category.getLabel().equalsIgnoreCase(label)) {
                return category;
            }
        }
        return null;
    }

    private void applyToProduct(Long id) {
        List<Product> products = ProductService.getInstance().getProducts();
        Product productFound = null;
        for (Product productLoop : products) {
            if (productLoop.getId() == id) {
                productFound = productLoop;
                break;
            }
        }
        productFound.setLabel(this.product.getLabel());
        productFound.setDescription(this.product.getDescription());
        productFound.setCategory(this.product.getCategory());
        productFound.setPrice(this.product.getPrice());
        productFound.setStock(this.product.getStock());
    }

    private void saveProduct() {
        Drawable productImageAdminDrawable = productImageAdmin.getDrawable();
        String productLabelValueAdminString = productLabelValueAdmin.getText().toString();
        String productDescriptionValueAdminString = productDescriptionValueAdmin.getText().toString();
        String productCategoryValueAdminString = productCategoryValueAdmin.getText().toString();
        String productPriceValueAdminString = productPriceValueAdmin.getText().toString();
        String productStockValueAdminString = productStockValueAdmin.getText().toString();

        if (productImageAdminDrawable == null || productLabelValueAdminString.isEmpty() || productDescriptionValueAdminString.isEmpty() || productCategoryValueAdminString.isEmpty() || productPriceValueAdminString.isEmpty() || productStockValueAdminString.isEmpty())
            StyleableToast.makeText(mainActivity, "All fields are required", R.style.Failure).show();
        else if (Double.parseDouble(productPriceValueAdminString) <= 0)
            StyleableToast.makeText(mainActivity, "Please enter a valid price", R.style.Failure).show();
        else if (Integer.parseInt(productStockValueAdminString) < 0)
            StyleableToast.makeText(mainActivity, "Please enter a valid stock amount", R.style.Failure).show();
        else if (product != null) {
            Long id = product.getId();
            String oldLabel = product.getLabel();
            String oldCategory = product.getCategory().getLabel();
            product.setLabel(productLabelValueAdminString);
            product.setDescription(productDescriptionValueAdminString);
            if (!checkCategory(oldCategory, productCategoryValueAdminString))
                return;
            product.setPrice(Double.parseDouble(productPriceValueAdminString));
            product.setStock(Integer.parseInt(productStockValueAdminString));
            Thread thread = new Thread(() -> {
                Product result = ProductService.getInstance().update(product);
                if (result == null) {
                    mainActivity.runOnUiThread(() -> StyleableToast.makeText(mainActivity, "Label already exists", R.style.Failure).show());
                    Product foundProduct = ProductService.getInstance().findById(id);
                    product.setLabel(foundProduct.getLabel());
                    product.setDescription(foundProduct.getDescription());
                    product.setCategory(foundProduct.getCategory());
                    product.setPrice(foundProduct.getPrice());
                    product.setStock(foundProduct.getStock());
                } else {
                    mainActivity.runOnUiThread(() -> {
                        applyToProduct(product.getId());
                        if (!oldLabel.equals(product.getLabel())) {
                            uploadImageFirebase();
                            deleteImageFirebase(oldLabel);
                        } else if (oldDrawable != productImageAdmin.getDrawable()) {
                            uploadImageFirebase();
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
                        StyleableToast.makeText(mainActivity, "Product updated", R.style.Success).show();
                    });

                }
            });
            thread.start();
        } else {
            product = new Product();
            product.setLabel(productLabelValueAdminString);
            product.setDescription(productDescriptionValueAdminString);
            Category category = findCategory(productCategoryValueAdminString);
            if (category != null) product.setCategory(category);
            else {
                StyleableToast.makeText(mainActivity, "Category does not exist", R.style.Failure).show();
                return;
            }
            product.setPrice(Double.parseDouble(productPriceValueAdminString));
            product.setStock(Integer.parseInt(productStockValueAdminString));
            Thread thread = new Thread(() -> {
                Product result = ProductService.getInstance().save(product);
                if (result == null) {
                    mainActivity.runOnUiThread(() -> StyleableToast.makeText(mainActivity, "Label already exists", R.style.Failure).show());
                    product = null;
                } else {
                    mainActivity.runOnUiThread(() -> {
                        uploadImageNewFirebase(result);
                    });
                }
            });
            thread.start();
        }

    }


    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        imageChooserActivity.launch(i);
    }

    private void uploadImageFirebase() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Saving product...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + product.getLabel());
        storageReference.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot -> {
            if (progressDialog.isShowing()) progressDialog.dismiss();
            oldDrawable = productImageAdmin.getDrawable();
            ProductService.getInstance().getLocalFiles().remove(product.getLabel());
        }).addOnFailureListener(e -> {
            if (progressDialog.isShowing()) progressDialog.dismiss();
            StyleableToast.makeText(mainActivity, "Uploading image failed", R.style.Failure).show();
        });
    }

    private void uploadImageNewFirebase(Product result) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Saving product...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + product.getLabel());
        storageReference.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot -> {
            if (progressDialog.isShowing()) progressDialog.dismiss();
            oldDrawable = productImageAdmin.getDrawable();
            ProductService.getInstance().getProducts().add(result);
            product = result;
            Fragment fragmentProducts = mainActivity.getSupportFragmentManager().findFragmentByTag("Products");
            if (fragmentProducts != null) {
                ((ProductListFragment) fragmentProducts).getProductAdapter().notifyDataSetChanged();
            }
            Fragment fragmentCategory = mainActivity.getSupportFragmentManager().findFragmentByTag(product.getCategory().getLabel());
            if (fragmentCategory != null) {
                ((ProductListFragment) fragmentCategory).getProductAdapter().getProducts().add(result);
                ((ProductListFragment) fragmentCategory).getProductAdapter().notifyDataSetChanged();
            }
            StyleableToast.makeText(mainActivity, "Product saved", R.style.Success).show();
            product = null;
            productImageAdmin.setImageDrawable(null);
            productLabelValueAdmin.setText("");
            productDescriptionValueAdmin.setText("");
            productCategoryValueAdmin.setText("");
            productPriceValueAdmin.setText("");
            productStockValueAdmin.setText("");
            productOrdersValueAdmin.setText("0");
        }).addOnFailureListener(e -> {
            if (progressDialog.isShowing()) progressDialog.dismiss();
            imageUploadResult = false;
            StyleableToast.makeText(mainActivity, "Uploading image failed", R.style.Failure).show();
            Thread threadDelete = new Thread(() -> {
                ProductService.getInstance().deleteByLabel(product.getLabel());
                product = null;
                mainActivity.runOnUiThread(() -> StyleableToast.makeText(mainActivity, "Saving product failed", R.style.Failure).show());
            });
            threadDelete.start();
        });
    }

    private void displayImage() {
        localFile = ProductService.getInstance().getLocalFiles().get(product.getLabel());
        if (localFile == null || localFile.length() == 0) {
            downloadImageFirebase();
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
            selectedImageUri = Uri.fromFile(localFile);
            productImageAdmin.setImageBitmap(bitmap);
            oldDrawable = productImageAdmin.getDrawable();
        }
    }

    private void downloadImageFirebase() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading product...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + product.getLabel());
        try {
            localFile = File.createTempFile(product.getLabel(), ".jpeg");
            storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                ProductService.getInstance().getLocalFiles().put(product.getLabel(), localFile);
                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                selectedImageUri = Uri.fromFile(localFile);
                productImageAdmin.setImageBitmap(bitmap);
                oldDrawable = productImageAdmin.getDrawable();
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

    private void deleteImageFirebase(String oldLabel) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Processing...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + oldLabel);
        storageReference.delete().addOnSuccessListener(unused -> {
            if (progressDialog.isShowing()) progressDialog.dismiss();
        }).addOnFailureListener(e -> {
            if (progressDialog.isShowing()) progressDialog.dismiss();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_product, container, false);
        initComponents(view);
        bindData();
        return view;
    }

    public Product getProduct() {
        return product;
    }

    public ImageView getProductImageAdmin() {
        return productImageAdmin;
    }
}