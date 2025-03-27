package ru.mwg.dispatcher.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.mgw.model.RabbitQueue;
import ru.mwg.dispatcher.controller.UpdateController;

@Service
public class AnswerConsumer {
  private final UpdateController updateController;

  public AnswerConsumer(UpdateController updateController) {
    this.updateController = updateController;
  }

  @RabbitListener(queues = RabbitQueue.ANSWER_MESSAGE)
  public void consume(SendMessage sendMessage) {
    updateController.setView(sendMessage);
  }
}
