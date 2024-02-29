package org.example.restapplication.dao.impl;

import org.example.restapplication.dao.OrderComponentDao;
import org.example.restapplication.dao.OrderDao;
import org.example.restapplication.db.ConnectionPool;
import org.example.restapplication.db.PropertiesUtil;
import org.example.restapplication.exception.DaoException;
import org.example.restapplication.model.OrderComponent;
import org.example.restapplication.model.Sushi;
import org.example.restapplication.model.SushiType;
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
class OrderComponentDaoImplTest {
    private static final String TEST_DB_NAME = "rest_project";
    private static final String TEST_DB_INIT_SCRIPT_FILE_NAME = "db-migration.sql";
    private static final String IMAGE_NAME = "mysql:8.0";
    private static final OrderComponent ORDER_COMPONENT = new OrderComponent(
            new Order(UUID.fromString("843a81b1-b0fe-4ec4-8505-bcf264fefef1"), Order.OrderStatus.IN_PROCESS, new BigDecimal("11.90")),
            new Sushi(UUID.fromString("75e04e8e-7ca3-4402-837c-e97217d70f9d"), "Унаги маки", new SushiType(UUID.fromString("4b74fe5d-7491-47c8-9dd0-5035d30ae020"), "Маки"), new BigDecimal("11.90"), "Угорь копченый, огурец"),
            1
    );
    private static final OrderComponent ORDER_COMPONENT_UPDATE = new OrderComponent(
            new Order(UUID.fromString("843a81b1-b0fe-4ec4-8505-bcf264fefef1"), Order.OrderStatus.IN_PROCESS, new BigDecimal("11.90")),
            new Sushi(UUID.fromString("75e04e8e-7ca3-4402-837c-e97217d70f9d"), "Унаги маки", new SushiType(UUID.fromString("4b74fe5d-7491-47c8-9dd0-5035d30ae020"), "Маки"), new BigDecimal("11.90"), "Угорь копченый, огурец"),
            4
    );
    private static final OrderComponent ORDER_COMPONENT_NOT_EXIST = new OrderComponent(
            new Order(UUID.fromString("567846ea-114d-43db-a782-ca22e7addc6f"), Order.OrderStatus.IN_PROCESS, new BigDecimal("00.00")),
            new Sushi(UUID.fromString("49dd0b88-641c-4fe0-bd21-57b1ac78a0bd"), "Яки тай маки", new SushiType(UUID.fromString("75e04e8e-7ca3-4402-837c-e97217d70f9d"), "Запеченные роллы"), new BigDecimal("13.40"), "Окунь жареный, творожный сыр, помидор, маринованный редис Такуан, сыр Джугас, майонез, соус Терияки, японский омлет"),
            4
    );
    private static ConnectionPool connectionManager;
    private static OrderComponentDao dao;

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
        dao = new OrderComponentDaoImpl();
    }

    @AfterAll
    static void afterAll() {
        connectionManager.destroyPool();
        CONTAINER.close();
    }

    @org.junit.jupiter.api.Order(1)
    @Test
    void updateAbsent() throws DaoException {
        var actual = dao.update(ORDER_COMPONENT_NOT_EXIST);

        assertTrue(actual.isEmpty());
    }

    @org.junit.jupiter.api.Order(2)
    @Test
    void create() throws DaoException {
        var actual = dao.create(ORDER_COMPONENT_NOT_EXIST);

        assertAll(
                () -> assertNotNull(actual),
                () -> assertTrue(dao.findByOrderIdSushiId(actual.getOrder().getId(), actual.getSushi().getId()).isPresent())
        );
    }

    @org.junit.jupiter.api.Order(3)
    @Test
    void findAllByOrderId() throws DaoException {
        int expectedSize = 1;

        var all = dao.findAllByOrderId(ORDER_COMPONENT.getOrder().getId());

        assertEquals(expectedSize, all.size());
    }

    @org.junit.jupiter.api.Order(4)
    @Test
    void findByOrderIdSushiId() throws DaoException {
        var actual = dao.findByOrderIdSushiId(ORDER_COMPONENT.getOrder().getId(), ORDER_COMPONENT.getSushi().getId());

        assertAll(
                () -> assertTrue(actual.isPresent()),
                () -> assertEquals(ORDER_COMPONENT, actual.get())
        );
    }

    @org.junit.jupiter.api.Order(5)
    @Test
    void createConstraint() {
        assertThrows(DaoException.class, () -> {
            dao.create(ORDER_COMPONENT);
        });
    }

    @org.junit.jupiter.api.Order(6)
    @Test
    void update() throws DaoException {
        var actual = dao.update(ORDER_COMPONENT_UPDATE);

        assertAll(
                () -> assertTrue(actual.isPresent()),
                () -> assertEquals(ORDER_COMPONENT_UPDATE.getAmount(), actual.get().getAmount())
        );
    }

    @org.junit.jupiter.api.Order(7)
    @Test
    void delete() throws DaoException {
        var actual = dao.delete(ORDER_COMPONENT_UPDATE.getOrder().getId(), ORDER_COMPONENT_UPDATE.getSushi().getId());

        assertAll(
                () -> assertTrue(actual.isPresent()),
                () -> assertEquals(ORDER_COMPONENT_UPDATE, actual.get()),
                () -> assertTrue(dao.findByOrderIdSushiId(ORDER_COMPONENT_UPDATE.getOrder().getId(), ORDER_COMPONENT_UPDATE.getSushi().getId()).isEmpty())
        );
    }

    @org.junit.jupiter.api.Order(8)
    @Test
    void findAll() throws DaoException {
        int expectedSize = 5;

        var all = dao.findAll();

        assertEquals(expectedSize, all.size());
    }

    @Test
    void findById() {
        assertThrows(DaoException.class, () -> dao.findById(UUID.randomUUID()));
    }

    @Test
    void findByOrderIdSushiIdAbsent() throws DaoException {
        var actual = dao.findByOrderIdSushiId(UUID.randomUUID(), UUID.randomUUID());

        assertTrue(actual.isEmpty());
    }

    @Test
    void deleteNotSupported() {
        assertThrows(DaoException.class, () -> dao.delete(UUID.randomUUID()));
    }

    @Test
    void deleteAbsent() throws DaoException {
        var actual = dao.delete(UUID.randomUUID(), UUID.randomUUID());

        assertTrue(actual.isEmpty());
    }
}
