package ru.mwg.node.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mwg.node.entyty.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> getByPhoneIn(List<String> phone);

    Player findByPhone(String phone);

    Player findByTgName(String tgName);


}
