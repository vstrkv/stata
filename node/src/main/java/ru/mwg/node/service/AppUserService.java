package ru.mwg.node.service;

import java.util.Optional;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.mwg.node.entyty.AppUser;
import ru.mwg.node.entyty.UserState;
import ru.mwg.node.repository.AppUserRepository;

@Service
@Log4j
public class AppUserService {

  private final AppUserRepository appUserRepository;

  public AppUserService(AppUserRepository appUserRepository) {
    this.appUserRepository = appUserRepository;
  }

  public AppUser getAppUser(Update update) {
    final User telegramUser = update.getMessage().getFrom();
    final Long id = telegramUser.getId();
    Optional<AppUser> appUserOptional = appUserRepository.findByTelegramUserId(id);
    return appUserOptional.orElseGet(() -> appUserRepository.save(
        AppUser.builder()
            .telegramUserId(telegramUser.getId())
            .username(telegramUser.getUserName())
            .firstName(telegramUser.getFirstName())
            .lastName(telegramUser.getLastName())
            //TODO изменить значение по умолчанию после добавления регистрации
            .isActive(true)
            .state(UserState.BASIC_STATE)
            .build()));
  }

  public AppUser save(AppUser appUser) {
    return appUserRepository.save(appUser);
  }
}
