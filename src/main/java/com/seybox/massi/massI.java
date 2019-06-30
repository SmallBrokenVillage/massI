package com.seybox.massi;

import com.seybox.massi.mapRenderers.rendererI;
import com.seybox.massi.util.dataStorageHelper;
import com.seybox.massi.util.itemStackHelper;
import com.seybox.massi.util.timeAmountHelper;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class massI extends JavaPlugin implements Listener {
  private dataStorageHelper dataStorageHelper;
  private timeAmountHelper timeAmountHelper;
  private itemStackHelper itemStackHelper;

  private static final String playerTimeAmountDataStorage = "playerTimeAmount.yml";
  private static final String playerTimeListDataStorage = "playerTimeList.yml";
  private static final String playerActualPosDataStorage = "playerActualPosList.yml";

  private Map<String, Boolean> isRec = new HashMap<String, Boolean>();
  private EntityPlayer npc;

  @Override
  public void onDisable() {
    super.onDisable();
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (command.getName().equalsIgnoreCase("listtime")) {
      if (!(sender instanceof Player)) {
        for (Player p : Bukkit.getOnlinePlayers()) {
          Object amountObj =
              this.dataStorageHelper.getDataStorage(playerTimeListDataStorage, p.getName());
          Long amount = 0l;
          if (amountObj != null) {
            amount = Long.valueOf(amountObj.toString());
          }
          Bukkit.getConsoleSender()
              .sendMessage("玩家 " + p.getName() + " 总在线时长 " + amount / 1000 + " 秒");
        }
      } else {
        for (Player p : Bukkit.getOnlinePlayers()) {
          Object amountObj =
              this.dataStorageHelper.getDataStorage(playerTimeListDataStorage, p.getName());
          Long amount = 0l;
          if (amountObj != null) {
            amount = Long.valueOf(amountObj.toString());
          }
          sender.sendMessage(
              ChatColor.YELLOW
                  + "玩家 "
                  + ChatColor.BLUE
                  + p.getName()
                  + ChatColor.YELLOW
                  + " 总在线时长 "
                  + ChatColor.RED
                  + amount / 1000
                  + ChatColor.YELLOW
                  + " 秒");
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public void onEnable() {
    super.onEnable();
    saveDefaultConfig();
    Bukkit.getPluginCommand("listtime").setExecutor(this);

    this.dataStorageHelper = new dataStorageHelper(this);
    this.timeAmountHelper = new timeAmountHelper();
    this.itemStackHelper = new itemStackHelper();

    this.dataStorageHelper.createDataStorage(playerTimeAmountDataStorage);
    this.dataStorageHelper.createDataStorage(playerTimeListDataStorage);
    this.dataStorageHelper.createDataStorage(playerActualPosDataStorage);

    Bukkit.getServer().getPluginManager().registerEvents(this, this);

    runTasks();
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player p = event.getPlayer();

    isRec.put(p.getUniqueId().toString(), false);

    p.playSound(p.getLocation(),Sound.MUSIC_GAME,1,1);

    /** ******************************* */
    /** 此部分实现玩家在线时长统计***** */
    /** ******************************* */
    Object playerAmount =
        this.dataStorageHelper.getDataStorage(
            playerTimeAmountDataStorage, p.getUniqueId().toString() + "Amount");
    Long amountTime;
    if (playerAmount == null) {
      this.dataStorageHelper.setDataStorage(
          playerTimeAmountDataStorage, p.getUniqueId().toString() + "Amount", 0);
      timeAmountHelper.playerJoin(p.getUniqueId(), 0, new Date().getTime());
      amountTime = 0l;
    } else {
      this.timeAmountHelper.playerJoin(
          p.getUniqueId(), Long.valueOf(playerAmount.toString()), new Date().getTime());
      amountTime = Long.valueOf(playerAmount.toString());
    }
    p.sendTitle(
        ChatColor.YELLOW + "Hello " + ChatColor.BLUE + p.getName(),
        ChatColor.RED + "你的累计在线时长为 " + amountTime / 1000 + " 秒",
        10,
        50,
        10);

    /** ******************************* */
    /** 此部分实现玩家回到主城********* */
    /** ******************************* */
    if (!p.getWorld().getName().equalsIgnoreCase("main")) {
      this.dataStorageHelper.setDataStorage(
          playerActualPosDataStorage, p.getUniqueId() + "World", p.getWorld().getName());
      this.dataStorageHelper.setDataStorage(
          playerActualPosDataStorage, p.getUniqueId() + "PosX", p.getLocation().getX());
      this.dataStorageHelper.setDataStorage(
          playerActualPosDataStorage, p.getUniqueId() + "PosY", p.getLocation().getY());
      this.dataStorageHelper.setDataStorage(
          playerActualPosDataStorage, p.getUniqueId() + "PosZ", p.getLocation().getZ());
      Bukkit.getServer()
          .dispatchCommand(Bukkit.getConsoleSender(), "world tp " + p.getName() + " main");
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    Player p = event.getPlayer();

    isRec.remove(p.getUniqueId().toString());

    /** ******************************* */
    /** 此部分实现玩家在线时长统计***** */
    /** ******************************* */
    timeAmountHelper.playerStatus status =
        this.timeAmountHelper.playerQuit(p.getUniqueId(), new Date().getTime());
    this.dataStorageHelper.setDataStorage(
        playerTimeAmountDataStorage, p.getUniqueId() + "Amount", status.getAmount());
    this.dataStorageHelper.setDataStorage(
        playerTimeListDataStorage, p.getName(), status.getAmount());
  }

  /*
  @EventHandler
  public void onPlayerIntoWorld(PlayerChangedWorldEvent event){
    Player p = event.getPlayer();

    p.playSound(p.getLocation(),Sound.MUSIC_GAME,1,1);
  }

   */

  /* @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    Player p = event.getPlayer();
  */
  /** ******************************* */
  /** 此部分实现在主城的玩家传送***** */
  /** ******************************* */
  /*
    if (p.getWorld().getName().equalsIgnoreCase("main")) {
      Inventory inv = Bukkit.createInventory(null, 9, "世界菜单");
      ItemStack book = new ItemStack(Material.BOOK, 1);
      ItemMeta meta = book.getItemMeta();
      meta.setDisplayName("§b回到原来的世界");
      ArrayList lore = new ArrayList();
      lore.add("§b这里是测试主城");
      lore.add("§b点击回到原来的世界");
      meta.setLore(lore);
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      book.setItemMeta(meta);
      inv.setItem(0, book);
      p.openInventory(inv);
    }
  }*/

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    if (isRec.get(event.getPlayer().getUniqueId().toString())) {
      Location loc = event.getPlayer().getLocation();
      loc.add((Math.random() - 0.5) * 2, (Math.random() - 0.5) * 2, (Math.random() - 0.5) * 2);
      event.getPlayer().getWorld().spawnParticle(Particle.HEART, loc, 1);
    }
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    Player p = event.getPlayer();

    /** ******************************* */
    /** 此部分实现在主城的 UI********** */
    /** ******************************* */
    if (p.getWorld().getName().equalsIgnoreCase("main")) {
      event.setCancelled(true);
      Inventory inv = Bukkit.createInventory(null, 9, "世界菜单");
      inv.setItem(0, this.itemStackHelper.getBackToOri());
      inv.setItem(1, this.itemStackHelper.getHaBook());
      p.openInventory(inv);
    }
  }

  @EventHandler
  public void onClickInventory(InventoryClickEvent event) {
    Player p = Bukkit.getPlayer(event.getWhoClicked().getName());
    if (p != null) {
      /** ******************************* */
      /** 此部分实现在主城的玩家传送***** */
      /** ******************************* */
      if (p.getWorld().getName().equalsIgnoreCase("main")
          && event.getInventory().contains(itemStackHelper.getBackToOri())) {
        event.setCancelled(true);
        if (event.getRawSlot() == 0) {
          Object worldObj =
              this.dataStorageHelper.getDataStorage(
                  playerActualPosDataStorage, p.getUniqueId() + "World");
          Object PosXObj =
              this.dataStorageHelper.getDataStorage(
                  playerActualPosDataStorage, p.getUniqueId() + "PosX");
          Object PosYObj =
              this.dataStorageHelper.getDataStorage(
                  playerActualPosDataStorage, p.getUniqueId() + "PosY");
          Object PosZObj =
              this.dataStorageHelper.getDataStorage(
                  playerActualPosDataStorage, p.getUniqueId() + "PosZ");
          if (worldObj != null && PosXObj != null && PosYObj != null && PosZObj != null) {
            String world = worldObj.toString();
            double x = Double.valueOf(PosXObj.toString());
            double y = Double.valueOf(PosYObj.toString());
            double z = Double.valueOf(PosZObj.toString());
            Bukkit.getServer()
                .dispatchCommand(
                    Bukkit.getConsoleSender(), "world tp " + p.getName() + " " + world);
            p.teleport(new Location(Bukkit.getWorld(world), x, y, z));
          } else {
            Bukkit.getServer()
                .dispatchCommand(Bukkit.getConsoleSender(), "world tp " + p.getName() + " world");
          }
        }
        /** ******************************* */
        /** 此部分实现给玩家分发生命之书*** */
        /** ******************************* */
        if (event.getRawSlot() == 1) {
          Inventory inv = p.getInventory();
          int value = 100;
          if (inv.contains(Material.DIAMOND, value)) {
            int firstEmpty = inv.firstEmpty();
            if (firstEmpty == -1) {
              p.sendMessage(ChatColor.RED + "你的背包没有空间！");
            } else {
              int amount = 0;
              while (amount < value) {
                int first = inv.first(Material.DIAMOND);
                ItemStack diamond = inv.getItem(first);
                if (diamond.getAmount() < value - amount) {
                  amount += diamond.getAmount();
                  inv.setItem(first, null);
                } else {
                  inv.setItem(
                      first,
                      new ItemStack(Material.DIAMOND, diamond.getAmount() - (value - amount)));
                  break;
                }
              }
              inv.setItem(firstEmpty, this.itemStackHelper.getHaBook());
              p.sendMessage(ChatColor.RED + "成功换取一本生命之书！");
            }
          } else {
            p.sendMessage(ChatColor.RED + "你没有足够的钻石！");
          }
        }
      }
    }
  }

  @EventHandler
  public void onItemDrop(PlayerDropItemEvent event) {
    Player p = event.getPlayer();

    if (p.getName().equalsIgnoreCase("huang825172")) {
      Spider spider = (Spider) p.getWorld().spawnEntity(p.getLocation(), EntityType.CAVE_SPIDER);
      spider.setCustomNameVisible(true);
      spider.setCustomName(ChatColor.RED + "公告板文字");
      spider.setAI(false);
      spider.setSilent(true);
    }
  }

  @EventHandler
  public void onMapInit(MapInitializeEvent event) {
    MapView map = event.getMap();
    for (MapRenderer renderer : map.getRenderers()) {
      map.removeRenderer(renderer);
    }
    map.addRenderer(new rendererI());
  }

  public void runTasks() {
    new BukkitRunnable() {
      public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
          if (player.getInventory().getItemInMainHand().isSimilar(itemStackHelper.getHaBook())
              || player.getInventory().getItemInOffHand().isSimilar(itemStackHelper.getHaBook())) {
            isRec.put(player.getUniqueId().toString(), true);
            if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() - player.getHealth()
                > 1) {
              player.setHealth(player.getHealth() + 1);
            } else {
              player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            }
          } else {
            isRec.put(player.getUniqueId().toString(), false);
          }
        }
      }
    }.runTaskTimer(this, 10, 10);
  }
}
