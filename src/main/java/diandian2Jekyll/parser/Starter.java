package diandian2Jekyll.parser;

import diandian2Jekyll.helper.GenerateImageHashMap;
import diandian2Jekyll.helper.ParserHelper;
import diandian2Jekyll.model.TextPost;
import diandian2Jekyll.presenter.PostPrinter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Everything starts here.
 */
public class Starter {
  XMLParser parser;
  ParserHelper helper;
  Document entries;
  HashMap<String, String> imageHashMap;
  ArrayList<TextPost> readyPosts;
  PostPrinter printer;

  public static void main(String[] args) throws DocumentException {
    Starter starter = new Starter();
    starter.run(args[0]);
  }

  @SuppressWarnings("rawtypes")
  public void run(String xmlFilePath) throws DocumentException {
    parser = new XMLParser();
    entries = parser.parse("/Users/lee/Downloads/diandian-blog-backup-longstation-20140707-143426.xml");
    Node images = entries.selectSingleNode("//Images");
    GenerateImageHashMap generator = new GenerateImageHashMap();
    imageHashMap = generator.getHashMap(images);
    helper = new ParserHelper(imageHashMap);
    readyPosts = new ArrayList<TextPost>();
    printer = new PostPrinter();

    List posts = entries.selectNodes("//Posts/Post");
    for (int i = 0; i < posts.size(); i++) {
      Element post = (Element) posts.get(i);
      readyPosts.add(helper.postHandler(post));
    }
    printer.printToFile(readyPosts);
  }
}
