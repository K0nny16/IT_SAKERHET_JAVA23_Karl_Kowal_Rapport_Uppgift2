package org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.service;

import jakarta.servlet.http.HttpSession;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.dto.UserDTO;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.entitys.UserEntity;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.repo.UserRepository;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.security.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final Hashing hashing;

    public UserService(){
        this.hashing = new Hashing();
    }

    public void registerUser(UserEntity user){
        if(userRepository.findByEmail(user.getEmail())!= null){
            throw new IllegalArgumentException("Error!");
        }
        String hashedPassword = hashing.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    public boolean loginUser(String email, String password, HttpSession session){
        UserEntity userEntity = userRepository.findByEmail(email);
        if(userEntity == null || !hashing.verifyPassword(password,userEntity.getPassword())){
            return false;
        }
        UserDTO userDTO = new UserDTO((long) userEntity.getId(),userEntity.getEncryptedKey(),userEntity.getUsername());
        session.setAttribute("user",userDTO);
        return true;
    }
}
