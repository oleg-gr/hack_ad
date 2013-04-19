/*checks syntax*/
var syntax = {

	check: function (doc) {
		var depth = 0;
		
        for (var i = 0; i < doc.lineCount(); i++) {
        	console.log(depth);
        	var line = doc.getLine(i);
        	if (line.split("(").length != line.split(")").length) {
        		this.hl(i);
        		syntax.out("Unmatched braces at line", i+1);
        	}
        	
//        	console.log(string.substring(line.indexOf("define"), line.indexOf("("))));
        	
        	if (line.indexOf("define") != -1 && line.indexOf("(") == -1) {
        		this.hl(i);
        		syntax.out("Function is found but no braces found", i+1);
        	}
        	
        	if (line.indexOf("while")!=-1 || line.indexOf("if")!=-1 || line.indexOf("define")!=-1 || line.indexOf ("else")!=-1) {
        		
        		if  (line.indexOf("do")==-1) {
        			this.hl(i);
        			syntax.out("Cannot find start of a block indicator 'do' at line", i+1);        	
        		}
        		
        		else {
        		
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
        		}
        	}
        
        }
        
		if (depth > 0) {
        	this.hl(start);
        	syntax.out("Unmatched 'do' at line", start+1);
        }
        
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
		        className: "CodeMirror-codeerror"
		    });
    },
    
    resethl: function (n) {
	
		editor.doc.markText({
		        line:n-1,
		        ch: 0
		    }, {
		        line:n,
		        ch: 0
		    }, {
		        className: "CodeMirror-reset"
		    });
    },
    
    out: function (m,n) {
    
    	$("#status").val(m+ ' ' +n);
    
    }
    
	
}
