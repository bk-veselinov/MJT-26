package bg.sofia.uni.fmi.mjt.order.analyzer;

import bg.sofia.uni.fmi.mjt.order.domain.Category;
import bg.sofia.uni.fmi.mjt.order.domain.Order;
import bg.sofia.uni.fmi.mjt.order.domain.PaymentMethod;
import bg.sofia.uni.fmi.mjt.order.domain.Status;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderAnalyzerImpl implements OrderAnalyzer {

    private static final int MIN_ALLOWED_CANCELLED_ORDERS_COUNT = 3;
    private static final int MIN_ALLOWED_CANCELLED_ORDERS_TOTAL_PRICE = 100;

    private final List<Order> orders;

    public OrderAnalyzerImpl(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public List<Order> allOrders() {
        return List.copyOf(orders);
    }

    @Override
    public List<Order> ordersByCustomer(String customer) {
        if (customer == null || customer.isBlank()) {
            throw new IllegalArgumentException("Customer is null or blank");
        }

        return List.copyOf(orders.stream()
                .filter(c -> c.customerName().equals(customer))
                .toList());
    }

    @Override
    public Map.Entry<LocalDate, Long> dateWithMostOrders() {
        return orders.stream().collect(Collectors.groupingBy(Order::date, Collectors.counting()))
                .entrySet()
                .stream().min(Comparator.comparingLong((Map.Entry<LocalDate, Long> e) -> e.getValue())
                        .reversed()
                        .thenComparing(Map.Entry::getKey)).orElse(null);

    }

    @Override
    public List<String> topNMostOrderedProducts(int n) {
        Map<String, Long> productsWithCount = orders.stream()
                .collect(Collectors.groupingBy(Order::product, Collectors.counting()));
        return productsWithCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue()
                        .reversed()
                        .thenComparing(Map.Entry::getKey))
                .limit(n)
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public Map<Category, Double> revenueByCategory() {
        return orders.stream().collect(Collectors
                .groupingBy(Order::category, Collectors.summingDouble(Order::totalSales)));
    }

    @Override
    public Set<String> suspiciousCustomers() {
        return orders.stream()
                .collect(Collectors
                        .groupingBy(Order::customerName,
                                Collectors
                                        .filtering(o -> o.status().equals(Status.CANCELLED)
                                                        && o.totalSales() < MIN_ALLOWED_CANCELLED_ORDERS_TOTAL_PRICE
                                                , Collectors.toList())))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > MIN_ALLOWED_CANCELLED_ORDERS_COUNT)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public Map<Category, PaymentMethod> mostUsedPaymentMethodForCategory() {
        if (orders == null || orders.isEmpty()) {
            return new HashMap<>();
        }
        return orders.stream()
                .collect(Collectors
                        .groupingBy(Order::category, Collectors
                                .groupingBy(Order::paymentMethod, Collectors.counting())))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> (e.getValue()
                                .entrySet()
                                .stream()
                                .min(Comparator.comparingLong((Map.Entry<PaymentMethod, Long> pc) -> pc.getValue())
                                        .reversed()
                                        .thenComparing(p -> p.getKey().name()))
                                .map(Map.Entry::getKey).orElse(PaymentMethod.DEBIT_CARD))));
    }

    @Override
    public String locationWithMostOrders() {
        return orders.stream()
                .collect(Collectors
                        .groupingBy(Order::customerLocation, Collectors.counting()))
                .entrySet()
                .stream().min(Comparator
                        .comparingLong((Map.Entry<String, Long> e) -> e.getValue())
                        .reversed()
                        .thenComparing(Map.Entry::getKey))
                .map(Map.Entry::getKey).orElse(null);
    }

    @Override
    public Map<Category, Map<Status, Long>> groupByCategoryAndStatus() {
        return orders.stream()
                .collect(Collectors
                        .groupingBy(Order::category, Collectors
                                .groupingBy(Order::status, Collectors.counting())));
    }
}
