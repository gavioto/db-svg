
package DBViewer.controllers;

import java.util.*;
import java.sql.*;
import javax.servlet.http.*;
import DBViewer.objects.model.*;
import DBViewer.objects.view.*;
import DBViewer.models.*;

/**
 * Controller in charge of reading, saving, and sorting a schema.
 * @author horizon
 */
public class SchemaController {

    MainDAO dao = MainDAO.getInstance();
    TableViewSorter tvSorter;
    InternalDataDAO iDAO;

///////////////////  Singletoning it  ///////////////////
   private static SchemaController instance = null;

   /**
    * private constructor
    */
   private SchemaController () {
   }

   /**
    * returns an instance of the Controller.
    * @return
    */
   public static SchemaController getInstance() {
      if (instance == null) {
         instance = new SchemaController();
      }
      return instance;
   }

   /**
     * Contains Logic for preparing a schema's tables.
     * checks to see if it has already been sorted, and things like that.
     *
     * wraps the table sorter controller
     *
     * @param session
     * @param dbi
     */
    public SchemaPage prepareSchema(SortedSchema schema, HttpSession session, String dbi, String pageid){
        SchemaPage page;
        try {
            iDAO = (InternalDataDAO)session.getAttribute("iDAO");
        } catch (Exception e) {
            e.printStackTrace();
            iDAO = InternalDataDAO.getInstance("~/unset.db");
        }
        if (TableViewSorter.getInstance().getIDAO()==null) {
            tvSorter = TableViewSorter.getInstance(iDAO);
        } else {
            tvSorter = TableViewSorter.getInstance();
        }
        boolean isNewTables = false;
        if (dbi!=null)
           isNewTables = readTables(schema, session, dbi);
        
        if ((pageid!=null && !pageid.equals("")) && schema.getPages().get(UUID.fromString(pageid))!=null ) {
            try {
                UUID pid = UUID.fromString(pageid);
                page = schema.getPages().get(pid);
                prepareTableViews(page, isNewTables);
            }catch (Exception e) {
                page = new SchemaPage();
                System.out.println("Error with Page ID");
            }
        } else {
            page = schema.getFirstPage();
            prepareTableViews(page, isNewTables);
        }
        session.setAttribute("pageid", page.getId().toString());
        return page;
    }

    /**
     * Reads the table objects from the database based on the DBI session variable
     * @param session
     * @param dbi
     */
    private boolean readTables(SortedSchema schema, HttpSession session, String dbi) {
        boolean newTables = false;
        if (session.getAttribute("CurrentDBI")==null ||
                (session.getAttribute("CurrentDBI")!=null && !session.getAttribute("CurrentDBI").equals(dbi))) {

            Object dbc = session.getAttribute("DB-Connections");
            Map<String,ConnectionWrapper> cwmap = new HashMap<String,ConnectionWrapper>();

            if (dbc!=null && dbc.getClass()==cwmap.getClass()) {
              cwmap = (Map<String,ConnectionWrapper>)dbc;

              try {
                 Connection conn = cwmap.get(dbi).getConnection();
                 schema.setTables(dao.getTables(conn,cwmap.get(dbi).getTitle()));
                 schema.setName(cwmap.get(dbi).getTitle());
                 session.setAttribute("CurrentDBI",dbi);
                 newTables = true;
                 readSchemaPages(schema);
                 // maybe set defaultpage here?
              } catch (Exception e) {
                 e.printStackTrace();
             }
          }
       }
        return newTables;
    }

    /**
     * Checks to see if the Tables need to be sorted, and runs the sort if so.
     * Also generates the lines between tables
     *
     * @param isNewTables
     */
    private void prepareTableViews(SchemaPage page, boolean isNewTables) {
        List<LinkLine> lines = page.getLines();
        if (!page.areTableViewsClean() || isNewTables) {
            tvSorter.SortAction(page,false);
//            tvSorter.calcLines(page);
        } else {
            List<TableView> tablesToClean = new ArrayList();
            for (LinkLine li : lines) {
                tablesToClean.addAll(li.recalculateLine());
            }
            for (TableView tv : tablesToClean) {
                tv.setClean();
            }
        }
        page.calcDimensions();
    }

    /**
     * Reads in the pages of the Schema and assigns the tables to
     * their pages.
     *
     * @throws java.lang.Exception
     */
    private void readSchemaPages(SortedSchema schema) throws Exception{
        if (schema.getTables().size()>0) {
             Connection conn = iDAO.getConnection();
             Map<UUID,SchemaPage> pages = iDAO.readSchemaPages(schema, conn);
             if (pages.size()<1) {
                 SchemaPage sp = SchemaPageCache.getInstance().makeSchemaPage();
                 pages.put(sp.getId(), sp);
                 sp.setTitle(SchemaPageCache.DB_SVG_DEFAULT_NAMESPACE);
                 sp.setOrderid(0);
                 sp.setSchema(schema);
             }
             for (SchemaPage page: pages.values()) {
                 int i = 0;
                 for (Table t: schema.getTables().values()){
                     iDAO.makeTableSchema(t, conn);
                     TableView tv = iDAO.makeViewWCoordinates(t, page, schema.getTables().size(), conn);
                     tv.setId(i);
                     page.getTableViews().add(tv);
                     i++;
                 }
             }
             schema.setPages(pages);
             conn.close();
         }
    }

    /**
     * Saves the positions in the already populated table views
     * @throws java.lang.Exception
     */
    public void saveTablePositions(SchemaPage page) throws Exception{
        Connection conn = iDAO.getConnection();
        iDAO.verifySchema(page.getSchema().getName(), conn);
        iDAO.saveSchemaPage(page, conn);
        for (TableView tv : page.getTableViews()) {
            iDAO.saveTable(tv.getTable(), conn);
            iDAO.saveTablePosition(tv, conn);
        }
        conn.close();
    }

/**
 * public access method
 * @param resort
 */
    public void resortTableViews(boolean resort, SchemaPage currentPage) {
        tvSorter.SortAction(currentPage,resort);
        tvSorter.calcLines(currentPage);
        currentPage.calcDimensions();
    }


    public void saveTableViews(SchemaPage currentPage) {
        try {
            Connection conn = iDAO.getConnection();
            iDAO.saveSchemaPage(currentPage, conn);
            for (TableView t : currentPage.getTableViews()) {
                iDAO.saveTablePosition(t, conn);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
