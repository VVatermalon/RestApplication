package org.example.restapplication.service;

import org.example.restapplication.exception.ServiceException;
import org.example.restapplication.model.SimpleEntity;
import org.example.restapplication.model.Sushi;
import org.example.restapplication.servlet.dto.SushiDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SushiService {
    Optional<SushiDto> findById(UUID id) throws ServiceException;

    List<SushiDto> findAll() throws ServiceException;

    SushiDto create(SushiDto dto) throws ServiceException;

    Optional<SushiDto> update(SushiDto dto) throws ServiceException;

    Optional<SushiDto> delete(UUID id) throws ServiceException;

    List<SushiDto> findByType(UUID typeId) throws ServiceException;
}
