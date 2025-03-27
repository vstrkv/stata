package ru.mwg.node.service;

import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.mgw.commom.mq.model.RabbitQueue;

@Service
@Log4j
public class ConsumerService {

  private final ProducerService producerService;

  public ConsumerService(ProducerService producerService) {
    this.producerService = producerService;
  }

  @RabbitListener(queues = RabbitQueue.TEXT_MESSAGE_UPDATE)
  public void consumeTextMessageUpdates(Update update) {
    log.debug("NODE: Text message is received");

    var message = update.getMessage();
    var sendMessage = new SendMessage();
    sendMessage.setChatId(message.getChatId().toString());
    sendMessage.setText("Hello from NODE");
    producerService.producerAnswer(sendMessage);
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
