package bg.sofia.uni.fmi.mjt.order.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record Order(String id, LocalDate date, String product, Category category,
                    double price, int quantity, double totalSales,
                    String customerName, String customerLocation,
                    PaymentMethod paymentMethod, Status status) {

    private static final int ID_IDX = 0;
    private static final int DATE_IDX = 1;
    private static final int PRODUCT_IDX = 2;
    private static final int CATEGORY_IDX = 3;
    private static final int PRICE_IDX = 4;
    private static final int QUANTITY_IDX = 5;
    private static final int TOTAL_SALES_IDX = 6;
    private static final int CUSTOMER_NAME_IDX = 7;
    private static final int CUSTOMER_LOCATION_IDX = 8;
    private static final int PAYMENT_METHOD_IDX = 9;
    private static final int STATUS_IDX = 10;

    public static Order of(String line) {
        String[] splitLine = line.split(",");

        String id = splitLine[ID_IDX];

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
        LocalDate date = LocalDate.parse(splitLine[DATE_IDX], formatter);
        String product = splitLine[PRODUCT_IDX];
        Category category = Category.valueOf(String.join(
                "_", splitLine[CATEGORY_IDX].toUpperCase().split(" ")));
        double price = Double.parseDouble(splitLine[PRICE_IDX]);
        int quantity = Integer.parseInt(splitLine[QUANTITY_IDX]);
        double totalSales = Double.parseDouble(splitLine[TOTAL_SALES_IDX]);
        String customerName = splitLine[CUSTOMER_NAME_IDX];
        String customerLocation = splitLine[CUSTOMER_LOCATION_IDX];
        PaymentMethod paymentMethod = PaymentMethod.valueOf(String.join(
                "_", splitLine[PAYMENT_METHOD_IDX].toUpperCase().split(" ")));
        Status status = Status.valueOf(splitLine[STATUS_IDX].toUpperCase());

        return new Order(id, date, product,
                category, price, quantity,
                totalSales, customerName, customerLocation,
                paymentMethod, status);
    }
}
