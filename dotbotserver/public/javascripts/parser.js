var parser = 
{
	parse: function(doc, callback)
	{
		parser.json = {};
		parser.code = [];
		doc.eachLine(function(line)
			{
				parsed_line = parser.format(line.text);
				for (var i = 0; i<parsed_line.length; i++)
				{
					if (parsed_line[i] === "") parsed_line = parsed_line.splice(i, 1);
				}
				if (parsed_line[0] != [""]) parser.code.push(parsed_line);
			});
		parser.defs = {};
		parser.main = parser.buildJSON(parser.code);
		parser.superJSON = {"status":"", "definitions":parser.defs, "main":parser.main};
		if (typeof callback === 'function') callback(parser.superJSON);
	},
	
	buildJSON: function(code)
	{
		var temp_code = [];
		parser.ops = {"!":"not", "|":"or", "&":"and", "<":"less_than", ">":"greater_than", "==":"equals", "=":"assign", "+": "add", "-":"sub", "*":"mult", "/":"div", "%":"mod"};
		for (var i = 0; i<code.length; i++)
		{
			for (var j = 0; j<code[i].length; j++)
			{
				var block_switch = {"define":1, "while":2, "if":3};
				if (block_switch.hasOwnProperty(code[i][j]))
				{
					var loop_type = block_switch[code[i][j]];
					var pf = parser.parseFunction(code[i][++j]);
					var block = [];
					i++;
					while (code[i][0] != "end")
					{
						block.push(code[i++]);
					}
					for (var key in pf)
					{
						if(loop_type==1) parser.defs[key]={"args": pf[key], "code":parser.buildJSON(block)};
						else if (loop_type==2) temp_code.push({"while":{"condition": pf, "code":parser.buildJSON(block)}});
						else if (loop_type==3) temp_code.push({"if":{"condition": pf, "code":parser.buildJSON(block)}});
					}
				}
				else temp_code.push(parser.parseFunction(code[i][j]));
			}
		}
		return temp_code;
					
	},
	
	parseFunction: function(code)
	{
		var pf = {};
		var split_ex = parser.splitStatement(code, parser.ops);
		if (split_ex.length != 1)
		{
			split_ex = parser.convertExpression(split_ex);
		}	
		else split_ex = split_ex[0];
		var matched = split_ex.match(/(.*?)\((.*)\)/);
		if (matched !== null)
		{
			var args = parser.splitStatement(matched[2], {",":","});
			pf[matched[1]]={};
			for (var i = 0; i<args.length; i+=2)
			{
				pf[matched[1]]["arg"+(i/2)] = parser.parseFunction(args[i]);
			}
			return pf;
		}
		else
		{
			return code;
		}
		
	},
	
	splitStatement: function(code, splitter)
	{
		var parentheses = 0;
		var split_ex = [];
		var temp_ex = "";
		for (var i = 0; i<code.length; i++)
		{
			if (code[i] == "(")
			{
				parentheses++;
				temp_ex += "(";
			}
			else if (code[i] == ")")
			{
				parentheses--;
				temp_ex += ")";
				if (parentheses === 0)
				{
					split_ex.push(temp_ex);
					temp_ex = "";
				}
			}
			else if (code[i] in splitter && parentheses === 0)
			{
				if (temp_ex !== "")
				{
					split_ex.push(temp_ex);
					temp_ex = "";
				}
				if(code[i]=="=" && code[i+1]=="=") split_ex.push(code[i]+code[++i]);
				else split_ex.push(code[i]);
			}
			else temp_ex += code[i];
		}
		if (temp_ex !== "") split_ex.push(temp_ex);
		return split_ex;
	},
	
	convertExpression: function(ex)
	{
		var postfix = [];
		var stack = [];
		if(ex[1]=="=") start=2;
		else start=0;
		for (var i = start; i<ex.length; i++)
		{
			if(!(parser.ops.hasOwnProperty(ex[i]))) postfix.push(ex[i]);
			else
			{
				if(stack.length === 0) stack.push(ex[i]);
				else
				{
					if(parser.compareOp(stack[stack.length-1],ex[i])>-1) postfix.push(stack.pop());
					stack.push(ex[i]);
				}
			}
		}
		while (stack.length !== 0)
		{
			postfix.push(stack.pop());
		}
		for (var j = 0; j < postfix.length; j++)
		{
			if (parser.ops.hasOwnProperty(postfix[j]))
			{
				var op1 = stack.pop();
				var op2 = stack.pop();
				stack.push(parser.ops[postfix[j]]+"("+op2+","+op1+")");
			}
			else
			{
				stack.push(postfix[j]);
			}
		}
		if (start===0) return stack.pop();
		else return "assign("+ex[0]+","+stack.pop()+")";
				
	},
	
	compareOp: function(op1, op2)
	{
		var ops = [op1,op2];
		for (var i = 0; i<2; i++)
		{
			if(ops[i]=="*"||ops[i]=="/") ops[i]=1;
			else ops[i]=0;
		}
		return (ops[0]-ops[1]);
	},
	
	format: function(line)
	{
		line = parser.remParSpace(line);
		line = line.trim();
		line = line.replace(/#+.*/g, "");
		line = line.replace(/\s*([/*/+-//=]+)\s*/g, "$1");
		line = line.replace(/\s+/g, " ");
		line = line.replace(/"(.*)?"/, '^$1');
		line = line.split(/\s/);
		return line;
	},
	
	remParSpace: function(line)
	{
		var parenthesis = 0;
		var newstr = "";
		for( var i = 0; i<line.length; i++)
		{
			if (line[i]=="(") {parenthesis++; newstr+=line[i];}
			else if (line[i]==")") {parenthesis--; newstr+=line[i];}
			else if (line[i]==" " && parenthesis!==0);
			else newstr+=line[i];
		}
		return newstr;
	}
};
