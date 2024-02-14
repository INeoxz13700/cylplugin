package fr.karmaowner.companies.shop;

import fr.karmaowner.utils.ItemUtils;
import org.bukkit.inventory.ItemStack;

public class ItemShop {
  private byte data;
  
  private int id;
  
  private double buyPrice;
  
  private double sellPrice;
  
  private int slot;
  
  private int amount;
  
  public ItemShop(int itemId, int slot) {
    setId(itemId);
    setSlot(slot);
    setAmount(1);
  }
  
  public ItemShop(int itemId, byte data, int slot) {
    this(itemId, slot);
    setData(data);
  }
  
  public ItemShop(int itemId, double buyPrice, double sellPrice, int slot) {
    this(itemId, slot);
    setBuyPrice(buyPrice);
    setSellPrice(sellPrice);
    setSlot(slot);
  }
  
  public ItemShop(int itemId, double buyPrice, double sellPrice, int slot, int amount) {
    this(itemId, slot);
    setBuyPrice(buyPrice);
    setSellPrice(sellPrice);
    setSlot(slot);
    setAmount(amount);
  }
  
  public ItemShop(int itemId, byte data, double buyPrice, double sellPrice, int slot) {
    this(itemId, buyPrice, sellPrice, slot);
    setData(data);
  }
  
  public ItemShop(int itemId, byte data, double buyPrice, double sellPrice, int slot, int amount) {
    this(itemId, buyPrice, sellPrice, slot, amount);
    setData(data);
  }
  
  public boolean isRightSlot(int max) {
    if (this.slot < 0 || this.slot > max)
      return false; 
    return true;
  }
  
  public byte getData() {
    return this.data;
  }
  
  public void setData(byte data) {
    this.data = data;
  }
  
  public int getId() {
    return this.id;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public double getBuyPrice() {
    return this.buyPrice;
  }
  
  public void setBuyPrice(double buyPrice) {
    this.buyPrice = buyPrice;
  }
  
  public double getSellPrice() {
    return this.sellPrice;
  }
  
  public void setSellPrice(double sellPrice) {
    this.sellPrice = sellPrice;
  }
  
  public ItemStack getItem() {
    return ItemUtils.getItem(this.id, this.data, this.amount, null, null);
  }
  
  public int getSlot() {
    return this.slot;
  }
  
  public void setSlot(int slot) {
    this.slot = slot;
  }
  
  public int getAmount() {
    return this.amount;
  }
  
  public void setAmount(int amount) {
    this.amount = amount;
  }
  
  public boolean equals(Object object) {
    if (object instanceof ItemShop) {
      ItemShop item = (ItemShop)object;
      return (item.getId() == getId() && item.getData() == getData());
    } 
    return false;
  }
  
  public ItemShop copy() {
    return new ItemShop(getId(), getData(), getBuyPrice(), getSellPrice(), getSlot(), 1);
  }
}
