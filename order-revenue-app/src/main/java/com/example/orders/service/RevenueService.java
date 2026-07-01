            package com.example.orders.service;

            import com.example.orders.model.CustomerType;
            import com.example.orders.model.Order;
            import org.springframework.stereotype.Service;

            import java.math.BigDecimal;
            import java.math.RoundingMode;
            import java.time.YearMonth;
            import java.util.Comparator;
            import java.util.List;
            import java.util.Map;
            import java.util.stream.Collectors;

            /**
             * Calculates monthly revenue from a list of orders.
             */
            @Service
            public class RevenueService {

                private static final BigDecimal PREMIUM_DISCOUNT_RATE = new BigDecimal("0.10");

                /**
                 * Returns total revenue per calendar month, sorted chronologically.
                 */
                public Map<YearMonth, BigDecimal> calculateMonthlyRevenue(List<Order> orders) {
                    if (orders == null) {
                        return Map.of();
                    }

                    return orders.stream()
                            .filter(this::isValidOrder)
                            .collect(Collectors.groupingBy(
                                    order -> YearMonth.from(order.getOrderDate()),
                                    Collectors.reducing(
                                            BigDecimal.ZERO,
                                            this::netAmount,
                                            BigDecimal::add
                                    )
                            ))
                            .entrySet()
                            .stream()
                            .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (a, b) -> a,
                                    java.util.LinkedHashMap::new
                            ));
                }

                /**
                 * Skips orders that should not be counted in revenue.
                 */
                private boolean isValidOrder(Order order) {
                    return order != null
                            && order.getAmount() != null
                            && order.getOrderDate() != null
                            && order.getAmount().compareTo(BigDecimal.ZERO) >= 0;
                }

                /**
                 * Applies the PREMIUM discount where needed.
                 */
                private BigDecimal netAmount(Order order) {
                    BigDecimal amount = order.getAmount();
                    if (order.getCustomerType() == CustomerType.PREMIUM) {
                        BigDecimal discount = amount.multiply(PREMIUM_DISCOUNT_RATE);
                        amount = amount.subtract(discount);
                    }
                    return amount.setScale(2, RoundingMode.HALF_UP);
                }
            }
