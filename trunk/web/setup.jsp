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
        <title>DB-SVG | Schema Setup</title>
        <link rel="stylesheet" type="text/css" media="screen" href="css/style.css" />
        <link rel="stylesheet" type="text/css" media="screen" href="css/jq-ui.css" />
        <script type="text/javascript" src="js/jquery.js"></script>
        <script type="text/javascript">
    var dbi = <%= request.getParameter("dbi") %>;

    $(function() {

        $('#info').click(function(){
           $.post("setupInfo.jsp", { "dbi": dbi },
           function(data){
               $('#changer').html(data);
           });
           return false;
      });

      $('#pages').click(function(){
           $.post("setupPages.jsp", { "refresh": "true" },
           function(data){
               $('#changer').html(data);
           });
           return false;
      });
    });

        </script>
    </head>
    <body>
    <div class="titleBox">
        <div class="titleHeader">
            <div class="titleLeft"></div>
            <div class="titleRight"></div>
            <div class="titleBody">
                <h1>DB-SVG Schema Setup</h1>
            </div>
        </div>
        <div class="menu">
        <a href="Menu">Back to Menu</a><a href="model.jsp?dbi=<%= request.getParameter("dbi") %>">Diagram</a><a id="info" href="#schemaInfo">Schema Info</a><a id="pages" href="#pagesConfig">Pages</a>
        </div>
    </div>
    <div id="content" class="contentBox">
      <div id="changer">
          <jsp:include page="setupInfo.jsp"></jsp:include>
      </div>
    </div>
    </body>
</html>