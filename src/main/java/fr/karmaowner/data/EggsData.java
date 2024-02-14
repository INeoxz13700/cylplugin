package fr.karmaowner.data;

import fr.karmaowner.common.Main;
import fr.karmaowner.companies.eggs.EggsHatching;
import fr.karmaowner.companies.eggs.StateEggs;
import fr.karmaowner.utils.CustomEntry;
import fr.karmaowner.utils.RecordBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EggsData implements Data {
  private ArrayList<EggsHatching> eggs;
  
  private CompanyData data;
  
  public EggsData(CompanyData data) {
    this.data = data;
    this.eggs = new ArrayList<>();
    loadData();
  }
  
  public List<EggsHatching> getEggs() {
    return this.eggs;
  }
  
  public void addEggs(EggsHatching egg) {
    this.eggs.add(egg);
  }
  
  public void loadData() {
    try {
      SqlCollection results = Main.Database.select(RecordBuilder.build().selectAll("eggs_data")
          .where(new CustomEntry("companyName", this.data.getCompanyName())).toString());
      if (results.count() > 0) {
        for (ResultSet d : results) {
          this.eggs.add(new EggsHatching(d
                  .getInt("timeHatching"), d
                  .getByte("TypeId"),
                  StateEggs.getState(d.getString("State")), d
                  .getLong("startedTime"), d
                  .getString("name")));
        }
        Main.Database.update(RecordBuilder.build().delete("eggs_data")
            .where(new CustomEntry("companyName", this.data.getCompanyName())).toString());
      } 
    } catch (SQLException e) {
      e.printStackTrace();
    } 
  }
  
  public void saveData() {
    for (EggsHatching e : this.eggs) {
      HashMap<String, Object> fields = new HashMap<>();
      fields.put("companyName", this.data.getCompanyName());
      fields.put("TypeId", e.getTypeId());
      fields.put("timeHatching", e.getTimeHatching());
      fields.put("State", e.getState().toString());
      fields.put("startedTime", e.getStartedTime().getTime());
      fields.put("name", e.getName());
      try {
        Main.Database.update(RecordBuilder.build().insert(fields, "eggs_data").toString());
      } catch (SQLException e1) {
        e1.printStackTrace();
      } 
    }
  }
}
