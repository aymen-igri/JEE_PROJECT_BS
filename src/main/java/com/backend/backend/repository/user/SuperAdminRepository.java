
package com.backend.backend.repository.user;


import com.backend.backend.entity.User.SuperAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SuperAdminRepository extends JpaRepository<SuperAdmin, UUID> {


    List<SuperAdmin> findByLevel(Integer level);

    List<SuperAdmin> findByLevelGreaterThanEqual(Integer level);

    Optional<SuperAdmin> findTopByOrderByLevelDesc();

    @Query("SELECT sa.level, COUNT(sa) FROM SuperAdmin sa GROUP BY sa.level")
    List<Object[]> countByLevel();
}

