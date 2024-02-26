package org.example.restapplication.servlet.mapper;

import org.example.restapplication.model.Sushi;
import org.example.restapplication.model.SushiType;
import org.example.restapplication.servlet.dto.SushiDto;
import org.example.restapplication.servlet.dto.SushiTypeDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


class SushiMapperImplTest {

    private static final UUID UUID_DEFAULT = UUID.randomUUID();
    private static final String NAME_DEFAULT = "Default sushi name";
    private static final BigDecimal PRICE_DEFAULT = BigDecimal.ZERO;
    private static final String DESCRIPTION_DEFAULT = "Default sushi description";
    private final SushiTypeDto sushiTypeDto = new SushiTypeDto(UUID_DEFAULT, NAME_DEFAULT);
    private final SushiType sushiType = new SushiType(UUID_DEFAULT, NAME_DEFAULT);
    private final Sushi sushi = new Sushi(UUID_DEFAULT, NAME_DEFAULT, sushiType, PRICE_DEFAULT, DESCRIPTION_DEFAULT);
    private final SushiDto sushiDto = new SushiDto(UUID_DEFAULT, NAME_DEFAULT, sushiTypeDto, PRICE_DEFAULT, DESCRIPTION_DEFAULT);

    @Test
    void toDto() {
        SushiDto actual = Mappers.getMapper(SushiMapper.class).toDto(sushi);

        assertEquals(UUID_DEFAULT, actual.getId());
        assertEquals(NAME_DEFAULT, actual.getName());
        assertEquals(PRICE_DEFAULT, actual.getPrice());
        assertEquals(DESCRIPTION_DEFAULT, actual.getDescription());
        assertEquals(sushiTypeDto.getId(), actual.getType().getId());
        assertEquals(sushiTypeDto.getName(), actual.getType().getName());
    }

    @Test
    void toDtoNull() {
        assertNull(Mappers.getMapper(SushiMapper.class).toDto(null));
    }

    @Test
    void toSushi() {
        Sushi actual = Mappers.getMapper(SushiMapper.class).toSushi(sushiDto);

        assertEquals(UUID_DEFAULT, actual.getId());
        assertEquals(NAME_DEFAULT, actual.getName());
        assertEquals(PRICE_DEFAULT, actual.getPrice());
        assertEquals(DESCRIPTION_DEFAULT, actual.getDescription());
        assertEquals(sushiType, actual.getType());
        assertEquals(sushiTypeDto.getId(), actual.getType().getId());
        assertEquals(sushiTypeDto.getName(), actual.getType().getName());
    }

    @Test
    void toSushiNull() {
        assertNull(Mappers.getMapper(SushiMapper.class).toSushi(null));
    }
}
