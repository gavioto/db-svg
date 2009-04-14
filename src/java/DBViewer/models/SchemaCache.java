
package DBViewer.models;

/**
 *
 * @author horizon
 */
public class SchemaCache {

///////////////////  Singletoning it  ///////////////////
   private static SchemaCache instance = null;

   /**
    * private constructor
    */
   private SchemaCache() {
   }

   /**
    * returns an instance of the DAO.
    * @return
    */
   public static SchemaCache getInstance() {
      if (instance == null) {
         instance = new SchemaCache();
      }
      return instance;
   }
/////////////////////////////////////////////////////////

}
