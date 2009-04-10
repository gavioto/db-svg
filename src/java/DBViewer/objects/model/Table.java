package DBViewer.objects.model;

import java.util.*;
import DBViewer.objects.view.*;
import java.io.Serializable;

/**
 *
 * @author Derrick Bowen <derrickbowen@gmail.com>
 */
public class Table  implements Serializable{

   String name = "";
   String schemaName = "";
   Map<String, Column> columns = new LinkedHashMap();
   Map<String, ForeignKey> foreignKeys = new LinkedHashMap();
   Map<String, Table> referencingTables = new LinkedHashMap();
   Map<String, PrimaryKey> primaryKeys = new LinkedHashMap();
   int width = 0;
   int height = 0;
   TableView tableView;

   /**
    * 
    */
   public Table() {
       tableView = new TableView(this);
   }

   /**
    * 
    * @param name
    */
   public Table(String name) {
      this.name = name;
       tableView = new TableView(this);
   }

   /**
    * 
    * @return
    */
   public String getName() {
      return name;
   }

   /**
    * 
    * @param name
    */
   public void setName(String name) {
      this.name = name;
   }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

   /**
    * Map key = column name
    * value = column object
    * @return
    */
   public Map<String, Column> getColumns() {
      return columns;
   }

   /**
    * 
    * @param columns
    */
   public void setColumns(Map<String, Column> columns) {
      this.columns = columns;
   }

   public Map<String, ForeignKey> getForeignKeys() {
      return foreignKeys;
   }

   public void setForeignKeys(Map<String, ForeignKey> foreignKeys) {
      this.foreignKeys = foreignKeys;
   }

   public Map<String, Table> getReferencingTables() {
      return referencingTables;
   }

   public void setReferencingTables(Map<String, Table> referencingTables) {
      this.referencingTables = referencingTables;
   }

   public Map<String, PrimaryKey> getPrimaryKeys() {
      return primaryKeys;
   }

   public void setPrimaryKeys(Map<String, PrimaryKey> primaryKeys) {
      this.primaryKeys = primaryKeys;
   }
    public int generateWidth() {
        if (width<1) {
            width = name.length();
            for (Column c : columns.values()) {
                int clen = c.getName().length();
                if (clen > width) {
                    width = clen;
                }
            }
        }
        return width;
    }

    public TableView getTableView() {
        return tableView;
    }

    public void setTableView(TableView tableView) {
        this.tableView = tableView;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
   
}
