package fr.karmaowner.utils;

import fr.karmaowner.common.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MoneyUtils {

  public static int getValueOfMoneyInInventory(Inventory inventory)
  {
      int checkValue = 0, oneDollars = 0, fiveDollars = 0, tenDollars = 0, twentyDollars = 0, fiftyDollars = 0, oneHundredDollars = 0, twoHundredDollars = 0, fiveHundredDollars = 0, twoDollars = 0;
      ItemStack oneDollarsIS = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.1dollars.id")), 1, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.1dollars.byte"));
      ItemStack twoDollarsIS = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.2dollars.id")), 1, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.2dollars.byte"));
      ItemStack fiveDollarsIS = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.5dollars.id")), 1, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.5dollars.byte"));
      ItemStack tenDollarsIS = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.10dollars.id")), 1, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.10dollars.byte"));
      ItemStack twentyDollarsIS = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.20dollars.id")), 1, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.20dollars.byte"));
      ItemStack fiftyDollarsIS = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.50dollars.id")), 1, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.50dollars.byte"));
      ItemStack oneHundredDollarsIS = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.100dollars.id")), 1, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.100dollars.byte"));
      ItemStack twoHundredDollarsIS = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.200dollars.id")), 1, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.200dollars.byte"));
      ItemStack fiveHundredDollarsIS = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.500dollars.id")), 1, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.500dollars.byte"));

      for (ItemStack i : inventory.getContents()) {
        if(i == null) continue;

        if (i.getTypeId() == fiveHundredDollarsIS.getTypeId()) {
          fiveHundredDollars += i.getAmount();
        } else if (i.getTypeId() == twoHundredDollarsIS.getTypeId()) {
          twoHundredDollars += i.getAmount();
        } else if (i.getTypeId() == oneHundredDollarsIS.getTypeId()) {
          oneHundredDollars += i.getAmount();
        } else if (i.getTypeId() == fiftyDollarsIS.getTypeId()) {
          fiftyDollars += i.getAmount();
        } else if (i.getTypeId() == twentyDollarsIS.getTypeId()) {
          twentyDollars += i.getAmount();
        } else if (i.getTypeId() == tenDollarsIS.getTypeId()) {
          tenDollars += i.getAmount();
        } else if (i.getTypeId() == fiveDollarsIS.getTypeId()) {
          fiveDollars += i.getAmount();
        } else if (i.getTypeId() == twoDollarsIS.getTypeId()) {
          twoDollars += i.getAmount();
        } else if (i.getTypeId() == oneDollarsIS.getTypeId()) {
          oneDollars += i.getAmount();
        } else if(i.getTypeId() == 4189) {
          String check = i.getItemMeta().getDisplayName();
          check = check.substring(11);
          check = check.replaceAll(" ","");
          check = check.replaceAll("€", "");
          check = check.replaceAll(",",".");
          checkValue += i.getAmount() * (int)Float.parseFloat(check);
        }
      }
      return checkValue + oneDollars + 2 * twoDollars + 5 * fiveDollars + 10 * tenDollars + 20 * twentyDollars + 50 * fiftyDollars + 100 * oneHundredDollars + 200 * twoHundredDollars + 500 * fiveHundredDollars;
  }

  public static int addMoneyToBank(int necessaryValue, Player p) {
    int oneDollars = 0, fiveDollars = 0, tenDollars = 0, twentyDollars = 0, fiftyDollars = 0, oneHundredDollars = 0, twoHundredDollars = 0, fiveHundredDollars = 0, twoDollars = 0;
    ItemStack oneDollarsIS = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.1dollars.id")), 1, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.1dollars.byte"));
    ItemStack twoDollarsIS = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.2dollars.id")), 1, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.2dollars.byte"));
    ItemStack fiveDollarsIS = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.5dollars.id")), 1, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.5dollars.byte"));
    ItemStack tenDollarsIS = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.10dollars.id")), 1, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.10dollars.byte"));
    ItemStack twentyDollarsIS = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.20dollars.id")), 1, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.20dollars.byte"));
    ItemStack fiftyDollarsIS = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.50dollars.id")), 1, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.50dollars.byte"));
    ItemStack oneHundredDollarsIS = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.100dollars.id")), 1, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.100dollars.byte"));
    ItemStack twoHundredDollarsIS = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.200dollars.id")), 1, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.200dollars.byte"));
    ItemStack fiveHundredDollarsIS = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.500dollars.id")), 1, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.500dollars.byte"));
    int value = necessaryValue;

    for (ItemStack i : p.getInventory().getContents()) {
      if (i != null && i.getTypeId() == fiveHundredDollarsIS.getTypeId()) {
        fiveHundredDollars += i.getAmount();
      } else if (i != null && i.getTypeId() == twoHundredDollarsIS.getTypeId()) {
        twoHundredDollars += i.getAmount();
      } else if (i != null && i.getTypeId() == oneHundredDollarsIS.getTypeId()) {
        oneHundredDollars += i.getAmount();
      } else if (i != null && i.getTypeId() == fiftyDollarsIS.getTypeId()) {
        fiftyDollars += i.getAmount();
      } else if (i != null && i.getTypeId() == twentyDollarsIS.getTypeId()) {
        twentyDollars += i.getAmount();
      } else if (i != null && i.getTypeId() == tenDollarsIS.getTypeId()) {
        tenDollars += i.getAmount();
      } else if (i != null && i.getTypeId() == fiveDollarsIS.getTypeId()) {
        fiveDollars += i.getAmount();
      } else if (i != null && i.getTypeId() == twoDollarsIS.getTypeId()) {
        twoDollars += i.getAmount();
      } else if (i != null && i.getTypeId() == oneDollarsIS.getTypeId()) {
        oneDollars += i.getAmount();
      } 
    } 
    int total = oneDollars + 2 * twoDollars + 5 * fiveDollars + 10 * tenDollars + 20 * twentyDollars + 50 * fiftyDollars + 100 * oneHundredDollars + 200 * twoHundredDollars + 500 * fiveHundredDollars;

    if (total < value)
      return 0;

    PlayerUtils utils = new PlayerUtils();
    utils.setInventory((Inventory)p.getInventory());
    while (value > 0) {
      if (value - 500 >= 0 && fiveHundredDollars > 0) {
        value -= 500;
        utils.removeItems(fiveHundredDollarsIS.getType(), 1);
        fiveHundredDollars--;
        continue;
      } 
      if (value - 200 >= 0 && twoHundredDollars > 0) {
        value -= 200;
        utils.removeItems(twoHundredDollarsIS.getType(), 1);
        twoHundredDollars--;
        continue;
      } 
      if (value - 100 >= 0 && oneHundredDollars > 0) {
        value -= 100;
        utils.removeItems(oneHundredDollarsIS.getType(), 1);
        oneHundredDollars--;
        continue;
      } 
      if (value - 50 >= 0 && fiftyDollars > 0) {
        value -= 50;
        utils.removeItems(fiftyDollarsIS.getType(), 1);
        fiftyDollars--;
        continue;
      } 
      if (value - 20 >= 0 && twentyDollars > 0) {
        value -= 20;
        utils.removeItems(twentyDollarsIS.getType(), 1);
        twentyDollars--;
        continue;
      } 
      if (value - 10 >= 0 && tenDollars > 0) {
        value -= 10;
        utils.removeItems(tenDollarsIS.getType(), 1);
        tenDollars--;
        continue;
      } 
      if (value - 5 >= 0 && fiveDollars > 0) {
        value -= 5;
        utils.removeItems(fiveDollarsIS.getType(), 1);
        fiveDollars--;
        continue;
      } 
      if (value - 2 >= 0 && twoDollars > 0) {
        value -= 2;
        utils.removeItems(twoDollarsIS.getType(), 1);
        twoDollars--;
        continue;
      }
      if (oneDollars > 0) {
        value--;
        utils.removeItems(oneDollarsIS.getType(), 1);
        oneDollars--;
      } else{
        break;
      }
    }
    if (value > 0) {
      p.sendMessage("§c" + value + " §crestant n'ont pas pu être poser car vous n'avez pas les billets nécessaire .");
    }
    return necessaryValue-value;
  }
  
  public static int convertValueToTickets(int value, Player p) {

    int valueToTakeFromAccount = value;

    int oneDollars = 0, fiveDollars = 0, tenDollars = 0, twentyDollars = 0, fiftyDollars = 0, oneHundredDollars = 0, twoHundredDollars = 0, fiveHundredDollars = 0, twoDollars = 0;

    while (value > 0) {
      if (value - 500 >= 0) {
        value -= 500;
        fiveHundredDollars++;
        continue;
      } 
      if (value - 200 >= 0) {
        value -= 200;
        twoHundredDollars++;
        continue;
      } 
      if (value - 100 >= 0) {
        value -= 100;
        oneHundredDollars++;
        continue;
      } 
      if (value - 50 >= 0) {
        value -= 50;
        fiftyDollars++;
        continue;
      } 
      if (value - 20 >= 0) {
        value -= 20;
        twentyDollars++;
        continue;
      } 
      if (value - 10 >= 0) {
        value -= 10;
        tenDollars++;
        continue;
      } 
      if (value - 5 >= 0) {
        value -= 5;
        fiveDollars++;
        continue;
      } 
      if (value - 2 >= 0) {
        value -= 2;
        twoDollars++;
        continue;
      } 
      value--;
      oneDollars++;
    }

    int amount = oneDollars;
    int i;

    for (i = 0; i < Math.ceil((oneDollars / 64.0F)); i++) {
      ItemStack is = null;
      if (amount - 64 >= 0) {
        is = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.1dollars.id")), 64, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.1dollars.byte"));
        amount -= 64;
      } else {
        is = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.1dollars.id")), (int)(oneDollars % 64.0F), (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.1dollars.byte"));
      } 
      ItemUtils.addItemToInventorySafe(is, p);
      valueToTakeFromAccount -= is.getAmount();
    }

    amount = twoDollars;
    for (i = 0; i < Math.ceil((twoDollars / 64.0F)); i++) {
      ItemStack is = null;
      if (amount - 64 >= 0) {
        is = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.2dollars.id")), 64, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.2dollars.byte"));
        amount -= 64;
      } else {
        is = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.2dollars.id")), (int)(twoDollars % 64.0F), (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.2dollars.byte"));
      } 
      ItemUtils.addItemToInventorySafe(is, p);
      valueToTakeFromAccount -= 2 * is.getAmount();
    } 
    amount = fiveDollars;
    for (i = 0; i < Math.ceil((fiveDollars / 64.0F)); i++) {
      ItemStack is = null;
      if (amount - 64 >= 0) {
        is = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.5dollars.id")), 64, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.5dollars.byte"));
        amount -= 64;
      } else {
        is = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.5dollars.id")), (int)(fiveDollars % 64.0F), (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.5dollars.byte"));
      } 
      ItemUtils.addItemToInventorySafe(is, p);
      valueToTakeFromAccount -= 5 * is.getAmount();
    } 
    amount = tenDollars;
    for (i = 0; i < Math.ceil((tenDollars / 64.0F)); i++) {
      ItemStack is = null;
      if (amount - 64 >= 0) {
        is = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.10dollars.id")), 64, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.10dollars.byte"));
        amount -= 64;
      } else {
        is = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.10dollars.id")), (int)(tenDollars % 64.0F), (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.10dollars.byte"));
      } 
      ItemUtils.addItemToInventorySafe(is, p);
      valueToTakeFromAccount -= 10 * is.getAmount();
    } 
    amount = twentyDollars;
    for (i = 0; i < Math.ceil((twentyDollars / 64.0F)); i++) {
      ItemStack is = null;
      if (amount - 64 >= 0) {
        is = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.20dollars.id")), 64, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.20dollars.byte"));
        amount -= 64;
      } else {
        is = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.20dollars.id")), (int)(twentyDollars % 64.0F), (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.20dollars.byte"));
      } 
      ItemUtils.addItemToInventorySafe(is, p);
      valueToTakeFromAccount -= 20 * is.getAmount();
    } 
    amount = fiftyDollars;
    for (i = 0; i < Math.ceil((fiftyDollars / 64.0F)); i++) {
      ItemStack is = null;
      if (amount - 64 >= 0) {
        is = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.50dollars.id")), 64, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.50dollars.byte"));
        amount -= 64;
      } else {
        is = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.50dollars.id")), (int)(fiftyDollars % 64.0F), (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.50dollars.byte"));
      } 
      ItemUtils.addItemToInventorySafe(is, p);
      valueToTakeFromAccount -= 50 * is.getAmount();
    } 
    amount = oneHundredDollars;
    for (i = 0; i < Math.ceil((oneHundredDollars / 64.0F)); i++) {
      ItemStack is = null;
      if (amount - 64 >= 0) {
        is = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.100dollars.id")), 64, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.100dollars.byte"));
        amount -= 64;
      } else {
        is = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.100dollars.id")), (int)(oneHundredDollars % 64.0F), (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.100dollars.byte"));
      } 
      ItemUtils.addItemToInventorySafe(is, p);
      valueToTakeFromAccount -= 100 * is.getAmount();
    } 
    amount = twoHundredDollars;
    for (i = 0; i < Math.ceil((twoHundredDollars / 64.0F)); i++) {
      ItemStack is = null;
      if (amount - 64 >= 0) {
        is = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.200dollars.id")), 64, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.200dollars.byte"));
        amount -= 64;
      } else {
        is = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.200dollars.id")), (int)(twoHundredDollars % 64.0F), (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.200dollars.byte"));
      } 
      ItemUtils.addItemToInventorySafe(is, p);
      valueToTakeFromAccount -= 200 * is.getAmount();
    }

    amount = fiveHundredDollars;
    for (i = 0; i < Math.ceil((fiveHundredDollars / 64.0F)); i++) {
      ItemStack is = null;
      if (amount - 64 >= 0) {
        is = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.500dollars.id")), 64, (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.500dollars.byte"));
        amount -= 64;
      } else {
        is = new ItemStack(Material.getMaterial(Main.INSTANCE.getConfig().getInt("tickets.500dollars.id")), (int)(fiveHundredDollars % 64.0F), (short)(byte)Main.INSTANCE.getConfig().getInt("tickets.500dollars.byte"));
      } 
      ItemUtils.addItemToInventorySafe(is, p);
      valueToTakeFromAccount -= 500 * is.getAmount();
    } 
    return valueToTakeFromAccount;
  }
}
