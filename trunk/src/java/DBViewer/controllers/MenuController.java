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

    private static String[] PossibleColumns = {"title","url","driver","username","password","pages",
                                               "Title","Url","Driver","Username","Password","Pages",
                                               "TITLE","URL","DRIVER","USERNAME","PASSWORD","PAGES"};

    private static String[] ParameterColumns = {"title","url","driver","username","password","pages"};

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
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
            request.getSession().setAttribute("iDAO", iDAO);
            Connection conn = iDAO.getConnection();
            iDAO.createTables(conn);
            conn.close();

            Map<String,ConnectionWrapper> prefs = getDBPrefs(path);

            request.getSession().setAttribute("DB-Connections", prefs);
            
            // TODO output your page here
            out.println("<html>" +
                    "    <head>" +
                    "        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
                    "        <title>DB-SVG | Menu</title>" +
                    "        <link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"css/style.css\" />" +
                    "        <link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"css/jq-ui.css\" />" +
                    "        <script type=\"text/javascript\" src=\"js/jquery.js\"></script>" +
                    "   </head>" +
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
                out.println("<li><a href=\"model.jsp?dbi="+cw.getId()+"\" >"+cw.getTitle()+"</a>  <span class=\"menusetup\"><a href=\"setup.jsp?dbi="+cw.getId()+"\">setup</a></span></li>");
            }
            out.println("</ul>");
            out.println("</div>");
            out.println("<div class=\"footer\">");
            out.println("You can add more databases in the dbs.xml config file");
            out.println("</div>");
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
