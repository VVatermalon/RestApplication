package org.example.restapplication.service.impl;

import org.example.restapplication.dao.SushiDao;
import org.example.restapplication.exception.DaoException;
import org.example.restapplication.exception.ServiceException;
import org.example.restapplication.model.Sushi;
import org.example.restapplication.service.SushiService;
import org.example.restapplication.servlet.dto.SushiDto;
import org.example.restapplication.servlet.mapper.SushiMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class SushiServiceImplTest {
    private static final UUID UUID_DEFAULT = UUID.fromString("cfd86bec-947d-4705-ac2b-26d52e5bdbb3");
    private static final SushiDto SUSHI_DTO_DEFAULT = new SushiDto();
    private static final Optional<SushiDto> SUSHI_DTO_OPTIONAL_DEFAULT = Optional.of(SUSHI_DTO_DEFAULT);
    private static final List<SushiDto> SUSHI_DTO_LIST_DEFAULT = new ArrayList<>(List.of(SUSHI_DTO_DEFAULT));
    private static final Sushi SUSHI_DEFAULT = new Sushi();
    private static final Optional<Sushi> SUSHI_OPTIONAL_DEFAULT = Optional.of(SUSHI_DEFAULT);
    private static final List<Sushi> SUSHI_LIST_DEFAULT = new ArrayList<>(List.of(SUSHI_DEFAULT));
    private SushiService service;
    @Mock
    private SushiMapper mapper;
    @Mock
    private SushiDao sushiDao;

    @BeforeEach
    void setUp() {
        service = new SushiServiceImpl(sushiDao, mapper);
    }

    @Test
    void findById() throws Exception {
        when(sushiDao.findById(UUID_DEFAULT)).thenReturn(SUSHI_OPTIONAL_DEFAULT);
        when(mapper.toDto(SUSHI_DEFAULT)).thenReturn(SUSHI_DTO_DEFAULT);

        var actual = service.findById(UUID_DEFAULT);

        verify(sushiDao, times(1)).findById(UUID_DEFAULT);
        verifyNoMoreInteractions(sushiDao);
        verify(mapper, times(1)).toDto(SUSHI_DEFAULT);
        verifyNoMoreInteractions(mapper);
        assertEquals(SUSHI_DTO_OPTIONAL_DEFAULT, actual);
    }

    @Test
    void findAll() throws DaoException, ServiceException {
        when(sushiDao.findAll()).thenReturn(SUSHI_LIST_DEFAULT);
        when(mapper.toDto(SUSHI_DEFAULT)).thenReturn(SUSHI_DTO_DEFAULT);

        var actual = service.findAll();

        verify(sushiDao, times(1)).findAll();
        verifyNoMoreInteractions(sushiDao);
        verify(mapper, times(1)).toDto(SUSHI_DEFAULT);
        verifyNoMoreInteractions(mapper);
        assertEquals(SUSHI_DTO_LIST_DEFAULT, actual);
    }

    @Test
    void create() throws ServiceException, DaoException {
        SUSHI_DTO_DEFAULT.setId(null);
        when(mapper.toSushi(SUSHI_DTO_DEFAULT)).thenReturn(SUSHI_DEFAULT);
        when(sushiDao.create(SUSHI_DEFAULT)).thenReturn(SUSHI_DEFAULT);
        when(mapper.toDto(SUSHI_DEFAULT)).thenReturn(SUSHI_DTO_DEFAULT);

        var actual = service.create(SUSHI_DTO_DEFAULT);

        verify(sushiDao, times(1)).create(SUSHI_DEFAULT);
        verifyNoMoreInteractions(sushiDao);
        verify(mapper, times(1)).toSushi(SUSHI_DTO_DEFAULT);
        verify(mapper, times(1)).toDto(SUSHI_DEFAULT);
        verifyNoMoreInteractions(mapper);
        assertEquals(SUSHI_DTO_DEFAULT, actual);
    }

    @Test
    void update() throws ServiceException, DaoException {
        SUSHI_DTO_DEFAULT.setId(UUID_DEFAULT);
        when(mapper.toSushi(SUSHI_DTO_DEFAULT)).thenReturn(SUSHI_DEFAULT);
        when(sushiDao.update(SUSHI_DEFAULT)).thenReturn(Optional.of(SUSHI_DEFAULT));
        when(mapper.toDto(SUSHI_DEFAULT)).thenReturn(SUSHI_DTO_DEFAULT);

        var actual = service.update(SUSHI_DTO_DEFAULT);

        verify(sushiDao, times(1)).update(SUSHI_DEFAULT);
        verifyNoMoreInteractions(sushiDao);
        verify(mapper, times(1)).toSushi(SUSHI_DTO_DEFAULT);
        verify(mapper, times(1)).toDto(SUSHI_DEFAULT);
        verifyNoMoreInteractions(mapper);
        assertEquals(SUSHI_DTO_DEFAULT, actual.orElse(null));
    }

    @Test
    void delete() throws DaoException, ServiceException {
        when(sushiDao.delete(UUID_DEFAULT)).thenReturn(SUSHI_OPTIONAL_DEFAULT);
        when(mapper.toDto(SUSHI_DEFAULT)).thenReturn(SUSHI_DTO_DEFAULT);

        var actual = service.delete(UUID_DEFAULT);

        verify(sushiDao, times(1)).delete(UUID_DEFAULT);
        verifyNoMoreInteractions(sushiDao);
        verify(mapper, times(1)).toDto(SUSHI_DEFAULT);
        verifyNoMoreInteractions(mapper);
        assertEquals(SUSHI_DTO_OPTIONAL_DEFAULT, actual);
    }

    @Test
    void findByType() throws DaoException, ServiceException {
        when(sushiDao.findSushiByTypeId(UUID_DEFAULT)).thenReturn(SUSHI_LIST_DEFAULT);
        when(mapper.toDto(SUSHI_DEFAULT)).thenReturn(SUSHI_DTO_DEFAULT);

        var actual = service.findByType(UUID_DEFAULT);

        verify(sushiDao, times(1)).findSushiByTypeId(UUID_DEFAULT);
        verifyNoMoreInteractions(sushiDao);
        verify(mapper, times(SUSHI_LIST_DEFAULT.size())).toDto(SUSHI_DEFAULT);
        verifyNoMoreInteractions(mapper);
        assertEquals(SUSHI_DTO_LIST_DEFAULT, actual);
    }
}