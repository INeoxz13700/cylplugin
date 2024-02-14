package fr.karmaowner.companies;

import fr.karmaowner.common.Main;
import fr.karmaowner.companies.pluginevent.CompanyGainXpEvent;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.SqlCollection;
import fr.karmaowner.utils.CustomEntry;
import fr.karmaowner.utils.RecordBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class Company implements Listener {
  public enum TYPE {
    BUCHERON, MENUISERIE, MINAGE, FORGERON, PECHE, METALLURGIE, AGRICULTURE, ELEVAGE, CHASSE, ARMURERIE;
    
    public Company getCompany(CompanyData data) {
      switch (this) {
        case BUCHERON:
          return new CompanyBucheron(data);
        case MENUISERIE:
          return new CompanyMenuiserie(data);
        case MINAGE:
          return new CompanyMinage(data);
        case FORGERON:
          return new CompanyForgeron(data);
        case PECHE:
          return new CompanyPeche(data);
        case METALLURGIE:
          return new CompanyMetallurgie(data);
        case AGRICULTURE:
          return new CompanyAgriculture(data);
        case ELEVAGE:
          return new CompanyElevage(data);
        case CHASSE:
          return new CompanyChasse(data);
        case ARMURERIE:
          return new CompanyArmurerie(data);
      } 
      return null;
    }
    
    public Class<?> getClasse() {
      switch (this) {
        case BUCHERON:
          return CompanyBucheron.class;
        case MENUISERIE:
          return CompanyMenuiserie.class;
        case MINAGE:
          return CompanyMinage.class;
        case FORGERON:
          return CompanyForgeron.class;
        case PECHE:
          return CompanyPeche.class;
        case METALLURGIE:
          return CompanyMetallurgie.class;
        case AGRICULTURE:
          return CompanyAgriculture.class;
        case ELEVAGE:
          return CompanyElevage.class;
        case CHASSE:
          return CompanyChasse.class;
        case ARMURERIE:
          return CompanyArmurerie.class;
      } 
      return null;
    }
  }
  
  public static interface XP {
    double getXp();
    
    int getLevelToUnlock();
    
    String getName();
  }
  
  public CompanyData data = null;
  
  private String CompteurName;
  
  protected int compteur;
  
  public boolean isThatRegion;
  
  public Company(CompanyData data, String CompteurName) {
    this.data = data;
    this.CompteurName = CompteurName;
    this.isThatRegion = false;
    loadData();
  }
  
  public static Company getInstanceByCategorie(String categorie, CompanyData data) {
    for (TYPE t : TYPE.values()) {
      if (categorie.equalsIgnoreCase(t.toString()))
        return t.getCompany(data); 
    } 
    return null;
  }
  
  public static Class<?> getClass(String categorie) {
    for (TYPE t : TYPE.values()) {
      if (categorie.equalsIgnoreCase(t.toString()))
        return t.getClasse(); 
    } 
    return null;
  }
  
  public boolean isItemUnlocked(XP item) {
    if (item != null) {
      return (this.data.getLevelReached() >= item.getLevelToUnlock());
    }
    else {
      return false;
    }
  }
  
  public int getCompteur() {
    return this.compteur;
  }
  
  public void setCompteur(int compteur) {
    this.compteur = compteur;
  }
  
  public String getCompteurName() {
    return this.CompteurName;
  }
  
  public void addXp(Player p, XP item) {
    CompanyGainXpEvent xpEvent = new CompanyGainXpEvent(p,data, item.getXp());
    Bukkit.getServer().getPluginManager().callEvent(xpEvent);
    if(xpEvent.isCancelled())
    {
      return;
    }

    this.data.addXp(xpEvent.xpGain);
    p.sendMessage("§cVous venez de remporter +§4" + xpEvent.xpGain + " §cxp");
    this.data.getCompany().setCompteur(this.data.getCompany().getCompteur() + 1);
  }

  public void addXp(Player p, double xp) {
    CompanyGainXpEvent xpEvent = new CompanyGainXpEvent(p,data, xp);
    Bukkit.getServer().getPluginManager().callEvent(xpEvent);
    if(xpEvent.isCancelled())
    {
      return;
    }

    this.data.addXp(xpEvent.xpGain);
    p.sendMessage("§cVous venez de remporter +§4" + xpEvent.xpGain + " §cxp");
    this.data.getCompany().setCompteur(this.data.getCompany().getCompteur() + 1);
  }
  
  public void locked_Message(Player p, XP item) {
    if(item != null)
    {
      p.sendMessage(ChatColor.DARK_RED + item.getName() + " non débloqué: disponible au niveau " + ChatColor.RED + "" + item.getLevelToUnlock());
    }
  }
  
  public void loadData() {
    try {
      SqlCollection results = Main.Database.select(RecordBuilder.build().selectAll("company_" + this.data
            .getCategoryName().toLowerCase(), new CustomEntry("CompanyName", this.data
              .getCompanyName())).toString());
      if (results.count() == 1 && !this.data.isCreation) {
        ResultSet rs = results.getActualResult();
        this.compteur = rs.getInt(this.CompteurName);
      } 
    } catch (SQLException e) {
      e.printStackTrace();
    } 
  }
  
  public void saveData() {
    if (this.data.isCreation) {
      HashMap<String, Object> fields = new HashMap<>();
      fields.put("CompanyName", this.data.getCompanyName());
      fields.put(this.CompteurName, this.compteur);
      try {
        Main.Database.update(RecordBuilder.build().insert(fields, "company_" + this.data
              .getCategoryName().toLowerCase()).toString());
      } catch (SQLException e) {
        e.printStackTrace();
      } 
    } else {
      try {
        Main.Log(RecordBuilder.build().update(new CustomEntry(this.CompteurName, this.compteur), "company_" + this.data
                .getCategoryName().toLowerCase()).toString());
        Main.Database.update(RecordBuilder.build().update(new CustomEntry(this.CompteurName, this.compteur), "company_" + this.data
              .getCategoryName().toLowerCase())
            .where(new CustomEntry("CompanyName", this.data.getCompanyName())).toString());
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
}
