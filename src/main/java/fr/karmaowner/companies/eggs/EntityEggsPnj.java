package fr.karmaowner.companies.eggs;

import fr.karmaowner.common.Main;
import fr.karmaowner.data.PnjsEggsData;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class EntityEggsPnj {
  private NPC npc;
  
  public static final String PNJNAME = ChatColor.GOLD + "Voir mes oeufs";
  
  public EntityEggsPnj(Player p) {
    this.npc = Main.npclib.getNPCRegistry().createNPC(EntityType.VILLAGER, PNJNAME);
    this.npc.spawn(p.getLocation());
    PnjsEggsData.pnjs.add(this.npc);
    p.sendMessage(ChatColor.GREEN + "EggsPnj crée avec succès !");
  }
}
