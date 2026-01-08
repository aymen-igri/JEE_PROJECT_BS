package com.backend.backend.mapper.Cabinet;

import com.backend.backend.dto.response.Cabinet.CabinetResponse;
import com.backend.backend.entity.practice.Cabinet;
import org.springframework.stereotype.Component;

@Component
public class CabinetMapper {

    public CabinetResponse toCabinetResponse(Cabinet cabinet) {
        return new CabinetResponse(
                cabinet.getCabinetId(),
                cabinet.getName(),
                cabinet.getLogo(),
                cabinet.getSpecialty(),
                cabinet.getDescription(),
                cabinet.getPhone(),
                cabinet.getStatus(),
                cabinet.getDefaultConsultPrice(),
                cabinet.getCreatedBy(),
                cabinet.getCreatedAt()
        );
    }
}
