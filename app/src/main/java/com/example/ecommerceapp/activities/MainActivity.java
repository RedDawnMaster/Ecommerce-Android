package com.example.ecommerceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.example.ecommerceapp.fragments.OrderListFragment;
import com.example.ecommerceapp.fragments.ProductListFragment;
import com.example.ecommerceapp.models.Category;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.services.CategoryService;
import com.example.ecommerceapp.services.ProductService;
import com.example.ecommerceapp.services.UserService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), o -> {

    });

    private void initComponents() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.bottom_nav);
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
                } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStackImmediate();
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    String tag = currentFragment.getTag();
                    switch (tag) {
                        case "home":
                            bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
                            break;
                        case "orders":
                            bottomNavigationView.getMenu().findItem(R.id.orders).setChecked(true);
                            break;
                        case "settings":
                            bottomNavigationView.getMenu().findItem(R.id.settings).setChecked(true);
                            break;

                    }
                }

            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    private void initBottomNav() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                item.setChecked(true);
                replaceFragment(new ProductListFragment(ProductService.getInstance().getProducts()), "home");
            } else if (itemId == R.id.orders) {
                item.setChecked(true);
                if (UserService.getInstance().getUser() != null)
                    replaceFragment(new OrderListFragment(), "orders");
                else {
                    Intent intent = new Intent(this, AuthenticationActivity.class);
                    activityResultLauncher.launch(intent);
                }
            } else if (itemId == R.id.settings) {
                item.setChecked(true);
                if (UserService.getInstance().getUser() != null)
                    replaceFragment(new OrderListFragment(), "settings");
                else {
                    Intent intent = new Intent(this, AuthenticationActivity.class);
                    activityResultLauncher.launch(intent);
                }
            }
            return false;
        });
    }

    private void loadData() {
        Menu menu = navigationView.getMenu();
        Thread thread = new Thread(() -> {
            List<Category> categories = CategoryService.getInstance().findAll();
            List<Product> products = ProductService.getInstance().findAll();
            runOnUiThread(() -> {
                replaceFragment(new ProductListFragment(products), "home");
                for (int i = 0; i < categories.size(); i++) {
                    Category category = categories.get(i);
                    menu.add(Menu.NONE, Menu.FIRST + i, Menu.NONE, category.getLabel());
                }
                navigationView.setNavigationItemSelectedListener(item -> {
                    replaceFragment(new ProductListFragment(ProductService.getInstance().getProducts().stream().filter(p -> p.getCategory().getLabel().equals(item.getTitle().toString())).collect(Collectors.toList())), null);
                    return false;
                });
            });
        });
        thread.start();
    }

    private void replaceFragment(Fragment fragment, String name) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment, name);
        fragmentTransaction.addToBackStack(name);
        fragmentTransaction.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        initToolbar();
        initDrawer();
        initBottomNav();
        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        MenuItem menuItem = menu.findItem(R.id.search_product);
        MenuItem menuItem1 = menu.findItem(R.id.cart);
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