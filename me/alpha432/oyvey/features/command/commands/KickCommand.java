package me.alpha432.oyvey.features.command.commands;

import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.util.WorldUtil;

public class KickCommand extends Command {
  public KickCommand() {
    super("kick");
  }
  
  public void execute(String[] commands) {
    WorldUtil.disconnectFromWorld(this);
  }
}
