package ru.mwg.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.mgw.model.RabbitQueue;
import ru.mwg.service.UpdateProducer;
import ru.mwg.utils.MessageUtils;

@Component
@Log4j
public class UpdateController {

  private TelegramBot telegramBot;

  private final MessageUtils messageUtils;

  private final UpdateProducer updateProducer;

  public UpdateController(
      MessageUtils messageUtils,
      UpdateProducer updateProducer) {
    this.messageUtils = messageUtils;
    this.updateProducer = updateProducer;
  }

  public void registerBot(TelegramBot telegramBot) {
    this.telegramBot = telegramBot;
  }

  public void processUpdate(Update update) {
    if (update == null) {
      log.error("Received update is null");
      return;
    }

    if (update.getMessage() != null) {
      distributeMessagesByType(update);
    } else {
      log.error("Unsupported message type is received: " + update);
    }
  }

  private void distributeMessagesByType(Update update) {
    var message = update.getMessage();
    if (message.getText() != null) {
      processTextMessage(update);
    } else if (message.getDocument() != null) {
      processDocMessage(update);
    } else if (message.getPhoto() != null) {
      processPhotoMessage(update);
    } else {
      setUnsupportedMessageTypeView(update);
    }
  }

  private void setUnsupportedMessageTypeView(Update update) {
    var sendMessage = messageUtils.generateSendMessageWithText(update,
        "РќРµРїРѕРґРґРµСЂР¶РёРІР°РµРјС‹Р№ С‚РёРї СЃРѕРѕР±С‰РµРЅРёСЏ!");
    setView(sendMessage);
  }

  private void setFileIsReceivedView(Update update) {
    var sendMessage = messageUtils.generateSendMessageWithText(update,
        "Р¤Р°Р№Р» РїРѕР»СѓС‡РµРЅ! РћР±СЂР°Р±Р°С‚С‹РІР°РµС‚СЃСЏ...");
    setView(sendMessage);
  }

  private void setView(SendMessage sendMessage) {
    telegramBot.sendAnswerMessage(sendMessage);
  }

  private void processPhotoMessage(Update update) {
    updateProducer.produce( RabbitQueue.PHOTO_MESSAGE_UPDATE, update);
    setFileIsReceivedView(update);
  }

  private void processDocMessage(Update update) {
    updateProducer.produce(RabbitQueue.DOC_MESSAGE_UPDATE, update);
    setFileIsReceivedView(update);
  }

  private void processTextMessage(Update update) {
    updateProducer.produce(RabbitQueue.TEXT_MESSAGE_UPDATE, update);
  }

}
