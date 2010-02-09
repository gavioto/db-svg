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
import java.util.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.sql.*;

/**
 *
 * @author horizon
 */
public class MenuController extends HttpServlet {

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
        try {
            Context env = (Context) new InitialContext().lookup("java:comp/env");

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

            Map<String,ConnectionWrapper> prefs = new HashMap();
            prefs = iDAO.readAllConnectionWrappers(conn);
            request.getSession().setAttribute("DB-Connections", prefs);
            
            conn.close();

            getServletConfig().getServletContext().getRequestDispatcher(
                        "/menu.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
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
