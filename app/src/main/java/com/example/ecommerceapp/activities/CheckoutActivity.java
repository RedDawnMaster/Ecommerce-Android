package com.example.ecommerceapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.models.Response;
import com.example.ecommerceapp.services.CartService;
import com.example.ecommerceapp.services.OrderService;

import io.github.muddz.styleabletoast.StyleableToast;

public class CheckoutActivity extends AppCompatActivity {
    private EditText streetAddress;
    private EditText city;
    private EditText postalCode;
    private EditText cardNumber;
    private EditText cardholder;
    private EditText cardExpiry;
    private EditText cardCvv;
    private TextView orderSizeCheckout;
    private TextView orderTotalCheckout;
    private Button submitOrderButton;

    private double total;
    private int size;
    private String username;

    private void initComponents() {
        streetAddress = findViewById(R.id.street_address);
        city = findViewById(R.id.city);
        postalCode = findViewById(R.id.postal_code);
        cardNumber = findViewById(R.id.card_number);
        cardholder = findViewById(R.id.cardholder);
        cardExpiry = findViewById(R.id.card_expiry);
        cardCvv = findViewById(R.id.card_cvv);
        orderSizeCheckout = findViewById(R.id.order_size_checkout);
        orderTotalCheckout = findViewById(R.id.order_total_checkout);
        submitOrderButton = findViewById(R.id.submit_order_button);
    }

    private void checkout() {
        String streetAddressString = streetAddress.getText().toString();
        String cityString = city.getText().toString();
        String postalCodeString = postalCode.getText().toString();
        String cardNumberString = cardNumber.getText().toString();
        String cardholderString = cardholder.getText().toString();
        String cardExpiryString = cardExpiry.getText().toString();
        String cardCvvString = cardCvv.getText().toString();
        if (streetAddressString.isEmpty() || cityString.isEmpty() || postalCodeString.isEmpty() || cardNumberString.isEmpty() || cardholderString.isEmpty() || cardExpiryString.isEmpty() || cardCvvString.isEmpty())
            StyleableToast.makeText(this, "All fields are required", R.style.Failure).show();
        else {
            Thread thread = new Thread(() -> {
                Response result = CartService.getInstance().checkout(username);
                if (result.getMessage().equals("completed")) {
                    OrderService.getInstance().findByUserUsername(username);
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("error", result.getMessage());
                    setResult(Activity.RESULT_CANCELED, intent);
                    finish();
                }
            });
            thread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        initComponents();
        Bundle extras = getIntent().getExtras();
        total = extras.getDouble("total");
        size = extras.getInt("size");
        username = extras.getString("username");
        orderSizeCheckout.setText(size + " item(s)");
        orderTotalCheckout.setText("$" + total);
        submitOrderButton.setOnClickListener(v -> checkout());
    }
}