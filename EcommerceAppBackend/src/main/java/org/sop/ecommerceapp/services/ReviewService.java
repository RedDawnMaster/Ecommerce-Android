package org.sop.ecommerceapp.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sop.ecommerceapp.models.Product;
import org.sop.ecommerceapp.models.Review;
import org.sop.ecommerceapp.models.User;
import org.sop.ecommerceapp.repositories.ReviewDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ReviewService {
    @Autowired
    private ReviewDao reviewDao;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;

    private Review findByUserUsernameAndProductLabel(String username, String label) {
        return reviewDao.findByUserUsernameAndProductLabel(username, label);
    }

    public List<Review> findByProductLabelAndUserUsernameContains(String label, String username) {
        if (username.equals("empty"))
            return findByProductLabel(label);
        return reviewDao.findByProductLabelAndUserUsernameContains(label, username);
    }

    public List<Review> findByProductLabel(String label) {
        return reviewDao.findByProductLabel(label);
    }

    @Transactional
    public void deleteByUserUsernameAndProductLabel(String username, String label) {
        Review review = findByUserUsernameAndProductLabel(username, label);
        User user = review.getUser();
        Product product = review.getProduct();
        user.getReviews().remove(review);
        userService.updateInBack(user);
        product.getReviews().remove(review);
        productService.update(product);
        reviewDao.deleteByUserUsernameAndProductLabel(username, label);
    }

    @Transactional
    int deleteByUserUsername(String username) {
        User user = userService.findByUsername(username);
        for (Review review : user.getReviews()) {
            Product product = productService.findByLabel(review.getProduct().getLabel());
            product.getReviews().remove(review);
            productService.update(product);
        }
        user.setReviews(null);
        userService.updateInBack(user);
        return reviewDao.deleteByUserUsername(username);
    }

    @Transactional
    int deleteByProductLabel(String label) {
        Product product = productService.findByLabel(label);
        for (Review review : product.getReviews()) {
            User user = userService.findByUsername(review.getUser().getUsername());
            user.getReviews().remove(review);
            userService.updateInBack(user);
        }
        product.setReviews(null);
        productService.update(product);
        return reviewDao.deleteByProductLabel(label);
    }

    @Transactional
    public Review save(Review review) {
        User user = review.getUser();
        Product product = review.getProduct();
        if (findByUserUsernameAndProductLabel(user.getUsername(), product.getLabel()) != null)
            return null;
        review.setDate(new Date());
        review.setUser(user);
        review = reviewDao.save(review);
        if (user.getReviews() == null)
            user.setReviews(new ArrayList<>());
        user.getReviews().add(review);
        userService.updateInBack(user);
        if (product.getReviews() == null)
            product.setReviews(new ArrayList<>());
        product.getReviews().add(review);
        productService.update(product);
        return review;
    }

    public void update(Review review) {
        reviewDao.save(review);
        productService.update(review.getProduct());
    }
}
