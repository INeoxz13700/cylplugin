package fr.karmaowner.companies.eggs;

import fr.karmaowner.data.UpdateTime;
import java.sql.Timestamp;

public class EggsHatching {
  private int timeHatching;
  
  private Timestamp startedTime;
  
  private byte typeId;
  
  private StateEggs s = StateEggs.WAITING;
  
  private UpdateTime now;
  
  private String name;
  
  public EggsHatching(int time, byte idTypeEgg, String name) {
    this.startedTime = new Timestamp(System.currentTimeMillis());
    this.timeHatching = time;
    this.typeId = idTypeEgg;
    this.name = name;
    this.now = new UpdateTime();
    getTimeLeft();
  }
  
  public EggsHatching(int time, byte idTypeEgg, StateEggs s, long start, String name) {
    this.timeHatching = time;
    this.typeId = idTypeEgg;
    this.now = new UpdateTime();
    this.s = s;
    this.name = name;
    this.startedTime = new Timestamp(start);
    getTimeLeft();
  }
  
  public Timestamp getTimeLeft() {
    long time = (this.timeHatching * 1000L);
    Timestamp timeLeft = new Timestamp(time - (this.now.getNow().getTime() - this.startedTime.getTime()));
    updateState(timeLeft);
    return timeLeft;
  }
  
  public long getTimeLeftInLong() {
    long time = (this.timeHatching * 1000L);
    return time - (this.now.getNow().getTime() - this.startedTime.getTime());
  }
  
  public int getTimeHatching() {
    return this.timeHatching;
  }
  
  public byte getTypeId() {
    return this.typeId;
  }
  
  public Timestamp getStartedTime() {
    return this.startedTime;
  }
  
  public void setState(StateEggs s) {
    this.s = s;
  }
  
  public StateEggs getState() {
    return this.s;
  }
  
  public void updateState(Timestamp timeLeft) {
    if (getTimeLeftInLong() <= 0L)
      this.s = StateEggs.HATCH; 
  }
  
  public String getName() {
    return this.name;
  }
}
