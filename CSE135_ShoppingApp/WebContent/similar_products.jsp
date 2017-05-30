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

for(int i=0; i < pc_map.size(); i++){
	Map.Entry pair1 = (Map.Entry)it1.next();
	li1 = List<SimilarProductsModel>(pair1.getValue());
	it2 =it1;
	while(it2.hasNext()){
		Map.Entry pair2 = (Map.Entry)it2.next();
		li2 = List<SimilarProductsModel>(pair2.getValue());
		
		for(SimilarProductsModel model : li1){
			if(li2.contains(model.getPerson())){
				int index = li2.indexOf(model);
				pri2 = li2.get(index);
				String s = model.getPerson() + pair2.getKey();
				cosine_map.put(s, );
			}
		}
	}
}
%>
</body>
</html>