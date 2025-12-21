
package com.backend.backend.repository.user;

import com.backend.backend.entity.User.User;
import com.backend.backend.enums.EStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {


    Optional<User> findUserByUserId(UUID userId);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByCIN(String cin);


    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.userId = :userId")
    int updatePassword(@Param("userId") UUID userId, @Param("password") String password);

    @Modifying
    @Query("UPDATE User u SET u.username = :username WHERE u.userId = :userId")
    int updateUsername(@Param("userId") UUID userId, @Param("username") String username);


    @Modifying
    @Query("UPDATE User u SET u.email = :email WHERE u.userId = :userId")
    int updateEmail(@Param("userId") UUID userId, @Param("email") String email);

    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.userId = :userId")
    int updateStatus(@Param("userId") UUID userId, @Param("status") EStatus status);

    @Query("SELECT u.userId as userId, u.username as username, u.status as status " +
            "FROM User u WHERE u.userId = :userId")
    Optional<UserStatusProjection> findUserStatusById(@Param("userId") UUID userId);

    @Query("SELECT u.userId as userId, u.email as email, u.phone as phone, u.fullName as fullName " +
            "FROM User u WHERE u.userId = :userId")
    Optional<UserContactProjection> findUserContactById(@Param("userId") UUID userId);

    @Query("SELECT u FROM User u WHERE u.userId = :userId AND TYPE(u) = :userType")
    Optional<User> findByUserIdAndType(@Param("userId") UUID userId,
                                       @Param("userType") Class<? extends User> userType);
}


interface UserStatusProjection {
    UUID getUserId();
    String getUsername();
    EStatus getStatus();
}


interface UserContactProjection {
    UUID getUserId();
    String getEmail();
    String getPhone();
    String getFullName();
}