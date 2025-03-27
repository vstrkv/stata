package ru.mwg.node.service;

import java.util.Optional;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.mgw.commom.mq.model.RabbitQueue;
import ru.mwg.node.entyty.AppUser;
import ru.mwg.node.entyty.RawData;
import ru.mwg.node.entyty.UserState;
import ru.mwg.node.repository.AppUserRepository;
import ru.mwg.node.repository.RawDataRepository;

@Service
@Log4j
public class ConsumerService {

  private final ProducerService producerService;

  private final RawDataRepository rawDataRepository;

  private final AppUserRepository appUserRepository;

  public ConsumerService(
      ProducerService producerService,
      RawDataRepository rawDataRepository,
      AppUserRepository appUserRepository
  ) {
    this.producerService = producerService;
    this.rawDataRepository = rawDataRepository;
    this.appUserRepository = appUserRepository;
  }

  @RabbitListener(queues = RabbitQueue.TEXT_MESSAGE_UPDATE)
  public void consumeTextMessageUpdates(Update update) {
    saveRawData(update);
    log.debug("NODE: Text message is received");
    AppUser appUser = getAppUser(update);
    var message = update.getMessage();
    var sendMessage = new SendMessage();
    sendMessage.setChatId(message.getChatId().toString());
    sendMessage.setText("Hello from NODE");
    producerService.producerAnswer(sendMessage);
  }

  private AppUser getAppUser(Update update) {
    final User telegramUser = update.getMessage().getFrom();
    final Long id = telegramUser.getId();
    Optional<AppUser> appUserOptional =  appUserRepository.findByTelegramUserId(id);
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

  private void saveRawData(Update update) {
    rawDataRepository.save(RawData.builder().event(update).build());
  }

  @RabbitListener(queues = RabbitQueue.DOC_MESSAGE_UPDATE)
  public void consumeDocMessageUpdates(Update update) {
    log.debug("NODE: Doc message is received");
  }

  @RabbitListener(queues = RabbitQueue.PHOTO_MESSAGE_UPDATE)
  public void consumePhotoMessageUpdates(Update update) {
    log.debug("NODE: Photo message is received");
  }
}
