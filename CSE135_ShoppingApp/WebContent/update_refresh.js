function changeColor(){
	document.getElementById("log").innerHTML = "inside changeColor";
	 var xmlHttp = new XMLHttpRequest();
	 var url="jsonCustomer.jsp";
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

function showCustJson(str) {
	document.getElementById("log").innerHTML = "inside showCustJson";
	 var xmlHttp = new XMLHttpRequest();
	 var url="salesAnalytics.jsp";
	 url = url + "?refresh=2";
	 var stateChanged = function () {
			document.getElementById("log").innerHTML = "inside statechanged";
			 if (xmlHttp.readyState==4) {
			  console.log(xmlHttp.responseText);
			  var jsonStr = xmlHttp.responseText;
			  var result = JSON.parse(jsonStr);
			  console.log(result);
			  listAllRows(result);
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
  for (int i = 0; i < arr.size(); i++){
	  for(int j =0; j < prodArrayList.size(); j++){
			if(arr[i].getProdID() == prodArrayList[j].getProdID()){

			}
		}
	}

}
