package org.sop.ecommerceapp.repositories;

import org.sop.ecommerceapp.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface OrderItemDao extends JpaRepository<OrderItem, Long> {
    OrderItem findByProductLabelAndOrderReference(String label, String reference);

    int deleteByProductLabelAndOrderReference(String label, String reference);

    int deleteByOrderReference(String reference);
}
