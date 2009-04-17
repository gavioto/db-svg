<%-- 
    Document   : PopulateDB
    Created on : Mar 28, 2009, 4:37:08 PM
    Author     : horizon
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="DBViewer.models.*" %>
<%@page import="java.util.*" %>
<%@page import="java.sql.*" %>
<%@page import="DBViewer.objects.model.*" %>
<%@page import="DBViewer.objects.view.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>DB-SVG - Populating Data Model</title>
        <link rel="stylesheet" type="text/css" media="screen" href="css/style.css" />
        <link rel="stylesheet" type="text/css" media="screen" href="css/jq-ui.css" />
        <script type="text/javascript" src="js/jquery.js"></script>
    </head>
    <body>
    <div class="titleBox">
            <div class="titleLeft"></div>
            <div class="titleRight"></div>
            <div class="titleBody">
            <h1>DB-SVG - Populating Data Model</h1>
        </div>
    </div>
    <div id="content" class="contentBox">
        <div class="menu">
        <a href="Menu">Back to Menu</a><a href="model.jsp?dbi=<%= request.getParameter("dbi") %>">SVG Diagram</a>
        </div>
        <%

    String dbi = request.getParameter("dbi");

    SortedSchema currentSchema = new SortedSchema();
    if (request.getSession().getAttribute("CurrentSchema")==null || request.getSession().getAttribute("CurrentSchema").getClass()!=currentSchema.getClass()) {
        request.getSession().setAttribute("CurrentSchema",currentSchema);
    } else {
        currentSchema = (SortedSchema)request.getSession().getAttribute("CurrentSchema");
    }
    currentSchema.prepareSchema(request.getSession(), dbi);

          for (TableView tv : currentSchema.getTableViews()) {
              Table t = tv.getTable();
             out.println("<h3 class=\"ui-widget-header ui-corner-all\">"+t.getName()+"</h3><ul>");
             Map<String,Column> cols = t.getColumns();
             for (Column c : cols.values()) {
                String fk = "";
                if (t.getForeignKeys().containsKey(c.getName())){
                   ForeignKey key = t.getForeignKeys().get(c.getName());
                   fk = " FK->"+key.getReferencedTable()+"."+key.getReferencedColumn();
                }
                out.println("<li>"+c.getName()+(t.getPrimaryKeys().containsKey(c.getName())?" PK":"")+fk+"</li>");
             }
             for (Table rt : t.getReferencingTables().values()) {

             out.println("&nbsp;&nbsp;&nbsp;&nbsp;    referenced by: "+rt.getName()+"<br />");
             }
             out.println("</ul>");
          }

        %>
    </div>
    </body>
</html>
