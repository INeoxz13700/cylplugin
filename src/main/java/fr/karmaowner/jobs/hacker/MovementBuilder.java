package fr.karmaowner.jobs.hacker;

public class MovementBuilder {
  private Movement movement = new Movement();
  
  public static MovementBuilder build() {
    return new MovementBuilder();
  }
  
  public MovementBuilder setLeft() {
    this.movement.setMove(Movement.Move.LEFT);
    return this;
  }
  
  public MovementBuilder setRight() {
    this.movement.setMove(Movement.Move.RIGHT);
    return this;
  }
  
  public MovementBuilder setRow() {
    this.movement.setType(Movement.Type.ROW);
    return this;
  }
  
  public Movement getMovement() {
    return this.movement;
  }
  
  public MovementBuilder setColumn() {
    this.movement.setType(Movement.Type.COLUMN);
    return this;
  }
  
  public MovementBuilder setPosition(int p) {
    this.movement.setPosition(p);
    return this;
  }
}
