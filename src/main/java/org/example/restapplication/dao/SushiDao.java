package org.example.restapplication.dao;

import org.example.restapplication.exception.DaoException;
import org.example.restapplication.model.Sushi;

import java.util.List;
import java.util.UUID;

public abstract class SushiDao extends AbstractDao<UUID, Sushi> {
    public abstract List<Sushi> findSushiByTypeId(UUID id) throws DaoException;
}
