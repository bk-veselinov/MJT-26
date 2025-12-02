package bg.sofia.uni.fmi.mjt.order.loader;

import bg.sofia.uni.fmi.mjt.order.domain.Category;
import bg.sofia.uni.fmi.mjt.order.domain.Order;
import bg.sofia.uni.fmi.mjt.order.domain.PaymentMethod;
import bg.sofia.uni.fmi.mjt.order.domain.Status;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class OrderLoaderTest {

    private static final String HEADER = "id,date,product,category,price,quantity,totalSales,customerName,customerLocation,paymentMethod,status\n";

    // Create mock Order objects for stubbing the static method
    private final Order mockOrder1 = new Order("1", LocalDate.of(2023, 1, 1), "P1", Category.BOOKS, 10.0, 1, 10.0, "C1", "L1", PaymentMethod.CREDIT_CARD, Status.COMPLETED);
    private final Order mockOrder2 = new Order("2", LocalDate.of(2023, 1, 2), "P2", Category.CLOTHING, 20.0, 1, 20.0, "C2", "L2", PaymentMethod.PAYPAL, Status.PENDING);

    @Test
    void testLoadWithValidData() {
        String line1 = "1,01-01-23,Product1,Books,10.0,1,10.0,Customer1,Location1,Credit Card,COMPLETED";
        String line2 = "2,02-01-23,Product2,Clothing,20.0,1,20.0,Customer2,Location2,PayPal,PENDING";
        String ordersData = HEADER + line1 + "\n" + line2;
        Reader reader = new StringReader(ordersData);

        try (MockedStatic<Order> mockedOrder = mockStatic(Order.class)) {
            mockedOrder.when(() -> Order.of(line1)).thenReturn(mockOrder1);
            mockedOrder.when(() -> Order.of(line2)).thenReturn(mockOrder2);

            List<Order> loadedOrders = OrderLoader.load(reader);

            assertEquals(2, loadedOrders.size());
            assertEquals(mockOrder1, loadedOrders.get(0));
            assertEquals(mockOrder2, loadedOrders.get(1));

            mockedOrder.verify(() -> Order.of(line1), times(1));
            mockedOrder.verify(() -> Order.of(line2), times(1));
            mockedOrder.verify(() -> Order.of(anyString()), times(2));
        }
    }

    @Test
    void testLoadWithEmptyData() {
        String ordersData = HEADER;
        Reader reader = new StringReader(ordersData);

        try (MockedStatic<Order> mockedOrder = mockStatic(Order.class)) {
            List<Order> loadedOrders = OrderLoader.load(reader);

            assertNotNull(loadedOrders);
            assertTrue(loadedOrders.isEmpty());

            mockedOrder.verify(() -> Order.of(anyString()), never());
        }
    }

    @Test
    void testLoadWithNullReaderThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> OrderLoader.load(null),
                "Should throw IllegalArgumentException when reader is null");
    }

    @Test
    void testLoadWithReaderThrowingIOException() throws IOException {
        Reader readerMock = mock(Reader.class);

        when(readerMock.read(any(char[].class), anyInt(), anyInt()))
                .thenThrow(new IOException("Simulated read error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> OrderLoader.load(readerMock),
                "Should throw RuntimeException when IOException occurs");

        assertTrue(exception.getCause() instanceof IOException, "RuntimeException should wrap IOException");
        assertEquals("Simulated read error", exception.getCause().getMessage());
    }
}