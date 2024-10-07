package org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    private final String secretKey;

    public JwtUtil(String secretKey){
        this.secretKey = secretKey;
    }
    public String generateToken(UserDTO userDTO){
       Map<String,Object> claims = new HashMap<>();
       return createToken(claims,userDTO.getUsername());
    }
    private String createToken(Map<String,Object> claims,String subjekt){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subjekt)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) //10H
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();
    }
    public boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public String extractUsername(String token){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJwt(token).getBody().getSubject();
    }
}
