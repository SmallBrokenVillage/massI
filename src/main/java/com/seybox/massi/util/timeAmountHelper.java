package com.seybox.massi.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class timeAmountHelper {
  public class playerStatus {
    private UUID uuid;
    private long amount;
    private long newStart;

    public playerStatus(UUID uuid, long amount, long newStart) {
      this.uuid = uuid;
      this.amount = amount;
      this.newStart = newStart;
    }

    public UUID getUuid() {
      return uuid;
    }

    public long getAmount() {
      return amount;
    }

    public long getNewStart() {
      return newStart;
    }

    public void setAmount(long amount) {
      this.amount = amount;
    }
  }

  private List<playerStatus> playerList;

  public timeAmountHelper() {
    this.playerList = new ArrayList<playerStatus>();
  }

  public void playerJoin(UUID uuid, long amount, long newStart) {
    playerStatus player = new playerStatus(uuid, amount, newStart);
    playerList.add(player);
  }

  public playerStatus playerQuit(UUID uuid, long quitTime) {
    for (playerStatus i : playerList) {
      if (i.getUuid() == uuid) {
        i.setAmount(i.getAmount() + (quitTime - i.getNewStart()));
        playerList.remove(i);
        return i;
      }
    }
    return null;
  }
}
