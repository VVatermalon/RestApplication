package org.example.restapplication.dao.impl;

import org.example.restapplication.dao.SushiDao;
import org.example.restapplication.db.ConnectionManager;
import org.example.restapplication.db.ConnectionPool;
import org.example.restapplication.db.PropertiesUtil;
import org.example.restapplication.exception.DaoException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class SushiDaoImplTest {
    private static final String TEST_DB_NAME = "rest_project";
    private static final String TEST_DB_INIT_SCRIPT_FILE_NAME = "db-migration.sql";
    private static final String IMAGE_NAME = "mysql:8.0";
    private static ConnectionPool connectionManager;
    private static SushiDao dao;

    @Container
    static final MySQLContainer<?> CONTAINER =
            new MySQLContainer<>(IMAGE_NAME)
                    .withDatabaseName(TEST_DB_NAME)
                    .withInitScript(TEST_DB_INIT_SCRIPT_FILE_NAME);

    @BeforeAll
    static void beforeAll() {
        Properties testDbProps = PropertiesUtil.getProperties();
        testDbProps.setProperty("db.url", CONTAINER.getJdbcUrl());
        testDbProps.setProperty("username", CONTAINER.getUsername());
        testDbProps.setProperty("password", CONTAINER.getPassword());
        try (MockedStatic<PropertiesUtil> mockedProps = mockStatic(PropertiesUtil.class)) {
            mockedProps.when(PropertiesUtil::getProperties).thenReturn(testDbProps);
            connectionManager = ConnectionPool.getInstance();
        }
        dao = new SushiDaoImpl();
    }

    @AfterAll
    static void afterAll() {
        connectionManager.destroyPool();
    }

    @Test
    void findByIdWhenAbsent() throws DaoException {
        UUID uuid = UUID.randomUUID();

        var actual = dao.findById(uuid);

        assertTrue(actual.isEmpty());
    }

    @Test
    void findAll() throws DaoException {
        int expectedSize = 4;

        var all = dao.findAll();

        assertEquals(expectedSize, all.size());
    }

    @Test
    void findSushiByTypeId() {
    }

    @Test
    void delete() {
    }

    @Test
    void create() {
    }

    @Test
    void update() {
    }

    @Test
    void testFindById() {
    }

    @Test
    void testFindAll() {
    }

    @Test
    void testFindSushiByTypeId() {
    }

    @Test
    void testDelete() {
    }

    @Test
    void testCreate() {
    }

    @Test
    void testUpdate() {
    }
}