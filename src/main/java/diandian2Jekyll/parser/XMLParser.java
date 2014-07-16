package diandian2Jekyll.parser;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public class XMLParser 
{
	public Document parse(String filePath) throws DocumentException
	{
		SAXReader reader = new SAXReader();
		Document doc = reader.read(filePath);
		return doc;
	}
}
