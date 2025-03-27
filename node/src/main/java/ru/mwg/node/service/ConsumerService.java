package ru.mwg.node.service;

import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.mgw.commom.mq.model.RabbitQueue;

@Service
@Log4j
public class ConsumerService {

  private final DocService docService;

  private final TextService textService;

  public ConsumerService(
      DocService docService,
      TextService textService
  ) {
    this.docService = docService;
    this.textService = textService;
  }

  @RabbitListener(queues = RabbitQueue.DOC_MESSAGE_UPDATE)
  public void consumeDocMessageUpdates(Update update) {
    log.debug("NODE: Doc message is received");
    docService.processDocMessage(update);
  }

  @RabbitListener(queues = RabbitQueue.PHOTO_MESSAGE_UPDATE)
  public void consumePhotoMessageUpdates(Update update) {
    log.debug("NODE: Photo message is received");
  }

  @RabbitListener(queues = RabbitQueue.TEXT_MESSAGE_UPDATE)
  public void consumeTextMessageUpdates(Update update) {
    log.debug("NODE: Text message is received");
    textService.processTextMessage(update);
  }
}
