package org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.security;

import org.mindrot.jbcrypt.BCrypt;

public class Hashing {
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}

