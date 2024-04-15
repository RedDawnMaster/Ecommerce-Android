package com.example.ecommerceapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.adapters.UserAdapter;
import com.example.ecommerceapp.models.User;

import java.util.ArrayList;
import java.util.List;


public class UsersListFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private TextView noUsers;
    private List<User> users;

    public UsersListFragment() {
        // Required empty public constructor
    }

    public UsersListFragment(List<User> users) {
        if (users == null) this.users = new ArrayList<>();
        else this.users = users;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users_list, container, false);
        noUsers = view.findViewById(R.id.no_users);
        recyclerView = view.findViewById(R.id.users_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        userAdapter = new UserAdapter(getContext(), noUsers, users);
        recyclerView.setAdapter(userAdapter);
        if (users.isEmpty()) noUsers.setVisibility(View.VISIBLE);
        return view;
    }

    public UserAdapter getUserAdapter() {
        return userAdapter;
    }
}