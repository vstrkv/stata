package ru.mwg.node.service;

import java.util.List;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.mwg.node.entyty.AppDocument;
import ru.mwg.node.entyty.AppUser;
import ru.mwg.node.entyty.RawData;
import ru.mwg.node.entyty.UserState;
import ru.mwg.node.repository.RawDataRepository;

@Service
@Log4j
public class DocService {

  private final ProducerService producerService;

  private final FileService fileService;

  private final RawDataRepository rawDataRepository;

  private final AppUserService appUserService;

  public DocService(
      AppUserService appUserService,
      FileService fileService,
      RawDataRepository rawDataRepository,
      ProducerService producerService
  ) {
    this.producerService = producerService;
    this.fileService = fileService;
    this.rawDataRepository = rawDataRepository;
    this.appUserService = appUserService;
  }

  public void processDocMessage(Update update) {
    saveRawData(update);
    var appUser = appUserService.getAppUser(update);
    var chatId = update.getMessage().getChatId();
    if (isNotAllowToSendContent(chatId, appUser)) {
      return;
    }
    AppDocument appDocument = fileService.processDoc(update.getMessage());

    sendAnswer("Данные успешно сохранены!", chatId);
  }



  private void saveRawData(Update update) {
    rawDataRepository.save(RawData.builder().event(update).build());
  }


  private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
    var userState = appUser.getState();
    if (!appUser.getIsActive()) {
      var error = "Зарегистрируйтесь или активируйте свою учетную запись для загрузки контента.";
      sendAnswer(error, chatId);
      return true;
    } else if (!UserState.BASIC_STATE.equals(userState)) {
      var error = "Отмените текущую команду с помощью /cancel для отправки файлов.";
      sendAnswer(error, chatId);
      return true;
    }
    return false;
  }

  private void sendAnswer(String output, Long chatId) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(chatId);
    sendMessage.setText(output);
    producerService.producerAnswer(sendMessage);
  }
}
