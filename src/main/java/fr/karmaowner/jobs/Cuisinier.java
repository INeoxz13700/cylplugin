package fr.karmaowner.jobs;

import fr.karmaowner.common.Main;
import fr.karmaowner.data.Data;
import fr.karmaowner.jobs.interact.Interact;
import fr.karmaowner.jobs.interact.InteractBuilder;
import fr.karmaowner.utils.ItemUtils;
import java.util.HashMap;
import java.util.Map;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Cuisinier extends Jobs implements Data, Legal {
  private static Inventory recettes;
  
  public static final String RECETTESINVENTORYNAME = ChatColor.GOLD + "Recettes";
  
  private static HashMap<ItemStack, HashMap<Integer, HashMap<Integer, ItemStack>>> crafts = new HashMap<>();
  
  private static HashMap<Integer, InventoryType> invtype = new HashMap<>();
  
  public enum CraftItems {
    VANILLA2(297, 0),
    JUICE1(7783, 0),
    JUICE2(7806, 0),
    JUICE3(7825, 0),
    JUICE4(7834, 0),
    JUICE5(7838, 0),
    JUICE6(7839, 0),
    JUICE7(7840, 0),
    JUICE8(7841, 0),
    JUICE9(7845, 0),
    JUICE10(7850, 0),
    JUICE11(7853, 0),
    JUICE12(7855, 0),
    JUICE13(7856, 0),
    JUICE14(7869, 0),
    JUICE15(7874, 0),
    JUICE16(7876, 0),
    JUICE17(7897, 0),
    FOOD1(7782, 0),
    FOOD2(7785, 0),
    FOOD3(7788, 0),
    FOOD4(7789, 0),
    FOOD5(7793, 0),
    FOOD6(7797, 0),
    FOOD7(7798, 0),
    FOOD8(7800, 0),
    FOOD9(7804, 0),
    FOOD10(7805, 0),
    FOOD11(7808, 0),
    FOOD12(7811, 0),
    FOOD13(7818, 0),
    FOOD14(7819, 0),
    FOOD15(7820, 0),
    FOOD16(7822, 0),
    FOOD17(7823, 0),
    FOOD18(7826, 0),
    FOOD19(7829, 0),
    FOOD20(7831, 0),
    FOOD21(7844, 0),
    FOOD22(7846, 0),
    FOOD23(7861, 0),
    FOOD24(7864, 0),
    FOOD25(7865, 0),
    FOOD26(7881, 0),
    FOOD27(7884, 0),
    FOOD28(7890, 0),
    FOOD29(7891, 0),
    FOOD30(7895, 0),
    FOOD31(7898, 0),
    SUGAR1(7775, 0),
    SUGAR2(7786, 0),
    SUGAR3(7791, 0),
    SUGAR4(7792, 0),
    SUGAR5(7795, 0),
    SUGAR6(7796, 0),
    SUGAR7(7799, 0),
    SUGAR8(7801, 0),
    SUGAR9(7809, 0),
    SUGAR10(7810, 0),
    SUGAR11(7812, 0),
    SUGAR12(7814, 0),
    SUGAR13(7815, 0),
    SUGAR14(7816, 0),
    SUGAR15(7824, 0),
    SUGAR16(7827, 0),
    SUGAR17(7836, 0),
    SUGAR18(7837, 0),
    SUGAR19(7842, 0),
    SUGAR20(7847, 0),
    SUGAR21(7848, 0),
    SUGAR22(7849, 0),
    SUGAR23(7854, 0),
    SUGAR24(7860, 0),
    SUGAR25(7863, 0),
    SUGAR26(7866, 0),
    SUGAR27(7867, 0),
    SUGAR28(7868, 0),
    SUGAR29(7871, 0),
    SUGAR30(7872, 0),
    SUGAR31(7873, 0),
    SUGAR32(7875, 0),
    SUGAR33(7877, 0),
    SUGAR34(7879, 0),
    SUGAR38(7886, 0),
    SUGAR39(7887, 0),
    SUGAR40(7900, 0),
    SUGAR41(7901, 0),
    SUGAR42(7903, 0),
    MEAT1(7777, 0),
    MEAT2(7779, 0),
    MEAT3(7780, 0),
    MEAT4(7784, 0),
    MEAT5(7787, 0),
    MEAT6(7790, 0),
    MEAT7(7794, 0),
    MEAT8(7813, 0),
    MEAT9(7821, 0),
    MEAT10(7828, 0),
    MEAT11(7830, 0),
    MEAT12(7833, 0),
    MEAT13(7835, 0),
    MEAT14(7851, 0),
    MEAT15(7857, 0),
    MEAT16(7858, 0),
    MEAT17(7862, 0),
    MEAT18(7870, 0),
    MEAT19(7878, 0),
    MEAT20(7882, 0),
    MEAT21(7883, 0),
    MEAT22(7894, 0),
    MEAT23(7899, 0),
    VANILLA1(400, 0),
    VANILLA3(282, 0),
    VANILLA4(319, 0),
    VANILLA5(320, 0),
    VANILLA6(349, 0),
    VANILLA7(349, 1),
    VANILLA8(349, 2),
    VANILLA9(350, 0),
    VANILLA10(350, 2),
    VANILLA11(354, 0),
    VANILLA12(357, 0),
    VANILLA13(363, 0),
    VANILLA14(364, 0),
    VANILLA15(365, 0),
    VANILLA16(366, 0),
    VANILLA17(393, 0),
    CHOCOLATE2(7896, 0),
    DOG(7807, 0),
    CHOCOLATE(7795, 0);
    
    private int id;
    
    private byte data;
    
    CraftItems(int id, int data) {
      this.id = id;
      this.data = (byte)data;
    }
    
    public int getId() {
      return this.id;
    }
    
    public byte getData() {
      return this.data;
    }
    
    public static boolean isAbleToCraft(ItemStack item) {
      for (CraftItems i : values()) {
        if (i.id == item.getTypeId() && i.data == item.getData().getData())
          return true; 
      } 
      return false;
    }
  }
  
  public enum Action {
    CADAVRE("§cSe promener avec le cadavre", 4281, (byte)0, null, null);
    
    private byte dataItem;
    
    private int idItem;
    
    private Byte data;
    
    private Integer id;
    
    private String displayName;
    
    Action(String displayName, int idItem, byte dataItem, Integer id, Byte data) {
      this.displayName = displayName;
      this.idItem = idItem;
      this.dataItem = dataItem;
      this.id = id;
      this.data = data;
    }
    
    public ItemStack getItem() {
      return ItemUtils.getItem(this.idItem, this.dataItem, 1, this.displayName, null);
    }
    
    public Integer getId() {
      return this.id;
    }
    
    public int getIdItem() {
      return this.idItem;
    }
    
    public Byte getData() {
      return this.data;
    }
    
    public String getDisplayName() {
      return this.displayName;
    }
    
    public Action getAction(String displayName) {
      for (Action a : values()) {
        if (a.getDisplayName().equals(displayName))
          return a; 
      } 
      return null;
    }
  }
  
  public Cuisinier(String player) {
    super(player);
    this.actionJobInventory = Main.INSTANCE.getServer().createInventory(null, 27, NAMEACTIONINVENTORY);
    recettes = Main.INSTANCE.getServer().createInventory(null, 27, RECETTESINVENTORYNAME);
    fillRecettesInventory();
    Interact interaction = InteractBuilder.build().interact(Jobs.TypeInteract.ENTITY).entity("lootableBody").OutService(null).priority(2).item(Action.CADAVRE.getItem()).create();
    setInActionInventory(interaction);
  }
  
  public static void openRecettesInventory(Player p) {
    fillRecettesInventory();
    p.openInventory(recettes);
  }
  
  public static void fillRecettesInventory() {
    recettes.clear();
    for (ItemStack item : crafts.keySet()) {
      recettes.addItem(item);
    } 
  }
  
  public static Inventory getRecettes() {
    return recettes;
  }
  
  public static HashMap<Integer, HashMap<Integer, ItemStack>> getRecipe(ItemStack item) {
    for (Map.Entry<ItemStack, HashMap<Integer, HashMap<Integer, ItemStack>>> recipe : crafts.entrySet()) {
      if (((ItemStack)recipe.getKey()).getTypeId() == item.getTypeId() && ((ItemStack)recipe.getKey()).getData().getData() == item.getData().getData())
        return recipe.getValue(); 
    } 
    return null;
  }
  
  public static boolean isExist(ItemStack recipe) {
    for (ItemStack item : crafts.keySet()) {
      if (item.getTypeId() == recipe.getTypeId() && item.getData().getData() == recipe.getData().getData())
        return true; 
    } 
    return false;
  }
  
  public static void addRecipe(ItemStack item, InventoryType type, Player p) {
    if (!isExist(item)) {
      HashMap<Integer, HashMap<Integer, ItemStack>> step1 = new HashMap<>();
      step1.put(1, new HashMap<>());
      invtype.put(1, type);
      crafts.put(item, step1);
      p.sendMessage(ChatColor.GREEN + "Recette créée avec succés !");
    } else {
      p.sendMessage(ChatColor.RED + "Cette recette existe déjà !");
    } 
  }
  
  public static void addStep(ItemStack recipe, int step, InventoryType type, Player p) {
    if (type == InventoryType.WORKBENCH || type == InventoryType.FURNACE) {
      if (getRecipe(recipe) != null) {
        getRecipe(recipe).put(step, new HashMap<>());
        invtype.put(step, type);
        p.sendMessage(ChatColor.GREEN + "Etape créée avec succès !");
      } else {
        p.sendMessage(ChatColor.RED + "Cette recette n'existe pas !");
      } 
    } else {
      p.sendMessage(ChatColor.RED + "Type d'inventaire incorrect !");
    } 
  }
  
  public static void removeStep(ItemStack recipe, int step, Player p) {
    for (Integer n : getRecipe(recipe).keySet()) {
      if (step == n) {
        getRecipe(recipe).remove(step);
        invtype.remove(step);
        p.sendMessage(ChatColor.GREEN + "Etape n°" + step + " supprimée !");
        return;
      } 
    } 
    p.sendMessage(ChatColor.RED + "Cette étape n'existe pas !");
  }
  
  public static HashMap<Integer, ItemStack> getStep(ItemStack recipe, int step, Player p) {
    if (getRecipe(recipe) != null) {
      for (Map.Entry<Integer, HashMap<Integer, ItemStack>> s : getRecipe(recipe).entrySet()) {
        if ((Integer) s.getKey() == step)
          return s.getValue(); 
      } 
    } else {
      p.sendMessage(ChatColor.RED + "Cette recette n'existe pas !");
    } 
    return null;
  }
  
  public static InventoryType getStepInventoryType(ItemStack recipe, int step, Player p) {
    int i = 1;
    if (getRecipe(recipe) != null)
      for (Map.Entry<Integer, HashMap<Integer, ItemStack>> s : getRecipe(recipe).entrySet()) {
        if ((Integer) s.getKey() == step)
          return invtype.get(step);
      }  
    return null;
  }
  
  public static void fillRecipe(ItemStack recipe, int step, int slot, ItemStack item, Player p) {
    HashMap<Integer, ItemStack> r = getStep(recipe, step, p);
    if (r != null) {
      if (getStepInventoryType(recipe, step, p) == InventoryType.WORKBENCH) {
        if (slot <= 9) {
          r.put(slot, item);
          p.sendMessage(ChatColor.GREEN + " Ingrédient rajouté avec succès !");
        } else {
          p.sendMessage(ChatColor.RED + "L'inventaire est une table de craft est en l'ocurrence le slot ne peut pas être supérieur à 9");
        } 
      } else if (getStepInventoryType(recipe, step, p) == InventoryType.FURNACE) {
        if (slot <= 2) {
          r.put(slot, item);
          p.sendMessage(ChatColor.GREEN + " Ingrédient rajouté avec succès !");
        } else {
          p.sendMessage(ChatColor.RED + "L'inventaire est un four est en l'ocurrence le slot ne peut pas être supérieur à 2");
        } 
      } 
    } else {
      p.sendMessage(ChatColor.RED + "Cette Etape n'existe pas !");
    } 
  }
  
  public static void removeRecipe(ItemStack recipe, Player p) {
    if (crafts.remove(recipe) == null)
      p.sendMessage(ChatColor.DARK_RED + "Recette introuvable:" + ChatColor.RED + " La suppression n'a pas pu être effectué !"); 
  }
  
  public static void loadJobData() {
    FileConfiguration f = Main.INSTANCE.getConfig();
    String section = "Recettes";
    ConfigurationSection s = f.getConfigurationSection(section);
    if (s != null) {
      for (String key : s.getKeys(false)) {
        String name = f.getString(section + "." + key + ".name");
        int id = f.getInt(section + "." + key + ".id");
        byte data = Byte.parseByte(f.getString(section + "." + key + ".data"));
        ItemStack item = new ItemStack(id, 1, (short)0, data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        HashMap<Integer, HashMap<Integer, ItemStack>> steps = new HashMap<>();
        String section1 = "Steps";
        for (String key1 : f.getConfigurationSection(section + "." + key + "." + section1).getKeys(false)) {
          String type = f.getString(section + "." + key + "." + section1 + "." + key1 + ".invtype");
          InventoryType t = null;
          if (type.equals("crafting")) {
            t = InventoryType.WORKBENCH;
          } else if (type.equals("four")) {
            t = InventoryType.FURNACE;
          } 
          invtype.put(Integer.parseInt(key1), t);
          HashMap<Integer, ItemStack> ingredients = new HashMap<>();
          String section2 = "Ingredients";
          for (String key2 : f.getConfigurationSection(section + "." + key + "." + section1 + "." + key1 + "." + section2).getKeys(false)) {
            int idIngredient = f.getInt(section + "." + key + "." + section1 + "." + key1 + "." + section2 + "." + key2 + ".id");
            byte dataIngredient = Byte.parseByte(f.getString(section + "." + key + "." + section1 + "." + key1 + "." + section2 + "." + key2 + ".data"));
            int slot = f.getInt(section + "." + key + "." + section1 + "." + key1 + "." + section2 + "." + key2 + ".slot");
            ingredients.put(slot, new ItemStack(idIngredient, 1, (short)0, dataIngredient));
          } 
          steps.put(Integer.parseInt(key1), ingredients);
        } 
        crafts.put(item, steps);
      } 
    } else {
      f.createSection("Recettes");
    } 
    Jobs.Job.CUISINIER.loadJobClothes();
  }
  
  public static void saveJobData() {
    FileConfiguration f = Main.INSTANCE.getConfig();
    String section = "Recettes";
    int i = 0, j = 0;
    for (Map.Entry<ItemStack, HashMap<Integer, HashMap<Integer, ItemStack>>> recipe : crafts.entrySet()) {
      f.set(section + "." + i + ".name", ((ItemStack)recipe.getKey()).getItemMeta().getDisplayName());
      f.set(section + "." + i + ".id", ((ItemStack) recipe.getKey()).getTypeId());
      f.set(section + "." + i + ".data", ((ItemStack) recipe.getKey()).getData().getData());
      String section1 = "Steps";
      for (Map.Entry<Integer, HashMap<Integer, ItemStack>> steps : (Iterable<Map.Entry<Integer, HashMap<Integer, ItemStack>>>)((HashMap)recipe.getValue()).entrySet()) {
        f.set(section + "." + i + "." + section1 + "." + steps.getKey() + ".invtype", 
            (invtype.get(steps.getKey()) == InventoryType.WORKBENCH) ? "crafting" : "four");
        String section2 = "Ingredients";
        for (Map.Entry<Integer, ItemStack> ingredients : (Iterable<Map.Entry<Integer, ItemStack>>)((HashMap)steps.getValue()).entrySet()) {
          f.set(section + "." + i + "." + section1 + "." + steps.getKey() + "." + section2 + "." + j + ".id", ((ItemStack) ingredients.getValue()).getTypeId());
          f.set(section + "." + i + "." + section1 + "." + steps.getKey() + "." + section2 + "." + j + ".data", ((ItemStack) ingredients.getValue()).getData().getData());
          f.set(section + "." + i + "." + section1 + "." + steps.getKey() + "." + section2 + "." + j + ".slot", ingredients.getKey());
          j++;
        } 
      } 
      i++;
    } 
  }
}
