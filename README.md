# README for Scanteen GDG Task 2

**Check out the Implementation video**       https://drive.google.com/file/d/1gL2VZOY1MDIuv0orFxCcF_BhvbAEQVVd/view?usp=sharing

---

## Overview

This document explains the functionality implemented in the `Scanteen` project for **QR Code Creation and Deletion Operations**. The code handles generating encrypted QR codes for orders, saving them as PNG files and in the database, and managing QR code retrieval and deletion.

---

### Package: `com.example.scanteen`

### Key Components:

#### 1. **Controller: `OrdersController`**
- **Purpose**: Manages HTTP requests for order-related operations.
- **Endpoints**:
  - `DELETE /orders/{orderId}`  
    Deletes the order and its associated QR code.
  - `POST /orders/qr`  
    Generates a QR code for the given order and saves it.
  - `GET /orders/{orderId}/qr`  
    Retrieves and decrypts the QR code for a specific order.

#### 2. **Service: `OrdersService`**
- **Purpose**: Implements business logic for handling QR codes.
- **Methods**:
  - `insertQRCode(Map<String, Object> qrRequest)`  
    - Takes order details and creates a QR code.
    - Encrypts order data using AES encryption.
    - Generates and saves the QR code as:
      - A PNG file in a specified folder.
      - An entry in the database with Base64-encoded data.
  - `getQRCodeByOrderId(Long orderId)`  
    - Retrieves the QR code for a given order.
    - Decrypts the data and prepares it for response.

#### 3. **Encryption Service: `AESService`**
- **Purpose**: Handles AES encryption and decryption.
- **Key Features**:
  - Reads the AES secret key from the configuration (`application.properties`).
  - Encrypts order data to protect sensitive information.
  - Decrypts QR code data for retrieval.

---

### Code Details:

#### **QR Code Generation**
- **Inputs**: 
  - `orderId` from the request body.
  - Corresponding `Orders` object fetched from the database.
- **Process**:
  1. **Data Preparation**: Concatenate `orderId`, `orderDate`, and `status`.
  2. **Encryption**: Secure data using AES encryption.
  3. **QR Code Creation**: Use encrypted data to generate a QR code.
  4. **Storage**: Save the QR code:
     - As a PNG file in the `C:\\Users\\udhya\\OneDrive\\Desktop\\OpenMITian\\` directory.
     - As Base64-encoded data in the database.
- **Response**: Returns QR code details (e.g., ID, status, and Base64 data).

#### **QR Code Retrieval**
- **Inputs**: `orderId` as a path parameter.
- **Process**:
  1. Fetch the QR code from the database.
  2. Decode and decrypt the QR code.
  3. Prepare response with order and QR details.

#### **Order Deletion**
- **Inputs**: `orderId` as a path parameter.
- **Process**:
  1. Locate the order in the database.
  2. Delete the order and its associated QR code.
  3. Return success or error message.

---

### AES Encryption/Decryption

#### Key Functions:
- **`encrypt(String message)`**:
  - Converts input string to bytes.
  - Encrypts using AES/GCM/NoPadding mode.
  - Returns Base64-encoded encrypted data and IV.
- **`decrypt(String encryptedMessage)`**:
  - Splits the encrypted message into data and IV.
  - Decrypts using the AES key and IV.
  - Returns the original message.

---

### Dependencies and Tools

- **Spring Boot**: For the REST API and dependency injection.
- **ZXing Library**: For QR code generation and decoding.
- **Base64 Encoding**: To encode and store QR codes.
- **AES Encryption**: To secure sensitive order data.
- **Java AWT and IO**: For handling QR code images.

---

### Folder Structure
- **Generated QR Codes**: Saved as PNG files in the `OpenMITian` folder.
- **Database**: Stores QR codes in Base64 format.

---

### Example Request and Response

#### **Insert QR Code**
- **Request**:  
  `POST /orders/qr`  
  ```json
  {
    "orderId": "7"
  }
  ```
- **Response**:  
  ```json
  {
  "qrCode": "iVBORw0KGgoAAAANSUhEUgAAAZAAAAGQAQAAAACoxAthAAACWElEQVR4Xu2UQW7DQAwD9wf+/y/3B2041NpOgKLYQxSgpRxYEsXRRXDG13aMV+H3CLIbQXYjyG4E2Y0gu/HnkDmIQ/lQ/6gO0uNxKkeQVoTSjqoZq8a+1CDNiGVlXg9/GVd3A4O0I5qq8xnn+qi8LMinELtXrm64DfIJpECm9gyuOlQgXL4gbcggdLCfnnIE6URuoUNeF/RNy7oiyFO8EZFv2qK5Spzl1ZdlljZIIzJ53Ki7jCwy8PTXF+TtiMZFyCJIL5nw2R+kHTldGA5bbNJrsKyIIG2INCk2i4YrBetZBOlDziPKTAfmahHnOEgjgkxmKo3jIdTO2hOkDcFuDVAZvzmSMt4gXcjEytsfl0xVy6fJFUG6EHlgfK5JUsXE0kUF6UJk0N2gHDLQ6MLrlN4SpAsxs/zmrxUQiwrSizBFFX7ojsolK0l7OmWQDmRJ2GjNqpPDFw3SisCgKBXhr0wtu3AF6USkA6gCE2LvbVWQZsTWGrKAjvw6DdKKeCpddl2WK46rEgYUpA/hcsQhfklH3ZVj+hWkEWFq7OailqSy/EEakemZNDKQHy9YxyUFaUN8Jhn8E+4X7pLKFqQLgbKNIdfzTW2clYL0I3VNjodVFnlNexqkE2GA4MpTbVkLzkMHaURWlCaPrWaQpdkV5Ir3IpNb2XAvTOkl8otjB2lEKMvvuRpsyOaCtCOznCrvfG2pTRoE6UeojDpWo11gQT6ESNaUGwrw4PwFaUVOUMTh/zqsYk1SBWlFBoGG6XQxVKOBiSBtyF4E2Y0guxFkN4LsRpDd+OfIN+AsB8as5FENAAAAAElFTkSuQmCC",
  "qrId": 3,
  "message": "QR code added successfully",
  "status": "not-scanned"
  }
  ```

#### **Get QR Code**
- **Request**:  
  `GET /orders/7/qr`
- **Response**:  
  ```json
  {
  "qrCode": "iVBORw0KGgoAAAANSUhEUgAAAZAAAAGQAQAAAACoxAthAAACUUlEQVR4Xu2TWW4jMRBD+wZ9/1vqBs5wKbUxQBDoY2hgwmpbS5Gvfghdr+O6/m78XEVOq8hpFTmtIqdV5LT+O2RdrBuHe/3ZX1gW9jmPo0gS4XGNG5ys0+J9fEVyCDOkbuy5QdqOIp9AeNZLwrZ3DSjyKWQpMzqxqKFfkU8gBvFReUYo0nEUiSLo2/TNZ0eRJDLF6LZh8tXhqSJP/VuEPqx24CnxT5AdBG22SAzRWU5SjBOrdphxKRJFnBSCw7qTw0aRSpE08qbqfSE3G9GemUWyCG2ELC2cROMot6YWySGWDRIaWE4d/MaKRBF7rcKAE9SbFCxFosjUgpsoD5xzk1BLVSSFqGd1G7eLTU8sEkTYvtGjGwN01k7FvyJJBID/c1RfNs3jhCJBBAozlEyRDfbfEmUVSSH0yQRV5MyQbrJIFJHNpFktDhNDjBfJIbjKSA1Pi4sJcxxbJIiQcS2A+6dpVDmvSBJZzG7boDpZEWaFFkkhiwqzkkvvSrNmtalIDlHBJydYXG/4ECm/GVAkhSxGaBMw0jp7DN1Fsghu47PbHZm3SGuRKEKHnpJ8sNFqAUuRJMI7KIX5YOYcK0xFgoio2ya5OYBnTAEmvUgOmSiZoTeDT8iTZJEYMgU3fsiQGCCfAYspkkJgYlgTIo3c0cEYDyoSRXjGCitv9NDpQVjlK5JDpA+9fZ6kqKdTJI1AGuN4wJJHykU+glB/SdbtYo4iZl6RHLJByNxs1SeLbEVyyMWC90aKurKFVHeaRbLIWRU5rSKnVeS0ipxWkdP65cgXs/s/jlRvHsQAAAAASUVORK5CYII=",
  "orderId": 5,
  "scannedBy": "Not Scanned",
  "qrId": 1,
  "actualData": "OrderID: 5\nOrderDate: 2025-01-01T23:34:57.316916\nStatus: ORDERED\n"
  }
  ```

#### **Delete Order**
- **Request**:  
  `DELETE /orders/5`
- **Response**:  
  ```json
  {
    "message": "Order deleted successfully"
  }
  ```

---
