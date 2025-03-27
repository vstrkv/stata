package ru.mwg.dispatcher.service;

import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Log4j
public class UpdateProducer {

  private final RabbitTemplate rabbitTemplate;

  public UpdateProducer(
      RabbitTemplate rabbitTemplate
  ) {
    this.rabbitTemplate = rabbitTemplate;
  }


  public void produce(String rabbitQueue, Update update) {
    log.debug(update.getMessage().getText());
    rabbitTemplate.convertAndSend(rabbitQueue, update);
  }
}

