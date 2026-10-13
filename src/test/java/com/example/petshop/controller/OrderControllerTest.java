package com.example.petshop.controller;

import com.example.petshop.dto.BreakdownResponse;
import com.example.petshop.dto.OrderResponse;
import com.example.petshop.exception.ApiError;
import com.example.petshop.exception.ConflictException;
import com.example.petshop.exception.GlobalExceptionHandler;
import com.example.petshop.exception.NotFoundException;
import com.example.petshop.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    private static final String PATH = "/orders";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService service;

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void shouldReturnCreatedWithEveryContractFieldWhenRequestIsValid() throws Exception {
        UUID id = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-10-08T12:00:00Z");
        when(service.register(any())).thenReturn(new OrderResponse(
                id, "10000001", "Test Customer", "20000002", "2026-2027",
                new BigDecimal("70.00"), 95,
                new BreakdownResponse(50, 30, 15, 0, 0), createdAt));

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(validOrder())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.orderReference").value("10000001"))
                .andExpect(jsonPath("$.customerName").value("Test Customer"))
                .andExpect(jsonPath("$.storeCode").value("20000002"))
                .andExpect(jsonPath("$.loyaltySeason").value("2026-2027"))
                .andExpect(jsonPath("$.amountPerPet").value(70.00))
                .andExpect(jsonPath("$.rewardPoints").value(95))
                .andExpect(jsonPath("$.breakdown.loyalty").value(50))
                .andExpect(jsonPath("$.breakdown.pickup").value(30))
                .andExpect(jsonPath("$.breakdown.basket").value(15))
                .andExpect(jsonPath("$.breakdown.adoption").value(0))
                .andExpect(jsonPath("$.breakdown.subscription").value(0))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldReturnBadRequestWhenOrderReferenceIsMissing() throws Exception {
        assertRequiredField("orderReference");
    }

    @Test
    void shouldReturnBadRequestWhenOrderReferenceIsNotEightDigits() throws Exception {
        assertBadRequest(with("orderReference", "123"));
    }

    @Test
    void shouldReturnBadRequestWhenCustomerNameIsMissing() throws Exception {
        assertRequiredField("customerName");
    }

    @Test
    void shouldReturnBadRequestWhenCustomerNameIsShorterThanTwoCharacters() throws Exception {
        assertBadRequest(with("customerName", "A"));
    }

    @Test
    void shouldReturnBadRequestWhenCustomerNameIsLongerThanEightyCharacters() throws Exception {
        assertBadRequest(with("customerName", "A".repeat(81)));
    }

    @Test
    void shouldReturnBadRequestWhenStoreCodeIsMissing() throws Exception {
        assertRequiredField("storeCode");
    }

    @Test
    void shouldReturnBadRequestWhenStoreCodeIsNotEightDigits() throws Exception {
        assertBadRequest(with("storeCode", "ABC12345"));
    }

    @Test
    void shouldReturnBadRequestWhenLoyaltySeasonIsMissing() throws Exception {
        assertRequiredField("loyaltySeason");
    }

    @Test
    void shouldReturnBadRequestWhenLoyaltySeasonHasWrongFormat() throws Exception {
        assertBadRequest(with("loyaltySeason", "2026/2027"));
    }

    @Test
    void shouldReturnBadRequestWhenLoyaltySeasonYearsAreNotConsecutive() throws Exception {
        assertBadRequest(with("loyaltySeason", "2026-2028"));
    }

    @Test
    void shouldReturnBadRequestWhenLoyaltyMemberIsMissing() throws Exception {
        assertRequiredField("loyaltyMember");
    }

    @Test
    void shouldReturnBadRequestWhenPickupMethodIsMissing() throws Exception {
        assertRequiredField("pickupMethod");
    }

    @Test
    void shouldReturnBadRequestWhenPickupMethodIsNotInTheEnum() throws Exception {
        assertBadRequest(with("pickupMethod", "DRONE"));
    }

    @Test
    void shouldReturnBadRequestWhenOrderTotalIsMissing() throws Exception {
        assertRequiredField("orderTotal");
    }

    @Test
    void shouldReturnBadRequestWhenOrderTotalIsNegative() throws Exception {
        assertBadRequest(with("orderTotal", new BigDecimal("-0.01")));
    }

    @Test
    void shouldReturnBadRequestWhenOrderTotalHasMoreThanTwoDecimals() throws Exception {
        assertBadRequest(with("orderTotal", new BigDecimal("10.001")));
    }

    @Test
    void shouldReturnBadRequestWhenPetCountIsMissing() throws Exception {
        assertRequiredField("petCount");
    }

    @Test
    void shouldReturnBadRequestWhenPetCountIsZero() throws Exception {
        assertBadRequest(with("petCount", 0));
    }

    @Test
    void shouldReturnBadRequestWhenPetCountIsAboveFifteen() throws Exception {
        assertBadRequest(with("petCount", 16));
    }

    @Test
    void shouldReturnBadRequestWhenPetCountIsDecimal() throws Exception {
        assertBadRequest(with("petCount", 2.5));
    }

    @Test
    void shouldReturnBadRequestWhenAdoptedPetIsMissing() throws Exception {
        assertRequiredField("adoptedPet");
    }

    @Test
    void shouldReturnBadRequestWhenSubscriptionIsMissing() throws Exception {
        assertRequiredField("subscription");
    }

    @Test
    void shouldReturnBadRequestWithContractErrorWhenJsonIsMalformed() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderReference\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.path").value(PATH));
        verify(service, never()).register(any());
    }

    @Test
    void shouldReturnConflictWithContractErrorWhenOrderIsDuplicated() throws Exception {
        when(service.register(any())).thenThrow(new ConflictException("An order with this reference already exists"));

        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(validOrder())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("An order with this reference already exists"))
                .andExpect(jsonPath("$.path").value(PATH));
    }

    @Test
    void shouldReturnBadRequestWhenThereAreConstraintViolations() {
        ResponseEntity<ApiError> response = exceptionHandler.handleConstraintViolation(
                new ConstraintViolationException(Set.of()), request());

        assertError(response, 400, "Invalid request");
    }

    @Test
    void shouldReturnBadRequestWhenThereIsATypeMismatch() {
        assertError(exceptionHandler.handleTypeMismatch(null, request()), 400, "Invalid data type");
    }

    @Test
    void shouldReturnNotFoundWhenResourceDoesNotExist() {
        ResponseEntity<ApiError> response = exceptionHandler.handleNotFound(
                new NotFoundException("Resource not found"), request());

        assertError(response, 404, "Resource not found");
    }

    @Test
    void shouldReturnNotFoundWhenRouteDoesNotExist() {
        assertError(exceptionHandler.handleUnknownRoute(null, request()), 404, "Resource not found");
    }

    @Test
    void shouldReturnMethodNotAllowedWhenMethodIsNotSupported() {
        assertError(exceptionHandler.handleMethodNotSupported(null, request()), 405, "Method not allowed");
    }

    @Test
    void shouldReturnNotAcceptableWhenAcceptHeaderIsNotSupported() {
        assertError(exceptionHandler.handleNotAcceptable(null, request()), 406, "Response format not acceptable");
    }

    @Test
    void shouldReturnUnsupportedMediaTypeWhenContentTypeIsNotSupported() {
        assertError(exceptionHandler.handleUnsupportedMediaType(null, request()), 415, "Unsupported content type");
    }

    @Test
    void shouldReturnInternalServerErrorForUnhandledException() {
        ResponseEntity<ApiError> response = exceptionHandler.handleGeneric(
                new RuntimeException("internal failure"), request());

        assertError(response, 500, "Internal server error");
    }

    private void assertError(ResponseEntity<ApiError> response, int status, String message) {
        assertThat(response.getStatusCode().value()).isEqualTo(status);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(status);
        assertThat(response.getBody().message()).isEqualTo(message);
        assertThat(response.getBody().path()).isEqualTo(PATH);
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    private HttpServletRequest request() {
        return new MockHttpServletRequest("POST", PATH);
    }

    private void assertRequiredField(String field) throws Exception {
        Map<String, Object> order = validOrder();
        order.remove(field);
        assertBadRequest(order);
    }

    private void assertBadRequest(Map<String, Object> order) throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(order)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.path").value(PATH));
        verify(service, never()).register(any());
    }

    private Map<String, Object> with(String field, Object value) {
        Map<String, Object> order = validOrder();
        order.put(field, value);
        return order;
    }

    private String json(Map<String, Object> order) throws Exception {
        return objectMapper.writeValueAsString(order);
    }

    private Map<String, Object> validOrder() {
        Map<String, Object> order = new LinkedHashMap<>();
        order.put("orderReference", "10000001");
        order.put("customerName", "Test Customer");
        order.put("storeCode", "20000002");
        order.put("loyaltySeason", "2026-2027");
        order.put("loyaltyMember", true);
        order.put("pickupMethod", "STORE_PICKUP");
        order.put("orderTotal", new BigDecimal("210.00"));
        order.put("petCount", 3);
        order.put("adoptedPet", false);
        order.put("subscription", false);
        return order;
    }
}
