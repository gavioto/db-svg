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

import DBViewer.objects.view.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author horizon
 */
public class ModelViewController extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // collect available parameters and initialize/retrieve objects
        String dbi = request.getParameter("dbi");
        String pageid = request.getParameter("page");
        if (pageid == null) {
            pageid = (String) request.getSession().getAttribute("pageid");
        }
        SortedSchema currentSchema = new SortedSchema();
        if (request.getSession().getAttribute("CurrentSchema") == null || request.getSession().getAttribute("CurrentSchema").getClass() != currentSchema.getClass()) {
            // if no schema loaded, save the new one to the session
            request.getSession().setAttribute("CurrentSchema", currentSchema);
        } else {
            // if a schema is found, load it from the session.
            currentSchema = (SortedSchema) request.getSession().getAttribute("CurrentSchema");
        }
        SchemaController sc = SchemaController.getInstance();
        // prepare the requested page of the schema for display.  
        SchemaPage sPage = sc.prepareSchema(currentSchema, request.getSession(), dbi, pageid);

        request.getSession().setAttribute("CurrentPage", sPage);

        String m = request.getParameter("m");

// ----------------------------------->   RESORT TABLE ACTION
        if (m!=null && m.equals("refresh")) {

            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                sc.resortTableViews(sPage);
                out.println("View Resorted");
            } finally {
                out.close();
            }
// ----------------------------------->   SET TABLE POSITION ACTION
        } else if (m!=null && m.equals("setTablePosition")) {

            String id = request.getParameter("name");
            String x_pos = request.getParameter("x");
            String y_pos = request.getParameter("y");

            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                if (id != null && x_pos != null && y_pos != null) {

                    int i = Integer.parseInt(id);
                    sPage.setTableViewPosition(i, x_pos, y_pos);
                    out.println("Table position set: " + id + x_pos + "," + y_pos);
                }
            } finally {
                out.close();
            }
// ----------------------------------->   SAVE TABLE ACTION
        } else if (m!=null && m.equals("save")) {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                sc.saveTablePositions(sPage);
                out.println("Table Positions Saved.");
            } catch (Exception e) {
                e.printStackTrace();
                out.println("Error Saving Table Positions.");
            } finally {
                out.close();
            }
// ----------------------------------->   DISPLAY TABLE ACTION
        } else {
            getServletConfig().getServletContext().getRequestDispatcher(
                    "/model.jsp").forward(request, response);
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
