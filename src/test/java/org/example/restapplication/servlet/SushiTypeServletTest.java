package org.example.restapplication.servlet;

import org.example.restapplication.db.ConnectionPool;
import org.example.restapplication.exception.DaoException;
import org.example.restapplication.exception.ServiceException;
import org.example.restapplication.service.SushiService;
import org.example.restapplication.service.TypeService;
import org.example.restapplication.servlet.dto.SushiDto;
import org.example.restapplication.servlet.dto.SushiTypeDto;
import org.example.restapplication.servlet.mapper.JsonMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;
import java.util.UUID;

import static org.example.restapplication.servlet.Parameters.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SushiTypeServletTest {
    private static final String UUID_DEFAULT = "cfd86bec-947d-4705-ac2b-26d52e5bdbb3";
    private static final String NAME_DEFAULT = "Type";
    private static final String WRONG_UUID_DEFAULT = "1";
    private static final SushiTypeDto SUSHI_TYPE_DTO_DEFAULT = new SushiTypeDto();
    @Mock
    private TypeService service;
    @Mock
    private JsonMapper jsonMapper;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private SushiTypeServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new SushiTypeServlet(service, jsonMapper);
    }

    @Test
    void doGetTypeNotFound() throws IOException, ServiceException {
        when(request.getParameter(TYPE_ID)).thenReturn(UUID_DEFAULT);
        when(service.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.empty());

        servlet.doGet(request, response);

        verify(request).getParameter(TYPE_ID);
        verify(service, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    @Test
    void doGetFoundType() throws IOException, ServiceException {
        when(request.getParameter(TYPE_ID)).thenReturn(UUID_DEFAULT);
        when(service.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(SUSHI_TYPE_DTO_DEFAULT));

        var writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        verify(request).getParameter(TYPE_ID);
        verify(service, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }
    @Test()
    void doGetWrongTypeId() throws IOException {
        lenient().when(request.getParameter(TYPE_ID)).thenReturn(WRONG_UUID_DEFAULT);

        servlet.doGet(request, response);

        verify(request).getParameter(TYPE_ID);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test()
    void doGetServiceException() throws IOException, ServiceException {
        when(request.getParameter(TYPE_ID)).thenReturn(UUID_DEFAULT);
        when(service.findById(UUID.fromString(UUID_DEFAULT))).thenThrow(new ServiceException());

        servlet.doGet(request, response);

        verify(request).getParameter(TYPE_ID);
        verify(service, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    @Test
    void doGetWithoutParameters() throws IOException, ServiceException {
        var writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        verify(request).getParameter(TYPE_ID);
        verify(service, times(1)).findAll();
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doPostWithoutParameters() throws IOException {
        servlet.doPost(request, response);

        verify(request).getParameter(TYPE_NAME);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    void doPostServiceException() throws IOException, ServiceException {
        when(request.getParameter(TYPE_NAME)).thenReturn(NAME_DEFAULT);
        when(service.create(any())).thenThrow(new ServiceException());

        servlet.doPost(request, response);

        verify(request).getParameter(TYPE_NAME);
        verify(service, times(1)).create(any());
        verify(response, times(1)).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    @Test
    void doPostOk() throws IOException, ServiceException {
        when(request.getParameter(TYPE_NAME)).thenReturn(NAME_DEFAULT);
        when(service.create(any())).thenReturn(SUSHI_TYPE_DTO_DEFAULT);

        var writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);

        verify(request).getParameter(TYPE_NAME);
        verify(service, times(1)).create(any());
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doPutWithoutParameters() throws IOException {
        servlet.doPut(request, response);

        verify(request).getParameter(TYPE_ID);
        verify(request).getParameter(TYPE_NAME);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPutWrongTypeId() throws IOException {
        when(request.getParameter(TYPE_NAME)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(TYPE_ID)).thenReturn(WRONG_UUID_DEFAULT);

        servlet.doPut(request, response);

        verify(request).getParameter(TYPE_ID);
        verify(request).getParameter(TYPE_NAME);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    void doPutNotExistingType() throws IOException, ServiceException {
        when(request.getParameter(TYPE_NAME)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(TYPE_ID)).thenReturn(UUID_DEFAULT);
        when(service.update(any())).thenReturn(Optional.empty());

        servlet.doPut(request, response);

        verify(request).getParameter(TYPE_NAME);
        verify(request).getParameter(TYPE_ID);
        verify(service, times(1)).update(any());
        verify(response, times(1)).sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    @Test
    void doPutServiceException() throws IOException, ServiceException {
        when(request.getParameter(TYPE_NAME)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(TYPE_ID)).thenReturn(UUID_DEFAULT);
        when(service.update(any())).thenThrow(new ServiceException());

        servlet.doPut(request, response);

        verify(request).getParameter(TYPE_NAME);
        verify(request).getParameter(TYPE_ID);
        verify(service, times(1)).update(any());
        verify(response, times(1)).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    @Test
    void doPutOk() throws IOException, ServiceException {
        when(request.getParameter(TYPE_NAME)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(TYPE_ID)).thenReturn(UUID_DEFAULT);
        when(service.update(any())).thenReturn(Optional.of(SUSHI_TYPE_DTO_DEFAULT));

        var writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPut(request, response);

        verify(request).getParameter(TYPE_NAME);
        verify(request).getParameter(TYPE_ID);
        verify(service, times(1)).update(any());
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doDeleteWithoutParameters() throws IOException {
        servlet.doDelete(request, response);

        verify(request).getParameter(TYPE_ID);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    void doDeleteWrongId() throws IOException {
        when(request.getParameter(TYPE_ID)).thenReturn(WRONG_UUID_DEFAULT);

        servlet.doDelete(request, response);

        verify(request).getParameter(TYPE_ID);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    void doDeleteNotExistingType() throws IOException, ServiceException {
        when(request.getParameter(TYPE_ID)).thenReturn(UUID_DEFAULT);
        when(service.delete(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.empty());

        servlet.doDelete(request, response);

        verify(request).getParameter(TYPE_ID);
        verify(service, times(1)).delete(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    @Test
    void doDeleteServiceException() throws IOException, ServiceException {
        when(request.getParameter(TYPE_ID)).thenReturn(UUID_DEFAULT);
        ServiceException exception = new ServiceException(new DaoException(new SQLException()));
        when(service.delete(UUID.fromString(UUID_DEFAULT))).thenThrow(exception);

        servlet.doDelete(request, response);

        verify(request).getParameter(TYPE_ID);
        verify(service, times(1)).delete(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    @Test
    void doDeleteServiceExceptionConstraint() throws IOException, ServiceException {
        when(request.getParameter(TYPE_ID)).thenReturn(UUID_DEFAULT);
        ServiceException exception = new ServiceException(new DaoException(new SQLIntegrityConstraintViolationException()));
        when(service.delete(UUID.fromString(UUID_DEFAULT))).thenThrow(exception);

        servlet.doDelete(request, response);

        verify(request).getParameter(TYPE_ID);
        verify(service, times(1)).delete(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).sendError(HttpServletResponse.SC_CONFLICT);
    }
    @Test
    void doDeleteOk() throws IOException, ServiceException {
        when(request.getParameter(TYPE_ID)).thenReturn(UUID_DEFAULT);
        when(service.delete(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(SUSHI_TYPE_DTO_DEFAULT));

        var writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        servlet.doDelete(request, response);

        verify(request).getParameter(TYPE_ID);
        verify(service, times(1)).delete(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }
}