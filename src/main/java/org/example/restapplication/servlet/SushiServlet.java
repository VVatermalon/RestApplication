package org.example.restapplication.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.restapplication.db.ConnectionPool;
import org.example.restapplication.exception.ServiceException;
import org.example.restapplication.service.SushiService;
import org.example.restapplication.service.impl.SushiServiceImpl;
import org.example.restapplication.service.TypeService;
import org.example.restapplication.service.impl.TypeServiceImpl;
import org.example.restapplication.servlet.dto.SushiDto;
import org.example.restapplication.servlet.mapper.JsonMapper;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.example.restapplication.servlet.Parameters.*;

@WebServlet(name = "SushiServlet", value = "/sushi")
public class SushiServlet extends HttpServlet {
    private final SushiService service;
    private final TypeService typeService;
    private final JsonMapper jsonMapper;

    public SushiServlet() {
        service = new SushiServiceImpl();
        typeService = new TypeServiceImpl();
        jsonMapper = new JsonMapper();
    }
    public SushiServlet(SushiService service, TypeService typeService, JsonMapper jsonMapper) {
        this.service = service;
        this.typeService = typeService;
        this.jsonMapper = jsonMapper;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter(SUSHI_ID);
        String type = req.getParameter(TYPE_ID);
        try {
            if (id != null) {
                UUID uuid = UUID.fromString(id);
                var sushiOptional = service.findById(uuid);
                if (sushiOptional.isEmpty()) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                resp.getWriter().write(jsonMapper.toJson(sushiOptional.get()));
                resp.setContentType(JSON_TYPE);
                resp.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            if (type != null) {
                UUID uuid = UUID.fromString(type);
                var sushiList = service.findByType(uuid);
                resp.getWriter().write(jsonMapper.toJson(sushiList));
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter(SUSHI_NAME);
        String typeParam = req.getParameter(TYPE_ID);
        String priceParam = req.getParameter(PRICE);
        String description = req.getParameter(DESCRIPTION);
        try {
            if(name == null || description == null || typeParam == null || priceParam == null) {
                throw new IllegalArgumentException("Null parameter");
            }
            UUID typeId = UUID.fromString(typeParam);
            BigDecimal price = new BigDecimal(priceParam);
            var type = typeService.findById(typeId);
            var saved = service.create(new SushiDto(name, type.get(), price, description));
            resp.getWriter().write(jsonMapper.toJson(saved));
            resp.setContentType(JSON_TYPE);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (NoSuchElementException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (JsonProcessingException | ServiceException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter(SUSHI_ID);
        String name = req.getParameter(SUSHI_NAME);
        String typeParam = req.getParameter(TYPE_ID);
        String priceParam = req.getParameter(PRICE);
        String description = req.getParameter(DESCRIPTION);
        try {
            if(name == null || description == null || typeParam == null || priceParam == null) {
                throw new IllegalArgumentException("Null parameter");
            }
            UUID id = UUID.fromString(idParam);
            UUID typeId = UUID.fromString(typeParam);
            BigDecimal price = new BigDecimal(priceParam);
            var type = typeService.findById(typeId);
            var saved = service.update(new SushiDto(id, name, type.get(), price, description));
            resp.getWriter().write(jsonMapper.toJson(saved.get()));
            resp.setContentType(JSON_TYPE);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (NoSuchElementException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (JsonProcessingException | ServiceException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter(SUSHI_ID);
        try {
            if(idParam == null) {
                throw new IllegalArgumentException("Null parameter");
            }
            UUID id = UUID.fromString(idParam);
            var deleted = service.delete(id);
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
