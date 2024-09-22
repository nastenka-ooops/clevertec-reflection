package ru.clevertec;

import ru.clevertec.domain.Customer;
import ru.clevertec.domain.Order;
import ru.clevertec.domain.Product;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // Создание Product
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        Map<UUID, BigDecimal> priceMap1 = new HashMap<>();
        priceMap1.put(productId1, new BigDecimal("99.99"));
        priceMap1.put(productId2, new BigDecimal("79.99"));
        Product product1 = new Product(productId1, "Product 1", 99.99, priceMap1);

        // Создание списка продуктов
        List<Product> productList = new ArrayList<>();
        productList.add(product1);

        // Создание Order
        UUID orderId = UUID.randomUUID();
        OffsetDateTime createDate = OffsetDateTime.now();
        Order order = new Order(orderId, productList, createDate);

        // Создание списка заказов
        List<Order> orders = new ArrayList<>();
        orders.add(order);

        // Создание Customer
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "John", "Doe", OffsetDateTime.now(), orders);

        String json = JsonSerializer.toJson(customer);
        System.out.println("Serialized JSON: " + json);

        Customer deserializedCustomer = JsonDeserializer.fromJson(json, Customer.class);
        System.out.println("Deserialized Customer: " + deserializedCustomer.getFirstName() + " " + deserializedCustomer.getLastName());
    }
}