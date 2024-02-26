package org.example.restapplication.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.restapplication.db.ConnectionPool;
import org.example.restapplication.exception.ServiceException;
import org.example.restapplication.model.SushiType;
import org.example.restapplication.service.TypeService;
import org.example.restapplication.service.impl.TypeServiceImpl;
import org.example.restapplication.servlet.dto.SushiTypeDto;
import org.example.restapplication.servlet.mapper.JsonMapper;
import org.example.restapplication.servlet.mapper.SushiTypeMapper;
import org.mapstruct.factory.Mappers;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static org.example.restapplication.servlet.Parameters.*;

@WebServlet(name = "SushiTypeServlet", value = "/sushi_type")
public class SushiTypeServlet extends HttpServlet {
    private final TypeService service;
    private final JsonMapper jsonMapper;

    public SushiTypeServlet(TypeService service, JsonMapper jsonMapper) {
        this.service = service;
        this.jsonMapper = jsonMapper;
    }

    public SushiTypeServlet() {
        service = new TypeServiceImpl();
        jsonMapper = new JsonMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter(TYPE_ID);
        String jsonResponse;
        try {
            if (id != null) {
                UUID uuid = UUID.fromString(id);
                var sushiOptional = service.findById(uuid);
                if (sushiOptional.isEmpty()) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                jsonResponse = jsonMapper.toJson(sushiOptional.get());
                resp.setContentType(JSON_TYPE);
                resp.getWriter().write(jsonResponse);
                resp.setStatus(HttpServletResponse.SC_OK);
                return;
            }
            jsonResponse = jsonMapper.toJson(service.findAll());
            resp.setContentType(JSON_TYPE);
            resp.getWriter().write(jsonResponse);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (JsonProcessingException | ServiceException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String jsonResponse;
        String name = req.getParameter(TYPE_NAME);
        try {
            if (name == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            var saved = service.create(new SushiTypeDto(name));
            jsonResponse = jsonMapper.toJson(saved);
            resp.setContentType(JSON_TYPE);
            resp.getWriter().write(jsonResponse);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (JsonProcessingException | ServiceException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String jsonResponse;
        String idParam = req.getParameter(TYPE_ID);
        String name = req.getParameter(TYPE_NAME);
        try {
            if (idParam == null || name == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            UUID id = UUID.fromString(idParam);
            var saved = service.update(new SushiTypeDto(id, name));
            if (saved.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            jsonResponse = jsonMapper.toJson(saved.get());
            resp.setContentType(JSON_TYPE);
            resp.getWriter().write(jsonResponse);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (JsonProcessingException | ServiceException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String jsonResponse;
        String idParam = req.getParameter(TYPE_ID);
        try {
            if (idParam == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            UUID id = UUID.fromString(idParam);
            var deleted = service.delete(id);
            if (deleted.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            jsonResponse = jsonMapper.toJson(deleted.get());
            resp.setContentType(JSON_TYPE);
            resp.getWriter().write(jsonResponse);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (JsonProcessingException | ServiceException e) {
            if(e.getCause().getCause().getClass().equals(SQLIntegrityConstraintViolationException.class)) {
                resp.sendError(HttpServletResponse.SC_CONFLICT);
            }
            else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    @Override
    public void destroy() {
        ConnectionPool.getInstance().destroyPool();
    }
}
