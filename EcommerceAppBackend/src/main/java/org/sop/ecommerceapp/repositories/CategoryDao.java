package org.sop.ecommerceapp.repositories;

import org.sop.ecommerceapp.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface CategoryDao extends JpaRepository<Category, Long> {
    Category findByLabel(String label);

    int deleteByLabel(String label);
}
