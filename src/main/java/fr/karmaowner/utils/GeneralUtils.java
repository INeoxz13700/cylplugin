package fr.karmaowner.utils;

public class GeneralUtils {
  public static String getMaxString(int max, String myString) {
    return (myString.length() > max - 1) ? myString.substring(0, max - 1) : myString;
  }
  
  public static String getMinMaxString(int min, int max, String myString) {
    return (myString.length() >= min && myString.length() <= max) ? myString : myString.substring(min, max);
  }
}
