package org.sop.ecommerceapp.repositories;

import org.sop.ecommerceapp.models.ProductsToDelete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface ProductsToDeleteDao extends JpaRepository<ProductsToDelete, Long> {

}
