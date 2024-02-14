package fr.karmaowner.jobs.hacker;

import fr.karmaowner.jobs.Hacker;
import fr.karmaowner.utils.ItemUtils;
import fr.karmaowner.utils.MessageUtils;
import fr.karmaowner.wantedlist.WantedList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class WantedRemoveGame extends WriteLettersGame {
  private static final char[] letters = new char[] { 'b', 'd', 'd' };
  
  public static final String RemoveWantedInventoryName = "§2Supprimer un avis de recherche";
  
  private int currentLetterIndex = 0;
  
  private ItemStack missingStep = ItemUtils.getItem(160, (byte)14, 1, "§4Etape à venir", null);
  
  private ItemStack finishedStep = ItemUtils.getItem(160, (byte)5, 1, "§4Etape terminée", null);
  
  public WantedRemoveGame(Player p, Hacker h) {
    super(letters[0], "§6Piratage de la base de donnée", p, h);
    for (int i = 0; i < 3; i++)
      getInventory().setItem((i + 1) * 9 - 1, this.missingStep); 
  }
  
  public void winGame() {
    if (this.currentLetterIndex == letters.length - 1) {
      end();
      MessageUtils.sendMessage((CommandSender)getPlayer(), "§aPiratage de la base de donnée du commissariat réussi !");
      Inventory wanted = WantedList.getList();
      Inventory inv = Bukkit.createInventory(null, wanted.getSize(), "§2Supprimer un avis de recherche");
      inv.setContents(wanted.getContents());
      if (!WantedList.getPlayers().isEmpty()) {
        MessageUtils.sendMessage((CommandSender)getPlayer(), "§2Choisissez l'individu à supprimer du réseau de la police");
        getPlayer().openInventory(inv);
      } else {
        MessageUtils.sendMessage((CommandSender)getPlayer(), "Aucun individu de recherché");
      } 
      return;
    } 
    this.currentLetterIndex++;
    setLetter(letters[this.currentLetterIndex]);
    fillInventory();
    for (int i = 0; i < 3; i++) {
      if (i < this.currentLetterIndex) {
        getInventory().setItem((i + 1) * 9 - 1, this.finishedStep);
      } else {
        getInventory().setItem((i + 1) * 9 - 1, this.missingStep);
      } 
    } 
  }
  
  public String HackingType() {
    return "Piratage de la base de donnée du commissariat";
  }
}
