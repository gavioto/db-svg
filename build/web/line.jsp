<%@page import="DBViewer.objects.view.*" %><%

LinkLine l = (LinkLine)request.getSession().getAttribute("CurrentLine");

// line main end points
double x1 = l.getX2();
double y1 = l.getY2();
double x2 = l.getX1();
double y2 = l.getY1();

//arrow head end points
double xa1 = l.getXa1();
double ya1 = l.getYa1();
double xa2 = l.getXa2();
double ya2 = l.getYa2();
double xa3 = l.getXa3();
double ya3 = l.getYa3();

%>
<g
     id="line-<%=l.getStartingTable().getName()+"->"+l.getForeignkey().getReferencedTable() %>">
    <path
       d="M <%=x1 %>,<%=y1 %> L <%=x2 %>,<%=y2 %>"
       id="path2383"
       style="fill:none;fill-rule:evenodd;stroke:#666666;stroke-width:2.2px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:.8" />
    <path
       d="M <%=xa1 %>,<%=ya1 %> L <%=xa2 %>,<%=ya2 %> L <%=xa3 %>,<%=ya3 %> L <%=xa1 %>,<%=ya1 %> z"
       id="path2385"
       style="fill:#5c6f6c;fill-opacity:.99;fill-rule:evenodd;stroke:#000000;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1" />
  </g>
