package com.example.ecommerceapp.adapters;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;

public class UserViewHolder extends RecyclerView.ViewHolder {
    TextView userUsername;
    TextView userFullname;
    TextView userEmail;
    TextView userBirthdate;
    TextView userCreationDate;
    TextView userTotalBought;
    Button userOrders;
    Button userCart;
    ImageButton deleteUserButton;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        userUsername = itemView.findViewById(R.id.user_username);
        userFullname = itemView.findViewById(R.id.user_fullname);
        userEmail = itemView.findViewById(R.id.user_email);
        userBirthdate = itemView.findViewById(R.id.user_birthdate);
        userCreationDate = itemView.findViewById(R.id.user_creation_date);
        userTotalBought = itemView.findViewById(R.id.user_total_bought);
        userOrders = itemView.findViewById(R.id.user_orders);
        userCart = itemView.findViewById(R.id.user_cart);
        deleteUserButton = itemView.findViewById(R.id.delete_user_button);
    }
}
