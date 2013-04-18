/*javascript editor behaviour*/
var editor =
{
	init: function()
	{
		var textArea = document.getElementById("editor");
		editor.doc = CodeMirror.fromTextArea(textArea, {
			mode: "ruby",
			indentUnit: 3,
			tabSize: 3,
			lineNumbers: true
			});
		editor.doc.setValue("#Program\ndefine functionName(x,y) do\n\tfd(20)\n\tx=x+y\nend");
	}
}
