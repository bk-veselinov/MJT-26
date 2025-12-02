package bg.sofia.uni.fmi.mjt.order.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testOfCreatesOrderFromValidLine() {
        String line = "1,01-01-23,T-Shirt,Clothing,20.50,2,41.00,John Doe,New York,Credit Card,COMPLETED";

        Order order = Order.of(line);

        assertNotNull(order);
        assertEquals("1", order.id());
        assertEquals(LocalDate.of(2023, 1, 1), order.date());
        assertEquals("T-Shirt", order.product());
        assertEquals(Category.CLOTHING, order.category());
        assertEquals(20.50, order.price());
        assertEquals(2, order.quantity());
        assertEquals(41.00, order.totalSales());
        assertEquals("John Doe", order.customerName());
        assertEquals("New York", order.customerLocation());
        assertEquals(PaymentMethod.CREDIT_CARD, order.paymentMethod());
        assertEquals(Status.COMPLETED, order.status());
    }

    @Test
    void testOfHandlesEnumsWithSpacesAndCase() {
        String line = "2,15-02-23,Blender,Home Appliances,50.0,1,50.0,Jane,Chicago,Amazon Pay,pending";

        Order order = Order.of(line);

        assertEquals(Category.HOME_APPLIANCES, order.category(), "Should parse 'Home Appliances' to HOME_APPLIANCES");
        assertEquals(PaymentMethod.AMAZON_PAY, order.paymentMethod(), "Should parse 'Amazon Pay' to AMAZON_PAY");
        assertEquals(Status.PENDING, order.status(), "Should parse 'pending' to PENDING after uppercasing");
    }

    @Test
    void testOfThrowsExceptionForInvalidDate() {
        String line = "3,2023-01-01,Book,Books,10.0,1,10.0,User,Loc,Paypal,Completed";

        assertThrows(DateTimeParseException.class, () -> Order.of(line),
                "Should throw DateTimeParseException for invalid date format");
    }

    @Test
    void testOfThrowsExceptionForInvalidNumber() {
        String line = "4,01-01-23,Book,Books,ten,1,10.0,User,Loc,Paypal,Completed";

        assertThrows(NumberFormatException.class, () -> Order.of(line),
                "Should throw NumberFormatException for non-numeric price");
    }

    @Test
    void testOfThrowsExceptionForInvalidEnum() {
        String line = "5,01-01-23,Item,Unknown Category,10.0,1,10.0,User,Loc,Paypal,Completed";

        assertThrows(IllegalArgumentException.class, () -> Order.of(line),
                "Should throw IllegalArgumentException for unknown category");
    }
}