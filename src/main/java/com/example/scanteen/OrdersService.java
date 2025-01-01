
package com.example.scanteen;

import com.example.entities.Entities.OrderStatus;
import com.example.entities.Entities.Orders;
import com.example.entities.Entities.User;
import com.example.entities.Entities.OrderItem;
import com.example.entities.Entities.User;
import com.example.entities.Entities.Products;
import com.example.entities.Entities.PaymentStatus;
import com.example.entities.Entities.TransactionType;
import com.example.entities.Repositories.OrderItemRepository;
import com.example.entities.Repositories.OrdersRepository;
import com.example.entities.Repositories.ProductsRepository;
import com.example.entities.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrdersService {

    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductsRepository productsRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    // Delete Order
    public Map<String, Object> deleteOrder(long orderId) {
        Optional<Orders> orderOptional = ordersRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Orders order = orderOptional.get();

            // Remove associated order items
            orderItemRepository.deleteAll(order.getOrderItems());

            // Delete the order
            ordersRepository.delete(order);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Order deleted successfully");
            return response;
        } else {
            throw new NoSuchElementException("Order not found");
        }
    }


    public Map<String, Object> createOrder(Map<String, Object> orderRequest) {
        // Step 1: Create a new order instance
        Orders order = new Orders();
        order.setOrderDate(LocalDateTime.now());  // Set the current order date

        // Set order status and payment status from request
        try {
            order.setOrderStatus(OrderStatus.valueOf((String) orderRequest.get("order_status")));
            order.setPaymentStatus(PaymentStatus.valueOf((String) orderRequest.get("payment_status")));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status or payment status");
        }

        // Step 2: Handle total amount safely
        try {
            order.setTotalAmount(new BigDecimal((String) orderRequest.get("totalAmount")));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid total amount format");
        }

        if (orderRequest.containsKey("transactionType")) {
            order.setTransactionType(TransactionType.valueOf((String) orderRequest.get("transactionType")));
        } else {
            order.setTransactionType(null);  // Explicitly set as null if not provided
        }

        // Step 3: Handle userId as Integer and convert it to Long
        Object userIdObj = orderRequest.get("userId");
        Long userId = null;

        if (userIdObj instanceof Integer) {
            userId = Long.valueOf((Integer) userIdObj);  // Convert Integer to Long
        } else if (userIdObj instanceof Long) {
            userId = (Long) userIdObj;  // If already Long, use it directly
        } else {
            throw new IllegalArgumentException("Invalid userId format");
        }

        // Now fetch user from the database using the userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Step 4: Create the Set of OrderItems
        Set<OrderItem> items = new HashSet<>();
        List<Map<String, Object>> itemsList = (List<Map<String, Object>>) orderRequest.get("items");
        for (Map<String, Object> item : itemsList) {
            Long prId = (Long) item.get("prId");  // Product ID
            Integer quantity = (Integer) item.get("quantity");  // Product quantity

            // Fetch the product by prId
            Products product = productsRepository.findById(prId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Create a new OrderItem for each item in the order
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);  // Set product
            orderItem.setItemQuantity(quantity);  // Set quantity
            orderItem.setItemCurrentPrice(product.getPrSellingPrice());  // Set the current price from the product
            orderItem.setOrder(order);  // Link the order with the item

            items.add(orderItem);  // Add item to the set
        }

        order.setOrderItems(items);  // Set the items to the order

        // Step 5: Save the order and its items
        Orders savedOrder = ordersRepository.save(order);  // Save the order
        orderItemRepository.saveAll(items);  // Save the order items

        // Step 6: Prepare the response
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", savedOrder.getOrderId());  // Include the orderId in the response
        response.put("message", "Order created successfully");  // Success message

        return response;
    }





    // Fetch All Orders
    public List<Map<String, Object>> fetchAllOrders() {
        List<Orders> orders = ordersRepository.findAll();
        List<Map<String, Object>> orderResponse = new ArrayList<>();
        for (Orders order : orders) {
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("orderId", order.getOrderId());


            orderData.put("totalAmount", order.getTotalAmount());
            orderData.put("orderStatus", order.getOrderStatus().toString());
            orderData.put("createdAt", order.getOrderDate().toString());
            orderResponse.add(orderData);
        }
        return orderResponse;
    }

    // Fetch Single Order
    public Map<String, Object> fetchSingleOrder(int orderId) {
        Optional<Orders> orderOptional = ordersRepository.findById((long) orderId);
        if (orderOptional.isPresent()) {
            Orders order = orderOptional.get();
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("orderId", order.getOrderId());

            orderData.put("totalAmount", order.getTotalAmount());
            orderData.put("orderStatus", order.getOrderStatus().toString());
            // Add other necessary fields like orderItems, etc.
            return orderData;
        } else {
            throw new NoSuchElementException("Order not found");
        }
    }

    // Update Order
    public Map<String, Object> updateOrder(int orderId, Map<String, Object> updateRequest) {
        Optional<Orders> orderOptional = ordersRepository.findById((long) orderId);
        if (orderOptional.isPresent()) {
            Orders order = orderOptional.get();
            if (updateRequest.containsKey("orderStatus")) {
                order.setOrderStatus(OrderStatus.valueOf((String) updateRequest.get("orderStatus")));
            }
            if (updateRequest.containsKey("paymentId")) {
                order.setPaymentId((String) updateRequest.get("paymentId"));
            }
            if (updateRequest.containsKey("transactionType")) {
                order.setTransactionType(TransactionType.valueOf((String) updateRequest.get("transactionType")));
            }
            if (updateRequest.containsKey("paymentStatus")) {
                order.setPaymentStatus(PaymentStatus.valueOf((String) updateRequest.get("paymentStatus")));
            }

            Orders updatedOrder = ordersRepository.save(order);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Order updated successfully");
            return response;
        } else {
            throw new NoSuchElementException("Order not found");
        }
    }

    // Update Order Status
    public Map<String, Object> updateOrderStatus(int orderId, Map<String, String> statusRequest) {
        Optional<Orders> orderOptional = ordersRepository.findById((long) orderId);
        if (orderOptional.isPresent()) {
            Orders order = orderOptional.get();
            order.setOrderStatus(OrderStatus.valueOf(statusRequest.get("orderStatus")));
            ordersRepository.save(order);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Order status updated successfully");
            return response;
        } else {
            throw new NoSuchElementException("Order not found");
        }
    }
}
