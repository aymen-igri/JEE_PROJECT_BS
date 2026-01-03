package com.backend.backend.service.cabinet;

import com.backend.backend.dto.response.Cabinet.CabinetResponse;
import com.backend.backend.entity.User.Doctor;
import com.backend.backend.entity.practice.Cabinet;
import com.backend.backend.mapper.Cabinet.CabinetMapper;
import com.backend.backend.repository.practice.CabinetRepository;
import com.backend.backend.repository.subscription.SubscriptionRepository;
import com.backend.backend.repository.user.DoctorRepository;
import com.backend.backend.repository.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CabinetService {
    private final CabinetRepository cabinetRepository;
    private final DoctorRepository doctorRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final CabinetMapper cabinetMapper;


    public CabinetService(
            CabinetRepository cabinetRepository,
            DoctorRepository doctorRepository,
            CabinetMapper cabinetMapper
    ){
        this.cabinetRepository = cabinetRepository;
        this.doctorRepository = doctorRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.cabinetMapper = cabinetMapper;
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
