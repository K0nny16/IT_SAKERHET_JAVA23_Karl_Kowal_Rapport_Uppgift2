package org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.service;

import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.dto.MessageDTO;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.entitys.MessagesEntity;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.entitys.UserEntity;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.repo.MessageRepository;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.repo.UserRepository;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.security.KeyStoreUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KeyStoreUtil keyStoreUtil;

    public boolean saveMessage(MessageDTO messageDTO, String username ){
        UserEntity userEntity = userRepository.findByUsername(username);
        if(userEntity == null) return false;
        String decryptedKey = keyStoreUtil.decryptKey(userEntity.getEncryptedKey());
        String encryptedContent = keyStoreUtil.encryptText(messageDTO.getContent(),decryptedKey);
        MessagesEntity messagesEntity = new MessagesEntity();
        messagesEntity.setMessageContent(encryptedContent);
        messagesEntity.setUser(userEntity);
        messageRepository.save(messagesEntity);
        return true;
    }
    public List<MessageDTO> getMessagesForUser(String username){
        UserEntity userEntity = userRepository.findByUsername(username);
        if(userEntity == null) return null;
        String decryptedKey = keyStoreUtil.decryptKey(userEntity.getEncryptedKey());
        return messageRepository.findByUser(userEntity).stream()
                .map(message -> new MessageDTO(
                        (long) message.getId(),
                        keyStoreUtil.decryptText(message.getMessageContent(),decryptedKey)
                ))
                .collect(Collectors.toList());
    }
    public boolean deleteMessage(int messageId,String username){
        UserEntity userEntity = userRepository.findByUsername(username);
        if(userEntity == null) return false;
        MessagesEntity message = messageRepository.findByIdAndUser(messageId,userEntity);
        if(message == null) return false;
        messageRepository.delete(message);
        return true;
    }
}
