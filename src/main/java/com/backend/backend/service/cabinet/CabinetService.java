package com.backend.backend.service.cabinet;

import com.backend.backend.dto.response.Cabinet.CabinetResponse;
import com.backend.backend.entity.User.Doctor;
import com.backend.backend.entity.practice.Cabinet;
import com.backend.backend.mapper.Cabinet.CabinetMapper;
import com.backend.backend.repository.practice.CabinetRepository;
import com.backend.backend.repository.subscription.SubscriptionRepository;
import com.backend.backend.repository.user.DoctorRepository;
import com.backend.backend.repository.user.UserRepository;
import com.backend.backend.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CabinetService {
    private final CabinetRepository cabinetRepository;
    private final DoctorRepository doctorRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final CabinetMapper cabinetMapper;

    public CabinetService(CabinetRepository cabinetRepository, DoctorRepository doctorRepository, SubscriptionRepository subscriptionRepository, CabinetMapper cabinetMapper) {
        this.cabinetRepository = cabinetRepository;
        this.doctorRepository = doctorRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.cabinetMapper = cabinetMapper;
    }
    public CabinetResponse getCabinetByAuthenticatedUser(Authentication authentication) {
        // Extract UUID from CustomUserDetails
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId(); // Assuming CustomUserDetails has getId() method

        // Find the active cabinet created by this user
        List<Cabinet> cabinets = cabinetRepository.findCabinetsByDoctorId(userId);

        if (cabinets.isEmpty()) {
            throw new RuntimeException("No cabinet found for user: " + userId);
        }

        Cabinet cabinet = cabinets.stream()
                .filter(c -> c.getStatus() != null && "ACTIVE".equalsIgnoreCase(c.getStatus()))
                .findFirst()
                .orElse(cabinets.get(0)); // Fallback to first cabinet if no active one found

        return mapToResponse(cabinet);
    }

    public CabinetResponse getCabinetById(UUID cabinetId) {
        Cabinet cabinet = cabinetRepository.findById(cabinetId)
                .orElseThrow(() -> new RuntimeException("Cabinet not found with id: " + cabinetId));

        return mapToResponse(cabinet);
    }
    private CabinetResponse mapToResponse(Cabinet cabinet) {
        return new CabinetResponse(
                cabinet.getCabinetId(),
                cabinet.getName(),
                cabinet.getLogo(),
                cabinet.getAddress(),
                cabinet.getSpecialty(),
                cabinet.getDescription(),



                cabinet.getPhone(),
                cabinet.getStatus(),
                cabinet.getDefaultConsultPrice(),
                cabinet.getDoctor().getFullName(),
                cabinet.getDoctor().getUserId(),
                cabinet.getCreatedAt()
        );
    }


    public Cabinet createCabinet(Cabinet cabinet, UUID doctorId) {
        // Find the doctor
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        cabinet.setDoctor(doctor);
        cabinet.setStatus("Active");

        return cabinetRepository.save(cabinet);
    }
    public boolean hasSubscription(UUID cabinetId){
        return subscriptionRepository.existsByCabinetId(cabinetId);
    }
    public Cabinet getActiveCabinetOrThrow(UUID doctorId) {
        return cabinetRepository.findActiveCabinetByDoctorId(doctorId)
                .orElseThrow(() -> new RuntimeException("No active cabinet found for doctor"));
    }

    public List<CabinetResponse> getAllCabinets() {
        List<Cabinet> cabinets = cabinetRepository.findAll();
        return cabinets.stream().map(cabinetMapper::toCabinetResponse).toList();
    }
}
