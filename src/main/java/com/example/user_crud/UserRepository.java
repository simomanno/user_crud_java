package com.example.user_crud;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(long id);

    List<User> findByNameContainingIgnoreCaseAndSurnameContainingIgnoreCase(String name, String surname);
}
    


