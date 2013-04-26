/*javascript editor behaviour*/
var editor =
{
	init: function(id)
	{
		var textArea = document.getElementById(id);
		this.doc = CodeMirror.fromTextArea(textArea, {
			mode: "tnt",
			indentUnit: 3,
			tabSize: 3,
			lineNumbers: true,
			autofocus: true,
			highlightSelectionMatches: true,
			styleActiveLine: true
			});
			
		this.doc.setValue("#Program\ndefine patrol(x) do\n\tforward(x)\n\tlt(180)\n\tforward(x)\n\trt(180)\nend\n\n#Patroling, 10cm\npatrol(10)");
	},
}
