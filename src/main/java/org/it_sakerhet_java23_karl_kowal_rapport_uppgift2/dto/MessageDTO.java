package org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO implements Serializable{
    private Long id;       // Unikt ID för meddelandet
    private String content; // Meddelandets innehåll
}