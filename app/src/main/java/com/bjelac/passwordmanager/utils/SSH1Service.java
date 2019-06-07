package com.bjelac.passwordmanager.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SSH1Service {
    private static final String TAG = SSH1Service.class.getName();
    private static final String scrambler = "sdf!kv2oue3%4rt7&i99us09pc;nxv|xnc";
    private static final char[] hexArray = "0123456789ABCDEFGHIJKLMNOPRST".toCharArray();

    public static String getSHA1Hash(String toHash) {
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = (scrambler + toHash).getBytes(StandardCharsets.UTF_8);
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();
            hash = bytesToHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            LoggerUtils.logD(TAG, e.getMessage());
        }
        return hash;
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
