package com.backend.backend.config;

import com.backend.backend.entity.User.Admin;
import com.backend.backend.entity.User.Doctor;
import com.backend.backend.entity.User.Secretary;
import com.backend.backend.entity.User.SuperAdmin;
import com.backend.backend.entity.patient.MedicalRecord;
import com.backend.backend.entity.patient.Patient;
import com.backend.backend.entity.practice.Cabinet;
import com.backend.backend.enums.EGender;
import com.backend.backend.enums.EStatus;
import com.backend.backend.repository.patient.MedicalRecordRepository;
import com.backend.backend.repository.patient.PatientRepository;
import com.backend.backend.repository.practice.CabinetRepository;
import com.backend.backend.repository.user.AdminRepository;
import com.backend.backend.repository.user.DoctorRepository;
import com.backend.backend.repository.user.SecretaryRepository;
import com.backend.backend.repository.user.SuperAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SuperAdminRepository superAdminRepository;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final SecretaryRepository secretaryRepository;
    private final PatientRepository patientRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final CabinetRepository cabinetRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        createSuperAdminIfNotExists();
        Admin admin = createAdminIfNotExists();
        Doctor doctor = createDoctorIfNotExists(admin);
        createCabinetIfNotExists(doctor);
        Secretary secretary = createSecretaryIfNotExists();
        createPatientsIfNotExists(secretary);
    }

    private void createSuperAdminIfNotExists() {
        SuperAdmin existingAdmin = superAdminRepository.findByUsername("superadmin");

        if (existingAdmin == null) {
            SuperAdmin superAdmin = new SuperAdmin();
            superAdmin.setFullName("test super admin");
            superAdmin.setCIN("AA123456");
            superAdmin.setDateOfBirth(LocalDate.of(1980, 1, 2));
            superAdmin.setGender(EGender.HOMME);
            superAdmin.setAddress("123 Main St, City, Country");
            superAdmin.setEmail("superAdmin654@gmail.com");
            superAdmin.setUsername("superadmin");
            superAdmin.setPassword(passwordEncoder.encode("superadminpassword123"));
            superAdmin.setPhone("600000000");
            superAdmin.setLevel(1);

            superAdminRepository.save(superAdmin);
            System.out.println("Super Admin created.");
        } else {
            System.out.println("Super Admin already exists.");
        }
        Doctor existingDoctor = doctorRepository.findByUsername("testdoctor").orElse(null);

        if(existingDoctor == null){
            Doctor doctor = new Doctor();
            doctor.setFullName("Saiid Farhan");
            doctor.setCIN("WC405");
            doctor.setDateOfBirth(LocalDate.of(1985, 5, 15));
            doctor.setGender(EGender.HOMME);
            doctor.setAddress("204, Rue Arahma Quartier Tarchman, Casablanca");
            doctor.setEmail("SaiidFarhaan450@gmail.com");
            doctor.setUsername("testdoctor");
            doctor.setPassword("{bcrypt}" + passwordEncoder.encode("doctor123"));
            doctor.setPhone("0645432313");
            doctor.setStatus(EStatus.ACTIVE);
            doctor.setSpecialty("Cardiology");
            doctor.setLicenseNumber("LIC-2025-001");
            doctorRepository.save(doctor);
            System.out.println("Test Doctor created.");
        } else {
            System.out.println("Test Doctor already exists.");
        }

    }

    private Admin createAdminIfNotExists() {
        if (!adminRepository.existsByUsername("cbinit")) {
            SuperAdmin superAdmin = superAdminRepository.findByUsername("superadmin");

            Admin admin = new Admin();
            admin.setFullName("Cabinet Init Admin");
            admin.setCIN("CB000001");
            admin.setDateOfBirth(LocalDate.of(1985, 5, 15));
            admin.setGender(EGender.HOMME);
            admin.setAddress("456 Admin St, City, Country");
            admin.setEmail("cbinit@gmail.com");
            admin.setUsername("cbinit");
            admin.setPassword(passwordEncoder.encode("cbinitpassword123"));
            admin.setPhone("600000001");
            admin.setStatus(EStatus.ACTIVE);
            admin.setPermissions(List.of("MANAGE_DOCTORS", "MANAGE_CABINETS", "MANAGE_SECRETARIES"));
            admin.setRegisteredBySuperAdmin(superAdmin);

            Admin savedAdmin = adminRepository.save(admin);
            System.out.println("Admin (cbinit) created.");
            return savedAdmin;
        } else {
            System.out.println("Admin (cbinit) already exists.");
            return null;
        }
    }

    private Doctor createDoctorIfNotExists(Admin admin) {
        if (!doctorRepository.existsByUsername("doctor1")) {
            // Get the admin if not passed (in case it already existed)
            if (admin == null) {
                admin = adminRepository.findAll().stream()
                        .filter(a -> "cbinit".equals(a.getUsername()))
                        .findFirst()
                        .orElse(null);
            }

            Doctor doctor = new Doctor();
            doctor.setFullName("Dr. John Smith");
            doctor.setCIN("DR000001");
            doctor.setDateOfBirth(LocalDate.of(1975, 3, 20));
            doctor.setGender(EGender.HOMME);
            doctor.setAddress("789 Doctor Ave, City, Country");
            doctor.setEmail("doctor1@gmail.com");
            doctor.setUsername("doctor1");
            doctor.setPassword(passwordEncoder.encode("doctorpassword123"));
            doctor.setPhone("600000002");
            doctor.setStatus(EStatus.ACTIVE);
            doctor.setSpecialty("General Medicine");
            doctor.setLicenseNumber("LIC-2024-001");
            doctor.setRegisteredByAdmin(admin);

            Doctor savedDoctor = doctorRepository.save(doctor);
            System.out.println("Doctor created.");
            return savedDoctor;
        } else {
            System.out.println("Doctor already exists.");
            return doctorRepository.findByLicenseNumber("LIC-2024-001").orElse(null);
        }
    }

    private void createCabinetIfNotExists(Doctor doctor) {
        if (doctor != null && !cabinetRepository.existsByDoctorUserIdAndStatus(doctor.getUserId(), "Active")) {
            Cabinet cabinet = Cabinet.builder()
                    .name("Cabinet Médical Dr. Smith")
                    .specialty("General Medicine")
                    .description("Cabinet médical généraliste offrant des consultations de médecine générale")
                    .address("789 Doctor Ave, City, Country")
                    .phone("600000002")
                    .status("Active")
                    .defaultConsultPrice(new BigDecimal("200.00"))
                    .doctor(doctor)
                    .build();

            cabinetRepository.save(cabinet);
            System.out.println("Cabinet created for doctor.");
        } else {
            System.out.println("Cabinet already exists or doctor is null.");
        }
    }

    private Secretary createSecretaryIfNotExists() {
        if (!secretaryRepository.existsByUsername("secretary1")) {
            Secretary secretary = new Secretary();
            secretary.setFullName("Marie Dupont");
            secretary.setCIN("SE000001");
            secretary.setDateOfBirth(LocalDate.of(1990, 7, 10));
            secretary.setGender(EGender.FEMME);
            secretary.setAddress("321 Secretary Lane, City, Country");
            secretary.setEmail("secretary1@gmail.com");
            secretary.setUsername("secretary1");
            secretary.setPassword(passwordEncoder.encode("secretarypassword123"));
            secretary.setPhone("600000003");
            secretary.setStatus(EStatus.ACTIVE);

            Secretary savedSecretary = secretaryRepository.save(secretary);
            System.out.println("Secretary created.");
            return savedSecretary;
        }
        System.out.println("Secretary already exists.");
        return secretaryRepository.findByUsername("secretary1");
    }

    private void createPatientsIfNotExists(Secretary secretary) {
        // Get the secretary if not passed
        if (secretary == null) {
            secretary = secretaryRepository.findByUsername("secretary1");
        }

        // Check if patients already exist by checking if any patient with our test CINs exist
        for (int i = 1; i <= 10; i++) {
            String cin = String.format("PT%06d", i);

            if (!patientRepository.existsByCin(cin)) {
                Patient patient = new Patient();
                patient.setCin(cin);
                patient.setFirstName("Patient" + i);
                patient.setLastName("TestLastName" + i);
                patient.setDateOfBirth(LocalDate.of(1990 + (i % 10), (i % 12) + 1, (i % 28) + 1));
                patient.setGender(i % 2 == 0 ? EGender.FEMME : EGender.HOMME);
                patient.setPhone(String.format("6%08d", 10000 + i));
                patient.setAddress(String.format("%d Patient Street, City, Country", 100 + i));
                patient.setRegisteredBySecretary(secretary);
                patient.setRegistrationDate(LocalDate.now());

                Patient savedPatient = patientRepository.save(patient);

                // Create empty medical record for the patient
                MedicalRecord medicalRecord = new MedicalRecord();
                medicalRecord.setPatient(savedPatient);
                medicalRecord.setAllergies(new ArrayList<>());
                medicalRecord.setBloodType(null);
                medicalRecord.setChronicDiseases(null);
                medicalRecord.setFamilyHistory(null);
                medicalRecord.setPastSurgeries(new ArrayList<>());

                medicalRecordRepository.save(medicalRecord);
                System.out.println("Patient " + i + " with empty medical record created.");
            } else {
                System.out.println("Patient with CIN " + cin + " already exists.");
            }
        }
    }
}

