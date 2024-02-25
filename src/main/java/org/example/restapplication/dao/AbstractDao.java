package org.example.restapplication.dao;

import org.example.restapplication.exception.DaoException;
import org.example.restapplication.model.SimpleEntity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDao<K, E extends SimpleEntity> {
    public abstract List<E> findAll() throws DaoException;
    public abstract Optional<E> findById(K id) throws DaoException;
    public abstract Optional<E> delete(K id) throws DaoException;
    public abstract E create(E entity) throws DaoException;
    public abstract Optional<E> update(E entity) throws DaoException;
}
