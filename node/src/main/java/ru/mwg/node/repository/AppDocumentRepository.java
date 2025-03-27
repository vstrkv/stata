package ru.mwg.node.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mwg.node.entyty.AppDocument;

@Repository
public interface AppDocumentRepository extends JpaRepository<AppDocument, Long> {

//    Optional<User> findAllByChatId(Long chatId);
}
