package com.example.ecommerceapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.activities.MainActivity;
import com.example.ecommerceapp.models.Category;
import com.example.ecommerceapp.services.CategoryService;

import io.github.muddz.styleabletoast.StyleableToast;

public class CategoryFragment extends Fragment {

    MainActivity mainActivity;
    private EditText categoryLabel;
    private Button saveCategoryButton;

    public CategoryFragment() {
        // Required empty public constructor
    }

    private void initComponents(View view) {
        categoryLabel = view.findViewById(R.id.category_label);
        saveCategoryButton = view.findViewById(R.id.save_category_button);
        mainActivity = (MainActivity) getContext();
    }

    private void saveCategory() {
        String categoryLabelString = categoryLabel.getText().toString();
        if (categoryLabelString.isEmpty())
            StyleableToast.makeText(mainActivity, "All fields are required", R.style.Failure).show();
        else {
            Thread thread = new Thread(() -> {
                Category category = new Category();
                category.setLabel(categoryLabelString);
                category = CategoryService.getInstance().save(category);
                if (category != null) {
                    CategoryService.getInstance().getCategories().add(category);
                    Menu menu = mainActivity.getNavigationView().getMenu();
                    Category finalCategory = category;
                    mainActivity.runOnUiThread(() -> {
                        menu.add(Menu.NONE, menu.size() - 1, Menu.NONE, finalCategory.getLabel());
                        StyleableToast.makeText(mainActivity, "Category saved", R.style.Success).show();
                    });
                } else {
                    mainActivity.runOnUiThread(() -> StyleableToast.makeText(mainActivity, "Category already exists", R.style.Failure).show());
                }
            });
            thread.start();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        initComponents(view);
        saveCategoryButton.setOnClickListener(v -> saveCategory());
        return view;
    }
}