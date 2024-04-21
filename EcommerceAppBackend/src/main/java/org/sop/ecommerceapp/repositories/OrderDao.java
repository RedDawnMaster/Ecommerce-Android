package org.sop.ecommerceapp.repositories;

import java.util.List;

import org.sop.ecommerceapp.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface OrderDao extends JpaRepository<Order, Long> {
    Order findByReference(String reference);

    List<Order> findByUserUsername(String username);

    int deleteByReference(String reference);

    int deleteByUserUsername(String username);
}
