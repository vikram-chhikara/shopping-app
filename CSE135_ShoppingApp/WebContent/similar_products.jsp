<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="java.sql.Connection, ucsd.shoppingApp.ConnectionManager, ucsd.shoppingApp.*"%>
<%@ page import="ucsd.shoppingApp.models.* , java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Similar Products Page</title>
</head>
<body>
<%
	if(session.getAttribute("roleName") != null) { %>
	<% if(session.getAttribute("similar_table") == null) {
		System.out.println("null table");
		response.sendRedirect("./SimProductsController");
	} else {
		//Get table
		ArrayList<SimilarProductsModel> simProds;
		if(session.getAttribute("similar_table") != null) {
			System.out.println("full table");
			simProds = (ArrayList<SimilarProductsModel>)session.getAttribute("similar_table");
		} else {
			System.out.println("Empty similar prods list");
			simProds = new ArrayList<SimilarProductsModel>();
		}
	%> 
		<table cellspacing="5">
			<tr>
				<td valign="top"> <jsp:include page="./menu.jsp"></jsp:include></td>
				<td></td>
				<td>
					<h3>Hello <%= session.getAttribute("personName") %></h3>
				</td>
			</tr>
		</table>
			
		<table style="border-collapse:collapse;" width="100%">
			<tr style="border:1px solid black;" >
			<td> Product 1 </td>
			<td> Product 2 </td>
			<td> Cosine Similarity </td>
			</tr>
			<%
			String curProd;
			SimilarProductsModel prodRef;

			for(int i = 0; i < simProds.size(); i++) {
				prodRef = simProds.get(i);
				%>
				<tr>
				<td style="border:1px solid black;"><%=prodRef.getProduct1() %></td>
				<td style="border:1px solid black;"><%=prodRef.getProduct2()%></td>
				<td style="border:1px solid black;"><%=prodRef.getPrice()%><td>
				</tr>
				<%
			}
		%>
		</table>
		<%
		}
	}
	else {%>
			<h3>Please <a href = "./login.jsp">login</a> before viewing the page</h3>
	<% }
	%>
</body>
</html>