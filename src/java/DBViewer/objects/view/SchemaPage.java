
package DBViewer.objects.view;

import java.util.*;
/**
 *
 * @author horizon
 */
public class SchemaPage {

    List<TableView> tableViews = new ArrayList();
    String title;
    int id;
    int orderid;
    int width = 0;
    int height = 0;
    int transx = 0;
    int transy = 0;

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

    public List<TableView> getTableViews() {
        return tableViews;
    }

    public void setTableViews(List<TableView> tableViews) {
        this.tableViews = tableViews;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
    

}
