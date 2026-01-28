package com.example.user_crud;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User save(User user);

    Optional<User> findById(long id);

    List<User> findByNameAndSurname(String name, String surname);
}
    


