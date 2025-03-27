package ru.mwg.controller;

import javax.annotation.PostConstruct;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.mwg.BotConfig;

@Component
@Log4j
public class TelegramBot extends TelegramLongPollingBot {

  private final BotConfig botConfig;

  private final UpdateController updateController;

  public TelegramBot(
      BotConfig botConfig,
      UpdateController updateController) {
    this.botConfig = botConfig;
    this.updateController = updateController;
  }

  @PostConstruct
  void init() {
    updateController.registerBot(this);
  }

  @Override
  public String getBotUsername() {
    return botConfig.getBotName();
  }

  @Override
  public String getBotToken() {
    return botConfig.getBotToken();
  }

  @Override
  public void onUpdateReceived(Update update) {
    updateController.processUpdate(update);
  }

  public void sendAnswerMessage(SendMessage message) {
    if (message != null) {
      try {
        execute(message);
      } catch (TelegramApiException e) {
        log.error(e);
      }
    }
  }
}
