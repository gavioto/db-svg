<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="DBViewer.models.*" %>
<%@page import="java.util.*" %>
<%@page import="java.sql.*" %>
<%@page import="DBViewer.objects.model.*" %>
<%@page import="DBViewer.objects.view.*" %><%

    String dbi = request.getParameter("dbi");

    SortedSchema currentSchema = new SortedSchema();
    if (request.getSession().getAttribute("CurrentSchema")==null || request.getSession().getAttribute("CurrentSchema").getClass()!=currentSchema.getClass()) {
        request.getSession().setAttribute("CurrentSchema",currentSchema);
    } else {
        currentSchema = (SortedSchema)request.getSession().getAttribute("CurrentSchema");
    }
    currentSchema.prepareSchema(request.getSession(), dbi);
%>
<h3 class="ui-widget-header ui-corner-all">Schema Information: <%= currentSchema.getName() %></h3>
<table class="info">
    <tr>
        <td class="label">Pages: </td><td class="data"><%= currentSchema.getPages().size() %></td>
    </tr>
    <tr>
        <td class="label">Tables: </td><td class="data"><%= currentSchema.getTableViews().size() %></td>
    </tr>
    <tr>
        <td class="label">Height: </td><td class="data"><%= currentSchema.getHeight() %></td>
    </tr>
    <tr>
        <td class="label">Width: </td><td class="data"><%= currentSchema.getWidth() %></td>
    </tr>
</table>
    <br />
    <h3 class="ui-widget-header ui-corner-all">Text Version </h3>
<%
      for (TableView tv : currentSchema.getTableViews()) {
              Table t = tv.getTable();
             out.println("<h4 >"+t.getName()+"</h4><ul>");
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