package fr.karmaowner.commands;

import com.sk89q.worldedit.BlockVector;
import fr.karmaowner.amende.Amende;
import fr.karmaowner.chat.Chat;
import fr.karmaowner.chat.ChatGroup;
import fr.karmaowner.common.CustomRunnable;
import fr.karmaowner.common.Main;
import fr.karmaowner.common.Request;
import fr.karmaowner.common.SaveScheduler;
import fr.karmaowner.common.TaskCreator;
import fr.karmaowner.data.CompanyData;
import fr.karmaowner.data.GangData;
import fr.karmaowner.data.PlayerData;
import fr.karmaowner.events.JobsEvents;
import fr.karmaowner.jobs.*;
import fr.karmaowner.jobs.chauffeur.Regions;
import fr.karmaowner.jobs.grades.Grade;
import fr.karmaowner.jobs.grades.Grades;
import fr.karmaowner.jobs.grades.JobGrades;
import fr.karmaowner.jobs.grades.hasGrade;
import fr.karmaowner.jobs.missions.Missions;
import fr.karmaowner.jobs.missions.type.GeneralType;
import fr.karmaowner.utils.*;
import fr.karmaowner.wantedlist.WantedList;

import java.awt.Point;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CommandJobs implements CommandExecutor {
    Player p;

    PlayerData pData;

    HelpCommand commands = new HelpCommand("jobs", "jbs");

    HelpCommand maireCmds = new HelpCommand("maire");

    public CommandJobs() {
        displayHelp();
        displayHelpAlias();
        displayHelpMaire();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            this.p = (Player) sender;
            this.pData = PlayerData.getPlayerData(this.p.getName());
        }
        if (label.equalsIgnoreCase("lois")) {
            Maire.listLaws(this.p.getName());
        } else if (label.equalsIgnoreCase("bienvenue") || label.equalsIgnoreCase("bvn")) {
            if (Delay.isDelayContains("newplayer")) {
                if (!Delay.isDelay("bienvenue", this.p.getName())) {
                    List<String[]> newplayers = Delay.getDelayContainsIdentity("newplayer");
                    Main.Log("Nombre de nouveau arrivants: " + newplayers.size());
                    boolean isAnotherPlayer = false;
                    for (String[] newplayer : newplayers) {
                        if (!this.p.getName().equals(newplayer[0])) {
                            Delay.newInstance(30.0D, this.p.getName(), "bienvenue");
                            Chat.getDefaultGroup().sendMessage(this.p, "Hey " + newplayer[0] + ", bienvenue sur l'archipel !");
                            MessageUtils.sendMessage((CommandSender) this.p, "§aVous venez de remporter §2100€ §apour votre jolie geste de civilité !");
                            this.pData.setMoney(this.pData.getMoney().add(BigDecimal.valueOf(100L)));
                            isAnotherPlayer = true;
                            break;
                        }
                    }
                    if (!isAnotherPlayer)
                        MessageUtils.sendMessage((CommandSender) this.p, "Aucun nouveau joueur détecté dans une timeline de 15 secondes");
                } else {
                    MessageUtils.sendMessage((CommandSender) this.p, "Vous devez attendre 30 secondes avant de pouvoir réexécuter cette commande");
                }
            } else {
                MessageUtils.sendMessage((CommandSender) this.p, "Aucun nouveau joueur détecté dans une timeline de 15 secondes");
            }
        }
        else if (label.equalsIgnoreCase("log")) {
            String statut = args[0];
            if (sender.hasPermission("cylrp.admin")) {
                if (statut.equalsIgnoreCase("enable")) {
                    if (!Main.showLogs) {
                        Main.showLogs = true;
                        sender.sendMessage("§aLes logs sont activés");
                    } else {
                        sender.sendMessage("§aLes logs sont déjà actifs");
                    }
                } else if (statut.equalsIgnoreCase("disable")) {
                    if (Main.showLogs) {
                        Main.showLogs = false;
                        sender.sendMessage("§cLes logs sont désactivés");
                    } else {
                        sender.sendMessage("§aLes logs sont déjà inactifs");
                    }
                } else {
                    sender.sendMessage("§c/log enable ou /log disable");
                }
            } else {
                sender.sendMessage("§cVous n'avez pas la permission d'exécuter cette commande");
            }
        }
        else if (label.equalsIgnoreCase("playerdebug")) {
            if (this.p.hasPermission("cylrp.admin")) {
                String playername = args[0];
                final PlayerData data = PlayerData.getPlayerData(playername);
                this.p.sendMessage("§c-----------------------------");
                this.p.sendMessage("§4Dans la hashmap = " + (PlayerData.getHashMap().containsKey(playername) ? "oui" : "non"));
                if (data != null) {

                    this.p.sendMessage("§4Nom du joueur = " + data.getPlayerName());
                    this.p.sendMessage("§4Mort = " + data.isDeath);
                    if (data.gangName != null)
                        this.p.sendMessage("§4Nom du gang = " + data.gangName);
                    if (data.companyName != null)
                        this.p.sendMessage("§4Nom de l'entreprise = " + data.companyName);
                    if (data.companyCategory != null)
                        this.p.sendMessage("§4Activité de l'entreprise = " + data.companyCategory);
                    if (data.getActuallyRegion() != null)
                        this.p.sendMessage("§4Actuellement situé dans la région = " + data.getActuallyRegion().getId());
                    this.p.sendMessage("§4Région d'intéraction = " + data.InteractingRegion);
                    this.p.sendMessage("§4Est menotté = " + (data.isMenotte ? "oui" : "non"));
                    this.p.sendMessage("§4 Argent = " + data.getMoney());
                    StringJoiner joiner = new StringJoiner(", ");
                    joiner.add("helmet id = " + ((data.helmet != null) ? data.helmet.getTypeId() : "rien"));
                    joiner.add("chestplate id = " + ((data.chestplate != null) ? data.chestplate.getTypeId() : "rien"));
                    joiner.add("leggings id = " + ((data.leggings != null) ? data.leggings.getTypeId() : "rien"));
                    joiner.add("boots id = " + ((data.boots != null) ? data.boots.getTypeId() : "rien"));
                    this.p.sendMessage("§4Tenues = " + joiner.toString());
                    if (data.selectedJob != null) {
                        this.p.sendMessage("§4 Nom de la tenue équipée = " + data.selectedJob.getEquipedClothes());
                        this.p.sendMessage("§4 Nom du métier = " + data.selectedJob.getFeatures().getName());
                    }
                    if (data.disconnectLocation != null)
                        this.p.sendMessage("§4Lieu de deconnexion = x=" + data.disconnectLocation.getX() + ";y=" + data.disconnectLocation.getY() + ";z=" + data.disconnectLocation.getZ());
                } else {
                    this.p.sendMessage("§4Les données de ce joueur n'existe pas");
                }
                this.p.sendMessage("§c-----------------------------");
            }
        } else if (label.equalsIgnoreCase("vote")) {
            MessageUtils.sendMessage((CommandSender) this.p, "§3Votez pour le serveur et obtenez des récompenses à couper le souffle en vous rendant sur §9https://www.craftyourliferp.fr/Votes/");
        } else if (label.equalsIgnoreCase("rp")) {
            Chat.switchCanal(this.p.getName(), "RP");
        } else if (label.equalsIgnoreCase("hrp")) {
            Chat.switchCanal(this.p.getName(), "HRP");
        } else if (label.equalsIgnoreCase("chatgang")) {
            Chat.switchCanal(this.p.getName(), "Gang");
        }
        else if (label.equalsIgnoreCase("chatentreprise")) {
            Chat.switchCanal(this.p.getName(), "Entreprise");
        } else if (label.equalsIgnoreCase("amende")) {
            if (args.length == 0) {
                MessageUtils.sendMessage((CommandSender) this.p, "§cIl faut entrer une motif");
                return false;
            }
            String reason = StringUtils.join(Arrays.copyOfRange((Object[]) args, 0, args.length), " ");
            if (reason != null) {
                Amende am = Amende.getAmendeByExpediteur(this.p.getName());
                if (am != null) {
                    am.setReason(reason);
                    am.getExpediteur().setReady(true);
                    MessageUtils.sendMessage((CommandSender) this.p, "§aMotif de l'amende envoyé");
                } else {
                    MessageUtils.sendMessage((CommandSender) this.p, "Aucun amende n'a été trouvé");
                }
            } else {
                MessageUtils.sendMessage((CommandSender) this.p, "Motif de l'amende attendu");
            }
        } else if (label.equalsIgnoreCase("craft")) {
            if (this.p.hasPermission("cylrp.admin")) {
                this.p.openInventory(Bukkit.createInventory(null, InventoryType.WORKBENCH));
            } else {
                MessageUtils.sendMessageFromConfig((CommandSender) this.p, "cylrp-not-permission");
            }
        } else if (label.equalsIgnoreCase("call")) {
            String job = args[0];
            if (!Delay.isDelay("call", this.p.getName())) {
                Delay.newInstance(60.0D, this.p.getName(), "call");
                if (job.equals(Jobs.Job.POMPIER.getName())) {
                    Jobs.Job.POMPIER.sendMessageAll("§c[Urgence]", "§4Un individu à besoin de renfort aux coordonnées x=" + this.p.getLocation().getBlockX() + ";z=" + this.p.getLocation().getBlockZ());
                    MessageUtils.sendMessage((CommandSender) this.p, "§aMessage envoyé aux pompiers");
                } else if (job.equals(Jobs.Job.GENDARME.getName())) {
                    Jobs.Job.sendMessageSecurityJobs("§c[Urgence]", "§4Un individu à besoin de renfort aux coordonnées x=" + this.p.getLocation().getBlockX() + ";z=" + this.p.getLocation().getBlockZ());
                    MessageUtils.sendMessage((CommandSender) this.p, "§aMessage envoyé aux gendarmes");
                } else if (job.equals(Jobs.Job.TAXI.getName())) {
                    Jobs.Job.TAXI.sendMessageAll("§6[Client]", "§eUn client vous attend aux coordonnées x=" + this.p.getLocation().getBlockX() + ";z=" + this.p.getLocation().getBlockZ());
                    MessageUtils.sendMessage((CommandSender) this.p, "§aMessage envoyé aux taxis");
                } else {
                    this.p.sendMessage("§cLes Urgences et Autres prestataires");
                    this.p.sendMessage("§6-------------------------------");
                    this.p.sendMessage("§ecall gendarme");
                    this.p.sendMessage("§ecall pompier");
                    this.p.sendMessage("§ecall taxi");
                    this.p.sendMessage("§6-------------------------------");
                }
            } else {
                MessageUtils.sendMessage((CommandSender) this.p, "Vous devez attendre " + TimerUtils.formatString(60L) + " avant de pouvoir rappeler les urgences");
            }
        } else if (label.equalsIgnoreCase("couvrefeu")) {
            if (this.pData.selectedJob instanceof Maire) {
                if (Maire.couvrefeu == null) {
                    if (!Delay.isDelay("couvre-feu")) {
                        Delay.newInstance(86400.0D, "couvre-feu");
                        Maire.couvrefeu = new TaskCreator(new CustomRunnable() {
                            private long elapsed = 0L;

                            private long playSound = 0L;

                            public void customRun() {
                                if (!(Boolean) getAttached()) {
                                    cancel();
                                    MessageUtils.broadcast("§3Le maire a décrété la fin du couvre feu");
                                    Maire.couvrefeu = null;
                                    return;
                                }
                                long now = System.currentTimeMillis();
                                if (now - this.elapsed >= 600000L) {
                                    this.elapsed = System.currentTimeMillis();
                                    MessageUtils.broadcast("§3Le maire à décrété un couvre-feu merci de rentrer chez-vous sous peine de poursuite judiciaire");
                                }
                                if (now - this.playSound >= 3000L) {
                                    this.playSound = System.currentTimeMillis();
                                    for (Player p : Bukkit.getOnlinePlayers())
                                        p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_TWINKLE_FAR, 1.0F, 1.0F);
                                }
                            }
                        }, false, 0L, 20L);
                        Maire.couvrefeu.attachObject(Boolean.TRUE);
                        MessageUtils.sendMessage((CommandSender) this.p, "§aCouvre-feu activé");
                    } else {
                        MessageUtils.sendMessage((CommandSender) this.p, "Vous pouvez instaurer un couvre-feu une fois par jour");
                    }
                } else {
                    Maire.couvrefeu.attachObject(Boolean.FALSE);
                    MessageUtils.sendMessage((CommandSender) this.p, "Couvre-feu désactivé");
                }
            } else {
                MessageUtils.sendMessage((CommandSender) this.p, "Le couvre-feu ne peut être instauré que par le Maire");
            }
        } else if (label.equalsIgnoreCase("actionrp")) {
            ChatGroup group = Chat.getPlayerChatGroup(this.p.getName());
            String action = StringUtils.join((Object[]) args, ' ');
            if (!Delay.isDelay(this.p.getName() + "-actionrp")) {
                Delay.newInstance(30.0D, this.p.getName() + "-actionrp");
                this.pData.roll = action;
                Bukkit.broadcastMessage("§d** §5" + this.pData.getIdentity()[0] + " " + this.pData.getIdentity()[1] + " §f- §d" + action + " **");
            } else {
                MessageUtils.sendMessage((CommandSender) this.p, "Vous devez attendre 1 minute avant de pouvoir réexécuter une nouvelle action");
            }
        } else if (label.equalsIgnoreCase("roll")) {
            if (this.pData.roll != null) {
                int random = (int) (Math.random() * 100.0D);
                this.pData.roll = null;
                if (random >= 50) {
                    MessageUtils.sendMessage((CommandSender) this.p, "§aVotre action a réussi avec un taux de réussite de §2" + random + "%");
                } else {
                    MessageUtils.sendMessage((CommandSender) this.p, "Vous avez échoué avec un taux de §4" + random + "%");
                }
            } else {
                MessageUtils.sendMessage((CommandSender) this.p, "Aucune action n'a été effectuée récemment");
            }
        } else if (label.equalsIgnoreCase("anonyme")) {
            String msg = StringUtils.join((Object[]) args, ' ');
            if (!Delay.isDelay("anonyme", this.p.getName())) {
                PlayerData pData = PlayerData.getPlayerData(this.p.getName());
                Delay.newInstance(60.0D, this.p.getName(), "anonyme");
                for (Player pl : Bukkit.getOnlinePlayers()) {
                    if (pl.hasPermission("cylrp.staff") || pl.hasPermission("cylrp.anonymemsg")) {
                        pl.sendMessage("§4(" + this.p.getName() + ") §7[Anonyme] §e" + msg);
                    } else {
                        pl.sendMessage("§7[Anonyme] §e" + msg);
                    }
                }
            } else {
                MessageUtils.sendMessage((CommandSender) this.p, "Vous pouvez envoyer un message toutes les " + TimerUtils.formatString(60L));
            }
        /*} else if (label.equalsIgnoreCase("radio")) {
            if (this.pData.selectedJob instanceof fr.karmaowner.jobs.Radio) {
                if (args[0].equalsIgnoreCase("fdl") && (this.pData.selectedJob instanceof fr.karmaowner.jobs.Security || this.pData.selectedJob instanceof fr.karmaowner.jobs.Pompier || this.pData.selectedJob instanceof fr.karmaowner.jobs.Medecin || this.pData.selectedJob instanceof Garde || this.pData.selectedJob instanceof fr.karmaowner.jobs.ChefGarde)) {
                    String[] msg = Arrays.<String>copyOfRange(args, 1, args.length);
                    String message = StringUtils.join((Object[]) msg, " ");
                    Jobs.Job.DOUANIER.sendMessageAll("§6[radio-FDL]", "§a" + this.pData.getIdentity()[0] + " " + this.pData.getIdentity()[1] + " : " + message);
                    Jobs.Job.MEDECIN.sendMessageAll("§6[radio-FDL]", "§a" + this.pData.getIdentity()[0] + " " + this.pData.getIdentity()[1] + " : " + message);
                    Jobs.Job.BAC.sendMessageAll("§6[radio-FDL]", "§a" + this.pData.getIdentity()[0] + " " + this.pData.getIdentity()[1] + " : " + message);
                    Jobs.Job.GENDARME.sendMessageAll("§6[radio-FDL]", "§a" + this.pData.getIdentity()[0] + " " + this.pData.getIdentity()[1] + " : " + message);
                    Jobs.Job.MILITAIRE.sendMessageAll("§6[radio-FDL]", "§a" + this.pData.getIdentity()[0] + " " + this.pData.getIdentity()[1] + " : " + message);
                    Jobs.Job.POLICIER.sendMessageAll("§6[radio-FDL]", "§a" + this.pData.getIdentity()[0] + " " + this.pData.getIdentity()[1] + " : " + message);
                    Jobs.Job.GIGN.sendMessageAll("§6[radio-FDL]", "§a" + this.pData.getIdentity()[0] + " " + this.pData.getIdentity()[1] + " : " + message);
                    Jobs.Job.POMPIER.sendMessageAll("§6[radio-FDL]", "§a" + this.pData.getIdentity()[0] + " " + this.pData.getIdentity()[1] + " : " + message);
                    Jobs.Job.GARDE.sendMessageAll("§6[radio-FDL]", "§a" + this.pData.getIdentity()[0] + " " + this.pData.getIdentity()[1] + " : " + message);
                    Jobs.Job.CHEFGARDE.sendMessageAll("§6[radio-FDL]", "§a" + this.pData.getIdentity()[0] + " " + this.pData.getIdentity()[1] + " : " + message);
                } else if (args[0].equalsIgnoreCase("mairie") && this.pData.selectedJob instanceof fr.karmaowner.jobs.JobsMairie) {
                    String[] msg = Arrays.<String>copyOfRange(args, 1, args.length);
                    String message = StringUtils.join((Object[]) msg, " ");
                    Jobs.Job.MAIRE.sendMessageAll("§6[radio-Mairie]", "§a" + this.pData.getIdentity()[0] + " " + this.pData.getIdentity()[1] + " : " + message);
                    Jobs.Job.MAIREADJOINT.sendMessageAll("§6[radio-Mairie]", "§a" + this.pData.getIdentity()[0] + " " + this.pData.getIdentity()[1] + " : " + message);
                    Jobs.Job.CHEFGARDE.sendMessageAll("§6[radio-Mairie]", "§a" + this.pData.getIdentity()[0] + " " + this.pData.getIdentity()[1] + " : " + message);
                    Jobs.Job.GARDE.sendMessageAll("§6[radio-Mairie]", "§a" + this.pData.getIdentity()[0] + " " + this.pData.getIdentity()[1] + " : " + message);
                } else {
                    String[] msg = Arrays.<String>copyOfRange(args, 0, args.length);
                    String message = StringUtils.join((Object[]) msg, " ");
                    this.pData.selectedJob.getFeatures().sendMessageAll("§e[radio-%jobname%]", "§a" + this.pData.getIdentity()[0] + " " + this.pData.getIdentity()[1] + " : " + message);
                }
            } else {
                this.p.sendMessage(ChatColor.RED + "Votre métier ne dispose pas de cette fonctionnalité");
            }*/
        } else if (label.equalsIgnoreCase("threaddebug")) {
            if (this.p.hasPermission("cylrp.debug")) {
                if (args[0].equalsIgnoreCase("on")) {
                    if (TaskCreator.getSavedTask("debug_" + this.p.getName()) == null) {
                        TaskCreator t = new TaskCreator(new CustomRunnable() {
                            private long start = System.currentTimeMillis();

                            public void customRun() {
                                if (!(Boolean) getAttached()) {
                                    cancel();
                                    TaskCreator.removeSavedTask("debug_" + CommandJobs.this.p.getName());
                                    return;
                                }
                                long now = System.currentTimeMillis();
                                if (now - this.start >= 15000L) {
                                    CommandJobs.this.p.sendMessage("§6-------------------------");
                                    CommandJobs.this.p.sendMessage("§cNombre de threads actif=§4" + Thread.activeCount());
                                    CommandJobs.this.p.sendMessage("§6-------------------------");
                                    this.start = System.currentTimeMillis();
                                }
                            }
                        }, true, 0L, 20L, "debug_" + this.p.getName());
                        t.attachObject(Boolean.TRUE);
                        this.p.sendMessage("§aLe mode débogage est activé");
                    } else {
                        this.p.sendMessage("§4Le mode débogage est déjà actif");
                    }
                } else if (args[0].equalsIgnoreCase("off")) {
                    CustomRunnable t = TaskCreator.getSavedTask("debug_" + this.p.getName());
                    if (t != null) {
                        t.setAttached(Boolean.FALSE);
                        this.p.sendMessage("§cLe mode débogage est désactivé");
                    } else {
                        this.p.sendMessage("§4Le mode débogage n'est pas actif");
                    }
                }
            } else {
                this.p.sendMessage("§cVous n'avez pas la permission d'effectuer cette action");
            }
        } else if (label.equalsIgnoreCase("pdata")) {
            if (this.p.hasPermission("cylrp.save")) {
                Bukkit.dispatchCommand((CommandSender) Main.INSTANCE.getServer().getConsoleSender(), "cylrpm &cDonnées des joueurs en cours de sauvegarde");
                Bukkit.broadcastMessage("§cDonnées des joueurs en cours de sauvegarde");
                ArrayList<String> onlineG = new ArrayList<>();
                ArrayList<String> onlineC = new ArrayList<>();
                for (Player o : Bukkit.getServer().getOnlinePlayers()) {
                    final PlayerData data = PlayerData.getPlayerData(o.getName());
                    data.saveData();
                    if (data.gangName != null && !data.gangName.equals("") &&
                            !onlineG.contains(data.gangName))
                        onlineG.add(data.gangName);
                    if (data.companyName != null && !data.companyName.equals("") &&
                            !onlineC.contains(data.companyName))
                        onlineC.add(data.companyName);
                }
                for (String g : onlineG) {
                    GangData gdata = GangData.getGang(g);
                    gdata.saveData();
                }
                for (String g : onlineC) {
                    CompanyData cdata = CompanyData.getCompanyData(g);
                    cdata.saveData();
                }
                Bukkit.broadcastMessage("§cDonnées des joueurs sauvegardées !");
            } else {
                MessageUtils.sendMessage((CommandSender) this.p, "Vous n'avez pas la permission de faire cela");
            }
        } else if (label.equalsIgnoreCase("sdata")) {
            if (this.p.hasPermission("cylrp.save")) {
                if (args.length == 1) {
                    String arg = args[0];
                    if (arg != null &&
                            arg.equalsIgnoreCase("force")) {
                        SaveScheduler.saveIsRunning = false;
                        Main.saveScheduler.activateScheduler();
                    }
                }
                if (!SaveScheduler.saveIsRunning) {
                    Main.saveScheduler.forceSave();
                } else {
                    this.p.sendMessage("§cLa sauvegarde est déjà en cours...");
                }
            } else {
                MessageUtils.sendMessage((CommandSender) this.p, "Vous n'avez pas la permission de faire cela");
            }
        } else if (label.equalsIgnoreCase("wantedlist")) {
            if(this.pData.selectedJob instanceof Security) WantedList.openInventory(this.p);
            else MessageUtils.sendMessage(p,"§cSeul les fdl peuvent accéder à la wanted list");
        }
        else if (label.equalsIgnoreCase("wanted")) {
            if (sender.hasPermission("cylrp.wanted")) {
                if (args.length >= 4) {
                    if (args[0].equalsIgnoreCase("add")) {
                        String pseudo = args[1];
                        if (Bukkit.getPlayerExact(pseudo) != null) {
                            int stars = Integer.parseInt(args[2]);
                            String[] msg = Arrays.<String>copyOfRange(args, 3, args.length);
                            String motif = StringUtils.join((Object[]) msg, " ");
                            WantedList.addStars(pseudo, stars);
                            WantedList.wantedMessagePlace(pseudo, stars, motif);
                            sender.sendMessage("§aUn avis de recherche a été déclaré pour le joueur.");
                        } else {
                            sender.sendMessage("§cCe joueur n'existe pas.");
                        }
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("remove")) {
                        String pseudo = args[1];
                        if (Bukkit.getPlayerExact(pseudo) != null) {
                            if (WantedList.isWanted(pseudo)) {
                                WantedList.stopWanted(pseudo);
                                sender.sendMessage("§aAvis de recherche supprimé.");
                            } else {
                                sender.sendMessage("§cCe joueur n'est pas recherché.");
                            }
                        } else {
                            sender.sendMessage("§cCe joueur n'existe pas.");
                        }
                    }
                } else if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("removemoney"))
                        if (!(sender instanceof Player)) {
                            String pseudo = args[1];
                            double sum = Double.parseDouble(args[2]);
                            Player target = Bukkit.getPlayerExact(pseudo);
                            if (target != null) {
                                if (sum > 0.0D) {
                                    if (WantedList.isWanted(pseudo)) {
                                        PlayerData pData = PlayerData.getPlayerData(pseudo);
                                        if (pData != null) {
                                            double minus = pData.getMoney().subtract(BigDecimal.valueOf(sum)).doubleValue();
                                            if (minus >= 0.0D) {
                                                WantedList.stopWanted(pseudo);
                                                pData.setMoney(BigDecimal.valueOf(minus));
                                                MessageUtils.sendMessage(sender, "§a Avis de recherché supprimé");
                                            } else {
                                                MessageUtils.sendMessage((CommandSender) target, "Vous n'avez pas assez d'argent");
                                            }
                                        } else {
                                            MessageUtils.sendMessage((CommandSender) target, "Vos données sont introuvables");
                                        }
                                    } else {
                                        MessageUtils.sendMessage((CommandSender) target, "Vous n'êtes pas recherché");
                                    }
                                } else {
                                    MessageUtils.sendMessage(sender, "Le montant doit être supérieur à 0");
                                }
                            } else {
                                MessageUtils.sendMessage(sender, "Ce joueur n'existe pas");
                            }
                        } else {
                            MessageUtils.sendMessage(sender, "Cette commande est réservée qu'à la console");
                        }
                }
            } else {
                MessageUtils.sendMessage((CommandSender) this.p, "Vous n'avez pas la permission de faire cela");
            }
        } else if (label.equalsIgnoreCase("assaut")) {
            if (this.pData.selectedJob instanceof RebelleTerroriste)
                if (this.pData.getActuallyRegion() != null && Douanier.isRegion(this.pData.getActuallyRegion().getId())) {
                    if (!Delay.isDelay("assaut")) {
                        PlayerUtils utils = new PlayerUtils();
                        utils.setInventory((Inventory) this.p.getInventory());
                        if (utils.isOneOfItemsOnInv(JobsEvents.WEAPON.items())) {
                            Delay.newInstance(7200.0D, "assaut");
                            Bukkit.broadcastMessage(ChatColor.WHITE.toString() + ChatColor.MAGIC + "[" + ChatColor.GOLD + ChatColor.BOLD + "Assaut" + ChatColor.WHITE + ChatColor.MAGIC + "]" + ChatColor.YELLOW + " Alerte: La douane est victime d'un braquage armé, les forces de l'ordre doivent se rendre sur place !");
                            if (!WantedList.isWanted(this.p.getName())) {
                                WantedList.addStars(this.p.getName(), 4);
                                WantedList.wantedMessagePlace(this.p.getName(), 4, "à la douane");
                            }
                        } else {
                            MessageUtils.sendMessage((CommandSender) this.p, "Pour effectuer cette action vous devez disposer d'une arme");
                        }
                    } else {
                        this.p.sendMessage("§4Le /assaut a déjà été utilisé par un individu et est disponible toutes les 2 heures.");
                    }
                } else {
                    this.p.sendMessage("§4Vous ne pouvez faire un assaut qu'à la douane.");
                }
        } else if (label.equalsIgnoreCase("attentat")) {
            if (this.pData.selectedJob instanceof fr.karmaowner.jobs.Terroriste) {
                if (!Delay.isDelay("attentat")) {
                    PlayerUtils utils = new PlayerUtils();
                    utils.setInventory((Inventory) this.p.getInventory());
                    if (utils.isOneOfItemsOnInv(JobsEvents.WEAPON.items())) {
                        Delay.newInstance(14400.0D, "attentat");
                        if (!WantedList.isWanted(this.p.getName()))
                            WantedList.addStars(this.p.getName(), 5);
                        if (this.pData.getActuallyRegion() != null) {
                            RebelleTerroriste.zoneAttentat.add(new RebelleTerroriste.AttentatArea(this.pData.getActuallyRegion()));
                            BlockVector middle = RegionUtils.getMiddleXZ(this.p.getWorld(), this.pData.getActuallyRegion());
                            String coord = "x=" + middle.getBlockX() + ";y=65;z=" + middle.getBlockZ();
                            Bukkit.broadcastMessage(ChatColor.WHITE.toString() + ChatColor.MAGIC + "[" + ChatColor.RED + ChatColor.BOLD + "Attentat" + ChatColor.WHITE + ChatColor.MAGIC + "]" + ChatColor.GREEN + " Alerte: Un attentat a lieu dans la région " + ChatColor.DARK_GREEN + this.pData

                                    .getActuallyRegion().getId() + ChatColor.GREEN + " aux coordonnées §4" + coord + " §a soyez vigilant !");
                            Jobs.Job.sendMessageSecurityJobs("§c[Danger]", "§4Un attentat a lieu dans la région §c" + this.pData.getActuallyRegion().getId() + "§4 aux coordonnées §c" + coord + "§4 votre intervention est necéssaire !");
                            PlayerUtils putils = new PlayerUtils();
                            putils.setPlayer(this.p);
                            for (Player t : putils.getNearbyPlayers(15.0D)) {
                                final PlayerData data = PlayerData.getPlayerData(t.getName());
                                if (data != null && data.selectedJob instanceof fr.karmaowner.jobs.Terroriste &&
                                        !WantedList.isWanted(t.getName()))
                                    WantedList.wantedMessagePlace(t.getName(), 5, data.getActuallyRegion().getId());
                            }
                            if (!WantedList.isWanted(this.p.getName()))
                                WantedList.wantedMessagePlace(this.p.getName(), 5, this.pData.getActuallyRegion().getId());
                        } else {
                            Point p1 = new Point(this.p.getLocation().getBlockX() - 50, this.p.getLocation().getBlockY() - 50);
                            Point p2 = new Point(this.p.getLocation().getBlockX() + 50, this.p.getLocation().getBlockY() + 50);
                            String coord = "x=" + this.p.getLocation().getBlockX() + ";y=65;z=" + this.p.getLocation().getBlockZ();
                            RebelleTerroriste.zoneAttentat.add(new RebelleTerroriste.AttentatArea(p1, p2));
                            Bukkit.broadcastMessage(ChatColor.WHITE.toString() + ChatColor.MAGIC + "[" + ChatColor.RED + ChatColor.BOLD + "Attentat" + ChatColor.WHITE + ChatColor.MAGIC + "]" + ChatColor.GREEN + " Alerte: Un attentat a lieu aux coordonnées " + ChatColor.DARK_GREEN + coord + ChatColor.GREEN + " soyez vigilant !");
                            Jobs.Job.sendMessageSecurityJobs("§c[Danger]", "§4Un attentat a lieu dans les coordonnées §c" + coord + "§4 votre intervention est necéssaire !");
                            PlayerUtils putils = new PlayerUtils();
                            putils.setPlayer(this.p);
                            for (Player t : putils.getNearbyPlayers(15.0D)) {
                                final PlayerData data = PlayerData.getPlayerData(t.getName());
                                if (data != null && data.selectedJob instanceof fr.karmaowner.jobs.Terroriste &&
                                        !WantedList.isWanted(t.getName()))
                                    WantedList.wantedMessage(t.getName(), 5);
                            }
                            if (!WantedList.isWanted(this.p.getName()))
                                WantedList.wantedMessage(this.p.getName(), 5);
                        }
                    } else {
                        MessageUtils.sendMessage((CommandSender) this.p, "Pour effectuer cette action vous devez disposer d'une arme");
                    }
                } else {
                    this.p.sendMessage("§4Le /attentat a déjà été utilisé par un individu et est disponible toutes les 3 heures.");
                }
            } else {
                MessageUtils.sendMessage((CommandSender) this.p, "Cette commande est réservée aux terroristes");
            }
        } else if (label.equalsIgnoreCase("reqmandat")) {
            if (args.length == 2) {
                String receiver = args[0];
                String gangName = args[1];
                if (GangData.getGangData(gangName) == null) {
                    MessageUtils.sendMessage((CommandSender) this.p, "Ce gang n'existe pas");
                    return false;
                }
                if (this.pData.selectedJob instanceof fr.karmaowner.jobs.BAC) {
                    hasGrade grade = (hasGrade) this.pData.selectedJob;
                    if (grade.getGrade().getGrades().getHg().contains(grade.getGrade().getGrade().getNom())) {
                        if (Bukkit.getPlayerExact(receiver) != null) {
                            PlayerData dataReceiver = PlayerData.getPlayerData(receiver);
                            if (dataReceiver.selectedJob instanceof Maire) {
                                Request.createRequest(Request.RequestType.BACMANDAT, this.p.getName(), receiver, gangName);
                            } else if (dataReceiver.selectedJob instanceof fr.karmaowner.jobs.AssembleeNationale) {
                                hasGrade Rgrade = (hasGrade) dataReceiver.selectedJob;
                                if (Rgrade.getGrade().getGrade().getNom().equals("President")) {
                                    Request.createRequest(Request.RequestType.BACMANDAT, this.p.getName(), receiver, gangName);
                                } else if (Rgrade.getGrade().getGrade().getNom().equals("VicePresident")) {
                                    Request.createRequest(Request.RequestType.BACMANDAT, this.p.getName(), receiver, gangName);
                                } else {
                                    MessageUtils.sendMessage((CommandSender) this.p, "Ce joueur n'est ni le Président ni le Vice Président de l'Assemblée");
                                }
                            } else {
                                MessageUtils.sendMessage((CommandSender) this.p, "Seul le Maire ou le Président de l'AssembléeNationale peut recevoir cette requête");
                            }
                        } else {
                            MessageUtils.sendMessage((CommandSender) this.p, "Ce joueur n'est pas connecté ou n'existe pas");
                        }
                    } else {
                        MessageUtils.sendMessage((CommandSender) this.p, "Cette commande est réservée aux " +
                                StringUtils.join(grade.getGrade().getGrades().getHg(), ", ") + " de la BAC");
                    }
                } else {
                    MessageUtils.sendMessage((CommandSender) this.p, "Vous n'avez pas la permission");
                }
            }
        } else if (label.equalsIgnoreCase("reqmandataccpt")) {
            if (args.length == 1) {
                String Reqsender = args[0];
                if (this.pData.selectedJob instanceof fr.karmaowner.jobs.AssembleeNationale) {
                    hasGrade grade = (hasGrade) this.pData.selectedJob;
                    if (!grade.getGrade().getGrade().getNom().equals("President") && !grade.getGrade().getGrade().getNom().equals("VicePresident")) {
                        MessageUtils.sendMessage((CommandSender) this.p, "Vous n'êtes ni le Président ni le Vice-Président de l'assemblée");
                        return false;
                    }
                } else if (!(this.pData.selectedJob instanceof Maire)) {
                    MessageUtils.sendMessage((CommandSender) this.p, "Vous n'avez pas la permission");
                    return false;
                }
                Request req = Request.findRequest(Request.RequestType.BACMANDAT,Reqsender,sender.getName());
                if (req != null) {
                    Player reqSenderPlayer = Bukkit.getPlayerExact(req.getSender());
                    String gangName = (String) req.getGangName();
                    if (!Maire.hasMandat(gangName)) {
                        if (reqSenderPlayer != null)
                            MessageUtils.sendMessage((CommandSender) reqSenderPlayer, "§aVotre demande a été accepté par §2" +
                                    StringUtils.capitalise(this.pData.selectedJob.getFeatures().getName()));
                        Maire.mandatGang.add(gangName);
                        req.destroy();
                        MessageUtils.sendMessage((CommandSender) this.p, "§aUn mandat de démantèlement a été créé pour le gang §2" + gangName);
                    } else {
                        MessageUtils.sendMessage((CommandSender) this.p, "Requête refusée: Ce gang a déjà un mandat");
                        if (reqSenderPlayer != null)
                            MessageUtils.sendMessage((CommandSender) reqSenderPlayer, "Requête refusée: Ce gang a déjà un mandat");
                        req.destroy();
                    }
                } else {
                    MessageUtils.sendMessage((CommandSender) this.p, "Aucune requête reçu de la part de cette individu");
                }
            }
        } else if (label.equalsIgnoreCase("mandatremove")) {
            if (args.length == 1) {
                String gangName = args[0];
                if (this.pData.selectedJob instanceof fr.karmaowner.jobs.AssembleeNationale) {
                    hasGrade grade = (hasGrade) this.pData.selectedJob;
                    if (!grade.getGrade().getGrade().getNom().equals("President") && !grade.getGrade().getGrade().getNom().equals("VicePresident")) {
                        MessageUtils.sendMessage((CommandSender) this.p, "Vous n'êtes ni le Président ni le Vice-Président de l'assemblée");
                        return false;
                    }
                } else if (!(this.pData.selectedJob instanceof Maire)) {
                    MessageUtils.sendMessage((CommandSender) this.p, "Vous n'avez pas la permission");
                    return false;
                }
                if (Maire.hasMandat(gangName)) {
                    Maire.removeMandat(gangName);
                    MessageUtils.sendMessage((CommandSender) this.p, "§aLe mandat pour ce gang a été supprimé !");
                } else {
                    MessageUtils.sendMessage((CommandSender) this.p, "Aucun mandat trouvé pour ce gang");
                }
            }
        } else if (label.equalsIgnoreCase("maire")) {
            if (args.length == 0) {
                MessageUtils.sendMessage((CommandSender) this.p, this.maireCmds.getCommands());
                return true;
            }
            if (args[0].equalsIgnoreCase("help")) {
                if(args.length == 1)
                {
                    MessageUtils.sendMessage((CommandSender) this.p, this.maireCmds.getCommands(1));
                    return true;
                }
                int page = Integer.parseInt(args[1]);
                MessageUtils.sendMessage((CommandSender) this.p, this.maireCmds.getCommands(page));
            }
            if (this.pData.selectedJob instanceof Maire || this.pData.selectedJob instanceof fr.karmaowner.jobs.ChefGarde || this.pData.selectedJob instanceof fr.karmaowner.jobs.MaireAdjoint || this.p.hasPermission("cylrp.maire.gestion")) {
                if (this.pData.selectedJob instanceof Maire && args[0].equalsIgnoreCase("add")) {
                    String[] msg = Arrays.<String>copyOfRange(args, 1, args.length);
                    String law = StringUtils.join((Object[]) msg, " ");
                    if (law != null)
                        if (law.length() <= 255) {
                            Maire.addLaw(law);
                        } else {
                            MessageUtils.sendMessage(sender, "Une loi doit-être constitué de moins de 255 caractères.");
                        }
                } else if (this.pData.selectedJob instanceof Maire && args[0].equalsIgnoreCase("remove")) {
                    int lawNum = Integer.parseInt(args[1]);
                    if (Maire.deleteLaw(lawNum)) {
                        MessageUtils.sendMessage(sender, "Loi supprimée.");
                    } else {
                        MessageUtils.sendMessage(sender, "Cette loi n'existe pas.");
                    }
                } else if (args[0].equalsIgnoreCase("garde") && args[1].equalsIgnoreCase("invite")) {
                    String pseudo = args[2];
                    if (pseudo != null)
                        if (Bukkit.getPlayerExact(pseudo) != null) {
                            PlayerData pseudoData = PlayerData.getPlayerData(pseudo);
                            if (!(pseudoData.selectedJob instanceof Maire) && !(pseudoData.selectedJob instanceof fr.karmaowner.jobs.MaireAdjoint)) {
                                if (pseudoData.selectedJob instanceof hasGrade) {
                                    hasGrade grade = (hasGrade) pseudoData.selectedJob;
                                    String rankName = grade.getGrade().getGrade().getNom();
                                    if (grade.getGrade().getGrades().getHg().contains(rankName)) {
                                        MessageUtils.sendMessage(sender, "Impossible d'inviter ce joueur car il est un haut-gradé");
                                        return true;
                                    }
                                }
                                Garde.inviteGarde(this.p, pseudo);
                            } else {
                                MessageUtils.sendMessage(sender, "Ce joueur ne doit ni être un Maire ni un Maire-Adjoint");
                            }
                        } else {
                            MessageUtils.sendMessage(sender, "Ce joueur n'existe pas ou n'est pas connecté.");
                        }
                } else if (args[0].equalsIgnoreCase("garde") && args[1].equalsIgnoreCase("kick")) {
                    String pseudo = args[2];
                    if (pseudo != null)
                        if (Bukkit.getPlayerExact(pseudo) != null) {
                            if (Garde.kickGarde(pseudo)) {
                                sender.sendMessage("Ce joueur n'est plus votre garde.");
                                Bukkit.getPlayerExact(pseudo).sendMessage(Maire.prefix + " Vous n'êtes plus le garde du Maire.");
                            } else {
                                sender.sendMessage("Ce joueur n'est pas votre garde.");
                            }
                        } else {
                            MessageUtils.sendMessage(sender, "Ce joueur n'existe pas ou n'est pas connecté.");
                        }
                } else if ((this.pData.selectedJob instanceof Maire || this.pData.selectedJob instanceof fr.karmaowner.jobs.MaireAdjoint) && args[0].equalsIgnoreCase("annonce")) {
                    if (args.length >= 2) {
                        String[] msg = Arrays.<String>copyOfRange(args, 1, args.length);
                        String message = StringUtils.join((Object[]) msg, " ");
                        Maire.broadcastMaire(message);
                    }
                } else if (args[0].equalsIgnoreCase("msgGardes")) {
                    if (args.length >= 2) {
                        String[] msg = Arrays.<String>copyOfRange(args, 1, args.length);
                        String message = StringUtils.join((Object[]) msg, " ");
                        Maire.sendMessageGardes(message);
                        sender.sendMessage(Maire.prefix+ " le message a bien été transmis aux gardes.");
                    }
                } else if (args[0].equalsIgnoreCase("listGardes") &&
                        !Maire.listGardes()) {
                    sender.sendMessage("§cVous avez aucun garde.");
                }
            } else if (args[0].equalsIgnoreCase("garde") && args[1].equalsIgnoreCase("accept")) {
                if (this.pData.selectedJob instanceof hasGrade) {
                    hasGrade grade = (hasGrade) this.pData.selectedJob;
                    String rankName = grade.getGrade().getGrade().getNom();
                    if (grade.getGrade().getGrades().getHg().contains(rankName)) {
                        MessageUtils.sendMessage(sender, "Impossible d'inviter ce joueur car il est un haut-gradé");
                        return true;
                    }
                }
                if (Garde.acceptInvitation(this.p.getName())) {
                    this.p.sendMessage(Maire.prefix + " §bInvitation acceptée.");
                    Maire.sendMessage("§3Vous avez un nouveau garde: §b" + this.p.getName());
                } else {
                    this.p.sendMessage(Maire.prefix + " §bVous n'avez pas reçu d'invitation de la part du Maire.");
                }
            } else {
                MessageUtils.sendMessage(sender, "Cette commande n'existe pas");
            }
        } else if (label.equalsIgnoreCase("jbs")) {
            if (Missions.isPlayerCreatingMissions(this.p))
                Missions.creatingMissions.put(this.p, args);
            if (args.length == 0) {
                MessageUtils.sendMessage((CommandSender) this.p, this.commands.getCommandsShortCut());
            } else {
                if (args[0].equalsIgnoreCase("clo"))
                    this.pData.selectedJob.getFeatures().openClothesInventory(this.p.getName());
                if (args[0].equalsIgnoreCase("h")) {
                    int page = Integer.parseInt(args[1]);
                    MessageUtils.sendMessage((CommandSender) this.p, this.commands.getCommandsShortcut(page));
                    return false;
                }
                if (args[0].equalsIgnoreCase("spb")) {
                    if (!sender.hasPermission("cylrp.jobs.setpoints")) {
                        MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                        return false;
                    }
                    if (args[1] != null && args[2] != null) {
                        int quantity = Integer.parseInt(args[1]);
                        String target = args[2];
                        final Player tgt = Main.INSTANCE.getServer().getPlayerExact(target);
                        if (tgt != null) {
                            final PlayerData data = PlayerData.getPlayerData(tgt.getName());
                            if (data.selectedJob instanceof hasGrade) {
                                hasGrade grade = (hasGrade) data.selectedJob;
                                grade.getGrade().setPointSuppl(grade.getGrade().getPointSuppl() + quantity);
                                MessageUtils.sendMessageFromConfig(sender, "jobs-sended-points");
                            } else {
                                MessageUtils.sendMessageFromConfig(sender, "jobs-cannot-send-points");
                            }
                        } else {
                            MessageUtils.sendMessageFromConfig(sender, "user-invalid");
                        }
                    }
                    return false;
                }
                if (args[0].equalsIgnoreCase("ch")) {
                    if (!sender.hasPermission("cylrp.jobs.change") && !(this.pData.selectedJob instanceof CanChangeJob)) {
                        MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                        return false;
                    }
                    String job = args[1];
                    String playername = args[2];
                    if (this.pData.selectedJob instanceof CanChangeJob && !this.p.hasPermission("cylrp.jobs.change")) {
                        CanChangeJob change = (CanChangeJob) this.pData.selectedJob;
                        if (!change.jobs().contains(job)) {
                            MessageUtils.sendMessage((CommandSender) this.p, "Vous n'avez pas accès à ce métier");
                            return false;
                        }
                        if (this.pData.selectedJob instanceof hasGrade) {
                            hasGrade grade = (hasGrade) this.pData.selectedJob;
                            if (!grade.getGrade().getGrades().getHg().contains(grade.getGrade().getGrade().getNom())) {
                                MessageUtils.sendMessage((CommandSender) this.p, "Vous n'avez pas la permission d'exécuter cette commande");
                                return false;
                            }
                            if (this.p.getName().equals(playername)) {
                                MessageUtils.sendMessage((CommandSender) this.p, "Vous ne pouvez pas exécuter cette commande sur vous");
                                return false;
                            }
                        }
                    }
                    if (job != null && playername != null)
                        if (Jobs.Job.isJob(job)) {
                            final PlayerData data = PlayerData.getPlayerData(playername);
                            if (data != null) {
                                JobsEvents.changePlayerJob(data, job, playername);
                                this.p.sendMessage(ChatColor.GREEN + "Le métier du joueur " + ChatColor.DARK_GREEN + playername + ChatColor.GREEN + " vient d'être changé.");
                            } else {
                                this.p.sendMessage(ChatColor.RED + "Données du joueur inexistante. Ce joueur n'existe peut-être pas.");
                            }
                        } else {
                            this.p.sendMessage(ChatColor.RED + "Ce métier n'existe pas !");
                        }
                }
                if (args[0].equalsIgnoreCase("mrac"))
                    for (Jobs.Job j : Jobs.Job.values())
                        this.p.sendMessage("§9" + j.getName() + " => " + j.getShortcut());
                if (args[0].equalsIgnoreCase("kp")) {
                    if (!sender.hasPermission("cylrp.jobs.change")) {
                        MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                        return false;
                    }
                    String playername = args[1];
                    if (playername != null) {
                        final PlayerData data = PlayerData.getPlayerData(playername);
                        if (data != null) {
                            data.keepJob = !data.keepJob;
                            if (data.keepJob) {
                                this.p.sendMessage(ChatColor.GREEN + "La préservation du métier pour le joueur " + ChatColor.DARK_GREEN + playername + ChatColor.GREEN + " a été activé.");
                            } else {
                                this.p.sendMessage(ChatColor.GREEN + "La préservation du métier pour le joueur " + ChatColor.DARK_GREEN + playername + ChatColor.RED + " a été désactivé.");
                            }
                        } else {
                            this.p.sendMessage(ChatColor.RED + "Données du joueur inexistante. Ce joueur n'existe peut-être pas.");
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("pun")) {
                    if (!sender.hasPermission("cylrp.jobs.punite")) {
                        MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                        return false;
                    }
                    String job = args[1];
                    String playername = args[2];
                    if (job != null && playername != null)
                        if (Jobs.Job.isJob(job)) {
                            final PlayerData data = PlayerData.getPlayerData(playername);
                            if (data != null) {
                                Jobs.ban(playername, job, 86400L);
                                this.p.sendMessage(ChatColor.GREEN + "Le joueur " + ChatColor.DARK_GREEN + playername + ChatColor.GREEN + " à désormais une interdiction d'exercer le métier " + ChatColor.DARK_GREEN + job);
                            } else {
                                this.p.sendMessage(ChatColor.RED + "Données du joueur inexistante. Ce joueur n'existe peut-être pas.");
                            }
                        } else {
                            this.p.sendMessage(ChatColor.RED + "Ce métier n'existe pas !");
                        }
                }
                if (args[0].equalsIgnoreCase("rpb")) {
                    if (!sender.hasPermission("cylrp.jobs.removepoints")) {
                        MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                        return false;
                    }
                    if (args[1] != null && args[2] != null) {
                        int quantity = Integer.parseInt(args[1]);
                        String target = args[2];
                        final Player tgt = Main.INSTANCE.getServer().getPlayerExact(target);
                        if (tgt != null) {
                            final PlayerData data = PlayerData.getPlayerData(tgt.getName());
                            if (data.selectedJob instanceof hasGrade) {
                                hasGrade grade = (hasGrade) data.selectedJob;
                                grade.getGrade().setPointSuppl(grade.getGrade().getPointSuppl() - quantity);
                                MessageUtils.sendMessageFromConfig(sender, "jobs-points-removed");
                            } else {
                                MessageUtils.sendMessageFromConfig(sender, "jobs-cannot-remove-points");
                            }
                        } else {
                            MessageUtils.sendMessageFromConfig((CommandSender) this.p, "jobs-cannot-remove-points");
                        }
                    }
                    return false;
                }
                if (args[0].equalsIgnoreCase("cur")) {
                    if (args.length == 2 && this.p.hasPermission("cylrp.staff")) {
                        String pseudo = args[1];
                        final PlayerData data = PlayerData.getPlayerData(pseudo);
                        if (data.selectedJob != null) {
                            this.p.sendMessage("§cSon métier : §d" + data.selectedJob.getFeatures().getDisplayName());
                        } else {
                            this.p.sendMessage("§cCe jouer n'a pas de métier");
                        }
                        return false;
                    }
                    if (this.pData.selectedJob != null) {
                        this.p.sendMessage("§cVotre métier : §d" + this.pData.selectedJob.getFeatures().getDisplayName());
                    } else {
                        this.p.sendMessage("§cVous n'avez pas de métier");
                    }
                    return false;
                }
                if (args[0].equalsIgnoreCase("gd")) {
                    if (this.pData.selectedJob instanceof hasGrade) {
                        hasGrade grade = (hasGrade) this.pData.selectedJob;
                        JobGrades Jg = grade.getGrade();
                        this.p.sendMessage(ChatColor.RED.toString() + ChatColor.MAGIC + "&" + ChatColor.GOLD + "------ Stats ------" + ChatColor.RED.toString() + ChatColor.MAGIC + "&");
                        this.p.sendMessage(ChatColor.GREEN + "Votre rang: " + ChatColor.DARK_GREEN + Jg.getGrade().getNom());
                        if (Jg.getGrades().nextGrade(Jg.getPoints()) != null) {
                            this.p.sendMessage(ChatColor.GREEN + "Prochain rang: " + ChatColor.DARK_GREEN + Jg.getGrades().nextGrade(Jg.getPoints()).getNom());
                            this.p.sendMessage(ChatColor.GREEN + "Points du prochain rang: " + ChatColor.DARK_GREEN + Jg.getGrades().nextGrade(Jg.getPoints()).getPoints());
                        }
                        this.p.sendMessage(ChatColor.RED + "Nombre de points: " + ChatColor.DARK_RED + Jg.getPoints());
                        this.p.sendMessage(ChatColor.AQUA + "Nombre de missions: " + ChatColor.DARK_AQUA + Jg.getNbMissions());
                        this.p.sendMessage(ChatColor.YELLOW + "Points malus: " + ChatColor.GOLD + Jg.getMalus());
                        this.p.sendMessage(ChatColor.GOLD + "-------------------");
                    } else {
                        this.p.sendMessage("§cVotre métier actuel n'est pas compatible avec le système de grades");
                    }
                    return false;
                }
                if (args[0].equalsIgnoreCase("oson"))
                    if (this.pData.selectedJob.getFeatures().hasServiceFunction()) {
                        if (this.pData.selectedJob instanceof Missions) {
                            Missions missions = (Missions) this.pData.selectedJob;
                            if (missions.getInProgress() != null) {
                                this.p.sendMessage(ChatColor.DARK_RED + "Impossible d'effectuer cette action car vous avez une mission qui est en cours.");
                                return false;
                            }
                        }
                        if (this.pData.selectedJob instanceof Douanier) {
                            if (this.pData.getActuallyRegion() != null && !this.pData.getActuallyRegion().getId().contains("douane")) {
                                this.p.sendMessage(ChatColor.DARK_RED + "Impossible d'effectuer cette action car vous n'êtes pas à la douane.");
                                return false;
                            }
                            if (this.pData.getActuallyRegion() == null) {
                                this.p.sendMessage(ChatColor.DARK_RED + "Impossible d'effectuer cette action car vous n'êtes pas à la douane.");
                                return false;
                            }
                        }
                        if (this.pData.selectedJob instanceof fr.karmaowner.jobs.Medecin) {
                            if (this.pData.getActuallyRegion() != null && !this.pData.getActuallyRegion().getId().contains("medecin")) {
                                this.p.sendMessage(ChatColor.DARK_RED + "Impossible d'effectuer cette action car vous n'êtes pas dans la salle de réanimation.");
                                return false;
                            }
                            if (this.pData.getActuallyRegion() == null) {
                                this.p.sendMessage(ChatColor.DARK_RED + "Impossible d'effectuer cette action car vous n'êtes pas dans la salle de réanimation.");
                                return false;
                            }
                        }
                        if (!this.pData.selectedJob.isOutOfService()) {
                            this.pData.selectedJob.setOutOfService(true);
                            this.pData.selectedJob.unequipClothes(pData, true, true);
                            this.p.getInventory().setHelmet(this.pData.helmet);
                            this.p.getInventory().setChestplate(this.pData.chestplate);
                            this.p.getInventory().setLeggings(this.pData.leggings);
                            this.p.getInventory().setBoots(this.pData.boots);
                            InventoryUtils.setGpb(p,this.pData.gpb, false);
                            this.p.sendMessage(ChatColor.GREEN + "Le mode hors service est désormais actif !");
                            return true;
                        }
                        this.p.sendMessage(ChatColor.RED + "Vous êtes déjà hors service !");
                    } else {
                        this.p.sendMessage(ChatColor.RED + "Le mode service est désactivé pour ce métier !");
                    }
                if (args[0].equalsIgnoreCase("osoff"))
                    if (this.pData.selectedJob.getFeatures().hasServiceFunction()) {
                        if (this.pData.selectedJob instanceof Douanier) {
                            if (this.pData.getActuallyRegion() != null && !this.pData.getActuallyRegion().getId().contains("douane")) {
                                this.p.sendMessage(ChatColor.DARK_RED + "Impossible d'effectuer cette action car vous n'êtes pas à la douane.");
                                return false;
                            }
                            if (this.pData.getActuallyRegion() == null) {
                                this.p.sendMessage(ChatColor.DARK_RED + "Impossible d'effectuer cette action car vous n'êtes pas à la douane.");
                                return false;
                            }
                        }
                        if (this.pData.selectedJob instanceof fr.karmaowner.jobs.Medecin) {
                            if (this.pData.getActuallyRegion() != null && !this.pData.getActuallyRegion().getId().contains("medecin")) {
                                this.p.sendMessage(ChatColor.DARK_RED + "Impossible d'effectuer cette action car vous n'êtes pas dans la salle de réanimation.");
                                return false;
                            }
                            if (this.pData.getActuallyRegion() == null) {
                                this.p.sendMessage(ChatColor.DARK_RED + "Impossible d'effectuer cette action car vous n'êtes pas dans la salle de réanimation.");
                                return false;
                            }
                        }
                        if (this.pData.selectedJob.isOutOfService()) {
                            if (this.p.getInventory().getHelmet() != null && !Jobs.isJobClothesItem(this.p.getInventory().getHelmet())) {
                                this.pData.helmet = ItemUtils.getItem(this.p.getInventory().getHelmet().getTypeId(), this.p
                                        .getInventory().getHelmet().getData().getData(), this.p.getInventory().getHelmet().getAmount(), null, null);
                            } else {
                                this.pData.helmet = null;
                            }
                            if (this.p.getInventory().getChestplate() != null && !Jobs.isJobClothesItem(this.p.getInventory().getChestplate())) {
                                this.pData.chestplate = ItemUtils.getItem(this.p.getInventory().getChestplate().getTypeId(), this.p
                                        .getInventory().getChestplate().getData().getData(), this.p.getInventory().getChestplate().getAmount(), null, null);
                            } else {
                                this.pData.chestplate = null;
                            }
                            if (this.p.getInventory().getLeggings() != null && !Jobs.isJobClothesItem(this.p.getInventory().getLeggings())) {
                                this.pData.leggings = ItemUtils.getItem(this.p.getInventory().getLeggings().getTypeId(), this.p
                                        .getInventory().getLeggings().getData().getData(), this.p.getInventory().getLeggings().getAmount(), null, null);
                            } else {
                                this.pData.leggings = null;
                            }
                            if (this.p.getInventory().getBoots() != null && !Jobs.isJobClothesItem(this.p.getInventory().getBoots())) {
                                this.pData.boots = ItemUtils.getItem(this.p.getInventory().getBoots().getTypeId(), this.p
                                        .getInventory().getBoots().getData().getData(), this.p.getInventory().getBoots().getAmount(), null, null);
                            } else {
                                this.pData.boots = null;
                            }

                            if (InventoryUtils.getGpb(p).getType() != Material.AIR && !Jobs.isJobClothesItem(InventoryUtils.getGpb(p))) {
                                this.pData.gpb = ItemUtils.getItem(InventoryUtils.getGpb(p).getTypeId(), InventoryUtils.getGpb(p).getData().getData(), InventoryUtils.getGpb(p).getAmount(), null, null);
                            } else {
                                this.pData.gpb = null;
                            }

                            this.pData.selectedJob.setOutOfService(false);
                            this.pData.selectedJob.equipClothes();
                            this.p.sendMessage(ChatColor.GREEN + "Le mode hors service est désactivé !");
                            return true;
                        }
                        this.p.sendMessage(ChatColor.RED + "Vous êtes déjà apte a exercer vos fonctions !");
                    } else {
                        this.p.sendMessage(ChatColor.RED + "Le mode service est désactivé pour ce métier !");
                    }
                if (args[0].equalsIgnoreCase("tacpt")) {
                    Taxi.getRequests().acceptRequest(this.p);
                    return true;
                }
                if (args[0].equalsIgnoreCase("tign")) {
                    Taxi.getRequests().removeRequest(this.p);
                    return true;
                }
                if (args[0].equalsIgnoreCase("tstp")) {
                    Taxi.getRequests().stopAcceptedRequest(this.p);
                    return true;
                }
                if (args[0].equalsIgnoreCase("taddrg")) {
                    if (!sender.hasPermission("cylrp.taxi.gestion")) {
                        MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                        return false;
                    }
                    String name = args[3];
                    String displayName = args[4];
                    int id = Integer.parseInt(args[5].split(":")[0]);
                    byte data = 0;
                    if ((args[5].split(":")).length > 1)
                        data = Byte.parseByte(args[5].split(":")[1]);
                    Regions.setRegion(name, displayName, id, data, this.p);
                    return true;
                }
                if (args[0].equalsIgnoreCase("trmvrg")) {
                    if (!sender.hasPermission("cylrp.taxi.gestion")) {
                        MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                        return false;
                    }
                    String name = args[3];
                    Regions.removeRegion(name, this.p);
                    return true;
                }
                if (!this.pData.selectedJob.isOutOfService()) {
                    if (args[0].equalsIgnoreCase("mo"))
                        Missions.open(this.p);
                    if (args[0].equalsIgnoreCase("ma")) {
                        final PlayerData data = PlayerData.getPlayerData(this.p.getName());
                        Missions missions = (Missions) data.selectedJob;
                        if (missions.getInProgress() != null) {
                            GeneralType gt = (GeneralType) missions.getInProgress().getType();
                            gt.informations(this.p.getName());
                        } else {
                            this.p.sendMessage(ChatColor.DARK_RED + "Aucune mission en cours.");
                        }
                    }
                    if (args[0].equalsIgnoreCase("mcte")) {
                        if (!sender.hasPermission("cylrp.missions.create")) {
                            MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                            return false;
                        }
                        Missions.createThread(this.p);
                    }
                    if (args[0].equalsIgnoreCase("mcpy")) {
                        String uuid = args[2];
                        String job = args[3];
                        if (!sender.hasPermission("cylrp.missions.create")) {
                            MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                            return false;
                        }
                        if (Missions.copy(uuid, job)) {
                            this.p.sendMessage(ChatColor.GREEN + "La mission a bien été copié");
                        } else {
                            this.p.sendMessage(ChatColor.RED + "Copie de la mission échouée");
                        }
                    }
                    if (args[0].equalsIgnoreCase("mabn")) {
                        final PlayerData data = PlayerData.getPlayerData(this.p.getName());
                        Missions missions = (Missions) data.selectedJob;
                        if (missions.getInProgress() != null) {
                            GeneralType gt = (GeneralType) missions.getInProgress().getType();
                            if (gt.getTask() != null)
                                gt.getTask().cancelTask();
                            gt.failed(this.p);
                        } else {
                            this.p.sendMessage(ChatColor.DARK_RED + "Aucune mission en cours.");
                        }
                    }
                    if (args[0].equalsIgnoreCase("mrmv")) {
                        String uuid = args[2];
                        String job = args[3];
                        if (uuid != null && job != null) {
                            if (!sender.hasPermission("cylrp.missions.remove")) {
                                MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                                return false;
                            }
                            Missions.deleteMission(uuid, Jobs.Job.getJobByName(job), this.p);
                        }
                    }
                    if (args[0].equalsIgnoreCase("padd")) {
                        if (!sender.hasPermission("cylrp.jobs.prison")) {
                            MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                            return false;
                        }
                        String region = args[2];
                        Prisons.add(region, this.p);
                    }
                    if (args[0].equalsIgnoreCase("prmv")) {
                        if (!sender.hasPermission("cylrp.jobs.prison")) {
                            MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                            return false;
                        }
                        String region = args[2];
                        Prisons.remove(region, this.p);
                    }
                    if (args[0].equalsIgnoreCase("rbl") &&
                            args[1].equalsIgnoreCase("op")) {
                        int price = Integer.parseInt(args[3]);
                        RebelleTerroriste rt = (RebelleTerroriste) this.pData.selectedJob;
                        rt.setPrixNegociation(this.p, price);
                    }
                    for (Jobs.Job j : Jobs.Job.values()) {
                        if (args[0].equalsIgnoreCase(j.getShortcut())) {
                            if (args[1].equalsIgnoreCase("ban")) {
                                if (j.getClasse().isInstance(this.pData.selectedJob)) {
                                    if (this.pData.selectedJob instanceof hasGrade) {
                                        String pseudo = args[2];
                                        long seconds = Long.parseLong(args[3]);
                                        PlayerData targetData = PlayerData.getPlayerData(pseudo);
                                        hasGrade senderGrade = (hasGrade) this.pData.selectedJob;

                                        if (targetData.selectedJob instanceof hasGrade) {
                                            hasGrade targetGrade = (hasGrade) targetData.selectedJob;
                                            if (senderGrade.getGrade().getGrades().gradeOrder(senderGrade.getGrade().getGrade().getNom()) <=
                                                    targetGrade.getGrade().getGrades().gradeOrder(targetGrade.getGrade().getGrade().getNom())) {
                                                this.p.sendMessage(ChatColor.RED + "Vous ne pouvez pas bannir votre supérieur");
                                                return false;
                                            }
                                        }

                                        if (senderGrade.getGrade().getGrades().getHg().contains(senderGrade.getGrade().getGrade().getNom())) {
                                            if (!this.p.getName().equalsIgnoreCase(pseudo)) {
                                                if (targetData == null) {
                                                    this.p.sendMessage(ChatColor.RED + "Données du joueur introuvable. Vérifiez le pseudo que vous avez entrez");
                                                    return false;
                                                }
                                                Jobs.ban(pseudo, j.getName(), seconds);
                                                this.p.sendMessage(ChatColor.GREEN + "Le joueur " + ChatColor.DARK_GREEN + pseudo + ChatColor.GREEN + " à désormais une interdiction d'exercerle métier " + ChatColor.DARK_GREEN + j
                                                        .getName());
                                                return true;
                                            }
                                            this.p.sendMessage(ChatColor.RED + "Vous ne pouvez pas exécuter cette commande sur vous.");
                                        } else {
                                            this.p.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
                                        }
                                    } else {
                                        this.p.sendMessage(ChatColor.RED + "Ce métier ne dispose pas de cette fonctionnalité");
                                    }
                                } else {
                                    this.p.sendMessage(ChatColor.RED + "Votre métier ne correspond pas");
                                }
                                return true;
                            }
                            if (args[1].equalsIgnoreCase("unban")) {
                                if (j.getClasse().isInstance(this.pData.selectedJob)) {
                                    if (this.pData.selectedJob instanceof hasGrade) {
                                        hasGrade grade = (hasGrade) this.pData.selectedJob;
                                        if (grade.getGrade().getGrades().getHg().contains(grade.getGrade().getGrade().getNom())) {
                                            String pseudo = args[2];
                                            if (!this.p.getName().equalsIgnoreCase(pseudo)) {
                                                PlayerData pseudoData = PlayerData.getPlayerData(pseudo);
                                                if (pseudoData == null) {
                                                    this.p.sendMessage(ChatColor.RED + "Données du joueur introuvable. Vérifiez le pseudo que vous avez entrez");
                                                    return false;
                                                }
                                                if (pseudoData.isForbidJob(j.getName())) {
                                                    Jobs.unban(pseudo, j.getName());
                                                    this.p.sendMessage(ChatColor.GREEN + "Le joueur " + ChatColor.DARK_GREEN + pseudo + ChatColor.GREEN + " peut à nouveau exercerle métier " + ChatColor.DARK_GREEN + j
                                                            .getName());
                                                    return true;
                                                }
                                                MessageUtils.sendMessage((CommandSender) this.p, "Ce joueur n'est pas banni de ce métier");
                                            } else {
                                                this.p.sendMessage(ChatColor.RED + "Vous ne pouvez pas exécuter cette commande sur vous.");
                                            }
                                        } else {
                                            this.p.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
                                        }
                                    } else {
                                        this.p.sendMessage(ChatColor.RED + "Ce métier ne dispose pas de cette fonctionnalité");
                                    }
                                } else {
                                    this.p.sendMessage(ChatColor.RED + "Votre métier ne correspond pas");
                                }
                                return true;
                            }
                            if (args[1].equalsIgnoreCase("bc")) {
                                if (j.getClasse().isInstance(this.pData.selectedJob)) {
                                    if (this.pData.selectedJob instanceof hasGrade) {
                                        hasGrade grade = (hasGrade) this.pData.selectedJob;
                                        if (grade.getGrade().getGrades().getHg().contains(grade.getGrade().getGrade().getNom())) {
                                            String[] msg = Arrays.<String>copyOfRange(args, 2, args.length);
                                            String message = StringUtils.join((Object[]) msg, " ");
                                            j.sendMessageAll(ChatColor.RED + this.pData.getPlayerName() + " : " + ChatColor.YELLOW + message);
                                        } else {
                                            this.p.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
                                        }
                                    } else {
                                        this.p.sendMessage(ChatColor.RED + "Ce métier ne dispose pas de cette fonctionnalité");
                                    }
                                } else {
                                    this.p.sendMessage(ChatColor.RED + "Votre métier ne correspond pas");
                                }
                            } else if (args[1].equalsIgnoreCase("rkup")) {
                                if (j.getClasse().isInstance(this.pData.selectedJob)) {
                                    if (this.pData.selectedJob instanceof hasGrade) {
                                        hasGrade grade = (hasGrade)this.pData.selectedJob;
                                        if (grade.getGrade().getGrades().getHg().contains(grade.getGrade().getGrade().getNom()) || this.p.hasPermission("cylrp.admin")) {
                                            String pseudo = args[2];
                                            if (!this.p.getName().equalsIgnoreCase(pseudo) || this.p.hasPermission("cylrp.admin")) {
                                                PlayerData pseudoData = PlayerData.getPlayerData(pseudo);
                                                if (pseudoData == null) {
                                                    this.p.sendMessage(ChatColor.RED + "Donndu joueur introuvable. Vle pseudo que vous avez entrez");
                                                    return false;
                                                }
                                                String oldjob = "";
                                                if (!j.getClasse().isInstance(pseudoData.selectedJob)) {
                                                    oldjob = pseudoData.selectedJob.getFeatures().getName();
                                                    JobsEvents.changePlayerJob(pseudoData, j.getName(), pseudo);
                                                }
                                                hasGrade pseudoP = (hasGrade)pseudoData.selectedJob;
                                                Grades grades = pseudoP.getGrade().getGrades();
                                                Grade next = grades.nextGrade(pseudoP.getGrade().getPoints());
                                                if (grade.getGrade().getGrades().getHg().contains(pseudoP.getGrade().getGrade().getNom()) && !this.p.hasPermission("cylrp.admin")) {
                                                    this.p.sendMessage(ChatColor.RED + "Vous ne pouvez pas rankup ce joueur en raison de son rang.");
                                                    if (!oldjob.isEmpty())
                                                        JobsEvents.changePlayerJob(pseudoData, oldjob, pseudo);
                                                    return false;
                                                }

                                                if (next == null) {
                                                    this.p.sendMessage(ChatColor.RED + "Ce joueur a déjà atteint le rang maximum.");
                                                    if (!oldjob.isEmpty())
                                                        JobsEvents.changePlayerJob(pseudoData, oldjob, pseudo);
                                                    return false;
                                                }

                                                pseudoP.getGrade().setPointSuppl(next.getPoints() - Grade.ConvertToPoints(pseudoP
                                                        .getGrade().getNbMissions(), pseudoP.getGrade().getXp(), pseudoP
                                                        .getGrade().getMalus(), pseudoP.getGrade().getTimer()));
                                                pseudoP.getGrade().checkRankUP();
                                                if (!oldjob.isEmpty())
                                                    JobsEvents.changePlayerJob(pseudoData, oldjob, pseudo);
                                                this.p.sendMessage(ChatColor.GREEN + "Ce joueur vient de rankup §2" + next.getNom());
                                                if (Bukkit.getPlayerExact(pseudo) != null)
                                                    Bukkit.getPlayerExact(pseudo).sendMessage(ChatColor.DARK_RED + " Vous avez promu " + next.getNom());
                                            } else {
                                                this.p.sendMessage(ChatColor.RED + "Vous ne pouvez pas excette commande sur vous.");
                                            }
                                        } else {
                                            this.p.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
                                        }
                                    } else {
                                        this.p.sendMessage(ChatColor.RED + "Ce métier ne dispose pas de cette fonctionnalité");
                                    }
                                } else {
                                    this.p.sendMessage(ChatColor.RED + "Votre métier ne correspond pas");
                                }
                            } else if (args[1].equalsIgnoreCase("dg")) {
                                if (j.getClasse().isInstance(this.pData.selectedJob)) {
                                    if (this.pData.selectedJob instanceof hasGrade) {
                                        hasGrade grade = (hasGrade)this.pData.selectedJob;
                                        if (grade.getGrade().getGrades().getHg().contains(grade.getGrade().getGrade().getNom()) || this.p.hasPermission("cylrp.admin")) {
                                            String pseudo = args[2];
                                            if (!this.p.getName().equalsIgnoreCase(pseudo) || this.p.hasPermission("cylrp.admin")) {
                                                PlayerData pseudoData = PlayerData.getPlayerData(pseudo);
                                                if (pseudoData == null) {
                                                    this.p.sendMessage(ChatColor.RED + "Données du joueur introuvable. Vérifier le pseudo que vous avez entrez");
                                                    return false;
                                                }
                                                String oldjob = "";
                                                if (!j.getClasse().isInstance(pseudoData.selectedJob)) {
                                                    oldjob = pseudoData.selectedJob.getFeatures().getName();
                                                    JobsEvents.changePlayerJob(pseudoData, j.getName(), pseudo);
                                                }
                                                hasGrade pseudoP = (hasGrade)pseudoData.selectedJob;
                                                Grades grades = pseudoP.getGrade().getGrades();
                                                Grade previous = grades.previousGrade(pseudoP.getGrade().getPoints());
                                                if (grade.getGrade().getGrades().gradeOrder(grade.getGrade().getGrade().getNom()) <= grade.getGrade().getGrades().gradeOrder(pseudoP.getGrade().getGrade().getNom()) &&
                                                        !this.p.hasPermission("cylrp.admin")) {
                                                    this.p.sendMessage(ChatColor.RED + "Vous ne pouvez pas downgrade ce joueur en raison de son rang.");
                                                    if (!oldjob.isEmpty())
                                                        JobsEvents.changePlayerJob(pseudoData, oldjob, pseudo);
                                                    return false;
                                                }
                                                if (previous == null) {
                                                    this.p.sendMessage(ChatColor.RED + "Ce joueur a déjà le rang le plus bas.");
                                                    if (!oldjob.isEmpty())
                                                        JobsEvents.changePlayerJob(pseudoData, oldjob, pseudo);
                                                    return false;
                                                }
                                                pseudoP.getGrade().setPointSuppl(previous.getPoints() - Grade.ConvertToPoints(pseudoP
                                                        .getGrade().getNbMissions(), pseudoP.getGrade().getXp(), pseudoP
                                                        .getGrade().getMalus(), pseudoP.getGrade().getTimer()));
                                                pseudoP.getGrade().checkRankUP();
                                                if (!oldjob.isEmpty())
                                                    JobsEvents.changePlayerJob(pseudoData, oldjob, pseudo);
                                                this.p.sendMessage(ChatColor.GREEN + "Ce joueur vient d'être rétrogradé en §2" + previous.getNom());
                                                if (Bukkit.getPlayerExact(pseudo) != null)
                                                    Bukkit.getPlayerExact(pseudo).sendMessage(ChatColor.DARK_RED + " Vous avez été rétrogradé en " + previous.getNom());
                                            } else {
                                                this.p.sendMessage(ChatColor.RED + "Vous ne pouvez pas exécuter cette commande sur vous.");
                                            }
                                        } else {
                                            this.p.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
                                        }
                                    } else {
                                        this.p.sendMessage(ChatColor.RED + "Ce métier ne dispose pas de cette fonctionnalité");
                                    }
                                } else {
                                    this.p.sendMessage(ChatColor.RED + "Votre métier ne correspond pas");
                                }
                            }
                        }
                    }
                } else {
                    this.p.sendMessage(ChatColor.DARK_RED + "Impossible d'exécuter cette action car vous êtes hors service !");
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("jobs")) {
            if (Missions.isPlayerCreatingMissions(this.p))
                Missions.creatingMissions.put(this.p, args);
            if (args.length == 0) {
                MessageUtils.sendMessage((CommandSender) this.p, this.commands.getCommands());
            } else {
                if (args[0].equalsIgnoreCase("help")) {
                    int page = Integer.parseInt(args[1]);
                    MessageUtils.sendMessage((CommandSender) this.p, this.commands.getCommands(page));
                    return false;
                }
                if (args[0].equalsIgnoreCase("clothes"))
                    this.pData.selectedJob.getFeatures().openClothesInventory(this.p.getName());
                if (args[0].equalsIgnoreCase("setPoints")) {
                    if (!sender.hasPermission("cylrp.jobs.setpoints")) {
                        MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                        return false;
                    }
                    if (args[1] != null && args[2] != null) {
                        int quantity = Integer.parseInt(args[1]);
                        String target = args[2];
                        final Player tgt = Main.INSTANCE.getServer().getPlayerExact(target);
                        if (tgt != null) {
                            final PlayerData data = PlayerData.getPlayerData(tgt.getName());
                            if (data.selectedJob instanceof hasGrade) {
                                hasGrade grade = (hasGrade) data.selectedJob;
                                grade.getGrade().setPointSuppl(grade.getGrade().getPointSuppl() + quantity);
                                MessageUtils.sendMessageFromConfig(sender, "jobs-sended-points");
                            } else {
                                MessageUtils.sendMessageFromConfig(sender, "jobs-cannot-send-points");
                            }
                        } else {
                            MessageUtils.sendMessageFromConfig(sender, "user-invalid");
                        }
                    }
                    return false;
                }
                if (args[0].equalsIgnoreCase("demenotter")) {
                    if (!sender.hasPermission("cylrp.jobs.demenotter")) {
                        MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                        return false;
                    }
                    if (args[1] != null) {
                        String target = args[1];
                        final Player tgt = Main.INSTANCE.getServer().getPlayerExact(target);
                        if (tgt != null) {
                            final PlayerData data = PlayerData.getPlayerData(tgt.getName());
                            if (data.getMenotte()) {
                                for (PotionEffect potion : tgt.getActivePotionEffects())
                                    tgt.removePotionEffect(potion.getType());
                                data.setMenotte(false, null);
                                this.p.sendMessage(ChatColor.GREEN + "Joueur démenotté !");
                            } else {
                                this.p.sendMessage(ChatColor.RED + "Ce joueur n'est pas menotté !");
                            }
                        } else {
                            MessageUtils.sendMessageFromConfig(sender, "user-invalid");
                        }
                    }
                    return false;
                }
                if (args[0].equalsIgnoreCase("menotter")) {
                    if (!sender.hasPermission("cylrp.jobs.menotter")) {
                        MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                        return false;
                    }
                    if (args[1] != null) {
                        String target = args[1];
                        final Player tgt = Main.INSTANCE.getServer().getPlayerExact(target);
                        if (tgt != null) {
                            final PlayerData data = PlayerData.getPlayerData(tgt.getName());
                            if (!data.getMenotte()) {
                                tgt.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 6000, 10));
                                tgt.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 6000, 10));
                                final Location location = this.p.getLocation().clone();
                                data.setMenotte(true, null);
                                new TaskCreator(new CustomRunnable() {
                                    private Location LastLocation = location;

                                    public void customRun() {
                                        if (!data.getMenotte())
                                            cancel();
                                        if (CommandJobs.this.p == null || !CommandJobs.this.p.isOnline())
                                            cancel();
                                        tgt.teleport(this.LastLocation);
                                    }
                                }, false, 0L, 10L);
                                this.p.sendMessage(ChatColor.GREEN + "Joueur menotté !");
                            } else {
                                this.p.sendMessage(ChatColor.RED + "Ce joueur est déjà menotté !");
                            }
                        } else {
                            MessageUtils.sendMessageFromConfig(sender, "user-invalid");
                        }
                    }
                    return false;
                }
                if (args[0].equalsIgnoreCase("change")) {
                    if (!sender.hasPermission("cylrp.jobs.change") && !(this.pData.selectedJob instanceof CanChangeJob)) {
                        MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                        return false;
                    }
                    String job = args[1];
                    String playername = args[2];
                    if (this.pData.selectedJob instanceof CanChangeJob) {
                        CanChangeJob change = (CanChangeJob) this.pData.selectedJob;
                        if (!change.jobs().contains(job)) {
                            MessageUtils.sendMessage((CommandSender) this.p, "Vous n'avez pas accès à ce métier");
                            return false;
                        }
                        if (this.pData.selectedJob instanceof hasGrade) {
                            hasGrade grade = (hasGrade) this.pData.selectedJob;
                            if (!grade.getGrade().getGrades().getHg().contains(grade.getGrade().getGrade().getNom())) {
                                MessageUtils.sendMessage((CommandSender) this.p, "Vous n'avez pas la permission d'exécuter cette commande");
                                return false;
                            }
                        }
                    }
                    if (job != null && playername != null)
                        if (Jobs.Job.isJob(job)) {
                            final PlayerData data = PlayerData.getPlayerData(playername);
                            if (data != null) {
                                JobsEvents.changePlayerJob(data, job, playername);
                                this.p.sendMessage(ChatColor.GREEN + "Le métier du joueur " + ChatColor.DARK_GREEN + playername + ChatColor.GREEN + " vient d'être changé.");
                            } else {
                                this.p.sendMessage(ChatColor.RED + "Données du joueur inexistante. Ce joueur n'existe peut-être pas.");
                            }
                        } else {
                            this.p.sendMessage(ChatColor.RED + "Ce métier n'existe pas !");
                        }
                }
                if (args[0].equalsIgnoreCase("keep")) {
                    if (!sender.hasPermission("cylrp.jobs.change")) {
                        MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                        return false;
                    }
                    String playername = args[1];
                    if (playername != null) {
                        final PlayerData data = PlayerData.getPlayerData(playername);
                        if (data != null) {
                            data.keepJob = !data.keepJob;
                            if (data.keepJob) {
                                this.p.sendMessage(ChatColor.GREEN + "La préservation du métier pour le joueur " + ChatColor.DARK_GREEN + playername + ChatColor.GREEN + " a été activé.");
                            } else {
                                this.p.sendMessage(ChatColor.GREEN + "La préservation du métier pour le joueur " + ChatColor.DARK_GREEN + playername + ChatColor.RED + " a été désactivé.");
                            }
                        } else {
                            this.p.sendMessage(ChatColor.RED + "Données du joueur inexistante. Ce joueur n'existe peut-être pas.");
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("punition")) {
                    if (!sender.hasPermission("cylrp.jobs.punite")) {
                        MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                        return false;
                    }
                    String job = args[1];
                    String playername = args[2];
                    if (job != null && playername != null)
                        if (Jobs.Job.isJob(job)) {
                            final PlayerData data = PlayerData.getPlayerData(playername);
                            if (data != null) {
                                Jobs.ban(playername, job, 86400L);
                                this.p.sendMessage(ChatColor.GREEN + "Le joueur " + ChatColor.DARK_GREEN + playername + ChatColor.GREEN + " à désormais une interdiction d'exercer le métier " + ChatColor.DARK_GREEN + job);
                            } else {
                                this.p.sendMessage(ChatColor.RED + "Données du joueur inexistante. Ce joueur n'existe peut-être pas.");
                            }
                        } else {
                            this.p.sendMessage(ChatColor.RED + "Ce métier n'existe pas !");
                        }
                }
                if (args[0].equalsIgnoreCase("removePoints")) {
                    if (!sender.hasPermission("cylrp.jobs.removepoints")) {
                        MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                        return false;
                    }
                    if (args[1] != null && args[2] != null) {
                        int quantity = Integer.parseInt(args[1]);
                        String target = args[2];
                        final Player tgt = Main.INSTANCE.getServer().getPlayerExact(target);
                        if (tgt != null) {
                            final PlayerData data = PlayerData.getPlayerData(tgt.getName());
                            if (data.selectedJob instanceof hasGrade) {
                                hasGrade grade = (hasGrade) data.selectedJob;
                                grade.getGrade().setPointSuppl(grade.getGrade().getPointSuppl() - quantity);
                                MessageUtils.sendMessageFromConfig(sender, "jobs-points-removed");
                            } else {
                                MessageUtils.sendMessageFromConfig(sender, "jobs-cannot-remove-points");
                            }
                        } else {
                            MessageUtils.sendMessageFromConfig((CommandSender) this.p, "jobs-cannot-remove-points");
                        }
                    }
                    return false;
                }
                if (args[0].equalsIgnoreCase("current")) {
                    if (args.length == 2 && this.p.hasPermission("cylrp.staff")) {
                        String pseudo = args[1];
                        final PlayerData data = PlayerData.getPlayerData(pseudo);
                        if (data.selectedJob != null) {
                            this.p.sendMessage("§cSon métier : §d" + data.selectedJob.getFeatures().getDisplayName());
                        } else {
                            this.p.sendMessage("§cCe jouer n'a pas de métier");
                        }
                        return false;
                    }
                    if (this.pData.selectedJob != null) {
                        this.p.sendMessage("§cVotre métier : §d" + this.pData.selectedJob.getFeatures().getDisplayName());
                    } else {
                        this.p.sendMessage("§cVous n'avez pas de métier");
                    }
                    return false;
                }
                if (args[0].equalsIgnoreCase("player")) {
                    if (!this.p.hasPermission("cylrp.jobs.change")) {
                        MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                        return false;
                    }
                    String playername = args[1];
                    if (playername != null) {
                        PlayerData plData = PlayerData.getPlayerData(playername);
                        if (plData != null) {
                            if (plData.selectedJob != null) {
                                this.p.sendMessage(ChatColor.AQUA.toString() + ChatColor.MAGIC + "&" + ChatColor.DARK_AQUA + "------ Informations sur le joueur ------" + ChatColor.AQUA.toString() + ChatColor.MAGIC + "&");
                                this.p.sendMessage(ChatColor.AQUA + "Métier : " + ChatColor.DARK_AQUA + plData.selectedJob.getFeatures().getDisplayName());
                                if (plData.selectedJob instanceof hasGrade) {
                                    hasGrade grade = (hasGrade) plData.selectedJob;
                                    JobGrades Jg = grade.getGrade();
                                    this.p.sendMessage(ChatColor.AQUA + "Rang: " + ChatColor.DARK_AQUA + Jg.getGrade().getNom());
                                    this.p.sendMessage(ChatColor.AQUA + "Nombre de points: " + ChatColor.DARK_AQUA + Jg.getPoints());
                                    this.p.sendMessage(ChatColor.AQUA + "Nombre de missions: " + ChatColor.DARK_AQUA + Jg.getNbMissions());
                                    this.p.sendMessage(ChatColor.AQUA + "Points malus: " + ChatColor.DARK_AQUA + Jg.getMalus());
                                }
                                this.p.sendMessage(ChatColor.AQUA + "----------------------------------------");
                            } else {
                                this.p.sendMessage("§cMétier du joueur introuvable");
                            }
                        } else {
                            this.p.sendMessage("§cDonnées du joueur introuvables");
                        }
                    }
                    return false;
                }
                if (args[0].equalsIgnoreCase("grade")) {
                    if (this.pData.selectedJob instanceof hasGrade) {
                        hasGrade grade = (hasGrade) this.pData.selectedJob;
                        JobGrades Jg = grade.getGrade();
                        this.p.sendMessage(ChatColor.RED.toString() + ChatColor.MAGIC + "&" + ChatColor.GOLD + "------ Stats ------" + ChatColor.RED.toString() + ChatColor.MAGIC + "&");
                        this.p.sendMessage(ChatColor.GREEN + "Votre rang: " + ChatColor.DARK_GREEN + Jg.getGrade().getNom());
                        this.p.sendMessage(ChatColor.RED + "Vos points: " + ChatColor.DARK_RED + Jg.getPoints());
                        if (Jg.getGrades().nextGrade(Jg.getPoints()) != null) {
                            this.p.sendMessage(ChatColor.GREEN + "Prochain rang: " + ChatColor.DARK_GREEN + Jg.getGrades().nextGrade(Jg.getPoints()).getNom());
                            this.p.sendMessage(ChatColor.GREEN + "Points du prochain rang: " + ChatColor.DARK_GREEN + Jg.getGrades().nextGrade(Jg.getPoints()).getPoints());
                        }
                        if (this.pData.selectedJob instanceof Missions)
                            this.p.sendMessage(ChatColor.AQUA + "Nombre de missions: " + ChatColor.DARK_AQUA + Jg.getNbMissions());
                        this.p.sendMessage(ChatColor.YELLOW + "Points malus: " + ChatColor.GOLD + Jg.getMalus());
                        this.p.sendMessage(ChatColor.GOLD + "-------------------");
                    } else {
                        this.p.sendMessage("§cVotre métier actuel n'est pas compatible avec le système de grades");
                    }
                    return false;
                }
                String metier = args[0];
                if (metier.equalsIgnoreCase("Taxi")) {
                    if (args[1].equalsIgnoreCase("accept")) {
                        Taxi.getRequests().acceptRequest(this.p);
                    } else if (args[1].equalsIgnoreCase("ignore")) {
                        Taxi.getRequests().removeRequest(this.p);
                    } else if (args[1].equalsIgnoreCase("stop")) {
                        Taxi.getRequests().stopAcceptedRequest(this.p);
                    } else if (args[1].equalsIgnoreCase("add")) {
                        if (!sender.hasPermission("cylrp.taxi.gestion")) {
                            MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                            return false;
                        }
                        if (args[2].equalsIgnoreCase("region")) {
                            String name = args[3];
                            String displayName = args[4];
                            int id = Integer.parseInt(args[5].split(":")[0]);
                            byte data = 0;
                            if ((args[5].split(":")).length > 1)
                                data = Byte.parseByte(args[5].split(":")[1]);
                            Regions.setRegion(name, displayName, id, data, this.p);
                        }
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        if (!sender.hasPermission("cylrp.taxi.gestion")) {
                            MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                            return false;
                        }
                        if (args[2].equalsIgnoreCase("region")) {
                            String name = args[3];
                            Regions.removeRegion(name, this.p);
                        }
                    }
                    return true;
                }

                if (args[0].equalsIgnoreCase("out")) {
                    if (args[1].equalsIgnoreCase("service"))
                        if (args[2].equalsIgnoreCase("on")) {
                            if (this.pData.selectedJob.getFeatures().hasServiceFunction()) {
                                if (this.pData.selectedJob instanceof Missions) {
                                    Missions missions = (Missions) this.pData.selectedJob;
                                    if (missions.getInProgress() != null) {
                                        this.p.sendMessage(ChatColor.DARK_RED + "Impossible d'effectuer cette action car vous avez une mission qui est en cours.");
                                        return false;
                                    }
                                }
                                if (this.pData.selectedJob instanceof Douanier) {
                                    if (this.pData.getActuallyRegion() != null && !this.pData.getActuallyRegion().getId().contains("douane")) {
                                        this.p.sendMessage(ChatColor.DARK_RED + "Impossible d'effectuer cette action car vous n'êtes pas à la douane.");
                                        return false;
                                    }
                                    if (this.pData.getActuallyRegion() == null) {
                                        this.p.sendMessage(ChatColor.DARK_RED + "Impossible d'effectuer cette action car vous n'êtes pas à la douane.");
                                        return false;
                                    }
                                }
                                if (this.pData.selectedJob instanceof fr.karmaowner.jobs.Medecin) {
                                    if (this.pData.getActuallyRegion() != null && !this.pData.getActuallyRegion().getId().contains("medecin")) {
                                        this.p.sendMessage(ChatColor.DARK_RED + "Impossible d'effectuer cette action car vous n'êtes pas dans la salle de réanimation.");
                                        return false;
                                    }
                                    if (this.pData.getActuallyRegion() == null) {
                                        this.p.sendMessage(ChatColor.DARK_RED + "Impossible d'effectuer cette action car vous n'êtes pas dans la salle de réanimation.");
                                        return false;
                                    }
                                }
                                if (!this.pData.selectedJob.isOutOfService()) {
                                    this.pData.selectedJob.unequipClothes(pData, true, true);
                                    this.p.getInventory().setHelmet(this.pData.helmet);
                                    this.p.getInventory().setChestplate(this.pData.chestplate);
                                    this.p.getInventory().setLeggings(this.pData.leggings);
                                    this.p.getInventory().setBoots(this.pData.boots);
                                    InventoryUtils.setGpb(p,this.pData.gpb, false);
                                    this.pData.selectedJob.setOutOfService(true);
                                    this.p.sendMessage(ChatColor.GREEN + "Le mode hors service est désormais actif !");
                                } else {
                                    this.p.sendMessage(ChatColor.RED + "Vous êtes déjà hors service !");
                                }
                            } else {
                                this.p.sendMessage(ChatColor.RED + "Le mode service est désactivé pour ce métier !");
                            }
                        } else if (args[2].equalsIgnoreCase("off")) {
                            if (this.pData.selectedJob.getFeatures().hasServiceFunction()) {
                                if (this.pData.selectedJob instanceof Douanier) {
                                    if (this.pData.getActuallyRegion() != null && !this.pData.getActuallyRegion().getId().contains("douane")) {
                                        this.p.sendMessage(ChatColor.DARK_RED + "Impossible d'effectuer cette action car vous n'êtes pas à la douane.");
                                        return false;
                                    }
                                    if (this.pData.getActuallyRegion() == null) {
                                        this.p.sendMessage(ChatColor.DARK_RED + "Impossible d'effectuer cette action car vous n'êtes pas à la douane.");
                                        return false;
                                    }
                                }
                                if (this.pData.selectedJob instanceof fr.karmaowner.jobs.Medecin) {
                                    if (this.pData.getActuallyRegion() != null && !this.pData.getActuallyRegion().getId().contains("medecin")) {
                                        this.p.sendMessage(ChatColor.DARK_RED + "Impossible d'effectuer cette action car vous n'êtes pas dans la salle de réanimation.");
                                        return false;
                                    }
                                    if (this.pData.getActuallyRegion() == null) {
                                        this.p.sendMessage(ChatColor.DARK_RED + "Impossible d'effectuer cette action car vous n'êtes pas dans la salle de réanimation.");
                                        return false;
                                    }
                                }
                                if (this.pData.selectedJob.isOutOfService()) {
                                    if (this.p.getInventory().getHelmet() != null && !Jobs.isJobClothesItem(this.p.getInventory().getHelmet())) {
                                        this.pData.helmet = ItemUtils.getItem(this.p.getInventory().getHelmet().getTypeId(), this.p
                                                .getInventory().getHelmet().getData().getData(), this.p.getInventory().getHelmet().getAmount(), null, null);
                                    } else {
                                        this.pData.helmet = null;
                                    }
                                    if (this.p.getInventory().getChestplate() != null && !Jobs.isJobClothesItem(this.p.getInventory().getChestplate())) {
                                        this.pData.chestplate = ItemUtils.getItem(this.p.getInventory().getChestplate().getTypeId(), this.p
                                                .getInventory().getChestplate().getData().getData(), this.p.getInventory().getChestplate().getAmount(), null, null);
                                    } else {
                                        this.pData.chestplate = null;
                                    }
                                    if (this.p.getInventory().getLeggings() != null && !Jobs.isJobClothesItem(this.p.getInventory().getLeggings())) {
                                        this.pData.leggings = ItemUtils.getItem(this.p.getInventory().getLeggings().getTypeId(), this.p
                                                .getInventory().getLeggings().getData().getData(), this.p.getInventory().getLeggings().getAmount(), null, null);
                                    } else {
                                        this.pData.leggings = null;
                                    }

                                    if (this.p.getInventory().getBoots() != null && !Jobs.isJobClothesItem(this.p.getInventory().getBoots())) {
                                        this.pData.boots = ItemUtils.getItem(this.p.getInventory().getBoots().getTypeId(), this.p
                                                .getInventory().getBoots().getData().getData(), this.p.getInventory().getBoots().getAmount(), null, null);
                                    } else {
                                        this.pData.boots = null;
                                    }

                                    if (InventoryUtils.getGpb(p).getType() != Material.AIR) {
                                        this.pData.gpb = ItemUtils.getItem(InventoryUtils.getGpb(p).getTypeId(), InventoryUtils.getGpb(p).getData().getData(), InventoryUtils.getGpb(p).getAmount(), null, null);
                                    } else {
                                        this.pData.gpb = null;
                                    }


                                    this.pData.selectedJob.equipClothes();
                                    this.pData.selectedJob.setOutOfService(false);
                                    this.p.sendMessage(ChatColor.GREEN + "Le mode hors service est désactivé !");
                                } else {
                                    this.p.sendMessage(ChatColor.RED + "Vous êtes déjà apte a exercer vos fonctions !");
                                }
                            } else {
                                this.p.sendMessage(ChatColor.RED + "Le mode service est désactivé pour ce métier !");
                            }
                        }
                } else if (!this.pData.selectedJob.isOutOfService()) {
                    if (metier.equalsIgnoreCase("Missions"))
                        if (args[1].equalsIgnoreCase("open")) {
                            Missions.open(this.p);
                        } else if (args[1].equalsIgnoreCase("about")) {
                            final PlayerData data = PlayerData.getPlayerData(this.p.getName());
                            Missions missions = (Missions) data.selectedJob;
                            if (missions.getInProgress() != null) {
                                GeneralType gt = (GeneralType) missions.getInProgress().getType();
                                gt.informations(this.p.getName());
                            } else {
                                this.p.sendMessage(ChatColor.DARK_RED + "Aucune mission en cours.");
                            }
                        } else if (args[1].equalsIgnoreCase("create")) {
                            if (!sender.hasPermission("cylrp.missions.create")) {
                                MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                                return false;
                            }
                            Missions.createThread(this.p);
                        } else if (args[1].equalsIgnoreCase("copy")) {
                            String uuid = args[2];
                            String job = args[3];
                            if (!sender.hasPermission("cylrp.missions.create")) {
                                MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                                return false;
                            }
                            if (Missions.copy(uuid, job)) {
                                this.p.sendMessage(ChatColor.GREEN + "La mission a bien été copié");
                            } else {
                                this.p.sendMessage(ChatColor.RED + "Copie de la mission échouée");
                            }
                        } else if (args[1].equalsIgnoreCase("abandon")) {
                            final PlayerData data = PlayerData.getPlayerData(this.p.getName());
                            Missions missions = (Missions) data.selectedJob;
                            if (missions.getInProgress() != null) {
                                GeneralType gt = (GeneralType) missions.getInProgress().getType();
                                if (gt.getTask() != null)
                                    gt.getTask().cancelTask();
                                gt.failed(this.p);
                            } else {
                                this.p.sendMessage(ChatColor.DARK_RED + "Aucune mission en cours.");
                            }
                        } else if (args[1].equalsIgnoreCase("remove")) {
                            String uuid = args[2];
                            String job = args[3];
                            if (uuid != null && job != null) {
                                if (!sender.hasPermission("cylrp.missions.remove")) {
                                    MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                                    return false;
                                }
                                Missions.deleteMission(uuid, Jobs.Job.getJobByName(job), this.p);
                            }
                        }
                    if (args[0].equalsIgnoreCase("Prison")) {
                        if (!sender.hasPermission("cylrp.jobs.prison")) {
                            MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                            return false;
                        }
                        if (args[1].equalsIgnoreCase("add")) {
                            String region = args[2];
                            Prisons.add(region, this.p);
                        } else if (args[1].equalsIgnoreCase("remove")) {
                            String region = args[2];
                            Prisons.remove(region, this.p);
                        }
                    }
                    if (Jobs.Job.isJob(metier)) {
                        if (metier.equalsIgnoreCase("Rebelle") &&
                                args[1].equalsIgnoreCase("otage") &&
                                args[2].equalsIgnoreCase("prix")) {
                            int price = Integer.parseInt(args[3]);
                            RebelleTerroriste rt = (RebelleTerroriste) this.pData.selectedJob;
                            rt.setPrixNegociation(this.p, price);
                        }
                        for (Jobs.Job j : Jobs.Job.values()) {
                            if (args[0].equalsIgnoreCase(j.getName())) {
                                if (args[1].equalsIgnoreCase("ban")) {
                                    if (j.getClasse().isInstance(this.pData.selectedJob)) {
                                        if (this.pData.selectedJob instanceof hasGrade) {
                                            String pseudo = args[2];
                                            long seconds = Long.parseLong(args[3]);
                                            PlayerData targetData = PlayerData.getPlayerData(pseudo);
                                            hasGrade senderGrade = (hasGrade) this.pData.selectedJob;

                                            if (targetData.selectedJob instanceof hasGrade) {
                                                hasGrade targetGrade = (hasGrade) targetData.selectedJob;
                                                if (senderGrade.getGrade().getGrades().gradeOrder(senderGrade.getGrade().getGrade().getNom()) <=
                                                        targetGrade.getGrade().getGrades().gradeOrder(targetGrade.getGrade().getGrade().getNom())) {
                                                    this.p.sendMessage(ChatColor.RED + "Vous ne pouvez pas bannir votre supérieur");
                                                    return false;
                                                }
                                            }

                                            if (senderGrade.getGrade().getGrades().getHg().contains(senderGrade.getGrade().getGrade().getNom())) {
                                                if (!this.p.getName().equalsIgnoreCase(pseudo)) {
                                                    Jobs.ban(pseudo, j.getName(), seconds);
                                                    this.p.sendMessage(ChatColor.GREEN + "Le joueur " + ChatColor.DARK_GREEN + pseudo + ChatColor.GREEN + " à désormais une interdiction d'exercerle métier " + ChatColor.DARK_GREEN + j
                                                            .getName());
                                                    return true;
                                                }
                                                this.p.sendMessage(ChatColor.RED + "Vous ne pouvez pas exécuter cette commande sur vous.");
                                            } else {
                                                this.p.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
                                            }
                                        } else {
                                            this.p.sendMessage(ChatColor.RED + "Ce métier ne dispose pas de cette fonctionnalité");
                                        }
                                    } else {
                                        this.p.sendMessage(ChatColor.RED + "Votre métier ne correspond pas");
                                    }
                                    return true;
                                }
                                if (args[1].equalsIgnoreCase("uban")) {
                                    if (j.getClasse().isInstance(this.pData.selectedJob)) {
                                        if (this.pData.selectedJob instanceof hasGrade) {
                                            hasGrade grade = (hasGrade) this.pData.selectedJob;
                                            if (grade.getGrade().getGrades().getHg().contains(grade.getGrade().getGrade().getNom())) {
                                                String pseudo = args[2];
                                                if (!this.p.getName().equalsIgnoreCase(pseudo)) {
                                                    PlayerData pseudoData = PlayerData.getPlayerData(pseudo);
                                                    if (pseudoData == null) {
                                                        this.p.sendMessage(ChatColor.RED + "Données du joueur introuvable. Vérifiez le pseudo que vous avez entrez");
                                                        return false;
                                                    }
                                                    if (pseudoData.isForbidJob(j.getName())) {
                                                        Jobs.unban(pseudo, j.getName());
                                                        this.p.sendMessage(ChatColor.GREEN + "Le joueur " + ChatColor.DARK_GREEN + pseudo + ChatColor.GREEN + " peut à nouveau exercerle métier " + ChatColor.DARK_GREEN + j
                                                                .getName());
                                                        return true;
                                                    }
                                                    MessageUtils.sendMessage((CommandSender) this.p, "Ce joueur n'est pas banni de ce métier");
                                                } else {
                                                    this.p.sendMessage(ChatColor.RED + "Vous ne pouvez pas exécuter cette commande sur vous.");
                                                }
                                            } else {
                                                this.p.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
                                            }
                                        } else {
                                            this.p.sendMessage(ChatColor.RED + "Ce métier ne dispose pas de cette fonctionnalité");
                                        }
                                    } else {
                                        this.p.sendMessage(ChatColor.RED + "Votre métier ne correspond pas");
                                    }
                                    return true;
                                }
                                if (args[1].equalsIgnoreCase("broadcast")) {
                                    if (j.getClasse().isInstance(this.pData.selectedJob)) {
                                        if (this.pData.selectedJob instanceof hasGrade) {
                                            hasGrade grade = (hasGrade) this.pData.selectedJob;
                                            if (grade.getGrade().getGrades().getHg().contains(grade.getGrade().getGrade().getNom())) {
                                                String[] msg = Arrays.<String>copyOfRange(args, 2, args.length);
                                                String message = StringUtils.join((Object[]) msg, " ");
                                                j.sendMessageAll(ChatColor.RED + this.pData.getPlayerName() + " : " + ChatColor.YELLOW + message);
                                            } else {
                                                this.p.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
                                            }
                                        } else {
                                            this.p.sendMessage(ChatColor.RED + "Ce métier ne dispose pas de cette fonctionnalité");
                                        }
                                    } else {
                                        this.p.sendMessage(ChatColor.RED + "Votre métier ne correspond pas");
                                    }
                                }
                                else if (args[1].equalsIgnoreCase("rankup"))
                                {
                                    if (j.getClasse().isInstance(this.pData.selectedJob)) {
                                        if (this.pData.selectedJob instanceof hasGrade) {
                                             hasGrade grade = (hasGrade)this.pData.selectedJob;
                                             if (grade.getGrade().getGrades().getHg().contains(grade.getGrade().getGrade().getNom()) || this.p.hasPermission("cylrp.admin")) {
                                                String pseudo = args[2];
                                                if (!this.p.getName().equalsIgnoreCase(pseudo) || this.p.hasPermission("cylrp.admin")) {
                                                    PlayerData pseudoData = PlayerData.getPlayerData(pseudo);
                                                    if (pseudoData == null) {
                                                        this.p.sendMessage(ChatColor.RED + "Données du joueur introuvable. Verifier le pseudo que vous avez entrez");
                                                        return false;
                                                    }
                                                    String oldjob = "";
                                                    if (!j.getClasse().isInstance(pseudoData.selectedJob)) {
                                                        oldjob = pseudoData.selectedJob.getFeatures().getName();
                                                        JobsEvents.changePlayerJob(pseudoData, j.getName(), pseudo);
                                                    }
                                                    hasGrade pseudoP = (hasGrade)pseudoData.selectedJob;
                                                    Grades grades = pseudoP.getGrade().getGrades();
                                                    Grade next = grades.nextGrade(pseudoP.getGrade().getPoints());
                                                    if (grade.getGrade().getGrades().getHg().contains(pseudoP.getGrade().getGrade().getNom()) && !this.p.hasPermission("cylrp.admin")) {
                                                        this.p.sendMessage(ChatColor.RED + "Vous ne pouvez pas rankup ce joueur en raison de son rang.");
                                                        if (!oldjob.isEmpty())
                                                            JobsEvents.changePlayerJob(pseudoData, oldjob, pseudo);
                                                        return false;
                                                    }

                                                    if (next == null) {
                                                        this.p.sendMessage(ChatColor.RED + "Ce joueur a déjà atteint le rang maximum.");
                                                        if (!oldjob.isEmpty())
                                                            JobsEvents.changePlayerJob(pseudoData, oldjob, pseudo);
                                                        return false;
                                                    }

                                                    pseudoP.getGrade().setPointSuppl(next.getPoints() - Grade.ConvertToPoints(pseudoP
                                                            .getGrade().getNbMissions(), pseudoP.getGrade().getXp(), pseudoP
                                                            .getGrade().getMalus(), pseudoP.getGrade().getTimer()));
                                                    pseudoP.getGrade().checkRankUP();
                                                    if (!oldjob.isEmpty())
                                                        JobsEvents.changePlayerJob(pseudoData, oldjob, pseudo);
                                                    this.p.sendMessage(ChatColor.GREEN + "Ce joueur vient de rankup §2" + next.getNom());
                                                    if (Bukkit.getPlayerExact(pseudo) != null)
                                                        Bukkit.getPlayerExact(pseudo).sendMessage(ChatColor.DARK_RED + " Vous avez promu " + next.getNom());
                                                } else {
                                                    this.p.sendMessage(ChatColor.RED + "Vous ne pouvez pas exécuter cette commande sur vous.");
                                                }
                                            } else {
                                                this.p.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
                                            }
                                        } else {
                                            this.p.sendMessage(ChatColor.RED + "Ce métier ne dispose pas de cette fonctionnalité");
                                        }
                                    } else {
                                        this.p.sendMessage(ChatColor.RED + "Votre métier ne correspond pas");
                                    }
                                } else if (args[1].equalsIgnoreCase("downgrade")) {
                                if (j.getClasse().isInstance(this.pData.selectedJob)) {
                                    if (this.pData.selectedJob instanceof hasGrade) {
                                        hasGrade grade = (hasGrade)this.pData.selectedJob;
                                        if (grade.getGrade().getGrades().getHg().contains(grade.getGrade().getGrade().getNom()) || this.p.hasPermission("cylrp.admin")) {
                                            String pseudo = args[2];
                                            if (!this.p.getName().equalsIgnoreCase(pseudo) || this.p.hasPermission("cylrp.admin")) {
                                                PlayerData pseudoData = PlayerData.getPlayerData(pseudo);
                                                if (pseudoData == null) {
                                                    this.p.sendMessage(ChatColor.RED + "Données du joueur introuvable. Vérifier le pseudo que vous avez entrez");
                                                    return false;
                                                }
                                                String oldjob = "";
                                                if (!j.getClasse().isInstance(pseudoData.selectedJob)) {
                                                    oldjob = pseudoData.selectedJob.getFeatures().getName();
                                                    JobsEvents.changePlayerJob(pseudoData, j.getName(), pseudo);
                                                }
                                                hasGrade pseudoP = (hasGrade)pseudoData.selectedJob;
                                                Grades grades = pseudoP.getGrade().getGrades();
                                                Grade previous = grades.previousGrade(pseudoP.getGrade().getPoints());
                                                if (grade.getGrade().getGrades().gradeOrder(grade.getGrade().getGrade().getNom()) <= grade.getGrade().getGrades().gradeOrder(pseudoP.getGrade().getGrade().getNom()) &&
                                                        !this.p.hasPermission("cylrp.admin")) {
                                                    this.p.sendMessage(ChatColor.RED + "Vous ne pouvez pas downgrade ce joueur en raison de son rang.");
                                                    if (!oldjob.isEmpty())
                                                        JobsEvents.changePlayerJob(pseudoData, oldjob, pseudo);
                                                    return false;
                                                }
                                                if (previous == null) {
                                                    this.p.sendMessage(ChatColor.RED + "Ce joueur a déjà le rang le plus bas.");
                                                    if (!oldjob.isEmpty())
                                                        JobsEvents.changePlayerJob(pseudoData, oldjob, pseudo);
                                                    return false;
                                                }
                                                pseudoP.getGrade().setPointSuppl(previous.getPoints() - Grade.ConvertToPoints(pseudoP
                                                        .getGrade().getNbMissions(), pseudoP.getGrade().getXp(), pseudoP
                                                        .getGrade().getMalus(), pseudoP.getGrade().getTimer()));
                                                pseudoP.getGrade().checkRankUP();
                                                if (!oldjob.isEmpty())
                                                    JobsEvents.changePlayerJob(pseudoData, oldjob, pseudo);
                                                this.p.sendMessage(ChatColor.GREEN + "Ce joueur vient d'être rétrogradé en §2" + previous.getNom());
                                                if (Bukkit.getPlayerExact(pseudo) != null)
                                                    Bukkit.getPlayerExact(pseudo).sendMessage(ChatColor.DARK_RED + " Vous avez été rétrogradé en " + previous.getNom());
                                            } else {
                                                this.p.sendMessage(ChatColor.RED + "Vous ne pouvez pas exécuter cette commande sur vous.");
                                            }
                                        } else {
                                            this.p.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
                                        }
                                    } else {
                                        this.p.sendMessage(ChatColor.RED + "Ce métier ne dispose pas de cette fonctionnalité");
                                    }
                                } else {
                                    this.p.sendMessage(ChatColor.RED + "Votre métier ne correspond pas");
                                }
                            }
                        }
                        }
                        if (metier.equalsIgnoreCase("Pompier"))
                            if (!sender.hasPermission("cylrp.incendie.gestion")) {
                                MessageUtils.sendMessageFromConfig(sender, "cylrp-not-permission");
                                return false;
                            }
                        if (args[0].equalsIgnoreCase("metier") &&
                                args[1].equalsIgnoreCase("raccourci"))
                            for (Jobs.Job j : Jobs.Job.values())
                                this.p.sendMessage("§9" + j.getName() + " => " + j.getShortcut());
                    }
                } else {
                    this.p.sendMessage(ChatColor.DARK_RED + "Impossible d'exécuter cette action car vous êtes hors service !");
                }
            }
        }
        return false;
    }

    public void displayHelp() {
        this.commands.addCommand("help <page>", "Affiche la liste des commandes");
        this.commands.addCommand("force death", "Forcer la mort");
        this.commands.addCommand("metier raccourci", "Affiche le nom de raccourci de tous les métiers (nécessaire pour exécuter certaines commandes)");
        this.commands.addCommand("setPoints <quantité> <username>", "Ajouter des points de rang à un joueur");
        this.commands.addCommand("removePoints <quantité> <username>", "Retirer des points de rang à un joueur");
        this.commands.addCommand("current", "Affiche le métier actuelle");
        this.commands.addCommand("grade", "Affiche son grade");
        this.commands.addCommand("out service on", "Se mettre en service (job)");
        this.commands.addCommand("out service off", "Se mettre hors service (job)");
        this.commands.addCommand("missions open", "Ouvre le gui pour voir les missions");
        this.commands.addCommand("prison add <nomRegion>", "Ajoute une region dans la liste des prisons");
        this.commands.addCommand("prison remove <nomRegion>", "Retire une region de la liste des prisons");
        this.commands.addCommand("rebelle otage <prix demandé>", "Fixe le prix de négocation d'une prise d'otage");
        this.commands.addCommand("pompier add region <nom region> <nature incendie> <temps>", "Ajoute une region dans la liste des regions d'incendie");
        this.commands.addCommand("pompier remove region <nom region>", "Retire une region de la liste des regions d'incendie");
        this.commands.addCommand("taxi add region <nom region> <NomAffichageGUI> <IdItemAffichageGui>", "Ajoute une region taxi");
        this.commands.addCommand("taxi remove region <nom region>", "Retire une region taxi");
        this.commands.addCommand("taxi accept", "Accepter la requête du chauffeur");
        this.commands.addCommand("taxi ignore", "Refuser la requête du chauffeur");
        this.commands.addCommand("taxi stop", "Dire au chauffeur de s'arrêter");
        this.commands.addCommand("missions abandon", "Abandonner une mission en cours");
        this.commands.addCommand("missions create", "Créer une mission");
        this.commands.addCommand("missions remove <uuid de la mission> <métier concerné>", "Supprimer une mission");
        this.commands.addCommand("missions stop", "Annuler une mission en cours de création");
        this.commands.addCommand("change <métier> <pseudo>", "Changer le métier d'un joueur");
        this.commands.addCommand("punition <métier> <pseudo>", "Interdit le joueur d'exercer le métier durant 1 jour");
        this.commands.addCommand("<métier> rankup <pseudo>", "Permet de rankup un joueur dans le métier pour les hauts-gradés");
        this.commands.addCommand("<métier> downgrade <pseudo>", "Permet de rétrograder un joueur dans le métier pour les hauts-gradés");
        this.commands.addCommand("<métier> ban <pseudo> <secondes>", "Interdit le joueur d'exercer le métier (pour les hauts-gradés)");
        this.commands.addCommandShortCut("<métier> unban <pseudo>", "Réautorise le joueur à exercer son métier");
        this.commands.addCommand("<métier> broadcast <msg>", "Permet d'envoyer un broadcast aux joueurs d'un métier pour les hauts-gradés");
        this.commands.addCommand("missions copy <uuid> <métier>", "Copie une mission en fonction de l'uuid dans le métier en question");
        this.commands.addCommand("missions about", "Donne des informations concernant la mission en cours");
        this.commands.addCommand("<métier> near", "Affichage sur un rayon de 100 blocs de vos confrères autours de vous");
        this.commands.addCommand("clothes", "Ouvre un inventaire avec les tenues du métier joué");
        //this.commands.addCommand("radio <message>", "Moyen de communication entre les joueurs d'un métier");
        this.commands.addCommand("attentat", "Commettre un attentat en étant terroriste");
        this.commands.addCommand("assaut", "Pour réaliser un assaut à la douane");
        this.commands.addCommand("reqmandat <pseudo> <nom du gang>", "Envoie une requête de démantèlement du gang donné en paramètre au Maire ou Président de l'Assemblée");
        this.commands.addCommand("reqmandataccpt <pseudo>", "Permet d'accepter la demande de mandat envoyé par l'individu donné en paramètre");
        this.commands.addCommand("mandatremove <Nom du gang>", "Supprime le mandat du gang donné en paramètre");
    }

    public void displayHelpAlias() {
        this.commands.addCommandShortCut("h <page>", "Affiche la liste des commandes");
        this.commands.addCommandShortCut("fd", "Forcer la mort");
        this.commands.addCommandShortCut("mrac", "Nom de raccourci de tous les métiers (nécessaire pour certaines commandes)");
        this.commands.addCommandShortCut("spb <quantité> <username>", "Ajouter des points de rang à un joueur");
        this.commands.addCommandShortCut("rpb <quantité> <username>", "Retirer des points de rang à un joueur");
        this.commands.addCommandShortCut("cur", "Affiche le métier actuelle");
        this.commands.addCommandShortCut("gd", "Affiche son grade");
        this.commands.addCommandShortCut("oson", "Se mettre en service (job)");
        this.commands.addCommandShortCut("osoff", "Se mettre hors service (job)");
        this.commands.addCommandShortCut("mo", "Ouvre le gui pour voir les missions");
        this.commands.addCommandShortCut("padd <nomRegion>", "Ajoute une region dans la liste des prisons");
        this.commands.addCommandShortCut("prmv <nomRegion>", "Retire une region de la liste des prisons");
        this.commands.addCommandShortCut("rbl op <prix demandé>", "Fixe le prix de négocation d'une prise d'otage");
        this.commands.addCommandShortCut("ppaddrg <nom region> <nature incendie> <temps>", "Ajoute une region dans la liste des regions d'incendie");
        this.commands.addCommandShortCut("pprmvrg <nom region>", "Retire une region de la liste des regions d'incendie");
        this.commands.addCommandShortCut("taddrg <nom region> <NomAffichageGUI> <IdItemAffichageGui>", "Ajoute une region taxi");
        this.commands.addCommandShortCut("trmvrg <nom region>", "Retire une region taxi");
        this.commands.addCommandShortCut("tacpt", "Accepter la requête du chauffeur");
        this.commands.addCommandShortCut("tign", "Refuser la requête du chauffeur");
        this.commands.addCommandShortCut("tstp", "Dire au chauffeur de s'arrêter");
        this.commands.addCommandShortCut("mabn", "Abandonner une mission en cours");
        this.commands.addCommandShortCut("mcte", "Créer une mission");
        this.commands.addCommandShortCut("mrmv <uuid de la mission> <métier concerné>", "Supprimer une mission");
        this.commands.addCommandShortCut("mstp", "Annuler une mission en cours de création");
        this.commands.addCommandShortCut("ch <métier raccourci> <pseudo>", "Changer le métier d'un joueur");
        this.commands.addCommandShortCut("pun <métier raccourci> <pseudo>", "Interdit le joueur d'exercer le métier durant 1 jour");
        this.commands.addCommandShortCut("<métier raccourci> rkup <pseudo>", "Permet de rankup un joueur dans le métier pour les hauts-gradés");
        this.commands.addCommandShortCut("<métier raccourci> dg <pseudo>", "Permet de rétrograder un joueur dans le métier pour les hauts-gradés");
        this.commands.addCommandShortCut("<métier raccourci> ban <pseudo> <secondes>", "Interdit le joueur d'exercer le métier (pour les hauts-gradés)");
        this.commands.addCommandShortCut("<métier raccourci> uban <pseudo>", "Réautorise le joueur à exercer son métier");
        this.commands.addCommandShortCut("<métier raccourci> bc <msg>", "Permet d'envoyer un broadcast aux joueurs d'un métier pour les hauts-gradés");
        this.commands.addCommandShortCut("mcpy <uuid> <métier>", "Copie une mission en fonction de l'uuid dans le métier en question");
        this.commands.addCommandShortCut("ma", "Donne des informations concernant la mission en cours");
        this.commands.addCommandShortCut("<métier ou raccourci métier> nr", "Affichage sur un rayon de 100 blocs de vos confrères autours de vous");
        this.commands.addCommandShortCut("clo", "Ouvre un inventaire avec les tenues du métier joué");
    }

    public void displayHelpMaire() {
        this.maireCmds.addCommand("help <page>", "Affiche la liste des commandes");
        this.maireCmds.addCommand("lois", "Affiche les lois");
        this.maireCmds.addCommand("garde invite <pseudo>", "Inviter un garde");
        this.maireCmds.addCommand("garde kick <pseudo>", "Exclure un garde");
        this.maireCmds.addCommand("garde accept", "Accepter l'invitation du Maire pour être garde");
        this.maireCmds.addCommand("annonce", "Annonce générale");
        this.maireCmds.addCommand("listGardes", "Affiche la liste des gardes");
        this.maireCmds.addCommand("msgGardes", "Envoie un message à tous les gardes");
        this.maireCmds.addCommand("add <loi>", "Ajoute une loi");
        this.maireCmds.addCommand("remove <numéro de la loi>", "Supprime une loi");
        this.maireCmds.addCommand("couvrefeu", "Décréter un couvre-feu");
        this.maireCmds.addCommand("identity name <pseudo> <prénom> <nom>", "Changer le nom et prénom d'un individu");
    }
}
