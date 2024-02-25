package org.example.restapplication.dao.mapper.impl;

import org.example.restapplication.dao.mapper.SimpleResultSetMapper;
import org.example.restapplication.exception.DaoException;
import org.example.restapplication.model.SushiType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public enum SushiTypeMapperImpl implements SimpleResultSetMapper<SushiType> {
    INSTANCE;
    private static final String ID_LABEL = "type_id";
    private static final String NAME_LABEL = "type_name";

    public SushiType map(ResultSet resultSet) throws DaoException {
        SushiType type;
        try {
            UUID id = resultSet.getObject(ID_LABEL, UUID.class);
            String name = resultSet.getString(NAME_LABEL);
            type = new SushiType(id, name);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return type;
    }
}
