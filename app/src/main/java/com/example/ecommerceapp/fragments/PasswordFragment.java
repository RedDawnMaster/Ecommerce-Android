package com.example.ecommerceapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.activities.MainActivity;
import com.example.ecommerceapp.models.User;
import com.example.ecommerceapp.services.LoginManager;
import com.example.ecommerceapp.services.UserService;

import io.github.muddz.styleabletoast.StyleableToast;

public class PasswordFragment extends Fragment {

    private EditText oldPassword;
    private EditText newPassword;
    private Button confirmButton;
    private MainActivity mainActivity;

    private void initComponents(View view) {
        oldPassword = view.findViewById(R.id.old_password);
        newPassword = view.findViewById(R.id.new_password);
        confirmButton = view.findViewById(R.id.confirm_password);
        mainActivity = (MainActivity) getContext();
    }

    public PasswordFragment() {
        // Required empty public constructor
    }

    private void changePassword() {
        String oldPasswordString = oldPassword.getText().toString();
        String newPasswordString = newPassword.getText().toString();
        if (oldPasswordString.isEmpty() || newPasswordString.isEmpty())
            StyleableToast.makeText(mainActivity, "All fields are required", R.style.Failure).show();
        else {
            User user = UserService.getInstance().getUser();
            if (!user.getPassword().equals(oldPasswordString))
                StyleableToast.makeText(mainActivity, "Wrong password", R.style.Failure).show();
            else {
                user.setPassword(newPasswordString);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UserService.getInstance().update();
                        LoginManager.removeCredentials(mainActivity);
                        LoginManager.saveCredentials(mainActivity, user.getUsername(), user.getPassword());
                        mainActivity.runOnUiThread(() -> StyleableToast.makeText(mainActivity, "Password updated", R.style.Success).show());
                        mainActivity.replaceFragment(new AccountFragment(), "Account", true);
                    }
                });
                thread.start();
            }
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
        View view = inflater.inflate(R.layout.fragment_password, container, false);
        initComponents(view);
        confirmButton.setOnClickListener(v -> changePassword());
        return view;
    }
}