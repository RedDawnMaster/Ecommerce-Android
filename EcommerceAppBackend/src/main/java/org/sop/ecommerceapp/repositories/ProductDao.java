package org.sop.ecommerceapp.repositories;

import java.util.List;

import org.sop.ecommerceapp.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface ProductDao extends JpaRepository<Product, Long> {
        Product findByLabel(String label);

        List<Product> findByLabelContains(String label);

        List<Product> findByPriceBetween(double lowerBoundPrice, double higherBoundPrice);

        List<Product> findByCategoryLabel(String label);

        int countByPriceBetween(double lowerBoundPrice, double higherBoundPrice);

        int countByCategoryLabel(String label);

        int deleteByLabel(String label);
}
