package com.luffy.EcommerceBackend.repository;

import com.luffy.EcommerceBackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String Email);

    void deleteByEmail(String email);

}
