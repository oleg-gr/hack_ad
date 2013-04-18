/*javascript editor behaviour*/

$(document).ready(function () {

	var textArea = document.getElementById("editor");

	var tnt = CodeMirror.fromTextArea(textArea, {
		mode: "ruby",
		indentUnit: 3,
		tabSize: 3,
		lineNumbers: true
	});

	tnt.setValue("#Program\ndefine functionName(x,y) do\n\tfd(20)\n\tx=x+y\nend");	
	
});
