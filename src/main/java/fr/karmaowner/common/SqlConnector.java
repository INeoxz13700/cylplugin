package fr.karmaowner.common;

import fr.karmaowner.data.SqlCollection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlConnector {
  private Connection connect = null;
  
  public void connect(String host, String database_name, String user, String pass) throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.jdbc.Driver");
    this.connect = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database_name + "?autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8", user, pass);
  }
  
  public SqlCollection select(String record) throws SQLException {
    ResultSet rs = this.connect.prepareStatement(record).executeQuery();
    return new SqlCollection(rs);
  }
  
  public void update(String request) throws SQLException {
    this.connect.prepareStatement(request).executeUpdate(request);
  }
  
  public Connection getConnection() {
    return this.connect;
  }
}
