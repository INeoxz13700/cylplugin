package fr.karmaowner.jobs;

import fr.karmaowner.common.Main;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.Data;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.data.SqlCollection;
import fr.karmaowner.events.JobsEvents;
import fr.karmaowner.jobs.grades.hasGrade;
import fr.karmaowner.jobs.interact.Interact;
import fr.karmaowner.utils.*;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class Jobs implements Data {
    public static Inventory POLEEMPLOIINVENTORY = Main.INSTANCE
            .getServer().createInventory(null, 27, ChatColor.RED + "Métiers");

    public static Inventory ILLEGALJOBSINVENTORY = Main.INSTANCE
            .getServer().createInventory(null, 27, ChatColor.RED + "Métiers");

    public static final String NAMEACTIONINVENTORY = ChatColor.DARK_AQUA + "Actions";

    public static final String POLEEMPLOIVILLAGERNAME = "Pôle Emploi";

    public static final String ILLEGALJOBSVILLAGERNAME = "Métiers illégaux";

    public static final String LICENCEPORTARMETEXTE = "§fPermis de port d'arme";

    private ArrayList<Interact> ActionInventoryContent = new ArrayList<>();

    public Inventory actionJobInventory;

    private Player target;

    private Entity entity;

    private TaskCreator task;

    private boolean outOfService;

    private Block block;

    private String EquipedClothes;

    private String lastSavedClothes;

    private String player;

    public enum TypeInteract {
        ENTITY, BLOCK, CLICK_AIR, UNKNOWN;
    }

    public enum Job {
        POLICIER("policier", "plc", Policier.class, Arrays.asList("cylrp.rank.default"), true, true),
        CIVILE("civile", "cvl", Civile.class, true, Arrays.asList("cylrp.rank.default"), true, false),
        CUISINIER("cuisinier", "csn", Cuisinier.class, Arrays.asList("cylrp.rank.default"), true, true),
        HACKER("hacker", "hkr", Hacker.class, Arrays.asList("cylrp.rank.default"), true, true, false),
        MEDECIN("medecin", "mdn", Medecin.class, Arrays.asList("cylrp.rank.default"), true, true),
        CHIMISTE("chimiste", "che", Chimiste.class, Arrays.asList("cylrp.rank.vip"), true, true),
        POMPIER("pompier", "ppr", Pompier.class, Arrays.asList("cylrp.rank.default"), true, true),
        TAXI("taxi", "tax", Taxi.class, Arrays.asList("cylrp.rank.default"), true, true),
        VOLEUR("voleur", "vol", Voleur.class, Arrays.asList("cylrp.rank.default"), true, true, false),
        REBELLE("rebelle", "rebl", Rebelle.class, Arrays.asList("cylrp.rank.default"), true, true, false),
        TERRORISTE("terroriste", "terro", Terroriste.class, Arrays.asList("cylrp.rank.vipplus"), true, true, false),
        MILITAIRE("militaire", "mlt", Militaire.class, Arrays.asList("cylrp.rank.vipplus"), true, true),
        ARMURIER("armurier", "arm", Armurier.class, Arrays.asList("cylrp.rank.default"), true, true),
        DOUANIER("douanier", "dnr", Douanier.class, Arrays.asList("cylrp.rank.default"), true, true),
        GIGN("gign", "ggn", Gign.class, Arrays.asList("cylrp.rank.vip"), true, true),
        MAIRE("maire", "mr", Maire.class, Arrays.asList("cylrp.rank.default"), false, true),
        MAIREADJOINT("maireadjoint", "mradj", MaireAdjoint.class, Arrays.asList("cylrp.rank.default"), false, true),
        CHEFGARDE("chefgarde", "mradj", ChefGarde.class, Arrays.asList("cylrp.rank.default"), false, true),
        GARDE("garde", "gar", Garde.class, Arrays.asList("cylrp.rank.default"), false, true),
        GENDARME("gendarme", "gdme", Gendarme.class, Arrays.asList("cylrp.rank.default"), true, true),
        PSYCHOPATHE("psychopathe", "psy", Psychopathe.class, Arrays.asList("cylrp.rank.vipplus"), true, true, false),
        BAC("bac", "bac", BAC.class, Arrays.asList("cylrp.rank.vipplus","cylrp.job.bac"), true, true),
        ASSEMBLEE("assemblee", "assemblee", AssembleeNationale.class, Arrays.asList("cylrp.rank.default"), false, true);

        private List<Clothes> Clothes = new ArrayList<>();

        private String name;

        private String shortcut;

        private Class<?> classe;

        private boolean isDefault;

        private Inventory ClothesInventory;

        private boolean InPnj;

        private boolean isIllegal;

        private List<String> permissions = new ArrayList<String>();

        private boolean hasServiceFunction;

        Job(String name, String shortcut, Class<?> classe, List<String> permissions, boolean in, boolean hasServiceFunction) {
            this.name = name;
            this.shortcut = shortcut;
            this.classe = classe;
            this.isDefault = false;
            this.hasServiceFunction = hasServiceFunction;
            this.ClothesInventory = Bukkit.createInventory(null, 54, "§6Tenues " + name);
            this.InPnj = in;
            this.permissions = permissions;
        }

        Job(String name, String shortcut, Class<?> classe, List<String> permissions, boolean in, boolean illegal, boolean hasServiceFunction) {
            this(name, shortcut, classe, permissions, in, hasServiceFunction);
            this.isIllegal = illegal;
        }

        Job(String name, String shortcut, Class<?> classe, boolean isDefault, List<String> permissions, boolean in, boolean hasServiceFunction) {
            this(name, shortcut, classe, permissions, in, hasServiceFunction);
            this.isDefault = isDefault;
        }

        Job(String name, String shortcut, Class<?> classe, boolean isDefault, List<String> permissions, boolean in, boolean illegal, boolean hasServiceFunction) {
            this(name, shortcut, classe, isDefault, permissions, in, hasServiceFunction);
            this.isIllegal = illegal;
        }

        public Class<?> getClasse() {
            return this.classe;
        }

        public boolean hasServiceFunction() {
            return this.hasServiceFunction;
        }

        public String getShortcut() {
            return this.shortcut;
        }

        public boolean isInInPnj() {
            return this.InPnj;
        }

        public boolean isJob(Job j) {
            return getName().equalsIgnoreCase(j.getName());
        }

        public boolean hasPermissions() {
            if (!this.permissions.isEmpty())
                return true;
            return false;
        }

        public List<String> getPermissions() {
            return this.permissions;
        }

        public boolean playerHasJobPermission(Player player)
        {
            for(String permission : getPermissions())
            {
                if(player.hasPermission(permission)) return true;
            }
            return false;
        }

        public String getDisplayName() {
            return Main.INSTANCE.getConfig().getString("jobs." + this.name + ".display-name");
        }

        public String getName() {
            return this.name;
        }

        public String getPrefix() {
            return "[" + getDisplayName() + "] ";
        }

        public Clothes getClothes(String id) {
            for (Clothes c : this.Clothes) {
                if (c.getId() != null && c.getId().equals(id))
                    return c;
            }
            return null;
        }

        public List<Clothes> getClothes() {
            return this.Clothes;
        }

        public void setClothes(Clothes c) {
            this.Clothes.add(c);
        }


        public void loadJobClothes() {
            FileConfiguration f = Main.INSTANCE.getConfig();
            String name = "jobs." + getName() + ".clothes";
            if (f.getConfigurationSection(name) != null) {
                int i = 0;
                for (String key : f.getConfigurationSection(name).getKeys(false)) {

                    ItemStack items;
                    String id = key;
                    int helmet = f.getInt(name + "." + id + ".Helmets");
                    int item = f.getInt(name + "." + id + ".item");
                    if (item == 0) item = 339;
                    int chestplate = f.getInt(name + "." + id + ".Chestplates");
                    int legging = f.getInt(name + "." + id + ".leggings");
                    int boot = f.getInt(name + "." + id + ".boots");
                    int gpb = f.getInt(name + "." + id + ".gpb");
                    boolean isDefault = f.getBoolean(name + "." + id + ".default");
                    boolean equipGrade = f.get(name + "." + id + ".equipGrade") == null || f.getBoolean(name + "." + id + ".equipGrade");
                    if (f.getList(name + "." + id + ".grades") != null) {
                        List<String> grades = f.getStringList(name + "." + id + ".grades");
                        setClothes(new Clothes(id, helmet, chestplate, legging, boot, gpb, isDefault, equipGrade, grades));
                        items = ItemUtils.getItem(item, (byte) 0, 1, id, Collections.singletonList("§4Grades: §c" + StringUtils.join(grades, ',')));
                    } else {
                        setClothes(new Clothes(id, helmet, chestplate, legging, boot, gpb, isDefault, equipGrade));
                        items = ItemUtils.getItem(item, (byte) 0, 1, id, Collections.singletonList("§4Grades: §cAucun"));
                    }
                    this.ClothesInventory.setItem(i, items);
                    i++;
                }
            }
        }

        public void openClothesInventory(String playername) {
            Player p = Bukkit.getPlayerExact(playername);
            if (p != null)
                p.openInventory(this.ClothesInventory);
        }

        public void sendMessageAll(String msg) {
            for (Player p : Main.INSTANCE.getServer().getOnlinePlayers()) {
                PlayerData data = PlayerData.getPlayerData(p.getName());
                if (data != null && data.selectedJob.getClass() == getClasse())
                    p.sendMessage(getPrefix() + " " + msg);
            }
        }

        public void sendMessageAllByClothes(String msg, String clothesId) {
            for (Player p : Main.INSTANCE.getServer().getOnlinePlayers()) {
                PlayerData data = PlayerData.getPlayerData(p.getName());
                if (data != null && data.selectedJob.getClass() == getClasse() &&
                        !data.selectedJob.isOutOfService() && data.selectedJob.getEquipedClothes() != null && data.selectedJob
                        .getEquipedClothes().contains(clothesId))
                    p.sendMessage(getPrefix() + " " + msg);
            }
        }

        public void sendMessageAll(String prefix, String msg) {
            for (Player p : Main.INSTANCE.getServer().getOnlinePlayers()) {
                PlayerData data = PlayerData.getPlayerData(p.getName());
                if (data != null && data.selectedJob.getClass() == getClasse()) {
                    String m = prefix + " " + msg;
                    m = m.replaceAll("%jobname%", getName());
                    m = m.replaceAll("%jobprefix%", getPrefix());
                    p.sendMessage(m);
                }
            }
        }

        public void sendMessageAll(TextComponent msg) {
            for (Player p : Main.INSTANCE.getServer().getOnlinePlayers()) {
                PlayerData data = PlayerData.getPlayerData(p.getName());
                if (data != null && data.selectedJob.getClass() == getClasse()) {
                    p.spigot().sendMessage(msg);
                }
            }
        }

        public static void sendMessageSecurityJobs(String prefix, String msg) {
            for (Player p : Main.INSTANCE.getServer().getOnlinePlayers()) {
                PlayerData data = PlayerData.getPlayerData(p.getName());
                if (data.selectedJob instanceof Security) {
                    String m = prefix + " " + msg;
                    p.sendMessage(m);
                }
            }
        }

        public static void sendMessageSecurityJobs(TextComponent msg) {
            for (Player p : Main.INSTANCE.getServer().getOnlinePlayers()) {
                PlayerData data = PlayerData.getPlayerData(p.getName());
                if (data.selectedJob instanceof Security) {
                    p.spigot().sendMessage(msg);
                }
            }
        }

        public static void loadDatas() {
            for (Job j : Job.values()) {
                Method method = null;
                try {
                    method = j.getClasse().getMethod("loadJobData");
                    method.invoke(null);
                } catch (Exception exception) {
                }
            }
        }

        public static void saveDatas() {
            for (Job j : Job.values()) {
                Method method = null;
                try {
                    method = j.getClasse().getMethod("saveJobData");
                    method.invoke(null);
                } catch (Exception exception) {
                }
            }
        }

        public static Job getJobByName(String name) {
            for (Job j : Job.values()) {
                if (j.getName().equals(name))
                    return j;
                if (j.getDisplayName().equals(name))
                    return j;
            }
            return null;
        }

        public static Job[] getJobs(List<String> jobs) {
            Job[] tab = new Job[jobs.size()];
            int i = 0;
            for (String job : jobs) {
                tab[i] = getJobByName(job);
                i++;
            }
            return tab;
        }

        public Jobs newInstance(String player) throws Exception {
            return (Jobs) getClasse().getConstructor(new Class[]{String.class}).newInstance(player);
        }

        public static boolean isJob(String displayNameorName) {
            for (Job j : Job.values()) {
                if (j.getDisplayName().equals(displayNameorName))
                    return true;
                if (j.getName().equalsIgnoreCase(displayNameorName))
                    return true;
            }
            return false;
        }

        public static boolean regionNameContainsJob(String name) {
            for (Job j : Job.values()) {
                if (name.toLowerCase().equals(j.getName().toLowerCase()))
                    return true;
                if (name.toLowerCase().equals(j.getDisplayName().toLowerCase()))
                    return true;
            }
            return false;
        }

        public static Jobs getJob(String displayNameorName, String player) throws Exception {
            for (Job j : Job.values()) {
                if (j.getDisplayName().equals(displayNameorName))
                    return j.newInstance(player);
                if (j.getName().equalsIgnoreCase(displayNameorName))
                    return j.newInstance(player);
            }
            return null;
        }

        public boolean getDefault() {
            return this.isDefault;
        }

        public static Job getFeatures(Class<?> classe) {
            for (Job j : Job.values()) {
                if (j.getClasse() == classe)
                    return j;
            }
            return null;
        }

        public static Job getDefaultJob() {
            for (Job j : Job.values()) {
                if (j.getDefault())
                    return j;
            }
            return null;
        }

        public ArrayList<Player> onlinePlayers() {
            ArrayList<Player> players = new ArrayList<>();
            for (Player p : Main.INSTANCE.getServer().getOnlinePlayers()) {
                PlayerData data = PlayerData.getPlayerData(p.getName());
                if (data.selectedJob.getClass() == getClasse())
                    players.add(p);
            }
            return players;
        }

        public static ArrayList<Player> onlinePlayers(Job... jobs) {
            ArrayList<Player> players = new ArrayList<>();
            for (Player p : Main.INSTANCE.getServer().getOnlinePlayers()) {
                PlayerData data = PlayerData.getPlayerData(p.getName());
                for (Job j : jobs) {
                    if (data.selectedJob.getClass() == j.getClasse()) {
                        players.add(p);
                        break;
                    }
                }
            }
            return players;
        }

        public static ArrayList<Player> onlineServicePlayers(Job... jobs) {
            ArrayList<Player> players = new ArrayList<>();
            for (Player p : onlinePlayers(jobs)) {
                PlayerData data = PlayerData.getPlayerData(p.getName());
                if (!data.selectedJob.isOutOfService())
                    players.add(p);
            }
            return players;
        }

        public ArrayList<Player> onlineServicePlayers() {
            ArrayList<Player> players = new ArrayList<>();
            for (Player p : onlinePlayers()) {
                PlayerData data = PlayerData.getPlayerData(p.getName());
                if (!data.selectedJob.isOutOfService())
                    players.add(p);
            }
            return players;
        }

        public ArrayList<Player> onlineServicePlayersByClothes(String clothesId) {
            ArrayList<Player> players = new ArrayList<>();
            for (Player p : onlinePlayers()) {
                PlayerData data = PlayerData.getPlayerData(p.getName());
                if (!data.selectedJob.isOutOfService() && data.selectedJob.getEquipedClothes().contains(clothesId))
                    players.add(p);
            }
            return players;
        }

        public Inventory getClothesInventory() {
            return this.ClothesInventory;
        }

        public boolean isIllegal() {
            return this.isIllegal;
        }
    }

    public Jobs(String name) {
        this.target = null;
        this.task = null;
        this.outOfService = false;
        this.player = name;
    }

    public void loadData() {
        PlayerData pData = PlayerData.getPlayerData(this.player);
        String record = RecordBuilder.build().selectAll("players_data").where(new CustomEntry("pseudo", this.player)).toString();
        try {
            SqlCollection results = Main.Database.select(record);
            if (results.count() == 1) {
                ResultSet data = results.getActualResult();
                try {
                    setOutOfService(data.getBoolean("outofservice"));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        if (!pData.selectedJob.getFeatures().isIllegal() && !(pData.selectedJob instanceof Civile))
            try {
                SqlCollection results = Main.Database.select(RecordBuilder.build().selectAll("jobs_data")
                        .where(new CustomEntry("pseudo", this.player))
                        .where(new CustomEntry("jobname", pData.selectedJob.getFeatures().getName().toLowerCase()), RecordBuilder.LINK.AND)
                        .toString());
                if (results.count() == 0) {
                    equipClothes();
                    String equippedClothes = pData.selectedJob.getEquipedClothes();
                    HashMap<String, Object> fields = new HashMap<>();
                    fields.put("pseudo", this.player);
                    fields.put("jobname", pData.selectedJob.getFeatures().getName().toLowerCase());
                    fields.put("clothes", equippedClothes);
                    Main.Database.update(RecordBuilder.build().insert(fields, "jobs_data")
                            .toString());
                    results = null;
                } else if (results.count() == 1) {
                    ResultSet res = results.getActualResult();
                    if (res != null) {
                        String clothes = res.getString("clothes");
                        if (!pData.selectedJob.outOfService) {
                            equipClothes(clothes);
                        } else {
                            this.lastSavedClothes = clothes;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public void saveData() {
        PlayerData pData = PlayerData.getPlayerData(this.player);
        String record = RecordBuilder.build().update(new CustomEntry("outofservice", this.outOfService), "players_data").where(new CustomEntry("pseudo", this.player)).toString();
        try {
            Main.Database.update(record);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (!pData.selectedJob.getFeatures().isIllegal() && !(pData.selectedJob instanceof Civile)) {
            Clothes c = pData.selectedJob.getFeatures().getClothes(this.lastSavedClothes);
            if (c != null) {
                String record2 = RecordBuilder.build().update(new CustomEntry("clothes", c.getId()), "jobs_data").where(new CustomEntry("pseudo", this.player)).where(new CustomEntry("jobname", pData.selectedJob.getFeatures().getName().toLowerCase()), RecordBuilder.LINK.AND).toString();
                try {
                    Main.Database.update(record2);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getPlayer() {
        return this.player;
    }

    public Player GetConnectedPlayer() {
        return Bukkit.getPlayerExact(this.player);
    }

    public void unequipClothes(PlayerData data, boolean clearPredefinedInv, boolean keepCache) {
        if (GetConnectedPlayer() != null) {
            GetConnectedPlayer().getInventory().setHelmet(new ItemStack(Material.AIR));
            GetConnectedPlayer().getInventory().setBoots(new ItemStack(Material.AIR));
            GetConnectedPlayer().getInventory().setChestplate(new ItemStack(Material.AIR));
            GetConnectedPlayer().getInventory().setLeggings(new ItemStack(Material.AIR));

            InventoryUtils.setGpb(GetConnectedPlayer(), new ItemStack(Material.AIR),false);
            InventoryUtils.setGalon(GetConnectedPlayer(), new ItemStack(Material.AIR),false);

            setEquipedClothes(null);

            if(clearPredefinedInv)
            {
                for(int i = 0; i < GetConnectedPlayer().getInventory().getStorageContents().length; i++)
                {
                    ItemStack contentStack = GetConnectedPlayer().getInventory().getStorageContents()[i];
                    for(ItemStack is : data.predefinedJobItems)
                    {
                        if(contentStack == null) break;

                        if(is.getTypeId() == contentStack.getTypeId() && is.getAmount() == contentStack.getAmount())
                        {
                            GetConnectedPlayer().getInventory().remove(GetConnectedPlayer().getInventory().getStorageContents()[i]);
                        }
                    }
                }

                if(!keepCache) data.predefinedJobItems.clear();
            }

        }
    }

    public List<Clothes> getDefaultClothes() {
        PlayerData data = PlayerData.getPlayerData(this.player);
        if (data != null) {
            List<Clothes> defaultClothes = new ArrayList<>();
            for (Clothes c : (data.selectedJob.getFeatures()).Clothes) {
                if (c.isDefaultClothes())
                    defaultClothes.add(c);
            }
            return defaultClothes;
        }
        return null;
    }

    public Clothes getDefaultClotheWithoutGrade() {
        PlayerData data = PlayerData.getPlayerData(this.player);
        if (data != null)
            for (Clothes c : getDefaultClothes()) {
                if (!c.hasGrade())
                    return c;
            }
        return null;
    }

    public Clothes getDefaultClotheByGrade(String grade) {
        PlayerData data = PlayerData.getPlayerData(this.player);
        if (data != null)
            for (Clothes c : getDefaultClothes()) {
                if (c.hasGrade(grade))
                    return c;
            }
        return null;
    }

    public boolean isGradeEquipable(String clothes) {
        PlayerData data = PlayerData.getPlayerData(this.player);
        if (data != null && clothes != null)
            for (Clothes c : (data.selectedJob.getFeatures()).Clothes) {
                if (c.getId().equals(clothes) && c.isEquipGrade())
                    return true;
            }
        return false;
    }

    public Clothes getDefaultClotheForPlayer() {
        PlayerData data = PlayerData.getPlayerData(this.player);
        Clothes defaultClothe = getDefaultClotheWithoutGrade();
        if (data.selectedJob instanceof hasGrade) {
            hasGrade grade = (hasGrade) data.selectedJob;
            String gradename = grade.getGrade().getGrade().getNom();
            Clothes defaultClotheGrade = getDefaultClotheByGrade(gradename);
            if (defaultClotheGrade != null)
                return defaultClotheGrade;
        }
        return defaultClothe;
    }

    public ArrayList<Interact> getActionInventoryContent() {
        return this.ActionInventoryContent;
    }

    public Job getFeatures() {
        return Job.getFeatures(getClass());
    }

    public void setInActionInventory(Interact interact) {
        this.ActionInventoryContent.add(interact);
        this.ActionInventoryContent.sort(new Comparator<Interact>() {
            public int compare(Interact elt1, Interact elt2) {
                if (elt1.getPriority() > elt2.getPriority())
                    return -1;
                if (elt1.getPriority() < elt2.getPriority())
                    return 1;
                return 0;
            }
        });
    }

    public void removeInActionInventory(int index) {
        this.ActionInventoryContent.remove(index);
    }

    public void setBlockClicked(Block b) {
        this.block = b;
    }

    public Block getBlockClicked() {
        return this.block;
    }

    public boolean isEquippedClothes(String id) {
        return (getEquipedClothes() != null && getEquipedClothes().equals(id));
    }

    public void equipClothes() {
        PlayerData pData = PlayerData.getPlayerData(this.player);
        Clothes c = getDefaultClotheForPlayer();
        if (c != null && GetConnectedPlayer() != null && !isEquippedClothes(c.getId()))
            if (this.lastSavedClothes != null) {
                c = pData.selectedJob.getFeatures().getClothes(this.lastSavedClothes);
                GetConnectedPlayer().getEquipment().setHelmet(ItemUtils.getItem(c.getHelmets(), (byte) 0, 1, null, null));
                GetConnectedPlayer().getEquipment().setChestplate(ItemUtils.getItem(c.getChestplates(), (byte) 0, 1, null, null));
                GetConnectedPlayer().getEquipment().setLeggings(ItemUtils.getItem(c.getLeggings(), (byte) 0, 1, null, null));
                GetConnectedPlayer().getEquipment().setBoots(ItemUtils.getItem(c.getBoots(), (byte) 0, 1, null, null));
                InventoryUtils.setGpb( GetConnectedPlayer(), ItemUtils.getItem(c.getGpb(), (byte) 0, 1, null, null),true);
                setEquipedClothes(this.lastSavedClothes);
            } else {
                this.lastSavedClothes = c.getId();
                GetConnectedPlayer().getEquipment().setHelmet(ItemUtils.getItem(c.getHelmets(), (byte) 0, 1, null, null));
                GetConnectedPlayer().getEquipment().setChestplate(ItemUtils.getItem(c.getChestplates(), (byte) 0, 1, null, null));
                GetConnectedPlayer().getEquipment().setLeggings(ItemUtils.getItem(c.getLeggings(), (byte) 0, 1, null, null));
                GetConnectedPlayer().getEquipment().setBoots(ItemUtils.getItem(c.getBoots(), (byte) 0, 1, null, null));
                InventoryUtils.setGpb(GetConnectedPlayer(), ItemUtils.getItem(c.getGpb(), (byte) 0, 1, null, null),true);



                setEquipedClothes(c.getId());
            }
        if (GetConnectedPlayer() != null && GetConnectedPlayer().getEquipment() != null && pData.selectedJob instanceof hasGrade && c != null) {
            hasGrade grade = (hasGrade) pData.selectedJob;
            grade.getGrade().equipGrade();
            grade.getGrade().equipInventory();
        }
    }

    public void equipClothes(String id) {
        PlayerData pData = PlayerData.getPlayerData(this.player);
        Clothes c = getFeatures().getClothes(id);
        if (c != null && GetConnectedPlayer() != null && !isEquippedClothes(id)) {
            GetConnectedPlayer().getEquipment().setHelmet(ItemUtils.getItem(c.getHelmets(), (byte) 0, 1, null, null));
            GetConnectedPlayer().getEquipment().setChestplate(ItemUtils.getItem(c.getChestplates(), (byte) 0, 1, null, null));
            GetConnectedPlayer().getEquipment().setLeggings(ItemUtils.getItem(c.getLeggings(), (byte) 0, 1, null, null));
            GetConnectedPlayer().getEquipment().setBoots(ItemUtils.getItem(c.getBoots(), (byte) 0, 1, null, null));
            InventoryUtils.setGpb(GetConnectedPlayer(), ItemUtils.getItem(c.getGpb(), (byte) 0, 1, null, null), true);


            setEquipedClothes(c.getId());
            this.lastSavedClothes = c.getId();
        }

        if (GetConnectedPlayer() != null && GetConnectedPlayer().getEquipment() != null && pData.selectedJob instanceof hasGrade && c != null) {
            hasGrade grade = (hasGrade) pData.selectedJob;
            grade.getGrade().equipGrade();
            grade.getGrade().equipInventory();
        }
    }

    public void equipClothes(Clothes c) {
        PlayerData pData = PlayerData.getPlayerData(this.player);
        if (c != null && GetConnectedPlayer() != null && !isEquippedClothes(c.getId())) {
            GetConnectedPlayer().getEquipment().setHelmet(ItemUtils.getItem(c.getHelmets(), (byte) 0, 1, null, null));
            GetConnectedPlayer().getEquipment().setChestplate(ItemUtils.getItem(c.getChestplates(), (byte) 0, 1, null, null));
            GetConnectedPlayer().getEquipment().setLeggings(ItemUtils.getItem(c.getLeggings(), (byte) 0, 1, null, null));
            GetConnectedPlayer().getEquipment().setBoots(ItemUtils.getItem(c.getBoots(), (byte) 0, 1, null, null));

            InventoryUtils.setGpb(GetConnectedPlayer(),ItemUtils.getItem(c.getGpb(), (byte) 0, 1, null, null), true);
            setEquipedClothes(c.getId());

            this.lastSavedClothes = c.getId();
        }
        if (GetConnectedPlayer() != null && GetConnectedPlayer().getEquipment() != null && pData.selectedJob instanceof hasGrade && c != null) {
            hasGrade grade = (hasGrade) pData.selectedJob;
            grade.getGrade().equipGrade();
            grade.getGrade().equipInventory();
        }
    }

    public static boolean isJobClothesItem(ItemStack item) {
        if (item == null)
            return false;
        for (Job j : Job.values()) {
            for (Clothes c : j.getClothes()) {
                if (c.isClothes(item.getTypeId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void fillActionJobInventory(TypeInteract type) {
        this.actionJobInventory.clear();
        for (Interact i : this.ActionInventoryContent) {
            if (i.getTypeInteract() == type)
                for (ItemStack item : i.getItems()) {
                    this.actionJobInventory.addItem(item);
                }
        }
    }

    public void fillActionJobInventory(Interact i) {
        this.actionJobInventory.clear();
        for (ItemStack item : i.getItems()) {
            this.actionJobInventory.addItem(item);
        }
    }

    public Interact getInteractionForBlock(Block block) {
        for (Interact i : this.ActionInventoryContent) {
            if (i.getTypeInteract() == TypeInteract.BLOCK && i.isInteractableBlock(block))
                return i;
        }
        return null;
    }

    public ArrayList<Interact> getInteraction(TypeInteract i) {
        ArrayList<Interact> interacts = new ArrayList<>();
        for (Interact interact : this.ActionInventoryContent) {
            if (interact.getTypeInteract() == i)
                interacts.add(interact);
        }
        return interacts;
    }

    public void setTask(TaskCreator t) {
        this.task = t;
    }

    public TaskCreator getTask() {
        return this.task;
    }

    public Inventory getActionJobInventory() {
        return this.actionJobInventory;
    }

    public boolean isLegalJob() {
        return this instanceof Legal;
    }

    public static void fillInventory() {
        POLEEMPLOIINVENTORY.clear();
        ILLEGALJOBSINVENTORY.clear();
        FileConfiguration f = Main.INSTANCE.getConfig();
        ConfigurationSection s = f.getConfigurationSection("jobs");
        for (String key : s.getKeys(false)) {
            int id = f.getInt("jobs." + key + ".item-id");
            if(id == 0)
            {
                id = 339;
            }
            String name = f.getString("jobs." + key + ".display-name");
            List<String> desc = f.getStringList("jobs." + key + ".description");
            ItemStack item = new ItemStack(id);

            if(item.getType() == null)
            {
                continue;
            }

            if(item.getData() == null)
            {
                continue;
            }

            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                meta.setDisplayName(name);
                meta.setLore(desc);
                item.setItemMeta(meta);
            }
            else
            {
                continue;
            }

            Job j = Job.getJobByName(key);
            if (j != null && j.isInInPnj()) {
                if (!j.isIllegal()) {
                    POLEEMPLOIINVENTORY.addItem(item);
                    continue;
                }
                ILLEGALJOBSINVENTORY.addItem(item);
            }
        }
    }

    public Player getTarget() {
        return this.target;
    }

    public Entity getEntityTarget() {
        return this.entity;
    }

    public void setEntityTarget(Entity entity) {
        this.entity = entity;
    }

    public void setTarget(Player target2) {
        this.target = target2;
    }

    public void ban(long seconds) {
        PlayerData data = PlayerData.getPlayerData(this.player);
        Job j = data.selectedJob.getFeatures();
        data.startProhibition.put(j.getName(), new Timestamp(System.currentTimeMillis()));
        data.delayProhibition.put(j.getName(), seconds);
        JobsEvents.changePlayerJob(data, Job.CIVILE.getName(), this.player);
        if (Bukkit.getPlayerExact(this.player) != null)
            Bukkit.getPlayerExact(this.player).sendMessage(ChatColor.RED + "Vous venez de perdre temporairement votre métier pendant " + ChatColor.DARK_RED + TimerUtils.formatString(seconds));
    }

    public static void ban(String player, String job, long seconds) {
        PlayerData data = PlayerData.getPlayerData(player);
        data.startProhibition.put(job, new Timestamp(System.currentTimeMillis()));
        data.delayProhibition.put(job, seconds);
        if(data.selectedJob.getFeatures().getName().equalsIgnoreCase(job)) {
            JobsEvents.changePlayerJob(data, Job.CIVILE.getName(), player);
        }
        if (Bukkit.getPlayerExact(player) != null)
            Bukkit.getPlayerExact(player).sendMessage(ChatColor.RED + "Vous venez de perdre temporairement votre métier pendant " + ChatColor.DARK_RED + TimerUtils.formatString(seconds));
    }

    public void unban() {
        PlayerData data = PlayerData.getPlayerData(this.player);
        Job j = data.selectedJob.getFeatures();
        data.startProhibition.remove(j.getName());
        data.delayProhibition.remove(j.getName());
    }

    public static void unban(String player, String job) {
        PlayerData data = PlayerData.getPlayerData(player);
        data.startProhibition.remove(job);
        data.delayProhibition.remove(job);
    }

    public boolean isOutOfService() {
        return this.outOfService;
    }

    public void setOutOfService(boolean outOfService) {
        PlayerData data = PlayerData.getPlayerData(this.player);
        this.outOfService = outOfService;
    }

    public String getEquipedClothes() {
        return this.EquipedClothes;
    }

    public void setEquipedClothes(String equipedClothes) {
        this.EquipedClothes = equipedClothes;
    }
}
