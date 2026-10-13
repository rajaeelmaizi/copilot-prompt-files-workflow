package com.example.petshop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "pet_orders",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_order_reference",
                columnNames = {"order_reference"}
        )
)
public class PetOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 8)
    private String orderReference;

    @Column(nullable = false, length = 80)
    private String customerName;

    @Column(nullable = false, length = 8)
    private String storeCode;

    @Column(nullable = false, length = 9)
    private String loyaltySeason;

    @Column(nullable = false, precision = 102, scale = 2)
    private BigDecimal amountPerPet;

    @Column(nullable = false)
    private int rewardPoints;

    @Column(nullable = false)
    private int loyaltyPoints;

    @Column(nullable = false)
    private int pickupPoints;

    @Column(nullable = false)
    private int basketPoints;

    @Column(nullable = false)
    private int adoptionPoints;

    @Column(nullable = false)
    private int subscriptionPoints;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    protected PetOrder() {
    }

    @SuppressWarnings("java:S107")
    public PetOrder(
            String orderReference,
            String customerName,
            String storeCode,
            String loyaltySeason,
            BigDecimal amountPerPet,
            int rewardPoints,
            int loyaltyPoints,
            int pickupPoints,
            int basketPoints,
            int adoptionPoints,
            int subscriptionPoints,
            OffsetDateTime createdAt
    ) {
        this.orderReference = orderReference;
        this.customerName = customerName;
        this.storeCode = storeCode;
        this.loyaltySeason = loyaltySeason;
        this.amountPerPet = amountPerPet;
        this.rewardPoints = rewardPoints;
        this.loyaltyPoints = loyaltyPoints;
        this.pickupPoints = pickupPoints;
        this.basketPoints = basketPoints;
        this.adoptionPoints = adoptionPoints;
        this.subscriptionPoints = subscriptionPoints;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public String getLoyaltySeason() {
        return loyaltySeason;
    }

    public BigDecimal getAmountPerPet() {
        return amountPerPet;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public int getPickupPoints() {
        return pickupPoints;
    }

    public int getBasketPoints() {
        return basketPoints;
    }

    public int getAdoptionPoints() {
        return adoptionPoints;
    }

    public int getSubscriptionPoints() {
        return subscriptionPoints;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
