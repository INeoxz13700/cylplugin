package fr.karmaowner.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class RecordBuilder {
  private String record = "";
  
  private STEP stepByStep = STEP.INITIALIZE;
  
  private enum STEP {
    INITIALIZE, SELECT, FROM, UPDATE, INSERT, DELETE, WHERE;
  }
  
  public enum LINK {
    AND, OR;
  }
  
  public static RecordBuilder build() {
    return new RecordBuilder();
  }
  
  public RecordBuilder select(ArrayList<String> fields) {
    if (this.stepByStep == STEP.INITIALIZE) {
      StringJoiner joiner = new StringJoiner(",");
      this.record += "SELECT " + joiner.toString() + " ";
      this.stepByStep = STEP.SELECT;
    } 
    return this;
  }
  
  public RecordBuilder select(ArrayList<String> fields, String tablename) {
    if (this.stepByStep == STEP.INITIALIZE) {
      select(fields);
      from(tablename);
    } 
    return this;
  }
  
  public RecordBuilder select(ArrayList<String> fields, String tablename, CustomEntry<String, Object> whereClause) {
    if (this.stepByStep == STEP.INITIALIZE) {
      select(fields, tablename);
      where(whereClause);
    } 
    return this;
  }
  
  public RecordBuilder select(String field) {
    if (this.stepByStep == STEP.INITIALIZE) {
      this.record += "SELECT " + field + " ";
      this.stepByStep = STEP.SELECT;
    } 
    return this;
  }
  
  public RecordBuilder select(String field, String tablename) {
    if (this.stepByStep == STEP.INITIALIZE) {
      select(field);
      from(tablename);
    } 
    return this;
  }
  
  public RecordBuilder select(String field, String tablename, CustomEntry<String, Object> whereClause) {
    if (this.stepByStep == STEP.INITIALIZE) {
      select(field, tablename);
      where(whereClause);
    } 
    return this;
  }
  
  public RecordBuilder delete(String tablename) {
    if (this.stepByStep == STEP.INITIALIZE) {
      this.record += "DELETE FROM " + tablename + " ";
      this.stepByStep = STEP.DELETE;
    } 
    return this;
  }
  
  public RecordBuilder selectAll() {
    if (this.stepByStep == STEP.INITIALIZE) {
      this.record += "SELECT * ";
      this.stepByStep = STEP.SELECT;
    } 
    return this;
  }
  
  public RecordBuilder selectAll(String tablename) {
    if (this.stepByStep == STEP.INITIALIZE) {
      selectAll();
      from(tablename);
    } 
    return this;
  }
  
  public RecordBuilder selectAll(String tablename, CustomEntry<String, Object> whereClause) {
    if (this.stepByStep == STEP.INITIALIZE) {
      selectAll(tablename);
      where(whereClause);
    } 
    return this;
  }
  
  public RecordBuilder update(HashMap<String, Object> fields, String tablename) {
    if (this.stepByStep == STEP.INITIALIZE) {
      StringJoiner joiner = new StringJoiner(",");
      for (Map.Entry<String, Object> s : fields.entrySet()) {
        if (s.getValue() instanceof String) {
          joiner.add((String)s.getKey() + " = '" + String.valueOf(s.getValue()).replaceAll("'", "''") + "'");
          continue;
        } 
        joiner.add((String)s.getKey() + " = " + String.valueOf(s.getValue()));
      } 
      this.record += "UPDATE " + tablename + " SET " + joiner.toString() + " ";
      this.stepByStep = STEP.UPDATE;
    } 
    return this;
  }
  
  public RecordBuilder insert(HashMap<String, Object> fields, String tablename) {
    if (this.stepByStep == STEP.INITIALIZE) {
      StringJoiner joinerKeys = new StringJoiner(",");
      StringJoiner joinerValues = new StringJoiner(",");
      for (String key : fields.keySet())
        joinerKeys.add(key); 
      for (Object value : fields.values()) {
        if (value instanceof String) {
          joinerValues.add("'" + ((String)value).replaceAll("'", "''") + "'");
          continue;
        } 
        joinerValues.add(String.valueOf(value));
      } 
      this.record += "INSERT INTO " + tablename + "(" + joinerKeys.toString() + ") VALUES (" + joinerValues.toString() + ") ";
      this.stepByStep = STEP.INSERT;
    } 
    return this;
  }
  
  public RecordBuilder insert(CustomEntry<String, Object> field, String tablename) {
    if (this.stepByStep == STEP.INITIALIZE) {
      if (field.getValue() instanceof String) {
        this.record += "INSERT INTO " + tablename + "(" + (String)field.getKey() + ") VALUES ('" + ((String)field.getValue()).replaceAll("'", "''") + "') ";
      } else {
        this.record += "INSERT INTO " + tablename + "(" + (String)field.getKey() + ") VALUES (" + field.getValue() + ") ";
      } 
      this.stepByStep = STEP.INSERT;
    } 
    return this;
  }
  
  public RecordBuilder update(CustomEntry<String, Object> field, String tablename) {
    if (this.stepByStep == STEP.INITIALIZE) {
      if (field.getValue() instanceof String) {
        this.record += "UPDATE " + tablename + " SET " + (String)field.getKey() + " = '" + ((String)field.getValue()).replaceAll("'", "''") + "' ";
      } else {
        this.record += "UPDATE " + tablename + " SET " + (String)field.getKey() + " = " + field.getValue() + " ";
      } 
      this.stepByStep = STEP.UPDATE;
    } 
    return this;
  }
  
  public RecordBuilder where(CustomEntry<String, Object> entry) {
    if (this.stepByStep == STEP.FROM || this.stepByStep == STEP.UPDATE || this.stepByStep == STEP.DELETE) {
      if (entry.getValue() instanceof String) {
        this.record += "WHERE " + (String)entry.getKey() + " = '" + ((String)entry.getValue()).replaceAll("'", "''") + "' ";
      } else {
        this.record += "WHERE " + (String)entry.getKey() + " = " + entry.getValue() + " ";
      } 
      this.stepByStep = STEP.WHERE;
    } 
    return this;
  }
  
  public RecordBuilder where(CustomEntry<String, Object> entry, LINK link) {
    if (this.stepByStep == STEP.WHERE)
      if (entry.getValue() instanceof String) {
        this.record += link.name() + " " + (String)entry.getKey() + " = '" + ((String)entry.getValue()).replaceAll("'", "''") + "' ";
      } else {
        this.record += link.name() + " " + (String)entry.getKey() + " = " + entry.getValue() + " ";
      }  
    return this;
  }
  
  public RecordBuilder from(String tablename) {
    if (this.stepByStep == STEP.SELECT) {
      this.record += "FROM " + tablename + " ";
      this.stepByStep = STEP.FROM;
    } 
    return this;
  }
  
  public String toString() {
    String rcd = this.record.substring(0, this.record.lastIndexOf(" "));
    return rcd;
  }
}
