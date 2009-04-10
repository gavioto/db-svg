package DBViewer.objects.model;

/**
 *
 * @author Derrick Bowen <derrickbowen@gmail.com>
 */
public class PrimaryKeyObject extends ColumnObject implements PrimaryKey {


   /**
    * 
    */
   public PrimaryKeyObject() {
   }
   
   /**
    * 
    * @param name
    */
   public PrimaryKeyObject(String name) {
      super.name = name;
   }
   
   public PrimaryKey cloneTo(PrimaryKey pk) {
      return pk;
   }
}
