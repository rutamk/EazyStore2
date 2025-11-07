package com.eazybytes.eazystore.repository;

import com.eazybytes.eazystore.entity.Role;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Cacheable("roles")
    // ROLE_USER -> CACHE MISS -> DB CALL -> CACHE STORE (ROLE_USER -> Role record) -> CUSTOMER 1
    // ROLE_USER -> CACHE HIT ->  CUSTOMER 2
    // ROLE_ADMIN -> CACHE MISS -> DB CALL -> CACHE STORE (ROLE_ADMIN -> Role record) -> CUSTOMER 3
    Optional<Role> findByName(String name);
}