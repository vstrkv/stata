package ru.mwg.node.entyty;

public enum ServiceCommand {
  HELP("/help"),
  REGISTRATION("/registration"),
  STATA("/statistic"),
  CANCEL("/cancel"),
  START("/start");
  private final String cmd;

  ServiceCommand(String cmd) {
    this.cmd = cmd;
  }

  @Override
  public String toString() {
    return cmd;
  }

  public boolean equals(String cmd) {
    return this.toString().equals(cmd);
  }
}
