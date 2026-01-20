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
}
