package diandian2Jekyll.helper;

import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import diandian2Jekyll.parser.XMLParser;

/**
 * @author Qingxiang Jia
 * This class processes the <text> tag (or its equivalent, see .model package) of a <post>.
 */
public class ArticleProcessor 
{
	// Feed in img id, you get the actual image url.
	HashMap<String, String> imgIdToImgUrl;
	
	public ArticleProcessor(HashMap<String, String> imgIdToImgUrl)
	{
		this.imgIdToImgUrl = imgIdToImgUrl;
	}
	
	public String process(String textWithHtmlEntities)
	{	// Convert the HTML entities into text.
		Document parsedHtml = Jsoup.parse(textWithHtmlEntities);
		for (Element imgTag : parsedHtml.select("img")) // Replace img id with actual image url.
		{
			imgTag.attr("src", imgTag.attr("Id"));
			imgTag.attr("src", imgIdToImgUrl.get(imgTag.attr("Id")));
			imgTag.removeAttr("Id");
		}
		return parsedHtml.body().html();
	}
	
	// Test
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws DocumentException
	{
		XMLParser parser = new XMLParser();
		org.dom4j.Document entries = parser.parse("/Users/lee/Downloads/diandian-blog-backup-longstation-20140707-143426.xml");
    	Node images = entries.selectSingleNode("//Images");
        GenerateImageHashMap generator = new GenerateImageHashMap();
        HashMap<String, String> imageHashMap = generator.getHashMap(images);
        ArticleProcessor processor = new ArticleProcessor(imageHashMap);
        
        List posts = entries.selectNodes("//Posts/Post");
        for(int i = 0; i < posts.size(); i++)
        {
        	try {
        		System.out.println(processor.process(((org.dom4j.Node) posts.get(i)).selectSingleNode("./Text").getText()));
        	} catch (NullPointerException e) {
        		System.out.println("An article skipped");
        	}
        }
	}
}
