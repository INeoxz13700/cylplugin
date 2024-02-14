package fr.karmaowner.common;

import com.earth2me.essentials.Essentials;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.cylapi.core.IPlayerData;
import fr.karmaowner.chat.ChatEmoji;
import fr.karmaowner.chat.ChatGroup;
import fr.karmaowner.chat.events.ChattingEvents;
import fr.karmaowner.chat.group.Entreprise;
import fr.karmaowner.chat.group.Gang;
import fr.karmaowner.chat.group.HRP;
import fr.karmaowner.chat.group.RP;
import fr.karmaowner.colis.Coli;
import fr.karmaowner.colis.ColiEvents;
import fr.karmaowner.commands.*;
import fr.karmaowner.companies.armurerie.ArmurerieEvents;
import fr.karmaowner.companies.shop.Npc;
import fr.karmaowner.companies.shop.events.CompanyShopEvents;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.GangData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.data.PnjsEggsData;
import fr.karmaowner.drogue.Drogue;
import fr.karmaowner.drogue.DrogueEvents;
import fr.karmaowner.election.VoteEvents;
import fr.karmaowner.events.*;
import fr.karmaowner.events.jobs.*;
import fr.karmaowner.gangs.Capture;
import fr.karmaowner.gangs.events.CaptureListener;
import fr.karmaowner.gangs.events.GangListener;
import fr.karmaowner.gps.GPS;
import fr.karmaowner.gps.GPSInventory;
import fr.karmaowner.jobs.Jobs;
import fr.karmaowner.jobs.Prisons;
import fr.karmaowner.jobs.Salary;
import fr.karmaowner.jobs.chauffeur.Regions;
import fr.karmaowner.jobs.hacker.MakeHackingGameEvents;
import fr.karmaowner.jobs.missions.Missions;
import fr.karmaowner.jobs.missions.MissionsEvents;
import fr.karmaowner.jobs.missions.type.events.KillTaskEvents;
import fr.karmaowner.jobs.parcelle.Champ;
import fr.karmaowner.jobs.parcelle.Enclo;
import fr.karmaowner.jobs.parcelle.Local;
import fr.karmaowner.jobs.parcelle.events.ParcelleEvents;
import fr.karmaowner.medikit.Medikit;
import fr.karmaowner.playermove.PlayerMoveListener;
import fr.karmaowner.restore.regions.Restore;
import fr.karmaowner.time.TimeRunnable;
import fr.karmaowner.tresorerie.MaireTresorerie;
import fr.karmaowner.tresorerie.StaffTresorerie;
import fr.karmaowner.tresorerie.Tresorerie;
import fr.karmaowner.utils.RegionUtils;
import fr.karmaowner.utils.events.AttentatAreaListener;
import fr.karmaowner.utils.events.RegionsListener;
import fr.karmaowner.wantedlist.WantedList;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import net.citizensnpcs.Citizens;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.chat.Chat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Main extends ApiCore {
    public static String prefix = "§7[§bCraftYourLifeRP§7]";

    public static final String version = "1.0";

    public static final String name = "CYLRP-CORE";

    public static final String WORLDNAME = "cyl";

    public static final String WORLDNAME2 = "world1";

    public static final String PLAYERDEBUG = "Ozmentv";

    public static Main INSTANCE;

    public static Essentials essentials;

    public static Chat chat;

    public static Vault vault;

    public static WorldGuardPlugin WG;

    public PnjsEggsData pnjsEggs;

    public static Citizens npclib;

    public static SaveScheduler saveScheduler = new SaveScheduler();

    public static boolean saveState = false;

    public static boolean resetRanking = false;

    public static boolean showLogs = true;

    public static SqlConnector Database;

    public boolean localhost = false;

    public void onEnable() {
        INSTANCE = this;
        WG = (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");
        vault = (Vault) getServer().getPluginManager().getPlugin("Vault");
        npclib = (Citizens) Bukkit.getPluginManager().getPlugin("Citizens");
        if (vault != null) {
            RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
            if (chatProvider != null)
                chat = (Chat) chatProvider.getProvider();
        }
        saveDefaultConfig();
        Database = new SqlConnector();
        if (this.localhost) {
            try {
                Database.connect("localhost", "cylrp", "root", "");
            } catch (ClassNotFoundException | SQLException e1) {
                e1.printStackTrace();
                Database = null;
            }
        } else {
            try {
                Database.connect("localhost", "cylrp", "cyl", "j4oS99AX9rc43PwT9KacGxOv3");
            } catch (ClassNotFoundException | SQLException e1) {
                e1.printStackTrace();
                Database = null;
            }
        }

        registerCommands();
        registerEvents();
        Jobs.fillInventory();
        try {
            Jobs.Job.loadDatas();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Prisons.loadData();
        Prisons.fillInvPrison();
        Coli.loadData();
        prefix = getConfig().getString("prefix").replaceAll("&", "§");
        essentials = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
        new TaskCreator((CustomRunnable) new TimeRunnable(this), false, 0L, 20L);
        ChatEmoji.loadEmojis();
        fr.karmaowner.chat.Chat.createGroup((ChatGroup) new Entreprise());
        fr.karmaowner.chat.Chat.createGroup((ChatGroup) new RP());
        fr.karmaowner.chat.Chat.createGroup((ChatGroup) new HRP());
        Local.loadData();
        Enclo.loadData();
        Champ.loadData();
        this.pnjsEggs = new PnjsEggsData();
        new Salary();
        new TaskCreator(new CustomRunnable() {
            public void customRun() {
                Npc.loadData();
                Main.this.pnjsEggs.loadData();
            }
        }, false, 40L);
        new TaskCreator(new CustomRunnable() {
            private Timestamp Timer1 = new Timestamp(0L);

            private Timestamp Timer2 = new Timestamp(0L);

            public void customRun() {
                ProtectedRegion bankRg = RegionUtils.getRegionByName("bank-regen", "cyl");
                ProtectedRegion bijouterieRg = RegionUtils.getRegionByName("bijouterie", "cyl");
                List<Player> playersInBank = RegionUtils.getPlayersInRegion(bankRg);
                List<Player> playersInBijouterie = RegionUtils.getPlayersInRegion(bijouterieRg);
                Timestamp now = new Timestamp(System.currentTimeMillis());
                if (playersInBank.size() > 0)
                    if (now.getTime() - this.Timer1.getTime() >= 300000L) {
                        for (Player p : playersInBank) {
                            PlayerData pData = PlayerData.getPlayerData(p.getName());
                            if (pData != null && (pData.selectedJob instanceof fr.karmaowner.jobs.Voleur || pData.selectedJob instanceof fr.karmaowner.jobs.RebelleTerroriste)) {
                                WantedList.addStars(p.getName(), 3);
                                WantedList.wantedMessagePlace(p.getName(), 3, "à la banque §2(x=" + bankRg.getMinimumPoint().getBlockX() + ";z=" + bankRg.getMinimumPoint().getBlockZ() + ")");
                            }
                        }
                        for (Player p : playersInBank) {
                            PlayerData pData = PlayerData.getPlayerData(p.getName());
                            if (pData != null && (pData.selectedJob instanceof fr.karmaowner.jobs.Voleur || pData.selectedJob instanceof fr.karmaowner.jobs.RebelleTerroriste)) {
                                this.Timer1 = new Timestamp(System.currentTimeMillis());
                                Jobs.Job.sendMessageSecurityJobs("§4[Alerte]", "§aLa banque se fait braquer §2(x=" + bankRg.getMinimumPoint().getBlockX() + ";z=" + bankRg.getMinimumPoint().getBlockZ() + ")§a, toutes les forces de l'ordre doivent se rendre sur place !");
                                break;
                            }
                        }
                    }
                if (playersInBijouterie.size() > 0)
                    if (now.getTime() - this.Timer2.getTime() >= 300000L) {
                        for (Player p : playersInBijouterie) {
                            PlayerData pData = PlayerData.getPlayerData(p.getName());
                            if (pData != null && (pData.selectedJob instanceof fr.karmaowner.jobs.Voleur || pData.selectedJob instanceof fr.karmaowner.jobs.RebelleTerroriste)) {
                                WantedList.addStars(p.getName(), 3);
                                WantedList.wantedMessagePlace(p.getName(), 3, "à la bijouterie");
                            }
                        }
                        for (Player p : playersInBijouterie) {
                            PlayerData pData = PlayerData.getPlayerData(p.getName());
                            if (pData != null && (pData.selectedJob instanceof fr.karmaowner.jobs.Voleur || pData.selectedJob instanceof fr.karmaowner.jobs.RebelleTerroriste)) {
                                this.Timer2 = new Timestamp(System.currentTimeMillis());
                                Jobs.Job.sendMessageSecurityJobs("§4[Alerte]", "§aLa bijouterie se fait braquer, toutes les forces de l'ordre doivent se rendre sur place !");
                                break;
                            }
                        }
                    }
                if (Calendar.getInstance().getTime().getDate() == 1 && Calendar.getInstance().getTime().getHours() == 0 && Calendar.getInstance().getTime().getMinutes() >= 0 && Calendar.getInstance().getTime().getMinutes() <= 5)
                    Main.resetRanking = true;

                if (Main.resetRanking) {
                    try {
                        GangData.resetRanking();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Main.resetRanking = false;
                }
            }
        }, false, 0L, 10L);
        WantedList.loadData();
        Capture.loadData();
        Restore.INSTANCE.loadData();
        Drogue.INSTANCE.loadData();
        Regions.loadData();
        Missions.loadDataMission();
        StaffTresorerie staffTresorerie = new StaffTresorerie();
        staffTresorerie.loadData();
        MaireTresorerie maireTresorerie = new MaireTresorerie();
        maireTresorerie.loadData();
        saveScheduler.activateScheduler();
    }

    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers())
            ConnectionEvents.onLeaveMethod(p);
        WantedList.saveData();
        Prisons.saveData();
        Regions.saveData();
        PlayerData.SaveDatas();
        try {
            Jobs.Job.saveDatas();
        } catch (Exception e) {
            e.printStackTrace();
        }
        CompanyData.saveDatas();

        Capture.saveData();
        Npc.saveData();
        GangData.saveDatas();
        Drogue.INSTANCE.saveData();
        Tresorerie tresorerie = Tresorerie.getTresorerie("staff");
        if(tresorerie != null)
        {
            tresorerie.saveData();
        }
        tresorerie = Tresorerie.getTresorerie("maire");
        if(tresorerie != null){
            tresorerie.saveData();
        }
        INSTANCE.saveConfig();
        System.out.println("disabled CYLRP-CORE");
    }

    public static void sendMessageDebugPlayer(String msg) {
        if (Bukkit.getPlayerExact("Ozmentv") != null) Bukkit.getPlayerExact("Ozmentv").sendMessage(msg);
    }

    private void registerCommands() {
        getCommand("apidata").setExecutor((CommandExecutor) new CommandApi());
        getCommand("entreprise").setExecutor((CommandExecutor) new CommandCompany());
        getCommand("casino").setExecutor((CommandExecutor) new CommandCasino());
        getCommand("jobs").setExecutor((CommandExecutor) new CommandJobs());
        getCommand("colis").setExecutor((CommandExecutor) new CommandColi());
        getCommand("atm").setExecutor((CommandExecutor) new CommandATM());
        getCommand("chat").setExecutor((CommandExecutor) new CommandChat());
        getCommand("restorerg").setExecutor((CommandExecutor) new CommandRestore());
        getCommand("gang").setExecutor((CommandExecutor) new CommandGang());
        getCommand("drogue").setExecutor((CommandExecutor) new CommandDrogue());
        getCommand("destination").setExecutor((CommandExecutor) new CommandGPSAdd());
        getCommand("votemaire").setExecutor((CommandExecutor) new CommandElection());
        getCommand("migrate").setExecutor((CommandExecutor) new CommandMigrationData());
        getCommand("tresorerie").setExecutor((CommandExecutor) new CommandTresorerie());
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents((Listener) new ConnectionEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new JackpotEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new RouletteEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new TictactocEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new CompanyBucheronEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new CompanyEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new CompanyMenuiserieEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new CompanyMinageEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new CompanyForgeronEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new CompanyPecheEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new CompanyMetallurgieEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new CompanyAgricultureEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new CompanyElevageEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new CompanyChasseEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new PnjsEggsEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new CompanyShopEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new JobsEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new HackerEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new CuisinierEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new TaxiEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new VoleurEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new RebelleTerroristeEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new GignEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new MissionsEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new KillTaskEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new SecuriteEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new DouanierEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new ColiEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new ChattingEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new ParcelleEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new Medikit(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new CaptureListener(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new RegionsListener(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new GangListener(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new JobsRegion(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new ExplosionEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new MakeHackingGameEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new DrogueEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new CommandBlockerEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new GPSInventory(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new GPS(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new VoteEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new AttentatAreaListener(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new WantedListEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new PsychopatheEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new BacEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new MoveCadavreEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new ArmurerieEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new PlayerMoveListener(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new VipRegion(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new LoggerEvent(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new RegionsEvent(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new MedecinEvents(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new CommandEvents(), (Plugin) this);
    }



    public static void Log(String message) {
        if (showLogs)
            System.out.println("[CYLRP-CORE] " + message);
    }


}
