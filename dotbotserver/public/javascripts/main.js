var main = 
{
	init: function()
	{
		editor.init();
	}
}

$(document).ready(function() {
	main.init();
	$("#compile").on("click", function() {
		if (syntax.check(editor.doc)) {
			parser.parse(editor.doc);
		}
	});
	
	$('#search').bind('keyup', function(e) {
	 var code = (e.keyCode ? e.keyCode : e.which);
 		if(code == 13) { //Enter keycode
   			
   			$('#status').val($('#status').val() + "> " + $('#search').val());
   			$('#search').val("");
   			
 		}
 	});
	
	/*
	$("#find").on("click", editor.find());
	$("#next").on("click", editor.findn());
	$("#prev").on("click", editor.findp());
	*/
});
