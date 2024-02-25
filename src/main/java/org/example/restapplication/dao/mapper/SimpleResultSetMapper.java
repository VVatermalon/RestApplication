package org.example.restapplication.dao.mapper;

import org.example.restapplication.exception.DaoException;
import org.example.restapplication.model.SimpleEntity;

import java.sql.ResultSet;

public interface SimpleResultSetMapper <E extends SimpleEntity>{
    E map(ResultSet resultSet) throws DaoException;
}
