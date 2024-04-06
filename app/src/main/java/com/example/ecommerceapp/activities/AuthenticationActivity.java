package com.example.ecommerceapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.fragments.LoginFragment;

public class AuthenticationActivity extends AppCompatActivity {

    private void replaceFragment(Fragment fragment, String name, boolean bool) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.authentication_fragment_container, fragment, name);
        if (bool) fragmentTransaction.addToBackStack(name);
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        replaceFragment(new LoginFragment(), null, false);
    }
}