package fr.karmaowner.companies.shop;

public class AbstractShop {
  private ItemShop basis;
  
  public AbstractShop(ItemShop item) {
    setItemShop(item);
  }
  
  public ItemShop getItemShop() {
    return this.basis;
  }
  
  public void setItemShop(ItemShop basis) {
    this.basis = basis;
  }
}
