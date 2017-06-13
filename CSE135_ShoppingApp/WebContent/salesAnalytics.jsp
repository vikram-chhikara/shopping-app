<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="java.sql.Connection, ucsd.shoppingApp.ConnectionManager, ucsd.shoppingApp.*"%>
<%@ page import="ucsd.shoppingApp.models.* , java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Sales Analytics</title>
	<!-- Style and JS stuff -->
	<script src="http://code.jquery.com/jquery-latest.min.js"
        type="text/javascript"></script>
    <style type="text/css">
    .colorRed{
        color:red;
    }
    .colorPurple{
        color:purple;
    }
    </style>
    <script type="text/javascript" src="./update_refresh.js"></script>
</head>
<body>
	<table>
	<tbody id="tablebody">
	<!-- put the result set -->
	</tbody>
	</table>
<%
	if(session.getAttribute("roleName") != null) {
		String role = session.getAttribute("roleName").toString();
		if("owner".equalsIgnoreCase(role) == true){
		%> 
		<!-- Sales Filtering Category List -->
		<%
		Connection con = ConnectionManager.getConnection();	
		CategoryDAO categoryDao = new CategoryDAO(con);
		List<CategoryModel> category_list = categoryDao.getCategories();
		con.close();
		
		//basic default
		if(session.getAttribute("rowChoice") == null) {
			session.setAttribute("rowChoice", "states");
		}
		if(session.getAttribute("orderChoice") == null) {
			session.setAttribute("orderChoice", "t");
		}
	%>
	
	<!-- Basic Filtering -->
		<table cellspacing="5">
			<tr>
				<td valign="top"> <jsp:include page="./menu.jsp"></jsp:include></td>
				<td></td>
				<td>
					<h3>Hello <%= session.getAttribute("personName") %></h3>
					<form method="POST" action="SalesController">
					<p>
					<input type="Submit" value="Run Query"></input> </p>
					<!-- Sales Filtering Options -->
					Category Filter: <select name="catFilter">
						<option value="0" selected>All</option>
						<%
						for (CategoryModel cat : category_list) {
						%>
						<option value="<%=cat.getId()%>" <%if((session.getAttribute("catFilter") != null) && ((Integer)session.getAttribute("catFilter") == cat.getId())) { %> selected="selected" <%} %>> 
							<%=cat.getCategoryName()%>
						</option>
						<%
						}
						%>
					</select>
					</form>
				</td>
			</tr>
		</table>
			
			<!-- Table ordered and sorted as required -->
			<table id="salestable" style="border-collapse:collapse;" width="100%">
				<tr id="productsrow" style="border:1px solid black;" >
				<td sytle="border:none;"> <button onclick="refreshTable(this);">Refresh</button> </td>
				<%
				//Get table values
				HashMap<Integer, HashMap<Integer, Double>> an;
				if(session.getAttribute("alist") != null) {
					an = (HashMap<Integer, HashMap<Integer, Double>>)session.getAttribute("alist");
				} else {
					System.out.println("null list");
					an = new HashMap<Integer, HashMap<Integer, Double>>();
				}
				
				//get Row Names
				LinkedHashMap<Integer, AnalyticsModel> rowNames;
				if(session.getAttribute("rowList") != null) {
					rowNames = (LinkedHashMap<Integer, AnalyticsModel>)session.getAttribute("rowList");
				} else {
					System.out.println("Empty row list");
					rowNames = new LinkedHashMap<Integer, AnalyticsModel>();
				}
				
				//get Product Names
				ArrayList<AnalyticsModel> prodNames;
				if(session.getAttribute("prodList") != null) {
					prodNames = (ArrayList<AnalyticsModel>)session.getAttribute("prodList");
				} else {
					System.out.println("Empty product list");
					prodNames = new ArrayList<AnalyticsModel>();
				}
				
				String curProd, curProdPrice, prodRefID = "";
				AnalyticsModel prodRef = null;
				/* Start with product list */
				for(int i = 0; i < 50 && i < prodNames.size(); i++) {
					prodRef = prodNames.get(i);
					curProd = prodRef.getProduct();
					curProdPrice = "(" + prodRef.getPrice() + ")";
					prodRefID = "prodtitle" + prodRef.getID();
					
					%>
					<th style="border:1px solid black;"><%=curProd%> <p id=<%=prodRefID %>><%=curProdPrice %></p> </th>
					<%
				}
				%>
				<td><button onclick="refreshTable(this);">Refresh</button></td>
				<%
				
				/* Bulk of the table */
				HashMap<Integer, Double> prodpri;
				AnalyticsModel currRow = null;
				String currRowPrice, rowValID, cellID, varycellID;
				String rowVal = "(0.0)";
				Double pri = 0.0;
				Integer stateID, prodID;
				
				int stateindx = 0;
				
				Iterator<AnalyticsModel> its = rowNames.values().iterator();
				while (its.hasNext() && stateindx < 50) {
					currRow = its.next();
					rowVal = currRow.getRowName();
					currRowPrice = "(" + currRow.getPrice() + ")";
					rowValID = "rowtitle" + currRow.getID();
					cellID = "cell" + currRow.getID();
					
					stateID = currRow.getID();
					
					%>
					<tr>
					<td style="border:1px solid black; font-weight:bold"><%=rowVal %> <p id=<%=rowValID%>><%=currRowPrice%></p></td>
					<% 
					
					for(int j = 0; j < 50 && j < prodNames.size(); j++) {
						if(an.containsKey(stateID)) {
							prodpri = an.get(stateID);
							prodID = prodNames.get(j).getID();
							varycellID = cellID + "_" + prodID;
							
							if(prodpri.containsKey(prodID)) {
								pri = prodpri.get(prodID);
								%>
								<td id=<%=varycellID%> style="border:1px solid black;"><%=pri%></td>
								<%
							} else {
								%>
								<td id=<%=varycellID%> style="border:1px solid black;"></td>
								<%
							}
						} else {
							%>
							<td id=<%=cellID%> style="border:1px solid black;"></td>
							<%
						}
						if((stateindx == 49) && (j == 49 || j == prodNames.size() - 1)) {
							%>
							<td><button onclick="refreshTable(this);">Refresh</button></td>
							<%
						}
					}
				%>
				</tr>
				<%
					
					stateindx++;
				}
			%>
			</table>
			<button onclick="refreshTable(this);">Refresh</button>
			<%
		}
		else { %>
			<h3>This page is available to owners only</h3>
		<%
		}
	}
	else { %>
			<h3>Please <a href = "./login.jsp">login</a> before viewing the page</h3>
	<%}
	%>

</body>
</html>