package com.pszemek.mtjworldcupstandings.repository;

import com.pszemek.mtjworldcupstandings.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByUsername(String username);
}
