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
    //    public SushiDto toDto(Sushi sushi) {
//        String name = sushi.getName();
//        SushiType type = sushi.getType();
//        BigDecimal price = sushi.getPrice();
//        String description = sushi.getDescription();
//
//        return new SushiDto(name, type, price, description);
//    }
//
//    public Sushi toSushi(SushiDto sushiDto) {
//        return new Sushi(sushiDto.getName(), sushiDto.getType(), sushiDto.getPrice(), sushiDto.getDescription());
//    }
//    public Sushi toSushi(SushiUpdateDto sushiDto) {
//        return new Sushi(sushiDto.getId(), sushiDto.getName(), sushiDto.getType(), sushiDto.getPrice(), sushiDto.getDescription());
//    }
    SushiDto toDto(Sushi sushi);
    Sushi toSushi(SushiDto dto);
}

