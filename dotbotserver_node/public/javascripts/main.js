activeId = "1";

var main = 
{
  state: {},
	init: function()
	{
		editor.init();
	}
  
};

var masterUpdate = function(id_list, callback){
  for (var i = 0; i < id_list.length; i++){
    getObject(id_list[i]);
  }
};

var printConsole = function(text){
  $('#std-out').append(text + '<br />>> ');
  var stdOut = document.getElementById("std-out");
  stdOut.scrollTop = stdOut.scrollHeight;
};

var postObject = function(obj, callback){
  var val = $.post("http://discos.herokuapp.com/io", obj, printConsole(JSON.stringify(obj, null, 4)), 'json');
  if (typeof callback === 'function') callback(val);
};

var getObject = function(id, callback){
  var val = $.get("http://discos.herokuapp.com/io", {from: 'master', id: id}, printConsole(JSON.stringify(resp, null, 4)), 'json');
  if (typeof callback === 'function') callback(val);
};

var parseObject = function(json, callback){
  if (typeof callback === 'function') callback();
};

var stdIn = function(str){
  if (str == "start()"){
    obj = {state: "active", from: "master", id: activeId};
  } else if (str == "pause()"){
    obj = {state: "paused", from: "master", id: activeId};
  } else if (str == "stop()"){
    obj = {state: "inactive", from: "master", id: activeId};
  }
  if (obj){
    postObject(obj, function(resp){
      printConsole(obj);
    });
  }
  $('#std-in').val('');
  parseJSON(str, function(json){
    json.from = 'master';
    json.id = activeId;
    console.log(json);
    postObject(json);
  });
};

var parseJSON = function(string, callback){
  var match = /[^/s]+?\(.*\)/g.test(string);
  console.log(match);
  console.log("parsing");
  if (match && typeof callback === 'function'){
    parser.parseSingle(string, callback);
  } else {
    printConsole("Syntax Error on std-in");
  }
};

$(document).ready(function() {
  main.init();
  $("#compile").on("click", function() {
    if (syntax.check(editor.doc)) {
      parser.parse(editor.doc, function(obj){
        obj.from = 'master';
        obj.id = activeId;
        JSON.stringify(obj, null, 4); 
        postObject(obj);});
    }
  });
  $('#std-in').bind('keypress', function(e){
    if (e.keyCode == 13){
      if (e.shiftKey === true){
      } else {
        e.preventDefault();
        stdIn($('#std-in').val());
      }
    }
  });

});

$(function () {
    $('#tabs a:last').tab('show');
});
