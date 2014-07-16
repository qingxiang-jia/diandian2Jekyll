package diandian2Jekyll.helper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.overzealous.remark.Remark;

import diandian2Jekyll.model.AudioPost;
import diandian2Jekyll.model.PhotoPost;
import diandian2Jekyll.model.TextPost;
import diandian2Jekyll.model.VideoPost;
import diandian2Jekyll.parser.XMLParser;

/**
 * @author Qingxiang Jia
 * A set of convenient methods to help parsing.
 */
public class ParserHelper
{
	ArticleProcessor processor;
	HashMap<String, String> imgIdToImgUrl;
	Remark remark;
	Whitelist nonIsAllowed; // Not used since I ended up not converting HTML to markdown at all.
	
	public ParserHelper() // Constructor for testing
	{
		remark = new Remark();
		nonIsAllowed = Whitelist.none();
	}
	
	public ParserHelper(HashMap<String, String> imgIdToImgUrl)
	{
		this.imgIdToImgUrl = imgIdToImgUrl;
		processor = new ArticleProcessor(imgIdToImgUrl);
		remark = new Remark();
		nonIsAllowed = new Whitelist();
	}
	
	/**
	 * @param root Where you want to start showing the XML structure.
	 * @param indent Initial indentation
	 * Performs a DFS on the elements of the XML to expose its structure.
	 */
	@SuppressWarnings("unchecked")
	public void printElementsByDFS(Element root, String indent)
    {
		Iterator<Element> iter = (Iterator<Element>) root.elementIterator();
    	while(iter.hasNext())
    	{
    		Element nextRoot = iter.next();
    		String nextIndent = indent+"    ";
    		System.out.println(nextIndent+nextRoot.getName()+"XPath: "+nextRoot.getStringValue());
    		printElementsByDFS(nextRoot, nextIndent);
    	}
    }
	
	private String getProperCreateTime(String createTime)
	{
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date(Long.parseLong(createTime)));
	}
	
	@SuppressWarnings("rawtypes")
	private String[] processTags(Node tags)
	{
		if(tags == null)
			return new String[]{""};
		else
		{
			List listOfTags = tags.selectNodes("./Tag");
			if(listOfTags == null)
				return new String[]{""};
			String[] arrayOfTags = new String[listOfTags.size()];
			for(int i = 0; i < listOfTags.size(); i++)
				arrayOfTags[i] = ((Node) listOfTags.get(i)).getText();
			return arrayOfTags;
		}
	}
	
	private String cleanHtmlEntity(String html) // Eventually not used
	{
		// Convert HTML entities to text
		String result = remark.convert(html);
		// Remove HTML tags
		result = Jsoup.clean(result, nonIsAllowed);
		// In case of any ill formed tag remains ("\" is caused by Remark's conversion)
		result = result.replace("/", "").replace("\\", "");
		return result;
	}
	
	/**
	 * @param post The blog post
	 * @return TextPost object
	 * Acts as a dispatcher for processing posts.
	 */
	public TextPost postHandler(Node post)
	{
		String postType = post.selectSingleNode("./PostType").getText();
		if(postType.equals("text"))
			return handleTextPost(post);
		else if(postType.equals("photo"))
			return handlePhotoPost(post);
		else if(postType.equals("audio"))
			return handleAudioPost(post);
		else if(postType.equals("video"))
			return handleVideoPost(post);
		else
			return new TextPost(); // Dummy return
	}
	
	
	/**
	 * @param textPost 
	 * @return TextPost is the most basic object that represents the post. Based on it,
	 * there are PhotoPost, AudioPost, and VideoPost objects.
	 */
	public TextPost handleTextPost(Node textPost)
	{
		TextPost resultPost = new TextPost();
		resultPost.createTime = getProperCreateTime(textPost.selectSingleNode("./CreateTime").getText());
		resultPost.tags = processTags(textPost.selectSingleNode("./Tags"));
		resultPost.title = cleanHtmlEntity(textPost.selectSingleNode("./Title").getText());
		resultPost.text = processor.process(textPost.selectSingleNode("./Text").getText());
		return resultPost;
	}
	
	/**
	 * @param photoPost
	 * @return Notice the title tag equivalent and text tag equivalent.
	 */
	public PhotoPost handlePhotoPost(Node photoPost)
	{
		PhotoPost resultPost = new PhotoPost();
		resultPost.createTime = getProperCreateTime(photoPost.selectSingleNode("./CreateTime").getText());
		resultPost.tags = processTags(photoPost.selectSingleNode("./Tags"));
		if(photoPost.selectSingleNode("./Desc").getText() == null)
			resultPost.title = "A Photo I like";
		else
			resultPost.title = cleanHtmlEntity(photoPost.selectSingleNode("./Desc").getText());
		
		if(photoPost.selectSingleNode("./PhotoItem/Desc").getText() == null)
			resultPost.text = "A photo I like.";
		else
			resultPost.text = processor.process(photoPost.selectSingleNode("./PhotoItem/Desc").getText());
		
		resultPost.id = imgIdToImgUrl.get(photoPost.selectSingleNode("./PhotoItem/Id").getText());
		return resultPost;
	}
	
	/**
	 * @param audioPost
	 * @return Notice the text tag equivalent.
	 */
	public AudioPost handleAudioPost(Node audioPost)
	{
		AudioPost resultPost = new AudioPost();
		resultPost.createTime = getProperCreateTime(audioPost.selectSingleNode("./CreateTime").getText());
		resultPost.tags = processTags(audioPost.selectSingleNode("./Tags"));
		resultPost.title = cleanHtmlEntity(audioPost.selectSingleNode("./SongName").getText());
		if(audioPost.selectSingleNode("./Comment").getText() == null)
			resultPost.text = "A song I like.";
		else
			resultPost.text = processor.process(audioPost.selectSingleNode("./Comment").getText());
		
		resultPost.albumName = audioPost.selectSingleNode("./AlbumName").getText();
		resultPost.artistName = audioPost.selectSingleNode("./ArtistName").getText();
		resultPost.songId = audioPost.selectSingleNode("./SongId").getText();
		resultPost.cover = audioPost.selectSingleNode("./Cover").getText();
		return resultPost;
	}
	
	/**
	 * @param videoPost
	 * @return Notice the text tag equivalent.
	 */
	public VideoPost handleVideoPost(Node videoPost)
	{
		VideoPost resultPost = new VideoPost();
		resultPost.createTime = getProperCreateTime(videoPost.selectSingleNode("./CreateTime").getText());
		resultPost.tags = processTags(videoPost.selectSingleNode("./Tags"));
		resultPost.title = cleanHtmlEntity(videoPost.selectSingleNode("./VideoName").getText());
		String content = videoPost.selectSingleNode("./Content").getText();
		String videoDesc = videoPost.selectSingleNode("./VideoDesc").getText();
		if(content.length() > videoDesc.length())
			resultPost.text = processor.process(videoPost.selectSingleNode("./Content").getText());
		else
			resultPost.text = processor.process(videoPost.selectSingleNode("./VideoDesc").getText());
		
		resultPost.videoUrl = videoPost.selectSingleNode("./VideoVideoUrl").getText();
		resultPost.videoImgUrl = videoPost.selectSingleNode("./VideoImgUrl").getText();
		return resultPost;
	}
	
	// Test
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws DocumentException
	{
		ParserHelper helper = new ParserHelper();
		System.out.println(helper.getProperCreateTime("1379990885327"));
		
		XMLParser parser = new XMLParser();
        Document doc = parser.parse("/Users/lee/Desktop/4 types of post.xml");
        List posts = doc.selectNodes("//Posts/Post");
        for(int i = 0; i < posts.size(); i++)
        	helper.postHandler((Node) posts.get(i));
	}
}
