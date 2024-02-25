package org.example.restapplication.servlet.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.restapplication.servlet.dto.SushiTypeDto;

import java.util.List;

public class JsonMapper {
    public String toJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
    public String toJson(List<SushiTypeDto> sushiDtoList) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(sushiDtoList);
    }
}
