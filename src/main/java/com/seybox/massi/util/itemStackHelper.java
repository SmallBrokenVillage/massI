package com.seybox.massi.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class itemStackHelper {
  public ItemStack getBackToOri() {
    ItemStack item = new ItemStack(Material.BOOK, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(ChatColor.RED + "回到原来的世界");
    ArrayList lore = new ArrayList();
    lore.add("§b这里是测试主城");
    lore.add("§b点击回到原来的世界");
    meta.setLore(lore);
    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    item.setItemMeta(meta);
    return item;
  }

  public ItemStack getHaBook() {
    ItemStack item = new ItemStack(Material.BOOK, 1);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(ChatColor.RED + "生命之书");
    ArrayList lore = new ArrayList();
    lore.add("§b点击换取生命之书");
    lore.add("§b将扣取 100 个钻石");
    lore.add("§b手持将持续 +1s");
    meta.setLore(lore);
    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    item.setItemMeta(meta);
    return item;
  }
}
