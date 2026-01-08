package com.backend.backend.repository.consultation;

import com.backend.backend.entity.patient.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, UUID> {

    // Get latest 9 consultations for a doctor ordered by updated_at
    @Query("SELECT c FROM Consultation c WHERE c.doctor.userId = :doctorId ORDER BY c.updatedAt DESC")
    List<Consultation> findTop9ByDoctorIdOrderByUpdatedAtDesc(@Param("doctorId") UUID doctorId);

    // Alternative: Get latest 9 consultations with all relationships loaded
    @Query("SELECT c FROM Consultation c " +
            "LEFT JOIN FETCH c.record r " +
            "LEFT JOIN FETCH r.patient p " +
            "LEFT JOIN FETCH c.doctor " +
            "WHERE c.doctor.userId = :doctorId " +
            "ORDER BY c.updatedAt DESC")
    List<Consultation> findLatestConsultationsWithDetails(@Param("doctorId") UUID doctorId);
}