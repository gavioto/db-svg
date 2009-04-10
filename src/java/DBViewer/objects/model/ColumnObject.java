/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DBViewer.objects.model;

/**
 *
 * @author Derrick Bowen <derrickbowen@gmail.com>
 */
public class ColumnObject implements Column {

   String name;
   String nullable;
   String comment;
   int ordinalValue;
   String dataType;
   String autoIncrement;
   Table table;

   /**
    * 
    */
   public ColumnObject() {
   }

   /**
    * 
    * @param name
    */
   public ColumnObject(String name) {
      this.name = name;
   }
   /**
    * 
    * @param c
    * @return
    */
   public static Column clone(Column c) {
      Column n = new ColumnObject();
      n.setName(c.getName());
      n.setComment(c.getComment());
      n.setNullable(c.getNullable());
      n.setOrdinalValue(c.getOrdinalValue());
      n.setDataType(c.getDataType());
      n.setTable(c.getTable());
      return n;
   }

   /**
    * 
    * @return
    */
   public ForeignKey transformToFK() {
      ForeignKey n;
      if (this.getClass() == new PrimaryKeyObject().getClass()) {
         n = new PrimaryForeignKey();
         PrimaryKeyObject pk = (PrimaryKeyObject)this;
         pk.cloneTo((PrimaryForeignKey)n);
      } else {
         n = new ForeignKey();
      }
      this.copyTo(n);
      return n;
   }

   /**
    * 
    * @return
    */
   public PrimaryKey transformToPK() {
      PrimaryKey n;
      if (this.getClass() == new PrimaryKeyObject().getClass()) {
         n = new PrimaryForeignKey();
         PrimaryKeyObject pk = (PrimaryKeyObject)this;
         pk.cloneTo((PrimaryForeignKey)n);
      } else {
         n = new PrimaryKeyObject();
      }
      this.copyTo(n);
      return n;
   }
   
   private Column copyTo(Column n) {
      n.setName(this.getName());
      n.setComment(this.getComment());
      n.setNullable(this.getNullable());
      n.setOrdinalValue(this.getOrdinalValue());
      n.setDataType(this.getDataType());
      n.setTable(this.getTable());
      return n;
       
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

   /**
    * 
    * @return
    */
   public String getComment() {
      return comment;
   }

   /**
    * 
    * @param comment
    */
   public void setComment(String comment) {
      this.comment = comment;
   }

   /**
    * 
    * @return
    */
   public String getNullable() {
      return nullable;
   }

   /**
    * 
    * @param nullable
    */
   public void setNullable(String nullable) {
      this.nullable = nullable;
   }

   /**
    * 
    * @return
    */
   public int getOrdinalValue() {
      return ordinalValue;
   }

   /**
    * 
    * @param ordinalValue
    */
   public void setOrdinalValue(int ordinalValue) {
      this.ordinalValue = ordinalValue;
   }

   /**
    * 
    * @return
    */
   public String getDataType() {
      return dataType;
   }

   /**
    * 
    * @param dataType
    */
   public void setDataType(String dataType) {
      this.dataType = dataType;
   }
   
   /**
    * 
    * @return
    */
   public String getAutoIncrement() {
      return autoIncrement;
   }

   /**
    * 
    * @param autoIncrement
    */
   public void setAutoIncrement(String autoIncrement) {
      this.autoIncrement = autoIncrement;
   }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }
}
