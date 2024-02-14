package fr.karmaowner.companies.eggs;

public enum StateEggs {
  WAITING, HATCH;
  
  public static StateEggs getState(String state) {
    for (StateEggs s : values()) {
      if (s.toString().equals(state))
        return s; 
    } 
    return null;
  }
}
