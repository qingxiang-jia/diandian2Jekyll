package diandian2Jekyll.helper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.script.*;

/**
 * @author Qingxiang Jia
 * The actual class that runs JavaScript in JVM (because of the issues mention in Html2Md, I didn't use this).
 */
public class Html2MdJsImp 
{
	ScriptEngineManager factory;
	ScriptEngine engine;
	String jsSrc;
	
	public Html2MdJsImp(String jsSrcPath)
	{
		jsSrc = "";
		try {
			factory = new ScriptEngineManager();
			engine = factory.getEngineByName("JavaScript");
			jsSrc = readFile(jsSrcPath, Charset.defaultCharset());
		} catch (IOException e) {
			System.out.println("IOException");
		}
	}
	
	static String readFile(String path, Charset encoding) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	public String convertFromFile(String htmlPath)
	{
		try {
			String htmlToConvert = readFile(htmlPath, Charset.defaultCharset());
			return convertFromString(htmlToConvert);
		} catch (IOException e) { e.printStackTrace(); }
		return ""; // Dummy return
	}
	
	public String convertFromString(String html)
	{
		String markdown = "";
		try {
			engine.put("htmlToConvert", html);
			markdown = (String) engine.eval("var toBeConverted = htmlToConvert;" + jsSrc +"convert(toBeConverted);");
		} catch (ScriptException e) { e.printStackTrace(); }
		return markdown;
	}
	
	// Test
	public static void main(String[] args)
	{
		Html2MdJsImp converter = new Html2MdJsImp("/Users/lee/Dropbox/Spring/workspace/DiandianXMLParser/src/main/java/DiandianXMLParser/helper/to-markdown.js");
		System.out.println(converter.convertFromFile("/Users/lee/Desktop/raw sample.html"));
	}
}
