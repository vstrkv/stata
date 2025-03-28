package ru.mwg.node.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import ru.mwg.node.entyty.Event;
import ru.mwg.node.entyty.EventPlayer;
import ru.mwg.node.entyty.Player;
import ru.mwg.node.repository.EventPlayerRepository;
import ru.mwg.node.repository.EventRepository;
import ru.mwg.node.repository.PlayerRepository;

@Service
public class DownloadPlayersService {

  private final PlayerRepository playerRepository;

  private final EventRepository eventRepository;

  private final EventPlayerRepository eventPlayerRepository;

  public DownloadPlayersService(
      PlayerRepository playerRepository,
      EventRepository eventRepository,
      EventPlayerRepository eventPlayerRepository
  ) {
    this.playerRepository = playerRepository;
    this.eventRepository = eventRepository;
    this.eventPlayerRepository = eventPlayerRepository;
  }

  public void exec(List<Player> newPlayers) {
//    проверка наличия участника в евентах
    LocalDate lastDate = newPlayers.stream()
        .map(Player::getDateLastGame)
        .filter(Objects::nonNull)
        .max(LocalDate::compareTo).orElseThrow();

    List<Event> eventList = eventRepository.findAll();
    List<LocalDate> datesEvent = eventList.stream()
        .map(Event::getDate).collect(Collectors.toList());
    LocalDate oldDateInDateBase = datesEvent.stream()
        .max(LocalDate::compareTo).orElse(LocalDate.of(2000, 1, 1));
    if (lastDate.compareTo(oldDateInDateBase) == 0) {
      return;
    }

    List<Player> oldPlayers = playerRepository.findAll();
    Map<LocalDate, List<Player>> mapNewPlayers = getMap(newPlayers);
    Map<LocalDate, List<Player>> mapOldPlayers = getMap(oldPlayers);

    List<Player> playersToAddFirstEvent = new ArrayList<>();
    List<EventPlayer> eventPlayerToAddFirstEvent = new ArrayList<>();

    for (Map.Entry<LocalDate, List<Player>> newEntry : mapNewPlayers.entrySet()) {
      if (datesEvent.contains(newEntry.getKey())) {
        // в старых данных уже есть такой ивент, нужду из новых данных добавить не достающие
        Event event = eventList.stream()
            .filter(x -> x.getDate().compareTo(newEntry.getKey()) == 0)
            .findFirst().orElseThrow();
        for (Player newPlayer : newEntry.getValue()) {
          Player oldPlayerInfo = getPlayer(newPlayer, mapOldPlayers.get(newEntry.getKey()));
          boolean isContains = Objects.nonNull(oldPlayerInfo);

          if (isContains) {
            checkToUpdatePlayer(oldPlayerInfo, newPlayer);
          } else {
            Player inDatabase = playerRepository.save(newPlayer);
            eventPlayerToAddFirstEvent.add(new EventPlayer(null, inDatabase, event));
          }

        }
      } else {
        // todo можно добавить не только в последний ивет, но и впервый
        if (newEntry.getKey() == null) {
          continue;
        }
        List<Player> byDate = mapNewPlayers.get(newEntry.getKey());
        Event event = eventRepository.save(
            Event.builder()
                .date(newEntry.getKey())
                .build()
        );
        for (Player p : byDate) {
          Player inDatabase = getPlayer(p, oldPlayers);
          if (Objects.isNull(inDatabase)) {
            inDatabase = playerRepository.save(p);
            if (newEntry.getKey().compareTo(inDatabase.getDateFirstPlay()) != 0) {
              playersToAddFirstEvent.add(inDatabase);
            }
          } else {
            inDatabase.setCountGame(p.getCountGame());
            inDatabase.setDateLastGame(p.getDateLastGame());
            inDatabase.setSpendMoney(p.getSpendMoney());
            inDatabase = playerRepository.save(inDatabase);
          }
          eventPlayerToAddFirstEvent.add(new EventPlayer(null, inDatabase, event));
        }
        datesEvent.add(newEntry.getKey());
        eventList.add(event);
      }
    }

    for (Player p : playersToAddFirstEvent) {
      Event firstEvent = eventList.stream()
          .filter(x -> x.getDate().compareTo(p.getDateFirstPlay()) == 0)
          .findFirst()
          .orElse(
              eventRepository.save(Event.builder()
                  .date(p.getDateFirstPlay())
                  .build())
          );
      eventPlayerToAddFirstEvent.add(new EventPlayer(null, p, firstEvent));
    }
    eventPlayerRepository.saveAll(eventPlayerToAddFirstEvent);

  }

  private void checkToUpdatePlayer(
      Player oldPlayerInfo,
      Player newPlayer
  ) {
    boolean flag = false;
    if (Objects.nonNull(oldPlayerInfo.getCountGame())
        && !oldPlayerInfo.getCountGame().isEmpty()
        && Objects.nonNull(newPlayer.getCountGame())
        && !newPlayer.getCountGame().isEmpty()) {
      if (Double.parseDouble(oldPlayerInfo.getCountGame()) >
          Double.parseDouble(newPlayer.getCountGame())) {
        oldPlayerInfo.setCountGame(newPlayer.getCountGame());
        flag = true;
      }
    }
    if (oldPlayerInfo.getDateLastGame().isAfter(newPlayer.getDateLastGame())) {
      oldPlayerInfo.setDateLastGame(newPlayer.getDateLastGame());
      flag = true;
    }
    if (oldPlayerInfo.getSpendMoney() > newPlayer.getSpendMoney()) {
      oldPlayerInfo.setSpendMoney(newPlayer.getSpendMoney());
      flag = true;
    }
    if (flag) {
      playerRepository.save(oldPlayerInfo);
    }
  }


  //todo optimise
  private Player getPlayer(Player player, List<Player> oldPlayers) {
    if (oldPlayers == null) {
      return null;
    }
    for (Player old : oldPlayers) {
      if (Objects.equals(player.getPhone(), old.getPhone())) {
        return old;
      }
      if (Objects.equals(player.getTgName(), old.getTgName())) {
        return old;
      }
    }

    for (Player old : oldPlayers) {
      if (Objects.equals(player.getDateCreate(), old.getDateCreate())
          && Objects.equals(player.getRealName(), old.getRealName())
          && Objects.equals(player.getCountGame(), old.getCountGame())
      ) {
        return old;
      }
    }
    return null;
  }

  private Map<LocalDate, List<Player>> getMap(List<Player> newPlayers) {
    Map<LocalDate, List<Player>> map = new HashMap<>();
    for (Player newPlayer : newPlayers) {
      final LocalDate dateLastGame = newPlayer.getDateLastGame();
      List<Player> playersByDate = map.get(dateLastGame);
      if (Objects.isNull(playersByDate)) {
        playersByDate = new ArrayList<>();
        playersByDate.add(newPlayer);
        map.put(dateLastGame, playersByDate);
      } else {
        playersByDate.add(newPlayer);
      }
    }
    return map;
  }
}
