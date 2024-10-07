package org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.repo;

import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.entitys.MessagesEntity;
import org.it_sakerhet_java23_karl_kowal_rapport_uppgift2.entitys.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessagesEntity,Long> {
    List<MessagesEntity> findByUser (UserEntity user);
    MessagesEntity findByIdAndUser (int id, UserEntity user);
}
