package org.example.restapplication.service.impl;

import org.example.restapplication.dao.SushiTypeDao;
import org.example.restapplication.dao.impl.SushiTypeDaoImpl;
import org.example.restapplication.exception.DaoException;
import org.example.restapplication.exception.ServiceException;
import org.example.restapplication.model.SushiType;
import org.example.restapplication.service.TypeService;
import org.example.restapplication.servlet.dto.SushiTypeDto;
import org.example.restapplication.servlet.mapper.SushiTypeMapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TypeServiceImpl implements TypeService {
    private final SushiTypeDao sushiTypeDao;
    private final SushiTypeMapper mapper;

    public TypeServiceImpl(SushiTypeDao sushiTypeDao, SushiTypeMapper mapper) {
        this.sushiTypeDao = sushiTypeDao;
        this.mapper = mapper;
    }

    public TypeServiceImpl() {
        this.sushiTypeDao = new SushiTypeDaoImpl();
        this.mapper = Mappers.getMapper(SushiTypeMapper.class);
    }

    @Override
    public Optional<SushiTypeDto> findById(UUID id) throws ServiceException {
        try {
            var result = sushiTypeDao.findById(id);
            return result.map(mapper::toDto);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<SushiTypeDto> findAll() throws ServiceException {
        try {
            return sushiTypeDao.findAll().stream().map(mapper::toDto).toList();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public SushiTypeDto create(SushiTypeDto dto) throws ServiceException {
        SushiType entity = mapper.toSushiType(dto);
        try {
            return mapper.toDto(sushiTypeDao.create(entity));
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Optional<SushiTypeDto> update(SushiTypeDto dto) throws ServiceException {
        SushiType entity = mapper.toSushiType(dto);
        try {
            var result = sushiTypeDao.update(entity);
            return result.map(mapper::toDto);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Optional<SushiTypeDto> delete(UUID id) throws ServiceException {
        try {
            var result = sushiTypeDao.delete(id);
            return result.map(mapper::toDto);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
