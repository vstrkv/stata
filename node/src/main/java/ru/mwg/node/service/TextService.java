package ru.mwg.node.service;


import java.util.Objects;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.mwg.node.entyty.AppUser;
import ru.mwg.node.entyty.Player;
import ru.mwg.node.entyty.RawData;
import ru.mwg.node.entyty.ServiceCommand;
import ru.mwg.node.entyty.UserState;
import ru.mwg.node.repository.EventPlayerRepository;
import ru.mwg.node.repository.PlayerRepository;
import ru.mwg.node.repository.RawDataRepository;

@Service
@Log4j
public class TextService {

  private final AppUserService appUserService;

  private final ProducerService producerService;

  private final RawDataRepository rawDataRepository;

  private final EventPlayerRepository eventPlayerRepository;

  private final PlayerRepository playerRepository;

  public TextService(
      AppUserService appUserService,
      RawDataRepository rawDataRepository,
      EventPlayerRepository eventPlayerRepository,
      PlayerRepository playerRepository,
      ProducerService producerService
  ) {
    this.appUserService = appUserService;
    this.rawDataRepository = rawDataRepository;
    this.eventPlayerRepository = eventPlayerRepository;
    this.playerRepository = playerRepository;
    this.producerService = producerService;
  }


  public void processTextMessage(Update update) {
    saveRawData(update);
    var appUser = appUserService.getAppUser(update);
    var message = update.getMessage();
    var text = message.getText();

    var output = "";
    UserState userState = appUser.getState();
    if (ServiceCommand.CANCEL.equals(text)) {
      output = cancelProcess(appUser);
    } else if (UserState.BASIC_STATE.equals(userState)) {
      output = processServiceCommand(appUser, text);
    } else if (UserState.WAIT_FOR_EMAIL_STATE.equals(userState)) {
      //TODO добавить обработку емейла
    } else {
      log.error("Unknown user state: " + userState);
      output = "Неизвестная ошибка! Введите /cancel и попробуйте снова!";
    }

    var chatId = update.getMessage().getChatId();
    sendAnswer(output, chatId);

  }

  private void saveRawData(Update update) {
    rawDataRepository.save(RawData.builder().event(update).build());
  }


  private String processServiceCommand(AppUser appUser, String cmd) {
    if (ServiceCommand.REGISTRATION.equals(cmd)) {
      //TODO добавить регистрацию
      return "Временно недоступно.";
    } else if (ServiceCommand.HELP.equals(cmd)) {
      return help();
    } else if (ServiceCommand.STATA.equals(cmd)) {
      if ("vstrkv_v".equals(appUser.getUsername())) {
        return getStatistic();
      }
      return "Информация ограничена";
    } else if (ServiceCommand.START.equals(cmd)) {
      return "Приветствую! Чтобы посмотреть список доступных команд введите /help";
    } else {
      return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
    }
  }

  private String getStatistic() {
    Long sumAllEvent =
        playerRepository.findAll().stream()
            .map(Player::getCountEvent)
            .filter(Objects::nonNull)
            .mapToLong(x -> x).sum();
    int sumRegisterEvent = eventPlayerRepository.findAll().size();
    try {
      return "Всего Ивентов: " + sumAllEvent +
          "\nЗарегистрировано Ивентов: " + sumRegisterEvent +
          "\nОтношение: " + (sumRegisterEvent * 100L / sumAllEvent);

    } catch (Exception e) {
      return "Всего Ивентов: " + sumAllEvent + "\nЗарегистрировано Ивентов: " + sumRegisterEvent;
    }

  }


  private String help() {
    return "Список доступных команд:\n"
        + "/cancel - отмена выполнения текущей команды;\n"
        + "/statistic - процент заполнения данных;\n"
        + "/registration - регистрация пользователя.";
  }

  private String cancelProcess(AppUser appUser) {
    appUser.setState(UserState.BASIC_STATE);
    appUserService.save(appUser);
    return "Команда отменена!";
  }

  private void sendAnswer(String output, Long chatId) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(chatId);
    sendMessage.setText(output);
    producerService.producerAnswer(sendMessage);
  }

}
