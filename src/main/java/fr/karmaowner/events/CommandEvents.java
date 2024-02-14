package fr.karmaowner.events;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Kit;
import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.Commandkit;
import fr.karmaowner.casino.Casino;
import fr.karmaowner.common.Main;
import fr.karmaowner.utils.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class CommandEvents implements Listener {



  @EventHandler
  public void onCommand(PlayerCommandPreprocessEvent event) {
    String command = event.getMessage(); // Obtenez la commande entrée
    Player player = event.getPlayer();

    if(command.toLowerCase().startsWith("/kit"))
    {

      if(!command.contains(" ")) return;

      String[] args = command.split(" ");


      String kitName = args[1].toLowerCase();

      Map<String, Object> kit = Main.essentials.getKits().getKit(kitName);
      if(kit != null)
      {
        Kit kitParse = null;
        User user = Main.essentials.getUser(event.getPlayer());

        try {
          kitParse = new Kit(kitName, Main.essentials);
          if(!player.hasPermission(Permissions.Staff))
          {
            kitParse.checkPerms(user);
            kitParse.checkDelay(user);
          }
        } catch (Exception e) {
          player.sendMessage(e.getMessage());
          return;
        }
        event.setCancelled(true);



          try {
            kitParse.setTime(user);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
          List<String> items =  (List<String>) kit.get("items");

          for(String item : items)
          {
            String[] itemDatas = item.split(" ");
            int id;
            byte subId = 0;
            if(itemDatas[0].contains(":"))
            {
              id = Integer.parseInt(itemDatas[0].split(":")[0]);
              subId = Byte.parseByte(itemDatas[0].split(":")[1]);
            }
            else
            {
              id = Integer.parseInt(itemDatas[0]);
            }

            int quantity = Integer.parseInt(itemDatas[1]);

            ItemStack is = new ItemStack(id, quantity, subId);

            player.getInventory().addItem(is);

          }

      }
    }
  }
}
