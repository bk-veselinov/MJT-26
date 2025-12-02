package bg.sofia.uni.fmi.mjt.order.loader;

import bg.sofia.uni.fmi.mjt.order.domain.Order;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class OrderLoader {
    /**
     * Returns a list of orders read from the source Reader.
     *
     * @param reader the Reader with orders
     * @throws IllegalArgumentException if the reader is null
     */
    public static List<Order> load(Reader reader) {
        List<Order> orders = new ArrayList<>();

        if (reader == null) {
            throw new IllegalArgumentException("The provided Reader is null");
        }
        try (reader) {
            BufferedReader bufferedReader = (reader instanceof BufferedReader) ?
                    (BufferedReader) reader : new BufferedReader(reader);
            
            String headerLine = bufferedReader.readLine();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                orders.add(Order.of(line));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return orders;

    }
}