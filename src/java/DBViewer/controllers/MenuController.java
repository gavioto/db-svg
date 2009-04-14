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

    String[] ParameterColumns = {"title","url","driver","username","password"};

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
                    "        <title>DB-SVG Menu</title>" +
                    "        <link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"css/style.css\" />" +
                    "        <link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"css/jq-ui.css\" />" +
                    "        <script type=\"text/javascript\" src=\"js/jquery.js\"></script>" +
                    "   </head>" +
                    "   <body>" +
                    "    <div class=\"titleBox\">" +
                    "        <div class=\"titleLeft\"></div>" +
                    "        <div class=\"titleRight\"></div>" +
                    "            <div class=\"titleBody\">" +
                    "            <h1>DB-SVG Menu</h1>" +
                    "        </div>" +
                    "    </div>" +
                    "    <div id=\"content\" class=\"contentBoxMenu\">" +
                    "<h2>Exploreable Databases:</h2>");
            out.println("<ul>");
            for (ConnectionWrapper cw : prefs.values()) {
                out.println("<li><a href=\"model.jsp?dbi="+cw.getId()+"\" >"+cw.getTitle()+"</a></li>");
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
    Map<String,ConnectionWrapper> prefs = new HashMap<String,ConnectionWrapper>();
    List<List<String>> DBparams = openXMLFile(path);
    int i = 1;
    for (List<String> c : DBparams ) {
        ConnectionWrapper cw = new ConnectionWrapper();
        cw.setId(i);
        cw.setTitle(c.get(0));
        cw.setUrl(c.get(1));
        cw.setDriver(c.get(2));
        cw.setUsername(c.get(3));
        cw.setPassword(c.get(4));
        prefs.put(""+cw.getId(),cw);
        i++;
    }
    return prefs;
}
 /**
  * creates a new instance of XMLTableWriter, creates a DOM document from the file
  *  on disk, then uses that document to get the column header array and the List
  *  of lists.  throws an exception if it runs into any problems
  */
private List<List<String>> openXMLFile (String path) throws Exception{
    XMLReadWriter xwriter = new XMLReadWriter();

    Document doc = xwriter.DocOpen(path);
    String[] cols = xwriter.readInColumnNames(doc);
    for(int i=0; i<cols.length; i++){
        if (!cols[i].equals(ParameterColumns[i])) {
            throw new Exception("Invalid DB definition XML file");
        }
    }
    return xwriter.readInList(doc,cols);
}

 /**
  * A generic save method.  this should be directed to the desired private save
  *  method.  in this case it is XML.
  */
public void saveFile (String path, List<List<String>> data){
    saveXMLFile(path,data);
}

 /**
  * creates a new instance of XMLTableWriter, and then feeds it the file path,
  * the List of Lists, and the Column Headers array.
  */
private void saveXMLFile (String path, List<List<String>> data){
    XMLReadWriter xwriter = new XMLReadWriter();
    try {
        xwriter.writeOut(path, data, ParameterColumns);
    } catch (Exception ex) {
        ex.printStackTrace();
    }
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
