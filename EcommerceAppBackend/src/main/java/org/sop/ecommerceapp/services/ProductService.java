package org.sop.ecommerceapp.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sop.ecommerceapp.models.*;
import org.sop.ecommerceapp.repositories.ProductDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProductService {
    @Autowired
    private ProductDao productDao;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private StatisticService statisticService;
    @Autowired
    private ProductsToDeleteService productsToDeleteService;

    Product findByLabel(String label) {
        return productDao.findByLabel(label);
    }


    public Product findById(Long id) {
        return productDao.findById(id).orElse(null);
    }

    public List<Product> findByLabelContains(String label) {
        return productDao.findByLabelContains(label);
    }

    public List<Product> findByPriceBetween(double lowerBoundPrice,
            double higherBoundPrice) {
        return productDao.findByPriceBetween(lowerBoundPrice, higherBoundPrice);
    }

    @Transactional
    public List<Product> findByCategoryLabel(String label) {
        deleteDeletedProducts();
        return productDao.findByCategoryLabel(label);
    }

    @Transactional
    public List<Product> findAll() {
        deleteDeletedProducts();
        return productDao.findAll();
    }

    public int countByPriceBetween(double lowerBoundPrice, double higherBoundPrice) {
        return this.productDao.countByPriceBetween(lowerBoundPrice, higherBoundPrice);
    }

    public int countByCategoryLabel(String label) {
        return this.productDao.countByCategoryLabel(label);
    }

    @Transactional
    public Response deleteByLabel(String label) {
        Response response = new Response();
        Product product = findByLabel(label);
        Statistic statistic = statisticService.findById(1L);
        Category category = product.getCategory();
        category.getProducts().remove(product);
        product.setDeleted(true);
        categoryService.update(category);
        if (product.getReviews() != null && !product.getReviews().isEmpty())
            reviewService.deleteByProductLabel(label);
        List<CartItem> cartItems = cartItemService.findAll().stream().filter(c -> c.getProduct() == product).toList();
        for (CartItem cartItem : cartItems) {
            cartItemService.deleteByProductLabelAndCartUserUsername(label,
                    cartItem.getCart().getUser().getUsername());
        }
        statistic.setTotalProducts(statistic.getTotalProducts() - 1);
        statisticService.save(statistic);
        productDao.save(product);
        if (checkProductRefundPeriod(product)) {
            ProductsToDelete productsToDelete = productsToDeleteService.findById(1L);
            productsToDelete.getProducts().add(product);
            productsToDeleteService.save(productsToDelete);
            response.setMessage("refund");
            return  response;
        }
        deleteProduct(product);
        response.setMessage("done");
        return response;
    }

    @Transactional
    protected int deleteProduct(Product product) {
        int num = 0;
        ProductsToDelete productsToDelete = productsToDeleteService.findById(1L);
        List<OrderItem> orderItems = orderItemService.findAll().stream().filter(o -> o.getProduct() == product)
                .toList();
        for (OrderItem orderItem : orderItems) {
            Order order = orderItem.getOrder();
            order.setDeletedProductsTotal(
                    order.getDeletedProductsTotal() + orderItem.getQuantity() * orderItem.getProduct().getPrice());
            order.getOrderItems().remove(orderItem);
            orderService.update(order);
            num += orderItemService.deleteByProductLabelAndOrderReference(product.getLabel(), order.getReference());
        }
        productsToDelete.getProducts().remove(product);
        productsToDeleteService.save(productsToDelete);
        num += productDao.deleteByLabel(product.getLabel());
        return num;
    }

    @Transactional
    public Product save(Product product) {
        if (findByLabel(product.getLabel()) != null)
            return null;
        Statistic statistic = statisticService.findById(1L);
        Category category = product.getCategory();
        product.setDeleted(false);
        product = productDao.save(product);
        if (category.getProducts() == null)
            category.setProducts(new ArrayList<>());
        category.getProducts().add(product);
        categoryService.update(category);
        statistic.setTotalProducts(statistic.getTotalProducts() + 1);
        statisticService.save(statistic);
        return product;
    }

    @Transactional
    public Product update(Product product) {
        Product found = findById(product.getId());

        if (!found.getLabel().equals(product.getLabel()) && findByLabel(product.getLabel()) != null)
            return null;
        if (found.getCategory() != product.getCategory()) {
            Category oldCategory = found.getCategory();
            Category newCategory = product.getCategory();
            oldCategory.getProducts().remove(product);
            if (newCategory.getProducts() == null)
                newCategory.setProducts(new ArrayList<>());
            newCategory.getProducts().add(product);
            categoryService.update(oldCategory);
            categoryService.update(newCategory);
        }
        if (product.getReviews() == null)
            product.setReviews(found.getReviews());
        updateStars(product);
        return productDao.save(product);
    }

    private void updateStars(Product product) {
        if (product.getReviews() != null && !product.getReviews().isEmpty()) {
            double sum = 0;
            product.setEvaluationCount(product.getReviews().size());
            for (Review review : product.getReviews()) {
                sum +=  review.getStars();
            }
            sum /= product.getEvaluationCount();
            product.setStars(sum);
        } else {
            product.setStars(0);
            product.setEvaluationCount(0);
        }
    }

    @Transactional
    protected void deleteDeletedProducts() {
        ProductsToDelete productsToDelete = productsToDeleteService.findById(1L);
        if (!productsToDelete.getProducts().isEmpty()) {
            for (int i=0;i<productsToDelete.getProducts().size();i++) {
                Product product = productsToDelete.getProducts().get(i);
                if (!checkProductRefundPeriod(product))
                    deleteProduct(product);
            }
        }
    }

    private boolean checkProductRefundPeriod(Product product) {
        Statistic statistic = statisticService.findById(1L);
        List<OrderItem> orderItems = orderItemService.findAll().stream().filter(o -> o.getProduct() == product)
                .toList();
        for (OrderItem orderItem : orderItems) {
            Date today = new Date();
            Date refundDeadline = new Date(
                    orderItem.getOrder().getDeliveryDate().getTime() + (long) statistic.getRefundPeriod() * 24 * 60 * 60 * 1000);
            if (today.before(refundDeadline))
                return true;
        }
        return false;
    }

}
