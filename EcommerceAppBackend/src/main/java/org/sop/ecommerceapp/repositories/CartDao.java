package org.sop.ecommerceapp.repositories;

import org.sop.ecommerceapp.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CartDao extends JpaRepository<Cart, Long> {
    Cart findByUserUsername(String username);

    int deleteByUserUsername(String username);
}
