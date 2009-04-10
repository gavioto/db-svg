package DBViewer.objects.model;

/**
 *
 * @author Derrick Bowen <derrickbowen@gmail.com>
 */
public class PrimaryForeignKey extends ForeignKey implements PrimaryKey{
 boolean autoIncrement;

   /**
    * 
    */
   public PrimaryForeignKey() {
   }

   /**
    * 
    * @param name
    */
   public PrimaryForeignKey(String name) {
      super.name = name;
   }
   
   /**
    * 
    * @return
    */
   public boolean isAutoIncrement() {
      return autoIncrement;
   }

   /**
    * 
    * @param autoIncrement
    */
   public void setAutoIncrement(boolean autoIncrement) {
      this.autoIncrement = autoIncrement;
   }
}
