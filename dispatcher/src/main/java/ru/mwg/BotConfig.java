package ru.mwg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.yml")
public class BotConfig {

  @Value("${config.bot.name}")
  private String botName;

  @Value("${config.bot.token}")
  private String botToken;

  public String getBotName() {
    return botName;
  }

  public String getBotToken() {
    return botToken;
  }
}
