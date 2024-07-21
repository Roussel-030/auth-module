package com.myCompany.projectAuthentication.authentication.repository;

import com.myCompany.projectAuthentication.authentication.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}
