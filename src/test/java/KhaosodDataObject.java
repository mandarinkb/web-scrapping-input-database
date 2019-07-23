
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class KhaosodDataObject {
   
    public void content(String url) throws IOException {
        Document doc = Jsoup.connect(url).timeout(60 * 1000).get();

        Element title = doc.select(".entry-title").first();//".entry-title"
        String strTitle = title.text();
        System.out.println("title : " + strTitle);

        Element content = doc.select(".td-post-content").first();//".entry-content"
        String strContent = content.text();
        System.out.println("content : " + strContent);

        Element date = doc.select(".entry-date").first();//".post-meta-date"
        String strDate = date.text();
        System.out.println("datetime : " + strDate);

        Element img = doc.select(".td-post-featured-image img").first();//".entry-media  img"
        String imgUrl = img.attr("src");
        System.out.println("img : " + imgUrl);

    }

    public void content(String url, String str) throws IOException {
        Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
        String titleStr = "";
        String contentStr = "";
        String datetimStr = "";
        String imgStr = "";
        JSONObject objData = new JSONObject(str);
        //System.out.println(objData);
        JSONArray ary = objData.getJSONArray("select_tag");  //สังเกตใช้ในกรณีที่เป็น Jsonarray สังเกตที่มี []   
        for (int i = 0; i < ary.length(); i++) {
            String type = ary.getJSONObject(i).getString("type");  //แกะเอาเฉพาะcontentที่ต้องการ 
            switch (type) {
                case "title":
                    titleStr = ary.getJSONObject(i).getString("tag");  //แกะเอาเฉพาะcontentที่ต้องการ 
                    //System.out.println("title : " + titleStr);
                    break;
                case "content":
                    contentStr = ary.getJSONObject(i).getString("tag");  //แกะเอาเฉพาะcontentที่ต้องการ 
                    //System.out.println("content : " + contentStr);
                    break;
                case "datetime":
                    datetimStr = ary.getJSONObject(i).getString("tag");  //แกะเอาเฉพาะcontentที่ต้องการ 
                    //System.out.println("datetime : " + datetimStr);
                    break;

                case "img":
                    imgStr = ary.getJSONObject(i).getString("tag");  //แกะเอาเฉพาะcontentที่ต้องการ 
                    //System.out.println("img : " + imgStr);
                    break;
            }
        }

        Element title = doc.select(titleStr).first();//".entry-title"
        String strTitle = title.text();
        System.out.println("title : " + strTitle);

        Element content = doc.select(contentStr).first();//".entry-content"
        String strContent = content.text();
        System.out.println("content : " + strContent);

        Element date = doc.select(datetimStr).first();//".post-meta-date"
        String strDate = date.text();
        System.out.println("datetime : " + strDate);

        if (!doc.select(imgStr).isEmpty()) {
            Element img = doc.select(imgStr).first();//".entry-media  img"
            String imgUrl = img.attr("src");
            System.out.println("img : " + imgUrl);
        }else{
            String imgUrl = "empty";
            System.out.println("img : " + imgUrl);
        }
            

        JSONObject obj = new JSONObject();

    }

    public static void main(String[] args) throws IOException {
        String url = "https://www.khaosod.co.th/newspaper-column/analysis-today-politics/news_1041277";//https://www.khaosod.co.th/newspaper-column/analysis-today-politics/news_935694
        String str = "{\"delete_tag\":[{\"\":\"\"}],\"select_tag\":[{\"tag\":\".entry-title\",\"type\":\"title\"},{\"tag\":\".td-post-content\",\"type\":\"content\"},{\"tag\":\".entry-date\",\"type\":\"datetime\"},{\"tag\":\".td-post-featured-image img\",\"type\":\"img\"}]}";
        KhaosodDataObject a = new KhaosodDataObject();
        // a.content(url);
        a.content(url, str);

    } 
}
