function refreshTable(){
	document.getElementById("log").innerHTML = "inside changeColor";
	 var xmlHttp = new XMLHttpRequest();
	 var url="jsonSales.jsp";
	 url = url + "?action=color";
	 var stateChanged = function () {
			document.getElementById("log").innerHTML = "inside statechanged";
			 if (xmlHttp.readyState==4) {
			  console.log(xmlHttp.responseText);
			  var jsonStr = xmlHttp.responseText;
			  var result = JSON.parse(jsonStr);
			  console.log(result);
			  showDelta(result);
			 }
		}
	 xmlHttp.onreadystatechange = stateChanged;
	 xmlHttp.open("GET", url, true);
	 xmlHttp.send(null);
}


function showDelta(obj) {
	var i;

    var arr = obj.updates;
    var row ="";
    //add new top50
    for(var newTop in arr.newTop50){
    	row = row + "<tr><td id= Prod_ID_"+newTop[1]+"><label>"+"<td id=Prod_Price"+ newTop[2] +"><label>"+ "</tr>";
    }
    console.log(row);
    document.getElementById("tablebody").innerHTML = row;
    
    //Update columns
    for (var prodMap in arr.updatedProducts){
      var x = document.getElementById("prodtitle" + prodMap[1]);
      x.innerHTML = "<label>"+ prodMap[2] +"</label>";
      x.style.backgroundColor = "red"
    }
    //Update rows
    for (var stateMap in arr.updatedStates){
      var x = document.getElementById("rowtitle" + stateMap[1]);
      x.innerHTML = "<label>"+ stateMap[2] +"</label>";
      x.style.backgroundColor = "red"
    }
   



}
