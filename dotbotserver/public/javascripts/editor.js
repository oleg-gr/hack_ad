/*javascript editor behaviour*/
var editor =
{
	init: function()
	{
		var textArea = document.getElementById("editor");
		this.doc = CodeMirror.fromTextArea(textArea, {
			mode: "tnt",
			indentUnit: 3,
			tabSize: 3,
			lineNumbers: true,
			autofocuse: true,
			highlightSelectionMatches: true,
			styleActiveLine: true
			});
			
		this.doc.setValue("#Program\ndefine patrol(x) do\n\tforward(x)\n\tlt(180)\n\tforward(x)\n\trt(180)\nend");
	},
/*		
	find:function() {
	
		this.doc.cursor = this.doc.getSearchCursor($("#search").val())
	
	},
	
	findn:function() {
	
		this.doc.cursor.findNext();
	
	},
	
	findp:function () {
	
		this.doc.cursor.findPrevious();

	}
*/
}
