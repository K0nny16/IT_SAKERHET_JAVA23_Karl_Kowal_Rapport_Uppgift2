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

        // Kryptera användarens unika nyckel med root-nyckeln
        SecretKey generteedSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String secretKey = Base64.getEncoder().encodeToString(generteedSecretKey.getEncoded());
        String encryptedKey = keyStoreUtil.encryptUserSecretKey(secretKey);
        user.setEncryptedKey(encryptedKey);
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    public String loginUser(String email, String password, HttpSession session) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null || !hashing.verifyPassword(password, userEntity.getPassword())) {
            return null;
        }

        String encryptedKey = userEntity.getEncryptedKey();
        System.out.println("User's Encrypted Key: " + encryptedKey);

        try {
            // Dekryptera användarens unika nyckel med root-nyckeln
            String decryptedKey = keyStoreUtil.decryptUserSecretKey(encryptedKey);
            // Generera JWT med den dekrypterade nyckeln
            UserDTO userDTO = new UserDTO((long) userEntity.getId(), encryptedKey, userEntity.getUsername());
            session.setAttribute("user", userDTO);
            JwtUtil jwtUtil = new JwtUtil(decryptedKey);
            return jwtUtil.generateToken(userDTO);

        } catch (Exception e) {
            System.out.println("Dekryptering misslyckades: " + e.getMessage());
            return null;
        }
    }
}