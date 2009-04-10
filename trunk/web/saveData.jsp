<%@page contentType="text/html" pageEncoding="UTF-8"%><%@page
import="DBViewer.models.*" %><%@page
import="java.util.*" %><%@page
import="java.sql.*" %><%@page
import="DBViewer.objects.model.*" %><%@page
import="DBViewer.objects.view.*" %><%
    String id = request.getParameter("name");
    int i = Integer.parseInt(id);
    String x_pos = request.getParameter("x");
    String y_pos = request.getParameter("y");

    SortedSchema currentSchema = new SortedSchema();
    if (request.getSession().getAttribute("CurrentSchema")==null || request.getSession().getAttribute("CurrentSchema").getClass()!=currentSchema.getClass()) {
        request.getSession().setAttribute("CurrentSchema",currentSchema);
    } else {
        currentSchema = (SortedSchema)request.getSession().getAttribute("CurrentSchema");
    }
    currentSchema.setTableViewPosition(i, x_pos, y_pos);
 %>Table Saved <%=id%> <%=x_pos%>,<%=y_pos%>