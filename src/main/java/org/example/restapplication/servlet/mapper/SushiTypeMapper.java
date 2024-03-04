package org.example.restapplication.servlet.mapper;

import org.example.restapplication.model.Sushi;
import org.example.restapplication.model.SushiType;
import org.example.restapplication.servlet.dto.SushiDto;
import org.example.restapplication.servlet.dto.SushiTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.UUID;
@Mapper
public interface SushiTypeMapper {
    SushiTypeDto toDto(SushiType sushiType);
    SushiType toSushiType(SushiTypeDto dto);
}
