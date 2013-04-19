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
		editor.doc.setValue("#Program\ndefine  functionName(  fd( x,  i) ,   y )  do\n\tfd( fd(20 )  +fd(23== 10)  )\n\tx =  x +   y\n    end");
	}
}
