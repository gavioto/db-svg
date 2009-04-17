<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

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
        <title>DB-SVG Data Model Explorer</title>
        <link rel="stylesheet" type="text/css" media="screen" href="css/style.css" />
        <link rel="stylesheet" type="text/css" media="screen" href="css/jquery.svg.css" />
        <link rel="stylesheet" type="text/css" media="screen" href="css/jq-ui.css" />
        <script type="text/javascript" src="js/jquery.js"></script>
        <script type="text/javascript" src="js/jquery.svg.js"></script>
        <script type="text/javascript" src="js/jquery.svganim.js"></script>
<script type="text/javascript">
var svg;
var onTable = false;
var currentTable;
var currentDragx;
var currentDragy;

<%
    String dbi = request.getParameter("dbi");
    
    SortedSchema currentSchema = new SortedSchema();
    if (request.getSession().getAttribute("CurrentSchema")==null || request.getSession().getAttribute("CurrentSchema").getClass()!=currentSchema.getClass()) {
        request.getSession().setAttribute("CurrentSchema",currentSchema);
    } else {
        currentSchema = (SortedSchema)request.getSession().getAttribute("CurrentSchema");
    }
    currentSchema.prepareSchema(request.getSession(), dbi);

    boolean refresh = request.getParameter("refresh")!=null;
    if (refresh)
        currentSchema.resortTableViews(true);

    boolean save = request.getParameter("save")!=null;
    if (save)
        currentSchema.saveTablePositions();

%>

  var transx = (-1*<%= currentSchema.getTransx() %>)-30;
  var transy = (-1*<%= currentSchema.getTransy() %>)-100;

$(function() {

      svg = $('#svgwindow').svg({loadURL: 'schema.svg.jsp'});

      svg.mousemove(function (e){
           if (onTable) {
              var scr = getScrollXY();
              var scrOfX = scr[0], scrOfY = scr[1];
              
              var ex = e.pageX;
              var ey = e.pageY;

              currentDragx = (ex+transx+scrOfX);
              currentDragy = (ey+transy+scrOfY);
              $('#coord').html('x:'+(currentDragx)+' y:'+(currentDragy)+' '+scrOfX+','+scrOfY);
              currentTable.setAttribute('transform','translate('+(currentDragx)+','+(currentDragy)+')');
          } 
      });
});

    function tableDown(evt) {
        currentTable = evt.parentNode;
        onTable = true;
    }
    
    function tableUp(evt) {
        onTable = false;
        var xx = currentDragx;
        var yy = currentDragy;
        $.post("saveData.jsp", { "name": evt.id, "x": currentDragx, "y": currentDragy },
           function(data){
            // alert("Data Loaded: " + data);
            location.reload(true);
           });

        //svg = $('#svgwindow').svg({loadURL: 'schema.svg.jsp'});
    }

    function tableMove(evt) {
    }
    function getScrollXY() {
	var sX = 0, sY = 0;
          if( document.body && ( document.body.scrollLeft || document.body.scrollTop ) && ( document.body.scrollLeft!=0 || document.body.scrollTop!=0 ) ) {
              // DOM compliant
                sY = document.body.scrollTop; sX = document.body.scrollLeft;
          } else if( typeof( window.pageYOffset ) == 'number' && window.pageYOffset!=0) {
              //Netscape compliant
                sY = window.pageYOffset; sX = window.pageXOffset;
          } else if( document.documentElement && ( document.documentElement.scrollLeft || document.documentElement.scrollTop )  && ( document.documentElement.scrollLeft!=0 || document.documentElement.scrollTop!=0 )) {
              //IE6 standards compliant mode
                sY = document.documentElement.scrollTop; sX = document.documentElement.scrollLeft;
          }
              $('#coord').html('x:'+(currentDragx)+' y:'+(currentDragy)+' '+sX+','+sY);
	return [ sX, sY ];
}
function isWebKit(){
    return RegExp(" AppleWebKit/").test(navigator.userAgent);
}


</script>
    </head>
    <body>
    <div class="titleHeader" style="">
            <div class="titleLeft"></div>
            <div class="titleRight"></div>
            <div class="titleBody">
            <h1>DB-SVG Data Model Explorer</h1>
        </div>
    </div>
    <div id="content" class="modelContentBox">
        <div class="tablayer">
<a href="#"><div class="tab">Tab 2</div></a>
<a href="#"><div class="tab">Tab 1</div></a>
        </div>
        <div class="menu">
            <a href="Menu">Back to Menu</a><a href="populate.jsp?dbi=<%= dbi %>">Text Version</a><a href="model.jsp?dbi=<%= dbi %>&refresh=true">Re-sort</a><a href="model.jsp?dbi=<%= dbi %>&save=true">Save View</a><a href="schema.svg.jsp" target="_blank">Download Diagram</a>
        </div>
      <!--  <a href="#" id="zoomin">Zoom In</a>
        <a href="#" id="zoomout">Zoom Out</a> -->
        <span style="display:none; float:right; padding-right:20px;" id="coord">x-y</span>
        <div class="svgwrapper" style="width:<%= currentSchema.getWidth() %>px;height:<%= currentSchema.getHeight() %>px;">
            <div id="svgwindow" class="svgwindow"></div>
        </div>
    </div>
    <div style="padding-left:200px; display:none;"><a href="javascript:getScrollXY();">get the scrolling offsets</a></div>
    </body>
</html>
