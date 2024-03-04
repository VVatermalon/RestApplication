package org.example.restapplication.dao.impl;

import org.example.restapplication.dao.SushiDao;
import org.example.restapplication.db.ConnectionManager;
import org.example.restapplication.db.ConnectionPool;
import org.example.restapplication.db.PropertiesUtil;
import org.example.restapplication.exception.DaoException;
import org.example.restapplication.model.Sushi;
import org.example.restapplication.model.SushiType;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.math.BigDecimal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SushiDaoImplTest {
    private static final String TEST_DB_NAME = "rest_project";
    private static final String TEST_DB_INIT_SCRIPT_FILE_NAME = "db-migration.sql";
    private static final String IMAGE_NAME = "mysql:8.0";
    private static final Sushi SUSHI = new Sushi(UUID.fromString("e6f1aa53-9e59-4c33-a804-a7885bd40996"), "Ролл с лососем и авокадо",
            new SushiType(UUID.fromString("f07ab70b-6caa-45fe-b1a5-0a12a2969d66"), "Урамаки"), new BigDecimal("20.40"), "Лосось свежий, сыр творожный, авокадо");
    private static final Sushi SUSHI_NO_UPDATE = new Sushi(UUID.fromString("49dd0b88-641c-4fe0-bd21-57b1ac78a0bd"), "Яки тай маки",
            new SushiType(UUID.fromString("75e04e8e-7ca3-4402-837c-e97217d70f9d"), "Запеченные роллы"), new BigDecimal("13.40"), "Окунь жареный, творожный сыр, помидор, маринованный редис Такуан, сыр Джугас, майонез, соус Терияки, японский омлет");

    private static final Sushi SUSHI_UPDATE = new Sushi(UUID.fromString("e6f1aa53-9e59-4c33-a804-a7885bd40996"), "Вкусный ролл",
            new SushiType(UUID.fromString("f07ab70b-6caa-45fe-b1a5-0a12a2969d66"), "Урамаки"), new BigDecimal("20.40"), "Лосось свежий, сыр творожный, авокадо");
    private static final Sushi SUSHI_WITHOUT_ORDERS = new Sushi(UUID.fromString("209d1668-606a-4066-a43a-99260f878535"), "Токио маки",
            new SushiType(UUID.fromString("f07ab70b-6caa-45fe-b1a5-0a12a2969d66"), "Урамаки"), new BigDecimal("22.60"), "Креветка тигровая, творожный сыр, помидор, авокадо, икра летучей рыбы красная");

    private static ConnectionPool connectionManager;
    private static SushiDao dao;

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
        dao = new SushiDaoImpl();
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
    @Test
    void findById() throws DaoException {
        var actual = dao.findById(SUSHI_NO_UPDATE.getId());

        assertAll(
                () -> assertTrue(actual.isPresent()),
                () -> assertEquals(SUSHI_NO_UPDATE.getId(), actual.get().getId())
        );
    }
    @Order(3)
    @Test
    void findAll() throws DaoException {
        int expectedSize = 4;

        var all = dao.findAll();

        assertEquals(expectedSize, all.size());
    }

    @Test
    void findSushiByTypeId() throws DaoException {
        int expectedSize = 2;

        var all = dao.findSushiByTypeId(SUSHI.getType().getId());

        assertEquals(expectedSize, all.size());
    }

    @Test
    void deleteConstraint() throws DaoException {
        assertThrows(DaoException.class, ()-> {
            dao.delete(SUSHI.getId());
        });
    }
    @Order(2)
    @Test
    void delete() throws DaoException {
        var actual = dao.delete(SUSHI_WITHOUT_ORDERS.getId());

        assertAll(
                () -> assertTrue(actual.isPresent()),
                () -> assertEquals(SUSHI_WITHOUT_ORDERS.getId(), actual.get().getId()),
                () -> assertTrue(dao.findById(SUSHI_WITHOUT_ORDERS.getId()).isEmpty())
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
        var actual = dao.create(SUSHI);

        assertAll(
                () -> assertNotEquals(SUSHI.getId(), actual.getId()),
                () -> assertTrue(dao.findById(actual.getId()).isPresent())
        );
    }

    @Test
    void update() throws DaoException {
        var actual = dao.update(SUSHI_UPDATE);

        assertAll(
                () -> assertTrue(actual.isPresent()),
                () -> assertEquals(SUSHI_UPDATE, actual.get())
        );
    }

    @Test
    void updateAbsent() throws DaoException {
        Sushi absentUuidSushi = new Sushi();
        absentUuidSushi.setId(UUID.randomUUID());
        var actual = dao.update(absentUuidSushi);

        assertTrue(actual.isEmpty());
    }
}