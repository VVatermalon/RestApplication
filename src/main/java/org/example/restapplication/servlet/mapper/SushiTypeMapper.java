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
    //    public SushiTypeDto toDto(SushiType sushiType) {
//        UUID id = sushiType.getId();
//        String name = sushiType.getTypeName();
//        return new SushiTypeDto(id, name);
//    }
//
//    public SushiTypeDto toDto(String name) {
//        return new SushiTypeDto(name);
//    }
//
//    public SushiType toSushiType(SushiTypeDto sushitypeDto) {
//        return new SushiType(sushitypeDto.getId(), sushitypeDto.getName());
//    }
//
//    public SushiType toSushiType(String name) {
//        return new SushiType(name);
//    }
    SushiTypeDto toDto(SushiType sushiType);
    SushiType toSushiType(SushiTypeDto dto);
}
