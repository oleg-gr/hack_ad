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
	/*
	$("#find").on("click", editor.find());
	$("#next").on("click", editor.findn());
	$("#prev").on("click", editor.findp());
	*/
});
