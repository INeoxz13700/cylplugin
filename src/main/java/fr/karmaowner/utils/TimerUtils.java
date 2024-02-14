package fr.karmaowner.utils;

import java.util.concurrent.TimeUnit;

public class TimerUtils {
  public static String formatString(long seconds) {
    long hr = TimeUnit.MILLISECONDS.toHours(seconds * 1000L);
    long min = TimeUnit.MILLISECONDS.toMinutes(seconds * 1000L - TimeUnit.HOURS.toMillis(hr));
    long sec = TimeUnit.MILLISECONDS.toSeconds(seconds * 1000L - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
    long ms = TimeUnit.MILLISECONDS.toMillis(seconds * 1000L - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));
    return String.format("%02dh %02dm %02ds", hr, min, sec);
  }
}
