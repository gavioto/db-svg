package DBViewer.testing;

import java.sql.*;

/**
 *
 * @author horizon
 */
public class SQLiteTest {
public static void main(String[] args) throws Exception {
      Class.forName("org.sqlite.JDBC");
      Connection conn = DriverManager.getConnection("jdbc:sqlite:/home/horizon/NetBeansProjects/DB-SVG/DB-SVG.db");
      Statement stmt1 = conn.createStatement();
      //stat.executeUpdate("drop table if exists people;");
      stmt1.executeUpdate("CREATE TABLE IF NOT EXISTS schema (title PRIMARY KEY, sorted BOOLEAN DEFAULT 0); ");
      stmt1.executeUpdate(" CREATE TABLE IF NOT EXISTS table_position (name, schema, x_pos FLOAT, y_pos FLOAT, UNIQUE (name, schema));");
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
