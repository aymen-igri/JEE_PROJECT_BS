package com.backend.backend.repository.user;

import com.backend.backend.entity.User.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface AdminRepository extends JpaRepository<Admin, UUID>, JpaSpecificationExecutor<Admin> {

    Admin findAdminByUserId(UUID userId);
    boolean existsByEmail(String email);
    boolean existsByCIN(String cin);
    boolean existsByUsername(String username);

}
