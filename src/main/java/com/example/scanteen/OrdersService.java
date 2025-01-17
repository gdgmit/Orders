package com.example.scanteen;

import com.example.entities.Entities.*;
import com.example.entities.Repositories.*;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


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
    @Autowired
    private QRRepository qrRepository;

    @Autowired
    private AESService aesService;

    // Delete Order Cascading Ordered Items Deletion on ordered_items table
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

    // Post Mapping  Generate QR code for  Order and store in QR Table and also in a folder as png
    public Map<String, Object> insertQRCode(Map<String, Object> qrRequest) throws Exception {
        Long orderId = Long.valueOf(qrRequest.get("orderId").toString());
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found! Invalid Order ID."));

        // Build QR Data : OrderId , OrderDate and Status
        String originalQRData = "OrderID: " + order.getOrderId() + "\n" +
                "OrderDate: " + order.getOrderDate() + "\n" +
                "Status: " + order.getOrderStatus() + "\n";

        // Encrypt QR Data

        String encryptedQRData = aesService.encrypt(originalQRData);

        // Generate QR Code with Encrypted Data
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(encryptedQRData, BarcodeFormat.QR_CODE, 400, 400);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        String qrCodeBase64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());

        String folderPath = "C:\\Users\\udhya\\OneDrive\\Desktop\\OpenMITian\\";

        String fileName = "Order_" + orderId + ".png";

        File imageFile = new File(folderPath + fileName);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", imageFile.toPath());
        System.out.println("QR Code saved at: " + imageFile.getAbsolutePath());
        System.out.println("Base64 QR Code Size: " + qrCodeBase64.length());
        // Save QR Code in Database

        QR qr = new QR();
        qr.setOrder(order);
        qr.setQrCode(qrCodeBase64);
        QR savedQR = qrRepository.save(qr);

        // Response
        Map<String, Object> response = new HashMap<>();
        response.put("qrId", savedQR.getQrId());
        response.put("qrCode", savedQR.getQrCode());
        response.put("message","QR code added successfully");
        response.put("status", "not-scanned");
        return response;
    }

    // Retrieve and Decrypt QR Code Data
    public Map<String, Object> getQRCodeByOrderId(Long orderId) throws Exception {
        QR qr = qrRepository.findAll().stream()
                .filter(qrItem -> qrItem.getOrder().getOrderId().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new Exception("QR Code not found for the order"));

        // Decode and Decrypt QR Code
        byte[] qrCodeBytes = Base64.getDecoder().decode(qr.getQrCode());
        ByteArrayInputStream inputStream = new ByteArrayInputStream(qrCodeBytes);
        BufferedImage qrImage = ImageIO.read(inputStream);

        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(qrImage)
        ));
        String encryptedQRData = new QRCodeReader().decode(binaryBitmap).getText();
        String decryptedQRData = aesService.decrypt(encryptedQRData);

        // Response
        Map<String, Object> response = new HashMap<>();
        response.put("qrId", qr.getQrId());
        response.put("orderId", qr.getOrder().getOrderId());
        response.put("qrCode", qr.getQrCode());
        response.put("actualData", decryptedQRData);
        response.put("scannedBy", qr.getScannedBy() == null ? "Not Scanned" :
                qr.getScannedBy().getFirstName() + " " + qr.getScannedBy().getLastName());

        return response;
    }


}
