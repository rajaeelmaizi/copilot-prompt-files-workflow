package com.example.petshop.service;

import com.example.petshop.dto.BreakdownResponse;
import com.example.petshop.dto.OrderRequest;
import com.example.petshop.dto.OrderResponse;
import com.example.petshop.exception.ConflictException;
import com.example.petshop.model.PetOrder;
import com.example.petshop.model.PickupMethod;
import com.example.petshop.repository.PetOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;

@Service
public class OrderService {

    static final BigDecimal BASKET_THRESHOLD = new BigDecimal("70.00");
    static final String UNIQUE_REFERENCE_CONSTRAINT = "uk_order_reference";
    private static final String DUPLICATE_MESSAGE = "An order with this reference already exists";
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    private final PetOrderRepository repository;

    public OrderService(PetOrderRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public OrderResponse register(OrderRequest request) {
        if (repository.existsByOrderReference(request.orderReference())) {
            LOGGER.warn("Duplicate order reference rejected");
            throw new ConflictException(DUPLICATE_MESSAGE);
        }

        BigDecimal amountPerPet = request.orderTotal()
                .divide(BigDecimal.valueOf(request.petCount()), 2, RoundingMode.HALF_UP);
        int loyaltyPoints = Boolean.TRUE.equals(request.loyaltyMember()) ? 50 : 0;
        int pickupPoints = pickupPoints(request.pickupMethod());
        int basketPoints = amountPerPet.compareTo(BASKET_THRESHOLD) >= 0 ? 15 : 0;
        int adoptionPoints = Boolean.TRUE.equals(request.adoptedPet()) ? 10 : 0;
        int subscriptionPoints = Boolean.TRUE.equals(request.subscription()) ? 15 : 0;
        int rewardPoints = loyaltyPoints + pickupPoints + basketPoints
                + adoptionPoints + subscriptionPoints;

        PetOrder order = new PetOrder(
                request.orderReference(),
                request.customerName(),
                request.storeCode(),
                request.loyaltySeason(),
                amountPerPet,
                rewardPoints,
                loyaltyPoints,
                pickupPoints,
                basketPoints,
                adoptionPoints,
                subscriptionPoints,
                OffsetDateTime.now()
        );

        try {
            PetOrder saved = repository.saveAndFlush(order);
            LOGGER.info("Order registered with {} reward points", rewardPoints);
            return toResponse(saved);
        } catch (DataIntegrityViolationException exception) {
            if (isUniqueReferenceViolation(exception)) {
                LOGGER.warn("Concurrent duplicate order reference rejected");
                throw new ConflictException(DUPLICATE_MESSAGE);
            }
            throw exception;
        }
    }

    private boolean isUniqueReferenceViolation(DataIntegrityViolationException exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof org.hibernate.exception.ConstraintViolationException violation) {
                String name = violation.getConstraintName();
                return name != null && name.toLowerCase().contains(UNIQUE_REFERENCE_CONSTRAINT);
            }
        }
        return false;
    }

    private int pickupPoints(PickupMethod method) {
        return switch (method) {
            case STORE_PICKUP -> 30;
            case PARCEL_LOCKER -> 20;
            case HOME_DELIVERY -> 0;
        };
    }

    private OrderResponse toResponse(PetOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderReference(),
                order.getCustomerName(),
                order.getStoreCode(),
                order.getLoyaltySeason(),
                order.getAmountPerPet(),
                order.getRewardPoints(),
                new BreakdownResponse(
                        order.getLoyaltyPoints(),
                        order.getPickupPoints(),
                        order.getBasketPoints(),
                        order.getAdoptionPoints(),
                        order.getSubscriptionPoints()
                ),
                order.getCreatedAt()
        );
    }
}
