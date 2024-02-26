package org.example.restapplication.dao.mapper.impl;

import org.example.restapplication.dao.mapper.SimpleResultSetMapper;
import org.example.restapplication.exception.DaoException;
import org.example.restapplication.model.Sushi;
import org.example.restapplication.model.SushiType;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public enum SushiMapperImpl implements SimpleResultSetMapper<Sushi> {
    INSTANCE;
    private static final String ID_LABEL = "sushi_id";
    private static final String NAME_LABEL = "sushi_name";
    private static final String PRICE_LABEL = "price";
    private static final String DESCRIPTION_LABEL = "description";

    public Sushi map(ResultSet resultSet) throws DaoException {
        Sushi sushi;
        try {
            UUID id = UUID.fromString(resultSet.getObject(ID_LABEL, String.class));
            String name = resultSet.getString(NAME_LABEL);
            BigDecimal price = resultSet.getBigDecimal(PRICE_LABEL);
            String description = resultSet.getString(DESCRIPTION_LABEL);
            SushiType type= SushiTypeMapperImpl.INSTANCE.map(resultSet);
            sushi = new Sushi(id, name, type, price, description);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return sushi;
    }
}
