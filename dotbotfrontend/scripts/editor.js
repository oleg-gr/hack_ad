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
		//this.doc.setValue("#Program\ndefine functionName(x,y) do\n\tfd(20)\n\tx=x+y\nend\n");
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
