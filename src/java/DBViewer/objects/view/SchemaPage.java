
package DBViewer.objects.view;

import java.util.*;
import java.io.Serializable;
/**
 *
 * @author horizon
 */
public class SchemaPage implements Comparable<SchemaPage>,Serializable{

    List<TableView> tableViews = new ArrayList();
    String title;
    UUID id;
    int orderid;
    int width = 0;
    int height = 0;
    int transx = 0;
    int transy = 0;
    SortedSchema schema;
    List<LinkLine> lines = new ArrayList();

    public SchemaPage(){
        this.id = UUID.randomUUID();
    }

    public SchemaPage(UUID id){
        this.id = id;
    }

    public SchemaPage(String id){
        this.id = UUID.fromString(id);
    }
    
    /**
     * Calculates the schema height width and translation values based on the tableviews
     */
    public void calcDimensions() {
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

    public void setTableViewPosition(int i, String x_pos, String y_pos) {
        try {
            TableView t = tableViews.get(i);
            t.setX(Double.parseDouble(x_pos));
            t.setY(Double.parseDouble(y_pos));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<TableView> getTableViews() {
        return tableViews;
    }

    public void setTableViews(List<TableView> tableViews) {
        this.tableViews = tableViews;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * sets the UUID by String
     * @param id
     */
    public void setId(String id) {
        this.id = UUID.fromString(id);
    }

    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
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

    public SortedSchema getSchema() {
        return schema;
    }

    public void setSchema(SortedSchema schema) {
        this.schema = schema;
    }

    public List<LinkLine> getLines() {
        return lines;
    }

    public void setLines(List<LinkLine> lines) {
        this.lines = lines;
    }

    public boolean areTableViewsClean() {
        boolean allClean = true;
        for(TableView tv : tableViews) {
            if (tv.isDirty())
                allClean = false;
        }
        return allClean;
    }

    public int compareTo(SchemaPage o) {
        if (this.orderid == o.getOrderid())
            return 0;
        else if (this.orderid > o.getOrderid())
            return 1;
        else
            return -1;
    }
    

}
