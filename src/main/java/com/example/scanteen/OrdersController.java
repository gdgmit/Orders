package com.example.scanteen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    // Delete Order
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> deleteOrder(@PathVariable long orderId) {
        try {
            Map<String, Object> response = ordersService.deleteOrder(orderId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }


    // Insert New QR Code
    @PostMapping("/qr")
    public ResponseEntity<Map<String, Object>> insertQRCode(@RequestBody Map<String, Object> qrRequest) {
        try {
            Map<String, Object> response = ordersService.insertQRCode(qrRequest);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    // Get QR Code by Order ID
    @GetMapping("/{orderId}/qr")
    public ResponseEntity<Map<String, Object>> getQRCodeByOrderId(@PathVariable Long orderId) {
        try {
            Map<String, Object> qrDetails = ordersService.getQRCodeByOrderId(orderId);
            return new ResponseEntity<>(qrDetails, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    // Create Order
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> orderRequest) {
        try {
            Map<String, Object> response = ordersService.createOrder(orderRequest);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    // Fetch All Orders
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> fetchAllOrders() {
        try {
            List<Map<String, Object>> orders = ordersService.fetchAllOrders();
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(List.of(Map.of("message", e.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Fetch Single Order
    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> fetchSingleOrder(@PathVariable int orderId) {
        try {
            Map<String, Object> order = ordersService.fetchSingleOrder(orderId);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    // Update Order
    @PatchMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> updateOrder(@PathVariable int orderId, @RequestBody Map<String, Object> updateRequest) {
        try {
            Map<String, Object> response = ordersService.updateOrder(orderId, updateRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    // Update Order Status
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(@PathVariable int orderId, @RequestBody Map<String, String> statusRequest) {
        try {
            Map<String, Object> response = ordersService.updateOrderStatus(orderId, statusRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
