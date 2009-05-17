
package DBViewer.models;

import java.sql.*;
import java.util.*;
import DBViewer.objects.view.*;
import DBViewer.controllers.*;
import DBViewer.objects.model.*;
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
   
/**
 * Generates a connection from the internal DB based on the initialized path.
 *
 * @return
 * @throws java.sql.SQLException
 */
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

/**
 * Makes sure that all the correct tables have been created in the internal db
 * @param conn
 */
      public void createTables(Connection conn){
          try {
              Statement st = conn.createStatement();
              st.executeUpdate(" CREATE TABLE IF NOT EXISTS schema (title PRIMARY KEY); ");
              st.executeUpdate(" CREATE TABLE IF NOT EXISTS table_schema (id PRIMARY KEY, name, schema," +
                      " UNIQUE (name, schema), " +
                      " FOREIGN KEY (schema) REFERENCES schema(title));");
              st.executeUpdate(" CREATE TABLE IF NOT EXISTS schema_page (id PRIMARY KEY, orderid INTEGER, title, schema, " +
                      " FOREIGN KEY (schema) REFERENCES schema(title)); ");
              st.executeUpdate(" CREATE TABLE IF NOT EXISTS table_page_position (pageid, tableid, x_pos FLOAT, y_pos FLOAT," +
                      " UNIQUE (pageid, tableid), " +
                      " FOREIGN KEY (pageid) REFERENCES schema_page(id), " +
                      " FOREIGN KEY (tableid) REFERENCES table_schema(id) " +
                      "); ");
          } catch (Exception e) {
              e.printStackTrace();
          } 
      }
      
/**
 * Inserts a tableView and its positions into the internal DB
 * @param t
 * @param conn
 */
      public void saveTable(Table t, Connection conn) {
          verifySchema(t.getSchemaName(), conn);
          String insertTableSQL = "INSERT OR REPLACE INTO table_schema (id,name,schema) VALUES (?,?,?);";
          String insertTableViewSQL = "INSERT OR REPLACE INTO table_page_position (pageid,tableid,x_pos,y_pos) VALUES (?,?,?,?);";
          try {
              PreparedStatement ps = conn.prepareStatement(insertTableSQL);
              ps.setString(1, t.getId().toString());
              ps.setString(2, t.getName());
              ps.setString(3, t.getSchemaName());
              ps.executeUpdate();

              if (t.getTablePageViews().size()>0){
                  ps = conn.prepareStatement(insertTableViewSQL);
                  for (TableView tv :t.getTablePageViews().values()){
                      ps.setString(1, tv.getTable().getId().toString());
                      ps.setString(2, tv.getPage().getId().toString());
                      ps.setDouble(3, tv.getX());
                      ps.setDouble(4, tv.getY());
                      ps.executeUpdate();
                  }
              }
              ps.close();
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
/**
 * Inserts or updates a TableView
 * @param tv
 * @param conn
 */
      public void saveTablePosition(TableView tv, Connection conn){
          verifySchema(tv.getTable().getSchemaName(), conn);
          String insertTableViewSQL = "INSERT OR REPLACE INTO table_page_position (pageid,tableid,x_pos,y_pos) VALUES (?,?,?,?);";
          try {
              PreparedStatement ps = conn.prepareStatement(insertTableViewSQL);
              ps.setString(1, tv.getTable().getId().toString());
              ps.setString(2, tv.getPage().getId().toString());
              ps.setDouble(3, tv.getX());
              ps.setDouble(4, tv.getY());
              ps.executeUpdate();

          } catch (Exception e) {
              e.printStackTrace();
          }
      }

      /**
       * Makes sure that the schema is in the internal db.
       * @param schema
       * @param conn
       */
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

      /**
       * Checks to see if there are coordinates for the current table in the
       * internal DB. If not, it returns false.
       * @param tv
       * @param conn
       * @return coordsFound
       */
      public TableView makeViewWCoordinates(Table t, SchemaPage page, int numTables, Connection conn) {
          TableView tv = new TableView(t);
          t.addTableViewForPage(tv, page);
          tv.setPage(page);
          String insertSQL = "SELECT * FROM table_page_position WHERE pageid = ? AND tableid = ? LIMIT 1;";
          try {
              PreparedStatement ps = conn.prepareStatement(insertSQL);

              ps.setString(1, page.getId().toString());
              ps.setString(2, t.getId().toString());

              ResultSet rs = ps.executeQuery();

              if (rs.next()) {
                  tv.setX(rs.getDouble("x_pos"));
                  tv.setY(rs.getDouble("y_pos"));
                  System.out.println("Populated Coordinates for: "+tv.getTable().getName()+"{"+tv.getX()+","+tv.getY()+"}");
                  tv.setClean();
              } else {
                  tv.setX((Math.random()) * 2 * numTables*200);
                  tv.setY((Math.random()) * 2 * numTables*50+300);
                  tv.setDirty();
              }
              rs.close();
          } catch (Exception e) {
              e.printStackTrace();
          }
          return tv;
      }

      /**
       * Returns a collection of Schema Pages from the internal DB.
       * 
       * @param schemaName
       * @param conn
       * @return
       */
      public Map<UUID,SchemaPage> readSchemaPages(SortedSchema schema, Connection conn) {
          String insertSQL = "SELECT * FROM schema_page sp WHERE schema = ? ORDER BY sp.orderid ASC;";
          Map<UUID,SchemaPage> pages = new HashMap();
          try {
              PreparedStatement ps = conn.prepareStatement(insertSQL);

              ps.setString(1, schema.getName());

              ResultSet rs = ps.executeQuery();

              while (rs.next()) {
                  SchemaPageCache spc = SchemaPageCache.getInstance();
                  SchemaPage p = spc.getSchemaPageByID(rs.getString("id"));
                  pages.put(p.getId(),p);
                  p.setOrderid(rs.getInt("orderid"));
                  p.setTitle(rs.getString("title"));
                  p.setSchema(schema);
              }
              rs.close();
          } catch (Exception e) {
              e.printStackTrace();
          }
          return pages;
      }

//      /**
//       * Checks to see if the Table is assigned to a page, and if so connects them
//       * in memory.
//       *
//       * @param tv
//       * @param pages
//       * @param conn
//       */
//      public void readTablePage(TableView tv, Map<Integer,SchemaPage> pages, Connection conn) {
//          String SQL = "SELECT DISTINCT p.id FROM schema_page_table spt, page p WHERE p.id=spt.pageid AND tablename = ? and schema =?;";
//          try {
//              PreparedStatement ps = conn.prepareStatement(SQL);
//
//              ps.setString(1, tv.getTable().getName());
//              ps.setString(2, tv.getTable().getSchemaName());
//
//              ResultSet rs = ps.executeQuery();
//              int i =0;
//              while (rs.next()) {
//                  Integer pageid = rs.getInt("id");
//                  SchemaPage p = pages.get(pageid);
//                  // if the table is on more than one page, we need to create a new TableView for the page.
//                  if (i>0) {
//                      TableView tvNew = new TableView(tv.getTable());
//                      tvNew.setPage(p);
//                      p.getTableViews().add(tvNew);
//                  } else {
//                      tv.setPage(p);
//                      p.getTableViews().add(tv);
//                  }
//                  i++;
//              }
//              rs.close();
//          } catch (Exception e) {
//              e.printStackTrace();
//          }
//      }

  /**
   * test method, creates a SQLite DB with the proper tables.
   * 
   * @param args
   * @throws java.lang.Exception
   */
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
