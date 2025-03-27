package ru.mwg.node.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mwg.node.entyty.EventPlayer;

public interface EventPlayerRepository  extends JpaRepository<EventPlayer, Long> {

}
