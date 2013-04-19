var main = 
{
	init: function()
	{
		//console.log(editor);
		//console.log(parser);
		editor.init();
		parser.parse(editor.doc);
		//console.log(parser.text);
	}
}

$(document).ready(main.init);
