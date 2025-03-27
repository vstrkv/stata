package ru.mwg.node.entyty;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Table(name = "player")
public class Player {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private Integer number;

  private String gameName;

  private String realName;

  private String phone;

  private String dateCreate;

  private String dateFirstPlay;

  private String typeGame;

  private LocalDate dateLastGame;

  private String tgName;

  private String countGame;

  private double spendMoney;

  private Integer countEvent;

  @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
  private List<EventPlayer> eventPlayers;

}
