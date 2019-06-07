package com.bjelac.passwordmanager.encryption;

import com.bjelac.passwordmanager.utils.LoggerUtils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES_EncryptionService {
    private static final String TAG = AES_EncryptionService.class.getName();
    private static final String cypherInstance = "AES/CBC/PKCS5Padding";
    static final String algorithm = "AES";

    public AES_EncryptionService() {
    }

    public String encrypt(String toEncrypt, byte[] encryptionKey, byte[] initializationVector) {
        String result = "";
        if (toEncrypt != null && !toEncrypt.isEmpty()) {
            byte[] encrypted;
            try {
                SecretKeySpec secretKey = new SecretKeySpec(encryptionKey, algorithm);
                Cipher cipher = Cipher.getInstance(cypherInstance);
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(initializationVector));
                encrypted = cipher.doFinal(toEncrypt.getBytes());
                result = new String(encrypted, StandardCharsets.ISO_8859_1);

            } catch (Exception e) {
                LoggerUtils.logD(TAG, e.getMessage());
            }
        }
        return result;
    }

    public String decrypt(String toDecrypt, byte[] decryptionKey, byte[] initializationVector) {
        String result = null;
        if (toDecrypt != null && !toDecrypt.isEmpty()) {
            try {
                SecretKeySpec secretKey = new SecretKeySpec(decryptionKey, algorithm);
                Cipher cipher = Cipher.getInstance(cypherInstance);
                cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(initializationVector));
                byte[] test = toDecrypt.getBytes(StandardCharsets.ISO_8859_1);
                result = new String(cipher.doFinal(test));
            } catch (Exception e) {
                LoggerUtils.logD(TAG, e.getMessage());
            }
        }
        return result;
    }

    public byte[] getRandomVi(){
        byte[] initializationVector = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(initializationVector);
        return initializationVector;
    }
}

