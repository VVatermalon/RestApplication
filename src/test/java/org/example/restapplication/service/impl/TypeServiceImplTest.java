package org.example.restapplication.service.impl;

import org.example.restapplication.dao.SushiTypeDao;
import org.example.restapplication.exception.DaoException;
import org.example.restapplication.exception.ServiceException;
import org.example.restapplication.model.SushiType;
import org.example.restapplication.servlet.dto.SushiTypeDto;
import org.example.restapplication.servlet.mapper.SushiTypeMapper;
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
class TypeServiceImplTest {
    private static final UUID UUID_DEFAULT = UUID.fromString("cfd86bec-947d-4705-ac2b-26d52e5bdbb3");
    private static final SushiTypeDto SUSHI_TYPE_DTO_DEFAULT = new SushiTypeDto();
    private static final Optional<SushiTypeDto> SUSHI_TYPE_DTO_OPTIONAL_DEFAULT = Optional.of(SUSHI_TYPE_DTO_DEFAULT);
    private static final List<SushiTypeDto> SUSHI_TYPE_DTO_LIST_DEFAULT = new ArrayList<>(List.of(SUSHI_TYPE_DTO_DEFAULT));
    private static final SushiType SUSHI_TYPE_DEFAULT = new SushiType();
    private static final Optional<SushiType> SUSHI_TYPE_OPTIONAL_DEFAULT = Optional.of(SUSHI_TYPE_DEFAULT);
    private static final List<SushiType> SUSHI_TYPE_LIST_DEFAULT = new ArrayList<>(List.of(SUSHI_TYPE_DEFAULT));
    private TypeServiceImpl service;

    @Mock
    private SushiTypeDao sushiTypeDao;
    @Mock
    private SushiTypeMapper mapper;

    @BeforeEach
    void setUp() {
        service = new TypeServiceImpl(sushiTypeDao, mapper);
    }

    @Test
    void findById() throws DaoException, ServiceException {
        when(sushiTypeDao.findById(UUID_DEFAULT)).thenReturn(SUSHI_TYPE_OPTIONAL_DEFAULT);
        when(mapper.toDto(SUSHI_TYPE_DEFAULT)).thenReturn(SUSHI_TYPE_DTO_DEFAULT);

        var actual = service.findById(UUID_DEFAULT);

        verify(sushiTypeDao, times(1)).findById(UUID_DEFAULT);
        verifyNoMoreInteractions(sushiTypeDao);
        verify(mapper, times(1)).toDto(SUSHI_TYPE_DEFAULT);
        verifyNoMoreInteractions(mapper);
        assertEquals(SUSHI_TYPE_DTO_OPTIONAL_DEFAULT, actual);
    }
    @Test
    void findByIdServiceException() throws DaoException {
        when(sushiTypeDao.findById(UUID_DEFAULT)).thenThrow(new DaoException());
        assertThrows(ServiceException.class, () -> {
            service.findById(UUID_DEFAULT);
        });
    }

    @Test
    void findAll() throws DaoException, ServiceException {
        when(sushiTypeDao.findAll()).thenReturn(SUSHI_TYPE_LIST_DEFAULT);
        when(mapper.toDto(SUSHI_TYPE_DEFAULT)).thenReturn(SUSHI_TYPE_DTO_DEFAULT);

        var actual = service.findAll();

        verify(sushiTypeDao, times(1)).findAll();
        verifyNoMoreInteractions(sushiTypeDao);
        verify(mapper, times(SUSHI_TYPE_LIST_DEFAULT.size())).toDto(SUSHI_TYPE_DEFAULT);
        verifyNoMoreInteractions(mapper);
        assertEquals(SUSHI_TYPE_DTO_LIST_DEFAULT, actual);
    }
    @Test
    void findAllServiceException() throws DaoException {
        when(sushiTypeDao.findAll()).thenThrow(new DaoException());
        assertThrows(ServiceException.class, () -> {
            service.findAll();
        });
    }

    @Test
    void create() throws DaoException, ServiceException {
        when(mapper.toSushiType(SUSHI_TYPE_DTO_DEFAULT)).thenReturn(SUSHI_TYPE_DEFAULT);
        when(sushiTypeDao.create(SUSHI_TYPE_DEFAULT)).thenReturn(SUSHI_TYPE_DEFAULT);
        when(mapper.toDto(SUSHI_TYPE_DEFAULT)).thenReturn(SUSHI_TYPE_DTO_DEFAULT);

        var actual = service.create(SUSHI_TYPE_DTO_DEFAULT);

        verify(sushiTypeDao, times(1)).create(SUSHI_TYPE_DEFAULT);
        verifyNoMoreInteractions(sushiTypeDao);
        verify(mapper, times(1)).toSushiType(SUSHI_TYPE_DTO_DEFAULT);
        verify(mapper, times(1)).toDto(SUSHI_TYPE_DEFAULT);
        verifyNoMoreInteractions(mapper);
        assertEquals(SUSHI_TYPE_DTO_DEFAULT, actual);
    }
    @Test
    void createServiceException() throws DaoException {
        when(mapper.toSushiType(SUSHI_TYPE_DTO_DEFAULT)).thenReturn(SUSHI_TYPE_DEFAULT);
        when(sushiTypeDao.create(SUSHI_TYPE_DEFAULT)).thenThrow(new DaoException());
        assertThrows(ServiceException.class, () -> {
            service.create(SUSHI_TYPE_DTO_DEFAULT);
        });
    }

    @Test
    void update() throws DaoException, ServiceException {
        when(mapper.toSushiType(SUSHI_TYPE_DTO_DEFAULT)).thenReturn(SUSHI_TYPE_DEFAULT);
        when(sushiTypeDao.update(SUSHI_TYPE_DEFAULT)).thenReturn(SUSHI_TYPE_OPTIONAL_DEFAULT);
        when(mapper.toDto(SUSHI_TYPE_DEFAULT)).thenReturn(SUSHI_TYPE_DTO_DEFAULT);

        var actual = service.update(SUSHI_TYPE_DTO_DEFAULT);

        verify(sushiTypeDao, times(1)).update(SUSHI_TYPE_DEFAULT);
        verifyNoMoreInteractions(sushiTypeDao);
        verify(mapper, times(1)).toSushiType(SUSHI_TYPE_DTO_DEFAULT);
        verify(mapper, times(1)).toDto(SUSHI_TYPE_DEFAULT);
        verifyNoMoreInteractions(mapper);
        assertEquals(SUSHI_TYPE_DTO_OPTIONAL_DEFAULT, actual);
    }
    @Test
    void updateServiceException() throws DaoException {
        when(mapper.toSushiType(SUSHI_TYPE_DTO_DEFAULT)).thenReturn(SUSHI_TYPE_DEFAULT);
        when(sushiTypeDao.update(SUSHI_TYPE_DEFAULT)).thenThrow(new DaoException());
        assertThrows(ServiceException.class, () -> {
            service.update(SUSHI_TYPE_DTO_DEFAULT);
        });
    }

    @Test
    void delete() throws DaoException, ServiceException {
        when(sushiTypeDao.delete(UUID_DEFAULT)).thenReturn(SUSHI_TYPE_OPTIONAL_DEFAULT);
        when(mapper.toDto(SUSHI_TYPE_DEFAULT)).thenReturn(SUSHI_TYPE_DTO_DEFAULT);

        var actual = service.delete(UUID_DEFAULT);

        verify(sushiTypeDao, times(1)).delete(UUID_DEFAULT);
        verifyNoMoreInteractions(sushiTypeDao);
        verify(mapper, times(1)).toDto(SUSHI_TYPE_DEFAULT);
        verifyNoMoreInteractions(mapper);
        assertEquals(SUSHI_TYPE_DTO_OPTIONAL_DEFAULT, actual);
    }
    @Test
    void deleteServiceException() throws DaoException {
        when(sushiTypeDao.delete(UUID_DEFAULT)).thenThrow(new DaoException());
        assertThrows(ServiceException.class, () -> {
            service.delete(UUID_DEFAULT);
        });
    }
}
