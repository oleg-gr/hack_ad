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
			
		this.doc.setValue("#Program\ndefine functionName(x,i,y) do\n\tfd(fd(20))+fd(23-10)/51-2*63\n\tx=x+y\nend");
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
