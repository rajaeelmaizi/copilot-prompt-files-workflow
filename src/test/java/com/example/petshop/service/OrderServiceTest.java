package com.example.petshop.service;

import com.example.petshop.dto.OrderRequest;
import com.example.petshop.dto.OrderResponse;
import com.example.petshop.exception.ConflictException;
import com.example.petshop.model.PetOrder;
import com.example.petshop.model.PickupMethod;
import com.example.petshop.repository.PetOrderRepository;
import com.example.petshop.validation.ConsecutiveSeasonValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private static final String DUPLICATE_MESSAGE = "An order with this reference already exists";

    @Mock
    private PetOrderRepository repository;

    private OrderService service;

    @BeforeEach
    void setUp() {
        service = new OrderService(repository);
    }

    @Test
    void shouldRegisterOrderWhenRequestIsValid() {
        OrderResponse response = register(order(true, PickupMethod.STORE_PICKUP, "210.00", 3, false, false));

        assertThat(response.orderReference()).isEqualTo("10000001");
        assertThat(response.customerName()).isEqualTo("Test Customer");
        assertThat(response.storeCode()).isEqualTo("20000002");
        assertThat(response.loyaltySeason()).isEqualTo("2026-2027");
        assertThat(response.amountPerPet()).isEqualByComparingTo("70.00");
        assertThat(response.rewardPoints()).isEqualTo(95);
        assertThat(response.createdAt()).isNotNull();
    }

    @Test
    void shouldAddUpTheFiveCriteriaWhenAllOfThemScore() {
        OrderResponse response = register(order(true, PickupMethod.STORE_PICKUP, "1000.00", 1, true, true));

        assertThat(response.rewardPoints()).isEqualTo(
                response.breakdown().loyalty()
                        + response.breakdown().pickup()
                        + response.breakdown().basket()
                        + response.breakdown().adoption()
                        + response.breakdown().subscription());
    }

    @Test
    void shouldGiveFiftyPointsWhenCustomerIsLoyaltyMember() {
        OrderResponse response = register(order(true, PickupMethod.HOME_DELIVERY, "10.00", 1, false, false));

        assertThat(response.breakdown().loyalty()).isEqualTo(50);
    }

    @Test
    void shouldGiveThirtyPointsWhenPickupIsInStore() {
        OrderResponse response = register(order(false, PickupMethod.STORE_PICKUP, "10.00", 1, false, false));

        assertThat(response.breakdown().pickup()).isEqualTo(30);
    }

    @Test
    void shouldGiveTwentyPointsWhenPickupIsParcelLocker() {
        OrderResponse response = register(order(false, PickupMethod.PARCEL_LOCKER, "10.00", 1, false, false));

        assertThat(response.breakdown().pickup()).isEqualTo(20);
    }

    @Test
    void shouldGiveZeroPointsWhenPickupIsHomeDelivery() {
        OrderResponse response = register(order(false, PickupMethod.HOME_DELIVERY, "10.00", 1, false, false));

        assertThat(response.breakdown().pickup()).isZero();
    }

    @Test
    void shouldGiveFifteenPointsWhenAmountPerPetEqualsThreshold() {
        OrderResponse response = register(order(false, PickupMethod.HOME_DELIVERY, "70.00", 1, false, false));

        assertThat(response.breakdown().basket()).isEqualTo(15);
    }

    @Test
    void shouldGiveZeroBasketPointsWhenAmountPerPetIsBelowThreshold() {
        OrderResponse response = register(order(false, PickupMethod.HOME_DELIVERY, "69.99", 1, false, false));

        assertThat(response.breakdown().basket()).isZero();
    }

    @Test
    void shouldGiveTenPointsWhenPetIsAdopted() {
        OrderResponse response = register(order(false, PickupMethod.HOME_DELIVERY, "10.00", 1, true, false));

        assertThat(response.breakdown().adoption()).isEqualTo(10);
    }

    @Test
    void shouldGiveFifteenPointsWhenOrderIsASubscription() {
        OrderResponse response = register(order(false, PickupMethod.HOME_DELIVERY, "10.00", 1, false, true));

        assertThat(response.breakdown().subscription()).isEqualTo(15);
    }

    @Test
    void shouldRoundAmountPerPetToTwoDecimalsHalfUpWhenItHasMoreDecimals() {
        OrderResponse response = register(order(false, PickupMethod.HOME_DELIVERY, "100.00", 3, false, false));

        assertThat(response.amountPerPet()).isEqualByComparingTo("33.33");
    }

    @Test
    void shouldCompareTheRoundedAmountWhenItRoundsUpToTheThreshold() {
        OrderResponse response = register(order(false, PickupMethod.HOME_DELIVERY, "209.99", 3, false, false));

        assertThat(response.amountPerPet()).isEqualByComparingTo("70.00");
        assertThat(response.breakdown().basket()).isEqualTo(15);
    }

    @Test
    void shouldKeepExactDecimalPrecisionWhenCalculatingAmountPerPet() {
        OrderResponse response = register(order(false, PickupMethod.HOME_DELIVERY, "0.10", 1, false, false));

        assertThat(response.amountPerPet()).isEqualTo(new BigDecimal("0.10"));
    }

    @Test
    void shouldRejectWithoutSavingWhenOrderReferenceAlreadyExists() {
        OrderRequest request = order(true, PickupMethod.STORE_PICKUP, "210.00", 3, false, false);
        when(repository.existsByOrderReference("10000001")).thenReturn(true);

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage(DUPLICATE_MESSAGE);
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void shouldReturnConflictWhenDatabaseDetectsConcurrentDuplicate() {
        OrderRequest request = order(true, PickupMethod.STORE_PICKUP, "210.00", 3, false, false);
        when(repository.saveAndFlush(any(PetOrder.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate",
                        new org.hibernate.exception.ConstraintViolationException(
                                "duplicate", new SQLException("duplicate"),
                                "UK_ORDER_REFERENCE_INDEX_2")));

        assertThatThrownBy(() -> service.register(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage(DUPLICATE_MESSAGE);
    }

    @Test
    void shouldPropagateIntegrityViolationWhenItIsNotTheDuplicateConstraint() {
        OrderRequest request = order(true, PickupMethod.STORE_PICKUP, "210.00", 3, false, false);
        DataIntegrityViolationException violation = new DataIntegrityViolationException(
                "other constraint",
                new org.hibernate.exception.ConstraintViolationException(
                        "other constraint", new SQLException("other constraint"),
                        "uk_other_constraint"));
        when(repository.saveAndFlush(any(PetOrder.class))).thenThrow(violation);

        assertThatThrownBy(() -> service.register(request))
                .isSameAs(violation)
                .isNotInstanceOf(ConflictException.class);
    }

    @Test
    void shouldPropagateIntegrityViolationWhenConstraintNameIsUnknown() {
        OrderRequest request = order(true, PickupMethod.STORE_PICKUP, "210.00", 3, false, false);
        DataIntegrityViolationException violation = new DataIntegrityViolationException("no cause");
        when(repository.saveAndFlush(any(PetOrder.class))).thenThrow(violation);

        assertThatThrownBy(() -> service.register(request)).isSameAs(violation);
    }

    @Test
    void shouldAcceptNullRequestInSeasonValidatorSoThatNotNullReportsIt() {
        assertThat(new ConsecutiveSeasonValidator().isValid(null, null)).isTrue();
    }

    @Test
    void shouldBuildEntityWithCalculatedValuesBeforeSaving() {
        OrderRequest request = order(true, PickupMethod.STORE_PICKUP, "210.00", 3, false, false);
        when(repository.saveAndFlush(any(PetOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.register(request);

        ArgumentCaptor<PetOrder> captor = ArgumentCaptor.forClass(PetOrder.class);
        verify(repository).saveAndFlush(captor.capture());
        PetOrder entity = captor.getValue();
        assertThat(entity.getId()).isNull();
        assertThat(entity.getOrderReference()).isEqualTo("10000001");
        assertThat(entity.getCustomerName()).isEqualTo("Test Customer");
        assertThat(entity.getStoreCode()).isEqualTo("20000002");
        assertThat(entity.getLoyaltySeason()).isEqualTo("2026-2027");
        assertThat(entity.getAmountPerPet()).isEqualByComparingTo("70.00");
        assertThat(entity.getRewardPoints()).isEqualTo(95);
        assertThat(entity.getLoyaltyPoints()).isEqualTo(50);
        assertThat(entity.getPickupPoints()).isEqualTo(30);
        assertThat(entity.getBasketPoints()).isEqualTo(15);
        assertThat(entity.getAdoptionPoints()).isZero();
        assertThat(entity.getSubscriptionPoints()).isZero();
        assertThat(entity.getCreatedAt()).isNotNull();
    }

    // Test data from FEATURES.md (C1–C5). The expected values come from the spec, never from the code.

    @Test
    void shouldScoreNinetyFiveWhenCaseC1() {
        assertCase(order(true, PickupMethod.STORE_PICKUP, "210.00", 3, false, false), "70.00", 95);
    }

    @Test
    void shouldScoreThirtyWhenCaseC2() {
        assertCase(order(false, PickupMethod.PARCEL_LOCKER, "209.97", 3, true, false), "69.99", 30);
    }

    @Test
    void shouldScoreZeroWhenCaseC3() {
        assertCase(order(false, PickupMethod.HOME_DELIVERY, "100.00", 4, false, false), "25.00", 0);
    }

    @Test
    void shouldScoreOneHundredTwentyWhenCaseC4() {
        assertCase(order(true, PickupMethod.STORE_PICKUP, "1000.00", 1, true, true), "1000.00", 120);
    }

    @Test
    void shouldScoreFifteenWhenCaseC5() {
        assertCase(order(false, PickupMethod.HOME_DELIVERY, "209.99", 3, false, false), "70.00", 15);
    }

    private void assertCase(OrderRequest request, String amountPerPet, int rewardPoints) {
        OrderResponse response = register(request);

        assertThat(response.amountPerPet()).isEqualByComparingTo(amountPerPet);
        assertThat(response.rewardPoints()).isEqualTo(rewardPoints);
    }

    private OrderResponse register(OrderRequest request) {
        when(repository.saveAndFlush(any(PetOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));
        OrderResponse response = service.register(request);
        ArgumentCaptor<PetOrder> captor = ArgumentCaptor.forClass(PetOrder.class);
        verify(repository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getRewardPoints()).isEqualTo(response.rewardPoints());
        return response;
    }

    private OrderRequest order(
            boolean loyaltyMember,
            PickupMethod pickupMethod,
            String orderTotal,
            int petCount,
            boolean adoptedPet,
            boolean subscription
    ) {
        return new OrderRequest(
                "10000001",
                "Test Customer",
                "20000002",
                "2026-2027",
                loyaltyMember,
                pickupMethod,
                new BigDecimal(orderTotal),
                petCount,
                adoptedPet,
                subscription
        );
    }
}
