
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Statistics {
    public String baseLink = "http://www.livesoccer888.com";
    public String getNewLinkImage(String url) {
        url = url.replace("../../../..", "");
        url = url.replace("../..", "");
        url = url.replace("/..", "");
        url = url.replace("..", "");
        return url;
    }  
    
    public String ddmmyyyyToyyyymmdd(String inputDate) {
        String day = inputDate.substring(0, 2);
        String month = inputDate.substring(3, 5);
        String year = inputDate.substring(6, 10);
        String mix = year + "-" + month + "-" + day;
        return mix;
    }
    
    public void stats(String url, String league) {
        String BaseLinkPalyer = "";
        if ("thaipremierleague".equals(league)) {
            BaseLinkPalyer = "http://www.livesoccer888.com/thaipremierleague";
        }
        if ("premierleague".equals(league)) {
            BaseLinkPalyer = "http://www.livesoccer888.com/premierleague";
        }
        try {
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Elements elements = doc.select(".content.main-content.left-content");

            List<String> listStats = new ArrayList<>();
            String strUrl;
            // เก็บ id component ของเดือน
            for (Element ele : elements) {
                Elements elesId = ele.getElementsByClass("league-stage");
                for (Element eleGetMonth : elesId) {
                    Elements a = eleGetMonth.select("a");
                    for (Element href : a) {
                        strUrl = href.attr("href");
                        strUrl = strUrl.replace("#", ""); //ตัด # ออก
                        listStats.add(strUrl); // เก็บใส่ list ไว้ 
                    }
                }
            }
            for (String idStats : listStats) {
                JSONObject json = new JSONObject();
                JSONObject jsonDetail;
                JSONArray arr = new JSONArray();
                
                //json.put("link", url);  // link
                if("topscores".equals(idStats)){
                    json.put("statistics", "goals");     // ดาวซัลโว
                    json.put("link", url+"#topscores");  // link
                }
                if("topassists".equals(idStats)){
                   json.put("statistics", "assists");    // แอสซิสต์
                   json.put("link", url+"#topassists");  // link
                }  
                if("cleansheets".equals(idStats)){
                    json.put("statistics", "clean_sheets");  // คลีนชีท
                    json.put("link", url+"#cleansheets");  // link
                }
                if("most-yellowcard".equals(idStats)){
                    json.put("statistics", "yellow_cards");  // ใบเหลือง
                    json.put("link", url+"#most-yellowcard");  // link
                } 
                if("most-yellowredcard".equals(idStats)){
                    json.put("statistics", "yellow_red_cards");  // เหลือง/แดง
                    json.put("link", url+"#most-yellowredcard");  // link
                }   
                if("most-redcard".equals(idStats)){
                    json.put("statistics", "red_cards");    // ใบแดง
                    json.put("link", url+"#most-redcard");  // link
                }  
                if("best-minutes-per-goal".equals(idStats)){
                    json.put("statistics", "minutes_played_goals");  //ค่าเฉลี่ย : ประตู
                    json.put("link", url+"#best-minutes-per-goal");  // link
                }                 
                Element ele = doc.getElementById(idStats);
                String notice = ele.select(".notice").text();  //อัพเดตล่าสุดเมื่อ
                json.put("date_update_th", notice); 
                
                String date_update = notice.replace("อัพเดตล่าสุดเมื่อ ", "");
                date_update = ddmmyyyyToyyyymmdd(date_update);
                json.put("date_update", date_update); 

                String title = ele.select(".statistics-title").text();  //title
                json.put("title", title);

                Elements elesData = ele.getElementsByClass("data");
                String rank = null;
                for (Element eleData : elesData) {
                    jsonDetail = new JSONObject();
                    String rankValue = eleData.select(".rank").text();  //อันดับ
                    if (rankValue.isEmpty()) {
                        jsonDetail.put("rank", rank);
                    } else {
                        rank = rankValue;
                        jsonDetail.put("rank", rank);
                    }

                    Elements eleName = eleData.select(".name");  // link profile
                    Elements a = eleName.select("a");
                    String linkProfile = a.attr("href");
                    linkProfile = BaseLinkPalyer + getNewLinkImage(linkProfile);
                    jsonDetail.put("link_profile", linkProfile);

                    String name = eleData.select(".name").text();  //อันดับ
                    jsonDetail.put("name", name);

                    Elements logoTeam = eleData.select(".inimage");  // logo
                    Elements img = logoTeam.select("img");
                    String logo = img.attr("src");
                    logo = baseLink + getNewLinkImage(logo);
                    jsonDetail.put("logo_team", logo);

                    String team = eleData.select(".inteam").text();  //ทีม
                    jsonDetail.put("team", team);

                    String stats = eleData.select(".stats").text();
                    if ("topscores".equals(idStats)) {
                        jsonDetail.put("goals",stats );     // ดาวซัลโว
                    }
                    if ("topassists".equals(idStats)) {
                        jsonDetail.put("assists",stats );    // แอสซิสต์
                    }
                    if ("cleansheets".equals(idStats)) {
                        jsonDetail.put("clean_sheets",stats );  // คลีนชีท
                    }
                    if ("most-yellowcard".equals(idStats)) {
                        jsonDetail.put("yellow_cards",stats );  // ใบเหลือง
                    }
                    if ("most-yellowredcard".equals(idStats)) {
                        jsonDetail.put("yellow_red_cards",stats );  // เหลือง/แดง
                    }
                    if ("most-redcard".equals(idStats)) {
                        jsonDetail.put("red_cards",stats );    // ใบแดง
                    }
                    if ("best-minutes-per-goal".equals(idStats)) {
                        jsonDetail.put("minutes_played_goals",stats );  //ค่าเฉลี่ย : ประตู
                        String goal = eleData.select(".goal").text();
                        jsonDetail.put("goal", goal);
                    }                 
                    arr.put(jsonDetail);
                }
                json.put("detail", arr); 
                inputElasticsearch(json.toString(),"test_statistics_pr");
                System.out.println(json.toString());
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }
    public void inputElasticsearch(String body,String index) {
        try {
            HttpResponse<String> response = Unirest.post("http://localhost:9200/"+index+"/text")
                    .header("Content-Type", "application/json")
                    .header("Cache-Control", "no-cache")
                    .body(body)
                    .asString();
        } catch (UnirestException ex) {
            Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("insert finish");
    }    

    public static void main(String[] args) {
        Statistics st = new Statistics();
        //String url = "http://www.livesoccer888.com/thaipremierleague/statistics/index.php";
        String url = "http://www.livesoccer888.com/premierleague/statistics/index.php";

        //"premierleague"  "thaipremierleague"
        st.stats(url,"premierleague"); 
    }
}
