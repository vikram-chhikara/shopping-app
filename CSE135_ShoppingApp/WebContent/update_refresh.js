function refreshTable(){
	 var xmlHttp = new XMLHttpRequest();
	 var url="jsonSales.jsp";
	 var stateChanged = function () {
			document.getElementById("log").innerHTML = "inside statechanged";
			 if (xmlHttp.readyState==4) {
			  console.log(xmlHttp.responseText);
			  var jsonStr = xmlHttp.responseText;
			  var result = JSON.parse(jsonStr);
			  console.log(result);
			  document.getElementById("log").innerHTML = result;
			  showDelta(result);
			 }
		}
	 xmlHttp.onreadystatechange = stateChanged;
	 xmlHttp.open("GET", url, true);
	 xmlHttp.send(null);
}


function showDelta(obj) {
	document.getElementById("log").innerHTML = "showDelta";
	var i;

    var arr = obj.updates;
    var row ="";
    var mostChange = 50;
    
    var stateTitleID = "rowtitle";
    var prodTitleID = "prodtitle";
    var cellID = "cell";
    
    var id, idc, newval, colid;
    
    document.getElementById("log1").innerHTML = "Entering sModel";
    
    parr = obj.purple
    document.getElementById("log").innerHTML = parr.length;
    //for each state, loop through purple prodtitle and cell elems changing it purple
    for(i = 0; i < parr.length; i++) {
    	id = parr[i].id;
    	console.log(id);
    	
    	colid = prodTitleID + id;

    	console.log(colid);
    	var ref = document.getElementById(id);
    	ref.style.color = "purple";
    	
    	for(var s = 1; s < 57; s++) {
    		colid = cellID + s + "_" + id;
    		ref = document.getElementById(colid);
        	ref.style.color = "purple";
    	}
    }
    
    rarr = obj.red
    document.getElementById("log1").innerHTML = rarr.length;
    for(i = 0; i < rarr.length; i++) {
    	id = rarr[i].id;
    	newval = rarr[i].value;
    	
    	console.log(id);
    	
    	var ref = document.getElementById(id);
    	ref.innerHTML = newval;
    	ref.style.color = "red";
    }
}
