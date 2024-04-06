package com.example.ecommerceapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import com.example.ecommerceapp.fragments.HomeFragment;
import com.example.ecommerceapp.fragments.OrderListFragment;
import com.example.ecommerceapp.fragments.ProductListFragment;
import com.example.ecommerceapp.models.Category;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.services.CategoryService;
import com.example.ecommerceapp.services.ProductService;
import com.example.ecommerceapp.services.UserService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private int currentPos;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), o -> {
        if (o.getResultCode() == 0) {
        }
    });

    private void initComponents() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        bottomNavigationView = findViewById(R.id.bottom_nav);
    }

    private void initToolbar() {
        toolbar.setTitle("E-App");
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
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
                    String tag = currentFragment.getTag();
                    switch (tag) {
                        case "home":
                            bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
                            break;
                        case "orders":
                            bottomNavigationView.getMenu().findItem(R.id.orders).setChecked(true);
                            break;
                        case "settings":
                            bottomNavigationView.getMenu().findItem(R.id.profile).setChecked(true);
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
                replaceFragment(new HomeFragment(), "home", true);
            } else if (itemId == R.id.orders) {
                item.setChecked(true);
                if (UserService.getInstance().getUser() != null)
                    replaceFragment(new OrderListFragment(), "orders", true);
                else {
                    Intent intent = new Intent(this, AuthenticationActivity.class);
                    currentPos = 0;
                    activityResultLauncher.launch(intent);
                }
            } else if (itemId == R.id.profile) {
                item.setChecked(true);
                if (UserService.getInstance().getUser() != null)
                    replaceFragment(new OrderListFragment(), "profile", true);
                else {
                    Intent intent = new Intent(this, AuthenticationActivity.class);
                    currentPos = 1;
                    activityResultLauncher.launch(intent);
                }
            }
            return false;
        });
    }

    private void loadData() {
        Menu menu = navigationView.getMenu();
        Thread thread = new Thread(() -> {
            CategoryService.getInstance().findAll();
            ProductService.getInstance().findAll();
            List<Category> categories;
            List<Product> products;
            if (CategoryService.getInstance().getCategories() == null)
                categories = new ArrayList<>();
            else categories = CategoryService.getInstance().getCategories();
            if (ProductService.getInstance().getProducts() == null) products = new ArrayList<>();
            else products = ProductService.getInstance().getProducts();
            runOnUiThread(() -> {
                replaceFragment(new HomeFragment(), "home", false);
                for (int i = 0; i < categories.size(); i++) {
                    Category category = categories.get(i);
                    menu.add(Menu.NONE, Menu.FIRST + i, Menu.NONE, category.getLabel());
                }
                navigationView.setNavigationItemSelectedListener(item -> {
                    replaceFragment(new ProductListFragment(products.stream().filter(p -> p.getCategory().getLabel().equals(item.getTitle().toString())).collect(Collectors.toList())), item.getTitle().toString(), true);
                    return false;
                });
            });
        });
        thread.start();
    }

    private void replaceFragment(Fragment fragment, String name, boolean bool) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment_container, fragment, name);
        if (bool) fragmentTransaction.addToBackStack(name);
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
        MenuItem search = menu.findItem(R.id.search_product);
        SearchView searchView = (SearchView) search.getActionView();
        assert searchView != null;
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cart)
            Toast.makeText(this, "Hola", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }
}