package com.myCompany.projectAuthentication.authentication.repository;

import com.myCompany.projectAuthentication.authentication.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
}
