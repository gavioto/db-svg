<%-- 
    Document   : schema
    Created on : Mar 28, 2009, 5:47:13 PM
    Author     : horizon
--%>

<%@page contentType="image/svg+xml" pageEncoding="UTF-8"%>
<%@page import="DBViewer.objects.model.*" %>
<%@page import="DBViewer.objects.view.*" %>
<%@page import="DBViewer.controllers.*" %>
<%@page import="DBViewer.controllers.*" %>
<%@page import="java.util.*" %>
<%
String dbi = (String)request.getSession().getAttribute("CurrentDBI");
String pageid = (String)request.getSession().getAttribute("pageid");

if (dbi!=null) {

    SortedSchema currentSchema = new SortedSchema();
    if (request.getSession().getAttribute("CurrentSchema")==null || request.getSession().getAttribute("CurrentSchema").getClass()!=currentSchema.getClass()) {
        request.getSession().setAttribute("CurrentSchema",currentSchema);
    } else {
        currentSchema = (SortedSchema)request.getSession().getAttribute("CurrentSchema");
    }
    SchemaController sc = SchemaController.getInstance();
    SchemaPage sPage = sc.prepareSchema(currentSchema,request.getSession(), dbi, pageid);

    List<TableView> tableViews = sPage.getTableViews();
    
%>
<svg
   xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:cc="http://creativecommons.org/ns#"
   xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
   xmlns:svg="http://www.w3.org/2000/svg"
   xmlns="http://www.w3.org/2000/svg"
   xmlns:sodipodi="http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd"
   xmlns:inkscape="http://www.inkscape.org/namespaces/inkscape"
   xmlns:xlink="http://www.w3.org/1999/xlink"
   width="<%= sPage.getWidth() %>"
   height="<%= sPage.getHeight() %>"
   transform="translate(<%= sPage.getTransx() %>,<%= sPage.getTransy() %>)"
   id="canvas"
   sodipodi:version="0.32"
   inkscape:version="0.46"
   version="1.0"
   sodipodi:docname="template.svg"
   onload="init();" >
<svg:g id="<%= sPage.getId() %>"
   transform="translate(<%= sPage.getTransx() %>,<%= sPage.getTransy() %>)">
<%
List<LinkLine> lines = sPage.getLines();
            for (LinkLine l : lines) {
                request.getSession().setAttribute("CurrentLine",l);

                %>
                <jsp:include page="line.jsp" ></jsp:include>
                <%
            }

            for (TableView t : tableViews) {
                
                request.getSession().setAttribute("CurrentTableView",t);
                
                %>
                <jsp:include page="table.jsp" >
                    <jsp:param name="transx" value="<%= t.getX() %>" />
                    <jsp:param name="transy" value="<%= t.getY() %>" />
                </jsp:include>
        <%
            }
          } else { %>
          <svg:g>
            <svg:rect width="350" height="50" x="20" y="90" fill="#9a564c"></svg:rect>
            <svg:text x="40" y="120" fill="white" font-weight="bold"> 
              No Schema Loaded</svg:text>
          </svg:g>
       <% } %>
       </svg:g>
   </svg>

