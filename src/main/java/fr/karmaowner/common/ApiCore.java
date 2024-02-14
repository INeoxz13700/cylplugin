package fr.karmaowner.common;

import fr.cylapi.core.IApiCore;
import fr.cylapi.core.ICompanyData;
import fr.cylapi.core.IGangData;
import fr.cylapi.core.IPlayerData;
import fr.cylapi.core.IWeapon;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.GangData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.utils.WeaponUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ApiCore extends JavaPlugin implements IApiCore {
  public ICompanyData getCompanyData(IPlayerData arg0) {
    CompanyData data = CompanyData.getCompanyData(arg0.getCompanyName());
    return CompanyData.Companies.containsKey(arg0.getCompanyName()) ? (ICompanyData)data : null;
  }
  
  public ICompanyData getCompanyData(String arg0) {
    CompanyData data = CompanyData.getCompanyData(arg0);
    return CompanyData.Companies.containsKey(arg0) ? (ICompanyData)data : null;
  }
  
  public IGangData getGangData(Player arg0) {
    GangData data = GangData.getGangData(arg0.getName());
    return GangData.GANGS.containsKey(arg0.getName()) ? (IGangData)data : null;
  }
  
  public IGangData getGangData(String arg0) {
    GangData data = GangData.getGangData(arg0);
    return GangData.GANGS.containsKey(arg0) ? (IGangData)data : null;
  }
  
  public IPlayerData getPlayerData(Player arg0) {
    PlayerData data = PlayerData.getPlayerData(arg0.getName());
    return PlayerData.getHashMap().containsKey(arg0.getName()) ? (IPlayerData)data : null;
  }
  
  public IWeapon getWeaponUtils() {
    return (IWeapon)new WeaponUtils();
  }
}
