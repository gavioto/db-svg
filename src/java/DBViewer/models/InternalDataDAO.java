
package DBViewer.models;

import java.sql.*;
import DBViewer.objects.view.*;
import java.io.Serializable;
/**
 * A very basic DAO for storing information about table positions in an SQLite database
 *
 *
 * @author horizon
 */
public class InternalDataDAO implements Serializable{

    private String url = "jdbc:sqlite:";
    private String driver = "org.sqlite.JDBC";
    private String path = "~/unset.db";

///////////////////  Singletoning it  ///////////////////
   private static InternalDataDAO instance = null;

   /**
    * private constructor
    */
   private InternalDataDAO (String path) {
        this.path = path;
   }

   /**
    * returns an instance of the DAO.
    * @return
    */
   public static InternalDataDAO getInstance(String path) {
      if (instance == null) {
         instance = new InternalDataDAO(path);
      }
      return instance;
   }

      public Connection getConnection() throws SQLException {
       try {
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url+path);
        return conn;
       } catch (SQLException e){
          System.out.println("Internal DAO path incorrect.");
          throw e;
       } catch (Exception e){
          e.printStackTrace();
       }
       return null;
    }

      public void createTables(Connection conn){
          try {
              Statement st = conn.createStatement();
              st.executeUpdate("CREATE TABLE IF NOT EXISTS schema (title PRIMARY KEY); ");
              st.executeUpdate(" CREATE TABLE IF NOT EXISTS table_position (name, schema, x_pos FLOAT, y_pos FLOAT," +
                      " UNIQUE (name, schema), " +
                      " FOREIGN KEY (schema) REFERENCES schema(title));");
              st.executeUpdate(" CREATE TABLE IF NOT EXISTS page (id INTEGER PRIMARY KEY, orderid INTEGER, title ); ");
              st.executeUpdate(" CREATE TABLE IF NOT EXISTS schema_page_table (pageid INTEGER, tablename, schema," +
                      " UNIQUE (pageid, tablename, schema), " +
                      " FOREIGN KEY (pageid) REFERENCES page(id), " +
                      " FOREIGN KEY (tablename) REFERENCES table_position(name), " +
                      " FOREIGN KEY (schema) REFERENCES schema(title) " +
                      "); ");
          } catch (Exception e) {
              e.printStackTrace();
          } 
      }

      public void insertTable(TableView t, Connection conn) {
          verifySchema(t.getTable().getSchemaName(), conn);
          String insertSQL = "INSERT OR REPLACE INTO table_position VALUES (?,?,?,?);";
          try {
              PreparedStatement ps = conn.prepareStatement(insertSQL);

              ps.setString(1, t.getTable().getName());
              ps.setString(2, t.getTable().getSchemaName());
              ps.setDouble(3, t.getX());
              ps.setDouble(4, t.getY());

              ps.executeUpdate();

          } catch (Exception e) {
              e.printStackTrace();
          }
      }

      public void verifySchema(String schema,Connection conn) {

          String insertSQL = "INSERT OR REPLACE INTO schema VALUES (?);";
          try {
              PreparedStatement ps = conn.prepareStatement(insertSQL);

              ps.setString(1, schema);

              ps.executeUpdate();
              
          } catch (Exception e) {
              e.printStackTrace();
          }
      }

      public boolean setCoordinates(TableView tv, Connection conn) {
          boolean coordsFound = false;
          String insertSQL = "SELECT * FROM table_position WHERE schema = ? AND name = ? LIMIT 1;";
          try {
              PreparedStatement ps = conn.prepareStatement(insertSQL);

              ps.setString(1, tv.getTable().getSchemaName());
              ps.setString(2, tv.getTable().getName());

              ResultSet rs = ps.executeQuery();

              if (rs.next()) {
                  coordsFound = true;
                  tv.setX(rs.getDouble("x_pos"));
                  tv.setY(rs.getDouble("y_pos"));
                  System.out.println("Populated Coordinates for: "+tv.getTable().getName()+"{"+tv.getX()+","+tv.getY()+"}");
              }
              rs.close();
          } catch (Exception e) {
              e.printStackTrace();
          }
          return coordsFound;
      }

    public static void main(String[] args) throws Exception {
        InternalDataDAO iDAO =  InternalDataDAO.getInstance("/DB-SVG/DB-SVG-2.db");
        Connection conn = iDAO.getConnection();
        iDAO.createTables(conn);
        
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
}
