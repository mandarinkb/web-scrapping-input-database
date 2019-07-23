
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author MandariN
 */
public class GetTeam {
    List<String> listPage = new ArrayList<>();
    List<String> listStaff = new ArrayList<>();
    List<String> LinkPlayerOfTeam = new ArrayList<>();
    public String baseLink = "http://www.livesoccer888.com";
    
    public String getNewLinkImage(String url) {
        url = url.replace("../../../..", "");
        url = url.replace("../..", "");
        url = url.replace("/..", "");
        url = url.replace("..", "");
        return url;
    } 
    //หา link ของแต่ละทีม
    public void getTeamPage(String url) {
        String newLink = url.replace("index.php", "");
        JSONObject json;
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
                listPage.add(linkPage);
                json.put("link", linkPage);

                Elements logoTeam = ele.select(".MatchLogoDivFull");  // logo
                Elements img = logoTeam.select("img");
                String logo = img.attr("src");
                logo = baseLink + getNewLinkImage(logo);
                json.put("logo_team", logo);

                String team = ele.select(".getCodeTeam").text();
                json.put("team", team);

                //System.out.println(json.toString());
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //link ผู้จัดการทีม
    public void getStaff(String url) {
        try {
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Elements elements = doc.select(".staff_team");
            Elements elesLink = elements.select(".staff_div");
            Elements elesA = elesLink.select("a");
            String link = elesA.attr("href");
            url = url.replace("index.php", "");
            link = url + link;
            //listStaff.add(link);

            //System.out.println(url);
            System.out.println(link);
            
            Elements eles = elements.select(".staff_image");
            Elements elesImg = eles.select("img");
            String img = elesImg.attr("src");
            img = getNewLinkImage(img);
            System.out.println(img);

            String name = elements.select(".staff_name").text();
            System.out.println(name);
            
            String age = elements.select(".staff_appointed").get(0).text(); //first
            System.out.println(age);
            
            String nationality = elements.select(".staff_appointed").get(1).text(); //second
            System.out.println(nationality);
            
            String appointed = elements.select(".staff_appointed").get(2).text(); //third
            System.out.println(appointed);
            
            System.out.println("");
        } catch (Exception e) {
            e.getMessage();
        }
    }
    
    
    
    // หา link เพื่อไปยังนักเตะทีมนั้นๆ
    public void getLinkPlayerOfTeam(String url) {
        String link = url.replace("index.php", "");
        try {
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Element elements = doc.select(".read-all-box").first();  
            Elements elesA = elements.select("a");
            String hrefValue = elesA.attr("href");
            String newLink = link + hrefValue;
            //System.out.println(newLink);
            LinkPlayerOfTeam.add(newLink);
            
        } catch (Exception e) {
            e.getMessage();
        }
    } 
    
    // list ข้อมูล ผู้จัดการทีม และนักเตะ
    public void listTeamDetail(String url, String league) {
        try {
            // รายชื่อนักเตะ
            JSONObject json = new JSONObject();
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
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
                jsonDetailPlayers.put("name", name);                            //ชื่อนักเตะ    

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

                String position = ele.getElementsByIndexEquals(3).text();   
                jsonDetailPlayers.put("position", position);                    //ตำแหน่ง

                Elements eleNationality = ele.select(".flag-icon");        
                String nationality = eleNationality.attr("title");
                jsonDetailPlayers.put("nationality", nationality);              //สัญชาติ

                String age = ele.getElementsByIndexEquals(5).text();       
                jsonDetailPlayers.put("age", age);                              //อายุ
                arrDetailPlayers.put(jsonDetailPlayers);
            }
            json.put("players", arrDetailPlayers);
            System.out.println(json.toString());

            /*if ("thaipremierleague".equals(league)) {
                els.inputElasticsearch(json.toString(), "team_detail_thaipremierleague");
                System.out.println(dateTimes.thaiDateTime()+" : insert team detail thaipremierleague complete");
            }
            if ("premierleague".equals(league)) {
                els.inputElasticsearch(json.toString(), "team_detail_premierleague");
                System.out.println(dateTimes.thaiDateTime()+" : insert team detail premierleague complete");
            }
*/
        } catch (IOException | JSONException e) {
            e.getMessage();
        }

    }   
    public void listPlayerPremierLeague(String url, String season, String detail) {
        String newLink = url.replace("index.php", "");
        JSONObject json;
        JSONObject jsonDetail;
        try {
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Elements elements = doc.select(".content.main-content.left-content");
            String title = elements.select(".title-page").text();

            //หา link ของแต่ละทีม
            Elements eles = elements.select(".MatchTeamFull");
            for (Element ele : eles) {
                json = new JSONObject();
                Elements a = ele.select("a");
                String strUrl = a.attr("href");
                String linkPage = newLink + strUrl;

                json.put("title", title);
                json.put("season", season);
                json.put("link", linkPage);

                Elements logoTeam = ele.select(".MatchLogoDivFull");  // logo
                Elements img = logoTeam.select("img");
                String logo = img.attr("src");
                logo = baseLink + getNewLinkImage(logo);
                json.put("logo_team", logo);

                String team = ele.select(".getCodeTeam").text();
                if (!team.isEmpty()) {
                    json.put("team", team);
                }

                //link ผู้จัดการทีม
                jsonDetail = new JSONObject();
                Document docLink = Jsoup.connect(linkPage).timeout(60 * 1000).get();
                Elements elesStaffTeam = docLink.select(".staff_team");
                if (!elesStaffTeam.isEmpty()) {  //ทำเฉพาะที่มีข้อมูลผู้จัดการทีมเท่านั้น ถ้าไม่มีข้อมูลก็ข้ามไป
                    Elements elesLink = elesStaffTeam.select(".staff_div");
                    Elements elesA = elesLink.select("a");
                    String link = elesA.attr("href");
                    linkPage = linkPage.replace("index.php", "");
                    link = linkPage + link;
                    jsonDetail.put("link_staff_profile", link);

                    Elements elesStaffImage = elesStaffTeam.select(".staff_image");
                    Elements elesImg = elesStaffImage.select("img");
                    String imgProfile = elesImg.attr("src");
                    imgProfile = baseLink + getNewLinkImage(imgProfile);
                    jsonDetail.put("staff_img", imgProfile);

                    String name = elesStaffTeam.select(".staff_name").text();
                    jsonDetail.put("staff_name", name);

                    String age = elesStaffTeam.select(".staff_appointed").get(0).text(); //first
                    age = age.replace("อายุ : ", "");
                    jsonDetail.put("staff_age", age);

                    String nationality = elesStaffTeam.select(".staff_appointed").get(1).text(); //second
                    nationality = nationality.replace("สัญชาติ : ", "");
                    jsonDetail.put("staff_nationality", nationality);

                    String appointed = elesStaffTeam.select(".staff_appointed").get(2).text(); //third
                    appointed = appointed.replace("แต่งตั้งเมื่อ : ", "");
                    jsonDetail.put("staff_appointed", appointed);

                    json.put("staff", jsonDetail);
                }

                // หา link เพื่อไปยังนักเตะทีมนั้นๆ
                Document docLinkTeam = Jsoup.connect(linkPage).timeout(60 * 1000).get();
                if (team.isEmpty()) {
                    Elements elesTeam = docLinkTeam.select(".titleContent-half._margl._margr._margt");
                    String getTeam = elesTeam.select(".wrapcolor-2").text(); 
                    getTeam = getTeam.replace("ผลบอล ", "");
                    team = getTeam;
                    json.put("team", team);
                }
                Element elesReadAllBox = docLinkTeam.select(".read-all-box").first();
                Elements elesReadAllBoxA = elesReadAllBox.select("a");
                String hrefValue = elesReadAllBoxA.attr("href");
                String originalLink = linkPage.replace("index.php", "");
                String linkPlayerOfTeam = originalLink + hrefValue;

                // list ข้อมูลนักเตะ  
                JSONArray arrDetailPlayers = new JSONArray();
                Document docListPlayer = Jsoup.connect(linkPlayerOfTeam).timeout(60 * 1000).get();
                //Elements elesDetailPlayer = docListPlayer.getElementsByClass("show-table");
                Elements elesDetailPlayer = docListPlayer.select(".show-table.sortable._margt");

                String elesTrHtml = elesDetailPlayer.select("tr").first().text();             //เลือก tr tag แรกสุด
                String[] arrOfStr = elesTrHtml.split(" ");
                int valueOfColumn = arrOfStr.length;   //นับจำนวนคอร์ลัม

                Element elesTr = elesDetailPlayer.select("tr").first();             //เลือก tr tag แรกสุด
                elesTr.remove();                                                    //ลบ tr tag แรกสุด
                Element elesNext = elesDetailPlayer.select("tr").first();           //เลือก tr tag ที่ 2
                elesNext.remove();                                                  //ลบ tr tag ที่ 2

                Elements elesClickableRow = elesDetailPlayer.select(".clickable-row");
                for (Element eleClickableRow : elesClickableRow) {
                    jsonDetail = new JSONObject();

                    String playerName = eleClickableRow.attr("title");
                    jsonDetail.put("player_name", playerName);                      //ชื่อนักเตะ   

                    Elements elesInjury = eleClickableRow.select(".injury_class");
                    if (!elesInjury.isEmpty()) {   //กรณีมีนักเตะที่ได้รับบาดเจ็บ
                        String injury = elesInjury.attr("title");
                        jsonDetail.put("injury", injury);      // อาการบาดเจ็บ             
                    }

                    Elements elesImgProfile = eleClickableRow.getElementsByClass("img");        //link image profile
                    if (elesImgProfile.hasClass("img")) {
                        Elements elesChild = elesImgProfile.select("*");
                        for (Element eleImgProfile : elesChild) {
                            if (eleImgProfile.tagName().equals("img")) {
                                Element eleImg = eleImgProfile.select("img").first();
                                String playerImg = eleImg.attr("src");
                                playerImg = baseLink + getNewLinkImage(playerImg);
                                jsonDetail.put("player_img", playerImg);           //รูปนักเตะ  
                            }
                        }
                    }
                    Elements eleNationality = eleClickableRow.select(".flag-icon");
                    String playerNationality = eleNationality.attr("title");
                    jsonDetail.put("player_nationality", playerNationality);              //สัญชาติ

                    String position = null;
                    String playerAge;
                    if (valueOfColumn == 5) {
                        position = eleClickableRow.getElementsByIndexEquals(3).text();
                        String[] arrPosition = position.split(" ");
                        int value = 0;
                        for (int i = 0; i < arrPosition.length; i++) {
                            value++;
                        }
                        position = arrPosition[value - 1];

                        playerAge = eleClickableRow.getElementsByIndexEquals(5).text();
                        String[] arrPlayerAge = playerAge.split(" ");
                        playerAge = arrPlayerAge[0];

                        jsonDetail.put("position", position);                    //ตำแหน่ง
                        jsonDetail.put("player_age", playerAge);

                    } else {
                        position = eleClickableRow.getElementsByIndexEquals(4).text();
                        String[] arrPosition = position.split(" ");
                        int value = 0;
                        for (int i = 0; i < arrPosition.length; i++) {
                            value++;
                        }
                        position = arrPosition[value - 1];

                        playerAge = eleClickableRow.getElementsByIndexEquals(6).text();
                        String[] arrPlayerAge = playerAge.split(" ");
                        playerAge = arrPlayerAge[0];

                        jsonDetail.put("position", position);                    //ตำแหน่ง
                        jsonDetail.put("player_age", playerAge);
                    }

                    String linkProfile = eleClickableRow.attr("data-href");
                    //ข้อมูลทีม season ปัจจุบัน
                    if ("present_teams".equals(detail)) {
                        linkProfile = baseLink + getNewLinkImage(linkProfile);
                        jsonDetail.put("link_player_profile", linkProfile);             //ลิ้งก์โปรไฟล์ เอาlink นี้ไปหาข้อมูลต่อไป

                        String number = eleClickableRow.select(".number_hide").text();
                        jsonDetail.put("squad_nember", number);                  //เบอร์เสื้อ

                        Document docLinkProfile = Jsoup.connect(linkProfile).timeout(60 * 1000).get();
                        Element elesDataPlayer = docLinkProfile.select(".data_played").first();
                        Elements elesFoot = elesDataPlayer.select(".foot");
                        Element eleImgFoot = elesFoot.select("a").first();
                        String imgData = eleImgFoot.attr("href");
                        String[] arrStr = imgData.split("/");
                        String plink = linkProfile + "/" + arrStr[1];

                        //performance-detail
                        String inputEditSeason = season.replace("20", "");  // เฉพาะพรีเมียร์ลีกเท่านั้น

                        boolean isGoalKeeper = false;
                        if ("ผู้รักษาประตู".equals(position)) {
                            isGoalKeeper = true;
                        }
                        Document docDataPlayedFull = Jsoup.connect(plink).timeout(60 * 1000).get();
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
                            for (int i = 0; i < (maxContent - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                                jsonPlayedLeagueDetail = new JSONObject();
                                Element eleContent = elesDataPlayedFull.select(".content").get(i);  //index แรกเริ่มจาก 0

                                if (isGoalKeeper) {  // กรณีผู้รักษาประตู
                                    String subSeason = eleContent.select(".season").text();            //ฤดูกาล  
                                    String club = eleContent.select(".club").text();                //ทีมสโมสร

                                    //System.out.println(plink);
                                    jsonDetail.put("link_performance_detail", plink);
                                    String EditSubSeason = subSeason.replace("20", "");  // เฉพาะพรีเมียร์ลีกเท่านั้น
                                    String editTeam  = changeTeamPremierLeague(season, team, club);
                                    if (inputEditSeason.equals(EditSubSeason) && editTeam.equals(club)) {  //เอาเฉพาะฤดูกาลนั้นๆ
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

                                        //arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                                        jsonDetail.put("performance_detail", jsonPlayedLeagueDetail);
                                        //System.out.println(jsonPlayedLeagueDetail.toString());
                                    }
                                } else {  //กรณีนักเตะผู้เล่นตำแหน่งอื่นๆ
                                    String subSeason = eleContent.select(".season").text();            //ฤดูกาล
                                    String club = eleContent.select(".club").text();                //ทีมสโมสร

                                    //System.out.println(plink);
                                    jsonDetail.put("link_performance_detail", plink);
                                    String EditSubSeason = subSeason.replace("20", "");  // เฉพาะพรีเมียร์ลีกเท่านั้น
                                    String editTeam  = changeTeamPremierLeague(season, team, club);
                                    if (inputEditSeason.equals(EditSubSeason) && editTeam.equals(club)) {  //เอาเฉพาะฤดูกาลนั้นๆ                    
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

                                        jsonDetail.put("performance_detail", jsonPlayedLeagueDetail);
                                        //System.out.println(jsonPlayedLeagueDetail.toString());
                                    }
                                }
                            }
                        }
                    } else {  //ข้อมูลทีม season ในอดีต
                        String baseLinkProfile = linkPlayerOfTeam.replace("index.php", "");
                        linkProfile = baseLinkProfile + linkProfile;
                        jsonDetail.put("link_player_profile", linkProfile);             //ลิ้งก์โปรไฟล์ เอาlink นี้ไปหาข้อมูลต่อไป

                        String number = eleClickableRow.select(".number_hide-2").text();
                        jsonDetail.put("squad_nember", number);                  //เบอร์เสื้อ

                        Document docLinkProfile = Jsoup.connect(linkProfile).timeout(60 * 1000).get();
                        Element elesDataPlayer = docLinkProfile.select(".data_played").first();
                        Elements elesFoot = elesDataPlayer.select(".foot");
                        Element eleImgFoot = elesFoot.select("a").first();
                        String imgData = eleImgFoot.attr("href");
                        String[] arrStr = imgData.split("/");
                        String plink = linkProfile + "/" + arrStr[1];

                        //performance-detail
                        String inputEditSeason = season.replace("20", "");  // เฉพาะพรีเมียร์ลีกเท่านั้น
                        inputEditSeason = inputEditSeason.replace("-", "/");

                        boolean isGoalKeeper = false;
                        if ("ผู้รักษาประตู".equals(position)) {
                            isGoalKeeper = true;
                        }
                        Document docDataPlayedFull = Jsoup.connect(plink).timeout(60 * 1000).get();
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
                            for (int i = 0; i < (maxContent - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                                jsonPlayedLeagueDetail = new JSONObject();
                                Element eleContent = elesDataPlayedFull.select(".content").get(i);  //index แรกเริ่มจาก 0

                                if (isGoalKeeper) {  // กรณีผู้รักษาประตู
                                    String subSeason = eleContent.select(".season").text();            //ฤดูกาล  
                                    String club = eleContent.select(".club").text();                //ทีมสโมสร

                                    //System.out.println(plink);
                                    jsonDetail.put("link_performance_detail", plink);
                                    String EditSubSeason = subSeason.replace("20", "");  // เฉพาะพรีเมียร์ลีกเท่านั้น
                                    String editTeam  = changeTeamPremierLeague(season, team, club);
                                    if (inputEditSeason.equals(EditSubSeason) && editTeam.equals(club)) {  //เอาเฉพาะฤดูกาลนั้นๆ
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

                                        jsonDetail.put("performance_detail", jsonPlayedLeagueDetail);
                                    }
                                } else {  //กรณีนักเตะผู้เล่นตำแหน่งอื่นๆ
                                    String subSeason = eleContent.select(".season").text();            //ฤดูกาล
                                    String club = eleContent.select(".club").text();                //ทีมสโมสร

                                    //System.out.println(plink);
                                    jsonDetail.put("link_performance_detail", plink);
                                    String EditSubSeason = subSeason.replace("20", "");  // เฉพาะพรีเมียร์ลีกเท่านั้น
                                    String editTeam  = changeTeamPremierLeague(season, team, club);
                                    if (inputEditSeason.equals(EditSubSeason) && editTeam.equals(club)) {  //เอาเฉพาะฤดูกาลนั้นๆ                    
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

                                        jsonDetail.put("performance_detail", jsonPlayedLeagueDetail);
                                        //System.out.println(jsonPlayedLeagueDetail.toString());
                                    }
                                }
                            }
                        }
                    }
                    arrDetailPlayers.put(jsonDetail);
                }

                json.put("players", arrDetailPlayers);
                inputElasticsearch(json.toString(), "premierleague_2019-20");
                //System.out.println(json.toString());
            }
        } catch (IOException | JSONException e) {
            System.out.println(e.getMessage());
        }
    } 
    
    public void listPlayerThaiPremierLeague(String url, String season, String detail) {
        String newLink = url.replace("index.php", "");
        JSONObject json;
        JSONObject jsonDetail;
        try {
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Elements elements = doc.select(".content.main-content.left-content");
            String title = elements.select(".title-page").text();

            //หา link ของแต่ละทีม
            Elements eles = elements.select(".MatchTeamFull");
            for (Element ele : eles) {
                json = new JSONObject();
                Elements a = ele.select("a");
                String strUrl = a.attr("href");
                String linkPage = newLink + strUrl;

                json.put("title", title);
                json.put("season", season);
                json.put("link", linkPage);

                Elements logoTeam = ele.select(".MatchLogoDivFull");  // logo
                Elements img = logoTeam.select("img");
                String logo = img.attr("src");
                logo = baseLink + getNewLinkImage(logo);
                json.put("logo_team", logo);

                String team = ele.select(".getCodeTeam").text();
                json.put("team", team);

                //link ผู้จัดการทีม
                jsonDetail = new JSONObject();
                Document docLink = Jsoup.connect(linkPage).timeout(60 * 1000).get();
                Elements elesStaffTeam = docLink.select(".staff_team");
                if (!elesStaffTeam.isEmpty()) {  //ทำเฉพาะที่มีข้อมูลผู้จัดการทีมเท่านั้น ถ้าไม่มีข้อมูลก็ข้ามไป
                    Elements elesLink = elesStaffTeam.select(".staff_div");
                    Elements elesA = elesLink.select("a");
                    String link = elesA.attr("href");
                    linkPage = linkPage.replace("index.php", "");
                    link = linkPage + link;
                    jsonDetail.put("link_staff_profile", link);

                    Elements elesStaffImage = elesStaffTeam.select(".staff_image");
                    Elements elesImg = elesStaffImage.select("img");
                    String imgProfile = elesImg.attr("src");
                    imgProfile = baseLink + getNewLinkImage(imgProfile);
                    jsonDetail.put("staff_img", imgProfile);

                    String name = elesStaffTeam.select(".staff_name").text();
                    jsonDetail.put("staff_name", name);

                    String age = elesStaffTeam.select(".staff_appointed").get(0).text(); //first
                    age = age.replace("อายุ : ", "");
                    jsonDetail.put("staff_age", age);

                    String nationality = elesStaffTeam.select(".staff_appointed").get(1).text(); //second
                    nationality = nationality.replace("สัญชาติ : ", "");
                    jsonDetail.put("staff_nationality", nationality);

                    String appointed = elesStaffTeam.select(".staff_appointed").get(2).text(); //third
                    appointed = appointed.replace("แต่งตั้งเมื่อ : ", "");
                    jsonDetail.put("staff_appointed", appointed);

                    json.put("staff", jsonDetail);
                }

                // หา link เพื่อไปยังนักเตะทีมนั้นๆ
                Document docLinkTeam = Jsoup.connect(linkPage).timeout(60 * 1000).get();

                Element elesReadAllBox = docLinkTeam.select(".read-all-box").first();
                Elements elesReadAllBoxA = elesReadAllBox.select("a");
                String hrefValue = elesReadAllBoxA.attr("href");
                String originalLink = linkPage.replace("index.php", "");
                String linkPlayerOfTeam = originalLink + hrefValue;

                // list ข้อมูลนักเตะ  
                JSONArray arrDetailPlayers = new JSONArray();
                Document docListPlayer = Jsoup.connect(linkPlayerOfTeam).timeout(60 * 1000).get();
                //Elements elesDetailPlayer = docListPlayer.getElementsByClass("show-table");
                Elements elesDetailPlayer = docListPlayer.select(".show-table.sortable._margt");

                String elesTrHtml = elesDetailPlayer.select("tr").first().text();             //เลือก tr tag แรกสุด
                String[] arrOfStr = elesTrHtml.split(" ");
                int valueOfColumn = arrOfStr.length;   //นับจำนวนคอร์ลัม

                Element elesTr = elesDetailPlayer.select("tr").first();             //เลือก tr tag แรกสุด
                elesTr.remove();                                                    //ลบ tr tag แรกสุด
                Element elesNext = elesDetailPlayer.select("tr").first();           //เลือก tr tag ที่ 2
                elesNext.remove();                                                  //ลบ tr tag ที่ 2

                Elements elesClickableRow = elesDetailPlayer.select(".clickable-row");
                for (Element eleClickableRow : elesClickableRow) {
                    jsonDetail = new JSONObject();

                    String playerName = eleClickableRow.attr("title");
                    jsonDetail.put("player_name", playerName);                      //ชื่อนักเตะ   

                    Elements elesInjury = eleClickableRow.select(".injury_class");
                    if (!elesInjury.isEmpty()) {   //กรณีมีนักเตะที่ได้รับบาดเจ็บ
                        String injury = elesInjury.attr("title");
                        jsonDetail.put("injury", injury);      // อาการบาดเจ็บ             
                    }

                    Elements elesImgProfile = eleClickableRow.getElementsByClass("img");        //link image profile
                    if (elesImgProfile.hasClass("img")) {
                        Elements elesChild = elesImgProfile.select("*");
                        for (Element eleImgProfile : elesChild) {
                            if (eleImgProfile.tagName().equals("img")) {
                                Element eleImg = eleImgProfile.select("img").first();
                                String playerImg = eleImg.attr("src");
                                playerImg = baseLink + getNewLinkImage(playerImg);
                                jsonDetail.put("player_img", playerImg);           //รูปนักเตะ  
                            }
                        }
                    }
                    Elements eleNationality = eleClickableRow.select(".flag-icon");
                    String playerNationality = eleNationality.attr("title");
                    jsonDetail.put("player_nationality", playerNationality);              //สัญชาติ

                    String position = null;
                    String playerAge;
                    if (valueOfColumn == 5) {
                        position = eleClickableRow.getElementsByIndexEquals(3).text();
                        String[] arrPosition = position.split(" ");
                        int value = 0;
                        for (int i = 0; i < arrPosition.length; i++) {
                            value++;
                        }
                        position = arrPosition[value - 1];

                        playerAge = eleClickableRow.getElementsByIndexEquals(5).text();
                        String[] arrPlayerAge = playerAge.split(" ");
                        playerAge = arrPlayerAge[0];

                        jsonDetail.put("position", position);                    //ตำแหน่ง
                        jsonDetail.put("player_age", playerAge);

                    } else {
                        position = eleClickableRow.getElementsByIndexEquals(4).text();
                        String[] arrPosition = position.split(" ");
                        int value = 0;
                        for (int i = 0; i < arrPosition.length; i++) {
                            value++;
                        }
                        position = arrPosition[value - 1];

                        playerAge = eleClickableRow.getElementsByIndexEquals(6).text();
                        String[] arrPlayerAge = playerAge.split(" ");
                        playerAge = arrPlayerAge[0];

                        jsonDetail.put("position", position);                    //ตำแหน่ง
                        jsonDetail.put("player_age", playerAge);
                    }

                    String linkProfile = eleClickableRow.attr("data-href");
                    //ข้อมูลทีม season ปัจจุบัน
                    if ("present_teams".equals(detail)) {
                        linkProfile = baseLink + getNewLinkImage(linkProfile);
                        jsonDetail.put("link_player_profile", linkProfile);             //ลิ้งก์โปรไฟล์ เอาlink นี้ไปหาข้อมูลต่อไป

                        String number = eleClickableRow.select(".number_hide").text();
                        jsonDetail.put("squad_nember", number);                  //เบอร์เสื้อ

                        Document docLinkProfile = Jsoup.connect(linkProfile).timeout(60 * 1000).get();
                        Element elesDataPlayer = docLinkProfile.select(".data_played").first();
                        Elements elesFoot = elesDataPlayer.select(".foot");
                        Element eleImgFoot = elesFoot.select("a").first();
                        String imgData = eleImgFoot.attr("href");
                        String[] arrStr = imgData.split("/");
                        String plink = linkProfile + "/" + arrStr[1];

                        //performance-detail
                        boolean isGoalKeeper = false;
                        if ("ผู้รักษาประตู".equals(position)) {
                            isGoalKeeper = true;
                        }
                        //System.out.println(plink);
                        Document docDataPlayedFull = Jsoup.connect(plink).timeout(60 * 1000).get();
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
                            for (int i = 0; i < (maxContent - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                                jsonPlayedLeagueDetail = new JSONObject();
                                Element eleContent = elesDataPlayedFull.select(".content").get(i);  //index แรกเริ่มจาก 0

                                if (isGoalKeeper) {  // กรณีผู้รักษาประตู
                                    String subSeason = eleContent.select(".season").text();            //ฤดูกาล  
                                    String club = eleContent.select(".club").text();                //ทีมสโมสร

                                    //System.out.println(plink);
                                    jsonDetail.put("link_performance_detail", plink);
                                    club  = changeTeamThaiPremierLeague(season, team, club);
                                    if (season.equals(subSeason) && team.equals(club)) {  //เอาเฉพาะฤดูกาลนั้นๆ
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

                                        //arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                                        jsonDetail.put("performance_detail", jsonPlayedLeagueDetail);
                                        //System.out.println(jsonPlayedLeagueDetail.toString());
                                    }
                                } else {  //กรณีนักเตะผู้เล่นตำแหน่งอื่นๆ
                                    String subSeason = eleContent.select(".season").text();            //ฤดูกาล
                                    String club = eleContent.select(".club").text();                //ทีมสโมสร

                                    //System.out.println(plink);
                                    jsonDetail.put("link_performance_detail", plink);
                                    club  = changeTeamThaiPremierLeague(season, team, club);
                                    if (season.equals(subSeason) && team.equals(club)) {  //เอาเฉพาะฤดูกาลนั้นๆ                    
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

                                        jsonDetail.put("performance_detail", jsonPlayedLeagueDetail);
                                        //System.out.println(jsonPlayedLeagueDetail.toString());
                                    }
                                }
                            }
                        }
                    } else {  //ข้อมูลทีม season ในอดีต
                        String baseLinkProfile = linkPlayerOfTeam.replace("index.php", "");
                        linkProfile = baseLinkProfile + linkProfile;
                        jsonDetail.put("link_player_profile", linkProfile);             //ลิ้งก์โปรไฟล์ เอาlink นี้ไปหาข้อมูลต่อไป

                        String number = eleClickableRow.select(".number_hide-2").text();
                        jsonDetail.put("squad_nember", number);                  //เบอร์เสื้อ

                        // ยกเว้น season 2015 เพราะไม่มีข้อมูล
                        if (!"2015".equals(season)) {
                            Document docLinkProfile = Jsoup.connect(linkProfile).timeout(60 * 1000).get();
                            Element elesDataPlayer = docLinkProfile.select(".data_played").first();
                            Elements elesFoot = elesDataPlayer.select(".foot");
                            Element eleImgFoot = elesFoot.select("a").first();
                            String imgData = eleImgFoot.attr("href");
                            String[] arrStr = imgData.split("/");
                            String plink = linkProfile + "/" + arrStr[1];

                            //performance-detail
                            boolean isGoalKeeper = false;
                            if ("ผู้รักษาประตู".equals(position)) {
                                isGoalKeeper = true;
                            }

                            Document docDataPlayedFull = Jsoup.connect(plink).timeout(60 * 1000).get();
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
                                for (int i = 0; i < (maxContent - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                                    jsonPlayedLeagueDetail = new JSONObject();
                                    Element eleContent = elesDataPlayedFull.select(".content").get(i);  //index แรกเริ่มจาก 0

                                    if (isGoalKeeper) {  // กรณีผู้รักษาประตู
                                        String subSeason = eleContent.select(".season").text();            //ฤดูกาล  
                                        String club = eleContent.select(".club").text();                //ทีมสโมสร

                                        //System.out.println(plink);
                                        jsonDetail.put("link_performance_detail", plink);
                                        club  = changeTeamThaiPremierLeague(season, team, club);
                                        if (season.equals(subSeason) && team.equals(club)) {  //เอาเฉพาะฤดูกาลนั้นๆ
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

                                            jsonDetail.put("performance_detail", jsonPlayedLeagueDetail);
                                        }
                                    } else {  //กรณีนักเตะผู้เล่นตำแหน่งอื่นๆ
                                        String subSeason = eleContent.select(".season").text();            //ฤดูกาล
                                        String club = eleContent.select(".club").text();                //ทีมสโมสร

                                        //System.out.println(plink);
                                        jsonDetail.put("link_performance_detail", plink);
                                        club  = changeTeamThaiPremierLeague(season, team, club);
                                        if (season.equals(subSeason) && team.equals(club)) {  //เอาเฉพาะฤดูกาลนั้นๆ                    
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

                                            jsonDetail.put("performance_detail", jsonPlayedLeagueDetail);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    arrDetailPlayers.put(jsonDetail);
                }

                json.put("players", arrDetailPlayers);
                //inputElasticsearch(json.toString(), "thaipremierleague_2019");
                System.out.println(json.toString());               
            }
        } catch (IOException | JSONException e) {
            System.out.println(e.getMessage());
        }
    } 
    public void testlistPlayerThaiPremierLeague(String url, String season, String detail,String team) {
        String newLink = url.replace("index.php", "");
        JSONObject json;
        JSONObject jsonDetail;
        try {
            
json = new JSONObject();
                // หา link เพื่อไปยังนักเตะทีมนั้นๆ
                Document docLinkTeam = Jsoup.connect(url).timeout(60 * 1000).get();
                Element elesReadAllBox = docLinkTeam.select(".read-all-box").first();
                Elements elesReadAllBoxA = elesReadAllBox.select("a");
                String hrefValue = elesReadAllBoxA.attr("href");
                String originalLink = url.replace("index.php", "");
                String linkPlayerOfTeam = originalLink + hrefValue;

                // list ข้อมูลนักเตะ  
                JSONArray arrDetailPlayers = new JSONArray();
                Document docListPlayer = Jsoup.connect(linkPlayerOfTeam).timeout(60 * 1000).get();
                //Elements elesDetailPlayer = docListPlayer.getElementsByClass("show-table");
                Elements elesDetailPlayer = docListPlayer.select(".show-table.sortable._margt");

                String elesTrHtml = elesDetailPlayer.select("tr").first().text();             //เลือก tr tag แรกสุด
                String[] arrOfStr = elesTrHtml.split(" ");
                int valueOfColumn = arrOfStr.length;   //นับจำนวนคอร์ลัม

                Element elesTr = elesDetailPlayer.select("tr").first();             //เลือก tr tag แรกสุด
                elesTr.remove();                                                    //ลบ tr tag แรกสุด
                Element elesNext = elesDetailPlayer.select("tr").first();           //เลือก tr tag ที่ 2
                elesNext.remove();                                                  //ลบ tr tag ที่ 2

                Elements elesClickableRow = elesDetailPlayer.select(".clickable-row");
                for (Element eleClickableRow : elesClickableRow) {
                    jsonDetail = new JSONObject();

                    String playerName = eleClickableRow.attr("title");
                    jsonDetail.put("player_name", playerName);                      //ชื่อนักเตะ   

                    Elements elesInjury = eleClickableRow.select(".injury_class");
                    if (!elesInjury.isEmpty()) {   //กรณีมีนักเตะที่ได้รับบาดเจ็บ
                        String injury = elesInjury.attr("title");
                        jsonDetail.put("injury", injury);      // อาการบาดเจ็บ             
                    }

                    Elements elesImgProfile = eleClickableRow.getElementsByClass("img");        //link image profile
                    if (elesImgProfile.hasClass("img")) {
                        Elements elesChild = elesImgProfile.select("*");
                        for (Element eleImgProfile : elesChild) {
                            if (eleImgProfile.tagName().equals("img")) {
                                Element eleImg = eleImgProfile.select("img").first();
                                String playerImg = eleImg.attr("src");
                                playerImg = baseLink + getNewLinkImage(playerImg);
                                jsonDetail.put("player_img", playerImg);           //รูปนักเตะ  
                            }
                        }
                    }
                    Elements eleNationality = eleClickableRow.select(".flag-icon");
                    String playerNationality = eleNationality.attr("title");
                    jsonDetail.put("player_nationality", playerNationality);              //สัญชาติ

                    String position = null;
                    String playerAge;
                    if (valueOfColumn == 5) {
                        position = eleClickableRow.getElementsByIndexEquals(3).text();
                        String[] arrPosition = position.split(" ");
                        int value = 0;
                        for (int i = 0; i < arrPosition.length; i++) {
                            value++;
                        }
                        position = arrPosition[value - 1];

                        playerAge = eleClickableRow.getElementsByIndexEquals(5).text();
                        String[] arrPlayerAge = playerAge.split(" ");
                        playerAge = arrPlayerAge[0];

                        jsonDetail.put("position", position);                    //ตำแหน่ง
                        jsonDetail.put("player_age", playerAge);

                    } else {
                        position = eleClickableRow.getElementsByIndexEquals(4).text();
                        String[] arrPosition = position.split(" ");
                        int value = 0;
                        for (int i = 0; i < arrPosition.length; i++) {
                            value++;
                        }
                        position = arrPosition[value - 1];

                        playerAge = eleClickableRow.getElementsByIndexEquals(6).text();
                        String[] arrPlayerAge = playerAge.split(" ");
                        playerAge = arrPlayerAge[0];

                        jsonDetail.put("position", position);                    //ตำแหน่ง
                        jsonDetail.put("player_age", playerAge);
                    }

                    String linkProfile = eleClickableRow.attr("data-href");
                    //ข้อมูลทีม season ปัจจุบัน
                    if ("present_teams".equals(detail)) {
                        linkProfile = baseLink + getNewLinkImage(linkProfile);
                        jsonDetail.put("link_player_profile", linkProfile);             //ลิ้งก์โปรไฟล์ เอาlink นี้ไปหาข้อมูลต่อไป

                        String number = eleClickableRow.select(".number_hide").text();
                        jsonDetail.put("squad_nember", number);                  //เบอร์เสื้อ

                        Document docLinkProfile = Jsoup.connect(linkProfile).timeout(60 * 1000).get();
                        Element elesDataPlayer = docLinkProfile.select(".data_played").first();
                        Elements elesFoot = elesDataPlayer.select(".foot");
                        Element eleImgFoot = elesFoot.select("a").first();
                        String imgData = eleImgFoot.attr("href");
                        String[] arrStr = imgData.split("/");
                        String plink = linkProfile + "/" + arrStr[1];

                        //performance-detail
                        boolean isGoalKeeper = false;
                        if ("ผู้รักษาประตู".equals(position)) {
                            isGoalKeeper = true;
                        }
                        //System.out.println(plink);
                        Document docDataPlayedFull = Jsoup.connect(plink).timeout(60 * 1000).get();
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
                            for (int i = 0; i < (maxContent - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                                jsonPlayedLeagueDetail = new JSONObject();
                                Element eleContent = elesDataPlayedFull.select(".content").get(i);  //index แรกเริ่มจาก 0

                                if (isGoalKeeper) {  // กรณีผู้รักษาประตู
                                    String subSeason = eleContent.select(".season").text();            //ฤดูกาล  
                                    String club = eleContent.select(".club").text();                //ทีมสโมสร

                                    //System.out.println(plink);
                                    jsonDetail.put("link_performance_detail", plink);
                                    String editTeam  = changeTeamThaiPremierLeague(season, team, club);                                    
                                    if (season.equals(subSeason) && editTeam.equals(club)) {  //เอาเฉพาะฤดูกาลนั้นๆ
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

                                        //arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                                        jsonDetail.put("performance_detail", jsonPlayedLeagueDetail);
                                        //System.out.println(jsonPlayedLeagueDetail.toString());
                                    }
                                } else {  //กรณีนักเตะผู้เล่นตำแหน่งอื่นๆ
                                    String subSeason = eleContent.select(".season").text();            //ฤดูกาล
                                    String club = eleContent.select(".club").text();                //ทีมสโมสร

                                    //System.out.println(plink);
                                    jsonDetail.put("link_performance_detail", plink);
                                    String editTeam  = changeTeamThaiPremierLeague(season, team, club);  
                                    if (season.equals(subSeason) && editTeam.equals(club)) {  //เอาเฉพาะฤดูกาลนั้นๆ                    
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

                                        jsonDetail.put("performance_detail", jsonPlayedLeagueDetail);
                                        //System.out.println(jsonPlayedLeagueDetail.toString());
                                    }
                                }
                            }
                        }
                    } else {  //ข้อมูลทีม season ในอดีต
                        String baseLinkProfile = linkPlayerOfTeam.replace("index.php", "");
                        linkProfile = baseLinkProfile + linkProfile;
                        jsonDetail.put("link_player_profile", linkProfile);             //ลิ้งก์โปรไฟล์ เอาlink นี้ไปหาข้อมูลต่อไป

                        String number = eleClickableRow.select(".number_hide-2").text();
                        jsonDetail.put("squad_nember", number);                  //เบอร์เสื้อ

                        // ยกเว้น season 2015 เพราะไม่มีข้อมูล
                        if (!"2015".equals(season)) {
                            Document docLinkProfile = Jsoup.connect(linkProfile).timeout(60 * 1000).get();
                            Element elesDataPlayer = docLinkProfile.select(".data_played").first();
                            Elements elesFoot = elesDataPlayer.select(".foot");
                            Element eleImgFoot = elesFoot.select("a").first();
                            String imgData = eleImgFoot.attr("href");
                            String[] arrStr = imgData.split("/");
                            String plink = linkProfile + "/" + arrStr[1];

                            //performance-detail
                            boolean isGoalKeeper = false;
                            if ("ผู้รักษาประตู".equals(position)) {
                                isGoalKeeper = true;
                            }

                            Document docDataPlayedFull = Jsoup.connect(plink).timeout(60 * 1000).get();
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
                                for (int i = 0; i < (maxContent - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                                    jsonPlayedLeagueDetail = new JSONObject();
                                    Element eleContent = elesDataPlayedFull.select(".content").get(i);  //index แรกเริ่มจาก 0

                                    if (isGoalKeeper) {  // กรณีผู้รักษาประตู
                                        String subSeason = eleContent.select(".season").text();            //ฤดูกาล  
                                        String club = eleContent.select(".club").text();                //ทีมสโมสร

                                        //System.out.println(plink);
                                        jsonDetail.put("link_performance_detail", plink);
                                        String editTeam  = changeTeamThaiPremierLeague(season, team, club);  
                                        if (season.equals(subSeason) && editTeam.equals(club)) {  //เอาเฉพาะฤดูกาลนั้นๆ
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

                                            jsonDetail.put("performance_detail", jsonPlayedLeagueDetail);
                                        }
                                    } else {  //กรณีนักเตะผู้เล่นตำแหน่งอื่นๆ
                                        String subSeason = eleContent.select(".season").text();            //ฤดูกาล
                                        String club = eleContent.select(".club").text();                //ทีมสโมสร

                                        //System.out.println(plink);
                                        jsonDetail.put("link_performance_detail", plink);
                                        String editTeam  = changeTeamThaiPremierLeague(season, team, club);  
                                        if (season.equals(subSeason) && editTeam.equals(club)) {  //เอาเฉพาะฤดูกาลนั้นๆ                    
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

                                            jsonDetail.put("performance_detail", jsonPlayedLeagueDetail);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    arrDetailPlayers.put(jsonDetail);
                }

                json.put("players", arrDetailPlayers);
                //inputElasticsearch(json.toString(), "thaipremierleague_2019");
                System.out.println(json.toString());               
           
        } catch (IOException | JSONException e) {
            System.out.println(e.getMessage());
        }
    } 
    public void testlistPlayerPremierLeague(String url, String season, String detail,String team) {
        String newLink = url.replace("index.php", "");
        JSONObject json;
        JSONObject jsonDetail;
        try {
                json = new JSONObject();
                // หา link เพื่อไปยังนักเตะทีมนั้นๆ
                Document docLinkTeam = Jsoup.connect(url).timeout(60 * 1000).get();

                if (team.isEmpty()) {
                    Elements elesTeam = docLinkTeam.select(".titleContent-half._margl._margr._margt");
                    String getTeam = elesTeam.select(".wrapcolor-2").text(); 
                    getTeam = getTeam.replace("ผลบอล ", "");
                    team = getTeam;
                }

                
                Element elesReadAllBox = docLinkTeam.select(".read-all-box").first();
                Elements elesReadAllBoxA = elesReadAllBox.select("a");
                String hrefValue = elesReadAllBoxA.attr("href");
                String originalLink = url.replace("index.php", "");
                String linkPlayerOfTeam = originalLink + hrefValue;

                // list ข้อมูลนักเตะ  
                JSONArray arrDetailPlayers = new JSONArray();
                Document docListPlayer = Jsoup.connect(linkPlayerOfTeam).timeout(60 * 1000).get();
                //Elements elesDetailPlayer = docListPlayer.getElementsByClass("show-table");
                Elements elesDetailPlayer = docListPlayer.select(".show-table.sortable._margt");

                String elesTrHtml = elesDetailPlayer.select("tr").first().text();             //เลือก tr tag แรกสุด
                String[] arrOfStr = elesTrHtml.split(" ");
                int valueOfColumn = arrOfStr.length;   //นับจำนวนคอร์ลัม

                Element elesTr = elesDetailPlayer.select("tr").first();             //เลือก tr tag แรกสุด
                elesTr.remove();                                                    //ลบ tr tag แรกสุด
                Element elesNext = elesDetailPlayer.select("tr").first();           //เลือก tr tag ที่ 2
                elesNext.remove();                                                  //ลบ tr tag ที่ 2

                Elements elesClickableRow = elesDetailPlayer.select(".clickable-row");
                for (Element eleClickableRow : elesClickableRow) {
                    jsonDetail = new JSONObject();

                    String playerName = eleClickableRow.attr("title");
                    jsonDetail.put("player_name", playerName);                      //ชื่อนักเตะ   

                    Elements elesInjury = eleClickableRow.select(".injury_class");
                    if (!elesInjury.isEmpty()) {   //กรณีมีนักเตะที่ได้รับบาดเจ็บ
                        String injury = elesInjury.attr("title");
                        jsonDetail.put("injury", injury);      // อาการบาดเจ็บ             
                    }

                    Elements elesImgProfile = eleClickableRow.getElementsByClass("img");        //link image profile
                    if (elesImgProfile.hasClass("img")) {
                        Elements elesChild = elesImgProfile.select("*");
                        for (Element eleImgProfile : elesChild) {
                            if (eleImgProfile.tagName().equals("img")) {
                                Element eleImg = eleImgProfile.select("img").first();
                                String playerImg = eleImg.attr("src");
                                playerImg = baseLink + getNewLinkImage(playerImg);
                                jsonDetail.put("player_img", playerImg);           //รูปนักเตะ  
                            }
                        }
                    }
                    Elements eleNationality = eleClickableRow.select(".flag-icon");
                    String playerNationality = eleNationality.attr("title");
                    jsonDetail.put("player_nationality", playerNationality);              //สัญชาติ

                    String position = null;
                    String playerAge;
                    if (valueOfColumn == 5) {
                        position = eleClickableRow.getElementsByIndexEquals(3).text();
                        String[] arrPosition = position.split(" ");
                        int value = 0;
                        for (int i = 0; i < arrPosition.length; i++) {
                            value++;
                        }
                        position = arrPosition[value - 1];

                        playerAge = eleClickableRow.getElementsByIndexEquals(5).text();
                        String[] arrPlayerAge = playerAge.split(" ");
                        playerAge = arrPlayerAge[0];

                        jsonDetail.put("position", position);                    //ตำแหน่ง
                        jsonDetail.put("player_age", playerAge);

                    } else {
                        position = eleClickableRow.getElementsByIndexEquals(4).text();
                        String[] arrPosition = position.split(" ");
                        int value = 0;
                        for (int i = 0; i < arrPosition.length; i++) {
                            value++;
                        }
                        position = arrPosition[value - 1];

                        playerAge = eleClickableRow.getElementsByIndexEquals(6).text();
                        String[] arrPlayerAge = playerAge.split(" ");
                        playerAge = arrPlayerAge[0];

                        jsonDetail.put("position", position);                    //ตำแหน่ง
                        jsonDetail.put("player_age", playerAge);
                    }

                    String linkProfile = eleClickableRow.attr("data-href");
                    //ข้อมูลทีม season ปัจจุบัน
                    if ("present_teams".equals(detail)) {
                        linkProfile = baseLink + getNewLinkImage(linkProfile);
                        jsonDetail.put("link_player_profile", linkProfile);             //ลิ้งก์โปรไฟล์ เอาlink นี้ไปหาข้อมูลต่อไป

                        String number = eleClickableRow.select(".number_hide").text();
                        jsonDetail.put("squad_nember", number);                  //เบอร์เสื้อ

                        Document docLinkProfile = Jsoup.connect(linkProfile).timeout(60 * 1000).get();
                        Element elesDataPlayer = docLinkProfile.select(".data_played").first();
                        Elements elesFoot = elesDataPlayer.select(".foot");
                        Element eleImgFoot = elesFoot.select("a").first();
                        String imgData = eleImgFoot.attr("href");
                        String[] arrStr = imgData.split("/");
                        String plink = linkProfile + "/" + arrStr[1];

                        //performance-detail
                        String inputEditSeason = season.replace("20", "");  // เฉพาะพรีเมียร์ลีกเท่านั้น

                        boolean isGoalKeeper = false;
                        if ("ผู้รักษาประตู".equals(position)) {
                            isGoalKeeper = true;
                        }
                        Document docDataPlayedFull = Jsoup.connect(plink).timeout(60 * 1000).get();
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
                            for (int i = 0; i < (maxContent - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                                jsonPlayedLeagueDetail = new JSONObject();
                                Element eleContent = elesDataPlayedFull.select(".content").get(i);  //index แรกเริ่มจาก 0

                                if (isGoalKeeper) {  // กรณีผู้รักษาประตู
                                    String subSeason = eleContent.select(".season").text();            //ฤดูกาล  
                                    String club = eleContent.select(".club").text();                //ทีมสโมสร

                                    //System.out.println(plink);
                                    jsonDetail.put("link_performance_detail", plink);
                                    String EditSubSeason = subSeason.replace("20", "");  // เฉพาะพรีเมียร์ลีกเท่านั้น
                                    String editTeam  = changeTeamPremierLeague(season, team, club);
                                    if (inputEditSeason.equals(EditSubSeason) && editTeam.equals(club)) {  //เอาเฉพาะฤดูกาลนั้นๆ
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

                                        //arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                                        jsonDetail.put("performance_detail", jsonPlayedLeagueDetail);
                                        //System.out.println(jsonPlayedLeagueDetail.toString());
                                    }
                                } else {  //กรณีนักเตะผู้เล่นตำแหน่งอื่นๆ
                                    String subSeason = eleContent.select(".season").text();            //ฤดูกาล
                                    String club = eleContent.select(".club").text();                //ทีมสโมสร

                                    //System.out.println(plink);
                                    jsonDetail.put("link_performance_detail", plink);
                                    String EditSubSeason = subSeason.replace("20", "");  // เฉพาะพรีเมียร์ลีกเท่านั้น
                                    String editTeam  = changeTeamPremierLeague(season, team, club);
                                    if (inputEditSeason.equals(EditSubSeason) && editTeam.equals(club)) {  //เอาเฉพาะฤดูกาลนั้นๆ                    
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

                                        jsonDetail.put("performance_detail", jsonPlayedLeagueDetail);
                                        //System.out.println(jsonPlayedLeagueDetail.toString());
                                    }
                                }
                            }
                        }
                    } else {  //ข้อมูลทีม season ในอดีต
                        String baseLinkProfile = linkPlayerOfTeam.replace("index.php", "");
                        linkProfile = baseLinkProfile + linkProfile;
                        jsonDetail.put("link_player_profile", linkProfile);             //ลิ้งก์โปรไฟล์ เอาlink นี้ไปหาข้อมูลต่อไป

                        String number = eleClickableRow.select(".number_hide-2").text();
                        jsonDetail.put("squad_nember", number);                  //เบอร์เสื้อ

                        Document docLinkProfile = Jsoup.connect(linkProfile).timeout(60 * 1000).get();
                        Element elesDataPlayer = docLinkProfile.select(".data_played").first();
                        Elements elesFoot = elesDataPlayer.select(".foot");
                        Element eleImgFoot = elesFoot.select("a").first();
                        String imgData = eleImgFoot.attr("href");
                        String[] arrStr = imgData.split("/");
                        String plink = linkProfile + "/" + arrStr[1];

                        //performance-detail
                        String inputEditSeason = season.replace("20", "");  // เฉพาะพรีเมียร์ลีกเท่านั้น
                        inputEditSeason = inputEditSeason.replace("-", "/");

                        boolean isGoalKeeper = false;
                        if ("ผู้รักษาประตู".equals(position)) {
                            isGoalKeeper = true;
                        }
                        Document docDataPlayedFull = Jsoup.connect(plink).timeout(60 * 1000).get();
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
                            for (int i = 0; i < (maxContent - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                                jsonPlayedLeagueDetail = new JSONObject();
                                Element eleContent = elesDataPlayedFull.select(".content").get(i);  //index แรกเริ่มจาก 0

                                if (isGoalKeeper) {  // กรณีผู้รักษาประตู
                                    String subSeason = eleContent.select(".season").text();            //ฤดูกาล  
                                    String club = eleContent.select(".club").text();                //ทีมสโมสร

                                    //System.out.println(plink);
                                    jsonDetail.put("link_performance_detail", plink);
                                    String EditSubSeason = subSeason.replace("20", "");  // เฉพาะพรีเมียร์ลีกเท่านั้น
                                    String editTeam  = changeTeamPremierLeague(season, team, club);
                                    if (inputEditSeason.equals(EditSubSeason) && editTeam.equals(club)) {  //เอาเฉพาะฤดูกาลนั้นๆ
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

                                        jsonDetail.put("performance_detail", jsonPlayedLeagueDetail);
                                    }
                                } else {  //กรณีนักเตะผู้เล่นตำแหน่งอื่นๆ
                                    String subSeason = eleContent.select(".season").text();            //ฤดูกาล
                                    String club = eleContent.select(".club").text();                //ทีมสโมสร

                                    //System.out.println(plink);
                                    jsonDetail.put("link_performance_detail", plink);
                                    String EditSubSeason = subSeason.replace("20", "");  // เฉพาะพรีเมียร์ลีกเท่านั้น
                                    String editTeam  = changeTeamPremierLeague(season, team, club);
                                    if (inputEditSeason.equals(EditSubSeason) && editTeam.equals(club)) {  //เอาเฉพาะฤดูกาลนั้นๆ                    
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

                                        jsonDetail.put("performance_detail", jsonPlayedLeagueDetail);
                                        //System.out.println(jsonPlayedLeagueDetail.toString());
                                    }
                                }
                            }
                        }
                    }
                    arrDetailPlayers.put(jsonDetail);
                }

                json.put("players", arrDetailPlayers);
                //inputElasticsearch(json.toString(), "premierleague_2019-20");
                System.out.println(json.toString());
            
        } catch (IOException | JSONException e) {
            System.out.println(e.getMessage());
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
            Logger.getLogger(GetTeam.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("insert finish");
    }  
    public String changeTeamThaiPremierLeague(String season, String team, String club){
        String newTeam = club;
        if("2019".equals(season)){
            if("ราชบุรี มิตรผล เอฟซี".equals(team)){
                newTeam = "ราชบุรี มิตรผล";           
            }
            if("สมุทรปราการ ซิตี้ เอฟซี".equals(team)){
                newTeam = "สมุทรปราการ ซิตี้"; 
            }
        }
        if("2018".equals(season)){
            if("ราชบุรี มิตรผล เอฟซี".equals(team)){
                newTeam = "ราชบุรี มิตรผล";           
            }
        }
        if("2017".equals(season)){
            if("โปลิศ เทโร เอฟซี".equals(team)){
                newTeam = "บีอีซี เทโรศาสน";           
            }
            if("ราชบุรี มิตรผล เอฟซี".equals(team)){
                newTeam = "ราชบุรี มิตรผล";           
            }
            if("สิงห์ เชียงราย ยูไนเต็ด".equals(team)){
                newTeam = "เชียงราย ยูไนเต็ด";           
            }
            if("ทรู แบงค็อก ยูไนเต็ด".equals(team)){
                newTeam = "แบงค็อก ยูไนเต็ด";           
            }
            if("ซุปเปอร์ พาวเวอร์ สมุทรปราการ เอฟซี".equals(team)){
                newTeam = "ซุปเปอร์ พาวเวอร์ สมุทรปราการ";           
            }
            if("ไทยฮอนด้า ลาดกระบัง เอฟซี".equals(team)){
                newTeam = "ไทยฮอนด้า เอฟซี";           
            }             
        }
        if("2016".equals(season)){
            if("ทรู แบงค็อก ยูไนเต็ด".equals(team)){
                newTeam = "แบงค็อก ยูไนเต็ด";           
            }
            if("บีบีซียู เอฟซี".equals(team)){
                newTeam = "บีบีซียู";           
            }
            if("สิงห์ เชียงราย ยูไนเต็ด".equals(team)){
                newTeam = "เชียงราย ยูไนเต็ด";           
            }
            if("ซุปเปอร์ พาวเวอร์ สมุทรปราการ เอฟซี".equals(team)){
                newTeam = "ซุปเปอร์ พาวเวอร์ สมุทรปราการ";           
            }
            if("พัทยา ยูไนเต็ด".equals(team)){
                newTeam = "พัทยา เอ็นเอ็นเค ยูไนเต็ด";           
            }
            if("ราชบุรี มิตรผล เอฟซี".equals(team)){
                newTeam = "ราชบุรี มิตรผล";           
            }             
        }
        return newTeam;
    }  
   
    public String changeTeamPremierLeague(String season, String team, String club){
        String newTeam = team;
        if("2019-20".equals(season)){
            if("วูล์ฟแฮมป์ตัน วันเดอร์เรอร์ส".equals(team)){
                newTeam = "วูล์ฟแฮมป์ตัน";           
            }
        }
        if("2018-19".equals(season)){
            if("ฟูแล่ม เอฟซี".equals(team)){
                newTeam = "ฟูแล่ม";           
            }
            if("วูล์ฟแฮมป์ตัน วันเดอร์เรอร์ส".equals(team)){
                newTeam = "วูล์ฟแฮมป์ตัน";           
            }            
        }
        return newTeam;
    }     
    public static void main(String[] args){
        
       GetTeam t = new GetTeam();
       
       //present_teams  //ปัจจุบัน
       //past_teams
       //String url = "http://www.livesoccer888.com/thaipremierleague/2016/teams/Ratchaburi-FC/index.php";
      // t.testlistPlayerThaiPremierLeague(url,"2016","past_teams","ราชบุรี มิตรผล เอฟซี");

       String url = "http://www.livesoccer888.com/premierleague/2015-16/teams/index.php";
       t.listPlayerPremierLeague(url,"2015-16","past_teams");
       
       
       //String url = "http://www.livesoccer888.com/premierleague/2015-16/teams/Arsenal/index.php";
       //t.testlistPlayerPremierLeague(url,"2015-16","past_teams","อาร์เซนอล");
    }

        
}
