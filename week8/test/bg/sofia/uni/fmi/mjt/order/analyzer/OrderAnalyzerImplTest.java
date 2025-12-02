package bg.sofia.uni.fmi.mjt.order.analyzer;

import bg.sofia.uni.fmi.mjt.order.domain.Category;
import bg.sofia.uni.fmi.mjt.order.domain.Order;
import bg.sofia.uni.fmi.mjt.order.domain.PaymentMethod;
import bg.sofia.uni.fmi.mjt.order.domain.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class OrderAnalyzerImplTest {

    private OrderAnalyzerImpl analyzer;
    private List<Order> orders;

    private Order createOrder(String id, int year, int month, int day, String product, Category category,
                              double price, int quantity, double totalSales, String customerName,
                              String customerLocation, PaymentMethod paymentMethod, Status status) {
        return new Order(id, LocalDate.of(year, month, day), product, category, price, quantity,
                totalSales, customerName, customerLocation, paymentMethod, status);
    }

    @BeforeEach
    void setUp() {
        orders = List.of(
                createOrder("1", 2023, 1, 10, "Laptop", Category.ELECTRONICS, 1200.0, 1, 1200.0, "Alice", "NY", PaymentMethod.CREDIT_CARD, Status.COMPLETED),
                createOrder("2", 2023, 1, 10, "Shirt", Category.CLOTHING, 50.0, 2, 100.0, "Alice", "NY", PaymentMethod.DEBIT_CARD, Status.COMPLETED),

                createOrder("3", 2023, 1, 11, "Book1", Category.BOOKS, 10.0, 1, 10.0, "Bob", "LA", PaymentMethod.PAYPAL, Status.CANCELLED),
                createOrder("4", 2023, 1, 11, "Pen", Category.BOOKS, 5.0, 1, 5.0, "Bob", "LA", PaymentMethod.PAYPAL, Status.CANCELLED),
                createOrder("5", 2023, 1, 11, "Eraser", Category.BOOKS, 2.0, 1, 2.0, "Bob", "LA", PaymentMethod.PAYPAL, Status.CANCELLED),
                createOrder("6", 2023, 1, 12, "Mouse", Category.ELECTRONICS, 15.0, 1, 15.0, "Bob", "LA", PaymentMethod.CREDIT_CARD, Status.CANCELLED),

                createOrder("7", 2023, 1, 13, "Sofa", Category.HOME_APPLIANCES, 150.0, 1, 150.0, "Charlie", "SF", PaymentMethod.DEBIT_CARD, Status.CANCELLED),

                createOrder("8", 2023, 1, 14, "Gloves", Category.CLOTHING, 30.0, 1, 30.0, "Alice", "NY", PaymentMethod.AMAZON_PAY, Status.PENDING),
                createOrder("12", 2023, 1, 17, "Hat", Category.CLOTHING, 20.0, 1, 20.0, "Alice", "NY", PaymentMethod.CREDIT_CARD, Status.COMPLETED),

                createOrder("9", 2023, 1, 15, "Sneakers", Category.FOOTWEAR, 80.0, 1, 80.0, "David", "Dallas", PaymentMethod.DEBIT_CARD, Status.COMPLETED),
                createOrder("10", 2023, 1, 16, "Boots", Category.FOOTWEAR, 100.0, 1, 100.0, "Eve", "Atlanta", PaymentMethod.CREDIT_CARD, Status.COMPLETED),
                createOrder("11", 2023, 1, 17, "Sandals", Category.FOOTWEAR, 40.0, 1, 40.0, "Frank", "Dallas", PaymentMethod.PAYPAL, Status.COMPLETED)
        );

        analyzer = new OrderAnalyzerImpl(orders);
    }

    @Test
    void testAllOrders() {
        List<Order> result = analyzer.allOrders();
        assertEquals(orders.size(), result.size());
        assertTrue(result.containsAll(orders));

        assertThrows(UnsupportedOperationException.class, () -> result.add(null));
    }

    @Test
    void testOrdersByExistingCustomer() {
        List<Order> aliceOrders = analyzer.ordersByCustomer("Alice");
        assertEquals(4, aliceOrders.size());
        assertTrue(aliceOrders.stream().allMatch(o -> o.customerName().equals("Alice")));
    }

    @Test
    void testOrdersByNonExistingCustomer() {
        List<Order> nonExistingOrders = analyzer.ordersByCustomer("Zoe");
        assertTrue(nonExistingOrders.isEmpty());
    }

    @Test
    void testOrdersByNullCustomerThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.ordersByCustomer(null));
    }

    @Test
    void testOrdersByBlankCustomerThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.ordersByCustomer("   "));
    }

    @Test
    void testGetDateWithMostOrders() {
        Map.Entry<LocalDate, Long> result = analyzer.dateWithMostOrders();

        assertEquals(LocalDate.of(2023, 1, 11), result.getKey());
        assertEquals(3, result.getValue());
    }

    @Test
    void testGetDateWithMostOrdersTieBreaker() {
        List<Order> tieOrders = List.of(
                createOrder("1", 2023, 1, 10, "P", Category.BOOKS, 1.0, 1, 1.0, "C1", "L", PaymentMethod.PAYPAL, Status.COMPLETED),
                createOrder("2", 2023, 1, 10, "P", Category.BOOKS, 1.0, 1, 1.0, "C2", "L", PaymentMethod.PAYPAL, Status.COMPLETED),
                createOrder("3", 2023, 1, 10, "P", Category.BOOKS, 1.0, 1, 1.0, "C3", "L", PaymentMethod.PAYPAL, Status.COMPLETED),
                createOrder("4", 2023, 1, 12, "P", Category.BOOKS, 1.0, 1, 1.0, "C4", "L", PaymentMethod.PAYPAL, Status.COMPLETED),
                createOrder("5", 2023, 1, 12, "P", Category.BOOKS, 1.0, 1, 1.0, "C5", "L", PaymentMethod.PAYPAL, Status.COMPLETED),
                createOrder("6", 2023, 1, 12, "P", Category.BOOKS, 1.0, 1, 1.0, "C6", "L", PaymentMethod.PAYPAL, Status.COMPLETED)
        );
        analyzer = new OrderAnalyzerImpl(tieOrders);

        Map.Entry<LocalDate, Long> result = analyzer.dateWithMostOrders();
        assertEquals(LocalDate.of(2023, 1, 10), result.getKey());
    }

    @Test
    void testGetDateWithMostOrdersEmpty() {
        analyzer = new OrderAnalyzerImpl(List.of());
        assertNull(analyzer.dateWithMostOrders());
    }

    @Test
    void testSuspiciousCustomers() {
        Set<String> suspicious = analyzer.suspiciousCustomers();

        assertEquals(1, suspicious.size());
        assertTrue(suspicious.contains("Bob"));
    }

    @Test
    void testSuspiciousCustomersEmptyList() {
        analyzer = new OrderAnalyzerImpl(List.of());
        Set<String> suspicious = analyzer.suspiciousCustomers();
        assertTrue(suspicious.isEmpty());
    }

    @Test
    void testMostUsedPaymentMethodForCategory() {
        Map<Category, PaymentMethod> result = analyzer.mostUsedPaymentMethodForCategory();

        assertEquals(PaymentMethod.CREDIT_CARD, result.get(Category.ELECTRONICS));

        assertEquals(PaymentMethod.PAYPAL, result.get(Category.BOOKS));

        assertEquals(PaymentMethod.DEBIT_CARD, result.get(Category.HOME_APPLIANCES));

        assertEquals(PaymentMethod.AMAZON_PAY, result.get(Category.CLOTHING));

        assertEquals(PaymentMethod.CREDIT_CARD, result.get(Category.FOOTWEAR));
    }

    @Test
    void testMostUsedPaymentMethodForCategoryEmptyList() {
        analyzer = new OrderAnalyzerImpl(List.of());
        Map<Category, PaymentMethod> result = analyzer.mostUsedPaymentMethodForCategory();
        assertTrue(result.isEmpty());
    }

    @Test
    void testLocationWithMostOrders() {
        assertEquals("LA", analyzer.locationWithMostOrders());
    }

    @Test
    void testLocationWithMostOrdersTieBreaker() {
        List<Order> tieOrders = List.of(
                createOrder("1", 2023, 1, 1, "P", Category.BOOKS, 1.0, 1, 1.0, "C1", "B-City", PaymentMethod.PAYPAL, Status.COMPLETED),
                createOrder("2", 2023, 1, 1, "P", Category.BOOKS, 1.0, 1, 1.0, "C2", "B-City", PaymentMethod.PAYPAL, Status.COMPLETED),
                createOrder("3", 2023, 1, 1, "P", Category.BOOKS, 1.0, 1, 1.0, "C3", "B-City", PaymentMethod.PAYPAL, Status.COMPLETED),
                createOrder("4", 2023, 1, 2, "P", Category.BOOKS, 1.0, 1, 1.0, "C4", "A-City", PaymentMethod.PAYPAL, Status.COMPLETED),
                createOrder("5", 2023, 1, 2, "P", Category.BOOKS, 1.0, 1, 1.0, "C5", "A-City", PaymentMethod.PAYPAL, Status.COMPLETED),
                createOrder("6", 2023, 1, 2, "P", Category.BOOKS, 1.0, 1, 1.0, "C6", "A-City", PaymentMethod.PAYPAL, Status.COMPLETED)
        );
        analyzer = new OrderAnalyzerImpl(tieOrders);

        assertEquals("A-City", analyzer.locationWithMostOrders());
    }

    @Test
    void testLocationWithMostOrdersEmptyList() {
        analyzer = new OrderAnalyzerImpl(List.of());
        assertNull(analyzer.locationWithMostOrders());
    }

    @Test
    void testGroupByCategoryAndStatus() {
        Map<Category, Map<Status, Long>> expected = Map.of(
                Category.ELECTRONICS, Map.of(
                        Status.COMPLETED, 1L,
                        Status.CANCELLED, 1L
                ),
                Category.CLOTHING, Map.of(
                        Status.COMPLETED, 2L,
                        Status.PENDING, 1L
                ),
                Category.BOOKS, Map.of(
                        Status.CANCELLED, 3L
                ),
                Category.HOME_APPLIANCES, Map.of(
                        Status.CANCELLED, 1L
                ),
                Category.FOOTWEAR, Map.of(
                        Status.COMPLETED, 3L
                )
        );

        Map<Category, Map<Status, Long>> actual = analyzer.groupByCategoryAndStatus();

        assertEquals(expected, actual);
    }

    @Test
    void testGroupByCategoryAndStatusEmptyList() {
        analyzer = new OrderAnalyzerImpl(List.of());
        Map<Category, Map<Status, Long>> result = analyzer.groupByCategoryAndStatus();
        assertTrue(result.isEmpty());
    }

    @Test
    void testTopNMostOrderedProductsStandard() {
        Order o1 = new Order("1", LocalDate.now(), "Laptop", Category.ELECTRONICS, 1000.0, 1, 1000.0, "C1", "L1", PaymentMethod.CREDIT_CARD, Status.COMPLETED);
        Order o2 = new Order("2", LocalDate.now(), "Mouse", Category.ELECTRONICS, 20.0, 1, 20.0, "C2", "L1", PaymentMethod.CREDIT_CARD, Status.COMPLETED);
        Order o3 = new Order("3", LocalDate.now(), "Laptop", Category.ELECTRONICS, 1000.0, 1, 1000.0, "C3", "L1", PaymentMethod.CREDIT_CARD, Status.COMPLETED);

        OrderAnalyzerImpl analyzer = new OrderAnalyzerImpl(List.of(o1, o2, o3));
        List<String> result = analyzer.topNMostOrderedProducts(2);

        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0));
        assertEquals("Mouse", result.get(1));
    }

    @Test
    void testTopNMostOrderedProductsWithAlphabeticalTieBreaker() {
        Order o1 = new Order("1", LocalDate.now(), "Banana", Category.BOOKS, 2.0, 1, 2.0, "C1", "L1", PaymentMethod.CREDIT_CARD, Status.COMPLETED);
        Order o2 = new Order("2", LocalDate.now(), "Apple", Category.BOOKS, 2.0, 1, 2.0, "C2", "L1", PaymentMethod.CREDIT_CARD, Status.COMPLETED);

        OrderAnalyzerImpl analyzer = new OrderAnalyzerImpl(List.of(o1, o2));
        List<String> result = analyzer.topNMostOrderedProducts(2);

        assertEquals(2, result.size());
        assertEquals("Apple", result.get(0));
        assertEquals("Banana", result.get(1));
    }

    @Test
    void testTopNMostOrderedProductsReturnsEmptyWhenNIsZero() {
        Order o1 = new Order("1", LocalDate.now(), "Laptop", Category.ELECTRONICS, 1000.0, 1, 1000.0, "C1", "L1", PaymentMethod.CREDIT_CARD, Status.COMPLETED);

        OrderAnalyzerImpl analyzer = new OrderAnalyzerImpl(List.of(o1));
        List<String> result = analyzer.topNMostOrderedProducts(0);

        assertTrue(result.isEmpty());
    }

    @Test
    void testTopNMostOrderedProductsReturnsAllWhenNExceedsDistinctProducts() {
        Order o1 = new Order("1", LocalDate.now(), "A", Category.CLOTHING, 10.0, 1, 10.0, "C1", "L1", PaymentMethod.CREDIT_CARD, Status.COMPLETED);
        Order o2 = new Order("2", LocalDate.now(), "B", Category.CLOTHING, 10.0, 1, 10.0, "C2", "L1", PaymentMethod.CREDIT_CARD, Status.COMPLETED);

        OrderAnalyzerImpl analyzer = new OrderAnalyzerImpl(List.of(o1, o2));
        List<String> result = analyzer.topNMostOrderedProducts(10);

        assertEquals(2, result.size());
        assertTrue(result.contains("A"));
        assertTrue(result.contains("B"));
    }

    @Test
    void testTopNMostOrderedProductsThrowsExceptionForNegativeN() {
        OrderAnalyzerImpl analyzer = new OrderAnalyzerImpl(Collections.emptyList());
        assertThrows(IllegalArgumentException.class, () -> analyzer.topNMostOrderedProducts(-1));
    }

    @Test
    void testRevenueByCategoryCalculatesCorrectSums() {
        Order o1 = new Order("1", LocalDate.now(), "Laptop", Category.ELECTRONICS, 100.0, 1, 100.0, "C1", "L1", PaymentMethod.CREDIT_CARD, Status.COMPLETED);
        Order o2 = new Order("2", LocalDate.now(), "Mouse", Category.ELECTRONICS, 50.0, 1, 50.0, "C2", "L1", PaymentMethod.CREDIT_CARD, Status.COMPLETED);
        Order o3 = new Order("3", LocalDate.now(), "Shirt", Category.CLOTHING, 20.0, 1, 20.0, "C3", "L1", PaymentMethod.CREDIT_CARD, Status.COMPLETED);

        OrderAnalyzerImpl analyzer = new OrderAnalyzerImpl(List.of(o1, o2, o3));
        Map<Category, Double> result = analyzer.revenueByCategory();

        assertEquals(150.0, result.get(Category.ELECTRONICS));
        assertEquals(20.0, result.get(Category.CLOTHING));
        assertEquals(2, result.size());
    }
}