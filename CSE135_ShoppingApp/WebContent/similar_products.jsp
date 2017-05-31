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
<form method="POST" action="SimProductsController">
<% 	
HashMap<String,List<SimilarProductsModel>> pc_map;
HashMap<String,Double> cosine_map = new HashMap<String,Double>();

if(session.getAttribute("prod_cust_list") != null) {
	pc_map = (HashMap<String,List<SimilarProductsModel>>)session.getAttribute("prod_cust_list");
} else {
	System.out.println("Empty Hashmap");
	pc_map = new HashMap<String,List<SimilarProductsModel>>();
}

//iterate over hashmap; calculate cosine similarity and display
Iterator it1 = pc_map.entrySet().iterator();
Iterator it2 = pc_map.entrySet().iterator();

List<SimilarProductsModel> li1 = new ArrayList<>();
List<SimilarProductsModel> li2 = new ArrayList<>();
double sum = 0;
for(int i=0; i < pc_map.size(); i++){
	Map.Entry pair1 = (Map.Entry)it1.next();
	li1 = (List<SimilarProductsModel>)(pair1.getValue());
	it2 =it1;
	while(it2.hasNext()){
		Map.Entry pair2 = (Map.Entry)it2.next();
		li2 = (List<SimilarProductsModel>)(pair2.getValue());
		sum = 0;
		for(SimilarProductsModel model1 : li1){
			String person1 = model1.getPerson();
			for(SimilarProductsModel model2 : li2){
				String person2 = model2.getPerson();
				if(person1.equals(person2)){
					sum += (model1.getPrice())*(model2.getPrice());
					break;
				}
			}
			String s = (String)(pair1.getKey()) + (String)pair2.getKey();
			cosine_map.put(s, sum);
		}
	}
}
List<String> mapKeys = new ArrayList<>(cosine_map.keySet());
List<Double> mapValues = new ArrayList<>(cosine_map.values());
Collections.sort(mapValues);
Collections.sort(mapKeys);

LinkedHashMap<String, Double> sortedMap =
    new LinkedHashMap<>();

Iterator<Double> valueIt = mapValues.iterator();
while (valueIt.hasNext()) {
    Double val = valueIt.next();
    Iterator<String> keyIt = mapKeys.iterator();

    while (keyIt.hasNext()) {
        String key = keyIt.next();
        Double comp1 = cosine_map.get(key);
        Double comp2 = val;

        if (comp1.equals(comp2)) {
            keyIt.remove();
            sortedMap.put(key, val);
            break;
        }
    }
}
Map<String, Double> map = new TreeMap<String, Double>();
map = sortedMap;
Map<String, Double> final_map = new TreeMap<String, Double>();

ArrayList<String> keys = new ArrayList<String>(map.keySet());
for(int i=keys.size()-1; i>=0;i--){
    final_map.put( keys.get(i), (map.get(keys.get(i))));
}


%>
<table style="border-collapse:collapse;" width="100%">
	<tr style="border:1px solid black;" > 
		<td style="border:1px solid black; "><%="Products" %></td>
		<td style="border:1px solid black; "><%="Cosine similarity" %></td>

<%
Iterator f_m_it = cosine_map.entrySet().iterator();
while (f_m_it.hasNext()) {
    Map.Entry pair = (Map.Entry)f_m_it.next();
    String products = (String)(pair.getKey());
    Double cosine_sim = (Double)(pair.getValue()); %>
    <tr>
    	<td style="border:1px solid black; "><%=products %></td>
    	<td style="border:1px solid black; "><%=cosine_sim %></td>
    </tr>
    
<%   
}
%>
</table>
</form>			
</body>
</html>