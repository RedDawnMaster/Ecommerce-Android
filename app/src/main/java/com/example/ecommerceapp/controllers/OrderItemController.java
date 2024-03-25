package com.example.ecommerceapp.controllers;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public interface OrderItemController {
    @DELETE("/api/orderItem/{label}/{reference}")
    public Call<Void> refund(@Path("label") String label, @Path("reference") String reference);

}
