package com.example.ecommerceapp.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.fragments.ProductListFragment;
import com.example.ecommerceapp.models.Category;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private List<Category> categories;

    private void initComponents() {
        categories = new ArrayList<>();
        categories.add(new Category("electronics", 5));
        categories.add(new Category("outfits", 2));
        categories.add(new Category("zmer", 7));
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
    }

    private void initToolbar() {
        toolbar.setTitle("Amazon");
        setSupportActionBar(toolbar);
    }

    private void initDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else MainActivity.super.onBackPressed();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            menu.add(Menu.NONE, Menu.FIRST + i, Menu.NONE, category.getLabel().toUpperCase());
        }
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                // Handle each menu item based on its id
                case Menu.FIRST:
                    replaceFragment(new ProductListFragment());
                    break;
                case Menu.FIRST + 1:
                    // Handle the second menu item
                    break;
                // Add more cases as needed for each menu item
                default:
                    // Handle default case
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        initToolbar();
        initDrawer();
        replaceFragment(new ProductListFragment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        MenuItem menuItem = menu.findItem(R.id.search_product);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search product");

        EditText searchPlate = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchPlate.setTextColor(getResources().getColor(R.color.white));
        searchPlate.setHintTextColor(getResources().getColor(R.color.white));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}