
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SmmSport {
public static String date = "12/05/2019";

    public String page(String url) throws IOException, InterruptedException {
        ArrayList<JSONObject> MyArrJson = null;
        ArrayList<JSONObject> arr = new ArrayList<JSONObject >();
        JSONObject objMain = new JSONObject();
        objMain.put("วันที่", date);
        JSONObject object = null;
        JSONObject subobject = null;
        
        Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
        Elements elements = doc.getElementsByClass("match_zone");
        for (Element ele : elements) {
            MyArrJson = new ArrayList<JSONObject>();
            object = new JSONObject();
            
            String country = ele.getElementsByClass("col1").text();
            String league = ele.getElementsByClass("col3").text();
            
            object.put("ประเทศ",country);  //country
            object.put("ลีก",league);      //
            Elements elesDetails = ele.getElementsByClass("match_program");
            for (Element eleDetail : elesDetails) {
                subobject = new JSONObject();
                
                String time = eleDetail.getElementsByClass("kickon").text();
                String home = eleDetail.getElementsByClass("match_item_home").text();
                String homeScore = eleDetail.getElementsByClass("home_score").text();
                String away = eleDetail.getElementsByClass("match_item_away").text();
                String awayScore = eleDetail.getElementsByClass("away_score").text();
                
                subobject.put("เวลา",time);
                subobject.put("home",home);
                subobject.put("home_score",Integer.parseInt(homeScore));
                subobject.put("away",away);
                subobject.put("away_score",Integer.parseInt(awayScore));
                MyArrJson.add(subobject);
            }
            //MyArrJson.add(subobject);
            object.put("ผลการแข่งขัน",MyArrJson); //result
            arr.add(object);
        }
        objMain.put("รายละเอียด", arr); //detail
        return objMain.toString();
    }
    
    public void elasticsearch(String body){
        try {
            HttpResponse<String> response = Unirest.post("http://localhost:9200/smmsport/_doc")
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "PostmanRuntime/7.11.0")
                    .header("Accept", "*/*")
                    .header("Cache-Control", "no-cache")
                    .header("Postman-Token", "1d20e8f1-0b7a-424c-812d-f769f2a38776,cd0befcb-e82c-4fc5-a341-f718fbd4afed")
                    .body(body)
                    .asString();
          System.out.println("ข้อมูลลง database เรียบร้อยแล้ว");  
        } catch (UnirestException ex) {
            Logger.getLogger(SmmSport.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String url = "http://livescore.smmsport.com/result_program.php?date=" + date;
        SmmSport smm = new SmmSport();
        String body = smm.page(url);
        System.out.println(body);
        smm.elasticsearch(body);
    }
}
