<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="DBViewer.models.*" %>
<%@page import="java.util.*" %>
<%@page import="java.sql.*" %>
<%@page import="DBViewer.objects.model.*" %>
<%@page import="DBViewer.objects.view.*" %>
<%@page import="DBViewer.controllers.*" %><%

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
    sc.prepareSchema(currentSchema,request.getSession(), dbi, pageid);
%>
<h3 class="ui-widget-header ui-corner-all">Schema Information: <%= currentSchema.getName() %></h3>
<table class="info">
    <tr>
        <td class="label">Pages: </td><td class="data"><%= currentSchema.getPages().size() %></td>
    </tr>
    <tr>
        <td class="label">Tables: </td><td class="data"><%= currentSchema.getTables().size() %></td>
    </tr>
</table>
    <br />
    <h3 class="ui-widget-header ui-corner-all">Text Version </h3>
<%

List<Table> tables = new ArrayList();
tables.addAll(currentSchema.getTables().values());
Collections.sort(tables);
      for (Table t : tables) {
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