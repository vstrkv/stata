package ru.mwg.node.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mgw.commom.mq.model.RabbitQueue;

@Service
public class ProducerService {
  private final RabbitTemplate rabbitTemplate;

  public ProducerService(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  public void producerAnswer(SendMessage sendMessage) {
    rabbitTemplate.convertAndSend(RabbitQueue.ANSWER_MESSAGE, sendMessage);
  }
}
