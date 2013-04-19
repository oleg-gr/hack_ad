var main = 
{
	init: function()
	{
		editor.init();
		parser.parse(editor.doc);
	}
}

$(document).ready(function() {
	main.init();
	/*
	$("#find").on("click", editor.find());
	$("#next").on("click", editor.findn());
	$("#prev").on("click", editor.findp());
	*/
});
