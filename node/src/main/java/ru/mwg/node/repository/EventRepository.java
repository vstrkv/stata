package ru.mwg.node.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mwg.node.entyty.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

//    Optional<User> findAllByChatId(Long chatId);
}
