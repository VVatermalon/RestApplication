package org.example.restapplication.dao.impl;

import org.example.restapplication.dao.SushiTypeDao;
import org.example.restapplication.db.ConnectionPool;
import org.example.restapplication.db.PropertiesUtil;
import org.example.restapplication.exception.DaoException;
import org.example.restapplication.model.SushiType;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SushiTypeDaoImplTest {
    private static final String TEST_DB_NAME = "rest_project";
    private static final String TEST_DB_INIT_SCRIPT_FILE_NAME = "db-migration.sql";
    private static final String IMAGE_NAME = "mysql:8.0";
    private static final SushiType SUSHI_TYPE = new SushiType(UUID.fromString("f07ab70b-6caa-45fe-b1a5-0a12a2969d66"), "Урамаки");
    private static final SushiType SUSHI_TYPE_UPDATE = new SushiType(UUID.fromString("f07ab70b-6caa-45fe-b1a5-0a12a2969d66"), "Урамаки2");
    private static final SushiType SUSHI_TYPE_FOR_DELETE = new SushiType(UUID.fromString("6cda33dd-59fc-403f-8b8d-61f05de42f9e"), "Темпура роллы");

    private static ConnectionPool connectionManager;
    private static SushiTypeDao dao;

    @Container
    static final MySQLContainer<?> CONTAINER =
            new MySQLContainer<>(IMAGE_NAME)
                    .withDatabaseName(TEST_DB_NAME)
                    .withInitScript(TEST_DB_INIT_SCRIPT_FILE_NAME);

    @BeforeAll
    static void beforeAll() {
        CONTAINER.start();
        Properties testDbProps = PropertiesUtil.getProperties();
        testDbProps.setProperty("db.url", CONTAINER.getJdbcUrl());
        testDbProps.setProperty("username", CONTAINER.getUsername());
        testDbProps.setProperty("password", CONTAINER.getPassword());
        try (MockedStatic<PropertiesUtil> mockedProps = mockStatic(PropertiesUtil.class)) {
            mockedProps.when(PropertiesUtil::getProperties).thenReturn(testDbProps);
            connectionManager = ConnectionPool.getInstance();
        }
        dao = new SushiTypeDaoImpl();
    }
    @AfterAll
    static void afterAll() {
        connectionManager.destroyPool();
        CONTAINER.close();
    }
    @Test
    void findByIdWhenAbsent() throws DaoException {
        UUID uuid = UUID.randomUUID();

        var actual = dao.findById(uuid);

        assertTrue(actual.isEmpty());
    }
    @Order(4)
    @Test
    void findById() throws DaoException {
        var actual = dao.findById(SUSHI_TYPE.getId());

        assertAll(
                () -> assertTrue(actual.isPresent()),
                () -> assertEquals(SUSHI_TYPE, actual.get())
        );
    }

    @Order(3)
    @Test
    void findAll() throws DaoException {
        int expectedSize = 5;

        var all = dao.findAll();

        assertEquals(expectedSize, all.size());
    }
    @Test
    void deleteConstraint() {
        assertThrows(DaoException.class, ()-> {
            dao.delete(SUSHI_TYPE.getId());
        });
    }
    @Order(2)
    @Test
    void delete() throws DaoException {
        var actual = dao.delete(SUSHI_TYPE_FOR_DELETE.getId());

        assertAll(
                () -> assertTrue(actual.isPresent()),
                () -> assertEquals(SUSHI_TYPE_FOR_DELETE, actual.get()),
                () -> assertTrue(dao.findById(SUSHI_TYPE_FOR_DELETE.getId()).isEmpty())
        );
    }

    @Test
    void deleteAbsent() throws DaoException {
        var actual = dao.delete(UUID.randomUUID());

        assertTrue(actual.isEmpty());
    }

    @Order(1)
    @Test
    void create() throws DaoException {
        var actual = dao.create(SUSHI_TYPE);

        assertAll(
                () -> assertNotEquals(SUSHI_TYPE.getId(), actual.getId()),
                () -> assertTrue(dao.findById(actual.getId()).isPresent())
        );
    }

    @Order(5)
    @Test
    void update() throws DaoException {
        var actual = dao.update(SUSHI_TYPE_UPDATE);

        assertAll(
                () -> assertTrue(actual.isPresent()),
                () -> assertEquals(SUSHI_TYPE_UPDATE, actual.get())
        );
    }

    @Test
    void updateAbsent() throws DaoException {
        SushiType absentUuidSushiType = new SushiType();
        absentUuidSushiType.setId(UUID.randomUUID());
        var actual = dao.update(absentUuidSushiType);

        assertTrue(actual.isEmpty());
    }
}
