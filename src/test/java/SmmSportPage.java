import java.io.IOException;
import java.util.List;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import redis.clients.jedis.Jedis;


public class SmmSportPage {
    public static String url = "http://livescore.smmsport.com";
    
    public String getPrevPage(String inputUrl) {
        String getUrl = null;
        try {
            Document doc = Jsoup.connect(inputUrl).timeout(60 * 1000).get();
            Elements elements = doc.getElementsByClass("ButtonPrev");
            Element eleUrl = elements.select("a").first();
            String urlPrev = eleUrl.attr("href");

            System.out.println(urlPrev);
            return url + urlPrev;

        } catch (IOException e) {
            //e.getMessage();
            return getUrl;
        }
    }
    
    public void test(String page) {  
        Jedis redis = new Jedis("127.0.0.1");
        try {
            boolean flag = true;
            while (flag) {
                if (page != null) {
                    Document doc = Jsoup.connect(page).timeout(60 * 1000).get();
                    Elements elements = doc.getElementsByClass("ButtonPrev");
                    Element eleUrl = elements.select("a").first();
                    String urlPrev = eleUrl.attr("href");
                    String newPage = url + urlPrev;
                    page = newPage;
                    
                    String pageJson = "{page:\""+page+"\"}";
                    redis.rpush("SmmSport", pageJson);
                    
                    //System.out.println(pageJson);
                    System.out.println("new page : " +newPage);
                    
                } else {
                    flag = false;
                }
            }

        } catch (IOException e) {
            e.getMessage();
        }
    }
    
    public void pop(){
        Jedis redis = new Jedis("127.0.0.1");
        int count = 0;
        boolean flag = true;
        while (flag) {
            String str = redis.rpop("SmmSport");
            if (str == null) {
                flag = false;
                return;
            }
            JSONObject obj = new JSONObject(str);
            String strObj = obj.getString("page");
            System.out.println(++count + " : " + strObj);
            //System.out.println(++count +" : "+ str);

        }
    }
   
    
    public static void main(String[] args){
        SmmSportPage smm = new SmmSportPage();
        String newPage = smm.getPrevPage(url);
        //smm.test(newPage);
        smm.pop();
        
    }
    
    
    
    
    
    
}
