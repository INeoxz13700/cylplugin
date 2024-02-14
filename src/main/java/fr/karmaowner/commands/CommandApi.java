package fr.karmaowner.commands;

import fr.karmaowner.data.PlayerData;
import fr.karmaowner.utils.MessageUtils;
import fr.karmaowner.utils.MoneyUtils;
import fr.karmaowner.utils.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.util.HashMap;

public class CommandApi implements CommandExecutor {

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

    if (cmd.getName().equalsIgnoreCase("apidata")) {

      if(!sender.hasPermission(Permissions.Admin)) return false;

      if(args[0].equalsIgnoreCase("identity"))
      {
        String playerName = args[1];


        PlayerData playerData = PlayerData.getPlayerData(playerName);
        String name = args[2];
        String lastName = args[3];
        playerData.setIdentity(lastName, name);

      }
      else if(args[0].equalsIgnoreCase("password"))
      {
        String username = args[1];

        PlayerData playerData = PlayerData.getPlayerData(username);
        playerData.passwordReceived = true;
      }
      else if(args[0].equalsIgnoreCase("respawn"))
      {
        String username = args[1];
        Player player = Bukkit.getPlayerExact(username);

        if(player == null) return false;


        PlayerData playerData = PlayerData.getPlayerData(username);

        if(!playerData.selectedJob.isOutOfService())
        {
          playerData.selectedJob.equipClothes();
        }
      }
      else if(args[0].equalsIgnoreCase("give"))
      {
        String username = args[1];
        int id = Integer.parseInt(args[2].split(":")[0]);
        byte subId = 0;
        if(args[2].length() == 2) subId = Byte.parseByte(args[2].split(":")[1]);
        int quantity = Integer.parseInt(args[3]);


        Player player = Bukkit.getPlayerExact(username);

        if(player == null) return false;

        ItemStack itemStack = new ItemStack(id,quantity,subId);
        if(args.length == 5)
        {
          String displayName = args[4].replace("_"," ").replace("&","§");

          ItemMeta meta = itemStack.getItemMeta();
          meta.setDisplayName(displayName);
          itemStack.setItemMeta(meta);

        }

        player.getInventory().addItem(itemStack);
      }
      else if(args[0].equalsIgnoreCase("animations"))
      {
        String username = args[1];
        String animationId = args[2];
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "animations unlock " + username + " " + animationId);
      }
      else if(args[0].equalsIgnoreCase("cosmetics"))
      {
        String username = args[1];
        String cosmeticId = args[2];
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cosmetics unlock " + username + " " + cosmeticId);
      }
      else if(args[0].equalsIgnoreCase("garage"))
      {
        String username = args[1];
        String vcoins = args[2];
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "vehicle vcoins add " + username + " " + vcoins);
      }
      else if(args[0].equalsIgnoreCase("trash"))
      {
        String username = args[1];
        Player player = Bukkit.getPlayerExact(username);
        Inventory inventory = Bukkit.createInventory(null, 36, "Poubelle");

        player.openInventory(inventory);
      }

    }
    return false;
  }
}
