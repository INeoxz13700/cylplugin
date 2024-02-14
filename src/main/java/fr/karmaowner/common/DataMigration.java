package fr.karmaowner.common;

import fr.karmaowner.jobs.Jobs;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class DataMigration {
  SqlConnector db;
  
  public DataMigration(SqlConnector db) throws ClassNotFoundException, SQLException {
    this.db = db;
  }
  
  public Map<File, FileConfiguration> getAllFile(TypeFile typefile) {
    Map<File, FileConfiguration> files = new HashMap<>();
    File dir = typefile.getFile();
    if (dir != null && dir.listFiles() != null)
      for (File f : dir.listFiles()) {
        try {
          YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(f);
          if (yamlConfiguration != null)
            files.put(f, yamlConfiguration); 
        } catch (Exception e) {}
      }  
    return files;
  }
  
  public enum TypeFile {
    PLAYER("UserData"),
    GANG("Gangs"),
    COMPANY("Entreprises");
    
    private File dir;
    
    TypeFile(String dirname) {
      this.dir = new File(Main.INSTANCE.getDataFolder() + File.separator + dirname);
    }
    
    public File getFile() {
      return this.dir;
    }
  }
  
  public enum TypeParser {
    PLAYERDATA, GANGDATA, COMPANYDATA;
  }
  
  public void migrateFileDataToSql(TypeParser parser, FileConfiguration config, HashMap<String, String> datas) throws SQLException, UnsupportedEncodingException {
    PlayerDataParser PlayerParser;
    CompanyDataParser CompanyParser;
    GangDataParser GangParser;
    switch (parser) {
      case PLAYERDATA:
        PlayerParser = new PlayerDataParser(config);
        if (datas.get("pseudo") != null)
          PlayerParser.adapter(datas.get("pseudo")); 
        break;
      case COMPANYDATA:
        CompanyParser = new CompanyDataParser(config);
        CompanyParser.adapter();
        break;
      case GANGDATA:
        GangParser = new GangDataParser(config);
        if (datas.get("gangname") != null)
          GangParser.adapter(datas.get("gangname")); 
        break;
    } 
  }
  
  private class Parser {
    protected FileConfiguration config;
    
    protected String tablename;
    
    protected HashMap<String, String> fields = new HashMap<>();
    
    public Parser(String tablename, FileConfiguration config) {
      this.config = config;
      this.tablename = tablename;
    }
    
    public void addRemplacementFields(HashMap<String, String> flds) {
      this.fields = new HashMap<>();
      this.fields.putAll(flds);
    }
    
    public void adapter(HashMap<String, String> addData) throws SQLException, UnsupportedEncodingException {
      String record = "";
      HashMap<String, String> values = new HashMap<>();
      for (String fileField : this.fields.keySet()) {
        if (fileField.contains("key")) {
          if (this.config.getConfigurationSection(fileField) != null)
            for (String key : this.config.getConfigurationSection(fileField).getKeys(false)) {
              if (fileField.endsWith(key)) {
                determineType(key, values);
                continue;
              } 
              determineType(fileField.replaceAll("key", key), values);
            }  
          continue;
        } 
        if (this.config.getConfigurationSection(fileField) != null) {
          StringJoiner joiner3 = new StringJoiner(";");
          for (String key : this.config.getConfigurationSection(fileField).getKeys(false)) {
            StringJoiner joiner4 = new StringJoiner("");
            determineType(fileField + "." + key, joiner4);
            joiner3.add(key + "=" + joiner4.toString().replace("'", ""));
          } 
          values.put(fileField, "'" + (new String(joiner3.toString().getBytes(), "UTF-8")).replace("'", "''") + "'");
          continue;
        } 
        determineType(fileField, values);
      } 
      record = record + "INSERT INTO " + this.tablename + "(";
      StringJoiner joiner = new StringJoiner(",");
      for (String fieldname : values.keySet())
        joiner.add(this.fields.get(fieldname)); 
      for (String field : addData.keySet())
        joiner.add(field); 
      record = record + joiner.toString();
      record = record + ") ";
      record = record + "VALUES (";
      StringJoiner joiner2 = new StringJoiner(",");
      for (String val : values.values())
        joiner2.add(val); 
      if (joiner2.toString().isEmpty() || joiner2.length() < 2)
        return; 
      for (String value : addData.values())
        joiner2.add(value); 
      record = record + joiner2.toString();
      record = record + ")";
      Main.Log(record);
      DataMigration.this.db.update(record);
      Main.Log("Record succesfully executed !");
    }
    
    public void adapter() throws SQLException, UnsupportedEncodingException {
      String record = "";
      HashMap<String, String> values = new HashMap<>();
      HashMap<String, StringJoiner> valuesFromKeys = new HashMap<>();
      HashMap<String, StringJoiner> keysFromKeys = new HashMap<>();
      for (String fileField : this.fields.keySet()) {
        if (fileField.contains("key")) {
          String fileFieldWithoutKey = fileField.substring(0, fileField.indexOf(".key"));
          if (this.config.getConfigurationSection(fileFieldWithoutKey) != null)
            for (String key : this.config.getConfigurationSection(fileFieldWithoutKey).getKeys(false)) {
              if (fileField.endsWith(".key")) {
                values.put(fileField, key);
                continue;
              } 
              StringJoiner joiner = new StringJoiner("");
              determineType(fileField.replaceAll("key", key), joiner);
              String value = new String(joiner.toString().getBytes(), "UTF-8");
              String ky = fileField.substring(fileField.lastIndexOf(".") + 1, fileField.length());
              String recordId = fileField.replaceAll("key", key).substring(0, fileField.replaceAll("key", key).lastIndexOf("."));
              if (valuesFromKeys.get(recordId) == null) {
                StringJoiner sj = new StringJoiner(",");
                sj.add(new String(value.getBytes(), "UTF-8"));
                valuesFromKeys.put(recordId, sj);
              } else {
                ((StringJoiner)valuesFromKeys.get(recordId)).add(new String(value.getBytes(), "UTF-8"));
              } 
              if (keysFromKeys.get(recordId) == null) {
                StringJoiner sj = new StringJoiner(",");
                sj.add(ky);
                keysFromKeys.put(recordId, sj);
                continue;
              } 
              ((StringJoiner)keysFromKeys.get(recordId)).add(ky);
            }  
          continue;
        } 
        if (this.config.getConfigurationSection(fileField) != null) {
          StringJoiner joiner3 = new StringJoiner(";");
          for (String key : this.config.getConfigurationSection(fileField).getKeys(false)) {
            if (this.config.getConfigurationSection(fileField + "." + key) != null) {
              for (String key2 : this.config.getConfigurationSection(fileField + "." + key).getKeys(false)) {
                StringJoiner stringJoiner = new StringJoiner("");
                determineType(fileField + "." + key + "." + key2, stringJoiner);
                joiner3.add(key + "." + key2 + "=" + stringJoiner.toString().replace("'", ""));
              } 
              continue;
            } 
            StringJoiner joiner4 = new StringJoiner("");
            determineType(fileField + "." + key, joiner4);
            joiner3.add(key + "=" + joiner4.toString().replace("'", ""));
          } 
          values.put(fileField, "'" + (new String(joiner3.toString().getBytes(), "UTF-8")).replace("'", "''") + "'");
          continue;
        } 
        determineType(fileField, values);
      } 
      if (!valuesFromKeys.isEmpty()) {
        ArrayList<String> records = new ArrayList<>();
        for (Map.Entry<String, StringJoiner> keys : keysFromKeys.entrySet()) {
          String rcd = "INSERT INTO " + this.tablename + "(";
          StringJoiner joiner = new StringJoiner(",");
          for (String fieldname : values.keySet())
            joiner.add(this.fields.get(fieldname)); 
          StringJoiner joiner2 = new StringJoiner(",");
          for (String val : values.values())
            joiner2.add(val); 
          rcd = rcd + ((StringJoiner)keys.getValue()).merge(joiner).toString() + ") VALUES (";
          rcd = rcd + ((StringJoiner)valuesFromKeys.get(keys.getKey())).merge(joiner2).toString() + ")";
          records.add(rcd);
        } 
        for (String rcd : records) {
          Main.Log(rcd);
          DataMigration.this.db.update(rcd);
        } 
      } else if (!values.isEmpty()) {
        record = record + "INSERT INTO " + this.tablename + "(";
        StringJoiner joiner = new StringJoiner(",");
        for (String fieldname : values.keySet())
          joiner.add(this.fields.get(fieldname)); 
        record = record + joiner.toString();
        record = record + ") ";
        record = record + "VALUES (";
        StringJoiner joiner2 = new StringJoiner(",");
        for (String val : values.values())
          joiner2.add(val); 
        record = record + joiner2.toString();
        record = record + ")";
        if ((joiner2.toString().split(",")).length < 2)
          return; 
        DataMigration.this.db.update(record);
        Main.Log("Record succesfully executed !");
      } 
    }
    
    private void determineType(String fileField, HashMap<String, String> values) throws UnsupportedEncodingException {
      if (this.config.get(fileField) instanceof String) {
        values.put(fileField, "'" + (new String(this.config.getString(fileField).getBytes(), "UTF-8")).replace("'", "''") + "'");
      } else if (this.config.get(fileField) instanceof Boolean) {
        values.put(fileField, this.config.getBoolean(fileField) + "");
      } else if (this.config.get(fileField) instanceof Double) {
        values.put(fileField, this.config.getDouble(fileField) + "");
      } else if (this.config.get(fileField) instanceof Integer) {
        values.put(fileField, this.config.getInt(fileField) + "");
      } else if (this.config.get(fileField) instanceof ItemStack) {
        ItemStack item = this.config.getItemStack(fileField);
        if (item.getData() != null) {
          values.put(fileField, "'" + item.getTypeId() + ":" + item.getData().getData() + "'");
        } else {
          values.put(fileField, "'" + item.getTypeId() + ":0'");
        } 
      } else if (this.config.get(fileField) instanceof Long) {
        values.put(fileField, this.config.getLong(fileField) + "");
      } else if (this.config.get(fileField) instanceof java.util.List) {
        StringJoiner joiner2 = new StringJoiner(";");
        for (String v : this.config.getStringList(fileField))
          joiner2.add(v); 
        values.put(fileField, "'" + (new String(joiner2.toString().getBytes(), "UTF-8")).replace("'", "''") + "'");
      } 
    }
    
    private void determineType(String fileField, StringJoiner joiner) throws UnsupportedEncodingException {
      if (this.config.get(fileField) instanceof String) {
        joiner.add("'" + (new String(this.config.getString(fileField).getBytes(), "UTF-8")).replace("'", "''") + "'");
      } else if (this.config.get(fileField) instanceof Boolean) {
        joiner.add(this.config.getBoolean(fileField) + "");
      } else if (this.config.get(fileField) instanceof Double) {
        joiner.add(this.config.getDouble(fileField) + "");
      } else if (this.config.get(fileField) instanceof Integer) {
        joiner.add(this.config.getInt(fileField) + "");
      } else if (this.config.get(fileField) instanceof ItemStack) {
        ItemStack item = this.config.getItemStack(fileField);
        if (item.getData() != null) {
          joiner.add("'" + item.getTypeId() + ":" + item.getData().getData() + "'");
        } else {
          joiner.add("'" + item.getTypeId() + ":0'");
        } 
      } else if (this.config.get(fileField) instanceof Long) {
        joiner.add(this.config.getLong(fileField) + "");
      } else if (this.config.get(fileField) instanceof java.util.List) {
        StringJoiner joiner2 = new StringJoiner(";");
        for (String v : this.config.getStringList(fileField))
          joiner2.add(v); 
        joiner.add("'" + (new String(joiner2.toString().getBytes(), "UTF-8")).replace("'", "''") + "'");
      } 
    }
  }
  
  private class ProhibitionDataParser extends Parser {
    public ProhibitionDataParser(FileConfiguration config) {
      super("startProhibition", config);
      this.fields.put("data.startProhibition.key", "jobname");
      this.fields.put("data.startProhibition.key.timer", "timer");
    }
  }
  
  private class JobHasGradeDataParser extends Parser {
    public JobHasGradeDataParser(FileConfiguration config) {
      super("jobs_grade_data", config);
    }
    
    public void addReplacementFields(String jobname) {
      HashMap<String, String> flds = new HashMap<>();
      flds.put("data." + jobname + ".xp", "xp");
      flds.put("data." + jobname + ".malus", "malus");
      flds.put("data." + jobname + ".timer", "timer");
      flds.put("data." + jobname + ".missions", "missions");
      flds.put("data." + jobname + ".pointSuppl", "pointSuppl");
      addRemplacementFields(flds);
    }
  }
  
  private class JobMissionsDataParser extends Parser {
    public JobMissionsDataParser(FileConfiguration config) {
      super("jobs_missions_data", config);
    }
    
    public void addReplacementFields(String jobname) {
      HashMap<String, String> flds = new HashMap<>();
      flds.put("data.Missions." + jobname + ".MissionsFinished", "MissionsFinished");
      flds.put("data.Missions." + jobname + ".randomMissionsAvailable", "randomMissionsAvailable");
      flds.put("data.Missions." + jobname + ".startMission", "startMission");
      addRemplacementFields(flds);
    }
  }
  
  private class PlayerRankDataParser extends Parser {
    public PlayerRankDataParser(FileConfiguration config) {
      super("ranks", config);
      this.fields.put("data.rank.expiration", "expiration");
    }
  }
  
  private class PlayerGPSDataParser extends Parser {
    public PlayerGPSDataParser(FileConfiguration config) {
      super("player_gps_data", config);
      this.fields.put("GPS.X", "x");
      this.fields.put("GPS.Y", "y");
      this.fields.put("GPS.Z", "z");
      this.fields.put("GPS.N", "names");
    }
  }
  
  private class PlayerDataParser extends Parser {
    private DataMigration.ProhibitionDataParser parser1;
    
    private DataMigration.JobHasGradeDataParser parser2;
    
    private DataMigration.JobMissionsDataParser parser3;
    
    private DataMigration.PlayerGPSDataParser parser4;
    
    private DataMigration.PlayerRankDataParser parser5;
    
    public PlayerDataParser(FileConfiguration config) {
      super("players_data", config);
      this.fields.put("data.companyName", "companyName");
      this.fields.put("data.companyCategory", "companyCategory");
      this.fields.put("data.gangName", "gangName");
      this.parser1 = new DataMigration.ProhibitionDataParser(config);
      this.parser2 = new DataMigration.JobHasGradeDataParser(config);
      this.parser3 = new DataMigration.JobMissionsDataParser(config);
      this.parser4 = new DataMigration.PlayerGPSDataParser(config);
      this.parser5 = new DataMigration.PlayerRankDataParser(config);
      this.fields.put("data.keepJob", "keepJob");
      this.fields.put("data.selectedJob", "selectedJob");
      this.fields.put("data.tempNbVente", "tempNbVente");
      this.fields.put("data.NbVente", "NbVente");
      this.fields.put("data.coordonnees", "coordonnees");
      this.fields.put("data.lastJob", "lastJob");
      this.fields.put("data.teleport", "teleport");
      this.fields.put("data.armor.chestplate", "chestplate");
      this.fields.put("data.armor.helmet", "helmet");
      this.fields.put("data.armor.leggings", "leggings");
      this.fields.put("data.armor.boots", "boots");
      this.fields.put("data.actuallyRegion", "actuallyRegion");
      this.fields.put("data.lastName", "lastName");
      this.fields.put("data.Name", "Name");
    }
    
    public void adapter(String pseudo) throws SQLException, UnsupportedEncodingException {
      HashMap<String, String> addData = new HashMap<>();
      addData.put("pseudo", pseudo);
      adapter(addData);
      this.parser1.adapter(addData);
      for (Jobs.Job job : Jobs.Job.getJobs(Arrays.asList("militaire", "gendarme", "medecin", "gign", "pompier", "douanier"))) {
        HashMap<String, String> addData2 = new HashMap<>();
        addData2.putAll(addData);
        addData2.put("jobname", "'" + job.getName() + "'");
        this.parser2.addReplacementFields(job.getName());
        this.parser2.adapter(addData2);
      } 
      for (Jobs.Job job : Jobs.Job.getJobs(Arrays.asList("militaire", "gendarme", "gign"))) {
        HashMap<String, String> addData2 = new HashMap<>();
        addData2.putAll(addData);
        addData2.put("jobname", "'" + job.getName() + "'");
        this.parser3.addReplacementFields(job.getName());
        this.parser3.adapter(addData2);
      } 
      this.parser4.adapter(addData);
      this.parser5.adapter(addData);
    }
  }
  
  private class EggsDataParser extends Parser {
    public EggsDataParser(FileConfiguration config) {
      super("eggs_data", config);
      this.fields.put("Name", "CompanyName");
      this.fields.put("Eggs.key.TypeId", "TypeId");
      this.fields.put("Eggs.key.timeHatching", "timeHatching");
      this.fields.put("Eggs.key.State", "State");
      this.fields.put("Eggs.key.startedTime", "startedTime");
      this.fields.put("Eggs.key.name", "name");
    }
  }
  
  private class CompanyActivitiesParser extends Parser {
    private CompanyActivitiesParser parser;
    
    public CompanyActivitiesParser(String tablename, String compteur, FileConfiguration config, CompanyActivitiesParser parser) {
      super(tablename, config);
      this.fields.put("Name", "CompanyName");
      this.fields.put(compteur, compteur);
      this.parser = parser;
    }
    
    public void adapter() throws SQLException, UnsupportedEncodingException {
      super.adapter();
      if (this.parser != null)
        this.parser.adapter(); 
    }
  }
  
  private class CompanyDataParser extends Parser {
    private DataMigration.EggsDataParser parser1;
    
    private DataMigration.CompanyActivitiesParser parser2;
    
    public CompanyDataParser(FileConfiguration config) {
      super("company_data", config);
      this.fields.put("CoGerant", "CoGerant");
      this.fields.put("Secretaires", "Secretaires");
      this.fields.put("Stagiaires", "Stagiaires");
      this.fields.put("Gerant", "Gerant");
      this.fields.put("Salaries", "Salaries");
      this.fields.put("nbSalaries", "nbSalaries");
      this.fields.put("UsersRepartition", "UsersRepartition");
      this.fields.put("StagiairesRepartition", "StagiairesRepartition");
      this.fields.put("SecretairesRepartition", "SecretairesRepartition");
      this.fields.put("CommunityManagers", "CommunityManagers");
      this.fields.put("CoGerantRepartition", "CoGerantRepartition");
      this.fields.put("GerantRepartition", "GerantRepartition");
      this.fields.put("NbVenteTotal", "NbVenteTotal");
      this.fields.put("wonMoneyPerDay", "wonMoneyPerDay");
      this.fields.put("elapsedTimeQuota", "elapsedTimeQuota");
      this.fields.put("lastAttributiontime", "lastAttributiontime");
      this.fields.put("Revenues", "Revenues");
      this.fields.put("Level", "Level");
      this.fields.put("xpToReachForLevelUp", "xpToReachForLevelUp");
      this.fields.put("xpActually", "xpActually");
      this.fields.put("Categorie", "Categorie");
      this.fields.put("Name", "Name");
      this.fields.put("parcellesowned", "parcelles");
      this.parser1 = new DataMigration.EggsDataParser(config);
      this.parser2 = new DataMigration.CompanyActivitiesParser("company_elevage", "Animaux", config, null);
      this.parser2 = new DataMigration.CompanyActivitiesParser("company_agriculture", "Plantations", config, this.parser2);
      this.parser2 = new DataMigration.CompanyActivitiesParser("company_bucheron", "Bois", config, this.parser2);
      this.parser2 = new DataMigration.CompanyActivitiesParser("company_chasse", "Animaux", config, this.parser2);
      this.parser2 = new DataMigration.CompanyActivitiesParser("company_forgeron", "Transformations", config, this.parser2);
      this.parser2 = new DataMigration.CompanyActivitiesParser("company_metallurgie", "Objets", config, this.parser2);
      this.parser2 = new DataMigration.CompanyActivitiesParser("company_minage", "Minerais", config, this.parser2);
      this.parser2 = new DataMigration.CompanyActivitiesParser("company_peche", "Poissons", config, this.parser2);
      this.parser2 = new DataMigration.CompanyActivitiesParser("company_menuiserie", "Objets", config, this.parser2);
    }
    
    public void adapter() throws SQLException, UnsupportedEncodingException {
      super.adapter();
      this.parser1.adapter();
      this.parser2.adapter();
    }
  }
  
  private class GangDataParser extends Parser {
    public GangDataParser(FileConfiguration config) {
      super("Gang_data", config);
      this.fields.put("Gang.RankingPoints", "RankingPoints");
      this.fields.put("Gang.Chef", "Chef");
      this.fields.put("Gang.SousChefs", "SousChefs");
      this.fields.put("Gang.Membres", "Membres");
      this.fields.put("Gang.Ennemies", "Ennemies");
      this.fields.put("Gang.Allies", "Allies");
    }
    
    public void adapter(String gangname) throws SQLException, UnsupportedEncodingException {
      HashMap<String, String> addData = new HashMap<>();
      addData.put("Name", gangname);
      adapter(addData);
    }
  }
}
