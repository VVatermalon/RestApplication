package org.example.restapplication.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.restapplication.db.ConnectionPool;
import org.example.restapplication.exception.ServiceException;
import org.example.restapplication.model.Order;
import org.example.restapplication.model.OrderComponent;
import org.example.restapplication.model.Sushi;
import org.example.restapplication.service.OrderComponentService;
import org.example.restapplication.service.OrderService;
import org.example.restapplication.service.SushiService;
import org.example.restapplication.service.impl.OrderServiceImpl;
import org.example.restapplication.service.impl.OrderComponentServiceImpl;
import org.example.restapplication.service.impl.SushiServiceImpl;
import org.example.restapplication.servlet.dto.OrderComponentDto;
import org.example.restapplication.servlet.dto.OrderDto;
import org.example.restapplication.servlet.dto.SushiDto;
import org.example.restapplication.servlet.mapper.JsonMapper;
import org.example.restapplication.servlet.mapper.OrderComponentMapper;
import org.mapstruct.factory.Mappers;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static org.example.restapplication.servlet.Parameters.*;

@WebServlet(name = "OrderComponentServlet", value = "/order_component")
public class OrderComponentServlet extends HttpServlet {
    private final SushiService sushiService;
    private final OrderService orderService;
    private final OrderComponentService orderComponentService;
    private final JsonMapper jsonMapper;

    public OrderComponentServlet(SushiService sushiService, OrderService orderService, OrderComponentService orderComponentService, JsonMapper jsonMapper) {
        this.sushiService = sushiService;
        this.orderService = orderService;
        this.orderComponentService = orderComponentService;
        this.jsonMapper = jsonMapper;
    }

    public OrderComponentServlet() {
        sushiService = new SushiServiceImpl();
        orderService = new OrderServiceImpl();
        orderComponentService = new OrderComponentServiceImpl();
        jsonMapper = new JsonMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter(ORDER_ID);
        try {
            if (id != null) {
                UUID uuid = UUID.fromString(id);
                resp.getWriter().write(jsonMapper.toJson(orderComponentService.findAllOrderComponents(uuid)));
                resp.setContentType(JSON_TYPE);
                resp.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            resp.getWriter().write(jsonMapper.toJson(orderComponentService.findAll()));
            resp.setContentType(JSON_TYPE);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (JsonProcessingException | ServiceException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String orderParam = req.getParameter(ORDER_ID);
        String sushiParam = req.getParameter(SUSHI_ID);
        String amountParam = req.getParameter(AMOUNT);
        try {
            if(sushiParam == null || amountParam == null) {
                throw new IllegalArgumentException("Null parameter");
            }
            OrderDto order;
            if (orderParam == null) {
                order = orderService.create();
            } else {
                UUID orderId = UUID.fromString(orderParam);
                order = orderService.findById(orderId).get();
            }
            UUID sushiId = UUID.fromString(sushiParam);
            int amount = Integer.parseInt(amountParam);
            SushiDto sushi = sushiService.findById(sushiId).get();
            if(orderComponentService.findByOrderIdSushiId(order.getId(), sushiId).isPresent()) {
                resp.sendError(HttpServletResponse.SC_CONFLICT);
                resp.addHeader("ERROR_MESSAGE", "order component already exists");
                return;
            }
            OrderComponentDto saved = orderComponentService.create(new OrderComponentDto(order, sushi, amount));
            resp.getWriter().write(jsonMapper.toJson(saved));
            resp.setContentType(JSON_TYPE);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (NoSuchElementException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            resp.addHeader("ERROR_MESSAGE", "cannot find order or sushi with this id");
        } catch (JsonProcessingException | ServiceException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String orderParam = req.getParameter(ORDER_ID);
        String sushiParam = req.getParameter(SUSHI_ID);
        String amountParam = req.getParameter(AMOUNT);
        try {
            if(orderParam == null || sushiParam == null || amountParam == null) {
                throw new IllegalArgumentException("Null parameter");
            }
            UUID orderId = UUID.fromString(orderParam);
            UUID sushiId = UUID.fromString(sushiParam);
            int amount = Integer.parseInt(amountParam);
            OrderDto order = orderService.findById(orderId).get();
            SushiDto sushi = sushiService.findById(sushiId).get();
            var updated = orderComponentService.update(new OrderComponentDto(order, sushi, amount));
            if(updated.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            OrderComponentDto saved = updated.get();
            resp.getWriter().write(jsonMapper.toJson(saved));
            resp.setContentType(JSON_TYPE);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (NoSuchElementException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            resp.addHeader("ERROR_MESSAGE", "cannot find order or sushi type with this id");
        } catch (JsonProcessingException | ServiceException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String orderParam = req.getParameter(ORDER_ID);
        String sushiParam = req.getParameter(SUSHI_ID);
        try {
            if(orderParam == null || sushiParam == null) {
                throw new IllegalArgumentException("Null parameter");
            }
            UUID orderId = UUID.fromString(orderParam);
            UUID sushiId = UUID.fromString(sushiParam);
            Optional<OrderComponentDto> deleted = orderComponentService.delete(orderId, sushiId);
            if (deleted.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            resp.getWriter().write(jsonMapper.toJson(deleted.get()));
            resp.setContentType(JSON_TYPE);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (JsonProcessingException | ServiceException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void destroy() {
        ConnectionPool.getInstance().destroyPool();
    }
}