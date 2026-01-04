package com.backend.backend.dto.response.Doctor.DocComplete;

import org.springframework.core.io.Resource;

public record AppfilesResponce(
        Resource diplomaDocument,
        Resource licenseDocument,
        Resource cvDocument
) {
}
