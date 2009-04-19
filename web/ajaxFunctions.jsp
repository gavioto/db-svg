<%@page contentType="text/html" pageEncoding="UTF-8"%><%@page
import="DBViewer.models.*" %><%@page
import="java.util.*" %><%@page
import="java.sql.*" %><%@page
import="DBViewer.objects.model.*" %><%@page
import="DBViewer.objects.view.*" %><%
SortedSchema currentSchema = new SortedSchema();
    if (request.getSession().getAttribute("CurrentSchema")==null || request.getSession().getAttribute("CurrentSchema").getClass()!=currentSchema.getClass()) {
        request.getSession().setAttribute("CurrentSchema",currentSchema);
    } else {
        currentSchema = (SortedSchema)request.getSession().getAttribute("CurrentSchema");
    }

    String id = request.getParameter("name");
    String x_pos = request.getParameter("x");
    String y_pos = request.getParameter("y");
if (id!=null && x_pos!=null && y_pos!=null) {

    int i = Integer.parseInt(id);
    currentSchema.setTableViewPosition(i, x_pos, y_pos);
    %>Table Saved <%=id%> <%=x_pos%>,<%=y_pos%><%
}

boolean refresh = request.getParameter("refresh")!=null;
    if (refresh)
        currentSchema.resortTableViews(true);

boolean save = request.getParameter("save")!=null;
    if (save) {
        currentSchema.saveTablePositions();
        %>Table Positions Saved.<%
        }
    
 %>