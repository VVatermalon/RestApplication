package org.example.restapplication.service.impl;

import org.example.restapplication.exception.DaoException;
import org.example.restapplication.exception.ServiceException;
import org.example.restapplication.model.*;
import org.example.restapplication.dao.SushiDao;
import org.example.restapplication.dao.impl.SushiDaoImpl;
import org.example.restapplication.service.SushiService;
import org.example.restapplication.servlet.dto.SushiDto;
import org.example.restapplication.servlet.mapper.SushiMapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SushiServiceImpl implements SushiService {
    private final SushiDao sushiDao;
    private final SushiMapper mapper;

    public SushiServiceImpl(SushiDao sushiDao, SushiMapper mapper) {
        this.sushiDao = sushiDao;
        this.mapper = mapper;
    }

    public SushiServiceImpl() {
        sushiDao = new SushiDaoImpl();
        mapper = Mappers.getMapper(SushiMapper.class);
    }

    @Override
    public Optional<SushiDto> findById(UUID id) throws ServiceException {
        try {
            var sushiOptional = sushiDao.findById(id);
            return sushiOptional.map(mapper::toDto);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<SushiDto> findAll() throws ServiceException {
        try {
            var list = sushiDao.findAll();
            return list.stream().map(mapper::toDto).toList();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public SushiDto create(SushiDto dto) throws ServiceException {
        var result = createOrUpdate(dto);
        if (result.isEmpty())
            throw new ServiceException();
        return result.get();
    }

    @Override
    public Optional<SushiDto> update(SushiDto dto) throws ServiceException {
        return createOrUpdate(dto);
    }

    @Override
    public Optional<SushiDto> delete(UUID id) throws ServiceException {
        try {
            var sushiOptional = sushiDao.delete(id);
            return sushiOptional.map(mapper::toDto);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<SushiDto> findByType(UUID typeId) throws ServiceException {
        try {
            var list = sushiDao.findSushiByTypeId(typeId);
            return list.stream().map(mapper::toDto).toList();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    private Optional<SushiDto> createOrUpdate(SushiDto dto) throws ServiceException {
        Sushi entity = mapper.toSushi(dto);
        try {
            Optional<Sushi> result;
            if (dto.getId() == null) {
                result = Optional.of(sushiDao.create(entity));
            } else {
                result = sushiDao.update(entity);
            }
            if (result.isEmpty()) {
                return Optional.empty();
            }
            return result.map(mapper::toDto);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
