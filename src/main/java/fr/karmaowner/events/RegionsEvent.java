package fr.karmaowner.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.common.Main;
import fr.karmaowner.utils.events.RegionEnterEvent;
import fr.karmaowner.utils.events.RegionQuitEvent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class RegionsEvent implements Listener {

    @EventHandler
    public void onRegionEnter(RegionEnterEvent event)
    {
        String regionsList = "";
        for(ProtectedRegion rg : event.getRegions())
        {
            regionsList += rg.getId()+"#SEP#";
        }
        regionsList = StringUtils.strip(regionsList,"#SEP#");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"api region " + event.getPlayer().getName() + " enter " + regionsList);
    }

    @EventHandler
    public void onRegionExit(RegionQuitEvent event)
    {
        String regionsList = "";
        for(ProtectedRegion rg : event.getRegions())
        {
            regionsList += rg.getId()+"#SEP#";
        }
        regionsList = StringUtils.strip(regionsList,"#SEP#");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"api region " + event.getPlayer().getName() + " exit " + regionsList);
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event)
    {
        String regionsList = "";
        for(ProtectedRegion rg : Main.WG.getRegionManager(event.getPlayer().getWorld()).getApplicableRegions(event.getPlayer().getLocation()).getRegions())
        {
            regionsList += rg.getId()+"#SEP#";
        }
        regionsList = StringUtils.strip(regionsList,"#SEP#");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"api region " + event.getPlayer().getName() + " enter " + regionsList);
    }

}
