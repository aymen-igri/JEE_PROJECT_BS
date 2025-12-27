package com.backend.backend.config;

import com.backend.backend.entity.User.SuperAdmin;
import com.backend.backend.enums.EGender;
import com.backend.backend.repository.user.SuperAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SuperAdminRepository superAdminRepository;
    private final PasswordEncoder passwordEncoder;

    public void run(String... args){
        SuperAdmin existingAdmin = superAdminRepository.findByUsername("superadmin");

        if(existingAdmin == null){
            SuperAdmin superAdmin = new SuperAdmin();
            superAdmin.setFullName("test super admin");
            superAdmin.setCIN("AA123456");
            superAdmin.setDateOfBirth(LocalDate.of(1980,1,2));
            superAdmin.setGender(EGender.HOMME);
            superAdmin.setAddress("123 Main St, City, Country");
            superAdmin.setEmail("superAdmin654@gmail.com");
            superAdmin.setUsername("superadmin");
            superAdmin.setPassword("{bcrypt}" + passwordEncoder.encode("superadminpassword123"));
            superAdmin.setPhone("600000000");
            superAdmin.setLevel(1);

            superAdminRepository.save(superAdmin);
            System.out.println("Super Admin created.");
        }else{
            System.out.println("Super Admin already exists.");
        }
    }
}
