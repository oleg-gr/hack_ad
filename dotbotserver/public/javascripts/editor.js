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
			
		this.doc.setValue("#Program\ndefine square(x) do\n\ti=0\n\twhile i<4\n\t\tfd(x)\n\t\tlt(90)\n\t\ti=i+1\n\tend\nend\nsquare(40)");
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
