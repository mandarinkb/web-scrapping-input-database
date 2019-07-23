package app.service;

import app.function.DateTimes;
import app.function.Elasticsearch;
import app.function.OtherFunc;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceStatsImp implements ServiceStats {
    @Autowired
    private DateTimes dateTimes;

    @Autowired
    private OtherFunc func;

    @Autowired
    private Elasticsearch els;

    @Override
    public void statsOfTeam(String url, String league) {
        String baseLink = "http://www.livesoccer888.com";
        try {
            JSONObject json;
            String BaseLinkPalyer = null;
            if ("thaipremierleague".equals(league)) {
                BaseLinkPalyer = "http://www.livesoccer888.com/thaipremierleague";
            }
            if ("premierleague".equals(league)) {
                BaseLinkPalyer = "http://www.livesoccer888.com/premierleague";
            }

            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Elements elements = doc.getElementsByClass("PlayerLeague");
            for (Element ele : elements) {
                json = new JSONObject();
                json.put("link", url);
                Elements elesLogo = ele.getElementsByClass("logo");
                if (elesLogo.hasClass("logo")) {
                    Elements elesChild = elesLogo.select("*");
                    for (Element eleLogo : elesChild) {
                        if (eleLogo.tagName().equals("img")) {
                            Element eleImg = eleLogo.select("img").first();
                            String img = eleImg.attr("src");
                            img = baseLink + func.getNewLinkImage(img);
                            json.put("logo_team", img);                         //โลโก้ทีม
                        }
                    }
                }
                String team = ele.getElementsByClass("team").text();
                json.put("team", team);                                         //ทีม                       

                Elements elesUl = ele.select("ul");
                for (Element eleChildren : elesUl) {
                    Elements elesLi = eleChildren.select("li");
                    Elements elesChildren = elesLi.select("*");
                    for (Element eleLiAll : elesChildren) {
                        if (eleLiAll.hasClass("player")) {
                            String player = eleLiAll.select(".player").text();  //จำนวนนักเตะ
                            player = func.statsOfTeamEnKey(player);
                            String point = eleLiAll.nextElementSibling().text();//ค่าจำนวนนักเตะ                       
                            json.put(player, point);                            //จำนวนนักเตะ
                        }
                        if (eleLiAll.hasClass("goal")) {
                            String goal = eleLiAll.select(".goal").text();      //ทำประตู
                            goal = func.statsOfTeamEnKey(goal);
                            String point = eleLiAll.nextElementSibling().text();//จำนวนทำประตู
                            json.put(goal, point);                              //ทำประตู
                        }
                        if (eleLiAll.hasClass("assist")) {
                            String assist = eleLiAll.select(".assist").text();  //ทำแอสซิสต์
                            assist = func.statsOfTeamEnKey(assist);
                            String point = eleLiAll.nextElementSibling().text();//จำนวนทำแอสซิสต์
                            json.put(assist, point);                            //ทำแอสซิสต์
                        }
                        if (eleLiAll.hasClass("shutout")) {
                            String shutout = eleLiAll.select(".shutout").text();//คลีนชีท
                            shutout = func.statsOfTeamEnKey(shutout);
                            String point = eleLiAll.nextElementSibling().text();//จำนวนคลีนชีท
                            json.put(shutout, point);                           //คลีนชีท
                        }
                        if (eleLiAll.hasClass("yellow")) {
                            String yellow = eleLiAll.select(".yellow").text();  //ใบเหลือง
                            yellow = func.statsOfTeamEnKey(yellow);
                            String point = eleLiAll.nextElementSibling().text();//จำนวนใบเหลือง
                            json.put(yellow, point);                            //ใบเหลือง

                        }
                        if (eleLiAll.hasClass("yellowred")) {
                            String yellowred = eleLiAll.select(".yellowred").text();  //ใบเหลืองแดง
                            yellowred = func.statsOfTeamEnKey(yellowred);
                            String point = eleLiAll.nextElementSibling().text();      //จำนวนใบเหลืองแดง
                            json.put(yellowred, point);                               //ใบเหลืองแดง

                        }
                        if (eleLiAll.hasClass("red")) {
                            String red = eleLiAll.select(".red").text();        //ใบแดง
                            red = func.statsOfTeamEnKey(red);
                            String point = eleLiAll.nextElementSibling().text();//จำนวนใบแดง 
                            json.put(red, point);                               //ใบแดง

                        }
                        if (eleLiAll.tagName().equals("a")) {                   //ดูนักเตะทั้งหมด
                            Element eleA = eleLiAll.select("a").first();
                            String linkPlayers = eleA.attr("href");
                            linkPlayers = func.getNewLinkImage(linkPlayers);
                            linkPlayers = BaseLinkPalyer + linkPlayers;
                            json.put("link_detail", linkPlayers);               //ลิ้งก์ดูนักเตะทั้งหมด
                        }
                    }
                    if ("thaipremierleague".equals(league)) {
                        els.inputElasticsearch(json.toString(), "stats_of_team_thaipremierleague");
                        System.out.println(dateTimes.thaiDateTime()+" : insert stats of team thaipremierleague complete");
                    }
                    if ("premierleague".equals(league)) {
                        els.inputElasticsearch(json.toString(), "stats_of_team_premierleague");
                        System.out.println(dateTimes.thaiDateTime()+" : insert stats of team premierleague complete");
                    }
                }
            }
        } catch (IOException | JSONException e) {
            e.getMessage();
        }
    }

    @Override
    public void teamDetail(String url, String league) {
        String baseLink = "http://www.livesoccer888.com";
        try {
            JSONObject json = new JSONObject();
            JSONObject jsonDetail;
            JSONArray arr;

            json.put("link", url);                                              //ลิ้งก์
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Elements elesLogo = doc.getElementsByClass("logo_team_player");
            Element eleImgTeam = elesLogo.select("img").get(0);
            String imgTeam = eleImgTeam.attr("src");
            imgTeam = baseLink + func.getNewLinkImage(imgTeam);
            json.put("logo_team", imgTeam);                                     //โลโกทีม

            String team = eleImgTeam.attr("alt");
            team = team.replace("สโมสรฟุตบอลทีม", "");
            json.put("team", team);                                             //ทีม

            String title = "";
            Elements elements = doc.getElementsByClass("data_statistic_played");
            for (Element ele : elements) {
                arr = new JSONArray();
                jsonDetail = new JSONObject();
                // top_player class
                //ผู้เล่นคนแรก
                Elements elesTopPly = ele.getElementsByClass("top_player");
                if (elesTopPly.hasClass("top_player")) {
                    Elements elesBgGoal = elesTopPly.select(".title.bg_goal");

                    // ทำประตูสูงสุด
                    if (elesBgGoal.hasClass("bg_goal")) {
                        String goal = elesTopPly.select(".title.bg_goal").text();  
                        title = goal;                                           //ทำประตูสูงสุด
                    }
                    Elements elesImgPlayer = elesTopPly.select(".image.ts");
                    if (elesImgPlayer.hasClass("ts")) {
                        Element eleImg = elesImgPlayer.select("img").first();     
                        String img = eleImg.attr("src");
                        img = baseLink + func.getNewLinkImage(img);
                        jsonDetail.put("img_player", img);                      //รูปนักเตะ

                        String goals = elesTopPly.select(".stats.stat_goal.goals").text();  
                        jsonDetail.put("goals", goals);                         //จำนวนประตู

                        String name = elesTopPly.select(".name").text();     
                        jsonDetail.put("name", name);                           //ชื่อนักเตะ

                        String ranking = elesTopPly.select(".ranking").text();  
                        jsonDetail.put("ranking", ranking);                     //อันดับ
                    }

                    //ทำแอสซิสต์สุงสุด
                    Elements elesBgAssist = elesTopPly.select(".title.bg_assist");
                    if (elesBgAssist.hasClass("bg_assist")) {
                        String assist = elesTopPly.select(".title.bg_assist").text();
                        title = assist;                                         //ทำแอสซิสต์สุงสุด
                    }
                    Elements elesImgPlayerAssist = elesTopPly.select(".image.ta");
                    if (elesImgPlayerAssist.hasClass("ta")) {
                        Element eleImg = elesImgPlayerAssist.select("img").first();
                        String img = eleImg.attr("src");
                        img = baseLink + func.getNewLinkImage(img);
                        jsonDetail.put("img_player", img);                      //รูปนักเตะ

                        String assist = elesTopPly.select(".stats.stat_assist.assists").text();
                        jsonDetail.put("assists", assist);                      //ทำแอสซิสต์

                        String name = elesTopPly.select(".name").text();
                        jsonDetail.put("name", name);                           //ชื่อนักเตะ

                        String ranking = elesTopPly.select(".ranking").text();
                        jsonDetail.put("ranking", ranking);                     //อันดับ              
                    }

                    //ลงเล่นมากที่สุด
                    Elements elesBgApp = elesTopPly.select(".title.bg_apps");
                    if (elesBgApp.hasClass("bg_apps")) {
                        String app = elesTopPly.select(".title.bg_apps").text();
                        title = app;                                            //ลงเล่นมากที่สุด
                    }
                    Elements elesImgPlayerApp = elesTopPly.select(".image.tt");
                    if (elesImgPlayerApp.hasClass("tt")) {
                        Element eleImg = elesImgPlayerApp.select("img").first();
                        String img = eleImg.attr("src");
                        img = baseLink + func.getNewLinkImage(img);
                        jsonDetail.put("img_player", img);                      //รูปนักเตะ   

                        String mostPlay = elesTopPly.select(".stats.stat_apps.apps").text();
                        jsonDetail.put("minutes_played", mostPlay);             //ลงเล่น(นาที)

                        String name = elesTopPly.select(".name").text();
                        jsonDetail.put("name", name);                           //ชื่อนักเตะ

                        String ranking = elesTopPly.select(".ranking").text();
                        jsonDetail.put("ranking", ranking);                     //อันดับ 
                    }
                    arr.put(jsonDetail);
                }

                // ผู้เล่นคนถัดไป
                // next_player class            
                Elements elesNextPlayer = ele.select(".next_player");
                for (Element eleNextPlayer : elesNextPlayer) {
                    jsonDetail = new JSONObject();
                    String ranking = eleNextPlayer.select(".ranking").text();  
                    jsonDetail.put("ranking", ranking);                         //อันดับ

                    //ทำประตูสุงสุด
                    Elements elesStatusTs = eleNextPlayer.select(".stats.ts");
                    if (elesStatusTs.hasClass("ts")) {
                        String status = eleNextPlayer.select(".stats.ts").text();  
                        jsonDetail.put("goals", status);                        //จำนวนประตู
                    }

                    //ทำแอสซิสต์สุงสุด
                    Elements elesStatusTa = eleNextPlayer.select(".stats.ta");
                    if (elesStatusTa.hasClass("ta")) {
                        String status = eleNextPlayer.select(".stats.ta").text();
                        jsonDetail.put("assists", status);                      //ทำแอสซิสต์
                    }

                    //ลงเล่นมากที่สุด
                    Elements elesStatusTt = eleNextPlayer.select(".stats.tt.double_width");
                    if (elesStatusTt.hasClass("tt")) {
                        String status = eleNextPlayer.select(".stats.tt.double_width").text();
                        jsonDetail.put("minutes_played", status);               //ลงเล่น(นาที)
                    }

                    Elements elesImgPlayer = eleNextPlayer.select(".image");
                    Element eleImg = elesImgPlayer.select("img").first();     
                    String img = eleImg.attr("src");
                    img = baseLink + func.getNewLinkImage(img);
                    jsonDetail.put("img_player", img);                          //รูปนักเตะ

                    String name = eleNextPlayer.select(".name").text();  
                    jsonDetail.put("name", name);                               //ชื่อนักเตะ

                    arr.put(jsonDetail);
                }
                title = func.titleToEnKey(title);
                json.put(title, arr);
            }

            // รายชื่อนักเตะ
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
                linkProfile = baseLink + func.getNewLinkImage(linkProfile);
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
                            img = baseLink + func.getNewLinkImage(img);
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
            json.put("players_detail", arrDetailPlayers);

            if ("thaipremierleague".equals(league)) {
                els.inputElasticsearch(json.toString(), "team_detail_thaipremierleague");
                System.out.println(dateTimes.thaiDateTime()+" : insert team detail thaipremierleague complete");
            }
            if ("premierleague".equals(league)) {
                els.inputElasticsearch(json.toString(), "team_detail_premierleague");
                System.out.println(dateTimes.thaiDateTime()+" : insert team detail premierleague complete");
            }
        } catch (IOException | JSONException e) {
            e.getMessage();
        }

    }

    @Override
    public void thaipremierleaguePlayerProfile(String url) {
        String baseLink = "http://www.livesoccer888.com";
        try {
            JSONObject json = new JSONObject();
            json.put("link", url);                                              //ลิ้งก์
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();

            Elements elesLogo = doc.getElementsByClass("logo_team_player");
            Element eleImgTeam = elesLogo.select("img").get(0);
            String imgTeam = eleImgTeam.attr("src");
            imgTeam = baseLink + func.getNewLinkImage(imgTeam);
            json.put("logo_team", imgTeam);                                     //โลโก้ทีม

            String team = eleImgTeam.attr("alt");
            team = team.replace("สโมสรฟุตบอลทีม", "");
            json.put("team", team);                                             //ทีม 

            Elements elements = doc.getElementsByClass("data_profile");
            Element eleImgProfile = elements.select("img").get(0);
            String imgProfile = eleImgProfile.attr("src");
            imgProfile = baseLink + func.getNewLinkImage(imgProfile);
            json.put("img_profile", imgProfile);                                //รูปนักเตะ

            Elements eles = elements.select(".content");
            Elements ps = eles.select("p");
            int pTag = -1;
            for (Element p : ps) {
                if (p.tagName().equals("p")) {
                    pTag++;
                }
            }
            //กรณีปกติ
            String engName = eles.select("span").get(0).text();
            json.put("en_name", engName);                                       //ชื่อภาษาอังกฤษ

            String thName = eles.select("span").get(1).text();
            json.put("th_name", thName);                                        //ชื่อภาษาไทย

            String netWorth = eles.select("p").get(pTag).text();
            //netWorth = netWorth.replace("ค่าตัวโดยประมาณ ", "");
            //netWorth = netWorth.replace(" ปอนด์", "");
            json.put("net_worth", netWorth);                                    //ค่าตัว

            Elements elesUl = elements.select("ul");
            Elements elesUlChild = elesUl.select("*");
            int count = 0;
            String value = "";
            String key = "";
            boolean isGoalKeeper = false;                       //ผู้รักษาประตู
            for (Element ele : elesUlChild) {
                if (count > 0) {                                //ไม่เอาค่าแรก
                    if (ele.tagName().equals("li")) {
                        value = ele.select("li").text();        //ค่าที่ต้องการ
                    }
                    if (ele.tagName().equals("b")) {            //เลือกหัวข้อของค่าที่ต้องการ
                        key = ele.select("b").text();
                        key = key.replace(" :", "");            //ลบ _: ออก
                        String keyEn = func.detailPlayerToEnKey(key);
                        value = value.replace(key, "");
                        if (!value.isEmpty()) {                 //เลือกเอาเฉพาะที่มีค่า
                            String firstChar = value.substring(0, 1);  //ตัดเอาตัวอักษรตัวแรก
                            if (!firstChar.isEmpty()) {
                                //value = value.substring(1);          //ลบตัวอักษรตัวแรกออก
                                value = value.replace(" : ", "");      //ลบ _:_ ออก
                                json.put(keyEn, value);
                                
                                if ("ผู้รักษาประตู".equals(value)) {
                                    isGoalKeeper = true;
                                }
                            }
                        }
                    }
                }
                count++;
            }
            // กรณือื่นๆเช่นเป็นกัปตันทีม  หรือ บาดเจ็บ
            Elements elesCaptain = elements.select(".captain");
            if (elesCaptain.hasClass("captain")) {
                String captain = elesCaptain.select(".captain").get(0).text();
                json.put("captain", captain);                                   //กัปตัน
            }
            Elements elesInjury = elements.select(".injury");
            if (elesInjury.hasClass("injury")) {
                String injury = elesInjury.select(".injury").get(0).text();
                json.put("injury", injury);                                     //อาการบาดเจ็บ
            }

            JSONObject jsonDetailTransfer;
            JSONArray arrTransfer = new JSONArray();
            //ข้อมูลประวัติการย้ายสโมสร
            Elements elementsContent = doc.select(".content.main-content.left-content");
            Elements elesDataTransfer = elementsContent.select(".data_transfer");
            Elements elesContent = elesDataTransfer.select(".content");
            for (Element ele : elesContent) {
                jsonDetailTransfer = new JSONObject();
                String season = ele.select(".season").text();                   
                jsonDetailTransfer.put("season_transfer", season);              //ฤดูกาล

                String date = ele.select(".date").text();           
                jsonDetailTransfer.put("date_transfer", date);                  //วันที่

                String movefrom = ele.select(".movefrom").text();   
                jsonDetailTransfer.put("movefrom", movefrom);                   //ย้ายออกจาก

                String moveto = ele.select(".moveto").text();       
                jsonDetailTransfer.put("moveto", moveto);                       //ย้ายเข้ามา

                String transfer = ele.select(".transfer").text();   
                jsonDetailTransfer.put("transfer", transfer);                   //การซื้อขาย

                arrTransfer.put(jsonDetailTransfer);
            }
            json.put("transfer_detail", arrTransfer);
            //จบข้อมูลประวัติการย้ายสโมสร

            //กรณีผู้รักษาประตู 
            JSONObject jsonPlayedLeagueDetail;
            JSONArray arrPlayedLeagueDetail = new JSONArray();
            if (isGoalKeeper) {
                Elements elesDataPlayer = elementsContent.select(".data_played");
                Elements elesFoot = elesDataPlayer.select(".foot");
                for (Element ele : elesFoot) {
                    Element eleImg = ele.select("a").first();
                    String img = eleImg.attr("href");
                    String[] arrStr = img.split("/");
                    //ข้อมูลการลงเล่นฟุตบอลลีกและถ้วยต่างๆ
                    if ("performance-detail".equals(arrStr[1])) {
                        String performanceDetail = url + "/" + arrStr[1];
                        Document docpd = Jsoup.connect(performanceDetail).timeout(60 * 1000).get();
                        Elements data = docpd.select(".data_played-full");
                        int countData = 0;
                        for (Element eleData : data) {  // นับจำนวน class data_played-full
                            if (eleData.hasClass("data_played-full")) {
                                countData++;
                            }
                        }
                        if (countData == 1) {  // มีตารางเดียว (ข้อมูลการลงเล่นฟุตบอลลีก)
                            Element elesTable1 = docpd.select(".data_played-full").first();
                            Elements elesContent1 = elesTable1.select(".content");
                            int maxContent = 0;
                            for (Element eleDataContent : elesContent1) {  // นับจำนวน class content 
                                if (eleDataContent.hasClass("content")) {
                                    maxContent++;
                                }
                            }
                            for (int i = 0; i < (maxContent - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                                jsonPlayedLeagueDetail = new JSONObject();
                                Element eleContent1 = elesTable1.select(".content").get(i);  //index แรกเริ่มจาก 0
                                String season = eleContent1.select(".season").text();  
                                jsonPlayedLeagueDetail.put("season", season);                                    //ฤดูกาล

                                String competition = eleContent1.select(".competition").text();  
                                jsonPlayedLeagueDetail.put("competition", competition);                          //รายการแข่งขัน

                                String club = eleContent1.select(".club").text();  
                                jsonPlayedLeagueDetail.put("club", club);                                        //ทีมสโมสร

                                String matches = eleContent1.select(".matches.league_club_keeper").text();  
                                jsonPlayedLeagueDetail.put("matches", matches);                                  //ลงเล่น(แมตซ์)

                                String goals = eleContent1.select(".data.league_club_keeper").get(0).text();  
                                jsonPlayedLeagueDetail.put("goals", goals);                                      //ทำประตู

                                String own_goals = eleContent1.select(".data.league_club_keeper").get(1).text();  
                                jsonPlayedLeagueDetail.put("own_goals", own_goals);                              //ทำเข้าประตูตนเอง
 
                                String substituted_on = eleContent1.select(".data.league_club_keeper").get(2).text();  
                                jsonPlayedLeagueDetail.put("substituted_on", substituted_on);                    //เปลี่ยนตัวเข้า

                                String substituted_off = eleContent1.select(".data.league_club_keeper").get(3).text();  
                                jsonPlayedLeagueDetail.put("substituted_off", substituted_off);                  //เปลี่ยนตัวออก

                                String yellow = eleContent1.select(".data.league_club_keeper").get(4).text();  
                                jsonPlayedLeagueDetail.put("yellow_cards", yellow);                              //ใบเหลือง

                                String yellowred = eleContent1.select(".data.league_club_keeper").get(5).text();  
                                jsonPlayedLeagueDetail.put("yellow_red_cards", yellowred);                       //ใบเหลือง/ใบเเดง

                                String red = eleContent1.select(".data.league_club_keeper").get(6).text();  
                                jsonPlayedLeagueDetail.put("red_cards", red);                                    //ใบแดง

                                String conceded = eleContent1.select(".data.league_club_keeper").get(7).text();  
                                jsonPlayedLeagueDetail.put("goals_conceded", conceded);                          //เสียประตู

                                String shutout = eleContent1.select(".data.league_club_keeper").get(8).text();  
                                jsonPlayedLeagueDetail.put("clean_sheets", shutout);                             //คลีนชีท

                                String time = eleContent1.select(".time").text();  
                                jsonPlayedLeagueDetail.put("minutes_played", time);                              //ลงเล่น (เวลา:นาที)

                                arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            }
                            // class content สุดท้าย
                            jsonPlayedLeagueDetail = new JSONObject();
                            Element elesContent1Last = elesTable1.select(".content").last();
                            String sumMatches = elesContent1Last.select(".matches.league_club_keeper.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_matches", sumMatches);                                               //ลงเล่น(แมตซ์)

                            String sum_goals = elesContent1Last.select(".data.league_club_keeper.sum").get(0).text();  
                            jsonPlayedLeagueDetail.put("sum_goals", sum_goals);                                                  //ทำประตู

                            String sum_own_goals = elesContent1Last.select(".data.league_club_keeper.sum").get(1).text();  
                            jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);                                          //ทำเข้าประตูตนเอง

                            String sum_substituted_on = elesContent1Last.select(".data.league_club_keeper.sum").get(2).text();  
                            jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);                                //เปลี่ยนตัวเข้า

                            String sum_substituted_off = elesContent1Last.select(".data.league_club_keeper.sum").get(3).text();  
                            jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);                              //เปลี่ยนตัวออก

                            String sum_yellow = elesContent1Last.select(".data.league_club_keeper.sum").get(4).text();  
                            jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);                                          //ใบเหลือง

                            String sum_yellow_red = elesContent1Last.select(".data.league_club_keeper.sum").get(5).text();  
                            jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);                                  //ใบเหลือง/ใบเเดง

                            String sum_red = elesContent1Last.select(".data.league_club_keeper.sum").get(6).text();  
                            jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);                                                //ใบแดง

                            String sum_conceded = elesContent1Last.select(".data.league_club_keeper.sum").get(7).text();  
                            jsonPlayedLeagueDetail.put("sum_goals_conceded", sum_conceded);                                      //เสียประตู

                            String sum_shutout = elesContent1Last.select(".data.league_club_keeper.sum").get(8).text();  
                            jsonPlayedLeagueDetail.put("sum_clean_sheets", sum_shutout);                                         //คลีนชีท

                            String sum_time = elesContent1Last.select(".time.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);                                          //ลงเล่น (เวลา:นาที)

                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            json.put("played_league_detail", arrPlayedLeagueDetail);
                        }
                        if (countData == 2) {  // มี 2 ตาราง
                            arrPlayedLeagueDetail = new JSONArray();
                            Element elesTable1 = docpd.select(".data_played-full").first();
                            Elements elesContent1 = elesTable1.select(".content");
                            int maxContent = 0;
                            for (Element eleDataContent : elesContent1) {  // นับจำนวน class content 
                                if (eleDataContent.hasClass("content")) {
                                    maxContent++;
                                }
                            }

                            for (int i = 0; i < (maxContent - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                                jsonPlayedLeagueDetail = new JSONObject();
                                Element eleContent1 = elesTable1.select(".content").get(i);  //index แรกเริ่มจาก 0
                                String season = eleContent1.select(".season").text();  
                                jsonPlayedLeagueDetail.put("season", season);                                                    //ฤดูกาล

                                String competition = eleContent1.select(".competition").text();  
                                jsonPlayedLeagueDetail.put("competition", competition);                                         //รายการแข่งขัน 

                                String club = eleContent1.select(".club").text();  
                                jsonPlayedLeagueDetail.put("club", club);                                                       //ทีมสโมสร

                                String matches = eleContent1.select(".matches.league_club_keeper").text();  
                                jsonPlayedLeagueDetail.put("matches", matches);                                                 //ลงเล่น(แมตซ์)

                                String goals = eleContent1.select(".data.league_club_keeper").get(0).text();  
                                jsonPlayedLeagueDetail.put("goals", goals);                                                     //ทำประตู 

                                String own_goals = eleContent1.select(".data.league_club_keeper").get(1).text();  
                                jsonPlayedLeagueDetail.put("own_goals", own_goals);                                             //ทำเข้าประตูตนเอง

                                String substituted_on = eleContent1.select(".data.league_club_keeper").get(2).text();  
                                jsonPlayedLeagueDetail.put("substituted_on", substituted_on);                                   //เปลี่ยนตัวเข้า

                                String substituted_off = eleContent1.select(".data.league_club_keeper").get(3).text();  
                                jsonPlayedLeagueDetail.put("substituted_off", substituted_off);                                 //เปลี่ยนตัวออก

                                String yellow = eleContent1.select(".data.league_club_keeper").get(4).text();  
                                jsonPlayedLeagueDetail.put("yellow_cards", yellow);                                             //ใบเหลือง

                                String yellow_red = eleContent1.select(".data.league_club_keeper").get(5).text();  
                                jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);                                     //ใบเหลือง/ใบเเดง

                                String red = eleContent1.select(".data.league_club_keeper").get(6).text();  
                                jsonPlayedLeagueDetail.put("red_cards", red);                                                   //ใบแดง

                                String conceded = eleContent1.select(".data.league_club_keeper").get(7).text();  
                                jsonPlayedLeagueDetail.put("goals_conceded", conceded);                                         //เสียประตู

                                String shutout = eleContent1.select(".data.league_club_keeper").get(8).text();  
                                jsonPlayedLeagueDetail.put("clean_sheets", shutout);                                            //คลีนชีท

                                String time = eleContent1.select(".time").text();  
                                jsonPlayedLeagueDetail.put("minutes_played", time);                                             //ลงเล่น (เวลา:นาที)

                                arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            }
                            // class content สุดท้าย
                            //json = new JSONObject();
                            jsonPlayedLeagueDetail = new JSONObject();
                            Element elesContent1Last = elesTable1.select(".content").last();
                            String sum_matches = elesContent1Last.select(".matches.league_club_keeper.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_matches", sum_matches);                                              //ลงเล่น(แมตซ์)

                            String sum_goals = elesContent1Last.select(".data.league_club_keeper.sum").get(0).text();  
                            jsonPlayedLeagueDetail.put("sum_goals", sum_goals);                                                  //ทำประตู

                            String sum_own_goals = elesContent1Last.select(".data.league_club_keeper.sum").get(1).text();  
                            jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);                                          //ทำเข้าประตูตนเอง

                            String sum_substituted_on = elesContent1Last.select(".data.league_club_keeper.sum").get(2).text();   
                            jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);                                //เปลี่ยนตัวเข้า

                            String sum_substituted_off = elesContent1Last.select(".data.league_club_keeper.sum").get(3).text();  
                            jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);                              //เปลี่ยนตัวออก

                            String sum_yellow = elesContent1Last.select(".data.league_club_keeper.sum").get(4).text();  
                            jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);                                          //ใบเหลือง

                            String sum_yellow_red = elesContent1Last.select(".data.league_club_keeper.sum").get(5).text();  
                            jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);                                  //ใบเหลือง/ใบเเดง

                            String sum_red = elesContent1Last.select(".data.league_club_keeper.sum").get(6).text();  
                            jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);                                                //ใบแดง

                            String sum_conceded = elesContent1Last.select(".data.league_club_keeper.sum").get(7).text();  
                            jsonPlayedLeagueDetail.put("sum_goals_conceded", sum_conceded);                                      //เสียประตู

                            String sum_shutout = elesContent1Last.select(".data.league_club_keeper.sum").get(8).text();  
                            jsonPlayedLeagueDetail.put("sum_clean_sheets", sum_shutout);                                         //คลีนชีท

                            String sum_time = elesContent1Last.select(".time.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);                                          //ลงเล่น (เวลา:นาที)

                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            json.put("played_league_detail", arrPlayedLeagueDetail);

                            //ข้อมูลการลงเล่นฟุตบอลถ้วยระดับนานาชาติ
                            arrPlayedLeagueDetail = new JSONArray();
                            Element elesTable2 = docpd.select(".data_played-full").last();
                            Elements elesContent2 = elesTable2.select(".content");
                            int maxContent2 = 0;
                            for (Element eleDataContent : elesContent2) {  // นับจำนวน class content 
                                if (eleDataContent.hasClass("content")) {
                                    maxContent2++;
                                }
                            }
                            //JSONObject json2;
                            for (int i = 0; i < (maxContent2 - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                                jsonPlayedLeagueDetail = new JSONObject();

                                Element eleContent1 = elesTable2.select(".content").get(i);  //index แรกเริ่มจาก 0
                                String season = eleContent1.select(".season").text();  
                                jsonPlayedLeagueDetail.put("season", season);                                                  //ฤดูกาล

                                String competition = eleContent1.select(".competition").text();  
                                jsonPlayedLeagueDetail.put("competition", competition);                                        //รายการแข่งขัน

                                String club = eleContent1.select(".club").text();  
                                jsonPlayedLeagueDetail.put("club", club);                                                      //ทีมสโมสร

                                String matches = eleContent1.select(".matches.league_club_keeper").text();  
                                jsonPlayedLeagueDetail.put("matches", matches);                                                //ลงเล่น(แมตซ์)

                                String goals = eleContent1.select(".data.league_club_keeper").get(0).text();  
                                jsonPlayedLeagueDetail.put("goals", goals);                                                    //ทำประตู

                                String own_goals = eleContent1.select(".data.league_club_keeper").get(1).text();  
                                jsonPlayedLeagueDetail.put("own_goals", own_goals);                                            //ทำเข้าประตูตนเอง

                                String substituted_on = eleContent1.select(".data.league_club_keeper").get(2).text();  
                                jsonPlayedLeagueDetail.put("substituted_on", substituted_on);                                  //เปลี่ยนตัวเข้า

                                String substituted_off = eleContent1.select(".data.league_club_keeper").get(3).text();  
                                jsonPlayedLeagueDetail.put("substituted_off", substituted_off);                                //เปลี่ยนตัวออก

                                String yellow = eleContent1.select(".data.league_club_keeper").get(4).text();  
                                jsonPlayedLeagueDetail.put("yellow_cards", yellow);                                            //ใบเหลือง

                                String yellow_red = eleContent1.select(".data.league_club_keeper").get(5).text();  
                                jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);                                    //ใบเหลือง/ใบเเดง

                                String red = eleContent1.select(".data.league_club_keeper").get(6).text();  
                                jsonPlayedLeagueDetail.put("red_cards", red);                                                  //ใบแดง

                                String conceded = eleContent1.select(".data.league_club_keeper").get(7).text();  
                                jsonPlayedLeagueDetail.put("goals_conceded", conceded);                                        //เสียประตู

                                String shutout = eleContent1.select(".data.league_club_keeper").get(8).text();  
                                jsonPlayedLeagueDetail.put("clean_sheets", shutout);                                           //คลีนชีท

                                String time = eleContent1.select(".time").text();  
                                jsonPlayedLeagueDetail.put("minutes_played", time);                                            //ลงเล่น (เวลา:นาที)

                                arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            }
                            // class content สุดท้าย
                            jsonPlayedLeagueDetail = new JSONObject();

                            Element elesContent1Last2 = elesTable2.select(".content").last();
                            String sum_matches2 = elesContent1Last2.select(".matches.league_club_keeper.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_matches", sum_matches2);                                                //ลงเล่น(แมตซ์)

                            String sum_goals2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(0).text();  
                            jsonPlayedLeagueDetail.put("sum_goals", sum_goals2);                                                   //ทำประตู

                            String sum_own_goals2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(1).text();  
                            jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals2);                                           //ทำเข้าประตูตนเอง

                            String sum_substituted_on2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(2).text();   
                            jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on2);                                 //เปลี่ยนตัวเข้า

                            String sum_substituted_off2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(3).text();  
                            jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off2);                               //เปลี่ยนตัวออก

                            String sum_yellow2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(4).text();  
                            jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow2);                                           //ใบเหลือง

                            String sum_yellow_red2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(5).text();  
                            jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red2);                                   //ใบเหลือง/ใบเเดง

                            String sum_red2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(6).text();  
                            jsonPlayedLeagueDetail.put("sum_red_cards", sum_red2);                                                 //ใบแดง

                            String sum_conceded2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(7).text();  
                            jsonPlayedLeagueDetail.put("sum_goals_conceded", sum_conceded2);                                       //เสียประตู

                            String sum_shutout2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(8).text();  
                            jsonPlayedLeagueDetail.put("sum_clean_sheets", sum_shutout2);                                          //คลีนชีท

                            String sum_time2 = elesContent1Last2.select(".time.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time2);                                           //ลงเล่น (เวลา:นาที)

                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            json.put("played_international_league_detail", arrPlayedLeagueDetail);
                        }
                    }
                    //ข้อมูลการลงเล่นทีมชาติ
                    if ("nationalteam".equals(arrStr[1])) {
                        arrPlayedLeagueDetail = new JSONArray();
                        String nationalteam = url + "/" + arrStr[1];
                        Document docNt = Jsoup.connect(nationalteam).timeout(60 * 1000).get();
                        Elements elesData = docNt.select(".data_played-full");
                        Elements elesContentNt = elesData.select(".content");
                        int maxContent = 0;
                        for (Element eleDataContent : elesContentNt) {  // นับจำนวน class content 
                            if (eleDataContent.hasClass("content")) {
                                maxContent++;
                            }
                        }

                        for (int i = 0; i < (maxContent - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                            jsonPlayedLeagueDetail = new JSONObject();
                            Element eleContent1 = elesData.select(".content").get(i);  //index แรกเริ่มจาก 0
                            String competition = eleContent1.select(".competition.national").text();  
                            jsonPlayedLeagueDetail.put("competition", competition);                                      //รายการแข่งขัน

                            String matches = eleContent1.select(".matches").text();  
                            jsonPlayedLeagueDetail.put("matches", matches);                                              //ลงเล่น(แมตซ์)

                            String goals = eleContent1.select(".data.keeper").get(0).text();  
                            jsonPlayedLeagueDetail.put("goals", goals);                                                  //ทำประตู 

                            String own_goals = eleContent1.select(".data.keeper").get(1).text();  
                            jsonPlayedLeagueDetail.put("own_goals", own_goals);                                          //ทำเข้าประตูตนเอง

                            String substituted_on = eleContent1.select(".data.keeper").get(2).text();  
                            jsonPlayedLeagueDetail.put("substituted_on", substituted_on);                                //เปลี่ยนตัวเข้า

                            String substituted_off = eleContent1.select(".data.keeper").get(3).text();  
                            jsonPlayedLeagueDetail.put("substituted_off", substituted_off);                              //เปลี่ยนตัวออก

                            String yellow = eleContent1.select(".data.keeper").get(4).text();  
                            jsonPlayedLeagueDetail.put("yellow_cards", yellow);                                          //ใบเหลือง  

                            String yellow_red = eleContent1.select(".data.keeper").get(5).text();  
                            jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);                                  //ใบเหลือง/ใบเเดง

                            String red = eleContent1.select(".data.keeper").get(6).text();  
                            jsonPlayedLeagueDetail.put("red_cards", red);                                                //ใบแดง

                            String conceded = eleContent1.select(".data.keeper").get(7).text();  
                            jsonPlayedLeagueDetail.put("goals_conceded", conceded);                                      //เสียประตู

                            String shutout = eleContent1.select(".data.keeper").get(8).text();  
                            jsonPlayedLeagueDetail.put("clean_sheets", shutout);                                         //คลีนชีท

                            String time = eleContent1.select(".time.keeper").text();  
                            jsonPlayedLeagueDetail.put("minutes_played", time);                                          //ลงเล่น (เวลา:นาที)
                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        }
                        // class content สุดท้าย
                        jsonPlayedLeagueDetail = new JSONObject();
                        Element elesContent1Last = elesData.select(".content").last();
                        String sum_matches = elesContent1Last.select(".matches.sum").text();  
                        jsonPlayedLeagueDetail.put("sum_matches", sum_matches);                                         //ลงเล่น(แมตช์)

                        String sum_goals = elesContent1Last.select(".data.keeper.sum").get(0).text();  
                        jsonPlayedLeagueDetail.put("sum_goals", sum_goals);                                             //ทำประตู

                        String sum_own_goals = elesContent1Last.select(".data.keeper.sum").get(1).text();  
                        jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);                                     //ทำเข้าประตูตนเอง

                        String sum_substituted_on = elesContent1Last.select(".data.keeper.sum").get(2).text();  
                        jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);                           //เปลี่ยนตัวเข้า

                        String sum_substituted_off = elesContent1Last.select(".data.keeper.sum").get(3).text();  
                        jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);                         //เปลี่ยนตัวออก

                        String sum_yellow = elesContent1Last.select(".data.keeper.sum").get(4).text();  
                        jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);                                     //ใบเหลือง

                        String sum_yellow_red = elesContent1Last.select(".data.keeper.sum").get(5).text();  
                        jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);                             //ใบเหลือง/ใบเเดง

                        String sum_red = elesContent1Last.select(".data.keeper.sum").get(6).text();  
                        jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);                                           //ใบแดง

                        String sum_conceded = elesContent1Last.select(".data.keeper.sum").get(7).text();  
                        jsonPlayedLeagueDetail.put("sum_goals_conceded", sum_conceded);                                 //เสียประตู

                        String sum_shutout = elesContent1Last.select(".data.keeper.sum").get(8).text();  
                        jsonPlayedLeagueDetail.put("sum_clean_sheets", sum_shutout);                                    //คลีนชีท

                        String sum_time = elesContent1Last.select(".time.keeper.sum").text();  
                        jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);                                     //ลงเล่น (เวลา:นาที)

                        arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        json.put("played_national_detail", arrPlayedLeagueDetail);
                    }
                }
                //จบกรณีผู้รักษาประตู 
                //กรณีผู้เล่นอื่นๆ   
            } else {
                //System.out.println("+++นักเตะผู้อื่น+++");
                arrPlayedLeagueDetail = new JSONArray();
                Elements elesDataPlayer = elementsContent.select(".data_played");
                Elements elesFoot = elesDataPlayer.select(".foot");
                for (Element ele : elesFoot) {
                    Element eleImg = ele.select("a").first();
                    String img = eleImg.attr("href");
                    String[] arrStr = img.split("/");
                    //ข้อมูลการลงเล่นฟุตบอลลีกและถ้วยต่างๆ
                    if ("performance-detail".equals(arrStr[1])) {
                        String performanceDetail = url + "/" + arrStr[1];
                        Document docpd = Jsoup.connect(performanceDetail).timeout(60 * 1000).get();
                        Elements data = docpd.select(".data_played-full");
                        int countData = 0;
                        for (Element eleData : data) {  // นับจำนวน class data_played-full
                            if (eleData.hasClass("data_played-full")) {
                                countData++;
                            }
                        }
                        if (countData == 1) {  // มีตารางเดียว
                            Element elesTable1 = docpd.select(".data_played-full").first();
                            Elements elesContent1 = elesTable1.select(".content");
                            int maxContent = 0;
                            for (Element eleDataContent : elesContent1) {  // นับจำนวน class content 
                                if (eleDataContent.hasClass("content")) {
                                    maxContent++;
                                }
                            }
                            for (int i = 0; i < (maxContent - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                                jsonPlayedLeagueDetail = new JSONObject();
                                Element eleContent1 = elesTable1.select(".content").get(i);  //index แรกเริ่มจาก 0
                                String season = eleContent1.select(".season").text();  
                                jsonPlayedLeagueDetail.put("season", season);                                    //ฤดูกาล
 
                                String competition = eleContent1.select(".competition").text();  
                                jsonPlayedLeagueDetail.put("competition", competition);                          //รายการแข่งขัน

                                String club = eleContent1.select(".club").text();  
                                jsonPlayedLeagueDetail.put("club", club);                                        //ทีมสโมสร

                                String matches = eleContent1.select(".matches.league_club").text();  
                                jsonPlayedLeagueDetail.put("matches", matches);                                  //ลงเล่น(แมตซ์)

                                String goals = eleContent1.select(".data.league_club").get(0).text();  
                                jsonPlayedLeagueDetail.put("goals", goals);                                      //ทำประตู

                                String assists = eleContent1.select(".data.league_club").get(1).text();  
                                jsonPlayedLeagueDetail.put("assists", assists);                                  //แอสซิสต์

                                String own_goals = eleContent1.select(".data.league_club").get(2).text();  
                                jsonPlayedLeagueDetail.put("own_goals", own_goals);                              //ทำเข้าประตูตนเอง

                                String substituted_on = eleContent1.select(".data.league_club").get(3).text();  
                                jsonPlayedLeagueDetail.put("substituted_on", substituted_on);                    //เปลี่ยนตัวเข้า

                                String substituted_off = eleContent1.select(".data.league_club").get(4).text();  
                                jsonPlayedLeagueDetail.put("substituted_off", substituted_off);                  //เปลี่ยนตัวออก 

                                String yellow = eleContent1.select(".data.league_club").get(5).text();  
                                jsonPlayedLeagueDetail.put("yellow_cards", yellow);                              //ใบเหลือง

                                String yellow_red = eleContent1.select(".data.league_club").get(6).text();  
                                jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);                      //ใบเหลือง/ใบเเดง

                                String red = eleContent1.select(".data.league_club").get(7).text();  
                                jsonPlayedLeagueDetail.put("red_cards", red);                                    //ใบเเดง

                                String penalty_goals = eleContent1.select(".data.league_club").get(8).text();  
                                jsonPlayedLeagueDetail.put("penalty_goals", penalty_goals);                      //ทำประตู(จุดโทษ)

                                String mpg = eleContent1.select(".mpg").text();  
                                jsonPlayedLeagueDetail.put("minutes_played_goals", mpg);                         //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)

                                String time = eleContent1.select(".time").text();  
                                jsonPlayedLeagueDetail.put("minutes_played", time);                              //ลงเล่น (เวลา:นาที)

                                arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            }
                            // class content สุดท้าย
                            jsonPlayedLeagueDetail = new JSONObject();
                            Element elesContent1Last = elesTable1.select(".content").last();
                            String sum_matches = elesContent1Last.select(".matches.league_club.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_matches", sum_matches);                                       //ลงเล่น(แมตซ์)

                            String sum_goals = elesContent1Last.select(".data.league_club.sum").get(0).text();  
                            jsonPlayedLeagueDetail.put("sum_goals", sum_goals);                                           //ทำประตู

                            String sum_assists = elesContent1Last.select(".data.league_club.sum").get(1).text();  
                            jsonPlayedLeagueDetail.put("sum_assists", sum_assists);                                       //แอสซิสต์

                            String sum_own_goals = elesContent1Last.select(".data.league_club.sum").get(2).text();  
                            jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);                                   //ทำเข้าประตูตนเอง

                            String sum_substituted_on = elesContent1Last.select(".data.league_club.sum").get(3).text();   
                            jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);                         //เปลี่ยนตัวเข้า

                            String sum_substituted_off = elesContent1Last.select(".data.league_club.sum").get(4).text();  
                            jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);                       //เปลี่ยนตัวออก 

                            String sum_yellow = elesContent1Last.select(".data.league_club.sum").get(5).text();  
                            jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);                                   //ใบเหลือง

                            String sum_yellow_red = elesContent1Last.select(".data.league_club.sum").get(6).text();  
                            jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);                           //ใบเหลือง/ใบเเดง

                            String sum_red = elesContent1Last.select(".data.league_club.sum").get(7).text();  
                            jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);                                         //ใบเเดง

                            String sum_penalty_goals = elesContent1Last.select(".data.league_club.sum").get(8).text();  
                            jsonPlayedLeagueDetail.put("sum_penalty_goals", sum_penalty_goals);                           //ทำประตู(จุดโทษ)
 
                            String sum_mpg = elesContent1Last.select(".mpg.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_minutes_played_goals", sum_mpg);                              //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)

                            String sum_time = elesContent1Last.select(".time.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);                                   //ลงเล่น (เวลา:นาที)

                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            json.put("played_league_detail", arrPlayedLeagueDetail);
                        }
                        if (countData == 2) {  // มี 2 ตาราง
                            arrPlayedLeagueDetail = new JSONArray();
                            Element elesTable1 = docpd.select(".data_played-full").first();
                            Elements elesContent1 = elesTable1.select(".content");
                            int maxContent = 0;
                            for (Element eleDataContent : elesContent1) {  // นับจำนวน class content 
                                if (eleDataContent.hasClass("content")) {
                                    maxContent++;
                                }
                            }

                            for (int i = 0; i < (maxContent - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                                jsonPlayedLeagueDetail = new JSONObject();
                                Element eleContent1 = elesTable1.select(".content").get(i);  //index แรกเริ่มจาก 0
                                String season = eleContent1.select(".season").text();  
                                jsonPlayedLeagueDetail.put("season", season);                                           //ฤดูกาล

                                String competition = eleContent1.select(".competition").text();  
                                jsonPlayedLeagueDetail.put("competition", competition);                                 //รายการแข่งขัน

                                String club = eleContent1.select(".club").text();  
                                jsonPlayedLeagueDetail.put("club", club);                                               //ทีมสโมสร

                                String matches = eleContent1.select(".matches.league_club").text();  
                                jsonPlayedLeagueDetail.put("matches", matches);                                         //ลงเล่น(แมตซ์)

                                String goals = eleContent1.select(".data.league_club").get(0).text();  
                                jsonPlayedLeagueDetail.put("goals", goals);                                             //ทำประตู

                                String assists = eleContent1.select(".data.league_club").get(1).text();  
                                jsonPlayedLeagueDetail.put("assists", assists);                                         //แอสซิสต์

                                String own_goals = eleContent1.select(".data.league_club").get(2).text();  
                                jsonPlayedLeagueDetail.put("own_goals", own_goals);                                     //ทำเข้าประตูตนเอง

                                String substituted_on = eleContent1.select(".data.league_club").get(3).text();  
                                jsonPlayedLeagueDetail.put("substituted_on", substituted_on);                           //เปลี่ยนตัวเข้า

                                String substituted_off = eleContent1.select(".data.league_club").get(4).text();  
                                jsonPlayedLeagueDetail.put("substituted_off", substituted_off);                         //เปลี่ยนตัวออก

                                String yellow = eleContent1.select(".data.league_club").get(5).text();  
                                jsonPlayedLeagueDetail.put("yellow_cards", yellow);                                     //ใบเหลือง

                                String yellow_red = eleContent1.select(".data.league_club").get(6).text();  
                                jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);                             //ใบเหลือง/ใบเเดง

                                String red = eleContent1.select(".data.league_club").get(7).text();  
                                jsonPlayedLeagueDetail.put("red_cards", red);                                           //ใบเเดง

                                String penalty_goals = eleContent1.select(".data.league_club").get(8).text();  
                                jsonPlayedLeagueDetail.put("penalty_goals", penalty_goals);                             //ทำประตู(จุดโทษ)

                                String mpg = eleContent1.select(".mpg").text();  
                                jsonPlayedLeagueDetail.put("minutes_played_goals", mpg);                                //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)

                                String time = eleContent1.select(".time").text();  
                                jsonPlayedLeagueDetail.put("minutes_played", time);                                     //ลงเล่น (เวลา:นาที)
                                arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            }
                            // class content สุดท้าย
                            jsonPlayedLeagueDetail = new JSONObject();
                            Element elesContent1Last = elesTable1.select(".content").last();
                            String sum_matches = elesContent1Last.select(".matches.league_club.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_matches", sum_matches);                                             //ลงเล่น(แมตซ์)

                            String sum_goals = elesContent1Last.select(".data.league_club.sum").get(0).text();  
                            jsonPlayedLeagueDetail.put("sum_goals", sum_goals);                                                 //ทำประตู

                            String sum_assists = elesContent1Last.select(".data.league_club.sum").get(1).text();  
                            jsonPlayedLeagueDetail.put("sum_assists", sum_assists);                                             //แอสซิสต์

                            String sum_own_goals = elesContent1Last.select(".data.league_club.sum").get(2).text();  
                            jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);                                         //ทำเข้าประตูตนเอง

                            String sum_substituted_on = elesContent1Last.select(".data.league_club.sum").get(3).text();  
                            jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);                               //เปลี่ยนตัวเข้า

                            String sum_substituted_off = elesContent1Last.select(".data.league_club.sum").get(4).text();  
                            jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);                             //เปลี่ยนตัวออก

                            String sum_yellow = elesContent1Last.select(".data.league_club.sum").get(5).text();  
                            jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);                                         //ใบเหลือง

                            String sum_yellow_red = elesContent1Last.select(".data.league_club.sum").get(6).text();  
                            jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);                                 //ใบเหลือง/ใบเเดง

                            String sum_red = elesContent1Last.select(".data.league_club.sum").get(7).text();  
                            jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);                                               //ใบเเดง

                            String sum_penalty_goals = elesContent1Last.select(".data.league_club.sum").get(8).text();  
                            jsonPlayedLeagueDetail.put("sum_penalty_goals", sum_penalty_goals);                                 //ทำประตู(จุดโทษ)

                            String sum_mpg = elesContent1Last.select(".mpg.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_minutes_played_goals", sum_mpg);                                    //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)

                            String sum_time = elesContent1Last.select(".time.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);                                         //ลงเล่น (เวลา:นาที)
                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            json.put("played_league_detail", arrPlayedLeagueDetail);

                            //ข้อมูลการลงเล่นฟุตบอลถ้วยระดับนานาชาติ
                            arrPlayedLeagueDetail = new JSONArray();
                            Element elesTable2 = docpd.select(".data_played-full").last();
                            Elements elesContent2 = elesTable2.select(".content");
                            int maxContent2 = 0;
                            for (Element eleDataContent : elesContent2) {  // นับจำนวน class content 
                                if (eleDataContent.hasClass("content")) {
                                    maxContent2++;
                                }
                            }

                            for (int i = 0; i < (maxContent2 - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                                jsonPlayedLeagueDetail = new JSONObject();
                                Element eleContent1 = elesTable2.select(".content").get(i);  //index แรกเริ่มจาก 0
                                String season = eleContent1.select(".season").text();  
                                jsonPlayedLeagueDetail.put("season", season);                                                 //ฤดูกาล

                                String competition = eleContent1.select(".competition").text();  
                                jsonPlayedLeagueDetail.put("competition", competition);                                       //รายการแข่งขัน

                                String club = eleContent1.select(".club").text();  
                                jsonPlayedLeagueDetail.put("club", club);                                                     //ทีมสโมสร

                                String matches = eleContent1.select(".matches.league_club").text();  
                                jsonPlayedLeagueDetail.put("matches", matches);                                               //ลงเล่น(แมตซ์)

                                String goals = eleContent1.select(".data.league_club").get(0).text();  
                                jsonPlayedLeagueDetail.put("goals", goals);                                                   //ทำประตู

                                String assists = eleContent1.select(".data.league_club").get(1).text();  
                                jsonPlayedLeagueDetail.put("assists", assists);                                               //แอสซิสต์

                                String own_goals = eleContent1.select(".data.league_club").get(2).text();  
                                jsonPlayedLeagueDetail.put("own_goals", own_goals);                                           //ทำเข้าประตูตนเอง

                                String substituted_on = eleContent1.select(".data.league_club").get(3).text();  
                                jsonPlayedLeagueDetail.put("substituted_on", substituted_on);                                 //เปลี่ยนตัวเข้า

                                String substituted_off = eleContent1.select(".data.league_club").get(4).text();  
                                jsonPlayedLeagueDetail.put("substituted_off", substituted_off);                               //เปลี่ยนตัวออก 
 
                                String yellow = eleContent1.select(".data.league_club").get(5).text();  
                                jsonPlayedLeagueDetail.put("yellow_cards", yellow);                                           //ใบเหลือง

                                String yellow_red = eleContent1.select(".data.league_club").get(6).text();  
                                jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);                                   //ใบเหลือง/ใบเเดง

                                String red = eleContent1.select(".data.league_club").get(7).text();  
                                jsonPlayedLeagueDetail.put("red_cards", red);                                                 //ใบเเดง

                                String penalty_goals = eleContent1.select(".data.league_club").get(8).text();  
                                jsonPlayedLeagueDetail.put("penalty_goals", penalty_goals);                                   //ทำประตู(จุดโทษ)

                                String mpg = eleContent1.select(".mpg").text();  
                                jsonPlayedLeagueDetail.put("minutes_played_goals", mpg);                                      //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)

                                String time = eleContent1.select(".time").text();  
                                jsonPlayedLeagueDetail.put("minutes_played", time);                                           //ลงเล่น (เวลา:นาที)

                                arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            }
                            // class content สุดท้าย
                            jsonPlayedLeagueDetail = new JSONObject();
                            Element elesContent1Last2 = elesTable2.select(".content").last();
                            String sum_matches2 = elesContent1Last2.select(".matches.league_club.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_matches", sum_matches2);                                              //ลงเล่น(แมตซ์)

                            String sum_goals2 = elesContent1Last2.select(".data.league_club.sum").get(0).text();  
                            jsonPlayedLeagueDetail.put("sum_goals", sum_goals2);                                                  //ทำประตู

                            String sum_assists2 = elesContent1Last2.select(".data.league_club.sum").get(1).text();  
                            jsonPlayedLeagueDetail.put("sum_assists", sum_assists2);                                              //แอสซิสต์

                            String sum_own_goals2 = elesContent1Last2.select(".data.league_club.sum").get(2).text();  
                            jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals2);                                          //ทำเข้าประตูตนเอง

                            String sum_substituted_on2 = elesContent1Last2.select(".data.league_club.sum").get(3).text();  
                            jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on2);                                //เปลี่ยนตัวเข้า

                            String sum_substituted_off2 = elesContent1Last2.select(".data.league_club.sum").get(4).text();  
                            jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off2);                              //เปลี่ยนตัวออก 

                            String sum_yellow2 = elesContent1Last2.select(".data.league_club.sum").get(5).text();  
                            jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow2);                                          //ใบเหลือง

                            String sum_yellow_red2 = elesContent1Last2.select(".data.league_club.sum").get(6).text();  
                            jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red2);                                  //ใบเหลือง/ใบเเดง

                            String sum_red2 = elesContent1Last2.select(".data.league_club.sum").get(7).text();  
                            jsonPlayedLeagueDetail.put("sum_red_cards", sum_red2);                                                //ใบเเดง

                            String sum_penalty_goals2 = elesContent1Last2.select(".data.league_club.sum").get(8).text();  
                            jsonPlayedLeagueDetail.put("sum_penalty_goals", sum_penalty_goals2);                                  //ทำประตู(จุดโทษ)

                            String sum_mpg2 = elesContent1Last2.select(".mpg.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_minutes_played_goals", sum_mpg2);                                     //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)

                            String sum_time2 = elesContent1Last2.select(".time.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time2);                                          //ลงเล่น (เวลา:นาที)

                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            json.put("played_international_league_detail", arrPlayedLeagueDetail);
                        }
                    }
                    //ข้อมูลการลงเล่นทีมชาติ
                    if ("nationalteam".equals(arrStr[1])) {
                        arrPlayedLeagueDetail = new JSONArray();
                        String nationalteam = url + "/" + arrStr[1];
                        Document docNt = Jsoup.connect(nationalteam).timeout(60 * 1000).get();
                        Elements elesData = docNt.select(".data_played-full");
                        Elements elesContentNt = elesData.select(".content");
                        int maxContent = 0;
                        for (Element eleDataContent : elesContentNt) {  // นับจำนวน class content 
                            if (eleDataContent.hasClass("content")) {
                                maxContent++;
                            }
                        }

                        for (int i = 0; i < (maxContent - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                            jsonPlayedLeagueDetail = new JSONObject();
                            Element eleContent1 = elesData.select(".content").get(i);  //index แรกเริ่มจาก 0                       
                            String competition = eleContent1.select(".competition.national").text();  
                            jsonPlayedLeagueDetail.put("competition", competition);                           //รายการแข่งขัน

                            String matches = eleContent1.select(".matches").text();  
                            jsonPlayedLeagueDetail.put("matches", matches);                                   //ลงเล่น(แมตซ์)

                            String goals = eleContent1.select(".data").get(0).text();  
                            jsonPlayedLeagueDetail.put("goals", goals);                                       //ทำประตู

                            String assists = eleContent1.select(".data").get(1).text();  
                            jsonPlayedLeagueDetail.put("assists", assists);                                   //แอสซิสต์

                            String own_goals = eleContent1.select(".data").get(2).text();  
                            jsonPlayedLeagueDetail.put("own_goals", own_goals);                               //ทำเข้าประตูตนเอง

                            String substituted_on = eleContent1.select(".data").get(3).text();  
                            jsonPlayedLeagueDetail.put("substituted_on", substituted_on);                     //เปลี่ยนตัวเข้า

                            String substituted_off = eleContent1.select(".data").get(4).text();  
                            jsonPlayedLeagueDetail.put("substituted_off", substituted_off);                   //เปลี่ยนตัวออก

                            String yellow = eleContent1.select(".data").get(5).text();  
                            jsonPlayedLeagueDetail.put("yellow_cards", yellow);                               //ใบเหลือง

                            String yellow_red = eleContent1.select(".data").get(6).text();  
                            jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);                       //ใบเหลือง/ใบเเดง

                            String red = eleContent1.select(".data").get(7).text();  
                            jsonPlayedLeagueDetail.put("red_cards", red);                                     //ใบเเดง

                            String penalty_goals = eleContent1.select(".data").get(8).text();  
                            jsonPlayedLeagueDetail.put("penalty_goals", penalty_goals);                       //ทำประตู(จุดโทษ)

                            String mpg = eleContent1.select(".mpg").text();  
                            jsonPlayedLeagueDetail.put("minutes_played_goals", mpg);                          //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)

                            String time = eleContent1.select(".time").text();  
                            jsonPlayedLeagueDetail.put("minutes_played", time);                               //ลงเล่น (เวลา:นาที)

                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        }
                        // class content สุดท้าย
                        jsonPlayedLeagueDetail = new JSONObject();
                        Element elesContent1Last = elesData.select(".content").last();
                        String sum_matches = elesContent1Last.select(".matches.sum").text();  
                        jsonPlayedLeagueDetail.put("sum_matches", sum_matches);                              //ลงเล่น(แมตซ์)

                        String sum_goals = elesContent1Last.select(".data.sum").get(0).text();  
                        jsonPlayedLeagueDetail.put("sum_goals", sum_goals);                                  //ทำประตู

                        String sum_assists = elesContent1Last.select(".data.sum").get(1).text();  
                        jsonPlayedLeagueDetail.put("sum_assists", sum_assists);                              //แอสซิสต์

                        String sum_own_goals = elesContent1Last.select(".data.sum").get(2).text();  
                        jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);                          //ทำเข้าประตูตนเอง

                        String sum_substituted_on = elesContent1Last.select(".data.sum").get(3).text();  
                        jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);                //เปลี่ยนตัวเข้า

                        String sum_substituted_off = elesContent1Last.select(".data.sum").get(4).text();  
                        jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);              //เปลี่ยนตัวออก 

                        String sum_yellow = elesContent1Last.select(".data.sum").get(5).text();  
                        jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);                          //ใบเหลือง

                        String sum_yellow_red = elesContent1Last.select(".data.sum").get(6).text();  
                        jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);                  //ใบเหลือง/ใบเเดง

                        String sum_red = elesContent1Last.select(".data.sum").get(7).text();  
                        jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);                                //ใบเเดง

                        String sum_penalty_goals = elesContent1Last.select(".data.sum").get(8).text();  
                        jsonPlayedLeagueDetail.put("sum_penalty_goals", sum_penalty_goals);                  //ทำประตู(จุดโทษ)

                        String sum_mpg = elesContent1Last.select(".mpg.sum").text();  
                        jsonPlayedLeagueDetail.put("sum_minutes_played_goals", sum_mpg);                     //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)

                        String sum_time = elesContent1Last.select(".time.sum").text();  
                        jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);                          //ลงเล่น (เวลา:นาที)

                        arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        json.put("played_national_detail", arrPlayedLeagueDetail);
                    }
                }
            }
            //จบกรณีผู้เล่นอื่นๆ 
            els.inputElasticsearch(json.toString(), "player_profile_thaipremierleague");
            System.out.println(dateTimes.thaiDateTime()+" : insert player profile thaipremierleague complete");
        } catch (IOException | JSONException e) {
            e.getMessage();
        }

    }

    @Override
    public void premierleaguePlayerProfile(String url) {
        String baseLink = "http://www.livesoccer888.com";
        try {
            boolean isGoalKeeper = false;  //ผู้รักษาประตู
            JSONObject json = new JSONObject();
            JSONObject jsonDetailTransfer;
            JSONArray arrTransfer = new JSONArray();

            json.put("link", url);                                              //ลิ้งก์
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Elements elements = doc.getElementsByClass("box-player");

            Element eleImgTeam = elements.select("img").get(0);
            String imgTeam = eleImgTeam.attr("src");
            imgTeam = baseLink + func.getNewLinkImage(imgTeam);
            json.put("logo_team", imgTeam);                                     //โลโก้ทีม

            String team = elements.select("span").get(1).text();
            json.put("team", team);                                             //ทีม  

            Element eleImg = elements.select("img").get(1);
            String img = eleImg.attr("src");
            img = baseLink + func.getNewLinkImage(img);
            json.put("img_profile", img);                                       //รูปนักเตะ

            String thName = elements.select("span").get(2).text();
            json.put("th_name", thName);                                        //ชื่อภาษาไทย
            //String thName = elements.select("span[itemprop = name]").text();
            //System.out.println(thName);

            String engName = elements.select("span[itemprop = alternateName]").text();
            json.put("en_name", engName);                                       //ชื่อภาษาอังกฤษ

            Elements elesProfile = elements.select(".data-profile");
            String birthdate = elesProfile.select("span[itemprop = birthDate]").text();//.get(2)
            json.put("birthday", birthdate);                                    //วันเกิด

            String age = elesProfile.select("#player-age").text();
            json.put("age", age);                                               //อายุ

            String nationality = elesProfile.select("span[itemprop = nationality]").text();
            json.put("nationality", nationality);                               //สัญชาติ

            String number = elesProfile.select("#number").text();
            json.put("squad_nember", number);                                   //สวมเสื้อเบอร์

            String height = elesProfile.select("span[itemprop = height]").text();
            json.put("height", height);

            String footedness = elesProfile.select("#footedness").text();
            json.put("footed", footedness);                                     //ถนัดเท้า

            String pricePlayer = elesProfile.select("span[itemprop = netWorth]").text();
            json.put("net_worth", pricePlayer);                                 //ค่าตัว

            String oldClub = elesProfile.select("#fromclub").text();
            json.put("original_club", oldClub);                                 //สโมสรเดิม

            Elements elesPosition = elements.select(".data-position");
            String position = elesPosition.select("span[itemprop = roleName]").text();
            json.put("position", position);
            if ("ผู้รักษาประตู".equals(position)) {
                isGoalKeeper = true;
            }

            Elements elesContract = elements.select(".data-contract");
            String startSignContract = elesContract.select("span[itemprop = startDate]").text();
            json.put("sign_contract", startSignContract);                       //เซ็นสัญญาเมื่อ

            String endSignContract = elesContract.select("span[itemprop = endDate]").text();
            json.put("end_contract", endSignContract);                          //สิ้นสุดสัญญา

            // กรณือื่นๆเช่นเป็นกัปตันทีม  หรือ บาดเจ็บ
            Elements elesCaptain = elements.select(".captain");
            if (elesCaptain.hasClass("captain")) {
                String captain = elesCaptain.select(".captain").get(0).text(); 
                json.put("captain", captain);                                   //กัปตัน
            }
            Elements elesInjury = elements.select(".injury");
            if (elesInjury.hasClass("injury")) {
                String injury = elesInjury.select(".injury").get(0).text();
                json.put("injury", injury);                                     //อาการบาดเจ็บ
            }

            //ข้อมูลประวัติการย้ายสโมสร
            Elements elementsContent = doc.select(".content.main-content.left-content");
            Elements elesDataTransfer = elementsContent.select(".data_transfer");
            Elements elesContent = elesDataTransfer.select(".content");
            for (Element ele : elesContent) {
                jsonDetailTransfer = new JSONObject();
                String season = ele.select(".season").text();       
                jsonDetailTransfer.put("season_transfer", season);              //ฤดูกาล

                String date = ele.select(".date").text();           
                jsonDetailTransfer.put("date_transfer", date);                  //วันที่

                String movefrom = ele.select(".movefrom").text();   
                jsonDetailTransfer.put("movefrom", movefrom);                   //ย้ายออกจาก

                String moveto = ele.select(".moveto").text();       
                jsonDetailTransfer.put("moveto", moveto);                       //ย้ายเข้ามา

                String transfer = ele.select(".transfer").text();   
                jsonDetailTransfer.put("transfer", transfer);                   //การซื้อขาย

                arrTransfer.put(jsonDetailTransfer);
            }
            json.put("transfer_detail", arrTransfer);
            //จบข้อมูลประวัติการย้ายสโมสร

            //กรณีผู้รักษาประตู 
            JSONObject jsonPlayedLeagueDetail;
            JSONArray arrPlayedLeagueDetail = new JSONArray();
            if (isGoalKeeper) {
                Elements elesDataPlayer = elementsContent.select(".data_played");
                Elements elesFoot = elesDataPlayer.select(".foot");
                for (Element ele : elesFoot) {
                    Element eleImgFoot = ele.select("a").first();
                    String imgData = eleImgFoot.attr("href");
                    String[] arrStr = imgData.split("/");
                    //ข้อมูลการลงเล่นฟุตบอลลีกและถ้วยต่างๆ
                    if ("performance-detail".equals(arrStr[1])) {
                        String performanceDetail = url + "/" + arrStr[1];
                        Document docpd = Jsoup.connect(performanceDetail).timeout(60 * 1000).get();
                        Elements data = docpd.select(".data_played-full");
                        int countData = 0;
                        for (Element eleData : data) {  // นับจำนวน class data_played-full
                            if (eleData.hasClass("data_played-full")) {
                                countData++;
                            }
                        }
                        if (countData == 1) {  // มีตารางเดียว (ข้อมูลการลงเล่นฟุตบอลลีก)
                            Element elesTable1 = docpd.select(".data_played-full").first();
                            Elements elesContent1 = elesTable1.select(".content");
                            int maxContent = 0;
                            for (Element eleDataContent : elesContent1) {  // นับจำนวน class content 
                                if (eleDataContent.hasClass("content")) {
                                    maxContent++;
                                }
                            }
                            for (int i = 0; i < (maxContent - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                                jsonPlayedLeagueDetail = new JSONObject();
                                Element eleContent1 = elesTable1.select(".content").get(i);  //index แรกเริ่มจาก 0
                                String season = eleContent1.select(".season").text();  
                                jsonPlayedLeagueDetail.put("season", season);                                           //ฤดูกาล

                                String competition = eleContent1.select(".competition").text();  
                                jsonPlayedLeagueDetail.put("competition", competition);                                 //รายการแข่งขัน

                                String club = eleContent1.select(".club").text();  
                                jsonPlayedLeagueDetail.put("club", club);                                               //ทีมสโมสร

                                String matches = eleContent1.select(".matches.league_club_keeper").text();  
                                jsonPlayedLeagueDetail.put("matches", matches);                                         //ลงเล่น(แมตซ์)

                                String goals = eleContent1.select(".data.league_club_keeper").get(0).text();  
                                jsonPlayedLeagueDetail.put("goals", goals);                                             //ทำประตู

                                String own_goals = eleContent1.select(".data.league_club_keeper").get(1).text();  
                                jsonPlayedLeagueDetail.put("own_goals", own_goals);                                     //ทำเข้าประตูตนเอง

                                String substituted_on = eleContent1.select(".data.league_club_keeper").get(2).text();  
                                jsonPlayedLeagueDetail.put("substituted_on", substituted_on);                           //เปลี่ยนตัวเข้า

                                String substituted_off = eleContent1.select(".data.league_club_keeper").get(3).text();  
                                jsonPlayedLeagueDetail.put("substituted_off", substituted_off);                         //เปลี่ยนตัวออก

                                String yellow = eleContent1.select(".data.league_club_keeper").get(4).text();  
                                jsonPlayedLeagueDetail.put("yellow_cards", yellow);                                     //ใบเหลือง

                                String yellowred = eleContent1.select(".data.league_club_keeper").get(5).text();  
                                jsonPlayedLeagueDetail.put("yellow_red_cards", yellowred);                              //ใบเหลือง/ใบเเดง

                                String red = eleContent1.select(".data.league_club_keeper").get(6).text();  
                                jsonPlayedLeagueDetail.put("red_cards", red);                                           //ใบแดง

                                String conceded = eleContent1.select(".data.league_club_keeper").get(7).text();  
                                jsonPlayedLeagueDetail.put("goals_conceded", conceded);                                 //เสียประตู

                                String shutout = eleContent1.select(".data.league_club_keeper").get(8).text();  
                                jsonPlayedLeagueDetail.put("clean_sheets", shutout);                                    //คลีนชีท

                                String time = eleContent1.select(".time").text();  
                                jsonPlayedLeagueDetail.put("minutes_played", time);                                     //ลงเล่น (เวลา:นาที)

                                arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            }
                            // class content สุดท้าย
                            jsonPlayedLeagueDetail = new JSONObject();
                            Element elesContent1Last = elesTable1.select(".content").last();
                            String sumMatches = elesContent1Last.select(".matches.league_club_keeper.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_matches", sumMatches);                                                 //ลงเล่น(แมตซ์)

                            String sum_goals = elesContent1Last.select(".data.league_club_keeper.sum").get(0).text();  
                            jsonPlayedLeagueDetail.put("sum_goals", sum_goals);                                                    //ทำประตู

                            String sum_own_goals = elesContent1Last.select(".data.league_club_keeper.sum").get(1).text();  
                            jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);                                            //ทำเข้าประตูตนเอง

                            String sum_substituted_on = elesContent1Last.select(".data.league_club_keeper.sum").get(2).text();  
                            jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);                                  //เปลี่ยนตัวเข้า

                            String sum_substituted_off = elesContent1Last.select(".data.league_club_keeper.sum").get(3).text();  
                            jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);                                //เปลี่ยนตัวออก

                            String sum_yellow = elesContent1Last.select(".data.league_club_keeper.sum").get(4).text(); 
                            jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);                                             //ใบเหลือง

                            String sum_yellow_red = elesContent1Last.select(".data.league_club_keeper.sum").get(5).text();  
                            jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);                                     //ใบเหลือง/ใบเเดง

                            String sum_red = elesContent1Last.select(".data.league_club_keeper.sum").get(6).text();  
                            jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);                                                   //ใบแดง

                            String sum_conceded = elesContent1Last.select(".data.league_club_keeper.sum").get(7).text();  
                            jsonPlayedLeagueDetail.put("sum_goals_conceded", sum_conceded);                                         //เสียประตู

                            String sum_shutout = elesContent1Last.select(".data.league_club_keeper.sum").get(8).text();  
                            jsonPlayedLeagueDetail.put("sum_clean_sheets", sum_shutout);                                            //คลีนชีท

                            String sum_time = elesContent1Last.select(".time.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);                                             //ลงเล่น (เวลา:นาที)

                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            json.put("played_league_detail", arrPlayedLeagueDetail);
                        }
                        if (countData == 2) {  // มี 2 ตาราง
                            arrPlayedLeagueDetail = new JSONArray();
                            Element elesTable1 = docpd.select(".data_played-full").first();
                            Elements elesContent1 = elesTable1.select(".content");
                            int maxContent = 0;
                            for (Element eleDataContent : elesContent1) {  // นับจำนวน class content 
                                if (eleDataContent.hasClass("content")) {
                                    maxContent++;
                                }
                            }

                            for (int i = 0; i < (maxContent - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                                jsonPlayedLeagueDetail = new JSONObject();
                                Element eleContent1 = elesTable1.select(".content").get(i);  //index แรกเริ่มจาก 0
                                String season = eleContent1.select(".season").text();  
                                jsonPlayedLeagueDetail.put("season", season);                                               //ฤดูกาล

                                String competition = eleContent1.select(".competition").text();  
                                jsonPlayedLeagueDetail.put("competition", competition);                                     //รายการแข่งขัน

                                String club = eleContent1.select(".club").text();  
                                jsonPlayedLeagueDetail.put("club", club);                                                   //ทีมสโมสร

                                String matches = eleContent1.select(".matches.league_club_keeper").text();  
                                jsonPlayedLeagueDetail.put("matches", matches);                                             //ลงเล่น(แมตซ์)

                                String goals = eleContent1.select(".data.league_club_keeper").get(0).text();  
                                jsonPlayedLeagueDetail.put("goals", goals);                                                 //ทำประตู 

                                String own_goals = eleContent1.select(".data.league_club_keeper").get(1).text();  
                                jsonPlayedLeagueDetail.put("own_goals", own_goals);                                         //ทำเข้าประตูตนเอง

                                String substituted_on = eleContent1.select(".data.league_club_keeper").get(2).text();  
                                jsonPlayedLeagueDetail.put("substituted_on", substituted_on);                               //เปลี่ยนตัวเข้า

                                String substituted_off = eleContent1.select(".data.league_club_keeper").get(3).text();  
                                jsonPlayedLeagueDetail.put("substituted_off", substituted_off);                             //เปลี่ยนตัวออก

                                String yellow = eleContent1.select(".data.league_club_keeper").get(4).text();  
                                jsonPlayedLeagueDetail.put("yellow_cards", yellow);                                         //ใบเหลือง

                                String yellow_red = eleContent1.select(".data.league_club_keeper").get(5).text();  
                                jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);                                 //ใบเหลือง/ใบเเดง

                                String red = eleContent1.select(".data.league_club_keeper").get(6).text();  
                                jsonPlayedLeagueDetail.put("red_cards", red);                                               //ใบแดง

                                String conceded = eleContent1.select(".data.league_club_keeper").get(7).text();  
                                jsonPlayedLeagueDetail.put("goals_conceded", conceded);                                     //เสียประตู

                                String shutout = eleContent1.select(".data.league_club_keeper").get(8).text();  
                                jsonPlayedLeagueDetail.put("clean_sheets", shutout);                                        //คลีนชีท

                                String time = eleContent1.select(".time").text();  
                                jsonPlayedLeagueDetail.put("minutes_played", time);                                         //ลงเล่น (เวลา:นาที)

                                arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            }
                            // class content สุดท้าย
                            jsonPlayedLeagueDetail = new JSONObject();
                            Element elesContent1Last = elesTable1.select(".content").last();
                            String sum_matches = elesContent1Last.select(".matches.league_club_keeper.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_matches", sum_matches);                                                //ลงเล่น(แมตซ์)

                            String sum_goals = elesContent1Last.select(".data.league_club_keeper.sum").get(0).text();  
                            jsonPlayedLeagueDetail.put("sum_goals", sum_goals);                                                    //ทำประตู

                            String sum_own_goals = elesContent1Last.select(".data.league_club_keeper.sum").get(1).text();  
                            jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);                                            //ทำเข้าประตูตนเอง

                            String sum_substituted_on = elesContent1Last.select(".data.league_club_keeper.sum").get(2).text();  
                            jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);                                  //เปลี่ยนตัวเข้า

                            String sum_substituted_off = elesContent1Last.select(".data.league_club_keeper.sum").get(3).text();  
                            jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);                                //เปลี่ยนตัวออก

                            String sum_yellow = elesContent1Last.select(".data.league_club_keeper.sum").get(4).text();  
                            jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);                                            //ใบเหลือง

                            String sum_yellow_red = elesContent1Last.select(".data.league_club_keeper.sum").get(5).text();  
                            jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);                                    //ใบเหลือง/ใบเเดง

                            String sum_red = elesContent1Last.select(".data.league_club_keeper.sum").get(6).text();  
                            jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);                                                  //ใบแดง

                            String sum_conceded = elesContent1Last.select(".data.league_club_keeper.sum").get(7).text();  
                            jsonPlayedLeagueDetail.put("sum_goals_conceded", sum_conceded);                                        //เสียประตู

                            String sum_shutout = elesContent1Last.select(".data.league_club_keeper.sum").get(8).text();  
                            jsonPlayedLeagueDetail.put("sum_clean_sheets", sum_shutout);                                           //คลีนชีท

                            String sum_time = elesContent1Last.select(".time.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);                                            //ลงเล่น (เวลา:นาที)

                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            json.put("played_league_detail", arrPlayedLeagueDetail);

                            //ข้อมูลการลงเล่นฟุตบอลถ้วยระดับนานาชาติ
                            arrPlayedLeagueDetail = new JSONArray();
                            Element elesTable2 = docpd.select(".data_played-full").last();
                            Elements elesContent2 = elesTable2.select(".content");
                            int maxContent2 = 0;
                            for (Element eleDataContent : elesContent2) {  // นับจำนวน class content 
                                if (eleDataContent.hasClass("content")) {
                                    maxContent2++;
                                }
                            }

                            for (int i = 0; i < (maxContent2 - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                                jsonPlayedLeagueDetail = new JSONObject();

                                Element eleContent1 = elesTable2.select(".content").get(i);  //index แรกเริ่มจาก 0
                                String season = eleContent1.select(".season").text();  
                                jsonPlayedLeagueDetail.put("season", season);                                              //ฤดูกาล

                                String competition = eleContent1.select(".competition").text();  
                                jsonPlayedLeagueDetail.put("competition", competition);                                    //รายการแข่งขัน

                                String club = eleContent1.select(".club").text();  
                                jsonPlayedLeagueDetail.put("club", club);                                                  //ทีมสโมสร

                                String matches = eleContent1.select(".matches.league_club_keeper").text();  
                                jsonPlayedLeagueDetail.put("matches", matches);                                            //ลงเล่น(แมตซ์)

                                String goals = eleContent1.select(".data.league_club_keeper").get(0).text();  
                                jsonPlayedLeagueDetail.put("goals", goals);                                                //ทำประตู

                                String own_goals = eleContent1.select(".data.league_club_keeper").get(1).text();  
                                jsonPlayedLeagueDetail.put("own_goals", own_goals);                                        //ทำเข้าประตูตนเอง

                                String substituted_on = eleContent1.select(".data.league_club_keeper").get(2).text();  
                                jsonPlayedLeagueDetail.put("substituted_on", substituted_on);                              //เปลี่ยนตัวเข้า

                                String substituted_off = eleContent1.select(".data.league_club_keeper").get(3).text();  
                                jsonPlayedLeagueDetail.put("substituted_off", substituted_off);                            //เปลี่ยนตัวออก

                                String yellow = eleContent1.select(".data.league_club_keeper").get(4).text();  
                                jsonPlayedLeagueDetail.put("yellow_cards", yellow);                                        //ใบเหลือง

                                String yellow_red = eleContent1.select(".data.league_club_keeper").get(5).text();  
                                jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);                                //ใบเหลือง/ใบเเดง

                                String red = eleContent1.select(".data.league_club_keeper").get(6).text();  
                                jsonPlayedLeagueDetail.put("red_cards", red);                                              //ใบแดง

                                String conceded = eleContent1.select(".data.league_club_keeper").get(7).text();  
                                jsonPlayedLeagueDetail.put("goals_conceded", conceded);                                    //เสียประตู

                                String shutout = eleContent1.select(".data.league_club_keeper").get(8).text();  
                                jsonPlayedLeagueDetail.put("clean_sheets", shutout);                                       //คลีนชีท

                                String time = eleContent1.select(".time").text();  
                                jsonPlayedLeagueDetail.put("minutes_played", time);                                        //ลงเล่น (เวลา:นาที)

                                arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            }
                            // class content สุดท้าย
                            jsonPlayedLeagueDetail = new JSONObject();

                            Element elesContent1Last2 = elesTable2.select(".content").last();
                            String sum_matches2 = elesContent1Last2.select(".matches.league_club_keeper.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_matches", sum_matches2);                                               //ลงเล่น(แมตซ์)

                            String sum_goals2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(0).text();  
                            jsonPlayedLeagueDetail.put("sum_goals", sum_goals2);                                                   //ทำประตู

                            String sum_own_goals2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(1).text();  
                            jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals2);                                           //ทำเข้าประตูตนเอง

                            String sum_substituted_on2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(2).text();  
                            jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on2);                                 //เปลี่ยนตัวเข้า

                            String sum_substituted_off2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(3).text();  
                            jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off2);                               //เปลี่ยนตัวออก

                            String sum_yellow2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(4).text();  
                            jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow2);                                           //ใบเหลือง

                            String sum_yellow_red2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(5).text();  
                            jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red2);                                   //ใบเหลือง/ใบเเดง

                            String sum_red2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(6).text();  
                            jsonPlayedLeagueDetail.put("sum_red_cards", sum_red2);                                                 //ใบแดง

                            String sum_conceded2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(7).text();  
                            jsonPlayedLeagueDetail.put("sum_goals_conceded", sum_conceded2);                                       //เสียประตู

                            String sum_shutout2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(8).text();  
                            jsonPlayedLeagueDetail.put("sum_clean_sheets", sum_shutout2);                                          //คลีนชีท

                            String sum_time2 = elesContent1Last2.select(".time.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time2);                                           //ลงเล่น (เวลา:นาที)

                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            json.put("played_international_league_detail", arrPlayedLeagueDetail);
                        }
                    }
                    //ข้อมูลการลงเล่นทีมชาติ
                    if ("nationalteam".equals(arrStr[1])) {
                        arrPlayedLeagueDetail = new JSONArray();
                        String nationalteam = url + "/" + arrStr[1];
                        Document docNt = Jsoup.connect(nationalteam).timeout(60 * 1000).get();
                        Elements elesData = docNt.select(".data_played-full");
                        Elements elesContentNt = elesData.select(".content");
                        int maxContent = 0;
                        for (Element eleDataContent : elesContentNt) {  // นับจำนวน class content 
                            if (eleDataContent.hasClass("content")) {
                                maxContent++;
                            }
                        }

                        for (int i = 0; i < (maxContent - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                            jsonPlayedLeagueDetail = new JSONObject();
                            Element eleContent1 = elesData.select(".content").get(i);  //index แรกเริ่มจาก 0
                            String competition = eleContent1.select(".competition.national").text();  
                            jsonPlayedLeagueDetail.put("competition", competition);                          //รายการแข่งขัน

                            String matches = eleContent1.select(".matches").text();  
                            jsonPlayedLeagueDetail.put("matches", matches);                                  //ลงเล่น(แมตซ์)

                            String goals = eleContent1.select(".data.keeper").get(0).text();  
                            jsonPlayedLeagueDetail.put("goals", goals);                                      //ทำประตู 

                            String own_goals = eleContent1.select(".data.keeper").get(1).text();  
                            jsonPlayedLeagueDetail.put("own_goals", own_goals);                              //ทำเข้าประตูตนเอง

                            String substituted_on = eleContent1.select(".data.keeper").get(2).text();  
                            jsonPlayedLeagueDetail.put("substituted_on", substituted_on);                    //เปลี่ยนตัวเข้า

                            String substituted_off = eleContent1.select(".data.keeper").get(3).text();  
                            jsonPlayedLeagueDetail.put("substituted_off", substituted_off);                  //เปลี่ยนตัวออก

                            String yellow = eleContent1.select(".data.keeper").get(4).text();  
                            jsonPlayedLeagueDetail.put("yellow_cards", yellow);                              //ใบเหลือง  

                            String yellow_red = eleContent1.select(".data.keeper").get(5).text();  
                            jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);                      //ใบเหลือง/ใบเเดง

                            String red = eleContent1.select(".data.keeper").get(6).text();  
                            jsonPlayedLeagueDetail.put("red_cards", red);                                    //ใบแดง

                            String conceded = eleContent1.select(".data.keeper").get(7).text();  
                            jsonPlayedLeagueDetail.put("goals_conceded", conceded);                          //เสียประตู

                            String shutout = eleContent1.select(".data.keeper").get(8).text();  
                            jsonPlayedLeagueDetail.put("clean_sheets", shutout);                             //คลีนชีท

                            String time = eleContent1.select(".time.keeper").text();  
                            jsonPlayedLeagueDetail.put("minutes_played", time);                              //ลงเล่น (เวลา:นาที)
                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        }
                        // class content สุดท้าย
                        jsonPlayedLeagueDetail = new JSONObject();
                        Element elesContent1Last = elesData.select(".content").last();
                        String sum_matches = elesContent1Last.select(".matches.sum").text();  
                        jsonPlayedLeagueDetail.put("sum_matches", sum_matches);                                   //ลงเล่น(แมตช์)

                        String sum_goals = elesContent1Last.select(".data.keeper.sum").get(0).text();  
                        jsonPlayedLeagueDetail.put("sum_goals", sum_goals);                                       //ทำประตู

                        String sum_own_goals = elesContent1Last.select(".data.keeper.sum").get(1).text();  
                        jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);                               //ทำเข้าประตูตนเอง

                        String sum_substituted_on = elesContent1Last.select(".data.keeper.sum").get(2).text();  
                        jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);                     //เปลี่ยนตัวเข้า

                        String sum_substituted_off = elesContent1Last.select(".data.keeper.sum").get(3).text();  
                        jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);                   //เปลี่ยนตัวออก

                        String sum_yellow = elesContent1Last.select(".data.keeper.sum").get(4).text();  
                        jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);                               //ใบเหลือง

                        String sum_yellow_red = elesContent1Last.select(".data.keeper.sum").get(5).text();  
                        jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);                       //ใบเหลือง/ใบเเดง

                        String sum_red = elesContent1Last.select(".data.keeper.sum").get(6).text();  
                        jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);                                    //ใบแดง

                        String sum_conceded = elesContent1Last.select(".data.keeper.sum").get(7).text();  
                        jsonPlayedLeagueDetail.put("sum_goals_conceded", sum_conceded);                          //เสียประตู

                        String sum_shutout = elesContent1Last.select(".data.keeper.sum").get(8).text();  
                        jsonPlayedLeagueDetail.put("sum_clean_sheets", sum_shutout);                             //คลีนชีท

                        String sum_time = elesContent1Last.select(".time.keeper.sum").text();  
                        jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);                              //ลงเล่น (เวลา:นาที)

                        arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        json.put("played_national_detail", arrPlayedLeagueDetail);
                    }
                }
                //จบกรณีผู้รักษาประตู 
                //กรณีผู้เล่นอื่นๆ   
            } else {
                arrPlayedLeagueDetail = new JSONArray();
                Elements elesDataPlayer = elementsContent.select(".data_played");
                Elements elesFoot = elesDataPlayer.select(".foot");
                for (Element ele : elesFoot) {
                    Element eleImgFoot = ele.select("a").first();
                    String imgData = eleImgFoot.attr("href");
                    String[] arrStr = imgData.split("/");
                    //ข้อมูลการลงเล่นฟุตบอลลีกและถ้วยต่างๆ
                    if ("performance-detail".equals(arrStr[1])) {
                        String performanceDetail = url + "/" + arrStr[1];
                        Document docpd = Jsoup.connect(performanceDetail).timeout(60 * 1000).get();
                        Elements data = docpd.select(".data_played-full");
                        int countData = 0;
                        for (Element eleData : data) {  // นับจำนวน class data_played-full
                            if (eleData.hasClass("data_played-full")) {
                                countData++;
                            }
                        }
                        if (countData == 1) {  // มีตารางเดียว
                            Element elesTable1 = docpd.select(".data_played-full").first();
                            Elements elesContent1 = elesTable1.select(".content");
                            int maxContent = 0;
                            for (Element eleDataContent : elesContent1) {  // นับจำนวน class content 
                                if (eleDataContent.hasClass("content")) {
                                    maxContent++;
                                }
                            }
                            for (int i = 0; i < (maxContent - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                                jsonPlayedLeagueDetail = new JSONObject();
                                Element eleContent1 = elesTable1.select(".content").get(i);  //index แรกเริ่มจาก 0
                                String season = eleContent1.select(".season").text();  
                                jsonPlayedLeagueDetail.put("season", season);                                     //ฤดูกาล

                                String competition = eleContent1.select(".competition").text();  
                                jsonPlayedLeagueDetail.put("competition", competition);                           //รายการแข่งขัน

                                String club = eleContent1.select(".club").text();  
                                jsonPlayedLeagueDetail.put("club", club);                                         //ทีมสโมสร

                                String matches = eleContent1.select(".matches.league_club").text();  
                                jsonPlayedLeagueDetail.put("matches", matches);                                   //ลงเล่น(แมตซ์)

                                String goals = eleContent1.select(".data.league_club").get(0).text();  
                                jsonPlayedLeagueDetail.put("goals", goals);                                       //ทำประตู

                                String assists = eleContent1.select(".data.league_club").get(1).text();  
                                jsonPlayedLeagueDetail.put("assists", assists);                                   //แอสซิสต์

                                String own_goals = eleContent1.select(".data.league_club").get(2).text();  
                                jsonPlayedLeagueDetail.put("own_goals", own_goals);                               //ทำเข้าประตูตนเอง

                                String substituted_on = eleContent1.select(".data.league_club").get(3).text();  
                                jsonPlayedLeagueDetail.put("substituted_on", substituted_on);                     //เปลี่ยนตัวเข้า

                                String substituted_off = eleContent1.select(".data.league_club").get(4).text();  
                                jsonPlayedLeagueDetail.put("substituted_off", substituted_off);                   //เปลี่ยนตัวออก 

                                String yellow = eleContent1.select(".data.league_club").get(5).text();  
                                jsonPlayedLeagueDetail.put("yellow_cards", yellow);                               //ใบเหลือง

                                String yellow_red = eleContent1.select(".data.league_club").get(6).text();  
                                jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);                       //ใบเหลือง/ใบเเดง

                                String red = eleContent1.select(".data.league_club").get(7).text();  
                                jsonPlayedLeagueDetail.put("red_cards", red);                                     //ใบเเดง

                                String penalty_goals = eleContent1.select(".data.league_club").get(8).text();  
                                jsonPlayedLeagueDetail.put("penalty_goals", penalty_goals);                       //ทำประตู(จุดโทษ)

                                String mpg = eleContent1.select(".mpg").text();  
                                jsonPlayedLeagueDetail.put("minutes_played_goals", mpg);                          //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)

                                String time = eleContent1.select(".time").text();  
                                jsonPlayedLeagueDetail.put("minutes_played", time);                               //ลงเล่น (เวลา:นาที)

                                arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            }
                            // class content สุดท้าย
                            jsonPlayedLeagueDetail = new JSONObject();
                            Element elesContent1Last = elesTable1.select(".content").last();
                            String sum_matches = elesContent1Last.select(".matches.league_club.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_matches", sum_matches);                                       //ลงเล่น(แมตซ์)

                            String sum_goals = elesContent1Last.select(".data.league_club.sum").get(0).text();  
                            jsonPlayedLeagueDetail.put("sum_goals", sum_goals);                                           //ทำประตู

                            String sum_assists = elesContent1Last.select(".data.league_club.sum").get(1).text();  
                            jsonPlayedLeagueDetail.put("sum_assists", sum_assists);                                       //แอสซิสต์

                            String sum_own_goals = elesContent1Last.select(".data.league_club.sum").get(2).text();  
                            jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);                                   //ทำเข้าประตูตนเอง

                            String sum_substituted_on = elesContent1Last.select(".data.league_club.sum").get(3).text();  
                            jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);                         //เปลี่ยนตัวเข้า

                            String sum_substituted_off = elesContent1Last.select(".data.league_club.sum").get(4).text();  
                            jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);                       //เปลี่ยนตัวออก 

                            String sum_yellow = elesContent1Last.select(".data.league_club.sum").get(5).text();  
                            jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);                                   //ใบเหลือง

                            String sum_yellow_red = elesContent1Last.select(".data.league_club.sum").get(6).text();  
                            jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);                           //ใบเหลือง/ใบเเดง

                            String sum_red = elesContent1Last.select(".data.league_club.sum").get(7).text();  
                            jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);                                         //ใบเเดง

                            String sum_penalty_goals = elesContent1Last.select(".data.league_club.sum").get(8).text();  
                            jsonPlayedLeagueDetail.put("sum_penalty_goals", sum_penalty_goals);                           //ทำประตู(จุดโทษ)

                            String sum_mpg = elesContent1Last.select(".mpg.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_minutes_played_goals", sum_mpg);                              //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)

                            String sum_time = elesContent1Last.select(".time.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);                                   //ลงเล่น (เวลา:นาที)

                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            json.put("played_league_detail", arrPlayedLeagueDetail);
                        }
                        if (countData == 2) {  // มี 2 ตาราง
                            arrPlayedLeagueDetail = new JSONArray();
                            Element elesTable1 = docpd.select(".data_played-full").first();
                            Elements elesContent1 = elesTable1.select(".content");
                            int maxContent = 0;
                            for (Element eleDataContent : elesContent1) {  // นับจำนวน class content 
                                if (eleDataContent.hasClass("content")) {
                                    maxContent++;
                                }
                            }

                            for (int i = 0; i < (maxContent - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                                jsonPlayedLeagueDetail = new JSONObject();
                                Element eleContent1 = elesTable1.select(".content").get(i);  //index แรกเริ่มจาก 0
                                String season = eleContent1.select(".season").text();  
                                jsonPlayedLeagueDetail.put("season", season);                                        //ฤดูกาล

                                String competition = eleContent1.select(".competition").text();  
                                jsonPlayedLeagueDetail.put("competition", competition);                              //รายการแข่งขัน

                                String club = eleContent1.select(".club").text();  
                                jsonPlayedLeagueDetail.put("club", club);                                            //ทีมสโมสร

                                String matches = eleContent1.select(".matches.league_club").text();  
                                jsonPlayedLeagueDetail.put("matches", matches);                                      //ลงเล่น(แมตซ์)

                                String goals = eleContent1.select(".data.league_club").get(0).text();   
                                jsonPlayedLeagueDetail.put("goals", goals);                                          //ทำประตู

                                String assists = eleContent1.select(".data.league_club").get(1).text();  
                                jsonPlayedLeagueDetail.put("assists", assists);                                      //แอสซิสต์

                                String own_goals = eleContent1.select(".data.league_club").get(2).text();  
                                jsonPlayedLeagueDetail.put("own_goals", own_goals);                                  //ทำเข้าประตูตนเอง

                                String substituted_on = eleContent1.select(".data.league_club").get(3).text();  
                                jsonPlayedLeagueDetail.put("substituted_on", substituted_on);                        //เปลี่ยนตัวเข้า

                                String substituted_off = eleContent1.select(".data.league_club").get(4).text();  
                                jsonPlayedLeagueDetail.put("substituted_off", substituted_off);                      //เปลี่ยนตัวออก

                                String yellow = eleContent1.select(".data.league_club").get(5).text();  
                                jsonPlayedLeagueDetail.put("yellow_cards", yellow);                                  //ใบเหลือง

                                String yellow_red = eleContent1.select(".data.league_club").get(6).text();  
                                jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);                          //ใบเหลือง/ใบเเดง

                                String red = eleContent1.select(".data.league_club").get(7).text();  
                                jsonPlayedLeagueDetail.put("red_cards", red);                                        //ใบเเดง

                                String penalty_goals = eleContent1.select(".data.league_club").get(8).text();  
                                jsonPlayedLeagueDetail.put("penalty_goals", penalty_goals);                          //ทำประตู(จุดโทษ)

                                String mpg = eleContent1.select(".mpg").text();  
                                jsonPlayedLeagueDetail.put("minutes_played_goals", mpg);                             //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)

                                String time = eleContent1.select(".time").text();  
                                jsonPlayedLeagueDetail.put("minutes_played", time);                                  //ลงเล่น (เวลา:นาที)
                                arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            }
                            // class content สุดท้าย
                            jsonPlayedLeagueDetail = new JSONObject();
                            Element elesContent1Last = elesTable1.select(".content").last();
                            String sum_matches = elesContent1Last.select(".matches.league_club.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_matches", sum_matches);                                       //ลงเล่น(แมตซ์)

                            String sum_goals = elesContent1Last.select(".data.league_club.sum").get(0).text();  
                            jsonPlayedLeagueDetail.put("sum_goals", sum_goals);                                           //ทำประตู

                            String sum_assists = elesContent1Last.select(".data.league_club.sum").get(1).text();  
                            jsonPlayedLeagueDetail.put("sum_assists", sum_assists);                                       //แอสซิสต์

                            String sum_own_goals = elesContent1Last.select(".data.league_club.sum").get(2).text();  
                            jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);                                   //ทำเข้าประตูตนเอง

                            String sum_substituted_on = elesContent1Last.select(".data.league_club.sum").get(3).text();  
                            jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);                         //เปลี่ยนตัวเข้า

                            String sum_substituted_off = elesContent1Last.select(".data.league_club.sum").get(4).text();  
                            jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);                       //เปลี่ยนตัวออก

                            String sum_yellow = elesContent1Last.select(".data.league_club.sum").get(5).text();  
                            jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);                                   //ใบเหลือง

                            String sum_yellow_red = elesContent1Last.select(".data.league_club.sum").get(6).text();  
                            jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);                           //ใบเหลือง/ใบเเดง

                            String sum_red = elesContent1Last.select(".data.league_club.sum").get(7).text();  
                            jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);                                         //ใบเเดง

                            String sum_penalty_goals = elesContent1Last.select(".data.league_club.sum").get(8).text();  
                            jsonPlayedLeagueDetail.put("sum_penalty_goals", sum_penalty_goals);                           //ทำประตู(จุดโทษ)

                            String sum_mpg = elesContent1Last.select(".mpg.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_minutes_played_goals", sum_mpg);                              //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)

                            String sum_time = elesContent1Last.select(".time.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);                                   //ลงเล่น (เวลา:นาที)
                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            json.put("played_league_detail", arrPlayedLeagueDetail);

                            //ข้อมูลการลงเล่นฟุตบอลถ้วยระดับนานาชาติ
                            arrPlayedLeagueDetail = new JSONArray();
                            Element elesTable2 = docpd.select(".data_played-full").last();
                            Elements elesContent2 = elesTable2.select(".content");
                            int maxContent2 = 0;
                            for (Element eleDataContent : elesContent2) {  // นับจำนวน class content 
                                if (eleDataContent.hasClass("content")) {
                                    maxContent2++;
                                }
                            }

                            for (int i = 0; i < (maxContent2 - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                                jsonPlayedLeagueDetail = new JSONObject();
                                Element eleContent1 = elesTable2.select(".content").get(i);  //index แรกเริ่มจาก 0
                                String season = eleContent1.select(".season").text();  
                                jsonPlayedLeagueDetail.put("season", season);                                                  //ฤดูกาล

                                String competition = eleContent1.select(".competition").text();  
                                jsonPlayedLeagueDetail.put("competition", competition);                                        //รายการแข่งขัน

                                String club = eleContent1.select(".club").text();  
                                jsonPlayedLeagueDetail.put("club", club);                                                      //ทีมสโมสร

                                String matches = eleContent1.select(".matches.league_club").text();  
                                jsonPlayedLeagueDetail.put("matches", matches);                                                //ลงเล่น(แมตซ์)

                                String goals = eleContent1.select(".data.league_club").get(0).text(); 
                                jsonPlayedLeagueDetail.put("goals", goals);                                                    //ทำประตู

                                String assists = eleContent1.select(".data.league_club").get(1).text();  
                                jsonPlayedLeagueDetail.put("assists", assists);                                                //แอสซิสต์

                                String own_goals = eleContent1.select(".data.league_club").get(2).text();  
                                jsonPlayedLeagueDetail.put("own_goals", own_goals);                                            //ทำเข้าประตูตนเอง

                                String substituted_on = eleContent1.select(".data.league_club").get(3).text(); 
                                jsonPlayedLeagueDetail.put("substituted_on", substituted_on);                                  //เปลี่ยนตัวเข้า

                                String substituted_off = eleContent1.select(".data.league_club").get(4).text();  
                                jsonPlayedLeagueDetail.put("substituted_off", substituted_off);                                //เปลี่ยนตัวออก 

                                String yellow = eleContent1.select(".data.league_club").get(5).text();  
                                jsonPlayedLeagueDetail.put("yellow_cards", yellow);                                            //ใบเหลือง

                                String yellow_red = eleContent1.select(".data.league_club").get(6).text();  
                                jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);                                    //ใบเหลือง/ใบเเดง

                                String red = eleContent1.select(".data.league_club").get(7).text();  
                                jsonPlayedLeagueDetail.put("red_cards", red);                                                  //ใบเเดง

                                String penalty_goals = eleContent1.select(".data.league_club").get(8).text();  
                                jsonPlayedLeagueDetail.put("penalty_goals", penalty_goals);                                    //ทำประตู(จุดโทษ)

                                String mpg = eleContent1.select(".mpg").text();  
                                jsonPlayedLeagueDetail.put("minutes_played_goals", mpg);                                       //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)

                                String time = eleContent1.select(".time").text();  
                                jsonPlayedLeagueDetail.put("minutes_played", time);                                            //ลงเล่น (เวลา:นาที)

                                arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            }
                            // class content สุดท้าย
                            jsonPlayedLeagueDetail = new JSONObject();
                            Element elesContent1Last2 = elesTable2.select(".content").last();
                            String sum_matches2 = elesContent1Last2.select(".matches.league_club.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_matches", sum_matches2);                                           //ลงเล่น(แมตซ์)

                            String sum_goals2 = elesContent1Last2.select(".data.league_club.sum").get(0).text();  
                            jsonPlayedLeagueDetail.put("sum_goals", sum_goals2);                                               //ทำประตู

                            String sum_assists2 = elesContent1Last2.select(".data.league_club.sum").get(1).text();  
                            jsonPlayedLeagueDetail.put("sum_assists", sum_assists2);                                           //แอสซิสต์

                            String sum_own_goals2 = elesContent1Last2.select(".data.league_club.sum").get(2).text();  
                            jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals2);                                       //ทำเข้าประตูตนเอง

                            String sum_substituted_on2 = elesContent1Last2.select(".data.league_club.sum").get(3).text();  
                            jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on2);                             //เปลี่ยนตัวเข้า

                            String sum_substituted_off2 = elesContent1Last2.select(".data.league_club.sum").get(4).text();  
                            jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off2);                           //เปลี่ยนตัวออก 

                            String sum_yellow2 = elesContent1Last2.select(".data.league_club.sum").get(5).text();   
                            jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow2);                                       //ใบเหลือง

                            String sum_yellow_red2 = elesContent1Last2.select(".data.league_club.sum").get(6).text();  
                            jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red2);                               //ใบเหลือง/ใบเเดง

                            String sum_red2 = elesContent1Last2.select(".data.league_club.sum").get(7).text();  
                            jsonPlayedLeagueDetail.put("sum_red_cards", sum_red2);                                             //ใบเเดง

                            String sum_penalty_goals2 = elesContent1Last2.select(".data.league_club.sum").get(8).text();  
                            jsonPlayedLeagueDetail.put("sum_penalty_goals", sum_penalty_goals2);                               //ทำประตู(จุดโทษ)

                            String sum_mpg2 = elesContent1Last2.select(".mpg.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_minutes_played_goals", sum_mpg2);                                  //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)

                            String sum_time2 = elesContent1Last2.select(".time.sum").text();  
                            jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time2);                                       //ลงเล่น (เวลา:นาที)

                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                            json.put("played_international_league_detail", arrPlayedLeagueDetail);
                        }
                    }
                    //ข้อมูลการลงเล่นทีมชาติ
                    if ("nationalteam".equals(arrStr[1])) {
                        arrPlayedLeagueDetail = new JSONArray();
                        String nationalteam = url + "/" + arrStr[1];
                        Document docNt = Jsoup.connect(nationalteam).timeout(60 * 1000).get();
                        Elements elesData = docNt.select(".data_played-full");
                        Elements elesContentNt = elesData.select(".content");
                        int maxContent = 0;
                        for (Element eleDataContent : elesContentNt) {  // นับจำนวน class content 
                            if (eleDataContent.hasClass("content")) {
                                maxContent++;
                            }
                        }

                        for (int i = 0; i < (maxContent - 1); i++) {  // ไม่เอาค่า class content  สุดท้าย (content - 1)
                            jsonPlayedLeagueDetail = new JSONObject();
                            Element eleContent1 = elesData.select(".content").get(i);  //index แรกเริ่มจาก 0                       
                            String competition = eleContent1.select(".competition.national").text();  
                            jsonPlayedLeagueDetail.put("competition", competition);                            //รายการแข่งขัน

                            String matches = eleContent1.select(".matches").text();  
                            jsonPlayedLeagueDetail.put("matches", matches);                                    //ลงเล่น(แมตซ์)

                            String goals = eleContent1.select(".data").get(0).text();  
                            jsonPlayedLeagueDetail.put("goals", goals);                                        //ทำประตู

                            String assists = eleContent1.select(".data").get(1).text();  
                            jsonPlayedLeagueDetail.put("assists", assists);                                    //แอสซิสต์

                            String own_goals = eleContent1.select(".data").get(2).text();  
                            jsonPlayedLeagueDetail.put("own_goals", own_goals);                                //ทำเข้าประตูตนเอง

                            String substituted_on = eleContent1.select(".data").get(3).text();  
                            jsonPlayedLeagueDetail.put("substituted_on", substituted_on);                      //เปลี่ยนตัวเข้า

                            String substituted_off = eleContent1.select(".data").get(4).text();  
                            jsonPlayedLeagueDetail.put("substituted_off", substituted_off);                    //เปลี่ยนตัวออก

                            String yellow = eleContent1.select(".data").get(5).text();  
                            jsonPlayedLeagueDetail.put("yellow_cards", yellow);                                //ใบเหลือง

                            String yellow_red = eleContent1.select(".data").get(6).text();  
                            jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);                        //ใบเหลือง/ใบเเดง

                            String red = eleContent1.select(".data").get(7).text();  
                            jsonPlayedLeagueDetail.put("red_cards", red);                                      //ใบเเดง

                            String penalty_goals = eleContent1.select(".data").get(8).text();  
                            jsonPlayedLeagueDetail.put("penalty_goals", penalty_goals);                        //ทำประตู(จุดโทษ)

                            String mpg = eleContent1.select(".mpg").text();  
                            jsonPlayedLeagueDetail.put("minutes_played_goals", mpg);                           //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)

                            String time = eleContent1.select(".time").text();  
                            jsonPlayedLeagueDetail.put("minutes_played", time);                                //ลงเล่น (เวลา:นาที)

                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        }
                        // class content สุดท้าย
                        jsonPlayedLeagueDetail = new JSONObject();
                        Element elesContent1Last = elesData.select(".content").last();
                        String sum_matches = elesContent1Last.select(".matches.sum").text();  
                        jsonPlayedLeagueDetail.put("sum_matches", sum_matches);                           //ลงเล่น(แมตซ์)

                        String sum_goals = elesContent1Last.select(".data.sum").get(0).text();  
                        jsonPlayedLeagueDetail.put("sum_goals", sum_goals);                               //ทำประตู

                        String sum_assists = elesContent1Last.select(".data.sum").get(1).text();  
                        jsonPlayedLeagueDetail.put("sum_assists", sum_assists);                           //แอสซิสต์

                        String sum_own_goals = elesContent1Last.select(".data.sum").get(2).text();  
                        jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);                       //ทำเข้าประตูตนเอง

                        String sum_substituted_on = elesContent1Last.select(".data.sum").get(3).text();  
                        jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);             //เปลี่ยนตัวเข้า

                        String sum_substituted_off = elesContent1Last.select(".data.sum").get(4).text();  
                        jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);           //เปลี่ยนตัวออก 

                        String sum_yellow = elesContent1Last.select(".data.sum").get(5).text();  
                        jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);                       //ใบเหลือง

                        String sum_yellow_red = elesContent1Last.select(".data.sum").get(6).text();  
                        jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);               //ใบเหลือง/ใบเเดง

                        String sum_red = elesContent1Last.select(".data.sum").get(7).text();  
                        jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);                             //ใบเเดง

                        String sum_penalty_goals = elesContent1Last.select(".data.sum").get(8).text();  
                        jsonPlayedLeagueDetail.put("sum_penalty_goals", sum_penalty_goals);               //ทำประตู(จุดโทษ)

                        String sum_mpg = elesContent1Last.select(".mpg.sum").text();  
                        jsonPlayedLeagueDetail.put("sum_minutes_played_goals", sum_mpg);                  //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)

                        String sum_time = elesContent1Last.select(".time.sum").text();  
                        jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);                       //ลงเล่น (เวลา:นาที)

                        arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        json.put("played_national_detail", arrPlayedLeagueDetail);
                    }
                }
            }
            els.inputElasticsearch(json.toString(), "player_profile_premierleague");
            System.out.println(dateTimes.thaiDateTime()+" : insert player profile premierleague complete");

        } catch (IOException | JSONException e) {
            e.getMessage();
        }

    }

}
