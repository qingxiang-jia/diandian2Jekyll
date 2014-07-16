package diandian2Jekyll.helper;

import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;

import diandian2Jekyll.parser.XMLParser;

/**
 * @author Qingxiang Jia
 * This classes reads the xml and maps the image id to image url.
 */
public class GenerateImageHashMap 
{
	@SuppressWarnings("rawtypes")
	public HashMap<String, String> getHashMap(Node images)
	{
		HashMap<String, String> imageHashMap = new HashMap<String, String>();
		List imageList = images.selectNodes("./Image");
		for(int i = 0; i < imageList.size(); i++)
		{
			Node imageNode = (Node) imageList.get(i);
			imageHashMap.put(imageNode.selectSingleNode("./Id").getText(), imageNode.selectSingleNode("./Url").getText());
		}
		return imageHashMap;
	}
	
	// Test
	public static void main(String[] args) throws DocumentException
	{
		XMLParser parser = new XMLParser();
        Document entries = parser.parse("/Users/lee/Downloads/diandian-blog-backup-longstation-20140707-143426.xml");
        Node images = entries.selectSingleNode("//Images");
        GenerateImageHashMap generator = new GenerateImageHashMap();
        generator.getHashMap(images);
	}
}
