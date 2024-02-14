package fr.karmaowner.data;

import java.sql.Timestamp;

public class UpdateTime {
  public Timestamp getNow() {
    return new Timestamp(System.currentTimeMillis());
  }
}
