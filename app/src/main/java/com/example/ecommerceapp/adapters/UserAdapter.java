package com.example.ecommerceapp.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.activities.MainActivity;
import com.example.ecommerceapp.fragments.CartItemListFragment;
import com.example.ecommerceapp.fragments.OrderListFragment;
import com.example.ecommerceapp.models.Cart;
import com.example.ecommerceapp.models.Order;
import com.example.ecommerceapp.models.Statistic;
import com.example.ecommerceapp.models.User;
import com.example.ecommerceapp.services.CartService;
import com.example.ecommerceapp.services.OrderService;
import com.example.ecommerceapp.services.ProductService;
import com.example.ecommerceapp.services.StatisticService;
import com.example.ecommerceapp.services.UserService;

import java.text.SimpleDateFormat;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private Context context;
    private TextView noUsers;
    private List<User> users;

    public UserAdapter(Context context, TextView noUsers, List<User> users) {
        this.context = context;
        this.noUsers = noUsers;
        this.users = users;
    }

    private void showOrders(User user) {
        MainActivity mainActivity = (MainActivity) context;
        Thread thread = new Thread(() -> {
            List<Order> orders = OrderService.getInstance().findByUserUsername(user.getUsername());
            mainActivity.runOnUiThread(() -> mainActivity.replaceFragment(new OrderListFragment(orders), "Orders", true));
        });
        thread.start();
    }

    private void showCart(User user) {
        MainActivity mainActivity = (MainActivity) context;
        Thread thread = new Thread(() -> {
            Cart cart = CartService.getInstance().findByUserUsername(user.getUsername());
            mainActivity.runOnUiThread(() -> mainActivity.replaceFragment(new CartItemListFragment(cart.getCartItems()), "Cart", true));
        });
        thread.start();
    }

    private void deleteUser(User user, int position) {
        MainActivity mainActivity = (MainActivity) context;
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to remove this user?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Thread thread = new Thread(() -> {
                UserService.getInstance().deleteByUsername(user.getUsername());
                UserService.getInstance().getUsers().remove(user);
                ProductService.getInstance().findAll();
                mainActivity.runOnUiThread(() -> {
                    this.notifyItemRemoved(position);
                    if (getItemCount() == 0) noUsers.setVisibility(View.VISIBLE);
                    Statistic statistic = StatisticService.getInstance().getStatistic();
                    statistic.setTotalClients(statistic.getTotalClients() - 1);
                    StyleableToast.makeText(context, "User removed", R.style.Success).show();
                });
            });
            thread.start();
            dialog.dismiss();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        User user = users.get(position);
        holder.userUsername.setText(user.getUsername());
        holder.userFullname.setText(user.getFirstName() + " " + user.getLastName());
        holder.userEmail.setText("Email : " + user.getEmail());
        holder.userBirthdate.setText("Birthdate : " + sdf.format(user.getBirthDate()));
        holder.userCreationDate.setText("Registration : " + sdf.format(user.getCreationDate()));
        holder.userTotalBought.setText("Spent : $" + user.getTotalBought());
        holder.userOrders.setOnClickListener(v -> showOrders(user));
        holder.userCart.setOnClickListener(v -> showCart(user));
        holder.deleteUserButton.setOnClickListener(v -> deleteUser(user, position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}


