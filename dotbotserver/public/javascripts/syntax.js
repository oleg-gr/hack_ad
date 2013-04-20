/*checks syntax*/
var syntax = {

	check: function (doc) {
		var depth = 0;
		
		editor.doc.setValue(doc.getValue()); //resetting values
		
        for (var i = 0; i < doc.lineCount(); i++) {
        	var line = doc.getLine(i);
        	if (line.split("(").length != line.split(")").length) {
        		this.hl(i);
        		syntax.out("Unmatched braces at line", i+1);
        		return false;
        	}
        	
			/*if ((line.indexOf("while") != -1 || line.indexOf("if") != -1) && ((line.indexOf("true") == -1 && line.indexOf("==") == -1 && line.indexOf(">") == -1 && line.indexOf("false") == -1 && line.indexOf("<") == -1 && line.indexOf("!=") == -1) || (line.indexOf("<<") != -1 || line.indexOf(">>") != -1 || line.indexOf("!==") != -1))) {
				this.hl(i);
        		syntax.out("Condition not found or not properly defined at line", i+1);
        		return false;
			} */
        	
        	if (line.indexOf("define") != -1) {
				if (line.indexOf("(") == -1) {
        			this.hl(i);
        			syntax.out("Function is found but no braces found", i+1);
        			return false;
        		}
        		else {
        			var function_name = line.substring(line.indexOf("define"), line.indexOf("(")).split(" "); 
        			var count = 0;
        			for (k in function_name) {
        				if (parseInt(function_name[k])) {
        					this.hl(i);
        					syntax.out("Name of the function expected but integer found at line", i+1);        					
        					return false;
        				}
        				if (function_name[k] != "") {
        					count++;
        					if (count>2) {
        						this.hl(i);
        						syntax.out("Name of the function is a single word. Error at line", i+1);        				
        						return false;
        					}	
        				}
        			}
        		}
        	}
        	
        	if (line.indexOf("while")!=-1 || line.indexOf("if")!=-1 || line.indexOf("define")!=-1 || line.indexOf ("else")!=-1) {
        		
        		if  (line.indexOf("do")==-1) {
        			this.hl(i);
        			syntax.out("Cannot find start of a block indicator 'do' at line", i+1);        	
        			return false;
        		}
        		
        		else if (line.indexOf("else")==-1) {
        		
		    		depth++;
		    		if (depth == 1) {
		    			var start = i;
		    		}
		    			
        		}
        	}
        	
        	if (line.indexOf("end") != -1) {
       			depth--;
        		
        		if (depth < 0) {
        			this.hl(i);
        			syntax.out("Unmatched 'end' at line", i+1);        	
        			return false;
        		}
        	}
        
        }
        
		if (depth > 0) {
        	this.hl(start);
        	syntax.out("Unmatched 'do' at line", start+1);
        	return false;
        }
        syntax.out("Syntax checked. Errors ", 0);
		return true;	
	},
	
	hl: function (n) {
		editor.doc.markText({
		        line:n,
		        ch: 0
		    }, {
		        line:n+1,
		        ch: 0
		    }, {
		        className: "CodeMirror-codeerror",
		        clearOnEnter: true
		    });
		    
	},
    
    out: function (m,n) {
    
    	$("#status").val(m+ ' ' +n);
    
    }
    
	
}
