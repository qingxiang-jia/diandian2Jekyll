package diandian2Jekyll.presenter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import diandian2Jekyll.model.AudioPost;
import diandian2Jekyll.model.PhotoPost;
import diandian2Jekyll.model.TextPost;
import diandian2Jekyll.model.VideoPost;

/**
 * @author Qingxiang Jia
 * This class prints out the post objects (TextPost, PhotoPost, AudioPost, and VideoPost) to .md file.
 */
public class PostPrinter 
{
	public void printToFile(ArrayList<TextPost> readyPosts)
	{
		for(TextPost post: readyPosts)
			if(post instanceof VideoPost)
				printVideoPostToFile((VideoPost) post);
			else if(post instanceof AudioPost)
				printAudioPostToFile((AudioPost) post);
			else if(post instanceof PhotoPost)
				printPhotoPostToFile((PhotoPost) post);
			else
				printTextPostToFile(post);
	}
	
	private String YamlBuilder(TextPost post)
	{
		String layout = "layout: post\n";
		String title = "title: " + post.title + "\n";
		String categories = "categories:\n- Diandian\n";
		String tags = "tags:\n";
		for(String tag: post.tags)
			tags = tags + "- " + tag + "\n";
		return "---\n" + layout + title + categories + tags + "\n---\n";
	}
	
	private void printTextPostToFile(TextPost post)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(YamlBuilder(post)); // Yaml
		builder.append(post.text); // My article
		writeStringToFile(generateFilename(post.title, post.createTime), builder.toString());
	}
	
	private void printPhotoPostToFile(PhotoPost post) // Only the first photo is grabbed.
	{
		StringBuilder builder = new StringBuilder();
		builder.append(YamlBuilder(post)); // Yaml
		builder.append(post.text); // My article
		
		builder.append("\n!["+post.title+"]("+post.id+" "+"\""+post.title+"\")\n"); //![Alt text](/url/img.jpg "Title")
		writeStringToFile(generateFilename(post.title, post.createTime), builder.toString());
	}
	
	private void printAudioPostToFile(AudioPost post)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(YamlBuilder(post)); // Yaml
		builder.append(post.text); // My article
		
		builder.append("* Album Name: "+post.albumName+"\n");
		builder.append("* Artist Name: "+post.artistName+"\n");
		builder.append("&lt;script type=\"text/javascript\" src=\"http://www.xiami.com/widget/player-single?uid=0&sid="
				+post.songId+"&mode=js\"&gt;&lt;/script&gt;\n");
		builder.append("[Music cover]("+post.cover+" \""+post.title+"\")\n");
		writeStringToFile(generateFilename(post.title, post.createTime), builder.toString());
	}
	
	private void printVideoPostToFile(VideoPost post)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(YamlBuilder(post)); // Yaml
		builder.append(post.text); // My article
		
		//[an example](http://example.com/ "Title")
		builder.append("\nClick to watch the video("+post.videoUrl+" \""+"点击看视频\")\n");
		builder.append("\n!["+post.title+"]("+post.videoImgUrl+" "+"\""+post.title+"\")\n");
	}
	
	private void writeStringToFile(String filename, String content)
	{
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(filename));
			writer.write(content);
			writer.close(); } catch (IOException e) { e.printStackTrace(); }
		System.out.println("File saved: "+filename);
	}
	
	private String generateFilename(String title, String createTime)
	{
		return createTime+"-"+title.replace(" ", "-")+".md";
	}
}
