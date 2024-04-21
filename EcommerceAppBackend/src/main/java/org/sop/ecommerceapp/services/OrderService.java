package org.sop.ecommerceapp.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sop.ecommerceapp.models.Order;
import org.sop.ecommerceapp.models.OrderItem;
import org.sop.ecommerceapp.models.Statistic;
import org.sop.ecommerceapp.models.User;
import org.sop.ecommerceapp.repositories.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private  StatisticService statisticService;

    Order findByReference(String reference) {
        return orderDao.findByReference(reference);
    }

    private Order findById(Long id) {
        return orderDao.findById(id).orElse(null);
    }

    @Transactional
    public List<Order> findByUserUsername(String username) {
        List<Order> orders = orderDao.findByUserUsername(username);
        for (Order order : orders) {
            checkDelivered(order);
        }
        return orders;
    }

    @Transactional
    public void deleteByReference(String reference) {
        int num;
        Order order = findByReference(reference);
        User user = order.getUser();
        user.getOrders().remove(order);
        userService.updateInBack(user);
        num = orderItemService.deleteByOrderReference(reference);
        num += orderDao.deleteByReference(reference);
    }

    @Transactional
    public void deleteByReferenceEmpty(String reference) {
        Order order = findByReference(reference);
        User user = order.getUser();
        user.getOrders().remove(order);
        userService.updateInBack(user);
        orderDao.deleteByReference(reference);
    }

    @Transactional
    int deleteByUserUsername(String username) {
        int num = 0;
        User user = userService.findByUsername(username);
        List<Order> orders = user.getOrders();
        for (int i =0;i<orders.size();i++){
            num += orderItemService.deleteByOrderReference(orders.get(i).getReference());
        }
        user.setOrders(null);
        userService.updateInBack(user);
        num += orderDao.deleteByUserUsername(username);
        return num;
    }

    @Transactional
    void save(Order order) {
        order = orderDao.save(order);
        User user = order.getUser();
        user.setTotalBought(user.getTotalBought() + order.getTotal());
        if (user.getOrders() == null)
            user.setOrders(new ArrayList<Order>());
        user.getOrders().add(order);
        userService.updateInBack(user);
        for (OrderItem orderItem : order.getOrderItems()) {
            orderItem.setOrder(order);
            orderItemService.save(orderItem);
        }
        String reference = order.getUser().getUsername() + "_" + order.getId();
        order.setReference(reference);
        orderDao.save(order);
    }

    @Transactional
    void update(Order order) {
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            double total = 0;
            for (OrderItem orderItem : order.getOrderItems()) {
                total += orderItem.getQuantity() * orderItem.getProduct().getPrice();
            }
            total += order.getDeletedProductsTotal();
            order.setTotal(total);
        } else if (order.getDeletedProductsTotal() != 0) {
            order.setTotal(order.getDeletedProductsTotal());
        } else {
            deleteByReferenceEmpty(order.getReference());
            return;
        }
        orderDao.save(order);
    }

    void checkDelivered(Order order) {
        Date today = new Date();
        if (today.after(order.getDeliveryDate())) {
            order.setDelivered(true);
            orderDao.save(order);
        }
    }

    public boolean checkRefundable(Long id) {
        Statistic statistic = statisticService.findById(1L);
        Order order = findById(id);
        Date today = new Date();
        Date refundDeadline = new Date(
                order.getDeliveryDate().getTime() + (long) statistic.getRefundPeriod() * 24 * 60 * 60 * 1000);
        return today.before(refundDeadline);
    }
}
