package org.example.restapplication.servlet.mapper;

import org.example.restapplication.model.Sushi;
import org.example.restapplication.model.SushiType;
import org.example.restapplication.servlet.dto.SushiDto;
import org.example.restapplication.servlet.dto.SushiTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
@Mapper
public interface SushiMapper {
    SushiDto toDto(Sushi sushi);
    Sushi toSushi(SushiDto dto);
}

