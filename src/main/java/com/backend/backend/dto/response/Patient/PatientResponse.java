package com.backend.backend.dto.response.Patient;

public record PatientResponse(

        CreatePatientResponse patientResponse,
        CreateMRResponse medicalRecordResponse
) {
}
