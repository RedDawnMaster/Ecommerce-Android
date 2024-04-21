package org.sop.ecommerceapp.repositories;

import java.util.List;

import org.sop.ecommerceapp.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface ReviewDao extends JpaRepository<Review, Long> {
    Review findByUserUsernameAndProductLabel(String username, String label);

    List<Review> findByProductLabelAndUserUsernameContains(String label, String username);

    List<Review> findByProductLabel(String label);

    int deleteByUserUsernameAndProductLabel(String username, String label);

    int deleteByUserUsername(String username);

    int deleteByProductLabel(String label);
}
