package org.example.restapplication.service;

import org.example.restapplication.exception.ServiceException;
import org.example.restapplication.model.SushiType;
import org.example.restapplication.servlet.dto.SushiTypeDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TypeService {
    Optional<SushiTypeDto> findById(UUID id) throws ServiceException;

    List<SushiTypeDto> findAll() throws ServiceException;

    SushiTypeDto create(SushiTypeDto dto) throws ServiceException;

    Optional<SushiTypeDto> update(SushiTypeDto dto) throws ServiceException;

    Optional<SushiTypeDto> delete(UUID id) throws ServiceException;
}
