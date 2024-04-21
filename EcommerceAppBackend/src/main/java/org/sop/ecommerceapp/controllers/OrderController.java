package org.sop.ecommerceapp.controllers;

import java.util.List;

import org.sop.ecommerceapp.models.Order;
import org.sop.ecommerceapp.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/{username}")
    public List<Order> findByUserUsername(@PathVariable String username) {
        return orderService.findByUserUsername(username);
    }

    @DeleteMapping("/reference/{reference}")
    public void deleteByReference(@PathVariable String reference){
        orderService.deleteByReference(reference);
    }

    @GetMapping("/id/{id}")
    public boolean checkRefundable(@PathVariable Long id) {
        return this.orderService.checkRefundable(id);
    }
}
