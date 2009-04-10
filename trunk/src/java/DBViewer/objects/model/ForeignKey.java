package DBViewer.objects.model;

/**
 *
 * @author Derrick Bowen <derrickbowen@gmail.com>
 */
public class ForeignKey extends ColumnObject {

   Column reference;
   String referencedColumn;
   String referencedTable;
   String updateRule;
   String deleteRule;

   /**
    * 
    */
   public ForeignKey() {
   }

   /**
    * 
    * @param name
    */
   public ForeignKey(String name) {
      super.name = name;
   }
   
   public ForeignKey cloneTo(ForeignKey fk) {
      fk.setReference(this.getReference());
      fk.setReferencedTable(this.getReferencedTable());
      fk.setReferencedColumn(this.getReferencedColumn());
      fk.setUpdateRule(this.getUpdateRule());
      fk.setDeleteRule(this.getDeleteRule());
      return fk;
   }

   /**
    * 
    * @return
    */
   public String getDeleteRule() {
      return deleteRule;
   }

   /**
    * 
    * @param deleteRule
    */
   public void setDeleteRule(String deleteRule) {
      this.deleteRule = deleteRule;
   }

   /**
    * 
    * @return
    */
   public String getUpdateRule() {
      return updateRule;
   }

   /**
    * 
    * @param updateRule
    */
   public void setUpdateRule(String updateRule) {
      this.updateRule = updateRule;
   }

   /**
    * 
    * @return
    */
   public Column getReference() {
      return reference;
   }

   /**
    * 
    * @param reference
    */
   public void setReference(Column reference) {
      this.reference = reference;
   }

   public String getReferencedColumn() {
      return referencedColumn;
   }

   public void setReferencedColumn(String referencedColumn) {
      this.referencedColumn = referencedColumn;
   }

   public String getReferencedTable() {
      return referencedTable;
   }

   public void setReferencedTable(String referencedTable) {
      this.referencedTable = referencedTable;
   }
   
   
}
