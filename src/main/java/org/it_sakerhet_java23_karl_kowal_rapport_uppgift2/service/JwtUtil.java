package org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.dto.UserDTO;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    private final Key secretKey;
    public JwtUtil(String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Generera token med användarens information
    public String generateToken(UserDTO userDTO) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDTO.getUsername());
    }
    // Skapa en JWT-token
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 timmar
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    // Validera tokenen
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    // Extrahera användarnamn från tokenen
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }
    // Hämta claims från tokenen
    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }
}
