<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% response.setContentType("application/json") ; %>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="org.json.*, java.lang.*"%>
<%@page import="ucsd.shoppingApp.models.*, java.util.*" %>
<%@page import="ucsd.shoppingApp.ConnectionManager"%>
<%@page import="ucsd.shoppingApp.SalesDAO"%>
<%@page import="java.sql.Connection"%>
<%
//Sales model/class here 

//Get the results from Database
ArrayList<SaleModel> sales = new ArrayList<SaleModel>();
SalesDAO aDB;

Connection con = ConnectionManager.getConnection();
aDB = new SalesDAO(con);

sales = aDB.getLogTable(1);
	
System.out.print(request.getParameter("action").toString());

ArrayList<AnalyticsModel> prodNames;
prodNames = (ArrayList<AnalyticsModel>)session.getAttribute("prodList");

ArrayList<AnalyticsModel> stateNames;
stateNames = (ArrayList<AnalyticsModel>)session.getAttribute("rowList");

HashMap<String, Double> col_update;
HashMap<String, Double> row_update;
HashMap<String,Double> newtop50;

double pr_price=0;
double st_price=0;


// put the results in Json object
JSONObject jObject = new JSONObject();
try
{
    JSONArray jArray = new JSONArray();
    for (SaleModel c : sales)
    {
    	String prID= Integer.toString(c.getProdID());
    	String stateID= Integer.toString(c.getStateID());

    	//loop over product sales arraylist
    	for(int i=0; i < prodNames.size(); i++){
    		AnalyticsModel prMod = prodNames.get(i);
    		if(Integer.toString(prMod.getID() )== prID){//red color
    			pr_price = prMod.getPrice() + c.getPrice();
    			if(i <50) col_update.put(prID, pr_price) ;
    			else{//purple column
    				if(pr_price > (prodNames.get(49).getPrice())){
    					newtop50.put(Integer.toString(prMod.getID()),pr_price);
    				}
    			}
    		}
    		pr_price=0;
    	}
    	//loop over states arraylist
    	for(int i=0; i < stateNames.size(); i++){
    		AnalyticsModel stateMod = stateNames.get(i);
    		if(Integer.toString(stateMod.getID() )== stateID){//red color
    			st_price = stateMod.getPrice() + c.getPrice();
    		}
    		row_update.put(stateID,st_price);
    		
    	}
    	
/*          JSONObject cJSON = new JSONObject();
         cJSON.put("prodid", c.getProdID());
         cJSON.put("stateId", c.getStateID());
         cJSON.put("price", c.getPrice());
         jArray.put(cJSON); */

    }
    JSONObject cJSON = new JSONObject();
    cJSON.put("updatedProducts", col_update);
    cJSON.put("updatedStates", row_update);
    cJSON.put("newTop50", newtop50);
    jArray.put(cJSON);
    
    jObject.put("updates", jArray);

} catch (Exception jse) {
    jse.printStackTrace();
}
response.getWriter().print(jObject);
 %>
