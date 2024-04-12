package com.example.ecommerceapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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
import com.example.ecommerceapp.adapters.CartItemAdapter;
import com.example.ecommerceapp.adapters.OrderAdapter;
import com.example.ecommerceapp.adapters.OrderItemAdapter;
import com.example.ecommerceapp.adapters.ProductAdapter;
import com.example.ecommerceapp.fragments.AccountFragment;
import com.example.ecommerceapp.fragments.CartItemListFragment;
import com.example.ecommerceapp.fragments.HomeFragment;
import com.example.ecommerceapp.fragments.OrderItemListFragment;
import com.example.ecommerceapp.fragments.OrderListFragment;
import com.example.ecommerceapp.fragments.ProductListFragment;
import com.example.ecommerceapp.models.CartItem;
import com.example.ecommerceapp.models.Category;
import com.example.ecommerceapp.models.Order;
import com.example.ecommerceapp.models.OrderItem;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.models.User;
import com.example.ecommerceapp.services.CartItemService;
import com.example.ecommerceapp.services.CartService;
import com.example.ecommerceapp.services.CategoryService;
import com.example.ecommerceapp.services.LoginManager;
import com.example.ecommerceapp.services.OrderItemService;
import com.example.ecommerceapp.services.OrderService;
import com.example.ecommerceapp.services.ProductService;
import com.example.ecommerceapp.services.StatisticService;
import com.example.ecommerceapp.services.UserService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.muddz.styleabletoast.StyleableToast;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;

    private int requestCode;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), o -> {
        int resultCode = o.getResultCode();
        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        if (resultCode == Activity.RESULT_OK) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    loadOrdersAndCart();
                    switch (requestCode) {
                        case 0:
                            replaceFragment(new OrderListFragment(OrderService.getInstance().getOrders()), "Orders", true);
                            runOnUiThread(() -> StyleableToast.makeText(MainActivity.this, "Login successful", R.style.Success).show());
                            break;
                        case 1:
                            replaceFragment(new AccountFragment(), "Account", true);
                            runOnUiThread(() -> StyleableToast.makeText(MainActivity.this, "Login successful", R.style.Success).show());
                            break;
                        case 2:
                            replaceFragment(new CartItemListFragment(CartItemService.getInstance().getCartItems()), "Cart", true);
                            runOnUiThread(() -> StyleableToast.makeText(MainActivity.this, "Login successful", R.style.Success).show());
                            break;
                        case 3:
                            replaceFragment(new OrderListFragment(OrderService.getInstance().getOrders()), "Orders", true);
                            runOnUiThread(() -> StyleableToast.makeText(MainActivity.this, "Order placed", R.style.Success).show());
                            List<Integer> quantities = ProductService.getInstance().getQuantities();
                            List<Product> products = ProductService.getInstance().getBoughProducts();
                            for (int i = 0; i < products.size(); i++) {
                                Product product = products.get(i);
                                product.setNumberOfOrders(product.getNumberOfOrders() + quantities.get(i));
                            }
                        default:
                    }
                }
            });
            thread.start();
        } else if (resultCode == Activity.RESULT_CANCELED && o.getData() != null) {
            String error = o.getData().getStringExtra("error");
            StyleableToast.makeText(this, error, R.style.Failure).show();
            ProductService.getInstance().setBoughProducts(null);
            ProductService.getInstance().setQuantities(null);
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
                    if (currentFragment != null) {
                        String tag = currentFragment.getTag();
                        switch (Objects.requireNonNull(tag)) {
                            case "Orders":
                                bottomNavigationView.getMenu().findItem(R.id.orders).setChecked(true);
                                break;
                            case "Account":
                                bottomNavigationView.getMenu().findItem(R.id.account).setChecked(true);
                                break;
                            case "Home":
                                currentFragment.getView().setVisibility(View.GONE);
                                replaceFragment(new HomeFragment(), "Home", true);
                                bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
                        }
                    }
                } else finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    private void initBottomNav() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                item.setChecked(true);
                replaceFragment(new HomeFragment(), "Home", true);
            } else if (itemId == R.id.orders) {
                item.setChecked(true);
                if (UserService.getInstance().getUser() != null)
                    replaceFragment(new OrderListFragment(OrderService.getInstance().getOrders()), "Orders", true);
                else {
                    Intent intent = new Intent(this, AuthenticationActivity.class);
                    requestCode = 0;
                    activityResultLauncher.launch(intent);
                }
            } else if (itemId == R.id.account) {
                item.setChecked(true);
                if (UserService.getInstance().getUser() != null)
                    replaceFragment(new AccountFragment(), "Account", true);
                else {
                    Intent intent = new Intent(this, AuthenticationActivity.class);
                    requestCode = 1;
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
            StatisticService.getInstance().findById();
            List<Category> categories;
            List<Product> products;
            if (CategoryService.getInstance().getCategories() == null)
                categories = new ArrayList<>();
            else categories = CategoryService.getInstance().getCategories();
            if (ProductService.getInstance().getProducts() == null) products = new ArrayList<>();
            else products = ProductService.getInstance().getProducts();
            runOnUiThread(() -> {
                replaceFragment(new HomeFragment(), "Home", true);
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

    private void loadOrdersAndCart() {
        User user = UserService.getInstance().getUser();
        if (user != null) {
            OrderService.getInstance().findByUserUsername(user.getUsername());
            CartService.getInstance().findByUserUsername(user.getUsername());
            CartItemService.getInstance().setCartItems(CartService.getInstance().getCart().getCartItems());
        } else {
            LoginManager.removeCredentials(this);
        }
    }

    private void checkLoggedIn() {
        String[] credentials = LoginManager.getCredentials(this);
        if (credentials != null) {
            Thread thread = new Thread(() -> {
                UserService.getInstance().login(credentials[0], credentials[1]);
                loadOrdersAndCart();
            });
            thread.start();
        }
    }

    public void replaceFragment(Fragment fragment, String name, boolean bool) {
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
        checkLoggedIn();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        MenuItem search = menu.findItem(R.id.search_product);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Search product");
        EditText searchPlate = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchPlate.setTextColor(getResources().getColor(R.color.white));
        searchPlate.setHintTextColor(getResources().getColor(R.color.white));
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    // replace later by backend api instead of searching in frontend
    @Override
    public boolean onQueryTextSubmit(String query) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        if (currentFragment != null) {
            String tag = currentFragment.getTag();
            switch (Objects.requireNonNull(tag)) {
                case "Orders":
                    OrderAdapter orderAdapter = ((OrderListFragment) currentFragment).getOrderAdapter();
                    List<Order> orders = OrderService.getInstance().getOrders();
                    if (!query.isEmpty()) {
                        List<Order> foundOrders = new ArrayList<>();
                        for (Order order : orders) {
                            if (order.getReference().toLowerCase().contains(query.toLowerCase()))
                                foundOrders.add(order);
                        }
                        orderAdapter.setOrders(foundOrders);
                    } else orderAdapter.setOrders(orders);
                    orderAdapter.notifyDataSetChanged();
                    break;
                case "Cart":
                    CartItemAdapter cartItemAdapter = ((CartItemListFragment) currentFragment).getCartItemAdapter();
                    List<CartItem> cartItems = CartItemService.getInstance().getCartItems();
                    if (!query.isEmpty()) {
                        List<CartItem> foundCartItems = new ArrayList<>();
                        for (CartItem cartItem : cartItems) {
                            if (cartItem.getProduct().getLabel().toLowerCase().contains(query.toLowerCase()))
                                foundCartItems.add(cartItem);
                        }
                        cartItemAdapter.setCartItems(foundCartItems);
                    } else cartItemAdapter.setCartItems(cartItems);
                    cartItemAdapter.notifyDataSetChanged();
                    break;
                case "OrderItems":
                    OrderItemAdapter orderItemAdapter = ((OrderItemListFragment) currentFragment).getOrderItemAdapter();
                    List<OrderItem> orderItems = OrderItemService.getInstance().getOrderItems();
                    if (!query.isEmpty()) {
                        List<OrderItem> foundOrderItems = new ArrayList<>();
                        for (OrderItem orderItem : orderItems) {
                            if (orderItem.getProduct().getLabel().toLowerCase().contains(query.toLowerCase()))
                                foundOrderItems.add(orderItem);
                        }
                        orderItemAdapter.setOrderItems(foundOrderItems);
                    } else orderItemAdapter.setOrderItems(orderItems);
                    orderItemAdapter.notifyDataSetChanged();
                    break;
                default:
                    ProductAdapter productAdapter;
                    List<Product> products = ProductService.getInstance().getProducts();
                    if (currentFragment instanceof ProductListFragment) {
                        productAdapter = ((ProductListFragment) currentFragment).getProductAdapter();
                        if (!query.isEmpty()) {
                            List<Product> foundProducts = new ArrayList<>();
                            for (Product product : products) {
                                if (product.getLabel().toLowerCase().contains(query.toLowerCase()))
                                    foundProducts.add(product);
                            }
                            productAdapter.setProducts(foundProducts);
                        } else
                            productAdapter.setProducts(products);
                        productAdapter.notifyDataSetChanged();
                    } else
                        replaceFragment(new ProductListFragment(products), "Products", true);
                    break;
            }
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty()) this.onQueryTextSubmit(newText);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cart)
            if (UserService.getInstance().getUser() != null)
                replaceFragment(new CartItemListFragment(CartItemService.getInstance().getCartItems()), "Cart", true);
            else {
                Intent intent = new Intent(this, AuthenticationActivity.class);
                requestCode = 2;
                activityResultLauncher.launch(intent);
            }
        return super.onOptionsItemSelected(item);
    }

    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public ActivityResultLauncher<Intent> getActivityResultLauncher() {
        return activityResultLauncher;
    }
}