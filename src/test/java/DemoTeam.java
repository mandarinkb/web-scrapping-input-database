
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import redis.clients.jedis.Jedis;

public class DemoTeam {

    public void presentPlayerThaiPremierLeague(String url) {

        String baseLink = "http://www.livesoccer888.com";

        try {

            // หารายชื่อนักเตะ
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            JSONObject jsonDetailPlayers;
            JSONArray arrDetailPlayers = new JSONArray();
            Elements elesDetailPlayer = doc.getElementsByClass("show-table");
            Element elesTr = elesDetailPlayer.select("tr").first();             //เลือก tr tag แรกสุด
            elesTr.remove();                                                    //ลบ tr tag แรกสุด
            Element elesNext = elesDetailPlayer.select("tr").first();           //เลือก tr tag ที่ 2
            elesNext.remove();                                                  //ลบ tr tag ที่ 2

            Elements eles = elesDetailPlayer.select(".clickable-row");
            
            int c = 0;
            for (Element ele : eles) {
                jsonDetailPlayers = new JSONObject();

                String linkProfile = ele.attr("data-href");
                linkProfile = baseLink + getNewLinkImage(linkProfile);
                jsonDetailPlayers.put("link_profile", linkProfile);             //ลิ้งก์โปรไฟล์
                System.out.println(++c +". "+linkProfile);

                String name = ele.attr("title");
                jsonDetailPlayers.put("player_name", name);                     //ชื่อนักเตะ  

                try {
                    String nameId = encrypt(name);
                    jsonDetailPlayers.put("player_name_id", nameId);
                    System.out.println(name +" : "+nameId);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                String number = ele.select(".number_hide").text();
                jsonDetailPlayers.put("squad_nember", number);                  //เบอร์เสื้อ

                Elements elesImgProfile = ele.getElementsByClass("img");        //link image profile
                if (elesImgProfile.hasClass("img")) {
                    Elements elesChild = elesImgProfile.select("*");
                    for (Element eleImgProfile : elesChild) {
                        if (eleImgProfile.tagName().equals("img")) {
                            Element eleImg = eleImgProfile.select("img").first();
                            String img = eleImg.attr("src");
                            img = baseLink + getNewLinkImage(img);
                            jsonDetailPlayers.put("img_player", img);           //รูปนักเตะ  
                        }
                    }
                }
                Elements eleNationality = ele.select(".flag-icon");
                String nationality = eleNationality.attr("title");
                jsonDetailPlayers.put("player_nationality", nationality);       //สัญชาติ

                String position = ele.getElementsByIndexEquals(3).text();
                String[] arrPosition = position.split(" ");
                position = arrPosition[arrPosition.length - 1];                 //แก้บัคโดยเอาตัวสุดท้ายจากการ split
                jsonDetailPlayers.put("position", position);                    //ตำแหน่ง

                String age = ele.getElementsByIndexEquals(5).text();
                String[] arrAge = age.split(" ");
                age = arrAge[arrAge.length - 1];                                //แก้บัคโดยเอาตัวสุดท้ายจากการ split
                jsonDetailPlayers.put("age", age);                              //อายุ
                jsonDetailPlayers.put("type", "present_players_detail_thaipremierleague");    //ประแกาศเพื่อส่งต่อไปยังฟังก็ชันถัดไป

                presentPlayerDetailThaiPremierLeague(linkProfile,position);
                arrDetailPlayers.put(jsonDetailPlayers);
            }
        } catch (IOException | JSONException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void presentPlayerDetailThaiPremierLeague(String url ,String position) {
        
        
        JSONObject json = new JSONObject();
        try {
            Document docLinkProfile = Jsoup.connect(url).timeout(60 * 1000).get();
            Element elesDataPlayer = docLinkProfile.select(".data_played").first();
            Elements elesFoot = elesDataPlayer.select(".foot");
            Element eleImgFoot = elesFoot.select("a").first();
            String imgData = eleImgFoot.attr("href");
            String[] arrStr = imgData.split("/");
            String performanceDetailLink = url + "/" + arrStr[1];

            //performance-detail
            boolean isGoalKeeper = false;
            if ("ผู้รักษาประตู".equals(position)) {
                isGoalKeeper = true;
            }
            Document docDataPlayedFull = Jsoup.connect(performanceDetailLink).timeout(60 * 1000).get();
            Elements elesContentPfmBox= docDataPlayedFull.select(".content.pfm-box");
            Elements elesUl = elesContentPfmBox.select("ul");
            
            Elements elesUlAll = elesUl.select("*");
            int maxLi = 0;
            for (Element eleLi: elesUlAll) {  // นับจำนวน tag li ทั้งหมด
                if (eleLi.tagName().equals("li")) {
                    maxLi++;
                }
            }
            for (int i = 0; i < maxLi; i++) {
                String li = elesUl.select("li").get(i).text();
                String[] arrayLi = li.split(" : ");
                String keyJson = playerDetailEnKey(arrayLi[0]); 
                String valueJson = arrayLi[1];
                json.put(keyJson, valueJson);
            }
            
            Element elesDataPlayedFull = docDataPlayedFull.select(".data_played-full").first();
            if (elesDataPlayedFull != null) {

                Elements elesContent = elesDataPlayedFull.select(".content");

                int maxContent = 0;
                for (Element eleDataContent : elesContent) {  // นับจำนวน class content 
                    if (eleDataContent.hasClass("content")) {
                        maxContent++;
                    }
                }
                JSONObject jsonPlayedLeagueDetail;
                JSONArray arrDetailPlayers = new JSONArray();
                for (int i = 0; i < (maxContent - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                    jsonPlayedLeagueDetail = new JSONObject();
                    Element eleContent = elesDataPlayedFull.select(".content").get(i);  //index แรกเริ่มจาก 0
//+++++++++++รอการคำนวน++++++++++
                    if (isGoalKeeper) {  // กรณีผู้รักษาประตู
                        String subSeason = eleContent.select(".season").text();            //ฤดูกาล  
                        String club = eleContent.select(".club").text();                   //ทีมสโมสร

                        json.put("link_performance_detail", performanceDetailLink);
                        jsonPlayedLeagueDetail.put("season", subSeason);
                        jsonPlayedLeagueDetail.put("club", club);

                        String competition = eleContent.select(".competition").text();  //รายการแข่งขัน
                        jsonPlayedLeagueDetail.put("competition", competition);

                        String matches = eleContent.select(".matches.league_club_keeper").text();              //ลงเล่น(แมตซ์)
                        jsonPlayedLeagueDetail.put("matches", matches);

                        String goals = eleContent.select(".data.league_club_keeper").get(0).text();           //ทำประตู 
                        jsonPlayedLeagueDetail.put("goals", goals);

                        String own_goals = eleContent.select(".data.league_club_keeper").get(1).text();       //ทำเข้าประตูตนเอง
                        jsonPlayedLeagueDetail.put("own_goals", own_goals);

                        String substituted_on = eleContent.select(".data.league_club_keeper").get(2).text();  //เปลี่ยนตัวเข้า
                        jsonPlayedLeagueDetail.put("substituted_on", substituted_on);

                        String substituted_off = eleContent.select(".data.league_club_keeper").get(3).text();  //เปลี่ยนตัวออก
                        jsonPlayedLeagueDetail.put("substituted_off", substituted_off);

                        String yellow = eleContent.select(".data.league_club_keeper").get(4).text();           //ใบเหลือง
                        jsonPlayedLeagueDetail.put("yellow_cards", yellow);

                        String yellow_red = eleContent.select(".data.league_club_keeper").get(5).text();      //ใบเหลือง/ใบเเดง
                        jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);

                        String red = eleContent.select(".data.league_club_keeper").get(6).text();             //ใบแดง
                        jsonPlayedLeagueDetail.put("red_cards", red);

                        String conceded = eleContent.select(".data.league_club_keeper").get(7).text();        //เสียประตู
                        jsonPlayedLeagueDetail.put("goals_conceded", conceded);

                        String shutout = eleContent.select(".data.league_club_keeper").get(8).text();         //คลีนชีท
                        jsonPlayedLeagueDetail.put("clean_sheets", shutout);

                        String time = eleContent.select(".time").text();                                      //ลงเล่น (เวลา:นาที)
                        jsonPlayedLeagueDetail.put("minutes_played", time);
                        
                        jsonPlayedLeagueDetail.put("average_statistics", 5.56);
                        arrDetailPlayers.put(jsonPlayedLeagueDetail);

                    } else {  //กรณีนักเตะผู้เล่นตำแหน่งอื่นๆ
//+++++++++++รอการคำนวน++++++++++                        
                        String subSeason = eleContent.select(".season").text();            //ฤดูกาล
                        String club = eleContent.select(".club").text();                   //ทีมสโมสร

                        json.put("link_performance_detail", performanceDetailLink);
                 
                        jsonPlayedLeagueDetail.put("season", subSeason);
                        jsonPlayedLeagueDetail.put("club", club);

                        String competition = eleContent.select(".competition").text();  //รายการแข่งขัน
                        jsonPlayedLeagueDetail.put("competition", competition);

                        String matches = eleContent.select(".matches.league_club").text();               //ลงเล่น(แมตซ์)
                        jsonPlayedLeagueDetail.put("matches", matches);

                        String goals = eleContent.select(".data.league_club").get(0).text();            //ทำประตู
                        jsonPlayedLeagueDetail.put("goals", goals);

                        String assists = eleContent.select(".data.league_club").get(1).text();          //แอสซิสต์
                        jsonPlayedLeagueDetail.put("assists", assists);

                        String own_goals = eleContent.select(".data.league_club").get(2).text();        //ทำเข้าประตูตนเอง
                        jsonPlayedLeagueDetail.put("own_goals", own_goals);

                        String substituted_on = eleContent.select(".data.league_club").get(3).text();   //เปลี่ยนตัวเข้า
                        jsonPlayedLeagueDetail.put("substituted_on", substituted_on);

                        String substituted_off = eleContent.select(".data.league_club").get(4).text();  //เปลี่ยนตัวออก 
                        jsonPlayedLeagueDetail.put("substituted_off", substituted_off);

                        String yellow = eleContent.select(".data.league_club").get(5).text();           //ใบเหลือง
                        jsonPlayedLeagueDetail.put("yellow_cards", yellow);

                        String yellow_red = eleContent.select(".data.league_club").get(6).text();       //ใบเหลือง/ใบเเดง
                        jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);

                        String red = eleContent.select(".data.league_club").get(7).text();              //ใบเเดง
                        jsonPlayedLeagueDetail.put("red_cards", red);

                        String penalty_goals = eleContent.select(".data.league_club").get(8).text();    //ทำประตู(จุดโทษ)
                        jsonPlayedLeagueDetail.put("penalty_goals", penalty_goals);

                        String mpg = eleContent.select(".mpg").text();                                  //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)
                        jsonPlayedLeagueDetail.put("minutes_played_goals", mpg);

                        String time = eleContent.select(".time").text();                                //ลงเล่น (เวลา:นาที)
                        jsonPlayedLeagueDetail.put("minutes_played", time);
                        
                        jsonPlayedLeagueDetail.put("average_statistics", 5.56);
                        arrDetailPlayers.put(jsonPlayedLeagueDetail);
                    }
                }
                json.put("performance_detail", arrDetailPlayers);
                System.out.println(json);
            }
        } catch (IOException | JSONException e) {
            System.out.println(e.getMessage());
        }
    }    
    public String playerDetailEnKey(String inputKey) {
        String key = "";
        if ("ชื่อ".equals(inputKey)) {
            key = "player_name";
        }
        if ("วันเกิด".equals(inputKey)) {
            key = "birthday";
        }
        if ("สัญชาติ".equals(inputKey)) {
            key = "nationality";
        }
        if ("ส่วนสูง".equals(inputKey)) {
            key = "height";
        }
        if ("ตำแหน่ง".equals(inputKey)) {
            key = "position";
        }
        if ("อดีตสังกัดทีมชาติ".equals(inputKey)) {
            key = "former_national_team";
        }
        return key;
    } 
    public String getNewLinkImage(String url) {
        url = url.replace("../../../..", "");
        url = url.replace("../..", "");
        url = url.replace("/..", "");
        url = url.replace("..", "");
        return url;
    }

    public String encrypt(String str) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashInBytes = md.digest(str.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    public static void main(String[] args){
        DemoTeam d = new DemoTeam();
        d.presentPlayerThaiPremierLeague("http://www.livesoccer888.com/thaipremierleague/teams/Chonburi/Players/index.php");
    }
    
}
