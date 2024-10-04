package org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MessagesDTO {
    private Long id;
    private List<String> messages;

    public MessagesDTO(Long id,List<String> messages){
       this.messages = messages;
       this.id = id;
    }
}
