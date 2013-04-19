var parser = 
{
	parse: function(doc)
	{
		parser.json = {};
		parser.code = [];
		doc.eachLine(function(line){parser.code.push(parser.format(line.text))});
		console.log(parser.code);
		parser.buildJSON(parser.code);
	},
	buildJSON: function(code)
	{
		
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
