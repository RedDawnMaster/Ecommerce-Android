package org.sop.ecommerceapp.services;

import java.util.ArrayList;
import java.util.List;

import org.sop.ecommerceapp.models.Category;
import org.sop.ecommerceapp.models.Product;
import org.sop.ecommerceapp.repositories.CategoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class CategoryService {
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private ProductService productService;

    private Category findByLabel(String label) {
        return categoryDao.findByLabel(label);
    }

    private Category findById(Long id) {
        return categoryDao.findById(id).orElse(null);
    }

    public List<Category> findAll() {
        return categoryDao.findAll();
    }

    @Transactional
    public int deleteByLabel(String label) {
        Category category = findByLabel(label);
        if (category.getProducts() != null && category.getProducts().size() != 0) {
            Category other = findByLabel("Other");
            if (other == null) {
                other = new Category();
                other.setLabel("Other");
                other.setProducts(category.getProducts());
            } else {
                if (other.getProducts() == null)
                    other.setProducts(new ArrayList<Product>());
                for (Product product : category.getProducts()) {
                    other.getProducts().add(product);
                }
            }
            other.setTotal(other.getProducts().size());
            other = categoryDao.save(other);
            for (Product product : category.getProducts()) {
                product.setCategory(other);
                productService.update(product);
            }
        }
        return categoryDao.deleteByLabel(label);
    }

    public Category save(Category category) {
        if (findByLabel(category.getLabel()) != null)
            return null;
        return categoryDao.save(category);
    }

    public int update(Category category) {
        Category found = findById(category.getId());
        if (!found.getLabel().equals(category.getLabel()) && findByLabel(category.getLabel()) != null)
            return -1;
        if (category.getProducts() == null)
            category.setProducts(found.getProducts());
        category.setTotal(category.getProducts().size());
        categoryDao.save(category);
        return 1;
    }
}
