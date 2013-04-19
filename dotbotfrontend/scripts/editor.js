/*javascript editor behaviour*/
var editor =
{
	init: function()
	{
		var textArea = document.getElementById("editor");
		editor.doc = CodeMirror.fromTextArea(textArea, {
			mode: "tnt",
			indentUnit: 3,
			tabSize: 3,
			lineNumbers: true
			});
		editor.doc.setValue("#Program\ndefine  functionName(  x ,   y )  do\n\tfd( lol(x+y)+ 2*y  )\n\tx =  x/2 +   y-1#hahaha\n    end\nfd(10)\nfunctionName(2,4)");
	}
}
