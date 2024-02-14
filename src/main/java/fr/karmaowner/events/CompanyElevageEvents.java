package fr.karmaowner.events;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.karmaowner.companies.Company;
import fr.karmaowner.companies.CompanyElevage;
import fr.karmaowner.companies.eggs.EggsHatching;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.utils.RegionUtils;

import java.util.Set;

import fr.karmaowner.utils.TimerUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Cauldron;

public class CompanyElevageEvents implements Listener {
    @EventHandler
    public void onClickCauldron(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        CompanyData data = CompanyData.Companies.get((PlayerData.getPlayerData(p.getName())).companyName);
        if (data != null &&
                data.getCompany() != null &&
                data.getCompany() instanceof CompanyElevage) {
            CompanyElevage ce = (CompanyElevage) data.getCompany();
            if (!data.getCompany().isThatRegion) {
                if (e.getClickedBlock() != null)
                    if (e.getClickedBlock().getType() != Material.CAULDRON)
                        return;
                if (e.getClickedBlock() != null) {
                    RegionManager rgm = RegionUtils.getRegionManager(e.getPlayer().getWorld().getName());
                    Location l = e.getClickedBlock().getLocation();
                    ApplicableRegionSet set = rgm.getApplicableRegions(l);
                    Set<ProtectedRegion> rgs = set.getRegions();
                    for (ProtectedRegion r : rgs) {
                        if (!r.getId().contains("eleveur")) {
                            p.sendMessage(ChatColor.DARK_RED + "Vous ne pouvez pas remplir le chaudron à cet endroit");
                            e.setCancelled(true);
                            return;
                        }
                    }
                } else {
                    return;
                }
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    ItemStack itemInHand = e.getItem();
                    if (itemInHand != null) {
                        int id = itemInHand.getTypeId();
                        byte getData = itemInHand.getData().getData();
                        CompanyElevage.XP_ELEVAGE noneclos = CompanyElevage.XP_ELEVAGE.getEnum(itemInHand.getItemMeta().getDisplayName());
                        if (e.getClickedBlock().getState().getData() instanceof Cauldron)
                            if (id == 383) {
                                if (noneclos != null) {
                                    if (ce.getEggsData().getEggs().size() < 27) {
                                        if (ce.isItemUnlocked(noneclos)) {
                                            Cauldron c = (Cauldron) e.getClickedBlock().getState().getData();
                                            if (c.isFull()) {
                                                BlockState state = e.getClickedBlock().getState();
                                                ce.getEggsData().addEggs(new EggsHatching(noneclos.getTime(), noneclos.getTypeId(), noneclos.getName()));
                                                ce.addXp(p, noneclos);
                                                p.getInventory().remove(itemInHand);
                                                p.sendMessage(ChatColor.DARK_AQUA + "Vous venez de rajouter un " + noneclos.getName() + " dans l'incubateur. C'est parti pour " + TimerUtils.formatString(noneclos.getTime()) + " d'attente !");
                                                ce.setCompanyAchievements();
                                                state.getData().setData((byte) 0);
                                                state.update();
                                            } else {
                                                p.sendMessage(ChatColor.DARK_RED + "Chaudron vide. remplissez-le d'eau !");
                                            }
                                        } else {
                                            ce.locked_Message(p, noneclos);
                                        }
                                    } else {
                                        p.sendMessage(ChatColor.DARK_RED + "Vous avez atteint la limite d'oeufs dans l'inventaire");
                                    }
                                } else {
                                    p.sendMessage(ChatColor.DARK_RED + "Ce n'est pas un oeuf valide");
                                }
                            }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onAnimalBirth(CreatureSpawnEvent e) {
        CreatureSpawnEvent.SpawnReason reason = e.getSpawnReason();
        LivingEntity livingEntity = e.getEntity();
        for (Entity entity : livingEntity.getNearbyEntities(2.0D, 2.0D, 2.0D)) {
            if (entity instanceof Player) {
                Player p = (Player) entity;
                CompanyData data = (CompanyData) CompanyData.Companies.get((PlayerData.getPlayerData(p.getName())).companyName);
                if (data != null &&
                        data.getCompany() != null)
                    if (data.getCompany() instanceof CompanyElevage)
                        if (reason.equals(CreatureSpawnEvent.SpawnReason.BREEDING)) {
                            CompanyElevage ce = (CompanyElevage) data.getCompany();
                            EntityType type = livingEntity.getType();
                            CompanyElevage.XP_ELEVAGE x = (CompanyElevage.XP_ELEVAGE) ce.toXP(type);
                            if (x != null) {
                                ItemStack item = new ItemStack(x.getId(), 1, (short) 0, x.getData());
                                ItemMeta meta = item.getItemMeta();
                                meta.setDisplayName(x.getName());
                                item.setItemMeta(meta);
                                p.getInventory().addItem(item);
                                p.sendMessage(ChatColor.GREEN + "Accouplement réussi ! Vous venez d'obtenir un " + x.getName());
                                e.setCancelled(true);
                                return;
                            }
                        }
            }
        }
        if (reason != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG && reason != CreatureSpawnEvent.SpawnReason.CUSTOM && reason != CreatureSpawnEvent.SpawnReason.SPAWNER && reason != CreatureSpawnEvent.SpawnReason.DEFAULT)
            e.setCancelled(true);
    }
}
