function changeColors(){
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
	// var i;
  //   var row="";
    	 var arr = obj.SalesList;
  //   for(i = 0; i < arr.length; i++) {
  //   	var x = document.getElementById("name_"+arr[i].id);
  //   	x.innerHTML = "<label>"+ arr[i].name +"</label>";
  //   	x.style.backgroundColor = "red"
	//
  //   	x = document.getElementById("city_"+arr[i].id);
  //   	x.innerHTML = "<label>"+ arr[i].city +"</label>";
  //   	x.style.backgroundColor = "red"
  //   }

	//Loop over result(logTable)
  for (int i =0; i < arr.length; i++){
	  for(int j =0; j < prodArrayList.lenght; j++){
			if(arr[i].getProdId == prodArrayList[j].getProdId){

			}
		}
	}

}
