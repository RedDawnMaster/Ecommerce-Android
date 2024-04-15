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
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private boolean validateEmail(String email) {
        String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                        "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);

        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean validateDate(String input, SimpleDateFormat dateFormat) {
        try {
            dateFormat.setLenient(false);
            Date date = dateFormat.parse(input);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private void register() {
        String usernameString = username.getText().toString();
        String emailString = email.getText().toString();
        String firstNameString = firstName.getText().toString();
        String lastNameString = lastName.getText().toString();
        String birthDateString = birthDate.getText().toString();
        String passwordString = password.getText().toString();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if (usernameString.isEmpty() || emailString.isEmpty() || firstNameString.isEmpty() || lastNameString.isEmpty() || birthDateString.isEmpty() || passwordString.isEmpty())
            StyleableToast.makeText(authenticationActivity, "All fields are required", R.style.Failure).show();
        else if (!validateEmail(emailString))
            StyleableToast.makeText(authenticationActivity, "Please enter a valid email address", R.style.Failure).show();
        else if (!validateDate(birthDateString, dateFormat))
            StyleableToast.makeText(authenticationActivity, "Date should be in this format : dd/MM/yyyy", R.style.Failure).show();
        else {
            Thread thread = new Thread(() -> {
                User user;
                try {
                    user = new User(usernameString, firstNameString, lastNameString, emailString, passwordString, dateFormat.parse(birthDateString));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                user = UserService.getInstance().register(user);
                if (user == null)
                    authenticationActivity.runOnUiThread(() -> StyleableToast.makeText(authenticationActivity, "Username or email already exists", R.style.Failure).show());
                else {
                    LoginManager.saveCredentials(authenticationActivity, user.getUsername(), user.getPassword());
                    authenticationActivity.setResultCodeAndFinish(Activity.RESULT_OK, 1);
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