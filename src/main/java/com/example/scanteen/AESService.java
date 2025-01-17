package com.example.scanteen;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class AESService {
    private final SecretKey key;
    private final int T_LEN = 128;

    // Read the base64-encoded key from application.properties
    public AESService(@Value("${aes.secret.key}") String base64Key) {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        this.key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }



    // Encrypt the input message
    public String encrypt(String message) throws Exception {
        byte[] messageInBytes = message.getBytes();
        Cipher encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = encryptionCipher.doFinal(messageInBytes);
        return encode(encryptedBytes) + ":" + encode(encryptionCipher.getIV());
    }

    // Decrypt the encrypted message
    public String decrypt(String encryptedMessage) throws Exception {
        String[] parts = encryptedMessage.split(":");
        byte[] messageInBytes = decode(parts[0]);
        byte[] iv = decode(parts[1]);

        Cipher decryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, iv);
        decryptionCipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] decryptedBytes = decryptionCipher.doFinal(messageInBytes);
        return new String(decryptedBytes);
    }

    // Encode byte array to Base64 string
    private String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    // Decode Base64 string to byte array
    private byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }
}
