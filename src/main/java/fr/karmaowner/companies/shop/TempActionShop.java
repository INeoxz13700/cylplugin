package fr.karmaowner.companies.shop;

import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.PlayerData;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TempActionShop {
  private ItemShop item;
  
  private Shop shop;
  
  private ActionShopType type;
  
  public static HashMap<Player, TempActionShop> playersAction = new HashMap<>();
  
  public TempActionShop(Player p, ItemShop item2, Shop shop, ActionShopType type) {
    this.item = item2;
    this.shop = shop;
    this.type = type;
    playersAction.put(p, this);
  }
  
  public static void addAction(Player p, ItemShop item2, Shop shop, ActionShopType type) {
    TempActionShop t = new TempActionShop(p, item2, shop, type);
    if (type == ActionShopType.SELL) {
      if (p.getInventory().containsAtLeast(item2.getItem(), 1)) {
        p.closeInventory();
        p.sendMessage(ChatColor.RED + "Executez la commande /entreprise shop sell item <quantité>");
      } else {
        p.sendMessage(ChatColor.DARK_RED + "Vous n'avez pas d'objet de ce type dans votre inventaire !");
      } 
    } else {
      t.buy(p);
    } 
  }
  
  public Shop getShop() {
    return this.shop;
  }
  
  private void buy(Player p) {
    int quantity = 1;
    if (this.type == ActionShopType.LOTBUY)
      quantity = 64; 
    PlayerData data = PlayerData.getPlayerData(p.getName());
    BigDecimal decimal = BigDecimal.valueOf(this.item.getBuyPrice() * quantity);
    if (data != null && data.getMoney().compareTo(decimal) == 1) {
      data.setMoney(data.getMoney().subtract(decimal));
      AbstractShop a = this.shop.getAbstractShop(this.item);
      ItemStack it = this.item.getItem().clone();
      it.setAmount(quantity);
      p.getInventory().addItem(it);
      playersAction.remove(p);
      p.sendMessage(ChatColor.GOLD + "Vous venez d'acheter " + ChatColor.RED + quantity + "x " + this.item.getItem().getType().toString());
      this.shop.fillAbInventory(a);
    } else {
      p.sendMessage(ChatColor.RED + "Vous n'avez pas assez d'argent pour effectuer cette action.");
    } 
  }
  
  public static TempActionShop getAction(Player p) {
    TempActionShop actual = playersAction.get(p);
    if (actual != null)
      return actual; 
    return null;
  }
  
  public static void runAction(Player p, int amount) {
    TempActionShop action = getAction(p);
    if (action != null) {
      if (action.type == ActionShopType.SELL) {
        if (amount > 0) {
          if (p.getInventory().containsAtLeast(action.item.getItem(), amount)) {
            if (action.getShop().getNpc().getType().equalsIgnoreCase("job")) {
              PlayerData data = PlayerData.getPlayerData(p.getName());
              if (data != null) {
                if (amount <= 16) {
                  ItemShop item = action.item.copy();
                  item.setAmount(amount);
                  AbstractShop ab = action.getShop().getAbstractShop(action.item);
                  ItemStack it = item.getItem();
                  it.setAmount(item.getAmount());
                  p.getInventory().removeItem(it);
                  playersAction.remove(p);
                  data.setMoney(data.getMoney().add(BigDecimal.valueOf(ab.getItemShop().getSellPrice() * amount)));
                  p.sendMessage(ChatColor.GRAY + "Vente effectuée: " + ChatColor.BOLD + amount + "x " + item.getItem().getType().toString());
                  return;
                } 
                p.sendMessage(ChatColor.DARK_RED + "La quantité que vous avez indiqué est > 16 !");
                return;
              }
            } else if (action.getShop().getNpc().getType().equalsIgnoreCase("company")) {
              PlayerData pData = PlayerData.getPlayerData(p.getName());
              if (pData != null && pData.companyName != null) {
                CompanyData data = (CompanyData)CompanyData.Companies.get(pData.companyName);
                if (amount <= 16) {
                  Timestamp now = new Timestamp(System.currentTimeMillis());
                  if (data.elapsedTimeQuota.getDay() != now.getDay()) {
                    data.setWonMoneyPerDay(0.0D);
                    data.elapsedTimeQuota = new Timestamp(System.currentTimeMillis());
                  } 
                  if (data.getWonMoneyPerDay() < 100000.0D) {
                    ItemShop item = action.item.copy();
                    item.setAmount(amount);
                    AbstractShop ab = action.getShop().getAbstractShop(action.item);
                    ItemStack it = item.getItem();
                    it.setAmount(item.getAmount());
                    p.getInventory().removeItem(it);
                    playersAction.remove(p);
                    data.setRevenues(data.getRevenues() + ab.getItemShop().getSellPrice() * amount);
                    data.addWonMoneyPerDay(ab.getItemShop().getSellPrice() * amount);
                    data.broadcastCompany(ChatColor.GRAY + "Vente effectuée: " + ChatColor.BOLD + amount + "x " + item.getItem().getType().toString());
                    return;
                  } 
                  p.sendMessage(ChatColor.DARK_RED + "Vous avez atteint le quota de vente quotidien de " + 100000.0D + "€");
                  return;
                } 
                p.sendMessage(ChatColor.DARK_RED + "La quantité que vous avez indiqué est > 16 !");
                return;
              } 
            } 
          } else {
            p.sendMessage(ChatColor.DARK_RED + "Vous n'avez pas une quantité suffisante de cet objet dans votre inventaire");
            return;
          } 
        } else {
          p.sendMessage(ChatColor.DARK_RED + "Le montant doit-être supérieur ou égal à " + action.item.getSellPrice() + "$ et inférieur ou égal à " + action.item.getBuyPrice() + " et la quantité supérieur à 0");
          return;
        } 
      } else {
        return;
      } 
    } else {
      p.sendMessage(ChatColor.DARK_RED + "Aucune action en cours !");
      return;
    } 
  }
}
