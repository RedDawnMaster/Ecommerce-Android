package org.sop.ecommerceapp.controllers;

import org.sop.ecommerceapp.models.Order;
import org.sop.ecommerceapp.models.OrderItem;
import org.sop.ecommerceapp.services.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/orderItem")
public class OrderItemController {
    @Autowired
    private OrderItemService orderItemService;

    @GetMapping("{label}/{reference}")
    private OrderItem findByProductLabelAndOrderReference(@PathVariable String label,@PathVariable String reference) {
        return orderItemService.findByProductLabelAndOrderReference(label, reference);
    }

    @DeleteMapping("/{label}/{reference}")
    public void refund(@PathVariable String label, @PathVariable String reference) {
        orderItemService.refund(label, reference);
    }
}
