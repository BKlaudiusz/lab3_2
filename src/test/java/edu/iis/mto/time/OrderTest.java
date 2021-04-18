package edu.iis.mto.time;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@ExtendWith(MockitoExtension.class)
class OrderTest {
    @Mock
    private Clock clockMock;
    private Order order;
    private final Instant Date = Instant.parse("2005-01-01T10:00:00Z");;
    @BeforeEach
    void setUp() {
        when(clockMock.getZone()).thenReturn(ZoneId.systemDefault());
        order = new Order(clockMock);
    }

    @Test
    void atTheSameTimeShouldReturnConfirmed() {

        Instant expirationTime = Date.plus(0, ChronoUnit.HOURS);
        when(clockMock.instant()).thenReturn(Date).thenReturn(expirationTime);

        try {
            order.submit();
            order.confirm();
            assertSame(Order.State.CONFIRMED, order.getOrderState());
        } catch (OrderExpiredException ignored) {
            fail("failed");
        }
    }
    @Test
    void after6845616HoursShouldReturnCanceled() {

        Instant expirationTime = Date.plus(6845616, ChronoUnit.HOURS);
        when(clockMock.instant()).thenReturn(Date).thenReturn(expirationTime);
        try {
            order.addItem(new OrderItem());
            order.submit();
            order.confirm();
            fail("failed");
        } catch (OrderExpiredException ignored) {

        }
        Order.State orderState = order.getOrderState();
        assertEquals(orderState, Order.State.CANCELLED);
    }

    @Test
    void afterHoursShouldReturnConfirmed() {

        Instant expirationTime = Date.plus(1, ChronoUnit.HOURS);
        when(clockMock.instant()).thenReturn(Date).thenReturn(expirationTime);
        try {
            order.addItem(new OrderItem());
            order.submit();
            order.confirm();

        } catch (OrderExpiredException ignored) {
            fail("failed");

        }
        Order.State orderState = order.getOrderState();
        assertEquals(orderState, Order.State.CONFIRMED);
    }
}
