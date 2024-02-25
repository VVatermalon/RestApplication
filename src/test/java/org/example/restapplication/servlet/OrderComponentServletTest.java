package org.example.restapplication.servlet;

import org.example.restapplication.exception.ServiceException;
import org.example.restapplication.model.Order;
import org.example.restapplication.service.OrderComponentService;
import org.example.restapplication.service.OrderService;
import org.example.restapplication.service.SushiService;
import org.example.restapplication.servlet.dto.OrderComponentDto;
import org.example.restapplication.servlet.dto.OrderDto;
import org.example.restapplication.servlet.dto.SushiDto;
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
class OrderComponentServletTest {
    private static final String UUID_DEFAULT = "cfd86bec-947d-4705-ac2b-26d52e5bdbb3";
    private static final String AMOUNT_DEFAULT = "1";
    private static final String WRONG_UUID_DEFAULT = "1";
    private static final String WRONG_AMOUNT_DEFAULT = "f";
    private static final OrderComponentDto ORDER_COMPONENT_DTO_DEFAULT = new OrderComponentDto();
    private static final OrderDto ORDER_DTO_DEFAULT = new OrderDto();
    private static final SushiDto SUSHI_DTO_DEFAULT = new SushiDto();
    @Mock
    private OrderComponentService service;

    @Mock
    private OrderService orderService;
    @Mock
    private SushiService sushiService;
    @Mock
    private JsonMapper jsonMapper;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private OrderComponentServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new OrderComponentServlet(sushiService, orderService, service, jsonMapper);
        ORDER_COMPONENT_DTO_DEFAULT.setOrder(new OrderDto(UUID.fromString(UUID_DEFAULT), Order.OrderStatus.IN_PROCESS));
    }

    @Test
    void doGetFoundOrder() throws IOException, ServiceException {
        when(request.getParameter(ORDER_ID)).thenReturn(UUID_DEFAULT);

        var writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(service, times(1)).findAllOrderComponents(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }
    @Test()
    void doGetWrongOrderId() throws IOException, ServiceException {
        lenient().when(request.getParameter(ORDER_ID)).thenReturn(WRONG_UUID_DEFAULT);

        servlet.doGet(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test()
    void doGetServiceException() throws IOException, ServiceException {
        when(request.getParameter(ORDER_ID)).thenReturn(UUID_DEFAULT);
        when(service.findAllOrderComponents(UUID.fromString(UUID_DEFAULT))).thenThrow(new ServiceException());

        servlet.doGet(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(service, times(1)).findAllOrderComponents(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    @Test
    void doGetWithoutParameters() throws IOException, ServiceException {
        var writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(service, times(1)).findAll();
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doPostWithoutParameters() throws IOException {
        servlet.doPost(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(AMOUNT);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    void doPostWrongOrderId() throws IOException {
        when(request.getParameter(ORDER_ID)).thenReturn(WRONG_UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(AMOUNT)).thenReturn(AMOUNT_DEFAULT);

        servlet.doPost(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(AMOUNT);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    void doPostWrongSushiId() throws IOException, ServiceException {
        when(request.getParameter(ORDER_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(WRONG_UUID_DEFAULT);
        when(request.getParameter(AMOUNT)).thenReturn(AMOUNT_DEFAULT);
        when(orderService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(ORDER_DTO_DEFAULT));

        servlet.doPost(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(AMOUNT);
        verify(orderService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    void doPostWrongAmount() throws IOException, ServiceException {
        when(request.getParameter(ORDER_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(AMOUNT)).thenReturn(WRONG_AMOUNT_DEFAULT);
        when(orderService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(ORDER_DTO_DEFAULT));

        servlet.doPost(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(AMOUNT);
        verify(orderService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    void doPostNotExistingOrder() throws IOException, ServiceException {
        when(request.getParameter(ORDER_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(AMOUNT)).thenReturn(AMOUNT_DEFAULT);
        when(orderService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.empty());

        servlet.doPost(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(AMOUNT);
        verify(orderService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    @Test
    void doPostNotExistingSushi() throws IOException, ServiceException {
        when(request.getParameter(ORDER_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(AMOUNT)).thenReturn(AMOUNT_DEFAULT);
        when(orderService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(ORDER_DTO_DEFAULT));
        when(sushiService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.empty());

        servlet.doPost(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(AMOUNT);
        verify(orderService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(sushiService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    @Test
    void doPostExistingPrimaryKey() throws IOException, ServiceException {
        when(request.getParameter(ORDER_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(AMOUNT)).thenReturn(AMOUNT_DEFAULT);
        when(orderService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(ORDER_DTO_DEFAULT));
        when(sushiService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(SUSHI_DTO_DEFAULT));
        when(service.findByOrderIdSushiId(any(), any())).thenReturn(Optional.of(ORDER_COMPONENT_DTO_DEFAULT));

        servlet.doPost(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(AMOUNT);
        verify(orderService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(sushiService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(service, times(1)).findByOrderIdSushiId(any(), any());
        verify(response, times(1)).sendError(HttpServletResponse.SC_CONFLICT);
    }
    @Test
    void doPostServiceException() throws IOException, ServiceException {
        when(request.getParameter(ORDER_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(AMOUNT)).thenReturn(AMOUNT_DEFAULT);
        when(orderService.findById(UUID.fromString(UUID_DEFAULT))).thenThrow(new ServiceException());

        servlet.doPost(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(AMOUNT);
        verify(orderService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    @Test
    void doPostWithoutOrderId() throws IOException, ServiceException {
        lenient().when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        lenient().when(request.getParameter(AMOUNT)).thenReturn(AMOUNT_DEFAULT);
        when(sushiService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(SUSHI_DTO_DEFAULT));
        when(orderService.create()).thenReturn(ORDER_DTO_DEFAULT);
        when(service.create(any())).thenReturn(ORDER_COMPONENT_DTO_DEFAULT);

        var writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(AMOUNT);
        verify(sushiService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(orderService, times(1)).create();
        verify(service, times(1)).findByOrderIdSushiId(any(), any());
        verify(service, times(1)).create(any());
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }
    @Test
    void doPostOk() throws IOException, ServiceException {
        when(request.getParameter(ORDER_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(AMOUNT)).thenReturn(AMOUNT_DEFAULT);
        when(orderService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(ORDER_DTO_DEFAULT));
        when(sushiService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(SUSHI_DTO_DEFAULT));
        when(service.create(any())).thenReturn(ORDER_COMPONENT_DTO_DEFAULT);

        var writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(AMOUNT);
        verify(orderService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(sushiService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(service, times(1)).findByOrderIdSushiId(any(), any());
        verify(service, times(1)).create(any());
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doPutWithoutParameters() throws IOException {
        servlet.doPut(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(AMOUNT);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPutWrongOrderId() throws IOException {
        when(request.getParameter(ORDER_ID)).thenReturn(WRONG_UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(AMOUNT)).thenReturn(AMOUNT_DEFAULT);

        servlet.doPut(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(AMOUNT);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    void doPutWrongSushiId() throws IOException {
        when(request.getParameter(ORDER_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(WRONG_AMOUNT_DEFAULT);
        when(request.getParameter(AMOUNT)).thenReturn(AMOUNT_DEFAULT);

        servlet.doPut(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(AMOUNT);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    void doPutWrongAmount() throws IOException {
        when(request.getParameter(ORDER_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(AMOUNT)).thenReturn(WRONG_AMOUNT_DEFAULT);

        servlet.doPut(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(AMOUNT);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    void doPutNotExistingOrder() throws IOException, ServiceException {
        when(request.getParameter(ORDER_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(AMOUNT)).thenReturn(AMOUNT_DEFAULT);
        when(orderService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.empty());

        servlet.doPut(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(AMOUNT);
        verify(orderService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    @Test
    void doPutNotExistingSushi() throws IOException, ServiceException {
        when(request.getParameter(ORDER_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(AMOUNT)).thenReturn(AMOUNT_DEFAULT);
        when(orderService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(ORDER_DTO_DEFAULT));
        when(sushiService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.empty());

        servlet.doPut(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(AMOUNT);
        verify(orderService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(sushiService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    @Test
    void doPutNotExistingOrderComponent() throws IOException, ServiceException {
        when(request.getParameter(ORDER_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(AMOUNT)).thenReturn(AMOUNT_DEFAULT);
        when(orderService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(ORDER_DTO_DEFAULT));
        when(sushiService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(SUSHI_DTO_DEFAULT));
        when(service.update(any())).thenReturn(Optional.empty());

        servlet.doPut(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(AMOUNT);
        verify(orderService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(sushiService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(service, times(1)).update(any());
        verify(response, times(1)).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    @Test
    void doPutServiceException() throws IOException, ServiceException {
        when(request.getParameter(ORDER_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(AMOUNT)).thenReturn(AMOUNT_DEFAULT);
        when(orderService.findById(UUID.fromString(UUID_DEFAULT))).thenThrow(new ServiceException());

        servlet.doPut(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(AMOUNT);
        verify(orderService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    @Test
    void doPutOk() throws IOException, ServiceException {
        when(request.getParameter(ORDER_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(AMOUNT)).thenReturn(AMOUNT_DEFAULT);
        when(orderService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(ORDER_DTO_DEFAULT));
        when(sushiService.findById(UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(SUSHI_DTO_DEFAULT));
        when(service.update(any())).thenReturn(Optional.of(ORDER_COMPONENT_DTO_DEFAULT));

        var writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPut(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(request).getParameter(AMOUNT);
        verify(orderService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(sushiService, times(1)).findById(UUID.fromString(UUID_DEFAULT));
        verify(service, times(1)).update(any());
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void doDeleteWithoutParameters() throws IOException {
        servlet.doDelete(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    void doDeleteWrongOrderId() throws IOException {
        when(request.getParameter(ORDER_ID)).thenReturn(WRONG_UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);

        servlet.doDelete(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    void doDeleteWrongSushiId() throws IOException {
        when(request.getParameter(ORDER_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(WRONG_UUID_DEFAULT);

        servlet.doDelete(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    @Test
    void doDeleteNotExistingComponent() throws IOException, ServiceException {
        when(request.getParameter(ORDER_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(service.delete(UUID.fromString(UUID_DEFAULT), UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.empty());

        servlet.doDelete(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(service, times(1)).delete(UUID.fromString(UUID_DEFAULT), UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    @Test
    void doDeleteServiceException() throws IOException, ServiceException {
        when(request.getParameter(ORDER_ID)).thenReturn(UUID_DEFAULT);
        when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(service.delete(UUID.fromString(UUID_DEFAULT), UUID.fromString(UUID_DEFAULT))).thenThrow(new ServiceException());

        servlet.doDelete(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(service, times(1)).delete(UUID.fromString(UUID_DEFAULT), UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    @Test
    void doDeleteOk() throws IOException, ServiceException {
        lenient().when(request.getParameter(ORDER_ID)).thenReturn(UUID_DEFAULT);
        lenient().when(request.getParameter(SUSHI_ID)).thenReturn(UUID_DEFAULT);
        when(service.delete(UUID.fromString(UUID_DEFAULT), UUID.fromString(UUID_DEFAULT))).thenReturn(Optional.of(ORDER_COMPONENT_DTO_DEFAULT));

        var writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        servlet.doDelete(request, response);

        verify(request).getParameter(ORDER_ID);
        verify(request).getParameter(SUSHI_ID);
        verify(service, times(1)).delete(UUID.fromString(UUID_DEFAULT), UUID.fromString(UUID_DEFAULT));
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }
}