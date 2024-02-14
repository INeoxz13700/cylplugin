package fr.karmaowner.companies;

import org.bukkit.Material;

public interface ConvertToXP {
  Company.XP toXP(Material paramMaterial, Byte paramByte);
  
  Company.XP toXP(int paramInt, Byte paramByte);
  
  Company.XP toXP(Material paramMaterial);
}
