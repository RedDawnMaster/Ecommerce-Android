package com.example.ecommerceapp.fragments;

import android.annotation.SuppressLint;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;

import io.github.muddz.styleabletoast.StyleableToast;

public class RegisterFragment extends Fragment {
    private EditText username;
    private EditText email;
    private EditText firstName;
    private EditText lastName;
    private EditText birthDate;
    private EditText password;
    private Button registerButton;
    private TextView loginText;
    private AuthenticationActivity authenticationActivity;

    public RegisterFragment() {
        // Required empty public constructor
    }

    private void initComponents(View view) {
        username = view.findViewById(R.id.register_username);
        email = view.findViewById(R.id.register_email);
        firstName = view.findViewById(R.id.register_firstname);
        lastName = view.findViewById(R.id.register_lastname);
        birthDate = view.findViewById(R.id.register_birthdate);
        password = view.findViewById(R.id.register_password);
        registerButton = view.findViewById(R.id.register_button);
        loginText = view.findViewById(R.id.yet);
        authenticationActivity = (AuthenticationActivity) getContext();
    }

    private void bindData() {
        registerButton.setOnClickListener(v -> register());
        loginText.setOnClickListener(v -> authenticationActivity.replaceFragment(new LoginFragment()));
    }


    private void register() {
        String usernameString = username.getText().toString();
        String emailStringString = email.getText().toString();
        String firstNameString = firstName.getText().toString();
        String lastNameString = lastName.getText().toString();
        String birthDateString = birthDate.getText().toString();
        String passwordString = password.getText().toString();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if (usernameString.isEmpty() || emailStringString.isEmpty() || firstNameString.isEmpty() || lastNameString.isEmpty() || birthDateString.isEmpty() || passwordString.isEmpty())
            StyleableToast.makeText(authenticationActivity, "All fields are required", R.style.Failure).show();
        else {
            Thread thread = new Thread(() -> {
                User user;
                try {
                    user = new User(usernameString, firstNameString, lastNameString, emailStringString, passwordString, dateFormat.parse(birthDateString));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                user = UserService.getInstance().register(user);
                if (user == null)
                    authenticationActivity.runOnUiThread(() -> StyleableToast.makeText(authenticationActivity, "Username or email already exists", R.style.Failure).show());
                else {
                    LoginManager.saveCredentials(authenticationActivity, user.getUsername(), user.getPassword());
                    authenticationActivity.setResultCodeAndFinish(Activity.RESULT_OK);
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initComponents(view);
        bindData();
        return view;
    }
}