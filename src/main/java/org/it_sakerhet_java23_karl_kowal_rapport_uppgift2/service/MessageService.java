package org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.service;

import org.apache.catalina.User;
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

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final KeyStoreUtil keyStoreUtil;

    @Autowired
    public MessageService(MessageRepository messageRepository, UserRepository userRepository, KeyStoreUtil keyStoreUtil){
       this.messageRepository = messageRepository;
       this.keyStoreUtil = keyStoreUtil;
       this.userRepository = userRepository;
    }

    public boolean saveMessage(MessageDTO messageDTO, String username ){
        UserEntity userEntity = userRepository.findByUsername(username);
        if(userEntity == null) return false;

        String decryptedKey = keyStoreUtil.decryptUserSecretKey(userEntity.getEncryptedKey());
        String encryptedContent = keyStoreUtil.encryptMessage(messageDTO.getContent(),decryptedKey);

        MessagesEntity messagesEntity = new MessagesEntity();
        messagesEntity.setMessageContent(encryptedContent);
        messagesEntity.setUser(userEntity);
        messageRepository.save(messagesEntity);
        return true;
    }
    public List<MessageDTO> getMessagesForUser(String username){
        UserEntity userEntity = userRepository.findByUsername(username);
        if(userEntity == null) return null;
        String decryptedKey = keyStoreUtil.decryptUserSecretKey(userEntity.getEncryptedKey());
        return messageRepository.findByUser(userEntity).stream()
                .map(message -> {
                    try {
                        return new MessageDTO(
                                (long) message.getId(),
                                keyStoreUtil.decryptMessage(message.getMessageContent(),decryptedKey)
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
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
