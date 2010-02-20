/*
 * DB-SVG Copyright 2009 Derrick Bowen
 * 
 * This file is part of DB-SVG.
 *
 *   DB-SVG is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   DB-SVG is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with DB-SVG.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package DBViewer.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import DBViewer.models.*;
import DBViewer.objects.view.SortedSchema;
import java.io.PrintWriter;
import java.util.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.sql.*;

/**
 *
 * @author horizon
 */
public class MenuViewController extends HttpServlet {

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


        String m = request.getParameter("m");

// ----------------------------------->   ADD CONNECTION ACTION
        if (m != null && m.equals("addConnection")) {
            InternalDataDAO iDAO = (InternalDataDAO) request.getSession().getAttribute("iDAO");

            String title = request.getParameter("title");
            String url = request.getParameter("url");
            String driver = request.getParameter("driver");
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            boolean isValid = false;

            if (title != null && !title.equals("") && url != null && !url.equals("")
                    && driver != null && !driver.equals("") && username != null && !username.equals("")
                    && password != null && !password.equals("")) {
                isValid = true;
            }

            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                if (!isValid) {
                    out.println("0");
                } else {
                    ConnectionWrapper cw = new ConnectionWrapper();
                    cw.setTitle(title);
                    cw.setUrl(url);
                    cw.setDriver(driver);
                    cw.setUsername(username);
                    cw.setPassword(password);
                    Connection conn = null;
                    try {
                        conn = iDAO.getConnection();
                        iDAO.saveConnectionWrapperNewID(cw, conn);
                        out.println(cw.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        out.println("0");
                    } finally {
                        try {
                            conn.close();
                        } catch (Exception ex) {
                            // It's probably already closed or never opened but we'll trace it anyway
                            ex.printStackTrace();
                        }
                    }
                }

            } finally {
                out.close();
            }
// ----------------------------------->   SAVE EXISTING CONNECTION ACTION
        } else if (m != null && m.equals("saveConnection")) {
            InternalDataDAO iDAO = (InternalDataDAO) request.getSession().getAttribute("iDAO");

            String dbi = request.getParameter("dbi");
            String title = request.getParameter("title");
            String url = request.getParameter("url");
            String driver = request.getParameter("driver");
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            boolean isValid = false;

            if (title != null && !title.equals("") && url != null && !url.equals("")
                    && driver != null && !driver.equals("") && username != null && !username.equals("")
                    && password != null && !password.equals("") && dbi != null && !dbi.equals("")) {
                isValid = true;
            }

            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                if (!isValid) {
                    out.println("0");
                } else {
                     Object dbc = request.getSession().getAttribute("DB-Connections");
                    Map<String, ConnectionWrapper> cwmap = new HashMap<String, ConnectionWrapper>();

                    ConnectionWrapper cw = new ConnectionWrapper();

                    if (dbc != null && dbc.getClass() == cwmap.getClass()) {
                        cwmap = (Map<String, ConnectionWrapper>) dbc;
                        cw = cwmap.get(dbi);
                        request.getSession().setAttribute("CurrentConn", cw);
                    }
                    cw.setId(Integer.parseInt(dbi));
                    cw.setTitle(title);
                    cw.setUrl(url);
                    cw.setDriver(driver);
                    cw.setUsername(username);
                    cw.setPassword(password);
                    Connection conn = null;

                    try {
                        conn = iDAO.getConnection();
                        iDAO.saveConnectionWrapper(cw, conn);
                        SortedSchema currentSchema = (SortedSchema)request.getSession().getAttribute("CurrentSchema");
                        currentSchema.setName(title);
                        out.println("1");
                    } catch (Exception e) {
                        e.printStackTrace();
                        out.println("0");
                    } finally {
                        try {
                            conn.close();
                        } catch (Exception ex) {
                            // It's probably already closed or never opened but we'll trace it anyway
                            ex.printStackTrace();
                        }
                    }
                }

            } finally {
                out.close();
            }
// ----------------------------------->   REMOVE CONNECTION ACTION
        } else if (m != null && m.equals("removeConnection")) {
            InternalDataDAO iDAO = (InternalDataDAO) request.getSession().getAttribute("iDAO");

            String id = request.getParameter("id");


            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                    Connection conn = null;
                    try {
                        conn = iDAO.getConnection();
                        iDAO.deleteConnectionWrapper(id, conn);
                        Map<String, ConnectionWrapper> prefs = new HashMap();
                        prefs = iDAO.readAllConnectionWrappers(conn);
                        request.getSession().setAttribute("DB-Connections", prefs);
                        out.println(id);
                    } catch (Exception e) {
                        e.printStackTrace();
                        out.println("0");
                    } finally {
                        try {
                            conn.close();
                        } catch (Exception ex) {
                            // It's probably already closed or never opened but well trace it anyway
                            ex.printStackTrace();
                        }
                    }

            } finally {
                out.close();
            }

// ----------------------------------->   MAIN MENU ACTION
        } else {

            Connection conn = null;
            try {
                Context env = (Context) new InitialContext().lookup("java:comp/env");

                String iDAOpath = (String) env.lookup("AppDBLocation");

                InternalDataDAO iDAO = InternalDataDAO.getInstance(iDAOpath);
                try {
                    conn = iDAO.getConnection();
                } catch (SQLException e) {
                    System.out.println("Attempting Second DB path.");
                    iDAOpath = (String) env.lookup("AppDBLocation2");
                    iDAO.setPath(iDAOpath);
                    conn = iDAO.getConnection();
                }
                request.getSession().setAttribute("iDAO", iDAO);
                iDAO.setUpInternalDB(conn);

                Map<String, ConnectionWrapper> prefs = new HashMap();
                prefs = iDAO.readAllConnectionWrappers(conn);
                request.getSession().setAttribute("DB-Connections", prefs);


                getServletConfig().getServletContext().getRequestDispatcher(
                        "/menu.jsp").forward(request, response);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (Exception ex) {
                    // It's probably already closed or never opened but well trace it anyway
                    ex.printStackTrace();
                }
            }
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
