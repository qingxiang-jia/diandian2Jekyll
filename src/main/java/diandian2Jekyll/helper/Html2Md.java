package diandian2Jekyll.helper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.overzealous.remark.Remark;

/**
 * @author Qingxiang Jia
 * Use the to-markdown (http://domchristie.github.io/to-markdown/) engine to convert HTML to markdown.
 * Because of the performance issues, I didn't use this class.
 */
public class Html2Md 
{
	Remark remark;
	HashMap<String, String> imgIdToImgUrl;
	Html2MdJsImp jsConverter;
	
	public Html2Md() // For testing purpose
	{
		remark = new Remark();		
		jsConverter = new Html2MdJsImp("/Users/lee/Dropbox/Spring/workspace/DiandianXMLParser/src/main/java/DiandianXMLParser/helper/to-markdown.js");
	}
	
	public Html2Md(HashMap<String, String> imgIdToImgUrl)
	{
		remark = new Remark();
		this.imgIdToImgUrl = imgIdToImgUrl;
		jsConverter = new Html2MdJsImp("/Users/lee/Dropbox/Spring/workspace/DiandianXMLParser/src/main/java/DiandianXMLParser/helper/to-markdown.js");
	}
	
	public String convert(String dirtyPost)
	{
		// Reverse HTML entities
		String reversed = remark.convert(dirtyPost);
		// Alter img tag with Jsoup
		Document parsedHtml = Jsoup.parseBodyFragment(reversed);
		for (Element imgTag : parsedHtml.select("img"))
		{
			imgTag.attr("src", imgTag.attr("Id"));
			imgTag.attr("src", imgIdToImgUrl.get(imgTag.attr("Id"))); // Actual, not for testing
			imgTag.removeAttr("Id");
		}
		// Pass the ready html to JavaScript converter
		return jsConverter.convertFromString(parsedHtml.body().html());
	}
	
	// Test
	public static void main(String[] args) throws IOException
	{
		Html2Md converter = new Html2Md();
		System.out.println(converter.convert(FileUtils.readFileToString(new File("/Users/lee/Desktop/raw sample.html"))));
	}
}
