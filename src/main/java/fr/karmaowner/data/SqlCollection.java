package fr.karmaowner.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class SqlCollection implements Iterable<ResultSet> {
  private ResultSet rs;
  
  private int count = -1;
  
  public SqlCollection(ResultSet rs) {
    this.rs = rs;
  }
  
  public Iterator<ResultSet> iterator() {
    Iterator<ResultSet> it = new Iterator<ResultSet>() {
        public boolean hasNext() {
          try {
            return SqlCollection.this.rs.next();
          } catch (SQLException e) {
            e.printStackTrace();
            return false;
          } 
        }
        
        public ResultSet next() {
          return SqlCollection.this.rs;
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
    return it;
  }
  
  public ResultSet getActualResult() {
    try {
      this.rs.first();
    } catch (SQLException e) {
      e.printStackTrace();
    } 
    return this.rs;
  }
  
  public int count() throws SQLException {
    if (this.count == -1) {
      int count = 0;
      while (this.rs.next())
        count++; 
      this.rs.beforeFirst();
      this.count = count;
      return this.count;
    } 
    return this.count;
  }
}
