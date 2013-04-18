var main = 
{
	init: function()
	{
		editor.init();
		parser.parse(editor.doc);
		console.log(parser.text);
	}
}

$(document).ready(main.init);
