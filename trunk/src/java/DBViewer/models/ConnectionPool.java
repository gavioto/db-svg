
package DBViewer.models;

import java.sql.*;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import javax.sql.StatementEventListener;

/**
 *
 * @author Derrick Bowen <derrickbowen@gmail.com>
 * 
 * supposed to extend java.sql.ConnectionPool, but it was throwing errors
 */
public class ConnectionPool {

    private static String url = "jdbc:mysql://localhost:3306/movelib";
    private static String driver = "com.mysql.jdbc.Driver";
    private static String username = "test";
    private static String password = "zarahemla";
    
    public ConnectionPool(){
    }
    
    public static Connection getConnection() {
       try {
        Class.forName(driver).newInstance();
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
       } catch (Exception e){
          e.printStackTrace();
       }
       return null;
    }

   public void close() throws SQLException {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public void addConnectionEventListener(ConnectionEventListener arg0) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public void removeConnectionEventListener(ConnectionEventListener arg0) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public void addStatementEventListener(StatementEventListener arg0) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public void removeStatementEventListener(StatementEventListener arg0) {
      throw new UnsupportedOperationException("Not supported yet.");
   }
    
}
