
<%@page import="DBViewer.objects.view.*" %>
<%@page import="DBViewer.objects.model.*" %>
<%@page import="java.util.*" %>
<%
TableView t = (TableView)request.getSession().getAttribute("CurrentTableView");

int height = t.getTable().getHeight();
int width = t.getTable().getWidth();
%>
  <g
     id="Table-<%= t.getId() %>"
     class="svgTable"
     transform="translate(<%= request.getParameter("transx") %>,<%= request.getParameter("transy") %>)">
 <a id="<%= t.getId() %>" class="tableanchor" href="#" style="cursor: move;" onmousedown="tableDown(this);" onmouseup="tableUp(this);" onmousemove="tableMove(this);">
    <rect
       style="opacity:0.9;fill:#ffffff;fill-opacity:1;fill-rule:evenodd;stroke:#406655;stroke-width:1.8;stroke-linecap:butt;stroke-linejoin:miter;marker:none;marker-start:none;marker-mid:none;marker-end:none;stroke-miterlimit:4;stroke-dasharray:none;stroke-dashoffset:0;stroke-opacity:1;visibility:visible;display:inline;overflow:visible;enable-background:accumulate"
       id="tableBody"
       width="<%= width %>"
       height="<%= height %>"
       x="0"
       y="0"
       rx="11.711954"
       ry="7.3775272" />
    <rect
       style="opacity:1;fill:#d1ead9;fill-opacity:1;fill-rule:evenodd;stroke:#7da996;stroke-width:1.8;stroke-linecap:butt;stroke-linejoin:miter;marker:none;marker-start:none;marker-mid:none;marker-end:none;stroke-miterlimit:4;stroke-dasharray:none;stroke-dashoffset:0;stroke-opacity:.9;visibility:visible;display:inline;overflow:visible;enable-background:accumulate"
       id="tableName"
       width="<%= width %>"
       height="30"
       x="0"
       y="0"
       rx="11.711954"
       ry="7.3775272" />
    <text
       xml:space="preserve"
       style="font-size:10px;font-style:normal;font-variant:normal;font-weight:normal;font-stretch:normal;fill:#000000;fill-opacity:1;stroke:none;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:.9;font-family:Times New Roman;-inkscape-font-specification:Times New Roman"
       x="10"
       y="3"
       id="tableHeadText"><tspan
         sodipodi:role="line"
         id="tspan3163"
         x="10"
         y="18"
         style="font-style:normal;font-variant:normal;font-weight:bold;font-stretch:normal;font-family:Verdana;-inkscape-font-specification:Verdana"><%= t.getTable().getName() %></tspan></text>
    <text
       xml:space="preserve"
       style="font-size:8px;font-style:normal;font-variant:normal;font-weight:normal;font-stretch:normal;fill:#000000;fill-opacity:1;stroke:none;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1;font-family:Times New Roman;-inkscape-font-specification:Times New Roman"
       x="10"
       y="20"
       id="tableBodyText">
           <%
           Map<String,Column> cols = t.getTable().getColumns();

           double y = 48.0;
             for (Column c : cols.values()) {
                String fk = "";
                if (t.getTable().getForeignKeys().containsKey(c.getName())){
                   ForeignKey key = t.getTable().getForeignKeys().get(c.getName());
                   fk = " FK->"+key.getReferencedTable()+"."+key.getReferencedColumn();
                }
                String colname = c.getName()+(t.getTable().getPrimaryKeys().containsKey(c.getName())?" PK":"")+fk;
                %>
         <tspan
         sodipodi:role="line"
         id="tspan3167"
         x="10"
         y="<%= y %>"
         style="font-size:8px;font-style:normal;font-variant:normal;font-weight:normal;font-stretch:normal;font-family:Courier New;-inkscape-font-specification:Courier New"><%= colname %></tspan>
          <% y+=15;
             } %></text>
     </a>
  </g>
