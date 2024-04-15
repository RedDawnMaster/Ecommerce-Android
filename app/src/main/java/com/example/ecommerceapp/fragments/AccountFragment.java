package com.example.ecommerceapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.activities.MainActivity;
import com.example.ecommerceapp.models.User;
import com.example.ecommerceapp.services.LoginManager;
import com.example.ecommerceapp.services.UserService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.muddz.styleabletoast.StyleableToast;

public class AccountFragment extends Fragment {

    private EditText username;
    private EditText email;
    private EditText firstName;
    private EditText lastName;
    private Button updateButton;
    private Button logoutButton;
    private Button passwordButton;
    private MainActivity mainActivity;

    public AccountFragment() {
        // Required empty public constructor
    }


    private void initComponents(View view) {
        username = view.findViewById(R.id.account_username);
        email = view.findViewById(R.id.account_email);
        firstName = view.findViewById(R.id.account_firstname);
        lastName = view.findViewById(R.id.account_lastname);
        updateButton = view.findViewById(R.id.update_button);
        logoutButton = view.findViewById(R.id.logout);
        passwordButton = view.findViewById(R.id.change_password_button);
        mainActivity = (MainActivity) getContext();
    }

    private void bindData() {
        User user = UserService.getInstance().getUser();
        username.setText(user.getUsername());
        email.setText(user.getEmail());
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        updateButton.setOnClickListener(v -> update());
        logoutButton.setOnClickListener(v -> logout());
        passwordButton.setOnClickListener(v -> mainActivity.replaceFragment(new PasswordFragment(), "Password", true));
    }

    private boolean validateEmail(String email) {
        String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                        "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);

        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void update() {
        String usernameString = username.getText().toString();
        String emailString = email.getText().toString();
        String firstNameString = firstName.getText().toString();
        String lastNameString = lastName.getText().toString();
        if (usernameString.isEmpty() || emailString.isEmpty() || firstNameString.isEmpty() || lastNameString.isEmpty())
            StyleableToast.makeText(mainActivity, "All fields are required", R.style.Failure).show();
        else if (!validateEmail(emailString))
            StyleableToast.makeText(mainActivity, "Please enter a valid email address", R.style.Failure).show();
        else {
            User user = UserService.getInstance().getUser();

            String oldUsername = user.getUsername();

            user.setUsername(usernameString);
            user.setEmail(emailString);
            user.setFirstName(firstNameString);
            user.setLastName(lastNameString);
            Thread thread = new Thread(() -> {
                User result = UserService.getInstance().update();
                if (result == null) {
                    mainActivity.runOnUiThread(() -> StyleableToast.makeText(mainActivity, "Username or Email already exists", R.style.Failure).show());
                    UserService.getInstance().login(oldUsername, user.getPassword());
                } else {
                    LoginManager.removeCredentials(mainActivity);
                    LoginManager.saveCredentials(mainActivity, user.getUsername(), user.getPassword());
                    mainActivity.runOnUiThread(() -> StyleableToast.makeText(mainActivity, "Account updated", R.style.Success).show());
                }
            });
            thread.start();
        }
    }

    private void logout() {
        LoginManager.removeCredentials(mainActivity);
        UserService.getInstance().setUser(null);
        mainActivity.getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mainActivity.replaceFragment(new HomeFragment(), "Home", true);
        mainActivity.invalidateOptionsMenu();
        mainActivity.initBottomNav();
        StyleableToast.makeText(mainActivity, "Logged out", R.style.Success).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        initComponents(view);
        bindData();
        return view;
    }
}