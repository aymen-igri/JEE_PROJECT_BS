package com.backend.backend.dto.response.Doctor.DocComplete;

import org.springframework.web.multipart.MultipartFile;

public record AppfilesResponce(
        MultipartFile diplomaDocument,
        MultipartFile licenseDocument,
        MultipartFile cvDocument
) {
}
