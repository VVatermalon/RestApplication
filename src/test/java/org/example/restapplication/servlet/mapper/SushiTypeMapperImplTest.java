package org.example.restapplication.servlet.mapper;

import org.example.restapplication.model.SushiType;
import org.example.restapplication.servlet.dto.SushiTypeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SushiTypeMapperImplTest {
    private static final UUID UUID_DEFAULT = UUID.randomUUID();
    private static final String NAME_DEFAULT = "Default type name";
    private final SushiType sushiType = new SushiType(UUID_DEFAULT, NAME_DEFAULT);
    private final SushiTypeDto sushiTypeDto = new SushiTypeDto(UUID_DEFAULT, NAME_DEFAULT);

    @Test
    void toDto() {
        var actual = Mappers.getMapper(SushiTypeMapper.class).toDto(sushiType);
        assertAll(() -> assertEquals(UUID_DEFAULT, actual.getId()),
                () -> assertEquals(NAME_DEFAULT, actual.getName()));
    }

    @Test
    void toDtoNull() {
        assertNull(Mappers.getMapper(SushiTypeMapper.class).toDto(null));
    }

    @Test
    void toSushiType() {
        var actual = Mappers.getMapper(SushiTypeMapper.class).toSushiType(sushiTypeDto);
        assertAll(() -> assertEquals(UUID_DEFAULT, actual.getId()),
                () -> assertEquals(NAME_DEFAULT, actual.getName()));
    }

    @Test
    void toSushiTypeNull() {
        assertNull(Mappers.getMapper(SushiTypeMapper.class).toSushiType(null));
    }
}