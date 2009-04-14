
package DBViewer.objects.view;

import DBViewer.objects.model.*;
import java.util.*;
import java.io.Serializable;
/**
 *
 * @author horizon
 */
public class TableView implements Comparable<TableView>, Serializable{

    Table table;
    double x = 0;
    double y = 0;
    double velocityX = 0.0;
    double velocityY = 0.0;
    double radius = 0;
    int numLinks = 0;
    List<TableView> referencesTo = new ArrayList();
    List<TableView> referencedBy = new ArrayList();
    List<TableView> ref = null;
    int id = 0;
    boolean dirty = true;


    public TableView(Table t){
        table = t;
    }

    /**
     * goes through the foreign keys, populates the list of TableViews this
     */
    public void calcLinksAndRadius() {
        this.numLinks = 0;
        Map<String,ForeignKey> fkeys = this.table.getForeignKeys();
        Map<String,Table> referencers = this.table.getReferencingTables();
        for (ForeignKey fk : fkeys.values()) {
            this.getReferencesTo().add(fk.getReference().getTable().getTableView());
            this.numLinks++;
        }
        for (Table t : referencers.values()) {
            this.getReferencedBy().add(t.getTableView());
            this.numLinks++;
        }

        int height = this.getTable().getHeight();
        int width = this.getTable().getWidth();

        double diagonal = Math.sqrt(Math.pow(height,2) + Math.pow(width,2));

        this.radius = (11.0/20.0) * diagonal;
    }

    public double calcDistance(TableView o) {
        double distance = 0.0;
        double x1 = this.getX();
        double y1 = this.getY();
        double x2 = o.getX();
        double y2 = o.getY();

        distance = Math.sqrt(Math.pow(x1-x2,2) + Math.pow(y1-y2,2));

        return distance;
    }

     public double calcDistance(int x, int y) {
        double distance = 0.0;
        double x1 = this.getX();
        double y1 = this.getY();

        distance = Math.sqrt(Math.pow(x1-x,2) + Math.pow(y1-y,2));

        return distance;
    }

    public double calcDistanceWRadius(TableView o) {
        double distance = 0.0;
        double x1 = this.getX();
        double y1 = this.getY();
        double r1 = this.getRadius();
        double x2 = o.getX();
        double y2 = o.getY();
        double r2 = o.getRadius();

        distance = Math.sqrt(Math.pow(x1-x2,2) + Math.pow(y1-y2,2))-r1-r2;

        return distance;
    }

    public double calcAngle(TableView o) {
        double angle = 0.0;

        double x1 = this.getX();
        double y1 = this.getY();
        double x2 = o.getX();
        double y2 = o.getY();

        angle = Math.atan((y1-y2)/(x1-x2));
        return angle;
    }

    public double calcAngle(int x, int y) {
        double angle = 0.0;

        double x1 = this.getX();
        double y1 = this.getY();

        angle = Math.atan((y1-y)/(x1-x));
        return angle;
    }

    public int getNumLinks() {
        return numLinks;
    }

    public void setNumLinks(int numLinks) {
        this.numLinks = numLinks;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }


    public List<TableView> getReferencesTo() {
        return referencesTo;
    }

    public void setReferencesTo(List<TableView> referencesTo) {
        this.referencesTo = referencesTo;
        this.ref = null;
        getReferences();
    }

    public List<TableView> getReferencedBy() {
        return referencedBy;
    }

    public void setReferencedBy(List<TableView> referencedBy) {
        this.referencedBy = referencedBy;
        this.ref = null;
        getReferences();
    }

    public List<TableView> getReferences() {
        if (this.ref==null) {
            this.ref = new ArrayList();
            for (TableView t : this.referencedBy) {
                if (!this.ref.contains(t)) this.ref.add(t);
            }
            for (TableView t : this.referencesTo) {
                if (!this.ref.contains(t)) this.ref.add(t);
            }
        }
        return this.ref;
    }
    
    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
        this.dirty = true;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
        this.dirty = true;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
    
    public void setDirty() {
        this.dirty = true;
    }

    public void setClean() {
        this.dirty = false;
    }

    /**
     * Compares TableViews by the number of nodes that they contain.
     * @param o
     * @return
     */
    public int compareTo(TableView o) {
            // If this < o, return a negative value
        if (this.getNumLinks() <  o.getNumLinks()) return -1;
            // If this = o, return 0
        if (this.getNumLinks() == o.getNumLinks()) return 0;
            // If this > o, return a positive value
        if (this.getNumLinks() >  o.getNumLinks()) return 1;
        
        return 0;
        }

}
