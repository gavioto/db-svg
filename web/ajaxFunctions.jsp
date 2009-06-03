<%--
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
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%><%@page
import="DBViewer.models.*" %><%@page
import="java.util.*" %><%@page
import="java.sql.*" %><%@page
import="DBViewer.objects.model.*" %><%@page
import="DBViewer.objects.view.*" %><%@page
import="DBViewer.controllers.*" %><%
    String dbi = request.getParameter("dbi");
    String pageid = request.getParameter("page");
    if (pageid==null) {
        pageid = (String)request.getSession().getAttribute("pageid");
    }

    SortedSchema currentSchema = new SortedSchema();
    if (request.getSession().getAttribute("CurrentSchema")==null || request.getSession().getAttribute("CurrentSchema").getClass()!=currentSchema.getClass()) {
        // if no schema loaded, save the new one to the session
        request.getSession().setAttribute("CurrentSchema",currentSchema);
    } else {
        // if a schema is found, load it from the session.
        currentSchema = (SortedSchema)request.getSession().getAttribute("CurrentSchema");
    }
    SchemaController sc = SchemaController.getInstance();
    SchemaPage sPage = sc.prepareSchema(currentSchema,request.getSession(), dbi, pageid);

    String id = request.getParameter("name");
    String x_pos = request.getParameter("x");
    String y_pos = request.getParameter("y");
if (id!=null && x_pos!=null && y_pos!=null) {

    int i = Integer.parseInt(id);
    sPage.setTableViewPosition(i, x_pos, y_pos);
    %>Table Saved <%=id%> <%=x_pos%>,<%=y_pos%><%
}

boolean refresh = request.getParameter("refresh")!=null;
    if (refresh)
        sc.resortTableViews(sPage);

boolean save = request.getParameter("save")!=null;
    if (save) {
        sc.saveTablePositions(sPage);
        %>Table Positions Saved.<%
        }
    
 %>