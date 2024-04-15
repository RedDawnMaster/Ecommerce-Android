package com.example.ecommerceapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.fragments.LoginFragment;

public class AuthenticationActivity extends AppCompatActivity {

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.authentication_fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public void setResultCodeAndFinish(int resultCode, int type) {
        if (type != 2) {
            Intent intent = new Intent();
            intent.putExtra("type", type);
            setResult(resultCode, intent);
        } else setResult(resultCode);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                setResultCodeAndFinish(Activity.RESULT_CANCELED, 2);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
        replaceFragment(new LoginFragment());

    }

}