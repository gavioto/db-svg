/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DBViewer.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import DBViewer.models.*;
import org.w3c.dom.*;
import java.util.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.sql.*;

/**
 *
 * @author horizon
 */
public class MenuController extends HttpServlet {

    private static String[] PossibleColumns = {"title","url","driver","username","password",
                                               "Title","Url","Driver","Username","Password",
                                               "TITLE","URL","DRIVER","USERNAME","PASSWORD",};

    private static String[] ParameterColumns = {"title","url","driver","username","password"};

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * creates a the main menu page for the application.  Reads the settings for the dbs.xml config file,
     * as well as the path to the internal database.  If the initial context values are not correct, it
     * tries the failover values.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            Context env = (Context) new InitialContext().lookup("java:comp/env");
            String path = (String)env.lookup("DBXMLPATH");

            String iDAOpath = (String)env.lookup("AppDBLocation");

            InternalDataDAO iDAO = InternalDataDAO.getInstance(iDAOpath);
            Connection conn = null;
            try {
                conn = iDAO.getConnection();
            } catch (SQLException e) {
                System.out.println("Attempting Second DB path.");
                iDAOpath = (String)env.lookup("AppDBLocation2");
                iDAO.setPath(iDAOpath);
                conn = iDAO.getConnection();
            }
            request.getSession().setAttribute("iDAO", iDAO);
            iDAO.setUpInternalDB(conn);
            conn.close();

            Map<String,ConnectionWrapper> prefs = new HashMap();
            try {
                prefs = getDBPrefs(path);
            } catch (IOException e) {
                System.out.println("Attempting Second XML config file path.");
                path = (String)env.lookup("DBXMLPATH2");
                prefs = getDBPrefs(path);
            }
            request.getSession().setAttribute("DB-Connections", prefs);
            
            // TODO output your page here
            out.println("<html>" +
                    "    <head>" +
                    "        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
                    "        <title>DB-SVG | Menu</title>" +
                    "        <link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"css/style.css\" />" +
                    "        <link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"css/jq-ui.css\" />" +
                    "        <script type=\"text/javascript\" src=\"js/jquery.js\"></script>\n" +
                    "        <script type=\"text/javascript\" src=\"js/jquery-ui.min.js\"></script>\n" +
                    "        <script type=\"text/javascript\">\n" +
                    "" +
                    "function showLoading() {" +
                    "        $(\"#loadDialog\").dialog({" +
                    "			bgiframe: true," +
                    "           modal: true," +
                    "           height: 10" +
                    "		});\n" +
                    "}" +
                    "        </script>\n" +
                    "   </head>\n" +
                    "   <body>" +
                    "    <div class=\"titleBox\">" +
                    "       <div class=\"titleHeader\">" +
                    "        <div class=\"titleLeft\"></div>" +
                    "        <div class=\"titleRight\"></div>" +
                    "            <div class=\"titleBody\">" +
                    "            <h1>DB-SVG Menu</h1>" +
                    "        </div>" +
                    "       </div>" +
                    "    </div>" +
                    "    <div id=\"content\" class=\"contentBoxMenu\">" +
                    "<h2>Exploreable Databases:</h2>");
            out.println("<ul>");
            for (ConnectionWrapper cw : prefs.values()) {
                out.println("<li><a href=\"model.jsp?dbi="+cw.getId()+"\" onclick=\"showLoading();\" >"+cw.getTitle()+"</a>  <span class=\"menusetup\"><a href=\"setup.jsp?dbi="+cw.getId()+"\" onclick=\"showLoading();\" >setup</a></span></li>");
            }
            out.println("</ul>" +
                    "<div id=\"loadDialog\" class=\"loadDialog\" title=\"Loading, Please Wait...\">" +
                    "<p>The database is being read into memory.</p>" +
                    "</div>");
            out.println("</div>");
            out.println("<div class=\"footer\">");
            out.println("You can add more databases in the dbs.xml config file.<br/>");
            out.println("<span class=\"footlight\">*If your database is very large, you'll probably want to set it up before you try to view it.</span>");
            out.println("</body>");
            out.println("</html>");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }
private Map<String,ConnectionWrapper> getDBPrefs (String path) throws Exception{

    XMLStringMapReader xsmr = new XMLStringMapReader();
    List<Map<String,String>> dbVals = xsmr.ParseFile(path, PossibleColumns);
    
    Map<String,ConnectionWrapper> prefs = new HashMap<String,ConnectionWrapper>();
    int i = 1;
    for (Map<String,String> dbentry : dbVals) {
        ConnectionWrapper cw = new ConnectionWrapper();
        cw.setId(i);
        cw.setTitle(dbentry.get(ParameterColumns[0]));
        cw.setUrl(dbentry.get(ParameterColumns[1]));
        cw.setDriver(dbentry.get(ParameterColumns[2]));
        cw.setUsername(dbentry.get(ParameterColumns[3])!=null ? dbentry.get(ParameterColumns[3]) :"");
        cw.setPassword(dbentry.get(ParameterColumns[4])!=null ? dbentry.get(ParameterColumns[4]) :"");
        prefs.put(""+cw.getId(),cw);
        i++;
    }
    return prefs;
}

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
