package org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserDTO implements Serializable {
    private Long id;
    private String encryptedKey;
    private String username;

    public UserDTO(Long id, String encryptedKey, String username){
        this.encryptedKey = encryptedKey;
        this.username = username;
        this.id = id;
    }
}
