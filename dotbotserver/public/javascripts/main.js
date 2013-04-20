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
		if (syntax.check(editor.doc) && !done) {
			parser.parse(editor.doc);
		}	
		
		$("#console").append("\\n" + $("#controls").val());
	});
	
	
	/*
	$("#find").on("click", editor.find());
	$("#next").on("click", editor.findn());
	$("#prev").on("click", editor.findp());
	*/
});
