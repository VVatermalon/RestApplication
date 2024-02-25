package org.example.restapplication.servlet;

import org.example.restapplication.db.ConnectionPool;
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
import java.util.Optional;
import java.util.UUID;

import static org.example.restapplication.servlet.Parameters.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SushiServletTest {
    private static final String UUID_DEFAULT = "cfd86bec-947d-4705-ac2b-26d52e5bdbb3";
    private static final String NAME_DEFAULT = "Sushi";
    private static final String PRICE_DEFAULT = "10.0";
    private static final String WRONG_UUID_DEFAULT = "1";
    private static final String WRONG_PRICE_DEFAULT = "f";
    private static final SushiDto SUSHI_DTO_DEFAULT = new SushiDto();
    private static final SushiTypeDto SUSHI_TYPE_DTO_DEFAULT = new SushiTypeDto();
    @Mock
    private SushiService service;
    @Mock
    private TypeService typeService;
    @Mock
    private JsonMapper jsonMapper;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private SushiServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new SushiServlet(service, typeService, jsonMapper);
    }

    @Test
    void doGetSushiNotFound() throws IOException, ServiceException {
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        servlet.doGet(request, response);

        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(TYPE_ID);
        verify(service, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    @Test
    void doGetFoundSushi() throws IOException, ServiceException {
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(service.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(SUSHI_DTO_DEFAULT));

        var writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(TYPE_ID);
        verify(service, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }
    @Test
    void doGetFoundSushiType() throws IOException, ServiceException {
        lenient().when(request.getParameter(TYPE_ID)).thenReturn(UUID_DEFAULT);

        var writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(TYPE_ID);
        verify(service, times(1)).findByType(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }
    @Test()
    void doGetWrongSushiId() throws IOException, ServiceException {
        lenient().when(request.getParameter(SUSHI_ID)).thenReturn(WRONG_UUID_DEFAULT);

        servlet.doGet(request, response);

        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(TYPE_ID);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test()
    void doGetWrongTypeId() throws IOException, ServiceException {
        lenient().when(request.getParameter(TYPE_ID)).thenReturn(WRONG_UUID_DEFAULT);

        servlet.doGet(request, response);

        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(TYPE_ID);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test()
    void doGetServiceException() throws IOException, ServiceException {
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(service.findById(UUID.fromString(UUID_DEFAULT))).thenThrow(new ServiceException());

        servlet.doGet(request, response);

        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(TYPE_ID);
        verify(service, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    @Test
    void doGetWithoutParameters() throws IOException, ServiceException {
        var writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(TYPE_ID);
        verify(service, times(1)).findAll();
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doPostWithoutParameters() throws IOException {
        servlet.doPost(request, response);

        verify(request).getParameter(SUSHI_NAME);
        verify(request).getParameter(TYPE_ID);
        verify(request).getParameter(PRICE);
        verify(request).getParameter(DESCRIPTION);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    void doPostWrongTypeId() throws IOException {
        when(request.getParameter(SUSHI_NAME)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(DESCRIPTION)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(PRICE)).thenReturn(PRICE_DEFAULT);
        when(request.getParameter(TYPE_ID)).thenReturn(WRONG_UUID_DEFAULT);

        servlet.doPost(request, response);

        verify(request).getParameter(SUSHI_NAME);
        verify(request).getParameter(TYPE_ID);
        verify(request).getParameter(PRICE);
        verify(request).getParameter(DESCRIPTION);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    void doPostWrongPrice() throws IOException {
        when(request.getParameter(SUSHI_NAME)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(DESCRIPTION)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(TYPE_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(PRICE)).thenReturn(WRONG_PRICE_DEFAULT);

        servlet.doPost(request, response);

        verify(request).getParameter(SUSHI_NAME);
        verify(request).getParameter(TYPE_ID);
        verify(request).getParameter(PRICE);
        verify(request).getParameter(DESCRIPTION);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    void doPostNotExistingType() throws IOException, ServiceException {
        when(request.getParameter(SUSHI_NAME)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(DESCRIPTION)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(TYPE_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(PRICE)).thenReturn(PRICE_DEFAULT);
        when(typeService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.empty());

        servlet.doPost(request, response);

        verify(request).getParameter(SUSHI_NAME);
        verify(request).getParameter(TYPE_ID);
        verify(request).getParameter(PRICE);
        verify(request).getParameter(DESCRIPTION);
        verify(typeService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    @Test
    void doPostServiceException() throws IOException, ServiceException {
        when(request.getParameter(SUSHI_NAME)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(DESCRIPTION)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(TYPE_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(PRICE)).thenReturn(PRICE_DEFAULT);
        when(typeService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(SUSHI_TYPE_DTO_DEFAULT));
        when(service.create(any())).thenThrow(new ServiceException());

        servlet.doPost(request, response);

        verify(request).getParameter(SUSHI_NAME);
        verify(request).getParameter(TYPE_ID);
        verify(request).getParameter(PRICE);
        verify(request).getParameter(DESCRIPTION);
        verify(typeService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(service, times(1)).create(any());
        verify(response, times(1)).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    @Test
    void doPostOk() throws IOException, ServiceException {
        when(request.getParameter(SUSHI_NAME)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(DESCRIPTION)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(TYPE_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(PRICE)).thenReturn(PRICE_DEFAULT);
        when(typeService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(SUSHI_TYPE_DTO_DEFAULT));

        var writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);

        verify(request).getParameter(SUSHI_NAME);
        verify(request).getParameter(TYPE_ID);
        verify(request).getParameter(PRICE);
        verify(request).getParameter(DESCRIPTION);
        verify(typeService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(service, times(1)).create(any());
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doPutWithoutParameters() throws IOException {
        servlet.doPut(request, response);

        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(SUSHI_NAME);
        verify(request).getParameter(TYPE_ID);
        verify(request).getParameter(PRICE);
        verify(request).getParameter(DESCRIPTION);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPutWrongSushiId() throws IOException {
        when(request.getParameter(SUSHI_NAME)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(DESCRIPTION)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(PRICE)).thenReturn(PRICE_DEFAULT);
        when(request.getParameter(TYPE_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(WRONG_UUID_DEFAULT);

        servlet.doPut(request, response);

        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(SUSHI_NAME);
        verify(request).getParameter(TYPE_ID);
        verify(request).getParameter(PRICE);
        verify(request).getParameter(DESCRIPTION);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    void doPutWrongTypeId() throws IOException {
        when(request.getParameter(SUSHI_NAME)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(DESCRIPTION)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(PRICE)).thenReturn(PRICE_DEFAULT);
        when(request.getParameter(TYPE_ID)).thenReturn(WRONG_UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);

        servlet.doPut(request, response);

        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(SUSHI_NAME);
        verify(request).getParameter(TYPE_ID);
        verify(request).getParameter(PRICE);
        verify(request).getParameter(DESCRIPTION);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    void doPutWrongPrice() throws IOException {
        when(request.getParameter(SUSHI_NAME)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(DESCRIPTION)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(PRICE)).thenReturn(WRONG_PRICE_DEFAULT);
        when(request.getParameter(TYPE_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);

        servlet.doPut(request, response);

        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(SUSHI_NAME);
        verify(request).getParameter(TYPE_ID);
        verify(request).getParameter(PRICE);
        verify(request).getParameter(DESCRIPTION);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    void doPutNotExistingType() throws IOException, ServiceException {
        when(request.getParameter(SUSHI_NAME)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(DESCRIPTION)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(PRICE)).thenReturn(PRICE_DEFAULT);
        when(request.getParameter(TYPE_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(typeService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.empty());

        servlet.doPut(request, response);

        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(SUSHI_NAME);
        verify(request).getParameter(TYPE_ID);
        verify(request).getParameter(PRICE);
        verify(request).getParameter(DESCRIPTION);
        verify(typeService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    @Test
    void doPutNotExistingSushi() throws IOException, ServiceException {
        when(request.getParameter(SUSHI_NAME)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(DESCRIPTION)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(PRICE)).thenReturn(PRICE_DEFAULT);
        when(request.getParameter(TYPE_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(typeService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(SUSHI_TYPE_DTO_DEFAULT));
        when(service.update(any())).thenReturn(Optional.empty());

        servlet.doPut(request, response);

        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(SUSHI_NAME);
        verify(request).getParameter(TYPE_ID);
        verify(request).getParameter(PRICE);
        verify(request).getParameter(DESCRIPTION);
        verify(typeService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(service, times(1)).update(any());
        verify(response, times(1)).sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    @Test
    void doPutServiceException() throws IOException, ServiceException {
        when(request.getParameter(SUSHI_NAME)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(DESCRIPTION)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(PRICE)).thenReturn(PRICE_DEFAULT);
        when(request.getParameter(TYPE_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(typeService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(SUSHI_TYPE_DTO_DEFAULT));
        when(service.update(any())).thenThrow(new ServiceException());

        servlet.doPut(request, response);

        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(SUSHI_NAME);
        verify(request).getParameter(TYPE_ID);
        verify(request).getParameter(PRICE);
        verify(request).getParameter(DESCRIPTION);
        verify(typeService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(service, times(1)).update(any());
        verify(response, times(1)).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    @Test
    void doPutOk() throws IOException, ServiceException {
        when(request.getParameter(SUSHI_NAME)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(DESCRIPTION)).thenReturn(NAME_DEFAULT);
        when(request.getParameter(PRICE)).thenReturn(PRICE_DEFAULT);
        when(request.getParameter(TYPE_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(typeService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(SUSHI_TYPE_DTO_DEFAULT));
        when(service.update(any())).thenReturn(Optional.of(SUSHI_DTO_DEFAULT));

        var writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPut(request, response);

        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(SUSHI_NAME);
        verify(request).getParameter(TYPE_ID);
        verify(request).getParameter(PRICE);
        verify(request).getParameter(DESCRIPTION);
        verify(typeService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(service, times(1)).update(any());
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doDeleteWithoutParameters() throws IOException {
        servlet.doDelete(request, response);

        verify(request).getParameter(SUSHI_ID);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    void doDeleteWrongId() throws IOException {
        when(request.getParameter(SUSHI_ID)).thenReturn(WRONG_UUID_DEFAULT);

        servlet.doDelete(request, response);

        verify(request).getParameter(SUSHI_ID);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    void doDeleteNotExistingSushi() throws IOException, ServiceException {
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(service.delete(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.empty());

        servlet.doDelete(request, response);

        verify(request).getParameter(SUSHI_ID);
        verify(service, times(1)).delete(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    @Test
    void doDeleteServiceException() throws IOException, ServiceException {
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(service.delete(UUID.fromString(UUID_DEFAULT))).thenThrow(new ServiceException());

        servlet.doDelete(request, response);

        verify(request).getParameter(SUSHI_ID);
        verify(service, times(1)).delete(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    @Test
    void doDeleteOk() throws IOException, ServiceException {
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(service.delete(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(SUSHI_DTO_DEFAULT));

        var writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        servlet.doDelete(request, response);

        verify(request).getParameter(SUSHI_ID);
        verify(service, times(1)).delete(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }
}