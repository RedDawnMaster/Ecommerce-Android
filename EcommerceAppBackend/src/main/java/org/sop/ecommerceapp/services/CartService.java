package org.sop.ecommerceapp.services;

import java.util.ArrayList;
import java.util.Date;

import org.sop.ecommerceapp.models.*;
import org.sop.ecommerceapp.repositories.CartDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class CartService {
    @Autowired
    private CartDao cartDao;
    @Autowired
    private UserService userService;
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private StatisticService statisticService;
    @Autowired
    private ProductService productService;

    public Cart findByUserUsername(String username) {
        return cartDao.findByUserUsername(username);
    }

    Cart findById(Long id) {
        return cartDao.findById(id).orElse(null);
    }

    @Transactional
    int deleteByUserUsername(String username) {
        int num;
        User user = userService.findByUsername(username);
        num = cartItemService.deleteByCartUserUsername(username);
        num += cartDao.deleteByUserUsername(username);
        user.setCart(null);
        userService.updateInBack(user);
        return num;
    }

    public void save(Cart cart) {
        cartDao.save(cart);
    }

    void update(Cart cart) {
        updateTotal(cart);
        cartDao.save(cart);
    }

    public double getCartTotal(Long id) {
        Cart cart = findById(id);
        return cart.getTotal();
    }

    private void updateTotal(Cart cart) {
        double total = 0;
        if (cart.getCartItems() != null) {
            for (CartItem cartItem : cart.getCartItems()) {
                total += cartItem.getQuantity() * cartItem.getProduct().getPrice();
            }
        }

        cart.setTotal(total);
    }

    @Transactional
    public Response checkout(String username) {
        Cart cart = findByUserUsername(username);
        String error = checkStocks(cart);
        Response response = new Response(error);
        if (!error.equals("Insufficient stocks for : "))
            return response;
        Order order = new Order();
        Statistic statistic = statisticService.findById(1L);
        int totalSoldProducts = 0;
        order.setTotal(cart.getTotal());
        order.setOrderDate(new Date());
        Date deliveryDate = new Date(order.getOrderDate().getTime() + (long) statistic.getDeliveryTime() * 24 * 60 * 60 * 1000);
        order.setDeliveryDate(deliveryDate);
        order.setUser(cart.getUser());
        order.setOrderItems(new ArrayList<OrderItem>());
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            Product product = cartItem.getProduct();
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setProduct(product);
            order.getOrderItems().add(orderItem);
            totalSoldProducts += orderItem.getQuantity();
            product.setNumberOfOrders(product.getNumberOfOrders() + orderItem.getQuantity());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productService.update(product);
        }
        cartItemService.deleteByCartUserUsername(username);
        orderService.save(order);
        statistic.setTotalSales(statistic.getTotalSales() + order.getTotal());
        statistic.setTotalSoldProducts(statistic.getTotalSoldProducts() + totalSoldProducts);
        statisticService.save(statistic);
        response.setMessage("completed");
        return response;
    }

    private String checkStocks(Cart cart) {
        StringBuilder error = new StringBuilder("Insufficient stocks for : ");
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity())
                error.append(product.getLabel()).append(" ");
        }
        return error.toString();
    }
}
