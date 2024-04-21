package org.sop.ecommerceapp.repositories;

import java.util.List;

import org.sop.ecommerceapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface UserDao extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByEmail(String email);

    List<User> findByUsernameContains(String username);

    List<User> findByEmailContains(String email);

    List<User> findByRole(String role);

    int deleteByUsername(String username);
}
