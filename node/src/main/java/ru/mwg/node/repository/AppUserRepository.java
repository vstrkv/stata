package ru.mwg.node.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mwg.node.entyty.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

  Optional<AppUser> findByTelegramUserId(Long id);

  Optional<AppUser> findByEmail(String email);
}
