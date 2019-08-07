
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import redis.clients.jedis.Jedis;

public class Team {

    List<String> listPage = new ArrayList<>();
    public String baseLink = "http://www.livesoccer888.com";

    public String getNewLinkImage(String url) {
        url = url.replace("../../../..", "");
        url = url.replace("../..", "");
        url = url.replace("/..", "");
        url = url.replace("..", "");
        return url;
    }

    //หา link ของแต่ละทีม
    public void team(String url) {
        String newLink = url.replace("index.php", "");
        JSONObject json = new JSONObject();
        Jedis redis = new Jedis();
        redis.connect();
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
                String linkPage = newLink + strUrl;
                json.put("link_team", linkPage);

                Elements logoTeam = ele.select(".MatchLogoDivFull");  // logo
                Elements img = logoTeam.select("img");
                String logo = img.attr("src");
                logo = baseLink + getNewLinkImage(logo);
                json.put("logo_team", logo);

                String team = ele.select(".getCodeTeam").text();
                json.put("team", team);

                try {
                    String teamId = encrypt(team);
                    json.put("team_id", teamId);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                redis.rpush("team", json.toString());
                //inputElasticsearch(json.toString(),"present_teams_premierleague");
            }
        } catch (IOException | JSONException e) {
            System.out.print(e.getMessage());
        }
        redis.close();
    }

    public void player(String rdObj) {
        Jedis redis = new Jedis();
        redis.connect();

        JSONObject obj = new JSONObject(rdObj);
        String teamId = obj.getString("team_id");
        String team = obj.getString("team");
        String logoTeam = obj.getString("logo_team");
        String linkTeam = obj.getString("link_team");
        try {
            String link = linkTeam.replace("index.php", "");
            // หา link เพื่อไปยังนักเตะทีมนั้นๆ
            Document docLink = Jsoup.connect(linkTeam).timeout(60 * 1000).get();
            Element elements = docLink.select(".read-all-box").first();
            Elements elesA = elements.select("a");
            String hrefValue = elesA.attr("href");
            String newLink = link + hrefValue;

            // หารายชื่อนักเตะ
            JSONObject json = new JSONObject();
            json.put("team_id", teamId);
            json.put("team", team);
            json.put("logo_team", logoTeam);

            Document doc = Jsoup.connect(newLink).timeout(60 * 1000).get();
            JSONObject jsonDetailPlayers;
            JSONArray arrDetailPlayers = new JSONArray();
            Elements elesDetailPlayer = doc.getElementsByClass("show-table");
            Element elesTr = elesDetailPlayer.select("tr").first();             //เลือก tr tag แรกสุด
            elesTr.remove();                                                    //ลบ tr tag แรกสุด
            Element elesNext = elesDetailPlayer.select("tr").first();           //เลือก tr tag ที่ 2
            elesNext.remove();                                                  //ลบ tr tag ที่ 2

            Elements eles = elesDetailPlayer.select(".clickable-row");
            for (Element ele : eles) {
                jsonDetailPlayers = new JSONObject();
                String linkProfile = ele.attr("data-href");
                linkProfile = baseLink + getNewLinkImage(linkProfile);
                jsonDetailPlayers.put("link_profile", linkProfile);             //ลิ้งก์โปรไฟล์

                String name = ele.attr("title");
                jsonDetailPlayers.put("player_name", name);                            //ชื่อนักเตะ  

                try {
                    String nameId = encrypt(name);
                    jsonDetailPlayers.put("player_name_id", nameId);
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
                jsonDetailPlayers.put("player_nationality", nationality);              //สัญชาติ

                String position = ele.getElementsByIndexEquals(3).text();
                String[] arrPosition = position.split(" ");
                position = arrPosition[arrPosition.length - 1];  // แก้บัคโดยเอาตัวสุดท้ายจากการ split
                jsonDetailPlayers.put("position", position);                    //ตำแหน่ง

                String age = ele.getElementsByIndexEquals(5).text();
                String[] arrAge = age.split(" ");
                age = arrAge[arrAge.length - 1];  // แก้บัคโดยเอาตัวสุดท้ายจากการ split
                jsonDetailPlayers.put("age", age);                              //อายุ

                arrDetailPlayers.put(jsonDetailPlayers);
                redis.rpush("players", jsonDetailPlayers.toString());
            }
            json.put("players", arrDetailPlayers);
            //inputElasticsearch(json.toString(), "test2");
            //System.out.println(json.toString());
        } catch (IOException | JSONException e) {
            e.getMessage();
        }
    }

    public void playerDetail(String rdObj) {
        JSONObject json = new JSONObject();
        String position = "กองกลาง - ตัวรุก";
        String plink = rdObj;
/*        
        JSONObject obj = new JSONObject(rdObj);
        String position = obj.getString("position");
        String linkProfile = obj.getString("link_profile");
*/        
        try {
/*            
            Document docLinkProfile = Jsoup.connect(linkProfile).timeout(60 * 1000).get();
            Element elesDataPlayer = docLinkProfile.select(".data_played").first();
            Elements elesFoot = elesDataPlayer.select(".foot");
            Element eleImgFoot = elesFoot.select("a").first();
            String imgData = eleImgFoot.attr("href");
            String[] arrStr = imgData.split("/");
            String plink = linkProfile + "/" + arrStr[1];
*/
            //performance-detail
            boolean isGoalKeeper = false;
            if ("ผู้รักษาประตู".equals(position)) {
                isGoalKeeper = true;
            }
            //System.out.println(plink);
            Document docDataPlayedFull = Jsoup.connect(plink).timeout(60 * 1000).get();
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
                String[] arrayLi = li.split(" : ");;
                String keyJson = playerDetailEnKey(arrayLi[0]); 
                String valueJson = arrayLi[1];
                //System.out.println(keyJson+" : "+valueJson);
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
                        String club = eleContent.select(".club").text();                //ทีมสโมสร

                        json.put("link_performance_detail", plink);
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
                        
                        arrDetailPlayers.put(jsonPlayedLeagueDetail);

                    } else {  //กรณีนักเตะผู้เล่นตำแหน่งอื่นๆ
//+++++++++++รอการคำนวน++++++++++                        
                        String subSeason = eleContent.select(".season").text();            //ฤดูกาล
                        String club = eleContent.select(".club").text();                //ทีมสโมสร

                        json.put("link_performance_detail", plink);
                 
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
                        
                        arrDetailPlayers.put(jsonPlayedLeagueDetail);
                    }
                }
                json.put("performance_detail", arrDetailPlayers);
            }
            System.out.println(json.toString());
            inputElasticsearch(json.toString(), "test3");
        } catch (IOException | JSONException e) {
            e.getMessage();
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
    
    public String changeTeamThaiPremierLeague(String season, String team, String club) {
        String newTeam = team;
        if ("2019".equals(season)) {
            if ("ราชบุรี มิตรผล เอฟซี".equals(team)) {
                newTeam = "ราชบุรี มิตรผล";
            }
            if ("สมุทรปราการ ซิตี้ เอฟซี".equals(team)) {
                newTeam = "สมุทรปราการ ซิตี้";
            }
        }
        if ("2018".equals(season)) {
            if ("ราชบุรี มิตรผล เอฟซี".equals(team)) {
                newTeam = "ราชบุรี มิตรผล";
            }
        }
        if ("2017".equals(season)) {
            if ("โปลิศ เทโร เอฟซี".equals(team)) {
                newTeam = "บีอีซี เทโรศาสน";
            }
            if ("ราชบุรี มิตรผล เอฟซี".equals(team)) {
                newTeam = "ราชบุรี มิตรผล";
            }
            if ("สิงห์ เชียงราย ยูไนเต็ด".equals(team)) {
                newTeam = "เชียงราย ยูไนเต็ด";
            }
            if ("ทรู แบงค็อก ยูไนเต็ด".equals(team)) {
                newTeam = "แบงค็อก ยูไนเต็ด";
            }
            if ("ซุปเปอร์ พาวเวอร์ สมุทรปราการ เอฟซี".equals(team)) {
                newTeam = "ซุปเปอร์ พาวเวอร์ สมุทรปราการ";
            }
            if ("ไทยฮอนด้า ลาดกระบัง เอฟซี".equals(team)) {
                newTeam = "ไทยฮอนด้า เอฟซี";
            }
        }
        if ("2016".equals(season)) {
            if ("ทรู แบงค็อก ยูไนเต็ด".equals(team)) {
                newTeam = "แบงค็อก ยูไนเต็ด";
            }
            if ("บีบีซียู เอฟซี".equals(team)) {
                newTeam = "บีบีซียู";
            }
            if ("สิงห์ เชียงราย ยูไนเต็ด".equals(team)) {
                newTeam = "เชียงราย ยูไนเต็ด";
            }
            if ("ซุปเปอร์ พาวเวอร์ สมุทรปราการ เอฟซี".equals(team)) {
                newTeam = "ซุปเปอร์ พาวเวอร์ สมุทรปราการ";
            }
            if ("พัทยา ยูไนเต็ด".equals(team)) {
                newTeam = "พัทยา เอ็นเอ็นเค ยูไนเต็ด";
            }
            if ("ราชบุรี มิตรผล เอฟซี".equals(team)) {
                newTeam = "ราชบุรี มิตรผล";
            }
        }
        return newTeam;
    }

    public void inputElasticsearch(String body, String index) {
        try {
            HttpResponse<String> response = Unirest.post("http://localhost:9200/" + index + "/text")
                    .header("Content-Type", "application/json")
                    .header("Cache-Control", "no-cache")
                    .body(body)
                    .asString();
        } catch (UnirestException ex) {
            Logger.getLogger(Team.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("insert finish");
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

    public static void main(String[] args) {
        //String team = "http://www.livesoccer888.com/thaipremierleague/teams/index.php"; //
/*        String team = "http://www.livesoccer888.com/premierleague/teams/index.php";
        Team t = new Team();
        t.team(team);

        Jedis redis = new Jedis();
        redis.connect();
        boolean flag = true;
        boolean flag2 = true;
        String rdObj = null;
        while (flag) {
            rdObj = redis.rpop("team");
            if (rdObj == null) {
                flag = false;
            } else {
                t.player(rdObj);
            }
        }
        while (flag2) {
            rdObj = redis.rpop("players");
            if (rdObj == null) {
                return;
            } else {
                t.playerDetail(rdObj);
            }

        }
*/

        String team = "http://www.livesoccer888.com/thaipremierleague/teams/Trat-FC/Players/Yuki-Bamba/performance-detail";
        Team t = new Team();
        t.playerDetail(team);
    }

}
