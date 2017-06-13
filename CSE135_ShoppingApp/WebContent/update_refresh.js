function refreshTable(){
	 var xmlHttp = new XMLHttpRequest();
	 var url="jsonSales.jsp";
	 var stateChanged = function () {
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
    var mostChange = 50;
    
    var stateTitleID = "rowtitle";
    var prodTitleID = "prodtitle";
    var cellID = "cell";
    
    var id, idc, newval, colid;
    
    parr = obj.purple
    //for each state, loop through purple prodtitle and cell elems changing it purple
    for(i = 0; i < parr.length; i++) {
    	id = parr[i].id;
    	
    	colid = prodTitleID + id;
    	console.log(colid);

    	var ref = document.getElementById(id);
    	if(ref != null)
    		ref.style.color = "purple";
    	
    	for(var s = 1; s < 57; s++) {
    		colid = cellID + s + "_" + id;
    		ref = document.getElementById(colid);
    		if(ref != null)
    			ref.style.color = "purple";
    	}
    }
    
    rarr = obj.red
    for(i = 0; i < rarr.length; i++) {
    	id = rarr[i].id;
    	newval = rarr[i].value;
    	
    	var ref = document.getElementById(id);
    	
    	if(ref != null) {
	    	ref.innerHTML = newval;
	    	ref.style.color = "red";
    	}
    }
    
    rarr = obj.colred
    for(i = 0; i < rarr.length; i++) {
    	id = rarr[i].id;
    	newval = rarr[i].value;
    	
    	var ref = document.getElementById(id);
    	
    	if(ref != null) {
	    	ref.innerHTML = newval;
	    	ref.style.color = "red";
    	}
    }
}
