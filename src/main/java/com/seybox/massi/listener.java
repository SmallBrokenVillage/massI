package com.seybox.massi;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class listener implements Listener {

  /*@EventHandler
  public void onDorpCreateVillager(PlayerDropItemEvent event) {
    List<MerchantRecipe> recipeList = new ArrayList<MerchantRecipe>();
    List<ItemStack> ingredients = new ArrayList<ItemStack>();
    ItemStack ingre = new ItemStack(Material.PAPER, 10);
    ingredients.add(ingre);
    MerchantRecipe recipeItem = new MerchantRecipe(new ItemStack(Material.DIAMOND, 1), 10);
    recipeItem.setIngredients(ingredients);
    recipeList.add(recipeItem);
    Player player = event.getPlayer();
    Villager VN =
        (Villager)
            Bukkit.getWorld("world")
                .spawnEntity(player.getLocation().add(0, 2, 0), EntityType.VILLAGER);
    VN.setRecipes(recipeList);
  }*/

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event){
    event.getPlayer().sendTitle("Hello",event.getPlayer().getName(),10,30,10);
    //event.getPlayer().setGameMode(GameMode.SPECTATOR);
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event){
    //event.setCancelled(true);
  }

  @EventHandler
  public void onPlayerLeave(PlayerQuitEvent event){
    Bukkit.getServer().getConsoleSender().sendMessage("Player " + event.getPlayer().getName() + " Quit");
  }

}
