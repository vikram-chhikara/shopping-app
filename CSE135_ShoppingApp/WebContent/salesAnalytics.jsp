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
			if(request.getAttribute("alist") == null) {
				response.sendRedirect("./salesController?action=list");
			} else {
	%>  		
			<table cellspacing="5">
				<tr>
					<td valign="top"> <jsp:include page="./menu.jsp"></jsp:include></td>
					<td></td>
					<td>
						<h3>Hello <%= session.getAttribute("personName") %></h3>
						<form method="POST" action="SalesController">
				   			Row: <select name="rowChoice" onchange="this.form.submit()">
				  				<option value="c">Customers</option>
				  				<option value="s" <%if((request.getAttribute("rowChoice") != null) && (request.getAttribute("rowChoice")).equals("s")) { %> selected <% } %>>States</option>
							</select>
							Order: 	<select name="orderChoice" onchange="this.form.submit()">
				  				<option value="a">Alphabetical</option>
				  				<option value="t" <%if((request.getAttribute("orderChoice") != null) && (request.getAttribute("orderChoice")).equals("t")) { %> selected <% } %>>Top-K</option>
							</select>			
				  		</form>
					</td>
				</tr>
			</table>
			
			<table>
				<% ArrayList<AnalyticsModel> an = (ArrayList<AnalyticsModel>)request.getAttribute("alist");
					int i = 0;
					String prev = an.get(i).getRowName();
					AnalyticsModel am = an.get(i);
					%>
					<tr> <td><%=prev %></td>
					<td><%=am.getProduct() %></td>
					<%
					for(i = 1 ; i < an.size(); i++) {
						am = an.get(i);
						if(!am.getRowName().equals(prev)) {
							%>
							</tr>
							<tr>
							<td><%=am.getRowName() %></td>
							<%
							prev = am.getRowName();
						} 
					%>
						<td><%=am.getProduct() %></td>
					<%} %>
			</table>	
	<%
			} 
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