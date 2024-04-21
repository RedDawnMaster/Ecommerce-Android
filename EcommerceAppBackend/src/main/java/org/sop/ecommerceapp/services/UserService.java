package org.sop.ecommerceapp.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sop.ecommerceapp.models.Cart;
import org.sop.ecommerceapp.models.Product;
import org.sop.ecommerceapp.models.Statistic;
import org.sop.ecommerceapp.models.User;
import org.sop.ecommerceapp.repositories.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ProductService productService;
    @Autowired
    private StatisticService statisticService;

    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    private User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    public User findById(Long id) {
        return userDao.findById(id).orElse(null);
    }

    public List<User> findByUsernameContains(String username) {
        return userDao.findByUsernameContains(username);
    }

    public List<User> findByEmailContains(String email) {
        return this.userDao.findByEmailContains(email);
    }

    public List<User> findByRole(String role) {
        return this.userDao.findByRole(role);
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

    @Transactional
    public void deleteByUsername(String username) {
        User user = findByUsername(username);
        Statistic statistic = statisticService.findById(1L);
        statistic.setTotalClients(statistic.getTotalClients() - 1);
        statisticService.save(statistic);
        cartService.deleteByUserUsername(username);
        if (user.getOrders() != null && !user.getOrders().isEmpty())
            orderService.deleteByUserUsername(username);
        if (user.getReviews() != null && !user.getReviews().isEmpty()) reviewService.deleteByUserUsername(username);
        userDao.deleteByUsername(username);
    }

    @Transactional
    public User register(User user) {
        if (findByUsername(user.getUsername()) != null || findByEmail(user.getEmail()) != null)
            return null;
        if (user.getRole() == null) {
            user.setRole("USER");
            Statistic statistic = statisticService.findById(1L);
            statistic.setTotalClients(statistic.getTotalClients() + 1);
            statisticService.save(statistic);
        }
        user.setCreationDate(new Date());
        user.setCart(new Cart());
        user = userDao.save(user);
        user.getCart().setUser(user);
        cartService.save(user.getCart());
        return user;
    }

    @Transactional
    public User updateInBack(User user) {
        return userDao.save(user);
    }

    @Transactional
    public User update(User user) {
        User found = findById(user.getId());
        if ((!found.getUsername().equals(user.getUsername()) && findByUsername(user.getUsername()) != null)
                || (!found.getEmail().equals(user.getEmail()) && findByEmail(user.getEmail()) != null))
            return null;
        if (user.getOrders() == null)
            user.setOrders(found.getOrders());
        if (user.getCart() == null)
            user.setCart(found.getCart());
        if (user.getReviews() == null)
            user.setReviews(found.getReviews());
        return userDao.save(user);
    }



    public User login(String username, String password) {
        User user = findByUsername(username);
        if (user == null || !user.getPassword().equals(password))
            return null;
        return user;
    }

    public int addToWishList(String username, String label) {
        User user = findByUsername(username);
        Product product = productService.findByLabel(label);
        if (user.getWishList() == null)
            user.setWishList(new ArrayList<Product>());
        if (user.getWishList().contains(product))
            return -1;
        user.getWishList().add(product);
        userDao.save(user);
        return 1;
    }

    public void removeFromWishList(String username, String label) {
        User user = findByUsername(username);
        Product product = productService.findByLabel(label);
        user.getWishList().remove(product);
        userDao.save(user);
    }
}
