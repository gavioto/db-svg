
package DBViewer.objects.view;

import java.util.*;
import java.sql.*;
import javax.servlet.http.*;
import DBViewer.objects.model.*;
import DBViewer.models.*;
import DBViewer.controllers.*;
import java.io.Serializable;
/**
 *
 * @author horizon
 */
public class SortedSchema implements Serializable{

    int width = 0;
    int height = 0;
    int transx = 0;
    int transy = 0;
    List<TableView> tableViews = new ArrayList();
    List<LinkLine> lines = new ArrayList();
    Map<String, Table> tables = new HashMap<String, Table>();
    MainDAO dao = MainDAO.getInstance();
    TableViewSorter tvSorter;
    InternalDataDAO iDAO;

    boolean tablesSorted = false;

    /**
     * Contains Logic for preparing a schema's tables.
     * checks to see if it has already been sorted, and things like that.
     *
     * wraps the table sorter controller
     * 
     * @param session
     * @param dbi
     */
    public void prepareSchema(HttpSession session, String dbi){
        try {
            iDAO = (InternalDataDAO)session.getAttribute("iDAO");
        } catch (Exception e) {
            e.printStackTrace();
            iDAO = new InternalDataDAO("~/");
        }
        tvSorter = new TableViewSorter(iDAO);
        boolean isNewTables = readTables(session, dbi);
        prepareTableViews(isNewTables);
        calcDimensions();
    }

    /**
     * Reads the table objects from the database based on the DBI session variable
     * @param session
     * @param dbi
     */
    private boolean readTables(HttpSession session, String dbi) {
        boolean newTables = false;
        if (session.getAttribute("CurrentDBI")==null || 
                (session.getAttribute("CurrentDBI")!=null && !session.getAttribute("CurrentDBI").equals(dbi))) {

            Object dbc = session.getAttribute("DB-Connections");
            Map<String,ConnectionWrapper> cwmap = new HashMap<String,ConnectionWrapper>();

            if (dbc!=null && dbc.getClass()==cwmap.getClass()) {
              cwmap = (Map<String,ConnectionWrapper>)dbc;

              try {
                 Connection conn = cwmap.get(dbi).getConnection();
                 tables = dao.getTables(conn,cwmap.get(dbi).getTitle());
                 session.setAttribute("CurrentDBI",dbi);
                 newTables = true;
              } catch (Exception e) {
                 e.printStackTrace();
             }
          }
       }
        return newTables;
    }

    private void prepareTableViews(boolean isNewTables) {
//        if (isNewTables){
//            tableViews = tvSorter.SortAction(tables,false);
//            tablesSorted = true;
//        }
        if (!tablesSorted || isNewTables) {
            tableViews = tvSorter.SortAction(tables,false);
            tablesSorted = true;
        }
        lines = tvSorter.calcLines(tableViews);
    }

/**
 * Calculates the schema height width and translation values based on the tableviews
 */
    private void calcDimensions() {
        double minx = 2000;
        double miny = 2000;
        double maxx = 0;
        double maxy = 0;

        for (TableView tv : tableViews) {
            double x = tv.getX();
            double y = tv.getY();

            if (x < minx) minx = x;
            if (y < miny) miny = y;
            if (x > maxx) maxx = x;
            if (y > maxy) maxy = y;
        }
        setWidth((int)(maxx-minx)+300);
        setHeight((int)(maxy-miny)+500);
        setTransx((int)(-1 * minx)+20);
        setTransy((int)(-1 * miny)+20);
    }

    /**
     * Saves the positions in the already populated table views
     * @throws java.lang.Exception
     */
    public void saveTablePositions() throws Exception{
        Connection conn = iDAO.getConnection();
        for (TableView tv : tableViews) {
            iDAO.insertTable(tv, conn);
        }
        conn.close();
    }


    public void resortTableViews(boolean resort) {
        tableViews = tvSorter.SortAction(tables,resort);
        tablesSorted = true;
        lines = tvSorter.calcLines(tableViews);
        calcDimensions();
    }


    public void saveTableViews() {
        try {
            Connection conn = iDAO.getConnection();
            boolean success = false;
            String schema = "";
            for (TableView t : tableViews) {
                iDAO.insertTable(t, conn);
                if (!success) {
                    success = true;
                    schema = t.getTable().getSchemaName();
                }
            }
            if (success)
                iDAO.schemaSortSaved(schema, conn);
            
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setTableViewPosition(int i, String x_pos, String y_pos) {
        try {
            TableView t = tableViews.get(i);
            t.setX(Double.parseDouble(x_pos));
            t.setY(Double.parseDouble(y_pos));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<TableView> getTableViews() {
        return tableViews;
    }

    public void setTableViews(List<TableView> tableviews) {
        this.tableViews = tableviews;
    }

    public int getTransx() {
        return transx;
    }

    public void setTransx(int transx) {
        this.transx = transx;
    }

    public int getTransy() {
        return transy;
    }

    public void setTransy(int transy) {
        this.transy = transy;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public List<LinkLine> getLines() {
        return lines;
    }

    public void setLines(List<LinkLine> lines) {
        this.lines = lines;
    }

    

}
