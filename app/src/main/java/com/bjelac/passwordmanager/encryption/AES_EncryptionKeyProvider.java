package com.bjelac.passwordmanager.encryption;

import com.bjelac.passwordmanager.utils.ApplicationContextProvider;
import com.bjelac.passwordmanager.utils.LoggerUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class AES_EncryptionKeyProvider {
    private static final String TAG = AES_EncryptionKeyProvider.class.getName();

    private static AES_EncryptionKeyProvider singleInstance = null;
    private char[] keyStorePassword = "s0fa$jio65rfs%dns".toCharArray();
    private byte[] keyStart = "jvu,ep3%df?ksk4§".getBytes();
    private String secretKeyAlias = "PWDM";
    private KeyStore ks;
    private static SecretKey encryptionKey;

    public static AES_EncryptionKeyProvider getInstance() {
        if (singleInstance == null) {
            singleInstance = new AES_EncryptionKeyProvider();
            encryptionKey = singleInstance.loadSecretKey();
        }
        return singleInstance;
    }

    private SecretKey loadSecretKey() {
        SecretKey secretKey = null;
        try {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, keyStorePassword);
            secretKey = getSecretKey();
        } catch (Exception e) {
            LoggerUtils.logD(TAG, e.getMessage());
        }
        return secretKey;
    }

    private SecretKey generateKey() throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
        //SecretKey secretKey = KeyGenerator.getInstance(AES_EncryptionService.algorithm).generateKey();

        KeyGenerator kgen = KeyGenerator.getInstance(AES_EncryptionService.algorithm);
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(keyStart);
        kgen.init(128, sr);
        SecretKey secretKey = kgen.generateKey();

        saveKey(secretKey);
        return secretKey;
    }

    private void saveKey(SecretKey encryptionKey) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(encryptionKey);
        ks.setEntry(secretKeyAlias, secretKeyEntry, null);

        FileOutputStream fos = new FileOutputStream(ApplicationContextProvider.getContext().getFilesDir().getAbsolutePath() + "/OEKeyStore");
        ks.store(fos, keyStorePassword);
    }

    private SecretKey getSecretKey() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableEntryException {
        SecretKey secretKey;
        try {
            FileInputStream fis = new FileInputStream(ApplicationContextProvider.getContext().getFilesDir().getAbsolutePath() + "/OEKeyStore");
            ks.load(fis, keyStorePassword);

            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) ks.getEntry(secretKeyAlias, null);

            if (secretKeyEntry == null) {
                secretKey = generateKey();
            } else {
                secretKey = secretKeyEntry.getSecretKey();
            }
        } catch (FileNotFoundException ex) {
            LoggerUtils.logD(TAG, ex.getMessage() + ". Creating new KeyStore file.");
            secretKey = generateKey();
        }
        return secretKey;
    }

    public SecretKey getEncryptionKey() {
        return encryptionKey;
    }

    public static void destroyInstance() {
        singleInstance = null;
    }
}