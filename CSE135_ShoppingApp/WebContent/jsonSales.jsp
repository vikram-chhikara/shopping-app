<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% response.setContentType("application/json") ; %>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="org.json.*, java.lang.*"%>
<%@page import="ucsd.shoppingApp.models.* , java.util.*, ucsd.shoppingApp.ConnectionManager, ucsd.shoppingApp.SalesDAO" %>
<%
//Sales model/class here 

	//Get the results from Database
	ArrayList<SaleModel> sales = new ArrayList<SaleModel>();

	Connection con = ConnectionManager.getConnection();
	aDB = new SalesDAO(con);
	
	sales = aDB.getLogTable();
	
	


 System.out.print(request.getParameter("action").toString());
 if (request.getParameter("action").toString().equalsIgnoreCase("all")){
	 for (int i=0; i<50; i++){
		 Company c = new Company();
		 c.setName("Company_"+Integer.toString(i));
		 c.setCity("City_"+Integer.toString(i));
		 c.setId(Integer.toString(i));
		 companies.add(c);
		 //TODO add id to update particular cell
	 }
 }
 else {
	 for (int i=0;i<10;i+=2){
		 Company c = new Company();
		 c.setName("Change_Company_"+Integer.toString(i));
		 c.setCity("Change_City_"+Integer.toString(i));
		 c.setId(Integer.toString(i));
		 companies.add(c);
		 //TODO add id to update particular cell
	 }
 }

 // put the results in Json object
JSONObject jObject = new JSONObject();
try
{
    JSONArray jArray = new JSONArray();
    for (SaleModel c : sales)
    {
         JSONObject cJSON = new JSONObject();
         cJSON.put("prodid", c.getProdID());
         cJSON.put("stateId", c.getStateID());
         cJSON.put("price", c.getPrice());
         jArray.put(cJSON);
    }
    jObject.put("SalesList", jArray);
} catch (Exception jse) {
    jse.printStackTrace();
}
response.getWriter().print(jObject);
 %>
