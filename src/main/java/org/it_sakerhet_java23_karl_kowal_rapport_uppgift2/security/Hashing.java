package org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Hashing {
    private final BCryptPasswordEncoder passwordEncoder;
    public Hashing(){
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    public String hashPassword(String password){
        return passwordEncoder.encode(password);
    }
    public boolean verifyPassword(String rawPassword, String hashedPassword){
        return passwordEncoder.matches(rawPassword,hashedPassword);
    }
}
