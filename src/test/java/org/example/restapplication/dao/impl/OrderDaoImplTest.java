package org.example.restapplication.dao.impl;

import org.example.restapplication.dao.OrderComponentDao;
import org.example.restapplication.dao.OrderDao;
import org.example.restapplication.db.ConnectionPool;
import org.example.restapplication.db.PropertiesUtil;
import org.example.restapplication.exception.DaoException;
import org.example.restapplication.model.Order;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.math.BigDecimal;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderDaoImplTest {
    private static final String TEST_DB_NAME = "rest_project";
    private static final String TEST_DB_INIT_SCRIPT_FILE_NAME = "db-migration.sql";
    private static final String IMAGE_NAME = "mysql:8.0";
    private static final Order ORDER = new Order(UUID.fromString("843a81b1-b0fe-4ec4-8505-bcf264fefef1"), Order.OrderStatus.IN_PROCESS, new BigDecimal("11.90"));
    private static final Order ORDER_UPDATE = new Order(UUID.fromString("843a81b1-b0fe-4ec4-8505-bcf264fefef1"), Order.OrderStatus.CONFIRMED, new BigDecimal("11.90"));
    private static final Order ORDER_DELETE = new Order(UUID.fromString("567846ea-114d-43db-a782-ca22e7addc6f"), Order.OrderStatus.IN_PROCESS, new BigDecimal("0.00"));
    private static ConnectionPool connectionManager;
    private static OrderDao dao;

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
        dao = new OrderDaoImpl();
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
    @org.junit.jupiter.api.Order(4)
    @Test
    void findById() throws DaoException {
        var actual = dao.findById(ORDER.getId());

        assertAll(
                () -> assertTrue(actual.isPresent()),
                () -> assertEquals(ORDER.getId(), actual.get().getId())
        );
    }

    @org.junit.jupiter.api.Order(3)
    @Test
    void findAll() throws DaoException {
        int expectedSize = 4;

        var all = dao.findAll();

        assertEquals(expectedSize, all.size());
    }
    @Test
    void deleteConstraint() throws DaoException {
        var actual = dao.delete(ORDER_UPDATE.getId());
        OrderComponentDao orderComponentDao = new OrderComponentDaoImpl();
        var list = orderComponentDao.findAllByOrderId(ORDER_UPDATE.getId());
        assertAll(
                () -> assertTrue(actual.isPresent()),
                () -> assertEquals(ORDER_UPDATE.getStatus(), actual.get().getStatus()),
                () -> assertTrue(dao.findById(ORDER_UPDATE.getId()).isEmpty()),
                () -> assertEquals(0, list.size())
        );
    }
    @org.junit.jupiter.api.Order(2)
    @Test
    void delete() throws DaoException {
        var actual = dao.delete(ORDER_DELETE.getId());

        assertAll(
                () -> assertTrue(actual.isPresent()),
                () -> assertEquals(ORDER_DELETE.getId(), actual.get().getId()),
                () -> assertTrue(dao.findById(ORDER_DELETE.getId()).isEmpty())
        );
    }

    @Test
    void deleteAbsent() throws DaoException {
        var actual = dao.delete(UUID.randomUUID());

        assertTrue(actual.isEmpty());
    }

    @org.junit.jupiter.api.Order(1)
    @Test
    void create() throws DaoException {
        var actual = dao.create(ORDER);

        assertAll(
                () -> assertNotEquals(ORDER.getId(), actual.getId()),
                () -> assertTrue(dao.findById(actual.getId()).isPresent())
        );
    }
    @org.junit.jupiter.api.Order(5)
    @Test
    void update() throws DaoException {
        var actual = dao.update(ORDER_UPDATE);

        assertAll(
                () -> assertTrue(actual.isPresent()),
                () -> assertEquals(ORDER_UPDATE, actual.get())
        );
    }

    @Test
    void updateAbsent() throws DaoException {
        Order absentUuidOrder = new Order();
        absentUuidOrder.setId(UUID.randomUUID());
        var actual = dao.update(absentUuidOrder);

        assertTrue(actual.isEmpty());
    }
}
