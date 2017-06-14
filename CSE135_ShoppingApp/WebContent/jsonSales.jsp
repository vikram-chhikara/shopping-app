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
prodNames = ((ArrayList<AnalyticsModel>)session.getAttribute("prodList"));

LinkedHashMap<Integer, AnalyticsModel> stateNames;
stateNames = (LinkedHashMap<Integer, AnalyticsModel>)session.getAttribute("rowList");

HashMap<Integer, HashMap<Integer, Double>> cellMap = (HashMap<Integer, HashMap<Integer, Double>>)session.getAttribute("alist");
HashMap<Integer, Double> newStateVals = new HashMap<Integer, Double>();

ArrayList<Double> outsider = new ArrayList<Double>();

ArrayList<Double> oldProd = new ArrayList<Double>(50);
for(int i = 0; i < 50; i++) {
	oldProd.add(prodNames.get(i).getPrice());
}

int currFilter = Integer.parseInt(session.getAttribute("categoryFilter").toString());

double pr_price=0, st_price=0, cell_price = 0;

int idxp = 0, idxs = 0;
int minimumidx = 49;
Double minP = prodNames.get(minimumidx).getPrice();

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
    JSONArray jArrayRT = new JSONArray();	//Make red title
    JSONArray jArrayRC = new JSONArray();	//Make red cells
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
    			//Find if already calculated
    			if(!newStateVals.containsKey(stateID)) {
    				st_price = stateNames.get(stateID).getPrice() + c.getStateSum();
        			newStateVals.put(stateID, st_price);
    				
    				JSONObject sJSON = new JSONObject();
    		        sJSON.put("id", stitle + stateID);
    		        sJSON.put("value", st_price);
    		        jArrayRC.put(sJSON);
    			}
    		}
    		
    		idxp = prodNames.indexOf(testM);
    		
    		//product header row
    		if(idxp <= minimumidx) {
    			pr_price = oldProd.get(idxp) + addpri;
    			oldProd.set(idxp, pr_price);
    			
		    	JSONObject pJSON = new JSONObject();
		        pJSON.put("id", ptitle + prID);
		        pJSON.put("value", pr_price);

		        jArrayRT.put(pJSON);
		        
		      //Cell
	    		if(cellMap.containsKey(stateID)) {
	    			if(cellMap.get(stateID).containsKey(prID)) {
			    		cell_price = cellMap.get(stateID).get(prID) + addpri;
			    		
			    		JSONObject cJSON = new JSONObject();
				        cJSON.put("id", celltitle + stateID + "_" + prID);
				        cJSON.put("value", cell_price);
				        	
				        jArrayRC.put(cJSON);
	    			}
	    		}
    		} else if(pr_price > minP){
    			pr_price = prodNames.get(idxp).getPrice() + addpri;

    			prodNames.get(idxp).setPrice(pr_price);
    			outsider.add(pr_price);
    		}
    	}
    }
    
  	//Compare price to current top list. If so, add that listed number to purple
  	Collections.sort(outsider);
  	ArrayList<Double> sortedO = oldProd;
  	Collections.sort(sortedO);
  	
  	//outsider = 8, 7, 6, 5 price
  	//oldProd = 3, 4, 5, 6, 6 price
  	
  	System.out.println("outsider comparison");
  	
  	for(int i = outsider.size() - 1, min = 0; i >= 0 && min < oldProd.size(); i--) {
  		Double check = outsider.get(i);
  		Double smin = sortedO.get(min);
  		
  		if(check > smin) {
	  		JSONObject pJSON = new JSONObject();
	  		int idxof = oldProd.indexOf(smin);
	  	    pJSON.put("id", prodNames.get(idxof).getID());
	  	    
	  	    jArrayP.put(pJSON);
	  	    System.out.println("add to purple column " + prodNames.get(idxof).getID());
	  	    min++;
  		} else {
  			break;
  		}
  	}
    
    //Add all arrays to final JSON object
    jObject.put("purple", jArrayP);
    jObject.put("red", jArrayRC);
    jObject.put("colred", jArrayRT);
    System.out.println("Finished with JSON");
    
} catch (Exception jse) {
    jse.printStackTrace();
}
response.getWriter().print(jObject);
%>