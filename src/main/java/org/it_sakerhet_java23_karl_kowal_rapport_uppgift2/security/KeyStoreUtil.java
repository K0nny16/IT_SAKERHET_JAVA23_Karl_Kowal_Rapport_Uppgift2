package org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.security;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Base64;

@Component
public class KeyStoreUtil {

    private final SecretKey rootKey;

    public KeyStoreUtil() {
        try {
            String keystorePath = "src/main/resources/encryption_keystore.p12";
            String keystorePassword = "password123";
            String alias = "encryptionkey";

            // Ladda keystoren och nyckeln
            try (InputStream keyStoreFile = new FileInputStream(keystorePath)) {
                this.rootKey = loadSecretKeyFromKeyStore(keyStoreFile, keystorePassword, alias);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load root key from KeyStore: " + e.getMessage(), e);
        }
    }

    // Metod för att ladda SecretKey från keystore
    private SecretKey loadSecretKeyFromKeyStore(InputStream keyStoreFile, String keyStorePassword, String keyAlias) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(keyStoreFile, keyStorePassword.toCharArray());
            KeyStore.ProtectionParameter entryPassword = new KeyStore.PasswordProtection(keyStorePassword.toCharArray());
            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(keyAlias, entryPassword);
            return secretKeyEntry.getSecretKey();

        } catch (Exception e) {
            throw new RuntimeException("Failed to load SecretKey from KeyStore: " + e.getMessage(), e);
        }
    }

    // Metod för att kryptera användarens hemliga nyckel med root-nyckeln
    public String encryptUserSecretKey(String userSecretKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, rootKey);
            byte[] encryptedKeyBytes = cipher.doFinal(userSecretKey.getBytes());
            return Base64.getEncoder().encodeToString(encryptedKeyBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt user secret key: " + e.getMessage(), e);
        }
    }

    // Metod för att dekryptera användarens hemliga nyckel med root-nyckeln
    public String decryptUserSecretKey(String encryptedUserSecretKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, rootKey);
            byte[] decodedKeyBytes = Base64.getDecoder().decode(encryptedUserSecretKey);
            byte[] decryptedKeyBytes = cipher.doFinal(decodedKeyBytes);
            return new String(decryptedKeyBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt user secret key: " + e.getMessage(), e);
        }
    }

    // Metoder för att kryptera och dekryptera användarmeddelanden
    public String encryptMessage(String message, String secretKey) {
        try {
            SecretKey userSecretKey = new SecretKeySpec(secretKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, userSecretKey);
            byte[] encryptedMessageBytes = cipher.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(encryptedMessageBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt message: " + e.getMessage(), e);
        }
    }

    public String decryptMessage(String encryptedMessage, String secretKey) {
        try {
            SecretKey userSecretKey = new SecretKeySpec(secretKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, userSecretKey);
            byte[] decodedMessageBytes = Base64.getDecoder().decode(encryptedMessage);
            byte[] decryptedMessageBytes = cipher.doFinal(decodedMessageBytes);
            return new String(decryptedMessageBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt message: " + e.getMessage(), e);
        }
    }
}