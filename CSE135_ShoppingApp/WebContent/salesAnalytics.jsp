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
</head>
<body>
<%
	if(session.getAttribute("roleName") != null) {
		String role = session.getAttribute("roleName").toString();
		if("owner".equalsIgnoreCase(role) == true){
			ArrayList<AnalyticsModel> an;
			if(session.getAttribute("alist") != null) {
				an = (ArrayList<AnalyticsModel>)session.getAttribute("alist");
			} else {
				System.out.println("null list");
				an = new ArrayList<AnalyticsModel>();
				//test
				AnalyticsModel amy = new AnalyticsModel("name","test",5);
				an.add(amy);
			}
			
			//get Row Names
			ArrayList<AnalyticsModel> rowNames;
			if(session.getAttribute("rowList") != null) {
				rowNames = (ArrayList<AnalyticsModel>)session.getAttribute("rowList");
			} else {
				System.out.println("Empty row list");
				rowNames = new ArrayList<AnalyticsModel>();
			}
			
			//get Product Names
			ArrayList<String> prodNames;
			if(session.getAttribute("prodList") != null) {
				prodNames = (ArrayList<String>)session.getAttribute("prodList");
			} else {
				System.out.println("Empty product list");
				prodNames = new ArrayList<String>();
			}
	%>  		
			<table cellspacing="5">
				<tr>
					<td valign="top"> <jsp:include page="./menu.jsp"></jsp:include></td>
					<td></td>
					<td>
						<h3>Hello <%= session.getAttribute("personName") %></h3>
						<form method="POST" action="SalesController">
				   			Row: <select name="rowChoice">
				  				<option value="c">Customers</option>
				  				<option value="s" <%if((request.getAttribute("rowChoice") != null) && (request.getAttribute("rowChoice")).equals("s")) { %> selected <% } %>>States</option>
							</select>
							Order: 	<select name="orderChoice">
				  				<option value="a">Alphabetical</option>
				  				<option value="t" <%if((request.getAttribute("orderChoice") != null) && (request.getAttribute("orderChoice")).equals("t")) { %> selected <% } %>>Top-K</option>
							</select>	
							<input type="Submit" value="Get Table"></input>
				  		</form>
					</td>
				</tr>
			</table>
			
			<table style="border:1px solid black;" width="100%">
				<tr style="border:1px solid black;" > <td></td>
				<%
				for(int i = 0; i < prodNames.size(); i++) {
					%>
					<td><%=prodNames.get(i) %></td>
					<%
				}
				%>
				<%
				AnalyticsModel am;
				String currRow = "";
				String rowVal = "(0.0)";
				int find = 0;
				
				for(int i = 0; i < rowNames.size(); i++) {
					currRow = rowNames.get(i).getRowName();
					rowVal = currRow + "\n (" + rowNames.get(i).getPrice() + ")";
					%>
					<tr>
					<td style="border:1px solid black;"><%=rowVal %></td>
					<% 
						for(int j = 0; j < prodNames.size(); j++) {
							for(int k = 0; k < an.size(); k++) {
								am = an.get(k);
								if(am.getRowName().equals(currRow) && am.getProduct().equals(prodNames.get(j))) {
									%>
									<td style="border:1px solid black;"><%=am.getPrice() %></td>
									<%
									find = 1;
									break;
								}
							}

							if(find == 1) {
								find = 0;
							} else {
								%>
									<td></td>
								<%
							}
						}
					%>
					</tr>
					<%
				}
			%>
			</table>	
	<%
		}
		else { %>
			<h3>This page is available to owners only</h3>
		<%
		}
	}
	else { %>
			<h3>Please <a href = "./login.jsp">login</a> before viewing the page</h3>
	<%} %>
</body>
</html>