package fr.karmaowner.common;

import fr.karmaowner.companies.CompanyBucheron;
import fr.karmaowner.companies.CompanyForgeron;
import fr.karmaowner.companies.CompanyMenuiserie;
import fr.karmaowner.companies.CompanyMinage;
import fr.karmaowner.companies.CompanyPeche;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.DataAchievements;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

public enum Achievements {
  WOOD10(ChatColor.RED + "10 bois coupés", CompanyBucheron.class, "Company", null, (byte)0),
  WOOD100(ChatColor.RED + "100 bois coupés", CompanyBucheron.class, "Company", null, (byte)0),
  FABRICATION10(ChatColor.RED + "10 objets fabriqués", CompanyMenuiserie.class, "Company", null, (byte)0),
  FABRICATION100(ChatColor.RED + "100 objets fabriqués", CompanyMenuiserie.class, "Company", null, (byte)0),
  MINAGE10(ChatColor.RED + "10 minerais collectés", CompanyMinage.class, "Company", null, (byte)0),
  MINAGE100(ChatColor.RED + "100 minerais collectés", CompanyMinage.class, "Company", null, (byte)0),
  PECHE10(ChatColor.RED + "10 poissons péchés", CompanyPeche.class, "Company", null, (byte)0),
  PECHE100(ChatColor.RED + "100 poissons péchés", CompanyPeche.class, "Company", null, (byte)0),
  PLANTATION10(ChatColor.RED + "10 graines plantées", CompanyPeche.class, "Company", null, (byte)0),
  PLANTATION100(ChatColor.RED + "100 graines plantées", CompanyPeche.class, "Company", null, (byte)0),
  TRANSFORMATION10(ChatColor.RED + "10 minerais transformés", CompanyForgeron.class, "Company", null, (byte)0),
  TRANSFORMATION100(ChatColor.RED + "100 minerais transformés", CompanyForgeron.class, "Company", null, (byte)0),
  STONEAXE(ChatColor.GRAY + "Hache en pierre débloqué", CompanyBucheron.class, "Company", Material.STONE_AXE, (byte)0),
  IRONAXE(ChatColor.WHITE + "Hache en fer débloqué", CompanyBucheron.class, "Company", Material.IRON_AXE, (byte)0),

  SPRUCE(ChatColor.DARK_AQUA + "Bois de sapin débloqué", CompanyBucheron.class, "Company", Material.LOG, (byte)1),
  BIRCH(ChatColor.DARK_AQUA + "Bois de bouleau débloqué", CompanyBucheron.class, "Company", Material.LOG, (byte)2),
  JUNGLE(ChatColor.DARK_AQUA + "Bois d'acajou débloqué", CompanyBucheron.class, "Company", Material.LOG, (byte)3),
  ACACIA(ChatColor.DARK_AQUA + "Bois d'acacia débloqué", CompanyBucheron.class, "Company", Material.LOG_2, (byte)0),
  DARK_OAK(ChatColor.DARK_AQUA + "Bois de chêne noir débloqué", CompanyBucheron.class, "Company", Material.LOG_2, (byte)1),
  TABLE(ChatColor.BLUE + "Tables débloqués", CompanyMenuiserie.class, "Company", 1426, (byte)0);
  
  public static final String MESSAGE;
  
  private String name;
  
  private String whichFor;
  
  private Class<?> classe;
  
  private Material m;
  
  private byte data;
  
  private int id;
  
  static {
    MESSAGE = ChatColor.DARK_BLUE + "Succès débloqué: ";
  }
  
  Achievements(String name, Class<?> classe, String whichFor, Material m, byte data) {
    this.name = name;
    this.whichFor = whichFor;
    this.m = m;
    this.data = data;
    this.classe = classe;
  }
  
  Achievements(String name, Class<?> classe, String whichFor, int id, byte data) {
    this.name = name;
    this.whichFor = whichFor;
    this.id = id;
    this.data = data;
    this.classe = classe;
  }
  
  public int getId() {
    return this.id;
  }
  
  public String getNameAchievements() {
    return this.name;
  }
  
  public Class<?> getClasse() {
    return this.classe;
  }
  
  public static List<Achievements> parseToAchievements(List<String> s) {
    List<Achievements> a = new ArrayList<>();
    for (Achievements a1 : values()) {
      if (s.contains(a1.toString()))
        a.add(a1); 
    } 
    return a;
  }
  
  public Material getType() {
    return this.m;
  }
  
  public Byte getData() {
    return this.data;
  }
  
  public static void setAchievement(Achievements a, DataAchievements data) {
    if (a != null) {
      data.setAchievements(a);
      if (a.whichFor.equals("Company")) {
        CompanyData cd = (CompanyData)data;
        cd.broadcastCompany(MESSAGE + a.name);
      } 
    } 
  }
  
  public static ArrayList<Achievements> valuesBySameClass(Class<?> c) {
    ArrayList<Achievements> list = new ArrayList<>();
    for (Achievements a : values()) {
      if (sameClass(a, c))
        list.add(a); 
    } 
    return list;
  }
  
  public static boolean sameClass(Achievements a1, Achievements a2) {
    return (a1.getClasse() == a2.getClasse());
  }
  
  public static boolean sameClass(Achievements a1, Class<?> c) {
    return (a1.getClasse() == c);
  }
  
  public static boolean hasAchievement(Achievements a1, DataAchievements data) {
    Iterator<Achievements> it = data.getAchievements().iterator();
    while (it.hasNext()) {
      Achievements a2 = it.next();
      if (a1.equals(a2))
        return true; 
    } 
    return false;
  }
}
