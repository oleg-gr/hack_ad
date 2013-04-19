var parser = 
{
	parse: function(doc)
	{
		parser.json = {};
		parser.code = [];
		doc.eachLine(function(line)
			{
				parsed_line = parser.format(line.text);
				for (var i = 0; i<parsed_line.length; i++)
				{
					if (parsed_line[i] == "") parsed_line = parsed_line.splice(i, 1)
				}
				if (parsed_line[0] != [""]) parser.code.push(parsed_line);
			});
		parser.defs = {};
		parser.main = parser.buildJSON(parser.code);
		console.log(parser.main);
		console.log(parser.defs);
	},
	
	buildJSON: function(code)
	{
		var temp_code = [];
		for (var i = 0; i<code.length; i++)
		{
			for (var j = 0; j<code[i].length; j++)
			{
				if (code[i][j] == "define")
				{
					var pf = parser.parseFunction(code[i][++j]);
					var block = [];
					i++;
					while (code[i][0] != "end")
					{
						block.push(code[i++]);
					}
					for (var key in pf)
					{
						parser.defs[key]={"args": pf[key], "code":parser.buildJSON(block)};
					}
				}
				else if (code[i][j] == "while");
				else temp_code.push(parser.parseFunction(code[i][j]));
			}
		}
		return temp_code;
					
	},
	
	parseFunction: function(code)
	{
		var pf = {};
		var split_ex = parser.parseExpression(code);
		if (split_ex.length != 1) parser.convertExpression(split_ex);
		else
		{
			var matched = code.match(/(.*?)\((.*)\)/);
			if (matched != null)
			{
				args = matched[2].split(",");
				pf[matched[1]]={};
				for (var i = 0; i<args.length; i++)
				{
					pf[matched[1]]["arg"+i] = parser.parseFunction(args[i]);
					return pf;
				}
			}
			else
			{
				return code;
			}
		}
	},
	
	parseExpression: function(code)
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
				if (parentheses == 0)
				{
					split_ex.push(temp_ex);
					temp_ex = "";
				}
			}
			else if (/[-*\+\/]/.test(code[i]) && parentheses == 0)
			{
				if (temp_ex != "")
				{
					split_ex.push(temp_ex);
					temp_ex = "";
				}
				split_ex.push(code[i]);
			}
			else temp_ex += code[i];
		}
		split_ex.push(temp_ex);
		return split_ex;
	},
	
	format: function(line)
	{
		line = parser.remParSpace(line);
		line = line.trim();
		line = line.replace(/#+.*/g, "");
		line = line.replace(/\s*([/*/+-//=]+)\s*/g, "$1");
		line = line.replace(/\s+/g, " ");
		line = line.split(/\s/);
		return line;
	},
	
	remParSpace: function(line)
	{
		var parenthesis = 0;
		var newstr = "";
		for( var i = 0; i<line.length; i++)
		{
			if (line[i]=="(") {parenthesis++; newstr+=line[i]}
			else if (line[i]==")") {parenthesis--; newstr+=line[i]}
			else if (line[i]==" " && parenthesis!=0);
			else newstr+=line[i]
		}
		return newstr;
	}
}
