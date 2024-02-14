package fr.karmaowner.gangs;

public enum CaptureState {
  WILDERNESS, CATCHED, CATCHING;
  
  public static CaptureState getCaptureByName(String name) {
    for (CaptureState s : values()) {
      if (s.toString().equalsIgnoreCase(name))
        return s; 
    } 
    return null;
  }
}
