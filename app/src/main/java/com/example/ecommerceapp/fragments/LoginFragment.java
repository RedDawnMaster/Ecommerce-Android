package com.example.ecommerceapp.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.activities.AuthenticationActivity;
import com.example.ecommerceapp.models.User;
import com.example.ecommerceapp.services.LoginManager;
import com.example.ecommerceapp.services.UserService;

import io.github.muddz.styleabletoast.StyleableToast;

public class LoginFragment extends Fragment {

    private EditText username;
    private EditText password;
    private Button loginButton;
    private TextView registerText;
    private AuthenticationActivity authenticationActivity;

    public LoginFragment() {
        // Required empty public constructor
    }

    private void initComponents(View view) {
        username = view.findViewById(R.id.login_username);
        password = view.findViewById(R.id.login_password);
        loginButton = view.findViewById(R.id.login_button);
        registerText = view.findViewById(R.id.not_yet);
        authenticationActivity = (AuthenticationActivity) getContext();
    }

    private void bindData() {
        loginButton.setOnClickListener(v -> login());
        registerText.setOnClickListener(v -> authenticationActivity.replaceFragment(new RegisterFragment()));
    }

    private void login() {
        String usernameString = username.getText().toString();
        String passwordString = password.getText().toString();
        if (usernameString.isEmpty() || passwordString.isEmpty())
            StyleableToast.makeText(authenticationActivity, "All fields are required", R.style.Failure).show();
        else {
            Thread thread = new Thread(() -> {
                User user = UserService.getInstance().login(usernameString, passwordString);
                if (user == null)
                    authenticationActivity.runOnUiThread(() -> StyleableToast.makeText(authenticationActivity, "Username or Password is incorrect", R.style.Failure).show());
                else {
                    LoginManager.saveCredentials(authenticationActivity, user.getUsername(), user.getPassword());
                    authenticationActivity.setResultCodeAndFinish(Activity.RESULT_OK, 0);
                }
            });
            thread.start();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initComponents(view);
        bindData();
        return view;
    }
}