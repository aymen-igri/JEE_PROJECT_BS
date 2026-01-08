package com.backend.backend.dto.response.Doctor;

import com.backend.backend.dto.response.Doctor.DocComplete.AppfilesResponce;
import com.backend.backend.dto.response.Doctor.DocComplete.DoctorAppResponce;

public record ApplicationResponse(
        DoctorAppResponce doctorApplication,
        AppfilesResponce applicationFiles
) {
}
