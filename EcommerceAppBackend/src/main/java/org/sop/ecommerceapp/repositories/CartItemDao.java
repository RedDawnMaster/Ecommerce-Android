package org.sop.ecommerceapp.repositories;

import org.sop.ecommerceapp.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CartItemDao extends JpaRepository<CartItem, Long> {
    CartItem findByProductLabelAndCartUserUsername(String label, String username);

    int deleteByProductLabelAndCartUserUsername(String label, String username);

    int deleteByCartUserUsername(String username);
}
