
function appendToList(content) {
  var newItem = document.createElement("li");
  var newItemContent = document.createTextNode(content);
  newItem.appendChild(newItemContent);
  document.getElementById("releases").appendChild(newItem);
}

function appendTo(parentId, text){
  var newItem = document.createElement("div");
  var newItemContent = document.createTextNode(text);
  newItem.appendChild(newItemContent);
  document.getElementById("rightColumn").appendChild(newItem);
}


/*
function appendVer(version, versionType){
  var listItem = document.createElement("li");
  var type = document.createTextNode(versionType + ": ");
  var link = document.createElement();
  link.appendChild(version);
}
*/

function checkVer() {
  console.log(document.getElementById("latest"));
}

function downloadJar(type){
  if(type == 'latest'){
    var ver = document.getElementById('latest').innerHTML;
    window.open("https://github.com/crowwastaken/private-chat/releases/download/" + ver + "/client-" + ver + ".jar", '_blank').focus();
  }
  else if(type == 'launcher'){
    var ver = document.getElementById('launcher').innerHTML;
    window.open("https://github.com/crowwastaken/private-chat/releases/download/" + ver + "/launcher-" + ver + ".jar", '_blank').focus();
  }
}

function downloadMisc(name){
	
}