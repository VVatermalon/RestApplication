package org.example.restapplication.servlet.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.restapplication.model.SushiType;
import org.example.restapplication.servlet.mapper.SushiTypeMapper;

import java.math.BigDecimal;
import java.util.UUID;

public final class SushiDto {
    private UUID id;
    private String name;
    private SushiTypeDto type;
    private BigDecimal price;
    private String description;

    public SushiDto(UUID id, String name, SushiTypeDto type, BigDecimal price, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.description = description;
    }

    public SushiDto() {
    }

    public SushiDto(String name, SushiTypeDto type, BigDecimal price, String description) {
        id = null;
        this.name = name;
        this.type = type;
        this.price = price;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public SushiTypeDto getType() {
        return type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(SushiTypeDto type) {
        this.type = type;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
