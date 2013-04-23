
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
  $('#std-out').append('>> ' + text + '<br />');
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

$(document).ready(function() {
  main.init();
  $("#compile").on("click", function() {
    if (syntax.check(editor.doc)) {
      parser.parse(editor.doc, function(obj){JSON.stringify(obj, null, 4); 
      postObject(obj);});
    }
  });
});

