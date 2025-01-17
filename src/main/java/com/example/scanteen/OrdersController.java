package com.example.scanteen;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.example.entities.Enums.OrderStatus;
import com.example.entities.Entities.Orders;
import com.example.entities.Enums.PaymentStatus;
import com.example.entities.Enums.TransactionType;
import com.example.entities.Repositories.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import org.springframework.http.ResponseEntity;
import java.util.Map;
@RestController @CrossOrigin @RequestMapping("/orders")
public class OrdersController {
    
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to ScanTeen API!";
    }

    @Autowired
    private OrdersRepository ordersRepository;

    // Get all products
    @GetMapping
    public List<Orders> getAllOrders() {
        return ordersRepository.findAll();
    }
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> requestBody) {
        try {
            // Parse request body
            //List<Map<String, Object>> items = (List<Map<String, Object>>) requestBody.get("items");
            BigDecimal totalAmount = new BigDecimal(requestBody.get("totalAmount").toString());
            String orderStatus = requestBody.get("order_status").toString();
            String paymentStatus = requestBody.get("payment_status").toString();
            String transactionType = requestBody.get("transaction_type").toString();

            // Validate ENUM fields
            if (!OrderStatus.PENDING.name().equalsIgnoreCase(orderStatus) ||
                !PaymentStatus.NOT_PAID.name().equalsIgnoreCase(paymentStatus) ||
                (!transactionType.equalsIgnoreCase("cash") && !transactionType.equalsIgnoreCase("online"))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid ENUM value"));
            }


            // Create Order Entity
            Orders order = new Orders();
            //order.setItems(items); // Assuming items field in Order entity is JSON-compatible
            order.setTotalAmount(totalAmount);
            order.setOrderStatus(OrderStatus.valueOf(orderStatus.toUpperCase()));
            order.setPaymentStatus(PaymentStatus.valueOf(paymentStatus.toUpperCase()));
            order.setTransactionType(TransactionType.valueOf(transactionType.toUpperCase()));

            // Save order to the database
            Orders savedOrder = ordersRepository.save(order);

            // Prepare JSON response
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", savedOrder.getOrderId());
            response.put("message", "Order created successfully");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            // Handle any errors during processing
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to create order"));
        }
    }
}
