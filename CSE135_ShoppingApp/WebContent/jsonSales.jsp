<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% response.setContentType("application/json") ; %>
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

sales = aDB.getLogTable(Integer.parseInt(session.getAttribute("login_id").toString()));

ArrayList<AnalyticsModel> prodNames;
prodNames = (ArrayList<AnalyticsModel>)session.getAttribute("prodList");

LinkedHashMap<Integer, AnalyticsModel> stateNames;
stateNames = (LinkedHashMap<Integer, AnalyticsModel>)session.getAttribute("rowList");

HashMap<Integer, HashMap<Integer, Double>> cellMap = (HashMap<Integer, HashMap<Integer, Double>>)session.getAttribute("alist");

int currFilter = Integer.parseInt(session.getAttribute("categoryFilter").toString());

double pr_price=0, st_price=0, cell_price = 0;

int idxp = 0, idxs = 0;
int minimumidx = 49;

String ptitle = "prodtitle";
String stitle = "rowtitle";
String celltitle = "cell";

// put the results in Json object
JSONObject jObject = new JSONObject();
try
{
	System.out.println("Making JSON");
    JSONArray jArray = new JSONArray();
    
    AnalyticsModel testM;
    
    //Determine changes in product values and totals
    JSONArray jArrayP = new JSONArray();	//Make purple
    JSONArray jArrayR = new JSONArray();	//Make red
    for (SaleModel c : sales)
    {
    	int prID = c.getProdID();
    	int stateID = c.getStateID();
    	double addpri = c.getPrice();
    	int catID = c.getCategoryID();
    	
    	testM = new AnalyticsModel("", "", addpri, prID);
    	
    	if(prodNames.contains(testM) && (currFilter == 0 || currFilter == catID)) {	//Should always find it, but sanity check
    		
    		//state header column
    		if(stateNames.containsKey(stateID)) {
    			st_price = stateNames.get(stateID).getPrice() + addpri;
    			
    			stateNames.get(stateID).setPrice(st_price);
    			
    			JSONObject sJSON = new JSONObject();
		        sJSON.put("id", stitle + stateID);
		        sJSON.put("value", st_price);
		        jArrayR.put(sJSON);
    		}
    		
    		idxp = prodNames.indexOf(testM);
    		pr_price = prodNames.get(idxp).getPrice() + addpri;
    		
    		//product header row
    		if(idxp <= minimumidx) {
    			prodNames.get(idxp).setPrice(pr_price);
    			
		    	JSONObject pJSON = new JSONObject();
		        pJSON.put("id", ptitle + prID);
		        pJSON.put("value", pr_price);
		        	
		        jArrayR.put(pJSON);
    		} else if(pr_price > prodNames.get(minimumidx).getPrice()) {
    			JSONObject pJSON = new JSONObject();
		        pJSON.put("id", prodNames.get(minimumidx).getID());
		        	
		        jArrayP.put(pJSON);
		        minimumidx--;
    		}

    		//Cell
    		if(cellMap.containsKey(stateID)) {
    			if(cellMap.get(stateID).containsKey(prID)) {
		    		cell_price = cellMap.get(stateID).get(prID) + addpri;
		    		JSONObject cJSON = new JSONObject();
			        cJSON.put("id", celltitle + stateID + "_" + prID);
			        cJSON.put("value", cell_price);
			        	
			        jArrayR.put(cJSON);
    			}
    		}
    	}
    }
    jObject.put("purple", jArrayP);
    jObject.put("red", jArrayR);
    System.out.println("Finished with JSON");
    
} catch (Exception jse) {
    jse.printStackTrace();
}
response.getWriter().print(jObject);
%>