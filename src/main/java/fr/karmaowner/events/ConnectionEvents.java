package fr.karmaowner.events;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fr.karmaowner.chat.Chat;
import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.SaveScheduler;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.gps.GPS;
import fr.karmaowner.jobs.Jobs;
import fr.karmaowner.jobs.Security;
import fr.karmaowner.jobs.grades.hasGrade;
import fr.karmaowner.utils.*;
import fr.karmaowner.wantedlist.WantedList;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public class ConnectionEvents implements Listener {
    public static HashMap<Player, Boolean> allowedConnection = new HashMap<>();

    public static int CounterPerSeconds = 0;

    public static long timer = System.currentTimeMillis();

    public static long stopAttack = System.currentTimeMillis();

    public static boolean serverIsAttacked = false;

    public static class oauth {
        public boolean has_auth = false;
        public boolean auth = true;
        public Location lastLocation = null;
    }

    public static CustomConcurrentHashMap<String, oauth> PlayersAuth = new CustomConcurrentHashMap();

    @EventHandler
    public void google_authenticator(PlayerCommandPreprocessEvent e) throws IOException {
        if (PlayersAuth.get(e.getPlayer().getName()) != null && !((oauth) PlayersAuth.get(e.getPlayer().getName())).auth && e.getMessage().contains("auth")) {
            if ((e.getMessage().split(" ")).length == 2) {
                if (Pattern.matches("[0-9]+", e.getMessage().split(" ")[1]) && e.getMessage().split(" ")[1].length() == 6) {
                    e.setCancelled(true);
                    int code = Integer.parseInt(e.getMessage().split(" ")[1]);
                    if (PlayersAuth.get(e.getPlayer().getName()).has_auth) {

                        HTTPUtils http = new HTTPUtils();
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "SbyN8PgwhHPd6aPrw3VhGFrzU2rwG6sb");
                        HashMap<String, String> params = new HashMap<>();
                        params.put("request", "auth_a2f");
                        params.put("pseudo", e.getPlayer().getName());
                        params.put("code", String.valueOf(code));
                        http.sendPostRequest("https://api.craftyourliferp.fr/main_plugin.php", params, headers);
                        String json_string = http.readSingleLineRespone();
                        Gson gson = new Gson();
                        HashMap<String, Object> response = gson.fromJson(json_string, new TypeToken<HashMap<String, Object>>() {
                        }.getType());

                        if ((Double) response.get("statut") == 200) {
                            PlayersAuth.get(e.getPlayer().getName()).auth = true;
                            e.getPlayer().sendMessage("§c[Auth-Double-Facteur] §aAuthentification double facteur réussie !");
                            Bukkit.getServer().getPluginManager().callEvent(new PlayerJoinEvent(e.getPlayer(), null));
                        } else {
                            e.getPlayer().kickPlayer("§c[Auth-Double-Facteur] §4Le code que vous avez saisi est incorrect. Le code attendu est celui affiché sur l'application android §cAuthenticator");
                        }
                    }
                } else {
                    MessageUtils.sendMessage(e.getPlayer(), "§c[Auth-Double-Facteur] §4Le code doit être constitué que des chiffres et doit contenir 6 chiffres.");
                }
            } else {
                MessageUtils.sendMessage(e.getPlayer(), "§c[Auth-Double-Facteur] §4Usage: /auth <code>");
            }
            return;
        }

        if ((PlayersAuth.get(e.getPlayer().getName()) != null && !((oauth) PlayersAuth.get(e.getPlayer().getName())).auth) || !(PlayerData.getPlayerData(e.getPlayer().getName())).passwordReceived)
            e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity ent = event.getEntity();
        if (ent instanceof Player) {
            Player p = (Player) ent;
            if (PlayersAuth.get(p.getName()) != null && !((oauth) PlayersAuth.get(p.getName())).auth &&
                    event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onlogin(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (PlayersAuth.get(p.getName()) != null && !((oauth) PlayersAuth.get(p.getName())).auth)
            e.setCancelled(true);
    }

    @EventHandler
    public void drop(PlayerDropItemEvent e) {
        if (PlayersAuth.get(e.getPlayer().getName()) != null && !((oauth) PlayersAuth.get(e.getPlayer().getName())).auth)
            e.setCancelled(true);
    }

    @EventHandler
    public void newPlayer(PlayerPreLoginEvent e) {
        if (!Bukkit.getOfflinePlayer(e.getName()).hasPlayedBefore()) {
            Main.Log("Nouveau arrivant détecté: " + e.getName());
            Delay.newInstance(15.0D, e.getName(), "newplayer");
        }
    }

    @EventHandler
    public void onlogin(PlayerLoginEvent e) {
        boolean isLogged = false;
        if (SaveScheduler.saveIsRunning) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§4Serveur en cours de sauvegarde. Veuillez patienter un petit moment avant de réessayer.");
            return;
        }
        CounterPerSeconds++;

        PlayersAuth.put(e.getPlayer().getName(), new oauth());
        try {
            HTTPUtils http = new HTTPUtils();
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", "SbyN8PgwhHPd6aPrw3VhGFrzU2rwG6sb");
            HashMap<String, String> params = new HashMap<>();
            params.put("request", "has_a2f");
            params.put("pseudo", e.getPlayer().getName());
            http.sendPostRequest("https://api.craftyourliferp.fr/main_plugin.php", params, headers);
            String json_string = http.readSingleLineRespone();
            Gson gson = new Gson();
            HashMap<String, Object> response = gson.fromJson(json_string, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            PlayersAuth.get(e.getPlayer().getName()).has_auth = (Double)response.get("statut") == 200.0;

            if (PlayersAuth.get(e.getPlayer().getName()).has_auth) {
                PlayersAuth.get(e.getPlayer().getName()).auth = false;
            } else {
                PlayersAuth.remove(e.getPlayer().getName());
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        long now = System.currentTimeMillis();
        if (now - timer >= 1000L) {
            timer = System.currentTimeMillis();
            CounterPerSeconds = 0;
        }
        if (CounterPerSeconds > 5 && !serverIsAttacked) {
            stopAttack = System.currentTimeMillis();
            serverIsAttacked = true;
            if (!Bukkit.hasWhitelist())
                Bukkit.setWhitelist(true);
        }
        if (serverIsAttacked && now - stopAttack >= 60000L && CounterPerSeconds <= 5) {
            serverIsAttacked = false;
            if (Bukkit.hasWhitelist())
                Bukkit.setWhitelist(false);
        }
        if (serverIsAttacked) {
            try {
                if (PlayerData.PlayerAlreadyPlayed(e.getPlayer().getName())) {
                    isLogged = true;
                    e.getPlayer().setWhitelisted(true);
                } else {
                    e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§4File d'attente activée: §c Merci de réessayer dans 1 min.");
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } else {
            isLogged = true;
        }
        allowedConnection.put(e.getPlayer(), isLogged);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(final PlayerJoinEvent e) throws Exception {
        final Player player = e.getPlayer();
        if (SaveScheduler.saveIsRunning)
            return;
        boolean allow = (Boolean) allowedConnection.get(player);
        if (!allow || (PlayersAuth.get(e.getPlayer().getName()) != null && !((oauth) PlayersAuth.get(e.getPlayer().getName())).auth)) {
            if (PlayersAuth.get(e.getPlayer().getName()) != null && !((oauth) PlayersAuth.get(e.getPlayer().getName())).auth) {
                Location lastPosition = player.getLocation().clone();
                ((oauth) PlayersAuth.get(e.getPlayer().getName())).lastLocation = lastPosition;
                new TaskCreator(new CustomRunnable() {
                    private int i = 0;

                    private long elapsed = 0L;

                    public void customRun() {
                        if (Bukkit.getPlayerExact(player.getName()) == null) {
                            ConnectionEvents.PlayersAuth.remove(player.getName());
                            cancel();
                            return;
                        }
                        if (ConnectionEvents.PlayersAuth.get(e.getPlayer().getName()) != null && ((ConnectionEvents.oauth) ConnectionEvents.PlayersAuth.get(e.getPlayer().getName())).auth) {
                            player.teleport(((ConnectionEvents.oauth) ConnectionEvents.PlayersAuth.get(e.getPlayer().getName())).lastLocation);
                            ConnectionEvents.PlayersAuth.remove(player.getName());
                            cancel();
                            return;
                        }
                        if (System.currentTimeMillis() - this.elapsed >= 15000L) {
                            if (this.i++ == 5) {
                                player.teleport(((ConnectionEvents.oauth) ConnectionEvents.PlayersAuth.get(e.getPlayer().getName())).lastLocation);
                                ConnectionEvents.PlayersAuth.remove(player.getName());
                                player.kickPlayer("§c[Auth-Double-Facteur] §4Temps écoulé. Veuillez vous reconnecter pour réessayer à nouveau !");
                            }
                            this.elapsed = System.currentTimeMillis();
                            player.sendMessage(StringUtils.repeat(" \n", 100));
                            player.sendMessage("§c[Auth-Double-Facteur] §aVeuillez entrer le code qui s'affiche à l'écran de votre portable en tapant la commande §2/auth <code>");
                        }
                    }
                }, false, 20L, 20L);
                player.teleport(new Location(player.getWorld(), 0.0D, 10.0D, 0.0D));
            }
            return;
        }
        if (!player.hasPermission("cylrp.staff"))
            e.getPlayer().setWhitelisted(false);
        for (PotionEffect effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());
        final PlayerData data = PlayerData.getPlayerData(player.getName());
        Main.Database.update(RecordBuilder.build().update(new CustomEntry("connected", Boolean.TRUE), "players_data")
                .where(new CustomEntry("pseudo", player.getName())).toString());
        player.setGameMode(GameMode.SURVIVAL);

        if (!data.selectedJob.getFeatures().isIllegal() && !(data.selectedJob instanceof fr.karmaowner.jobs.Civile)) {
            if (data.helmet == null)
                if (player.getInventory().getHelmet() != null && !Jobs.isJobClothesItem(player.getInventory().getHelmet()))
                    data.helmet = ItemUtils.getItem(player.getInventory().getHelmet().getTypeId(), player
                            .getInventory().getHelmet().getData().getData(), player.getInventory().getHelmet().getAmount(), null, null);
            if (data.chestplate == null)
                if (player.getInventory().getChestplate() != null && !Jobs.isJobClothesItem(player.getInventory().getChestplate()))
                    data.chestplate = ItemUtils.getItem(player.getInventory().getChestplate().getTypeId(), player
                            .getInventory().getChestplate().getData().getData(), player.getInventory().getChestplate().getAmount(), null, null);
            if (data.leggings == null)
                if (player.getInventory().getLeggings() != null && !Jobs.isJobClothesItem(player.getInventory().getLeggings()))
                    data.leggings = ItemUtils.getItem(player.getInventory().getLeggings().getTypeId(), player
                            .getInventory().getLeggings().getData().getData(), player.getInventory().getLeggings().getAmount(), null, null);
            if (data.boots == null)
                if (player.getInventory().getBoots() != null && !Jobs.isJobClothesItem(player.getInventory().getBoots()))
                    data.boots = ItemUtils.getItem(player.getInventory().getBoots().getTypeId(), player
                            .getInventory().getBoots().getData().getData(), player.getInventory().getBoots().getAmount(), null, null);
            if (data.gpb == null)
                if (player.getInventory().getBoots() != null && !Jobs.isJobClothesItem(player.getInventory().getBoots()))
                    data.boots = ItemUtils.getItem(player.getInventory().getBoots().getTypeId(), player
                            .getInventory().getBoots().getData().getData(), player.getInventory().getBoots().getAmount(), null, null);

        }
        new TaskCreator(new CustomRunnable() {
            public void customRun() {
                ArrayList<String> removed = data.updateForbidState();
                if (Bukkit.getPlayerExact(player.getName()) == null) {
                    cancel();
                    return;
                }
                if (removed.size() > 0)
                    for (String job : removed)
                        player.sendMessage(ChatColor.GREEN + "Vous pouvez de nouveau exercer le m�tier de " + ChatColor.DARK_GREEN + job);
            }
        }, false, 0L, 1200L);
        data.salaryTimeStamp = new Timestamp(System.currentTimeMillis());
        if (data.isMenotte)
            data.setMenotte(false, null);
        if (data.selectedJob instanceof hasGrade) {
            hasGrade grade = (hasGrade) data.selectedJob;
            grade.getGrade().startTaskTimer();
        }
        if (data.teleport != null)
            new TaskCreator(new CustomRunnable() {
                public void customRun() {
                    if (Bukkit.getPlayerExact(player.getName()) == null || data.teleport == null) {
                        cancel();
                        return;
                    }
                    Timestamp now = new Timestamp(System.currentTimeMillis());
                    if (data.teleport != null && now.getTime() - data.teleport.getTime() >= data.waitTime) {
                        Bukkit.dispatchCommand((CommandSender) Bukkit.getConsoleSender(), "warp comico " + player.getName());
                        for (PotionEffect potion : player.getActivePotionEffects())
                            player.removePotionEffect(potion.getType());
                        data.waitTime = 0L;
                        data.teleport = null;
                        cancel();
                    } else if (data.teleport != null) {
                        Timestamp timeLeft = new Timestamp(data.waitTime - now.getTime() - data.teleport.getTime());
                        Bukkit.dispatchCommand((CommandSender) Bukkit.getConsoleSender(), "hudmessage 500 " + player.getName() + " 1 " + ChatColor.DARK_RED.toString() + timeLeft.getMinutes() + ":" + timeLeft.getSeconds());
                    }
                }
            }, false, 0L, 20L);
        if (WantedList.isWanted(e.getPlayer().getName()) && !(data.selectedJob instanceof fr.karmaowner.jobs.Security) && !(data.selectedJob instanceof fr.karmaowner.jobs.Pompier) && !(data.selectedJob instanceof fr.karmaowner.jobs.Medecin) && !(data.selectedJob instanceof fr.karmaowner.jobs.JobsMairie)) {
            int stars = WantedList.getStars(e.getPlayer().getName());
            WantedList.TaskSemer(e.getPlayer().getName());
            WantedList.wantedMessage(e.getPlayer().getName(), stars);
        } else {
            if(!data.selectedJob.isLegalJob())
            {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "noppes faction " + e.getPlayer().getName() + " defender set 1000");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "noppes faction " + e.getPlayer().getName() + " illegal set 1600");
            }
            else if(data.selectedJob instanceof Security) //si fdl
            {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "noppes faction " + e.getPlayer().getName() + " defender set 1600");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "noppes faction " + e.getPlayer().getName() + " illegal set 0");
            }
            else
            {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "noppes faction " + e.getPlayer().getName() + " defender set 600");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "noppes faction " + e.getPlayer().getName() + " illegal set 600");
            }
        }

        if (!data.keepJob && !data.selectedJob.getFeatures().playerHasJobPermission(player)) {
            JobsEvents.changePlayerJob(data, Jobs.Job.CIVILE.getName(), e.getPlayer().getName());
            e.getPlayer().sendMessage("§dVous n'avez pas la permission du métier par conséquent votre mêtier a été changé.");
        }

        InventoryUtils.addAnInexistantItemInInventory((Inventory) player.getInventory(),
                ItemUtils.getItem(ServerUtils.CARTEIDENTITE, (byte) 0, 1, null, null));
        InventoryUtils.addAnInexistantItemInInventory((Inventory) player.getInventory(),
                ItemUtils.getItem(345, (byte) 0, 1, "§cGPS", Arrays.asList("§aClic-droit pour l'activer")));

        InventoryUtils.removeDuplicateItemStack(player,ServerUtils.CARTEIDENTITE, (byte)0);
        InventoryUtils.removeDuplicateItemStack(player,345, (byte)0);

        if (!data.selectedJob.isOutOfService()) {
            if (!data.selectedJob.getFeatures().isIllegal()) {
                data.selectedJob.equipClothes();
                if (data.selectedJob instanceof hasGrade) {
                    ((hasGrade) data.selectedJob).getGrade().equipGrade();
                    ((hasGrade) data.selectedJob).getGrade().equipInventory();
                }
            }
        } else if (!data.selectedJob.getFeatures().isIllegal()) {
            player.getEquipment().setHelmet(data.helmet);
            player.getEquipment().setChestplate(data.chestplate);
            player.getEquipment().setLeggings(data.leggings);
            player.getEquipment().setBoots(data.boots);
        }
        Chat.getDefaultGroup().addPlayer(e.getPlayer().getName());
        new TaskCreator(new CustomRunnable() {
            public void customRun() {
                if (player.hasPermission("cylrp.staff")) {
                    cancel();
                    return;
                }
                Player p = Bukkit.getPlayerExact(player.getName());
                if (p == null) {
                    cancel();
                    return;
                }
                PlayerUtils pu = new PlayerUtils();
                pu.setPlayer(player);
                pu.setItem(player.getItemInHand());
                String kitperm = pu.isItemKit();
                if (kitperm != null) {
                    String kitname = kitperm.split("\\.")[2];
                    if (data.selectedJob instanceof fr.karmaowner.jobs.Security && (kitname
                            .equalsIgnoreCase("shotgun") || kitname
                            .equalsIgnoreCase("tireurelite") || kitname
                            .equalsIgnoreCase("assault") || kitname
                            .equalsIgnoreCase("explosif")) && !player.hasPermission(kitperm))
                        if (player.getItemInHand() != null && player.getItemInHand().getItemMeta() != null)
                            if (player.getItemInHand().getItemMeta().getLore() != null)
                                for (String l : player.getItemInHand().getItemMeta().getLore()) {
                                    if (l.contains("§cKit")) {
                                        player.getInventory().setHeldItemSlot((player.getInventory().getHeldItemSlot() + 1) % 8);
                                        player.sendMessage(ChatColor.RED + "Vous ne pouvez pas intéragir avec cet objet. Vous n'avez pas le kit " + kitname);
                                        break;
                                    }
                                }
                }
            }
        }, false, 0L, 20L);
        new TaskCreator(new CustomRunnable() {
            public void customRun() {
                if (Main.essentials.getUser(player).isAfk() && !player.hasPermission("cylrp.staff")) {
                    player.kickPlayer("§aVous avez été inactif pendant plusieurs minutes");
                    cancel();
                }
            }
        }, false, 0L, 20L);
    }

    public static void onLeaveMethod(Player p) {
        if (p == null)
            return;
        boolean allow = (Boolean) allowedConnection.remove(p);
        if (!allow || (PlayersAuth.get(p.getName()) != null && !((oauth) PlayersAuth.get(p.getName())).auth)) {
            if (PlayersAuth.get(p.getName()) != null && !((oauth) PlayersAuth.get(p.getName())).auth)
                p.teleport(((oauth) PlayersAuth.get(p.getName())).lastLocation);
            return;
        }
        PlayerData data = PlayerData.getPlayerData(p.getName());
        data.passwordReceived = false;
        try {
            Main.Database.update(RecordBuilder.build().update(new CustomEntry("connected", Boolean.FALSE), "players_data")
                    .where(new CustomEntry("pseudo", p.getName())).toString());
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        PlayerInventory inv = p.getInventory();
        boolean isOut = data.selectedJob.isOutOfService();
        if (data.selectedJob.isOutOfService()) {
            if (p.getInventory().getHelmet() != null && !Jobs.isJobClothesItem(p.getInventory().getHelmet())) {
                data.helmet = ItemUtils.getItem(p.getInventory().getHelmet().getTypeId(), p
                        .getInventory().getHelmet().getData().getData(), p.getInventory().getHelmet().getAmount(), null, null);
            } else {
                data.helmet = null;
            }
            if (p.getInventory().getChestplate() != null && !Jobs.isJobClothesItem(p.getInventory().getChestplate())) {
                data.chestplate = ItemUtils.getItem(p.getInventory().getChestplate().getTypeId(), p
                        .getInventory().getChestplate().getData().getData(), p.getInventory().getChestplate().getAmount(), null, null);
            } else {
                data.chestplate = null;
            }
            if (p.getInventory().getLeggings() != null && !Jobs.isJobClothesItem(p.getInventory().getLeggings())) {
                data.leggings = ItemUtils.getItem(p.getInventory().getLeggings().getTypeId(), p
                        .getInventory().getLeggings().getData().getData(), p.getInventory().getLeggings().getAmount(), null, null);
            } else {
                data.leggings = null;
            }
            if (p.getInventory().getBoots() != null && !Jobs.isJobClothesItem(p.getInventory().getBoots())) {
                data.boots = ItemUtils.getItem(p.getInventory().getBoots().getTypeId(), p
                        .getInventory().getBoots().getData().getData(), p.getInventory().getBoots().getAmount(), null, null);
            } else {
                data.boots = null;
            }
        } else if (!data.selectedJob.getFeatures().isIllegal() && !(data.selectedJob instanceof fr.karmaowner.jobs.Civile)) {
            data.selectedJob.setOutOfService(true);
            data.selectedJob.unequipClothes(data, false, false);
        }
        if (!data.selectedJob.getFeatures().isIllegal() && !(data.selectedJob instanceof fr.karmaowner.jobs.Civile)) {
            inv.setHelmet(data.helmet);
            inv.setChestplate(data.chestplate);
            inv.setLeggings(data.leggings);
            inv.setBoots(data.boots);
        }
        if (GPS.gpse.containsKey(p)) {
            ((TaskCreator) GPS.gpse.get(p)).cancelTask();
            GPS.gpse.remove(p);
        }
        GPS.gps.remove(p);
        if (!isOut)
            data.selectedJob.setOutOfService(isOut);
        Chat.leftFromCanal(p.getName());
        Chat.getDefaultGroup().deletePlayer(p.getName());
        data.saveData();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        onLeaveMethod(e.getPlayer());
    }
}
