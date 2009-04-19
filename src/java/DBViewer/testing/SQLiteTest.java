package DBViewer.testing;

import java.sql.*;

/**
 *
 * @author horizon
 */
public class SQLiteTest {
public static void main(String[] args) throws Exception {
      Class.forName("org.sqlite.JDBC");
      Connection conn = DriverManager.getConnection("jdbc:sqlite:/DB-SVG/DB-SVG-2.db");
      Statement stmt1 = conn.createStatement();
      stmt1.executeUpdate("drop table if exists schema;");
      stmt1.executeUpdate("drop table if exists table_position;");
      stmt1.executeUpdate("drop table if exists page;");
      stmt1.executeUpdate("drop table if exists schema_page_table;");
      stmt1.executeUpdate("CREATE TABLE IF NOT EXISTS schema (title PRIMARY KEY); ");
      stmt1.executeUpdate(" CREATE TABLE IF NOT EXISTS table_position (name, schema, x_pos FLOAT, y_pos FLOAT," +
              " UNIQUE (name, schema), " +
              " FOREIGN KEY (schema) REFERENCES schema(title));");
      stmt1.executeUpdate(" CREATE TABLE IF NOT EXISTS page (id INTEGER PRIMARY KEY, orderid INTEGER, title ); ");
      stmt1.executeUpdate(" CREATE TABLE IF NOT EXISTS schema_page_table (pageid INTEGER, tablename, schema," +
              " UNIQUE (pageid, tablename, schema), " +
              " FOREIGN KEY (pageid) REFERENCES page(id), " +
              " FOREIGN KEY (tablename) REFERENCES table_position(name), " +
              " FOREIGN KEY (schema) REFERENCES schema(title) " +
              "); ");
      PreparedStatement ps = conn.prepareStatement(
          "INSERT OR REPLACE INTO table_position VALUES (?,?,?,?);");

      ps.setString(1, "test1_table");
      ps.setString(2, "testschema");
      ps.setDouble(3, 1);
      ps.setDouble(4, 10);
      ps.addBatch();
      ps.setString(1, "test2_table");
      ps.setString(2, "testschema");
      ps.setDouble(3, 100);
      ps.setDouble(4, 410);
      ps.addBatch();
      ps.setString(1, "test3_table");
      ps.setString(2, "testschema");
      ps.setDouble(3, 530);
      ps.setDouble(4, 160);
      ps.addBatch();

      conn.setAutoCommit(false);
      ps.executeBatch();
      conn.setAutoCommit(true);

      ResultSet rs = stmt1.executeQuery("select * from table_position;");
      System.out.println("Tables: {position}");
      while (rs.next()) {
          System.out.print(rs.getString("schema")+"."+rs.getString("name"));
          System.out.println(" {" + rs.getDouble("x_pos")+"," + rs.getDouble("y_pos")+"}");
      }
      rs.close();
      conn.close();
  }

}
