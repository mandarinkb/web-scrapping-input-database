
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class StaffTeam {

    List<String> listPage = new ArrayList<>();
    List<String> listStaff = new ArrayList<>();
    public String baseLink = "http://www.livesoccer888.com";

    public String getNewLinkImage(String url) {
        url = url.replace("../../../..", "");
        url = url.replace("../..", "");
        url = url.replace("/..", "");
        url = url.replace("..", "");
        return url;
    }

    public void getTeamPage(String url, String league) {
        JSONObject json;
        String BaseLinkPalyer = "";
        if ("thaipremierleague".equals(league)) {
            BaseLinkPalyer = "http://www.livesoccer888.com/thaipremierleague/teams/";
        }
        if ("premierleague".equals(league)) {
            BaseLinkPalyer = "http://www.livesoccer888.com/premierleague/teams/";
        }
        try {
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Elements elements = doc.select(".content.main-content.left-content");
            String title = elements.select(".title-page").text();
            System.out.println(title);

            Elements eles = elements.select(".MatchTeamFull");
            for (Element ele : eles) {
                json = new JSONObject();
                Elements a = ele.select("a");
                String strUrl = a.attr("href");
                String linkPage = BaseLinkPalyer + strUrl;
                listPage.add(linkPage);
                json.put("link", linkPage);

                Elements logoTeam = ele.select(".MatchLogoDivFull");  // logo
                Elements img = logoTeam.select("img");
                String logo = img.attr("src");
                logo = baseLink + getNewLinkImage(logo);
                json.put("logo_team", logo);

                String team = ele.select(".getCodeTeam").text();
                json.put("team", team);

                System.out.println(json.toString());
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //link ผู้จัดการทีม
    public void getStaffTeamPage(String url, String league) {
        try {
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Elements elements = doc.select(".staff_team");
            Elements elesLink = elements.select(".staff_div");
            Elements elesA = elesLink.select("a");
            String link = elesA.attr("href");

            url = url.replace("index.php", "");
            link = url + link;
            listStaff.add(link);

            //System.out.println(url);
            //System.out.println(link);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void getStaffTeamDetail(String url, String league) {
        try {
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Elements elements = doc.select(".data_profile");

            Elements elesImg = elements.select(".image");
            Elements elesImgProfile = elesImg.select("img");
            String imgProfile = elesImgProfile.attr("src");
            imgProfile = baseLink + getNewLinkImage(imgProfile);
            System.out.println(imgProfile);
            
            
            String pFirst = elements.select("p").first().text();
            if ("ชื่อในประเทศบ้านเกิด :".equals(pFirst)) {
                Element elesP_First = elements.select("p").first();
                elesP_First.remove();
            }

            String pSecond = elements.select("p").first().text();
            if ("ชื่อเต็ม :".equals(pSecond)) {
                Element elesP_Second = elements.select("p").first();
                elesP_Second.remove();
            }

            String enName = elements.select("p").first().text();
            System.out.println(enName);
            String thName = elements.select("p").last().text();
            System.out.println(thName);
            
            Elements elesUl = elements.select("ul");
            Elements elesUlChild = elesUl.select("*");
            int count = 0;
            String value = "";
            String key = "";
            for (Element ele : elesUlChild) {
                if (count > 0) {                                //ไม่เอาค่าแรก
                    if (ele.tagName().equals("li")) {
                        value = ele.select("li").text();        //ค่าที่ต้องการ
                    }
                    if (ele.tagName().equals("b")) {            //เลือกหัวข้อของค่าที่ต้องการ
                        key = ele.select("b").text();
                        key = key.replace(" :", "");            //ลบ _: ออก
                        value = value.replace(key, "");
                        if (!value.isEmpty()) {                 //เลือกเอาเฉพาะที่มีค่า
                            String firstChar = value.substring(0, 1);      //ตัดเอาตัวอักษรตัวแรก
                            if (!firstChar.isEmpty()) {
                                value = value.replace(" : ", "");     //ลบ _:_ ออก
                                System.out.println(key+" : "+value);
                            }
                        }
                    }
                }
                count++;
            }

            JSONObject jsonDetailTransfer;
            //ประวัติข้อมูลการทำงาน
            Elements elementsContent = doc.select(".content.main-content.left-content._margt10");
            Elements elesDataTransfer = elementsContent.select(".data_transfer");
            Elements elesContent = elesDataTransfer.select(".content");
            for (Element ele : elesContent) {
                jsonDetailTransfer = new JSONObject();
                String club = ele.select(".club").text();       //ทีมชาติ & สโมสร
                jsonDetailTransfer.put("club", club);

                String appointed = ele.select(".appointed").text();           //แต่งตั้ง
                jsonDetailTransfer.put("appointed", appointed);

                String inchange_until = ele.select(".inchange_until").text();   //สิ้นสุด
                jsonDetailTransfer.put("inchange_until", inchange_until);

                String function = ele.select(".function").text();       //ตำแหน่ง
                jsonDetailTransfer.put("function", function);

                String matches = ele.select(".matches").text();   //คุมทีม
                jsonDetailTransfer.put("matches", matches);
                
                System.out.println(jsonDetailTransfer.toString());

            }
            //จบประวัติข้อมูลการทำงาน
            
            //ดูข้อมูลแบบละเอียดทั้งหมด
            Elements elesFoot = elesDataTransfer.select(".foot");
            Elements elesA = elesFoot.select("a");
            String linkDetail = elesA.attr("href");
            String newLinkDetail = baseLink +getNewLinkImage(linkDetail);
            System.out.println(newLinkDetail);
            //จบดูข้อมูลแบบละเอียดทั้งหมด
             
            
            JSONArray arrDetail = new JSONArray();
            JSONObject jsonPerformanceDetail;
            Document doc2 = Jsoup.connect(newLinkDetail).timeout(60 * 1000).get();
            Elements elements2 = doc2.select(".data_played-full");
            Elements elesContentDetail = elements2.select(".content");
            
            if (!elesContentDetail.isEmpty()) {  //กรณีที่มีข้อมูลการคุมทีม
                System.out.println("++ข้อมูลการคุมทีม++");
                for (Element ele : elesContentDetail) {
                    jsonPerformanceDetail = new JSONObject();
                    String season = ele.select(".season").text();              //ฤดูกาล
                    jsonPerformanceDetail.put("season", season);

                    String competition = ele.select(".competition").text();    //รายการแข่งขัน
                    jsonPerformanceDetail.put("competition", competition);

                    String clubs = ele.select(".clubs").text();                //ทีมสโมสร
                    jsonPerformanceDetail.put("clubs", clubs);

                    String matches = ele.select(".matches").text();            //คุมทีม (แมตช์)
                    jsonPerformanceDetail.put("matches", matches);

                    String win = ele.select(".win").text();                    //ชนะ
                    jsonPerformanceDetail.put("win", win);

                    String draw = ele.select(".draw").text();                  //เสมอ
                    jsonPerformanceDetail.put("draw", draw);

                    String lose = ele.select(".lose").text();                  //แพ้
                    jsonPerformanceDetail.put("lose", lose);

                    String points = ele.select(".points").text();              //คะแนนรวม
                    jsonPerformanceDetail.put("points", points);

                    String ranking = ele.select(".ranking").text();            //อันดับของทีม
                    jsonPerformanceDetail.put("ranking", ranking);

                    String players_used = ele.select(".players_used").text();  //ใช้นักเตะไปทั้งหมด
                    jsonPerformanceDetail.put("players_used", players_used);

                    System.out.println(jsonPerformanceDetail.toString());
                }
                
            }

            System.out.println("");
        } catch (IOException | JSONException e) {
            e.getMessage();
        }
    }

    public void inputElasticsearch(String body, String index) {
        try {
            HttpResponse<String> response = Unirest.post("http://localhost:9200/" + index + "/text")
                    .header("Content-Type", "application/json")
                    .header("Cache-Control", "no-cache")
                    .body(body)
                    .asString();
        } catch (UnirestException ex) {
            Logger.getLogger(StaffTeam.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("insert finish");
    }

    public static void main(String[] args) {
        int count = 0;
        StaffTeam t = new StaffTeam();
        //String url = "http://www.livesoccer888.com/thaipremierleague/teams/index.php";
        String url = "http://www.livesoccer888.com/premierleague/teams/index.php";
        t.getTeamPage(url, "premierleague");
        
        
        
        
        
        
        //"premierleague"  "thaipremierleague"

        //String url = "http://www.livesoccer888.com/thaipremierleague/teams/Chonburi/index.php";
        //t.getStaffTeamPage(url, "thaipremierleague");
        
        
        //String url = "http://www.livesoccer888.com/premierleague/teams/Leicester-City/Staff/Brendan-Rodgers";
        //t.getStaffTeamDetail(url, "premierleague");
        
        
 /*       for (String link : t.listPage) {
            t.getStaffTeamPage(link, "premierleague");
        }
        for (String linkStaff : t.listStaff) {
            System.out.println(++count);
            t.getStaffTeamDetail(linkStaff, "premierleague");
        }
*/

        //String url = "http://www.livesoccer888.com/thaipremierleague/teams/Chonburi/Staff/Sasom-Pobprasert";   
        //t.getStaffTeamDetail(url, "thaipremierleague");
    }
}
