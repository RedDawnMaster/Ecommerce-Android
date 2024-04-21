package org.sop.ecommerceapp.services;

import java.util.List;

import org.sop.ecommerceapp.models.*;
import org.sop.ecommerceapp.repositories.OrderItemDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class OrderItemService {
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private StatisticService statisticService;
    @Autowired
    private UserService userService;

    public OrderItem findByProductLabelAndOrderReference(String label, String reference) {
        return orderItemDao.findByProductLabelAndOrderReference(label, reference);
    }

    OrderItem findById(Long id) {
        return orderItemDao.findById(id).orElse(null);
    }

    List<OrderItem> findAll() {
        return orderItemDao.findAll();
    }

    int deleteByProductLabelAndOrderReference(String label, String reference) {
        OrderItem orderItem = findByProductLabelAndOrderReference(label, reference);
        Order order = orderItem.getOrder();
        order.getOrderItems().remove(orderItem);
        orderService.update(order);
        return orderItemDao.deleteByProductLabelAndOrderReference(label, reference);
    }

    @Transactional
    int deleteByOrderReference(String reference) {
        Order order = orderService.findByReference(reference);
        int num =  orderItemDao.deleteByOrderReference(reference);
        order.setOrderItems(null);
//        orderService.update(order);
        return num;
    }

    @Transactional
    public void refund(String label, String reference) {
        OrderItem orderItem = findByProductLabelAndOrderReference(label, reference);
        Order order = orderItem.getOrder();
        User user = order.getUser();
        Statistic statistic = statisticService.findById(1L);
        Product product = orderItem.getProduct();
        order.getOrderItems().remove(orderItem);
        orderItem.setOrder(null);
        orderService.update(order);
        user.setTotalBought(user.getTotalBought() - (orderItem.getQuantity() * orderItem.getProduct().getPrice()));
        userService.updateInBack(user);
        product.setStock(product.getStock() + orderItem.getQuantity());
        product.setNumberOfOrders(product.getNumberOfOrders() - orderItem.getQuantity());
        productService.update(product);
        statistic
                .setTotalSales(
                        statistic.getTotalSales() - (orderItem.getQuantity() * orderItem.getProduct().getPrice()));
        statistic.setTotalSoldProducts(statistic.getTotalSoldProducts() - orderItem.getQuantity());
        statisticService.save(statistic);
        orderItemDao.deleteById(orderItem.getId());
    }


    void save(OrderItem orderItem) {
        orderItemDao.save(orderItem);
    }
}
