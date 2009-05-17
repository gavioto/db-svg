
package DBViewer.models;

import DBViewer.objects.view.*;
import java.util.*;

/**
 * Keeps a Cache of all Schema Pages so that they are for sure created, and saved and such,
 * and to makes it so that objects can be shared between users.  This doesn't need to be an extravagant
 * cache, since this application is mostly for viewing.
 *
 * @author horizon
 */
public class SchemaPageCache {

   Map<UUID, SchemaPage> pageMap = new HashMap();
   Map<UUID, Date> pageLastAccessed = new HashMap();

   private Timer timer;
   private static int SECONDS = 60*60*2;

   // So that the Databases can be given a default page to start out with.
   // This page will be generated when no page is found, but the user can
   // rename it to whatever they want.
   public static String DB_SVG_DEFAULT_NAMESPACE = "DB-SVG_DEFAULT";

///////////////////  Singletoning it  ///////////////////
   private static SchemaPageCache instance = null;

   /**
    * private constructor
    *
    * Sets timer to clear all pages not accessed within 2 hours
    */
   private SchemaPageCache() {
       timer = new Timer () ;
       timer.schedule ( new ToDoTask () , SECONDS*1000 ) ;
   }

   /**
    * returns an instance of the DAO.
    * @return
    */
   public static SchemaPageCache getInstance() {
      if (instance == null) {
         instance = new SchemaPageCache();
      }
      return instance;
   }
/////////////////////////////////////////////////////////

   /**
    * Creates or Fetches a SchemaPage by id.
    * @param id
    * @return
    */
   public SchemaPage getSchemaPageByID(UUID id) {
        if (pageMap.get(id)==null){
            pageMap.put(id,new SchemaPage(id));
        }
        pageLastAccessed.put(id, new Date());
        return pageMap.get(id);
   }

   /**
    * Creates or Fetches a SchemaPage by id.
    * @param id
    * @return
    */
   public SchemaPage getSchemaPageByID(String plainid) {
       UUID id = UUID.fromString(plainid);
        if (pageMap.get(id)==null){
            pageMap.put(id,new SchemaPage(id));
        }
        pageLastAccessed.put(id, new Date());
        return pageMap.get(id);
   }

   /**
    * makes a SchemaPage from scratch.
    */
   public SchemaPage makeSchemaPage(){
       SchemaPage sp = new SchemaPage();
       pageMap.put(sp.getId(), sp);
       pageLastAccessed.put(sp.getId(), new Date());
       return sp;
   }


   /**
    * Resets the timer on a page so it isn't garbaged.
    * @param id
    */
   public void touchSchemaPage(UUID id) {
       pageLastAccessed.put(id, new Date());
   }

   /**
    * Resets the timer on a page so it isn't garbaged.
    * @param id
    */
   public void touchSchemaPage(String id) {
       pageLastAccessed.put(UUID.fromString(id), new Date());
   }

   /**
    * Timer Task that removes old pages from the cache.
    *
    * Going by the date of last access should be sufficient, as we wouldn't
    * want to remove a page that is being used by someone.
    *
    * If someone has a page in their session then it won't be garbage collected
    * until their session expires.
    *
    * This is a very rudimentary way to cache, but it should work for our needs.
    *
    */
    class ToDoTask extends TimerTask {
        public void run() {
            System.out.println("Clearing all Expired Schema Pages");
            Date expirationTime = new Date(java.lang.System.currentTimeMillis() - SECONDS * 1000);
            for (UUID i : pageMap.keySet()) {
                if (pageLastAccessed.get(i).before(expirationTime)) {
                    pageMap.remove(pageMap.get(i));
                    pageLastAccessed.remove(pageMap.get(i));
                }
            }
        }
    }

}
