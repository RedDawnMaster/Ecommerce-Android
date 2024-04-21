package org.sop.ecommerceapp.services;

import java.util.List;

import org.sop.ecommerceapp.models.ProductsToDelete;
import org.sop.ecommerceapp.repositories.ProductsToDeleteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class ProductsToDeleteService {
    @Autowired
    private ProductsToDeleteDao productsToDeleteDao;

    public ProductsToDelete findById(Long id) {
        return productsToDeleteDao.findById(id).orElse(null);
    }

    public List<ProductsToDelete> findAll() {
        return productsToDeleteDao.findAll();
    }

    public void save(ProductsToDelete productsToDelete) {
        productsToDeleteDao.save(productsToDelete);
    }
}
