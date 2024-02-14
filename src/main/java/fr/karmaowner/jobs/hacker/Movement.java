package fr.karmaowner.jobs.hacker;

public class Movement {
  private Move m;
  
  private Type t;
  
  private int position;
  
  public enum Move {
    LEFT, RIGHT, NONE;
  }
  
  public enum Type {
    ROW, COLUMN, NONE;
  }
  
  public Movement() {
    this.m = Move.NONE;
    this.t = Type.NONE;
    this.position = -1;
  }
  
  public void setType(Type t) {
    this.t = t;
  }
  
  public Type getType() {
    return this.t;
  }
  
  public int getPosition() {
    return this.position;
  }
  
  public Move getMove() {
    return this.m;
  }
  
  public void setMove(Move m) {
    this.m = m;
  }
  
  public void setPosition(int p) {
    this.position = p;
  }
}
