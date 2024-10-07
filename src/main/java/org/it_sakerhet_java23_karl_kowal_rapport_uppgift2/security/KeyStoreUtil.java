package org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyStore;
import java.util.Base64;

@Component
public class KeyStoreUtil {

    //Hämtar dom olika attributerna från properties.
    @Value("${keystore.location}")
    private String keystoreLocation;

    @Value("${keystore.password}")
    private String keystorePassword;

    @Value("${keystore.alias}")
    private String keystoreAlias;

    private final Key privateKey;

    public KeyStoreUtil(){
        try{
            //Hämtar nyckel och kollar ifall den finns och har ett värde.
            InputStream keystoreStream = getClass().getClassLoader().getResourceAsStream(keystoreLocation);
            if(keystoreStream == null){
                throw new RuntimeException("Keystore file not found!");
            }
            //Skapar en instans av keystore med PKCS12
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            //Laddar keystore filen.
            keyStore.load(keystoreStream,keystorePassword.toCharArray());
            privateKey = keyStore.getKey(keystoreAlias,keystorePassword.toCharArray());
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Could not load key!");
        }
    }

    public String encryptKey(String key){
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE,privateKey);
            byte[] encryptedKey = cipher.doFinal(key.getBytes());
            return Base64.getEncoder().encodeToString(encryptedKey);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    public String decryptKey(String encryptedKey){
        try{
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE,privateKey);
            byte[] decodedKey = Base64.getDecoder().decode(encryptedKey);
            return new String(cipher.doFinal(decodedKey));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    public String encryptText(String text, String decryptedKey){
        try {
            SecretKeySpec secretKey = new SecretKeySpec(decryptedKey.getBytes(StandardCharsets.UTF_8),"AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE,secretKey);
            byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        }catch (Exception e){
            throw new RuntimeException("Could not encrypt text content",e);
        }
    }
    public String decryptText(String encryptedText,String decryptedKey){
        try {
            SecretKeySpec secretKey = new SecretKeySpec(decryptedKey.getBytes(StandardCharsets.UTF_8),"AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE,secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes,StandardCharsets.UTF_8);
        }catch (Exception e){
            throw new RuntimeException("Could not decrypt text!",e);
        }
    }
}
