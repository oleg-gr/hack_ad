var main = 
{
	init: function()
	{
		editor.init();
		parser.parse(editor.doc);
	}
}

$(document).ready(main.init);
