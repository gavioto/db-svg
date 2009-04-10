
package DBViewer.models;

import java.sql.*;
import java.io.Serializable;

/**
 *
 * @author horizon
 */
public class ConnectionWrapper implements Serializable{

    private int id;
    private String title = "";
    private String url = "";
    private String driver = "";
    private String username = "";
    private String password = "";

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

      public Connection getConnection() {
       try {
        Class.forName(driver).newInstance();
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
       } catch (Exception e){
          e.printStackTrace();
       }
       return null;
    }

    
}
