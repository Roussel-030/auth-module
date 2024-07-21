package com.myCompany.projectAuthentication.authentication.repository;

import com.myCompany.projectAuthentication.authentication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
