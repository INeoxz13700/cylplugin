package fr.karmaowner.data;

import com.earth2me.essentials.User;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import fr.cylapi.core.IGrade;
import fr.cylapi.core.IGrades;
import fr.cylapi.core.IJob;
import fr.cylapi.core.IPlayerData;
import fr.cylapi.core.IRpPlayer;
import fr.karmaowner.casino.Casino;
import fr.karmaowner.common.Main;
import fr.karmaowner.jobs.Jobs;
import fr.karmaowner.jobs.grades.hasGrade;
import fr.karmaowner.utils.CustomConcurrentHashMap;
import fr.karmaowner.utils.CustomEntry;
import fr.karmaowner.utils.ItemUtils;
import fr.karmaowner.utils.PlayerUtils;
import fr.karmaowner.utils.RecordBuilder;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ess3.api.MaxMoneyException;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerData implements Data, IPlayerData {
    private static CustomConcurrentHashMap<String, PlayerData> playersData = new CustomConcurrentHashMap();

    public static final String dataKey = "data";

    public static final long delayLogin = 60000L;

    public Timestamp lastConnectionDate;

    public String gangName = "";

    public String companyName = "";

    public String companyCategory = "";

    public String InteractingRegion = null;

    public Jobs selectedJob;

    public Timestamp lastJob;

    public boolean isMenotte = false;

    public Player whoMenotted = null;

    public boolean isFouille = false;

    public int cps;

    public boolean isDeath = false;

    public Timestamp CpsTime = new Timestamp(System.currentTimeMillis());

    public Timestamp teleport;

    public HashMap<String, Timestamp> startProhibition = new HashMap<>();

    public HashMap<String, Long> delayProhibition = new HashMap<>();

    public boolean isUsingMedikit = false;

    private String playername = "";

    public ProtectedRegion ActuallyRegion = null;

    private int nbVente = 10;

    private int tempNbVente = 10;

    public Location disconnectLocation;

    public ItemStack helmet;

    public ItemStack chestplate;

    public ItemStack leggings;

    public ItemStack boots;

    public ItemStack gpb;


    public Timestamp salaryTimeStamp;

    private String LastName;

    private String Name;

    public ResultSet results;

    public boolean commandConfirmation = false;

    public boolean passwordReceived = false;

    public String advertMessage = "";

    public Casino c = null;

    public long waitTime;

    public boolean keepJob = false;

    public float shieldAmount;

    public boolean launchedColi;

    public List<ItemStack> predefinedJobItems = new ArrayList<>();



    public String roll = null;

    public PlayerData(String player) throws Exception {
        if (playersData.containsKey(player))
            return;
        Main.Log("Data loading...");
        this.playername = player;
        this.selectedJob = Jobs.Job.getDefaultJob().newInstance(player);

        SqlCollection results = Main.Database.select(RecordBuilder.build().selectAll("players_data")
                .where(new CustomEntry("pseudo", player)).toString());
        if (results.count() == 0) {
            Main.Database.update(RecordBuilder.build().insert(new CustomEntry("pseudo", player), "players_data")
                    .toString());
        } else if (results.count() == 1) {
            this.results = results.getActualResult();
            loadData();
            if (this.companyName != null && !this.companyName.isEmpty())
                if (!CompanyData.Companies.containsKey(this.companyName))
                    CompanyData.getCompanyData(this.companyName);
            if (this.gangName != null && !this.gangName.isEmpty())
                if (GangData.getGang(this.gangName) == null)
                    GangData.GANGS.put(this.gangName, GangData.getGangData(this.gangName));
            playersData.put(player, this);
            this.selectedJob.loadData();
        }
        this.playername = player;
        Main.Log("Data loaded");
    }

    public static boolean comparePlayersIdentity(String[] identity1, String[] identity2) {
        return (identity1[0].equals(identity2[0]) && identity1[1]
                .equals(identity2[1]));
    }

    public static boolean PlayerAlreadyPlayed(String playername) throws SQLException {
        SqlCollection collection = Main.Database.select(RecordBuilder.build().selectAll("players_data", new CustomEntry("pseudo", playername)).toString());
        return (collection.count() >= 1);
    }

    public ProtectedRegion getActuallyRegion() {
        return this.ActuallyRegion;
    }

    public void setActuallyRegion(ProtectedRegion actuallyRegion) {
        this.ActuallyRegion = actuallyRegion;
    }

    public boolean hasGang() {
        return (this.gangName != null && !this.gangName.isEmpty());
    }

    public boolean hasCompany() {
        return (this.companyName != null && !this.companyName.isEmpty() && this.companyCategory != null);
    }

    public boolean getMenotte() {
        return this.isMenotte;
    }

    public void setMenotte(boolean b, Player whoMenotted) {
        this.isMenotte = b;
        this.whoMenotted = whoMenotted;
    }

    public void setUsingMedikit(boolean b) {
        this.isUsingMedikit = b;
    }

    public boolean getUsingMedikit() {
        return this.isUsingMedikit;
    }

    public String getPlayerName() {
        return this.playername;
    }

    public void loadData() {
        if (Bukkit.getPlayerExact(this.playername) != null)
            this.lastConnectionDate = new Timestamp(System.currentTimeMillis());
        try {
            this.companyName = this.results.getString("companyName");
            this.gangName = this.results.getString("gangName");
            this.companyCategory = this.results.getString("companyCategory");
            this.keepJob = this.results.getBoolean("keepJob");
            this.tempNbVente = this.results.getInt("tempNbVente");
            this.nbVente = this.results.getInt("NbVente");
            String coordonnees = this.results.getString("coordonnees");
            if (coordonnees != null) {
                double x = 0.0D, y = 0.0D, z = 0.0D;
                String[] splitted = coordonnees.split(";");
                if (splitted.length > 1) {
                    String xString = splitted[0].split("=")[1];
                    String yString = splitted[1].split("=")[1];
                    String zString = splitted[2].split("=")[1];
                    if (xString.equalsIgnoreCase(".NaN") || yString
                            .equalsIgnoreCase(".NaN") || zString
                            .equalsIgnoreCase(".NaN")) {
                        if (Bukkit.getPlayerExact(this.playername) != null)
                            Bukkit.getPlayerExact(this.playername).sendMessage("§cErreur 1: Vous avez été téléporté au spawn en raison de votre dernier lieu de déconnexion qui est incorrect.");
                        x = -75.0D;
                        y = 63.0D;
                        z = -1528.0D;
                    } else if (Double.parseDouble(splitted[0].split("=")[1]) == 0.0D &&
                            Double.parseDouble(splitted[1].split("=")[1]) == 5.0D &&
                            Double.parseDouble(splitted[2].split("=")[1]) == 0.0D) {
                        if (Bukkit.getPlayerExact(this.playername) != null)
                            Bukkit.getPlayerExact(this.playername).sendMessage("§cErreur 2: Vous avez été téléporté au spawn en raison de votre dernier lieu de déconnexion qui est incorrect.");
                        x = -75.0D;
                        y = 63.0D;
                        z = -1528.0D;
                    } else {
                        x = Double.parseDouble(splitted[0].split("=")[1]);
                        y = Double.parseDouble(splitted[1].split("=")[1]);
                        z = Double.parseDouble(splitted[2].split("=")[1]);
                    }
                }
                this.disconnectLocation = new Location(Main.INSTANCE.getServer().getWorld("cyl"), x, y, z);
            }
            String jobname = this.results.getString("selectedJob");
            this.lastJob = new Timestamp(this.results.getLong("lastJob"));
            String lastrg = this.results.getString("actuallyRegion");
            if (lastrg != null)
                this.ActuallyRegion = Main.WG.getRegionManager(Bukkit.getWorld("cyl")).getRegion(lastrg);
            this.teleport = (this.results.getLong("teleport") == 0L) ? null : new Timestamp(this.results.getLong("teleport"));
            this.waitTime = this.results.getLong("waitTime");
            if (this.results.getString("chestplate") != null)
                this.chestplate = ItemUtils.getItem(
                        Integer.parseInt(this.results.getString("chestplate").split(":")[0]),
                        Byte.parseByte(this.results.getString("chestplate").split(":")[1]), 1, null, null);
            if (this.results.getString("helmet") != null)
                this.helmet = ItemUtils.getItem(
                        Integer.parseInt(this.results.getString("helmet").split(":")[0]),
                        Byte.parseByte(this.results.getString("helmet").split(":")[1]), 1, null, null);
            if (this.results.getString("leggings") != null)
                this.leggings = ItemUtils.getItem(
                        Integer.parseInt(this.results.getString("leggings").split(":")[0]),
                        Byte.parseByte(this.results.getString("leggings").split(":")[1]), 1, null, null);
            if (this.results.getString("boots") != null)
                this.boots = ItemUtils.getItem(
                        Integer.parseInt(this.results.getString("boots").split(":")[0]),
                        Byte.parseByte(this.results.getString("boots").split(":")[1]), 1, null, null);
            if (this.results.getString("gpb") != null)
                this.gpb = ItemUtils.getItem(
                        Integer.parseInt(this.results.getString("gpb").split(":")[0]),
                        Byte.parseByte(this.results.getString("gpb").split(":")[1]), 1, null, null);

            this.LastName = this.results.getString("lastName");
            this.Name = this.results.getString("Name");
            if (jobname != null)
                try {
                    this.selectedJob = Jobs.Job.getJob(jobname, this.playername);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            isDeath = this.results.getBoolean("isDeath");

            SqlCollection ProhibitionResult = Main.Database.select(RecordBuilder.build().selectAll("startProhibition")
                    .where(new CustomEntry("pseudo", getPlayerName())).toString());
            if (ProhibitionResult.count() > 0)
                for (ResultSet prohibition : ProhibitionResult) {
                    this.startProhibition.put(prohibition.getString("jobname"), new Timestamp(prohibition.getLong("timer")));
                    this.delayProhibition.put(prohibition.getString("jobname"), prohibition.getLong("delay"));
                }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    public void saveData() {
        if (Bukkit.getPlayerExact(this.playername) != null)
            this.disconnectLocation = Bukkit.getPlayer(this.playername).getLocation();
        HashMap<String, Object> fields = new HashMap<>();
        if (this.lastConnectionDate != null)
            fields.put("lastConnectionDate", this.lastConnectionDate.getTime());
        fields.put("isDeath", isDeath);
        try {
            Main.Database.update(RecordBuilder.build().delete("startProhibition").where(new CustomEntry("pseudo",
                            getPlayerName()))
                    .toString());
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        for (Map.Entry<String, Timestamp> prohib : this.startProhibition.entrySet()) {
            HashMap<String, Object> fieldsProhib = new HashMap<>();
            fieldsProhib.put("pseudo", getPlayerName());
            fieldsProhib.put("jobname", prohib.getKey());
            fieldsProhib.put("timer", ((Timestamp) prohib.getValue()).getTime());
            fieldsProhib.put("delay", this.delayProhibition.get(prohib.getKey()));
            try {
                Main.Database.update(RecordBuilder.build().insert(fieldsProhib, "startProhibition")
                        .toString());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (this.selectedJob != null && Jobs.Job.getFeatures(this.selectedJob.getClass()) != null)
            fields.put("selectedJob", Jobs.Job.getFeatures(this.selectedJob.getClass()).getName());
        fields.put("companyName", this.companyName);
        fields.put("companyCategory", this.companyCategory);
        fields.put("keepJob", this.keepJob);
        fields.put("gangName", this.gangName);
        fields.put("tempNbVente", this.tempNbVente);
        fields.put("NbVente", this.nbVente);
        if (this.disconnectLocation != null) {
            String coordonnees = "x=" + this.disconnectLocation.getX() + ";y=" + this.disconnectLocation.getY() + ";z=" + this.disconnectLocation.getZ();
            fields.put("coordonnees", coordonnees);
        }
        if (this.lastJob != null)
            fields.put("lastJob", this.lastJob.getTime());
        if (this.teleport != null) {
            fields.put("teleport", this.teleport.getTime());
        } else {
            fields.put("teleport", null);
        }
        fields.put("waitTime", this.waitTime);
        if (this.chestplate != null) {
            fields.put("chestplate", this.chestplate.getTypeId() + ":" + ((this.chestplate.getData() == null) ? 0 : this.chestplate.getData().getData()));
        } else {
            fields.put("chestplate", null);
        }
        if (this.helmet != null) {
            fields.put("helmet", this.helmet.getTypeId() + ":" + ((this.helmet.getData() == null) ? 0 : this.helmet.getData().getData()));
        } else {
            fields.put("helmet", null);
        }
        if (this.leggings != null) {
            fields.put("leggings", this.leggings.getTypeId() + ":" + ((this.leggings.getData() == null) ? 0 : this.leggings.getData().getData()));
        } else {
            fields.put("leggings", null);
        }

        if (this.boots != null) {
            fields.put("boots", this.boots.getTypeId() + ":" + ((this.boots.getData() == null) ? 0 : this.boots.getData().getData()));
        } else {
            fields.put("boots", null);
        }

        if (this.gpb != null) {
            fields.put("gpb", this.gpb.getTypeId() + ":" + ((this.gpb.getData() == null) ? 0 : this.gpb.getData().getData()));
        } else {
            fields.put("gpb", null);
        }

        fields.put("lastName", this.LastName);
        fields.put("Name", this.Name);
        String record = RecordBuilder.build().update(fields, "players_data").where(new CustomEntry("pseudo", this.playername)).toString();
        try {
            Main.Database.update(record);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (this.selectedJob != null)
            this.selectedJob.saveData();

    }

    public boolean isForbidJob(String jobname) {
        for (String prohib : this.startProhibition.keySet()) {
            if (prohib.equalsIgnoreCase(jobname))
                return true;
        }
        return false;
    }

    public ArrayList<String> updateForbidState() {
        ArrayList<String> removed = new ArrayList<>();
        for (Map.Entry<String, Timestamp> prohib : this.startProhibition.entrySet()) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            long delay = (Long) this.delayProhibition.get(prohib.getKey());
            if (now.getTime() - ((Timestamp) prohib.getValue()).getTime() >= delay * 1000L)
                removed.add(prohib.getKey());
        }
        for (String job : removed) {
            try {
                Main.Database.update(RecordBuilder.build().delete("startProhibition")
                        .where(new CustomEntry("pseudo", this.playername))
                        .where(new CustomEntry("jobname", job), RecordBuilder.LINK.AND).toString());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            this.startProhibition.remove(job);
            this.delayProhibition.remove(job);
        }
        return removed;
    }

    public BigDecimal getMoney() {
        User user = Main.essentials.getUser(this.playername);
        if (user != null)
            return user.getMoney();
        if (Main.essentials.getOfflineUser(this.playername) != null)
            return Main.essentials.getOfflineUser(this.playername).getMoney();
        return null;
    }

    public String getGrade() {
        User user = Main.essentials.getUser(this.playername);
        if (user != null)
            return user.getGroup();
        if (Main.essentials.getOfflineUser(this.playername) != null)
            return Main.essentials.getOfflineUser(this.playername).getGroup();
        return null;
    }

    public boolean setMoney(BigDecimal value) {
        User user = Main.essentials.getUser(this.playername);
        if (user != null) {
            try {
                user.setMoney(value);
            } catch (MaxMoneyException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        if (Main.essentials.getOfflineUser(this.playername).getMoney() != null) {
            try {
                Main.essentials.getOfflineUser(this.playername).setMoney(value);
            } catch (MaxMoneyException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    public static PlayerData getPlayerData(String username) {
        if (username == null)
            return null;
        String user = containsUserIgnorecase(username);
        if (user != null)
            return playersData.get(user);
        try {
            return new PlayerData(username);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String containsUserIgnorecase(String username) {
        for (String pseudo : playersData.keySet()) {
            if (pseudo.equalsIgnoreCase(username))
                return pseudo;
        }
        return null;
    }

    public static CustomConcurrentHashMap<String, PlayerData> getHashMap() {
        return playersData;
    }

    public static void SaveDatas() {
        Main.Log("Players Data saving...");
        List<String> usersToRemove = new ArrayList<>();
        for (Map.Entry<String, PlayerData> entry : (Iterable<Map.Entry<String, PlayerData>>) playersData.entrySet()) {
            if (entry.getValue() != null)
                ((PlayerData) entry.getValue()).saveData();
            boolean connected = PlayerUtils.isConnected(entry.getKey());
            if (!connected)
                usersToRemove.add(entry.getKey());
        }
        Main.Log("Players Data saved");
        getHashMap().keySet().removeAll(usersToRemove);
    }

    public int getTempNbVente() {
        return this.tempNbVente;
    }

    public void setTempNbVente(int tempNbVente) {
        this.tempNbVente = tempNbVente;
    }

    public int getNbVente() {
        return this.nbVente;
    }

    public void setNbVente(int nbVente) {
        this.nbVente = nbVente;
    }

    public void setIdentity(String lastName, String Name) {
        this.LastName = lastName;
        this.Name = Name;
    }

    public String[] getIdentity() {
        String[] identity = new String[2];
        identity[0] = this.LastName;
        identity[1] = this.Name;
        return identity;
    }

    public void setShield(float shieldAmount, boolean syncToForge) {
        this.shieldAmount = shieldAmount;
        if (syncToForge)
            Bukkit.getServer().dispatchCommand((CommandSender) Bukkit.getConsoleSender(), "api setshield " + getPlayerName() + " " + shieldAmount);
    }

    public boolean estMenotte() {
        return this.isMenotte;
    }

    public String getCompanyCategorie() {
        return this.companyCategory;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public Location getDisconnectLocation() {
        return this.disconnectLocation;
    }

    public String getGangName() {
        return this.gangName;
    }

    public String getInteractingRegion() {
        return this.InteractingRegion;
    }

    public IRpPlayer getRpPlayer() {
        return new IRpPlayer() {
            public String getLastName() {
                return PlayerData.this.LastName;
            }

            public String getFirstName() {
                return PlayerData.this.Name;
            }
        };
    }

    public IJob getSelectedJob() {
        return new IJob() {
            public String jobName() {
                return PlayerData.this.selectedJob.getFeatures().getName();
            }

            public boolean isVipJob() {
                for(String permission : PlayerData.this.selectedJob.getFeatures().getPermissions())
                {
                    if(permission.contains("vip")) return true;
                }
                return false;
            }

            public boolean isIllegalJob() {
                return PlayerData.this.selectedJob.getFeatures().isIllegal();
            }

            public IGrades getGrades() {
                if (PlayerData.this.selectedJob instanceof hasGrade)
                    return (IGrades) ((hasGrade) PlayerData.this.selectedJob).getGrade().getGrades();
                return null;
            }

            public boolean hasGrade() {
                return PlayerData.this.selectedJob instanceof hasGrade;
            }

            public boolean isFDL() {
                return PlayerData.this.selectedJob instanceof fr.karmaowner.jobs.Security;
            }

            public void sendMessageAll(String arg0) {
                PlayerData.this.selectedJob.getFeatures().sendMessageAll(arg0);
            }

            public void sendMessageAll(String arg0, String arg1) {
                PlayerData.this.selectedJob.getFeatures().sendMessageAll(arg0, arg1);
            }

            @Override
            public void sendMessageTextComponentAll(TextComponent textComponent) {
                PlayerData.this.selectedJob.getFeatures().sendMessageAll(textComponent);
            }

            @Override
            public void sendMessageAllByClothes(String arg0, String arg1) {
                PlayerData.this.selectedJob.getFeatures().sendMessageAllByClothes(arg0, arg1);
            }

            @Override
            public void sendMessageSecurityJobs(String arg0, String arg1) {
                Jobs.Job.sendMessageSecurityJobs(arg0, arg1);
            }

            @Override
            public void sendMessageTextComponentSecurityJobs(TextComponent textComponent) {
                Jobs.Job.sendMessageSecurityJobs(textComponent);
            }

            @Override
            public IGrade getPlayerGrade() {
                if (PlayerData.this.selectedJob instanceof hasGrade) {
                    hasGrade grades = (fr.karmaowner.jobs.grades.hasGrade) PlayerData.this.selectedJob;
                    return grades.getGrade().getGrade();
                }
                return null;
            }
        };
    }

    @Override
    public boolean estEnReanimation() {
        return false;
    }

    public boolean isfouille() {
        return this.isFouille;
    }

    public Player getWhoMenotted() {
        return this.whoMenotted;
    }

    public String getIp()
    {
        Player player = Bukkit.getPlayerExact(playername);
        if(player != null)
        {
            return player.getAddress().getAddress().toString();
        }
        return "";
    }
}
