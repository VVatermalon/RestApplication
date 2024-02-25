package org.example.restapplication.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.restapplication.db.ConnectionPool;
import org.example.restapplication.exception.ServiceException;
import org.example.restapplication.model.Order;
import org.example.restapplication.service.OrderService;
import org.example.restapplication.service.impl.OrderServiceImpl;
import org.example.restapplication.servlet.dto.OrderDto;
import org.example.restapplication.servlet.mapper.JsonMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.example.restapplication.servlet.Parameters.*;

@WebServlet(name = "OrderServlet", value = "/order")
public class OrderServlet extends HttpServlet {
    private final OrderService service;
    private final JsonMapper jsonMapper;

    public OrderServlet(OrderService service, JsonMapper jsonMapper) {
        this.service = service;
        this.jsonMapper = jsonMapper;
    }

    public OrderServlet() {
        service = new OrderServiceImpl();
        jsonMapper = new JsonMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter(ORDER_ID);
        try {
            if (id != null) {
                UUID uuid = UUID.fromString(id);
                Optional<OrderDto> orderOptional = service.findById(uuid);
                if (orderOptional.isEmpty()) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                resp.getWriter().write(jsonMapper.toJson(orderOptional.get()));
                resp.setContentType(JSON_TYPE);
                resp.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            resp.getWriter().write(jsonMapper.toJson(service.findAll()));
            resp.setContentType(JSON_TYPE);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (JsonProcessingException | ServiceException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter(ORDER_ID);
        String statusParam = req.getParameter(ORDER_STATUS);
        try {
            if(idParam == null || statusParam == null) {
                throw new IllegalArgumentException("Null parameter");
            }
            UUID id = UUID.fromString(idParam);
            var status = Order.OrderStatus.valueOf(statusParam);
            Optional<OrderDto> saved = service.update(new OrderDto(id, status));
            if (saved.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            resp.getWriter().write(jsonMapper.toJson(saved.get()));
            resp.setContentType(JSON_TYPE);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (ServiceException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter(ORDER_ID);
        try {
            if(idParam == null) {
                throw new IllegalArgumentException("Null parameter");
            }
            UUID id = UUID.fromString(idParam);
            Optional<OrderDto> deleted = service.delete(id);
            if (deleted.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            resp.getWriter().write(jsonMapper.toJson(deleted.get()));
            resp.setContentType(JSON_TYPE);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (ServiceException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void destroy() {
        ConnectionPool.getInstance().destroyPool();
    }
}

