package org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.service;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpSession;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.dto.UserDTO;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.entitys.UserEntity;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.repo.UserRepository;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.security.Hashing;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.security.KeyStoreUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.UUID;
@Service
public class UserService {

    private final UserRepository userRepository;
    private final KeyStoreUtil keyStoreUtil;
    private final Hashing hashing;

    @Autowired
    public UserService(UserRepository userRepository,KeyStoreUtil keyStoreUtil) {
        this.hashing = new Hashing();
        this.userRepository = userRepository;
        this.keyStoreUtil = keyStoreUtil;
    }

    public void registerUser(UserEntity user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Error! User with email already exists.");
        }
        String hashedPassword = hashing.hashPassword(user.getPassword());
        try {
            // Använd KeyGenerator för att skapa en korrekt AES-nyckel för användaren
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256); // 256-bitars nyckel
            SecretKey generatedSecretKey = keyGenerator.generateKey();
            // Konvertera SecretKey till Base64-sträng för lagring
            String secretKey = Base64.getEncoder().encodeToString(generatedSecretKey.getEncoded());
            // Kryptera användarens unika nyckel med root-nyckeln från KeyStore
            String encryptedKey = keyStoreUtil.encryptUserSecretKey(secretKey);
            // Spara den krypterade nyckeln och lösenordet i UserEntity
            user.setEncryptedKey(encryptedKey);
            user.setPassword(hashedPassword);
            // Spara användaren i databasen
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate and encrypt user secret key: " + e.getMessage(), e);
        }
    }

    public String loginUser(String email, String password, HttpSession session) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null || !hashing.verifyPassword(password, userEntity.getPassword())) {
            return null;
        }
        String encryptedKey = userEntity.getEncryptedKey();
        try {
            // Dekryptera användarens unika nyckel med root-nyckeln
            String decryptedKey = keyStoreUtil.decryptUserSecretKey(encryptedKey);
            // Generera JWT med den dekrypterade nyckeln
            UserDTO userDTO = new UserDTO((long) userEntity.getId(), encryptedKey, userEntity.getUsername());
            session.setAttribute("user", userDTO);
            JwtUtil jwtUtil = new JwtUtil(decryptedKey);
            return jwtUtil.generateToken(userDTO);

        } catch (Exception e) {
            return null;
        }
    }
}