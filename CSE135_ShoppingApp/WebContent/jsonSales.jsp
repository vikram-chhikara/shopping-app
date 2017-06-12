<% response.setContentType("application/json") ; %>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="org.json.*, java.lang.*"%>
<%@ page import="java.util.*" %>
<%
//Sales model/class here 

 //Get the results from Database
 ArrayList<SaleModel> sales = new ArrayList<SaleModel>();
 System.out.print(request.getParameter("action").toString());
 if (request.getParameter("action").toString().equalsIgnoreCase("all")){
	 for (int i=0;i<10;i++){
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
         cJSON.put("prodid", c.getProdId());
         cJSON.put("prodName", c.getProdName());
         cJSON.put("stateId", c.getStateId());
         cJSON.put("stateName", c.getStateName());
         cJSON.put("price", c.getPrice());
         jArray.put(cJSON);
    }
    jObject.put("SalesList", jArray);
} catch (Exception jse) {
    jse.printStackTrace();
}
response.getWriter().print(jObject);
 %>
