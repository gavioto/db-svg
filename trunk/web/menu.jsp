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

    Document   : menu
    Created on : Feb 8, 2010, 10:27:33 PM
    Author     : Derrick Bowen
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="DBViewer.models.*" %>
<%@page import="java.util.*" %>
<%@page import="DBViewer.objects.model.*" %>
<%@page import="DBViewer.objects.view.*" %>
<%@page import="DBViewer.controllers.*" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>DB-SVG | Menu</title>
        <link rel="stylesheet" type="text/css" media="screen" href="css/style.css" />
        <link rel="stylesheet" type="text/css" media="screen" href="css/jq-ui.css" />
        <link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
        <script type="text/javascript" src="js/jquery.js"></script>
        <script type="text/javascript" src="js/jquery-ui.min.js"></script>
        <script type="text/javascript">
            $(function() {
                $("#loadDialog").dialog({
                    autoOpen: false,
                    bgiframe: true,
                    resizable: false,
                    height:80,
                    modal: true
                });
            });
            function showLoading() {
                $("#loadDialog").dialog("open");
            }
        </script>
    </head>
    <body>
        <div class="titleHeader">
            <div class="titleLeft"></div>
            <div class="titleRight"></div>
            <div class="titleBody">
                <h1>DB-SVG Menu</h1>
            </div>
        </div>
        <div id="content" class="contentBoxMenu">
            <h2>Exploreable Databases:</h2>
            <ul class="frontmenulist">
<%
Map<String,ConnectionWrapper> prefs = (Map<String,ConnectionWrapper>)request.getSession().getAttribute("DB-Connections");
for (ConnectionWrapper cw : prefs.values()) {
    out.println("<li><a href=\"model.jsp?dbi="+cw.getId()+"\" onclick=\"showLoading();\" >"+cw.getTitle()+"</a><span class=\"frontmenusetup\"><a href=\"setup.jsp?dbi="+cw.getId()+"\" onclick=\"showLoading();\" ><img src=\"images/document-properties.png\" alt=\"setup\"></a></span><span class=\"frontmenuremove\"><a href=\"setup.jsp?dbi="+cw.getId()+"\" onclick=\"showLoading();\" ><img src=\"images/edit-delete.png\" alt=\"remove\"></a></span></li>");
}
%>
                <li class="frontmenuadd"><a href="#">+ Add Connection</a></li>
            </ul>
            <div id="loadDialog" class="loadDialog ui-dialog" title="Loading, Please Wait...">
                <p>
                    <center><img src="images/ajax-loader.gif" alt="The database is being read into memory."/></center>
                </p>
            </div>

        </div>
    </body>
</html>
