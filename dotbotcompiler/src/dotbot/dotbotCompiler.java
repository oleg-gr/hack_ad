package dotbot;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class dotbotCompiler {

	public static void main (String args[]) {
	
		String jsonText = "{\"first\": 123, \"second\": [4, 5, 6], \"third\": 789}";
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory(){
		    public List creatArrayContainer() {
		      return new LinkedList();
		    }

		    public Map createObjectContainer() {
		      return new LinkedHashMap();
		    }
		                        
		  };
		                
		  try{
		    Map json = (Map)parser.parse(jsonText, containerFactory);
		    Iterator iter = json.entrySet().iterator();
		    System.out.println("==iterate result==");
		    while(iter.hasNext()){
		      Map.Entry entry = (Map.Entry)iter.next();
		      System.out.println(entry.getKey() + "=>" + entry.getValue());
		    }
		                        
		    System.out.println("==toJSONString()==");
		    System.out.println(JSONValue.toJSONString(json));
		  }
		  catch(ParseException pe){
		    System.out.println(pe);
		  }
		
	}
	
}
