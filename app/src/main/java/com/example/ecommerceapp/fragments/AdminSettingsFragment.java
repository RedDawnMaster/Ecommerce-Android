package com.example.ecommerceapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.activities.MainActivity;
import com.example.ecommerceapp.models.Statistic;
import com.example.ecommerceapp.services.LoginManager;
import com.example.ecommerceapp.services.ProductService;
import com.example.ecommerceapp.services.StatisticService;
import com.example.ecommerceapp.services.UserService;

import io.github.muddz.styleabletoast.StyleableToast;


public class AdminSettingsFragment extends Fragment {

    private TextView totalProductsValue;
    private TextView totalSoldProductsValue;
    private TextView totalClientsValue;
    private TextView revenueValue;

    private EditText deliveryDaysValue;
    private EditText refundDaysValue;

    private Button updateButtonAdmin;
    private Button logoutAdmin;

    private MainActivity mainActivity;


    public AdminSettingsFragment() {
        // Required empty public constructor
    }

    private void initComponents(View view) {
        totalProductsValue = view.findViewById(R.id.total_products_value);
        totalSoldProductsValue = view.findViewById(R.id.total_sold_products_value);
        totalClientsValue = view.findViewById(R.id.total_clients_value);
        revenueValue = view.findViewById(R.id.revenue_value);
        deliveryDaysValue = view.findViewById(R.id.delivery_days_value);
        refundDaysValue = view.findViewById(R.id.refund_days_value);
        updateButtonAdmin = view.findViewById(R.id.update_button_admin);
        logoutAdmin = view.findViewById(R.id.logout_admin);
        mainActivity = (MainActivity) getContext();
    }

    private void logout() {
        LoginManager.removeCredentials(mainActivity);
        UserService.getInstance().setUser(null);
        ProductService.getInstance().calculateProducts();
        mainActivity.getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mainActivity.replaceFragment(new HomeFragment(), "Home", true);
        mainActivity.invalidateOptionsMenu();
        mainActivity.initBottomNav();
        StyleableToast.makeText(mainActivity, "Logged out", R.style.Success).show();
    }

    private void update() {
        String deliveryDaysValueString = deliveryDaysValue.getText().toString();
        String refundDaysValueString = refundDaysValue.getText().toString();

        if (deliveryDaysValueString.isEmpty() || refundDaysValueString.isEmpty())
            StyleableToast.makeText(mainActivity, "All fields are required", R.style.Failure).show();
        else if (Integer.parseInt(deliveryDaysValueString) <= 0 || Integer.parseInt(refundDaysValueString) <= 0)
            StyleableToast.makeText(mainActivity, "Values should be positive", R.style.Failure).show();
        else {
            Statistic statistic = StatisticService.getInstance().getStatistic();
            statistic.setDeliveryTime(Integer.parseInt(deliveryDaysValueString));
            statistic.setRefundPeriod(Integer.parseInt(refundDaysValueString));
            Thread thread = new Thread(() -> {
                StatisticService.getInstance().save();
                mainActivity.runOnUiThread(() -> {
                    Fragment fragmentProducts = mainActivity.getSupportFragmentManager().findFragmentByTag("Home");
                    if (fragmentProducts != null) {
                        ((HomeFragment) fragmentProducts).getDeliveryDays().setText(deliveryDaysValue + " Days Delivery");
                        ((HomeFragment) fragmentProducts).getRefundDays().setText(refundDaysValue + " Days Refund");
                    }
                    StyleableToast.makeText(mainActivity, "Parameters updated", R.style.Success).show();
                });
            });
            thread.start();
        }
    }

    @SuppressLint("SetTextI18n")
    private void bindData() {
        Statistic statistic = StatisticService.getInstance().getStatistic();
        totalProductsValue.setText(String.valueOf(statistic.getTotalProducts()));
        totalSoldProductsValue.setText(String.valueOf(statistic.getTotalSoldProducts()));
        totalClientsValue.setText(String.valueOf(statistic.getTotalClients()));
        revenueValue.setText("$" + statistic.getTotalSales());
        deliveryDaysValue.setText(String.valueOf(statistic.getDeliveryTime()));
        refundDaysValue.setText(String.valueOf(statistic.getRefundPeriod()));
        logoutAdmin.setOnClickListener(v -> logout());
        updateButtonAdmin.setOnClickListener(v -> update());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_settings, container, false);
        initComponents(view);
        bindData();
        return view;
    }
}