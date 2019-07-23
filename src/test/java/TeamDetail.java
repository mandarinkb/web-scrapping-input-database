
//import app.function.Elasticsearch;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
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

public class TeamDetail {

    public String baseLink = "http://www.livesoccer888.com";
    
    List<String> list = new ArrayList<>();
    List<String> listPlayer = new ArrayList<>();

    public String getNewLinkImage(String url) {
        url = url.replace("../../../..", "");
        url = url.replace("../..", "");
        url = url.replace("/..", "");
        url = url.replace("..", "");
        return url;
    }
    String statsOfTeamEnKey(String inputKey) {
        String key = "";
        if ("จำนวนนักเตะ".equals(inputKey)) {
            key = "players";
        }
        if ("ทำประตู".equals(inputKey)) {
            key = "goals";
        }
        if ("ทำแอสซิสต์".equals(inputKey)) {
            key = "assists";
        }
        if ("คลีนชีท".equals(inputKey)) {
            key = "clean_sheets";
        }
        if ("ใบเหลือง".equals(inputKey)) {
            key = "yellow_cards";
        }
        if ("ใบเหลืองแดง".equals(inputKey)) {
            key = "yellow_red_cards";
        }
        if ("ใบแดง".equals(inputKey)) {
            key = "red_cards";
        }
        return key;
    }
    //สถิติต่างๆของสโมสรฟุตบอล
    public void statsOfTeam(String url) throws IOException, InterruptedException {
        JSONObject json;
        //String BaseLinkPalyer = "http://www.livesoccer888.com/thaipremierleague";
        String BaseLinkPalyer = "http://www.livesoccer888.com/premierleague";
        Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
        Elements elements = doc.getElementsByClass("PlayerLeague");
        for (Element ele : elements) {
            json = new JSONObject();
            json.put("link",url);
            Elements elesLogo = ele.getElementsByClass("logo");
            if (elesLogo.hasClass("logo")) {
                Elements elesChild = elesLogo.select("*");
                for (Element eleLogo : elesChild) {
                    if (eleLogo.tagName().equals("img")) {
                        Element eleImg = eleLogo.select("img").first();
                        String img = eleImg.attr("src");
                        img = baseLink + getNewLinkImage(img);
                        json.put("logo_team",img);
                        //System.out.println(img);
                    }
                }
            }
            String team = ele.getElementsByClass("team").text();
            json.put("team",team);
            //System.out.println(team);

            Elements elesUl = ele.select("ul");
            for (Element eleChildren : elesUl) {
                Elements elesLi = eleChildren.select("li");
                Elements elesChildren = elesLi.select("*");
                for (Element eleLiAll : elesChildren) {
                    if (eleLiAll.hasClass("player")) {
                        String player = eleLiAll.select(".player").text();  //จำนวนนักเตะ
                        player = statsOfTeamEnKey(player);
                        String point = eleLiAll.nextElementSibling().text();  //ค่าจำนวนนักเตะ                       
                        //System.out.println(player+" : "+point);
                        json.put(player,point);
                    }

                    if (eleLiAll.hasClass("goal")) {
                        String goal = eleLiAll.select(".goal").text();  //ทำประตู
                        goal = statsOfTeamEnKey(goal);
                        String point = eleLiAll.nextElementSibling().text();  //จำนวนทำประตู
                        //System.out.println(goal+" : "+point);
                        json.put(goal,point);
                    }
                    if (eleLiAll.hasClass("assist")) {
                        String assist = eleLiAll.select(".assist").text();  //ทำแอสซิสต์
                        assist = statsOfTeamEnKey(assist);
                        String point = eleLiAll.nextElementSibling().text();  //จำนวนทำแอสซิสต์
                        //System.out.println(assist+" : "+point);
                        json.put(assist,point);
                    }
                    if (eleLiAll.hasClass("shutout")) {
                        String shutout = eleLiAll.select(".shutout").text();  //คลีนชีท
                        shutout = statsOfTeamEnKey(shutout);
                        String point = eleLiAll.nextElementSibling().text();  //จำนวนคลีนชีท
                        //System.out.println(shutout+" : "+point);
                        json.put(shutout,point);
                    }
                    if (eleLiAll.hasClass("yellow")) {
                        String yellow = eleLiAll.select(".yellow").text();  //ใบเหลือง
                        yellow = statsOfTeamEnKey(yellow);
                        String point = eleLiAll.nextElementSibling().text();  //จำนวนใบเหลือง
                        //System.out.println(yellow+" : "+point);
                        json.put(yellow,point);
                        
                    }
                    if (eleLiAll.hasClass("yellowred")) {
                        String yellowred = eleLiAll.select(".yellowred").text();  //ใบเหลืองแดง
                        yellowred = statsOfTeamEnKey(yellowred);
                        String point = eleLiAll.nextElementSibling().text();  //จำนวนใบเหลืองแดง
                        //System.out.println(yellowred+" : "+point);
                        json.put(yellowred,point);
                        
                    }
                    if (eleLiAll.hasClass("red")) {
                        String red = eleLiAll.select(".red").text();  //ใบแดง
                        red = statsOfTeamEnKey(red);
                        String point = eleLiAll.nextElementSibling().text();  //จำนวนใบแดง
                        //System.out.println(red+" : "+point);  
                        json.put(red,point);

                    }

                    if (eleLiAll.tagName().equals("a")) {       // ดูนักเตะทั้งหมด
                        Element eleA = eleLiAll.select("a").first();
                        String linkPlayers = eleA.attr("href");
                        linkPlayers = getNewLinkImage(linkPlayers);                       
                        linkPlayers = BaseLinkPalyer + linkPlayers;
                        
                        System.out.println(linkPlayers);
                        json.put("link_detail",linkPlayers);
                        list.add(linkPlayers);
                    }        
                }
                //System.out.println(json.toString());
                //System.out.println("");
            }
        }
    }

    //ผู้เล่นนักเตะทีม...
    public void teamDetail(String url) throws IOException, InterruptedException {
        JSONObject json = new JSONObject();
        JSONObject jsonDetail;
        JSONArray arr;
        
        json.put("link",url);
        Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
        Elements elesLogo = doc.getElementsByClass("logo_team_player");
        Element eleImgTeam = elesLogo.select("img").get(0);
        String imgTeam = eleImgTeam.attr("src");
        imgTeam = baseLink + getNewLinkImage(imgTeam);
        json.put("logo_team",imgTeam);

        String team = eleImgTeam.attr("alt");
        team = team.replace("สโมสรฟุตบอลทีม", "");
        json.put("team",team);
         
        String title = "";
        Elements elements = doc.getElementsByClass("data_statistic_played");
        for (Element ele : elements) {
            arr  = new JSONArray();
            jsonDetail = new JSONObject();
            // top_player class
            //ผู้เล่นคนแรก
            Elements elesTopPly = ele.getElementsByClass("top_player");
            if (elesTopPly.hasClass("top_player")) {
                Elements elesBgGoal = elesTopPly.select(".title.bg_goal");
                
                // ทำประตูสูงสุด
                if (elesBgGoal.hasClass("bg_goal")) {
                    String goal = elesTopPly.select(".title.bg_goal").text();  // ทำประตูสูงสุด
                    title = goal;
                }
                Elements elesImgPlayer = elesTopPly.select(".image.ts");
                if (elesImgPlayer.hasClass("ts")) {
                    Element eleImg = elesImgPlayer.select("img").first();     //รูปนักเตะ
                    String img = eleImg.attr("src");
                    img = baseLink + getNewLinkImage(img);
                    jsonDetail.put("img_player",img);

                    String goals = elesTopPly.select(".stats.stat_goal.goals").text();  //จำนวนประตู
                    jsonDetail.put("goals",goals);

                    String name = elesTopPly.select(".name").text();     //ชื่อนักเตะ
                    jsonDetail.put("name",name);

                    String ranking = elesTopPly.select(".ranking").text();  //อันดับ
                    jsonDetail.put("ranking",ranking);
                }
                
                //ทำแอสซิสต์สุงสุด
                Elements elesBgAssist = elesTopPly.select(".title.bg_assist");
                if (elesBgAssist.hasClass("bg_assist")) {
                    String assist = elesTopPly.select(".title.bg_assist").text();
                    title = assist;
                }
                Elements elesImgPlayerAssist = elesTopPly.select(".image.ta");
                if (elesImgPlayerAssist.hasClass("ta")) {
                    Element eleImg = elesImgPlayerAssist.select("img").first();
                    String img = eleImg.attr("src");
                    img = baseLink + getNewLinkImage(img);
                    jsonDetail.put("img_player",img);
                    
                    String assist = elesTopPly.select(".stats.stat_assist.assists").text();
                    jsonDetail.put("assists",assist);

                    String name = elesTopPly.select(".name").text();
                    jsonDetail.put("name",name);

                    String ranking = elesTopPly.select(".ranking").text();
                    jsonDetail.put("ranking",ranking);
                }
                
                //ลงเล่นมากที่สุด
                Elements elesBgApp = elesTopPly.select(".title.bg_apps");
                if (elesBgApp.hasClass("bg_apps")) {
                    String app = elesTopPly.select(".title.bg_apps").text();
                    title = app;
                }
                Elements elesImgPlayerApp = elesTopPly.select(".image.tt");
                if (elesImgPlayerApp.hasClass("tt")) {
                    Element eleImg = elesImgPlayerApp.select("img").first();
                    String img = eleImg.attr("src");
                    img = baseLink + getNewLinkImage(img);
                    jsonDetail.put("img_player",img);

                    String mostPlay = elesTopPly.select(".stats.stat_apps.apps").text();
                    jsonDetail.put("minutes_played",mostPlay);
                    
                    String name = elesTopPly.select(".name").text();
                    jsonDetail.put("name",name);

                    String ranking = elesTopPly.select(".ranking").text();
                    jsonDetail.put("ranking",ranking);
                }
                arr.put(jsonDetail);
            }
            
            // ผู้เล่นคนถัดไป
            // next_player class            
            Elements elesNextPlayer = ele.select(".next_player");
            for (Element eleNextPlayer : elesNextPlayer) {
                jsonDetail = new JSONObject();
                String ranking = eleNextPlayer.select(".ranking").text();  //อันดับ
                jsonDetail.put("ranking",ranking);

                //ทำประตูสุงสุด
                Elements elesStatusTs = eleNextPlayer.select(".stats.ts");
                if (elesStatusTs.hasClass("ts")) {
                    String status = eleNextPlayer.select(".stats.ts").text();  //จำนวนประตู
                    jsonDetail.put("goals",status);
                }
                
                //ทำแอสซิสต์สุงสุด
                Elements elesStatusTa = eleNextPlayer.select(".stats.ta");
                if (elesStatusTa.hasClass("ta")) {
                    String status = eleNextPlayer.select(".stats.ta").text();
                    jsonDetail.put("assists",status);
                }
                
                //ลงเล่นมากที่สุด
                Elements elesStatusTt = eleNextPlayer.select(".stats.tt.double_width");
                if (elesStatusTt.hasClass("tt")) {
                    String status = eleNextPlayer.select(".stats.tt.double_width").text();
                    jsonDetail.put("minutes_played",status);
                }

                Elements elesImgPlayer = eleNextPlayer.select(".image");
                Element eleImg = elesImgPlayer.select("img").first();     //รูปนักเตะ
                String img = eleImg.attr("src");
                img = baseLink + getNewLinkImage(img);
                jsonDetail.put("img_player",img);

                String name = eleNextPlayer.select(".name").text();  //ชื่อนักเตะ
                jsonDetail.put("name",name);
                
                arr.put(jsonDetail);
            }
            title = titleToEnKey(title);
            json.put(title,arr);
        }
        
        // รายชื่อนักเตะ
        JSONObject jsonDetailPlayers ;
        JSONArray arrDetailPlayers  = new JSONArray();
        Elements elesDetailPlayer = doc.getElementsByClass("show-table");
        Element elesTr = elesDetailPlayer.select("tr").first();   // เลือก tr tag แรกสุด
        elesTr.remove();                                  // ลบ tr tag แรกสุด
        Element elesNext = elesDetailPlayer.select("tr").first(); // เลือก tr tag ที่ 2
        elesNext.remove();                                // ลบ tr tag ที่ 2

        Elements eles = elesDetailPlayer.select(".clickable-row");
        for (Element ele : eles) {
            jsonDetailPlayers = new JSONObject();
            String linkProfile = ele.attr("data-href");  // link profile
            linkProfile = baseLink + getNewLinkImage(linkProfile);
            jsonDetailPlayers.put("link_profile",linkProfile);

            String name = ele.attr("title");  //ชื่อ
            jsonDetailPlayers.put("name",name);

            String number = ele.select(".number_hide").text();  //เบอร์เสื้อ
            jsonDetailPlayers.put("squad_nember",number);

            Elements elesImgProfile = ele.getElementsByClass("img");  //link image profile
            if (elesImgProfile.hasClass("img")) {
                Elements elesChild = elesImgProfile.select("*");
                for (Element eleImgProfile : elesChild) {
                    if (eleImgProfile.tagName().equals("img")) {
                        Element eleImg = eleImgProfile.select("img").first();
                        String img = eleImg.attr("src");
                        img = baseLink + getNewLinkImage(img);
                        jsonDetailPlayers.put("img_player",img);
                    }
                }
            }

            String position = ele.getElementsByIndexEquals(3).text();   //ตำแหน่ง
            jsonDetailPlayers.put("position",position);

            Elements eleNationality = ele.select(".flag-icon");        //สัญชาติ
            String nationality = eleNationality.attr("title");
            jsonDetailPlayers.put("nationality",nationality);

            String age = ele.getElementsByIndexEquals(5).text();       //อายุ
            jsonDetailPlayers.put("age",age);
            arrDetailPlayers.put(jsonDetailPlayers); 
        }
        json.put("players_detail",arrDetailPlayers);
        
        inputElasticsearch(json.toString(), "team_datail_premierleague");
        //System.out.println(json.toString());
    }
/*
    public void playerOfTeam(String url) throws IOException, InterruptedException {
        JSONObject jsonPlayers = new JSONObject();
        JSONObject jsonDetailPlayers ;
        JSONArray arr  = new JSONArray();
        
        jsonPlayers.put("link",url);
        Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
        Elements elesLogo = doc.getElementsByClass("logo_team_player");
        Element eleImgTeam = elesLogo.select("img").get(0);
        String imgTeam = eleImgTeam.attr("src");
        imgTeam = baseLink + getNewLinkImage(imgTeam);
        jsonPlayers.put("logo_team",imgTeam);

        String team = eleImgTeam.attr("alt");
        team = team.replace("สโมสรฟุตบอลทีม", "");
        jsonPlayers.put("team",team);
        
        // รายชื่อนักเตะ
        Elements elements = doc.getElementsByClass("show-table");
        Element elesTr = elements.select("tr").first();   // เลือก tr tag แรกสุด
        elesTr.remove();                                  // ลบ tr tag แรกสุด
        Element elesNext = elements.select("tr").first(); // เลือก tr tag ที่ 2
        elesNext.remove();                                // ลบ tr tag ที่ 2

        Elements eles = elements.select(".clickable-row");
        for (Element ele : eles) {
            jsonDetailPlayers = new JSONObject();
            String linkProfile = ele.attr("data-href");  // link profile
            linkProfile = baseLink + getNewLinkImage(linkProfile);
            listPlayer.add(linkProfile);
            jsonDetailPlayers.put("link_profile",linkProfile);

            String name = ele.attr("title");  //ชื่อ
            jsonDetailPlayers.put("name",name);

            String number = ele.select(".number_hide").text();  //เบอร์เสื้อ
            jsonDetailPlayers.put("squad_nember",number);

            Elements elesImgProfile = ele.getElementsByClass("img");  //link image profile
            if (elesImgProfile.hasClass("img")) {
                Elements elesChild = elesImgProfile.select("*");
                for (Element eleImgProfile : elesChild) {
                    if (eleImgProfile.tagName().equals("img")) {
                        Element eleImg = eleImgProfile.select("img").first();
                        String img = eleImg.attr("src");
                        img = baseLink + getNewLinkImage(img);
                        jsonDetailPlayers.put("img_player",img);
                    }
                }
            }

            String position = ele.getElementsByIndexEquals(3).text();   //ตำแหน่ง
            jsonDetailPlayers.put("position",position);

            Elements eleNationality = ele.select(".flag-icon");        //สัญชาติ
            String nationality = eleNationality.attr("title");
            jsonDetailPlayers.put("nationality",nationality);

            String age = ele.getElementsByIndexEquals(5).text();       //อายุ
            jsonDetailPlayers.put("age",age);
            arr.put(jsonDetailPlayers); 
        }
        jsonPlayers.put("players_detail",arr);
        //System.out.println(jsonPlayers.toString());
        //inputElasticsearch(jsonPlayers.toString(), "thaipremierleague_teams");
    }
*/
    public void thaipremierleaguePlayerProfile(String url) throws IOException, InterruptedException {
        JSONObject json = new JSONObject();

        json.put("link",url);
        System.out.println(url);
        Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
        // logo team & team
        Elements elesLogo = doc.getElementsByClass("logo_team_player");
        Element eleImgTeam = elesLogo.select("img").get(0);
        String imgTeam = eleImgTeam.attr("src");
        imgTeam = baseLink + getNewLinkImage(imgTeam);
        json.put("logo_team",imgTeam);

        String team = eleImgTeam.attr("alt");
        team = team.replace("สโมสรฟุตบอลทีม", "");
        json.put("team",team);

        Elements elements = doc.getElementsByClass("data_profile");
        Element eleImgProfile = elements.select("img").get(0);
        String imgProfile = eleImgProfile.attr("src");
        imgProfile = baseLink + getNewLinkImage(imgProfile);
        json.put("img_profile",imgProfile);

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
        json.put("en_name",engName);

        String thName = eles.select("span").get(1).text();
        json.put("th_name",thName);

        String netWorth = eles.select("p").get(pTag).text();
        //netWorth = netWorth.replace("ค่าตัวโดยประมาณ ", "");
        //netWorth = netWorth.replace(" ปอนด์", "");
        json.put("net_worth",netWorth);  // ค่าตัว

        Elements elesUl = elements.select("ul");
        Elements elesUlChild = elesUl.select("*");
        int count = 0;
        String value = "";
        String key = "";
        boolean isGoalKeeper = false;  //ผู้รักษาประตู
        for (Element ele : elesUlChild) {
            if (count > 0) {                                //ไม่เอาค่าแรก
                if (ele.tagName().equals("li")) {
                    value = ele.select("li").text();        //ค่าที่ต้องการ
                }
                if (ele.tagName().equals("b")) {            //เลือกหัวข้อของค่าที่ต้องการ
                    key = ele.select("b").text();
                    key = key.replace(" :", "");            //ลบ _: ออก
                    String keyEn = detailPlayerToEnKey(key);
                    value = value.replace(key, "");
                    if (!value.isEmpty()) {                 //เลือกเอาเฉพาะที่มีค่า
                        String firstChar = value.substring(0, 1);      //ตัดเอาตัวอักษรตัวแรก
                        if (!firstChar.isEmpty()) {
                            //value = value.substring(1);               //ลบตัวอักษรตัวแรกออก
                            value = value.replace(" : ", "");     //ลบ _:_ ออก
                            //System.out.println(keyEn+" : "+value);
                            json.put(keyEn,value);

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
            json.put("captain",captain);
        }
        Elements elesInjury = elements.select(".injury");
        if (elesInjury.hasClass("injury")) {
            String injury = elesInjury.select(".injury").get(0).text();
            json.put("injury",injury);  //อาการบาดเจ็บ
        }

        JSONObject jsonDetailTransfer ;
        JSONArray arrTransfer  = new JSONArray();
        //ข้อมูลประวัติการย้ายสโมสร
        Elements elementsContent = doc.select(".content.main-content.left-content");
        Elements elesDataTransfer = elementsContent.select(".data_transfer");
        Elements elesContent = elesDataTransfer.select(".content");
        for (Element ele : elesContent) {
            jsonDetailTransfer = new JSONObject();
            String season = ele.select(".season").text();       //ฤดูกาล
            jsonDetailTransfer.put("season_transfer",season);
            
            String date = ele.select(".date").text();           //วันที่
            jsonDetailTransfer.put("date_transfer",date);
            
            String movefrom = ele.select(".movefrom").text();   //ย้ายออกจาก
            jsonDetailTransfer.put("movefrom",movefrom);
            
            String moveto = ele.select(".moveto").text();       //ย้ายเข้ามา
            jsonDetailTransfer.put("moveto",moveto);
            
            String transfer = ele.select(".transfer").text();   //การซื้อขาย
            jsonDetailTransfer.put("transfer",transfer);
 
            arrTransfer.put(jsonDetailTransfer);
        }
        json.put("transfer_detail",arrTransfer);
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
                            String season = eleContent1.select(".season").text();  //ฤดูกาล
                            jsonPlayedLeagueDetail.put("season", season);

                            String competition = eleContent1.select(".competition").text();  //รายการแข่งขัน
                            jsonPlayedLeagueDetail.put("competition", competition);

                            String club = eleContent1.select(".club").text();  //ทีมสโมสร
                            jsonPlayedLeagueDetail.put("club", club);

                            String matches = eleContent1.select(".matches.league_club_keeper").text();  //ลงเล่น(แมตซ์)
                            jsonPlayedLeagueDetail.put("matches", matches);

                            String goals = eleContent1.select(".data.league_club_keeper").get(0).text();  //ทำประตู
                            jsonPlayedLeagueDetail.put("goals", goals);

                            String own_goals = eleContent1.select(".data.league_club_keeper").get(1).text();  //ทำเข้าประตูตนเอง
                            jsonPlayedLeagueDetail.put("own_goals", own_goals);

                            String substituted_on = eleContent1.select(".data.league_club_keeper").get(2).text();  //เปลี่ยนตัวเข้า
                            jsonPlayedLeagueDetail.put("substituted_on", substituted_on);

                            String substituted_off = eleContent1.select(".data.league_club_keeper").get(3).text();  //เปลี่ยนตัวออก
                            jsonPlayedLeagueDetail.put("substituted_off", substituted_off);

                            String yellow = eleContent1.select(".data.league_club_keeper").get(4).text();  //ใบเหลือง
                            jsonPlayedLeagueDetail.put("yellow_cards", yellow);

                            String yellowred = eleContent1.select(".data.league_club_keeper").get(5).text();  //ใบเหลือง/ใบเเดง
                            jsonPlayedLeagueDetail.put("yellow_red_cards", yellowred);

                            String red = eleContent1.select(".data.league_club_keeper").get(6).text();  //ใบแดง
                            jsonPlayedLeagueDetail.put("red_cards", red);

                            String conceded = eleContent1.select(".data.league_club_keeper").get(7).text();  //เสียประตู
                            jsonPlayedLeagueDetail.put("goals_conceded", conceded);

                            String shutout = eleContent1.select(".data.league_club_keeper").get(8).text();  //คลีนชีท
                            jsonPlayedLeagueDetail.put("clean_sheets", shutout);

                            String time = eleContent1.select(".time").text();  //ลงเล่น (เวลา:นาที)
                            jsonPlayedLeagueDetail.put("minutes_played", time);

                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        }
                        // class content สุดท้าย
                        jsonPlayedLeagueDetail = new JSONObject();
                        Element elesContent1Last = elesTable1.select(".content").last();
                        String sumMatches = elesContent1Last.select(".matches.league_club_keeper.sum").text();  //ลงเล่น(แมตซ์)
                        jsonPlayedLeagueDetail.put("sum_matches", sumMatches);

                        String sum_goals= elesContent1Last.select(".data.league_club_keeper.sum").get(0).text();  //ทำประตู
                        jsonPlayedLeagueDetail.put("sum_goals", sum_goals);

                        String sum_own_goals = elesContent1Last.select(".data.league_club_keeper.sum").get(1).text();  //ทำเข้าประตูตนเอง
                        jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);

                        String sum_substituted_on = elesContent1Last.select(".data.league_club_keeper.sum").get(2).text();  //เปลี่ยนตัวเข้า
                        jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);

                        String sum_substituted_off = elesContent1Last.select(".data.league_club_keeper.sum").get(3).text();  //เปลี่ยนตัวออก
                        jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);

                        String sum_yellow = elesContent1Last.select(".data.league_club_keeper.sum").get(4).text();  //ใบเหลือง
                        jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);

                        String sum_yellow_red = elesContent1Last.select(".data.league_club_keeper.sum").get(5).text();  //ใบเหลือง/ใบเเดง
                        jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);

                        String sum_red = elesContent1Last.select(".data.league_club_keeper.sum").get(6).text();  //ใบแดง
                        jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);

                        String sum_conceded = elesContent1Last.select(".data.league_club_keeper.sum").get(7).text();  //เสียประตู
                        jsonPlayedLeagueDetail.put("sum_goals_conceded", sum_conceded);

                        String sum_shutout= elesContent1Last.select(".data.league_club_keeper.sum").get(8).text();  //คลีนชีท
                        jsonPlayedLeagueDetail.put("sum_clean_sheets", sum_shutout);

                        String sum_time = elesContent1Last.select(".time.sum").text();  //ลงเล่น (เวลา:นาที)
                        jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);
                        
                        arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        json.put("played_league_detail",arrPlayedLeagueDetail);
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
                            String season = eleContent1.select(".season").text();  //ฤดูกาล
                            jsonPlayedLeagueDetail.put("season", season);

                            String competition = eleContent1.select(".competition").text();  //รายการแข่งขัน
                            jsonPlayedLeagueDetail.put("competition", competition);

                            String club = eleContent1.select(".club").text();  //ทีมสโมสร
                            jsonPlayedLeagueDetail.put("club", club);

                            String matches = eleContent1.select(".matches.league_club_keeper").text();  //ลงเล่น(แมตซ์)
                            jsonPlayedLeagueDetail.put("matches", matches);

                            String goals = eleContent1.select(".data.league_club_keeper").get(0).text();  //ทำประตู 
                            jsonPlayedLeagueDetail.put("goals", goals);

                            String own_goals = eleContent1.select(".data.league_club_keeper").get(1).text();  //ทำเข้าประตูตนเอง
                            jsonPlayedLeagueDetail.put("own_goals", own_goals);

                            String substituted_on = eleContent1.select(".data.league_club_keeper").get(2).text();  //เปลี่ยนตัวเข้า
                            jsonPlayedLeagueDetail.put("substituted_on", substituted_on);

                            String substituted_off = eleContent1.select(".data.league_club_keeper").get(3).text();  //เปลี่ยนตัวออก
                            jsonPlayedLeagueDetail.put("substituted_off", substituted_off);

                            String yellow = eleContent1.select(".data.league_club_keeper").get(4).text();  //ใบเหลือง
                            jsonPlayedLeagueDetail.put("yellow_cards", yellow);

                            String yellow_red = eleContent1.select(".data.league_club_keeper").get(5).text();  //ใบเหลือง/ใบเเดง
                            jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);

                            String red = eleContent1.select(".data.league_club_keeper").get(6).text();  //ใบแดง
                            jsonPlayedLeagueDetail.put("red_cards", red);

                            String conceded = eleContent1.select(".data.league_club_keeper").get(7).text();  //เสียประตู
                            jsonPlayedLeagueDetail.put("goals_conceded", conceded);

                            String shutout = eleContent1.select(".data.league_club_keeper").get(8).text();  //คลีนชีท
                            jsonPlayedLeagueDetail.put("clean_sheets", shutout);

                            String time = eleContent1.select(".time").text();  //ลงเล่น (เวลา:นาที)
                            jsonPlayedLeagueDetail.put("minutes_played", time);

                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);                            
                        }
                        // class content สุดท้าย
                        //json = new JSONObject();
                        jsonPlayedLeagueDetail = new JSONObject();
                        Element elesContent1Last = elesTable1.select(".content").last();
                        String sum_matches = elesContent1Last.select(".matches.league_club_keeper.sum").text();  //ลงเล่น(แมตซ์)
                        jsonPlayedLeagueDetail.put("sum_matches", sum_matches);

                        String sum_goals = elesContent1Last.select(".data.league_club_keeper.sum").get(0).text();  //ทำประตู
                        jsonPlayedLeagueDetail.put("sum_goals", sum_goals);

                        String sum_own_goals = elesContent1Last.select(".data.league_club_keeper.sum").get(1).text();  //ทำเข้าประตูตนเอง
                        jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);

                        String sum_substituted_on = elesContent1Last.select(".data.league_club_keeper.sum").get(2).text();  //เปลี่ยนตัวเข้า
                        jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);

                        String sum_substituted_off = elesContent1Last.select(".data.league_club_keeper.sum").get(3).text();  //เปลี่ยนตัวออก
                        jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);

                        String sum_yellow = elesContent1Last.select(".data.league_club_keeper.sum").get(4).text();  //ใบเหลือง
                        jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);

                        String sum_yellow_red = elesContent1Last.select(".data.league_club_keeper.sum").get(5).text();  //ใบเหลือง/ใบเเดง
                        jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);

                        String sum_red = elesContent1Last.select(".data.league_club_keeper.sum").get(6).text();  //ใบแดง
                        jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);

                        String sum_conceded = elesContent1Last.select(".data.league_club_keeper.sum").get(7).text();  //เสียประตู
                        jsonPlayedLeagueDetail.put("sum_goals_conceded", sum_conceded);

                        String sum_shutout = elesContent1Last.select(".data.league_club_keeper.sum").get(8).text();  //คลีนชีท
                        jsonPlayedLeagueDetail.put("sum_clean_sheets", sum_shutout);

                        String sum_time = elesContent1Last.select(".time.sum").text();  //ลงเล่น (เวลา:นาที)
                        jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);

                        arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        json.put("played_league_detail",arrPlayedLeagueDetail);

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
                            String season = eleContent1.select(".season").text();  //ฤดูกาล
                            jsonPlayedLeagueDetail.put("season", season);

                            String competition = eleContent1.select(".competition").text();  //รายการแข่งขัน
                            jsonPlayedLeagueDetail.put("competition", competition);

                            String club = eleContent1.select(".club").text();  //ทีมสโมสร
                            jsonPlayedLeagueDetail.put("club", club);

                            String matches = eleContent1.select(".matches.league_club_keeper").text();  //ลงเล่น(แมตซ์)
                            jsonPlayedLeagueDetail.put("matches", matches);

                            String goals = eleContent1.select(".data.league_club_keeper").get(0).text();  //ทำประตู
                            jsonPlayedLeagueDetail.put("goals", goals);

                            String own_goals = eleContent1.select(".data.league_club_keeper").get(1).text();  //ทำเข้าประตูตนเอง
                            jsonPlayedLeagueDetail.put("own_goals", own_goals);

                            String substituted_on = eleContent1.select(".data.league_club_keeper").get(2).text();  //เปลี่ยนตัวเข้า
                            jsonPlayedLeagueDetail.put("substituted_on", substituted_on);

                            String substituted_off = eleContent1.select(".data.league_club_keeper").get(3).text();  //เปลี่ยนตัวออก
                            jsonPlayedLeagueDetail.put("substituted_off", substituted_off);

                            String yellow = eleContent1.select(".data.league_club_keeper").get(4).text();  //ใบเหลือง
                            jsonPlayedLeagueDetail.put("yellow_cards", yellow);

                            String yellow_red = eleContent1.select(".data.league_club_keeper").get(5).text();  //ใบเหลือง/ใบเเดง
                            jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);

                            String red = eleContent1.select(".data.league_club_keeper").get(6).text();  //ใบแดง
                            jsonPlayedLeagueDetail.put("red_cards", red);

                            String conceded = eleContent1.select(".data.league_club_keeper").get(7).text();  //เสียประตู
                            jsonPlayedLeagueDetail.put("goals_conceded", conceded);

                            String shutout = eleContent1.select(".data.league_club_keeper").get(8).text();  //คลีนชีท
                            jsonPlayedLeagueDetail.put("clean_sheets", shutout);

                            String time = eleContent1.select(".time").text();  //ลงเล่น (เวลา:นาที)
                            jsonPlayedLeagueDetail.put("minutes_played", time);
                            
                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        }
                        // class content สุดท้าย
                        jsonPlayedLeagueDetail = new JSONObject();
                        
                        Element elesContent1Last2 = elesTable2.select(".content").last();
                        String sum_matches2 = elesContent1Last2.select(".matches.league_club_keeper.sum").text();  //ลงเล่น(แมตซ์)
                        jsonPlayedLeagueDetail.put("sum_matches", sum_matches2);

                        String sum_goals2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(0).text();  //ทำประตู
                        jsonPlayedLeagueDetail.put("sum_goals", sum_goals2);

                        String sum_own_goals2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(1).text();  //ทำเข้าประตูตนเอง
                        jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals2);

                        String sum_substituted_on2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(2).text();  //เปลี่ยนตัวเข้า
                        jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on2);

                        String sum_substituted_off2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(3).text();  //เปลี่ยนตัวออก
                        jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off2);

                        String sum_yellow2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(4).text();  //ใบเหลือง
                        jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow2);

                        String sum_yellow_red2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(5).text();  //ใบเหลือง/ใบเเดง
                        jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red2);

                        String sum_red2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(6).text();  //ใบแดง
                        jsonPlayedLeagueDetail.put("sum_red_cards", sum_red2);

                        String sum_conceded2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(7).text();  //เสียประตู
                        jsonPlayedLeagueDetail.put("sum_goals_conceded", sum_conceded2);

                        String sum_shutout2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(8).text();  //คลีนชีท
                        jsonPlayedLeagueDetail.put("sum_clean_sheets", sum_shutout2);

                        String sum_time2 = elesContent1Last2.select(".time.sum").text();  //ลงเล่น (เวลา:นาที)
                        jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time2);
                        
                        arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        json.put("played_international_league_detail",arrPlayedLeagueDetail);                       
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
                        String competition = eleContent1.select(".competition.national").text();  //รายการแข่งขัน
                        jsonPlayedLeagueDetail.put("competition", competition);

                        String matches = eleContent1.select(".matches").text();  //ลงเล่น(แมตซ์)
                        jsonPlayedLeagueDetail.put("matches", matches);

                        String goals = eleContent1.select(".data.keeper").get(0).text();  //ทำประตู 
                        jsonPlayedLeagueDetail.put("goals", goals);

                        String own_goals = eleContent1.select(".data.keeper").get(1).text();  //ทำเข้าประตูตนเอง
                        jsonPlayedLeagueDetail.put("own_goals", own_goals);

                        String substituted_on = eleContent1.select(".data.keeper").get(2).text();  //เปลี่ยนตัวเข้า
                        jsonPlayedLeagueDetail.put("substituted_on", substituted_on);

                        String substituted_off = eleContent1.select(".data.keeper").get(3).text();  //เปลี่ยนตัวออก
                        jsonPlayedLeagueDetail.put("substituted_off", substituted_off);

                        String yellow = eleContent1.select(".data.keeper").get(4).text();  //ใบเหลือง  
                        jsonPlayedLeagueDetail.put("yellow_cards", yellow);

                        String yellow_red = eleContent1.select(".data.keeper").get(5).text();  //ใบเหลือง/ใบเเดง
                        jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);

                        String red = eleContent1.select(".data.keeper").get(6).text();  //ใบแดง
                        jsonPlayedLeagueDetail.put("red_cards", red);

                        String conceded = eleContent1.select(".data.keeper").get(7).text();  //เสียประตู
                        jsonPlayedLeagueDetail.put("goals_conceded", conceded);

                        String shutout = eleContent1.select(".data.keeper").get(8).text();  //คลีนชีท
                        jsonPlayedLeagueDetail.put("clean_sheets", shutout);

                        String time = eleContent1.select(".time.keeper").text();  //ลงเล่น (เวลา:นาที)
                        jsonPlayedLeagueDetail.put("minutes_played", time);
                        arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);   
                    }
                    // class content สุดท้าย
                    jsonPlayedLeagueDetail = new JSONObject();
                    Element elesContent1Last = elesData.select(".content").last();
                    String sum_matches = elesContent1Last.select(".matches.sum").text();  //ลงเล่น(แมตช์)
                    jsonPlayedLeagueDetail.put("sum_matches", sum_matches);

                    String sum_goals = elesContent1Last.select(".data.keeper.sum").get(0).text();  //ทำประตู
                    jsonPlayedLeagueDetail.put("sum_goals", sum_goals);

                    String sum_own_goals = elesContent1Last.select(".data.keeper.sum").get(1).text();  //ทำเข้าประตูตนเอง
                    jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);

                    String sum_substituted_on = elesContent1Last.select(".data.keeper.sum").get(2).text();  //เปลี่ยนตัวเข้า
                    jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);

                    String sum_substituted_off = elesContent1Last.select(".data.keeper.sum").get(3).text();  //เปลี่ยนตัวออก
                    jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);

                    String sum_yellow = elesContent1Last.select(".data.keeper.sum").get(4).text();  //ใบเหลือง
                    jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);

                    String sum_yellow_red = elesContent1Last.select(".data.keeper.sum").get(5).text();  //ใบเหลือง/ใบเเดง
                    jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);

                    String sum_red = elesContent1Last.select(".data.keeper.sum").get(6).text();  //ใบแดง
                    jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);

                    String sum_conceded = elesContent1Last.select(".data.keeper.sum").get(7).text();  //เสียประตู
                    jsonPlayedLeagueDetail.put("sum_goals_conceded", sum_conceded);

                    String sum_shutout = elesContent1Last.select(".data.keeper.sum").get(8).text();  //คลีนชีท
                    jsonPlayedLeagueDetail.put("sum_clean_sheets", sum_shutout);

                    String sum_time = elesContent1Last.select(".time.keeper.sum").text();  //ลงเล่น (เวลา:นาที)
                    jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);

                    arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                    json.put("played_national_detail",arrPlayedLeagueDetail);   
                }
            }
        //จบกรณีผู้รักษาประตู 
        //กรณีผู้เล่นอื่นๆ   
        }  else {
            System.out.println("+++นักเตะผู้อื่น+++");
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
                            String season = eleContent1.select(".season").text();  //ฤดูกาล
                            jsonPlayedLeagueDetail.put("season", season);

                            String competition = eleContent1.select(".competition").text();  //รายการแข่งขัน
                            jsonPlayedLeagueDetail.put("competition", competition);

                            String club = eleContent1.select(".club").text();  //ทีมสโมสร
                            jsonPlayedLeagueDetail.put("club", club);

                            String matches = eleContent1.select(".matches.league_club").text();  //ลงเล่น(แมตซ์)
                            jsonPlayedLeagueDetail.put("matches", matches);

                            String goals = eleContent1.select(".data.league_club").get(0).text();  //ทำประตู
                            jsonPlayedLeagueDetail.put("goals", goals);

                            String assists = eleContent1.select(".data.league_club").get(1).text();  //แอสซิสต์
                            jsonPlayedLeagueDetail.put("assists", assists);

                            String own_goals = eleContent1.select(".data.league_club").get(2).text();  //ทำเข้าประตูตนเอง
                            jsonPlayedLeagueDetail.put("own_goals", own_goals);

                            String substituted_on = eleContent1.select(".data.league_club").get(3).text();  //เปลี่ยนตัวเข้า
                            jsonPlayedLeagueDetail.put("substituted_on", substituted_on);

                            String substituted_off = eleContent1.select(".data.league_club").get(4).text();  //เปลี่ยนตัวออก 
                            jsonPlayedLeagueDetail.put("substituted_off", substituted_off);

                            String yellow = eleContent1.select(".data.league_club").get(5).text();  //ใบเหลือง
                            jsonPlayedLeagueDetail.put("yellow_cards", yellow);

                            String yellow_red = eleContent1.select(".data.league_club").get(6).text();  //ใบเหลือง/ใบเเดง
                            jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);

                            String red = eleContent1.select(".data.league_club").get(7).text();  //ใบเเดง
                            jsonPlayedLeagueDetail.put("red_cards", red);

                            String penalty_goals = eleContent1.select(".data.league_club").get(8).text();  //ทำประตู(จุดโทษ)
                            jsonPlayedLeagueDetail.put("penalty_goals", penalty_goals);

                            String mpg = eleContent1.select(".mpg").text();  //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)
                            jsonPlayedLeagueDetail.put("minutes_played_goals", mpg);

                            String time = eleContent1.select(".time").text();  //ลงเล่น (เวลา:นาที)
                            jsonPlayedLeagueDetail.put("minutes_played", time);

                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        }
                        // class content สุดท้าย
                        jsonPlayedLeagueDetail = new JSONObject();
                        Element elesContent1Last = elesTable1.select(".content").last();
                        String sum_matches = elesContent1Last.select(".matches.league_club.sum").text();  //ลงเล่น(แมตซ์)
                        jsonPlayedLeagueDetail.put("sum_matches", sum_matches);

                        String sum_goals = elesContent1Last.select(".data.league_club.sum").get(0).text();  //ทำประตู
                        jsonPlayedLeagueDetail.put("sum_goals", sum_goals);

                        String sum_assists = elesContent1Last.select(".data.league_club.sum").get(1).text();  //แอสซิสต์
                        jsonPlayedLeagueDetail.put("sum_assists", sum_assists);

                        String sum_own_goals = elesContent1Last.select(".data.league_club.sum").get(2).text();  //ทำเข้าประตูตนเอง
                        jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);

                        String sum_substituted_on= elesContent1Last.select(".data.league_club.sum").get(3).text();  //เปลี่ยนตัวเข้า
                        jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);

                        String sum_substituted_off= elesContent1Last.select(".data.league_club.sum").get(4).text();  //เปลี่ยนตัวออก 
                        jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);

                        String sum_yellow= elesContent1Last.select(".data.league_club.sum").get(5).text();  //ใบเหลือง
                        jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);

                        String sum_yellow_red= elesContent1Last.select(".data.league_club.sum").get(6).text();  //ใบเหลือง/ใบเเดง
                        jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);

                        String sum_red = elesContent1Last.select(".data.league_club.sum").get(7).text();  //ใบเเดง
                        jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);

                        String sum_penalty_goals = elesContent1Last.select(".data.league_club.sum").get(8).text();  //ทำประตู(จุดโทษ)
                        jsonPlayedLeagueDetail.put("sum_penalty_goals", sum_penalty_goals);

                        String sum_mpg = elesContent1Last.select(".mpg.sum").text();  //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)
                        jsonPlayedLeagueDetail.put("sum_minutes_played_goals", sum_mpg);

                        String sum_time = elesContent1Last.select(".time.sum").text();  //ลงเล่น (เวลา:นาที)
                        jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);

                        arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        json.put("played_league_detail",arrPlayedLeagueDetail);
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
                            String season = eleContent1.select(".season").text();  //ฤดูกาล
                            jsonPlayedLeagueDetail.put("season", season);

                            String competition = eleContent1.select(".competition").text();  //รายการแข่งขัน
                            jsonPlayedLeagueDetail.put("competition", competition);

                            String club = eleContent1.select(".club").text();  //ทีมสโมสร
                            jsonPlayedLeagueDetail.put("club", club);

                            String matches = eleContent1.select(".matches.league_club").text();  //ลงเล่น(แมตซ์)
                            jsonPlayedLeagueDetail.put("matches", matches);

                            String goals = eleContent1.select(".data.league_club").get(0).text();  //ทำประตู
                            jsonPlayedLeagueDetail.put("goals", goals);

                            String assists = eleContent1.select(".data.league_club").get(1).text();  //แอสซิสต์
                            jsonPlayedLeagueDetail.put("assists", assists);

                            String own_goals = eleContent1.select(".data.league_club").get(2).text();  //ทำเข้าประตูตนเอง
                            jsonPlayedLeagueDetail.put("own_goals", own_goals);

                            String substituted_on = eleContent1.select(".data.league_club").get(3).text();  //เปลี่ยนตัวเข้า
                            jsonPlayedLeagueDetail.put("substituted_on", substituted_on);

                            String substituted_off = eleContent1.select(".data.league_club").get(4).text();  //เปลี่ยนตัวออก
                            jsonPlayedLeagueDetail.put("substituted_off", substituted_off);

                            String yellow = eleContent1.select(".data.league_club").get(5).text();  //ใบเหลือง
                            jsonPlayedLeagueDetail.put("yellow_cards", yellow);

                            String yellow_red = eleContent1.select(".data.league_club").get(6).text();  //ใบเหลือง/ใบเเดง
                            jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);

                            String red = eleContent1.select(".data.league_club").get(7).text();  //ใบเเดง
                            jsonPlayedLeagueDetail.put("red_cards", red);

                            String penalty_goals = eleContent1.select(".data.league_club").get(8).text();  //ทำประตู(จุดโทษ)
                            jsonPlayedLeagueDetail.put("penalty_goals", penalty_goals);

                            String mpg = eleContent1.select(".mpg").text();  //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)
                            jsonPlayedLeagueDetail.put("minutes_played_goals", mpg);

                            String time = eleContent1.select(".time").text();  //ลงเล่น (เวลา:นาที)
                            jsonPlayedLeagueDetail.put("minutes_played", time);
                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        }
                        // class content สุดท้าย
                        jsonPlayedLeagueDetail = new JSONObject();
                        Element elesContent1Last = elesTable1.select(".content").last();
                        String sum_matches = elesContent1Last.select(".matches.league_club.sum").text();  //ลงเล่น(แมตซ์)
                        jsonPlayedLeagueDetail.put("sum_matches", sum_matches);

                        String sum_goals = elesContent1Last.select(".data.league_club.sum").get(0).text();  //ทำประตู
                        jsonPlayedLeagueDetail.put("sum_goals", sum_goals);

                        String sum_assists = elesContent1Last.select(".data.league_club.sum").get(1).text();  //แอสซิสต์
                        jsonPlayedLeagueDetail.put("sum_assists", sum_assists);

                        String sum_own_goals = elesContent1Last.select(".data.league_club.sum").get(2).text();  //ทำเข้าประตูตนเอง
                        jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);

                        String sum_substituted_on = elesContent1Last.select(".data.league_club.sum").get(3).text();  //เปลี่ยนตัวเข้า
                        jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);

                        String sum_substituted_off = elesContent1Last.select(".data.league_club.sum").get(4).text();  //เปลี่ยนตัวออก
                        jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);

                        String sum_yellow = elesContent1Last.select(".data.league_club.sum").get(5).text();  //ใบเหลือง
                        jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);

                        String sum_yellow_red = elesContent1Last.select(".data.league_club.sum").get(6).text();  //ใบเหลือง/ใบเเดง
                        jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);

                        String sum_red = elesContent1Last.select(".data.league_club.sum").get(7).text();  //ใบเเดง
                        jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);

                        String sum_penalty_goals = elesContent1Last.select(".data.league_club.sum").get(8).text();  //ทำประตู(จุดโทษ)
                        jsonPlayedLeagueDetail.put("sum_penalty_goals", sum_penalty_goals);

                        String sum_mpg = elesContent1Last.select(".mpg.sum").text();  //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)
                        jsonPlayedLeagueDetail.put("sum_minutes_played_goals", sum_mpg);

                        String sum_time = elesContent1Last.select(".time.sum").text();  //ลงเล่น (เวลา:นาที)
                        jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);
                        arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        json.put("played_league_detail",arrPlayedLeagueDetail);

                        System.out.println("ข้อมูลการลงเล่นฟุตบอลถ้วยระดับนานาชาติ");
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
                            String season = eleContent1.select(".season").text();  //ฤดูกาล
                            jsonPlayedLeagueDetail.put("season", season);

                            String competition = eleContent1.select(".competition").text();  //รายการแข่งขัน
                            jsonPlayedLeagueDetail.put("competition", competition);

                            String club = eleContent1.select(".club").text();  //ทีมสโมสร
                            jsonPlayedLeagueDetail.put("club", club);

                            String matches = eleContent1.select(".matches.league_club").text();  //ลงเล่น(แมตซ์)
                            jsonPlayedLeagueDetail.put("matches", matches);

                            String goals = eleContent1.select(".data.league_club").get(0).text();  //ทำประตู
                            jsonPlayedLeagueDetail.put("goals", goals);

                            String assists = eleContent1.select(".data.league_club").get(1).text();  //แอสซิสต์
                            jsonPlayedLeagueDetail.put("assists", assists);

                            String own_goals = eleContent1.select(".data.league_club").get(2).text();  //ทำเข้าประตูตนเอง
                            jsonPlayedLeagueDetail.put("own_goals", own_goals);

                            String substituted_on = eleContent1.select(".data.league_club").get(3).text();  //เปลี่ยนตัวเข้า
                            jsonPlayedLeagueDetail.put("substituted_on", substituted_on);

                            String substituted_off = eleContent1.select(".data.league_club").get(4).text();  //เปลี่ยนตัวออก 
                            jsonPlayedLeagueDetail.put("substituted_off", substituted_off);

                            String yellow = eleContent1.select(".data.league_club").get(5).text();  //ใบเหลือง
                            jsonPlayedLeagueDetail.put("yellow_cards", yellow);

                            String yellow_red = eleContent1.select(".data.league_club").get(6).text();  //ใบเหลือง/ใบเเดง
                            jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);

                            String red = eleContent1.select(".data.league_club").get(7).text();  //ใบเเดง
                            jsonPlayedLeagueDetail.put("red_cards", red);

                            String penalty_goals = eleContent1.select(".data.league_club").get(8).text();  //ทำประตู(จุดโทษ)
                            jsonPlayedLeagueDetail.put("penalty_goals", penalty_goals);

                            String mpg = eleContent1.select(".mpg").text();  //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)
                            jsonPlayedLeagueDetail.put("minutes_played_goals", mpg);

                            String time = eleContent1.select(".time").text();  //ลงเล่น (เวลา:นาที)
                            jsonPlayedLeagueDetail.put("minutes_played", time);

                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        }
                        // class content สุดท้าย
                        jsonPlayedLeagueDetail = new JSONObject();
                        Element elesContent1Last2 = elesTable2.select(".content").last();
                        String sum_matches2 = elesContent1Last2.select(".matches.league_club.sum").text();  //ลงเล่น(แมตซ์)
                        jsonPlayedLeagueDetail.put("sum_matches", sum_matches2);

                        String sum_goals2 = elesContent1Last2.select(".data.league_club.sum").get(0).text();  //ทำประตู
                        jsonPlayedLeagueDetail.put("sum_goals", sum_goals2);

                        String sum_assists2 = elesContent1Last2.select(".data.league_club.sum").get(1).text();  //แอสซิสต์
                        jsonPlayedLeagueDetail.put("sum_assists", sum_assists2);

                        String sum_own_goals2 = elesContent1Last2.select(".data.league_club.sum").get(2).text();  //ทำเข้าประตูตนเอง
                        jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals2);

                        String sum_substituted_on2 = elesContent1Last2.select(".data.league_club.sum").get(3).text();  //เปลี่ยนตัวเข้า
                        jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on2);

                        String sum_substituted_off2 = elesContent1Last2.select(".data.league_club.sum").get(4).text();  //เปลี่ยนตัวออก 
                        jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off2);

                        String sum_yellow2 = elesContent1Last2.select(".data.league_club.sum").get(5).text();  //ใบเหลือง
                        jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow2);

                        String sum_yellow_red2 = elesContent1Last2.select(".data.league_club.sum").get(6).text();  //ใบเหลือง/ใบเเดง
                        jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red2);

                        String sum_red2 = elesContent1Last2.select(".data.league_club.sum").get(7).text();  //ใบเเดง
                        jsonPlayedLeagueDetail.put("sum_red_cards", sum_red2);

                        String sum_penalty_goals2 = elesContent1Last2.select(".data.league_club.sum").get(8).text();  //ทำประตู(จุดโทษ)
                        jsonPlayedLeagueDetail.put("sum_penalty_goals", sum_penalty_goals2);

                        String sum_mpg2 = elesContent1Last2.select(".mpg.sum").text();  //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)
                        jsonPlayedLeagueDetail.put("sum_minutes_played_goals", sum_mpg2);

                        String sum_time2 = elesContent1Last2.select(".time.sum").text();  //ลงเล่น (เวลา:นาที)
                        jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time2);

                        arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        json.put("played_international_league_detail",arrPlayedLeagueDetail);
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
                        String competition = eleContent1.select(".competition.national").text();  //รายการแข่งขัน
                        jsonPlayedLeagueDetail.put("competition", competition);

                        String matches = eleContent1.select(".matches").text();  //ลงเล่น(แมตซ์)
                        jsonPlayedLeagueDetail.put("matches", matches);

                        String goals = eleContent1.select(".data").get(0).text();  //ทำประตู
                        jsonPlayedLeagueDetail.put("goals", goals);

                        String assists = eleContent1.select(".data").get(1).text();  //แอสซิสต์
                        jsonPlayedLeagueDetail.put("assists", assists);

                        String own_goals = eleContent1.select(".data").get(2).text();  //ทำเข้าประตูตนเอง
                        jsonPlayedLeagueDetail.put("own_goals", own_goals);

                        String substituted_on = eleContent1.select(".data").get(3).text();  //เปลี่ยนตัวเข้า
                        jsonPlayedLeagueDetail.put("substituted_on", substituted_on);

                        String substituted_off = eleContent1.select(".data").get(4).text();  //เปลี่ยนตัวออก
                        jsonPlayedLeagueDetail.put("substituted_off", substituted_off);

                        String yellow = eleContent1.select(".data").get(5).text();  //ใบเหลือง
                        jsonPlayedLeagueDetail.put("yellow_cards", yellow);

                        String yellow_red = eleContent1.select(".data").get(6).text();  //ใบเหลือง/ใบเเดง
                        jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);

                        String red = eleContent1.select(".data").get(7).text();  //ใบเเดง
                        jsonPlayedLeagueDetail.put("red_cards", red);

                        String penalty_goals = eleContent1.select(".data").get(8).text();  //ทำประตู(จุดโทษ)
                        jsonPlayedLeagueDetail.put("penalty_goals", penalty_goals);
                        
                        String mpg = eleContent1.select(".mpg").text();  //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)
                        jsonPlayedLeagueDetail.put("minutes_played_goals", mpg);

                        String time = eleContent1.select(".time").text();  //ลงเล่น (เวลา:นาที)
                        jsonPlayedLeagueDetail.put("minutes_played", time);

                        arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);                  
                    }
                    // class content สุดท้าย
                    jsonPlayedLeagueDetail = new JSONObject();
                    Element elesContent1Last = elesData.select(".content").last();
                    String sum_matches = elesContent1Last.select(".matches.sum").text();  //ลงเล่น(แมตซ์)
                    jsonPlayedLeagueDetail.put("sum_matches", sum_matches);

                    String sum_goals = elesContent1Last.select(".data.sum").get(0).text();  //ทำประตู
                    jsonPlayedLeagueDetail.put("sum_goals", sum_goals);

                    String sum_assists = elesContent1Last.select(".data.sum").get(1).text();  //แอสซิสต์
                    jsonPlayedLeagueDetail.put("sum_assists", sum_assists);

                    String sum_own_goals = elesContent1Last.select(".data.sum").get(2).text();  //ทำเข้าประตูตนเอง
                    jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);

                    String sum_substituted_on = elesContent1Last.select(".data.sum").get(3).text();  //เปลี่ยนตัวเข้า
                    jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);

                    String sum_substituted_off = elesContent1Last.select(".data.sum").get(4).text();  //เปลี่ยนตัวออก 
                    jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);

                    String sum_yellow = elesContent1Last.select(".data.sum").get(5).text();  //ใบเหลือง
                    jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);

                    String sum_yellow_red = elesContent1Last.select(".data.sum").get(6).text();  //ใบเหลือง/ใบเเดง
                    jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);

                    String sum_red = elesContent1Last.select(".data.sum").get(7).text();  //ใบเเดง
                    jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);

                    String sum_penalty_goals = elesContent1Last.select(".data.sum").get(8).text();  //ทำประตู(จุดโทษ)
                    jsonPlayedLeagueDetail.put("sum_penalty_goals", sum_penalty_goals);

                    String sum_mpg = elesContent1Last.select(".mpg.sum").text();  //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)
                    jsonPlayedLeagueDetail.put("sum_minutes_played_goals", sum_mpg);

                    String sum_time = elesContent1Last.select(".time.sum").text();  //ลงเล่น (เวลา:นาที)
                    jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);
                    
                    arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                    json.put("played_national_detail",arrPlayedLeagueDetail);
                }   
            }  
        }
        //จบกรณีผู้เล่นอื่นๆ 
        inputElasticsearch(json.toString(), "test"); 
        System.out.println("");
    }

    public void premierleaguePlayerProfile(String url) throws IOException, InterruptedException {
        boolean isGoalKeeper = false;  //ผู้รักษาประตู
        JSONObject json = new JSONObject();
        JSONObject jsonDetailTransfer ;
        JSONArray arrTransfer  = new JSONArray();

        //System.out.println(url);
        json.put("link",url);
        Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
        Elements elements = doc.getElementsByClass("box-player");

        Element eleImgTeam = elements.select("img").get(0);
        String imgTeam = eleImgTeam.attr("src");
        imgTeam = baseLink +  getNewLinkImage(imgTeam);
        //System.out.println("logo : "+imgTeam);
        json.put("logo_team",imgTeam);        
        
        String team = elements.select("span").get(1).text();
        //System.out.println("team : "+team); 
        json.put("team",team);
           
        Element eleImg = elements.select("img").get(1);
        String img = eleImg.attr("src");
        img = baseLink +  getNewLinkImage(img);
        //System.out.println("img profile : "+img);
        json.put("img_profile",img);
        
        String thName = elements.select("span").get(2).text();
        //System.out.println("th name : "+thName);
        json.put("th_name",thName);
        //String thName = elements.select("span[itemprop = name]").text();
        //System.out.println(thName);
        
        String engName = elements.select("span[itemprop = alternateName]").text();
        //System.out.println("en name : "+engName);
        json.put("en_name",engName);
        
        Elements elesProfile = elements.select(".data-profile");
        String birthdate = elesProfile.select("span[itemprop = birthDate]").text();//.get(2)
        //System.out.println("วันเกิด : "+birthdate);
        json.put("birthday",birthdate);
        
        String age = elesProfile.select("#player-age").text();
        //System.out.println("อายุ : "+age);
        json.put("age",age);
        
        String nationality  = elesProfile.select("span[itemprop = nationality]").text();
        //System.out.println("สัญชาติ : "+nationality); 
        json.put("nationality",nationality);
      
        String number  = elesProfile.select("#number").text();
        //System.out.println("สวมเสื้อเบอร์ : "+number);
        json.put("squad_nember",number);
        
        String height  = elesProfile.select("span[itemprop = height]").text();
        //System.out.println("ส่วนสูง : "+height);
        json.put("height",height);
        
        String footedness  = elesProfile.select("#footedness").text();
        //System.out.println("ถนัดเท้า : "+footedness); 
        json.put("footed",footedness);
        
        String pricePlayer  = elesProfile.select("span[itemprop = netWorth]").text();
        //System.out.println("ค่าตัวล่าสุด : "+pricePlayer);
        json.put("net_worth",pricePlayer);
        
        
        String oldClub  = elesProfile.select("#fromclub").text();
        //System.out.println("สโมสรเดิม : "+oldClub); 
        json.put("original_club",oldClub);
        
        Elements elesPosition = elements.select(".data-position");       
        String position  = elesPosition.select("span[itemprop = roleName]").text();
        //System.out.println("ตำแหน่ง : "+position);
        json.put("position",position);
        if ("ผู้รักษาประตู".equals(position)) {
            isGoalKeeper = true;
        }
        
        Elements elesContract = elements.select(".data-contract"); 
        String startSignContract  = elesContract.select("span[itemprop = startDate]").text();
        //System.out.println("ร่วมสโมสรเมื่อ : "+startSignContract);  
        json.put("sign_contract",startSignContract);
        
        String endSignContract  = elesContract.select("span[itemprop = endDate]").text();
        //System.out.println("สิ้นสุดสัญญา : "+endSignContract);  
        json.put("end_contract",endSignContract);
        
        // กรณือื่นๆเช่นเป็นกัปตันทีม  หรือ บาดเจ็บ
        Elements elesCaptain = elements.select(".captain");  
        if (elesCaptain.hasClass("captain")) {
           String captain = elesCaptain.select(".captain").get(0).text();
           //System.out.println(captain);   
           json.put("captain",captain);
        }
        Elements elesInjury = elements.select(".injury");  
        if (elesInjury.hasClass("injury")) {
           String injury  = elesInjury.select(".injury").get(0).text();
           //System.out.println(injury); 
           json.put("injury",injury);
        }
        

        //ข้อมูลประวัติการย้ายสโมสร
        Elements elementsContent = doc.select(".content.main-content.left-content");
        Elements elesDataTransfer = elementsContent.select(".data_transfer");
        Elements elesContent = elesDataTransfer.select(".content");
        for (Element ele : elesContent) {
            jsonDetailTransfer = new JSONObject();
            String season = ele.select(".season").text();       //ฤดูกาล
            jsonDetailTransfer.put("season_transfer",season);
            
            String date = ele.select(".date").text();           //วันที่
            jsonDetailTransfer.put("date_transfer",date);
            
            String movefrom = ele.select(".movefrom").text();   //ย้ายออกจาก
            jsonDetailTransfer.put("movefrom",movefrom);
            
            String moveto = ele.select(".moveto").text();       //ย้ายเข้ามา
            jsonDetailTransfer.put("moveto",moveto);
            
            String transfer = ele.select(".transfer").text();   //การซื้อขาย
            jsonDetailTransfer.put("transfer",transfer);
            
            //System.out.println(jsonDetailTransfer.toString());
 
            arrTransfer.put(jsonDetailTransfer);
        }
        json.put("transfer_detail",arrTransfer);
        //จบข้อมูลประวัติการย้ายสโมสร

        //กรณีผู้รักษาประตู 
        JSONObject jsonPlayedLeagueDetail;
        JSONArray arrPlayedLeagueDetail = new JSONArray();
        if (isGoalKeeper) {
            //System.out.println("+++ผู้รักษาประตู+++");
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
                            String season = eleContent1.select(".season").text();  //ฤดูกาล
                            jsonPlayedLeagueDetail.put("season", season);

                            String competition = eleContent1.select(".competition").text();  //รายการแข่งขัน
                            jsonPlayedLeagueDetail.put("competition", competition);

                            String club = eleContent1.select(".club").text();  //ทีมสโมสร
                            jsonPlayedLeagueDetail.put("club", club);

                            String matches = eleContent1.select(".matches.league_club_keeper").text();  //ลงเล่น(แมตซ์)
                            jsonPlayedLeagueDetail.put("matches", matches);

                            String goals = eleContent1.select(".data.league_club_keeper").get(0).text();  //ทำประตู
                            jsonPlayedLeagueDetail.put("goals", goals);

                            String own_goals = eleContent1.select(".data.league_club_keeper").get(1).text();  //ทำเข้าประตูตนเอง
                            jsonPlayedLeagueDetail.put("own_goals", own_goals);

                            String substituted_on = eleContent1.select(".data.league_club_keeper").get(2).text();  //เปลี่ยนตัวเข้า
                            jsonPlayedLeagueDetail.put("substituted_on", substituted_on);

                            String substituted_off = eleContent1.select(".data.league_club_keeper").get(3).text();  //เปลี่ยนตัวออก
                            jsonPlayedLeagueDetail.put("substituted_off", substituted_off);

                            String yellow = eleContent1.select(".data.league_club_keeper").get(4).text();  //ใบเหลือง
                            jsonPlayedLeagueDetail.put("yellow_cards", yellow);

                            String yellowred = eleContent1.select(".data.league_club_keeper").get(5).text();  //ใบเหลือง/ใบเเดง
                            jsonPlayedLeagueDetail.put("yellow_red_cards", yellowred);

                            String red = eleContent1.select(".data.league_club_keeper").get(6).text();  //ใบแดง
                            jsonPlayedLeagueDetail.put("red_cards", red);

                            String conceded = eleContent1.select(".data.league_club_keeper").get(7).text();  //เสียประตู
                            jsonPlayedLeagueDetail.put("goals_conceded", conceded);

                            String shutout = eleContent1.select(".data.league_club_keeper").get(8).text();  //คลีนชีท
                            jsonPlayedLeagueDetail.put("clean_sheets", shutout);

                            String time = eleContent1.select(".time").text();  //ลงเล่น (เวลา:นาที)
                            jsonPlayedLeagueDetail.put("minutes_played", time);

                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        }
                        // class content สุดท้าย
                        jsonPlayedLeagueDetail = new JSONObject();
                        Element elesContent1Last = elesTable1.select(".content").last();
                        String sumMatches = elesContent1Last.select(".matches.league_club_keeper.sum").text();  //ลงเล่น(แมตซ์)
                        jsonPlayedLeagueDetail.put("sum_matches", sumMatches);

                        String sum_goals= elesContent1Last.select(".data.league_club_keeper.sum").get(0).text();  //ทำประตู
                        jsonPlayedLeagueDetail.put("sum_goals", sum_goals);

                        String sum_own_goals = elesContent1Last.select(".data.league_club_keeper.sum").get(1).text();  //ทำเข้าประตูตนเอง
                        jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);

                        String sum_substituted_on = elesContent1Last.select(".data.league_club_keeper.sum").get(2).text();  //เปลี่ยนตัวเข้า
                        jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);

                        String sum_substituted_off = elesContent1Last.select(".data.league_club_keeper.sum").get(3).text();  //เปลี่ยนตัวออก
                        jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);

                        String sum_yellow = elesContent1Last.select(".data.league_club_keeper.sum").get(4).text();  //ใบเหลือง
                        jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);

                        String sum_yellow_red = elesContent1Last.select(".data.league_club_keeper.sum").get(5).text();  //ใบเหลือง/ใบเเดง
                        jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);

                        String sum_red = elesContent1Last.select(".data.league_club_keeper.sum").get(6).text();  //ใบแดง
                        jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);

                        String sum_conceded = elesContent1Last.select(".data.league_club_keeper.sum").get(7).text();  //เสียประตู
                        jsonPlayedLeagueDetail.put("sum_goals_conceded", sum_conceded);

                        String sum_shutout= elesContent1Last.select(".data.league_club_keeper.sum").get(8).text();  //คลีนชีท
                        jsonPlayedLeagueDetail.put("sum_clean_sheets", sum_shutout);

                        String sum_time = elesContent1Last.select(".time.sum").text();  //ลงเล่น (เวลา:นาที)
                        jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);
                        
                        arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        json.put("played_league_detail",arrPlayedLeagueDetail);
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
                            String season = eleContent1.select(".season").text();  //ฤดูกาล
                            jsonPlayedLeagueDetail.put("season", season);

                            String competition = eleContent1.select(".competition").text();  //รายการแข่งขัน
                            jsonPlayedLeagueDetail.put("competition", competition);

                            String club = eleContent1.select(".club").text();  //ทีมสโมสร
                            jsonPlayedLeagueDetail.put("club", club);

                            String matches = eleContent1.select(".matches.league_club_keeper").text();  //ลงเล่น(แมตซ์)
                            jsonPlayedLeagueDetail.put("matches", matches);

                            String goals = eleContent1.select(".data.league_club_keeper").get(0).text();  //ทำประตู 
                            jsonPlayedLeagueDetail.put("goals", goals);

                            String own_goals = eleContent1.select(".data.league_club_keeper").get(1).text();  //ทำเข้าประตูตนเอง
                            jsonPlayedLeagueDetail.put("own_goals", own_goals);

                            String substituted_on = eleContent1.select(".data.league_club_keeper").get(2).text();  //เปลี่ยนตัวเข้า
                            jsonPlayedLeagueDetail.put("substituted_on", substituted_on);

                            String substituted_off = eleContent1.select(".data.league_club_keeper").get(3).text();  //เปลี่ยนตัวออก
                            jsonPlayedLeagueDetail.put("substituted_off", substituted_off);

                            String yellow = eleContent1.select(".data.league_club_keeper").get(4).text();  //ใบเหลือง
                            jsonPlayedLeagueDetail.put("yellow_cards", yellow);

                            String yellow_red = eleContent1.select(".data.league_club_keeper").get(5).text();  //ใบเหลือง/ใบเเดง
                            jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);

                            String red = eleContent1.select(".data.league_club_keeper").get(6).text();  //ใบแดง
                            jsonPlayedLeagueDetail.put("red_cards", red);

                            String conceded = eleContent1.select(".data.league_club_keeper").get(7).text();  //เสียประตู
                            jsonPlayedLeagueDetail.put("goals_conceded", conceded);

                            String shutout = eleContent1.select(".data.league_club_keeper").get(8).text();  //คลีนชีท
                            jsonPlayedLeagueDetail.put("clean_sheets", shutout);

                            String time = eleContent1.select(".time").text();  //ลงเล่น (เวลา:นาที)
                            jsonPlayedLeagueDetail.put("minutes_played", time);

                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);                            
                        }
                        // class content สุดท้าย
                        //json = new JSONObject();
                        jsonPlayedLeagueDetail = new JSONObject();
                        Element elesContent1Last = elesTable1.select(".content").last();
                        String sum_matches = elesContent1Last.select(".matches.league_club_keeper.sum").text();  //ลงเล่น(แมตซ์)
                        jsonPlayedLeagueDetail.put("sum_matches", sum_matches);

                        String sum_goals = elesContent1Last.select(".data.league_club_keeper.sum").get(0).text();  //ทำประตู
                        jsonPlayedLeagueDetail.put("sum_goals", sum_goals);

                        String sum_own_goals = elesContent1Last.select(".data.league_club_keeper.sum").get(1).text();  //ทำเข้าประตูตนเอง
                        jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);

                        String sum_substituted_on = elesContent1Last.select(".data.league_club_keeper.sum").get(2).text();  //เปลี่ยนตัวเข้า
                        jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);

                        String sum_substituted_off = elesContent1Last.select(".data.league_club_keeper.sum").get(3).text();  //เปลี่ยนตัวออก
                        jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);

                        String sum_yellow = elesContent1Last.select(".data.league_club_keeper.sum").get(4).text();  //ใบเหลือง
                        jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);

                        String sum_yellow_red = elesContent1Last.select(".data.league_club_keeper.sum").get(5).text();  //ใบเหลือง/ใบเเดง
                        jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);

                        String sum_red = elesContent1Last.select(".data.league_club_keeper.sum").get(6).text();  //ใบแดง
                        jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);

                        String sum_conceded = elesContent1Last.select(".data.league_club_keeper.sum").get(7).text();  //เสียประตู
                        jsonPlayedLeagueDetail.put("sum_goals_conceded", sum_conceded);

                        String sum_shutout = elesContent1Last.select(".data.league_club_keeper.sum").get(8).text();  //คลีนชีท
                        jsonPlayedLeagueDetail.put("sum_clean_sheets", sum_shutout);

                        String sum_time = elesContent1Last.select(".time.sum").text();  //ลงเล่น (เวลา:นาที)
                        jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);

                        arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        json.put("played_league_detail",arrPlayedLeagueDetail);

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
                            String season = eleContent1.select(".season").text();  //ฤดูกาล
                            jsonPlayedLeagueDetail.put("season", season);

                            String competition = eleContent1.select(".competition").text();  //รายการแข่งขัน
                            jsonPlayedLeagueDetail.put("competition", competition);

                            String club = eleContent1.select(".club").text();  //ทีมสโมสร
                            jsonPlayedLeagueDetail.put("club", club);

                            String matches = eleContent1.select(".matches.league_club_keeper").text();  //ลงเล่น(แมตซ์)
                            jsonPlayedLeagueDetail.put("matches", matches);

                            String goals = eleContent1.select(".data.league_club_keeper").get(0).text();  //ทำประตู
                            jsonPlayedLeagueDetail.put("goals", goals);

                            String own_goals = eleContent1.select(".data.league_club_keeper").get(1).text();  //ทำเข้าประตูตนเอง
                            jsonPlayedLeagueDetail.put("own_goals", own_goals);

                            String substituted_on = eleContent1.select(".data.league_club_keeper").get(2).text();  //เปลี่ยนตัวเข้า
                            jsonPlayedLeagueDetail.put("substituted_on", substituted_on);

                            String substituted_off = eleContent1.select(".data.league_club_keeper").get(3).text();  //เปลี่ยนตัวออก
                            jsonPlayedLeagueDetail.put("substituted_off", substituted_off);

                            String yellow = eleContent1.select(".data.league_club_keeper").get(4).text();  //ใบเหลือง
                            jsonPlayedLeagueDetail.put("yellow_cards", yellow);

                            String yellow_red = eleContent1.select(".data.league_club_keeper").get(5).text();  //ใบเหลือง/ใบเเดง
                            jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);

                            String red = eleContent1.select(".data.league_club_keeper").get(6).text();  //ใบแดง
                            jsonPlayedLeagueDetail.put("red_cards", red);

                            String conceded = eleContent1.select(".data.league_club_keeper").get(7).text();  //เสียประตู
                            jsonPlayedLeagueDetail.put("goals_conceded", conceded);

                            String shutout = eleContent1.select(".data.league_club_keeper").get(8).text();  //คลีนชีท
                            jsonPlayedLeagueDetail.put("clean_sheets", shutout);

                            String time = eleContent1.select(".time").text();  //ลงเล่น (เวลา:นาที)
                            jsonPlayedLeagueDetail.put("minutes_played", time);
                            
                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        }
                        // class content สุดท้าย
                        jsonPlayedLeagueDetail = new JSONObject();
                        
                        Element elesContent1Last2 = elesTable2.select(".content").last();
                        String sum_matches2 = elesContent1Last2.select(".matches.league_club_keeper.sum").text();  //ลงเล่น(แมตซ์)
                        jsonPlayedLeagueDetail.put("sum_matches", sum_matches2);

                        String sum_goals2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(0).text();  //ทำประตู
                        jsonPlayedLeagueDetail.put("sum_goals", sum_goals2);

                        String sum_own_goals2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(1).text();  //ทำเข้าประตูตนเอง
                        jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals2);

                        String sum_substituted_on2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(2).text();  //เปลี่ยนตัวเข้า
                        jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on2);

                        String sum_substituted_off2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(3).text();  //เปลี่ยนตัวออก
                        jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off2);

                        String sum_yellow2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(4).text();  //ใบเหลือง
                        jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow2);

                        String sum_yellow_red2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(5).text();  //ใบเหลือง/ใบเเดง
                        jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red2);

                        String sum_red2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(6).text();  //ใบแดง
                        jsonPlayedLeagueDetail.put("sum_red_cards", sum_red2);

                        String sum_conceded2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(7).text();  //เสียประตู
                        jsonPlayedLeagueDetail.put("sum_goals_conceded", sum_conceded2);

                        String sum_shutout2 = elesContent1Last2.select(".data.league_club_keeper.sum").get(8).text();  //คลีนชีท
                        jsonPlayedLeagueDetail.put("sum_clean_sheets", sum_shutout2);

                        String sum_time2 = elesContent1Last2.select(".time.sum").text();  //ลงเล่น (เวลา:นาที)
                        jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time2);
                        
                        arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        json.put("played_international_league_detail",arrPlayedLeagueDetail);                       
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
                        String competition = eleContent1.select(".competition.national").text();  //รายการแข่งขัน
                        jsonPlayedLeagueDetail.put("competition", competition);

                        String matches = eleContent1.select(".matches").text();  //ลงเล่น(แมตซ์)
                        jsonPlayedLeagueDetail.put("matches", matches);

                        String goals = eleContent1.select(".data.keeper").get(0).text();  //ทำประตู 
                        jsonPlayedLeagueDetail.put("goals", goals);

                        String own_goals = eleContent1.select(".data.keeper").get(1).text();  //ทำเข้าประตูตนเอง
                        jsonPlayedLeagueDetail.put("own_goals", own_goals);

                        String substituted_on = eleContent1.select(".data.keeper").get(2).text();  //เปลี่ยนตัวเข้า
                        jsonPlayedLeagueDetail.put("substituted_on", substituted_on);

                        String substituted_off = eleContent1.select(".data.keeper").get(3).text();  //เปลี่ยนตัวออก
                        jsonPlayedLeagueDetail.put("substituted_off", substituted_off);

                        String yellow = eleContent1.select(".data.keeper").get(4).text();  //ใบเหลือง  
                        jsonPlayedLeagueDetail.put("yellow_cards", yellow);

                        String yellow_red = eleContent1.select(".data.keeper").get(5).text();  //ใบเหลือง/ใบเเดง
                        jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);

                        String red = eleContent1.select(".data.keeper").get(6).text();  //ใบแดง
                        jsonPlayedLeagueDetail.put("red_cards", red);

                        String conceded = eleContent1.select(".data.keeper").get(7).text();  //เสียประตู
                        jsonPlayedLeagueDetail.put("goals_conceded", conceded);

                        String shutout = eleContent1.select(".data.keeper").get(8).text();  //คลีนชีท
                        jsonPlayedLeagueDetail.put("clean_sheets", shutout);

                        String time = eleContent1.select(".time.keeper").text();  //ลงเล่น (เวลา:นาที)
                        jsonPlayedLeagueDetail.put("minutes_played", time);
                        arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);   
                    }
                    // class content สุดท้าย
                    jsonPlayedLeagueDetail = new JSONObject();
                    Element elesContent1Last = elesData.select(".content").last();
                    String sum_matches = elesContent1Last.select(".matches.sum").text();  //ลงเล่น(แมตช์)
                    jsonPlayedLeagueDetail.put("sum_matches", sum_matches);

                    String sum_goals = elesContent1Last.select(".data.keeper.sum").get(0).text();  //ทำประตู
                    jsonPlayedLeagueDetail.put("sum_goals", sum_goals);

                    String sum_own_goals = elesContent1Last.select(".data.keeper.sum").get(1).text();  //ทำเข้าประตูตนเอง
                    jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);

                    String sum_substituted_on = elesContent1Last.select(".data.keeper.sum").get(2).text();  //เปลี่ยนตัวเข้า
                    jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);

                    String sum_substituted_off = elesContent1Last.select(".data.keeper.sum").get(3).text();  //เปลี่ยนตัวออก
                    jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);

                    String sum_yellow = elesContent1Last.select(".data.keeper.sum").get(4).text();  //ใบเหลือง
                    jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);

                    String sum_yellow_red = elesContent1Last.select(".data.keeper.sum").get(5).text();  //ใบเหลือง/ใบเเดง
                    jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);

                    String sum_red = elesContent1Last.select(".data.keeper.sum").get(6).text();  //ใบแดง
                    jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);

                    String sum_conceded = elesContent1Last.select(".data.keeper.sum").get(7).text();  //เสียประตู
                    jsonPlayedLeagueDetail.put("sum_goals_conceded", sum_conceded);

                    String sum_shutout = elesContent1Last.select(".data.keeper.sum").get(8).text();  //คลีนชีท
                    jsonPlayedLeagueDetail.put("sum_clean_sheets", sum_shutout);

                    String sum_time = elesContent1Last.select(".time.keeper.sum").text();  //ลงเล่น (เวลา:นาที)
                    jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);

                    arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                    json.put("played_national_detail",arrPlayedLeagueDetail);   
                }
            }
        //จบกรณีผู้รักษาประตู 
        //กรณีผู้เล่นอื่นๆ   
        }  else {
            //System.out.println("+++นักเตะผู้อื่น+++");
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
                            String season = eleContent1.select(".season").text();  //ฤดูกาล
                            jsonPlayedLeagueDetail.put("season", season);

                            String competition = eleContent1.select(".competition").text();  //รายการแข่งขัน
                            jsonPlayedLeagueDetail.put("competition", competition);

                            String club = eleContent1.select(".club").text();  //ทีมสโมสร
                            jsonPlayedLeagueDetail.put("club", club);

                            String matches = eleContent1.select(".matches.league_club").text();  //ลงเล่น(แมตซ์)
                            jsonPlayedLeagueDetail.put("matches", matches);

                            String goals = eleContent1.select(".data.league_club").get(0).text();  //ทำประตู
                            jsonPlayedLeagueDetail.put("goals", goals);

                            String assists = eleContent1.select(".data.league_club").get(1).text();  //แอสซิสต์
                            jsonPlayedLeagueDetail.put("assists", assists);

                            String own_goals = eleContent1.select(".data.league_club").get(2).text();  //ทำเข้าประตูตนเอง
                            jsonPlayedLeagueDetail.put("own_goals", own_goals);

                            String substituted_on = eleContent1.select(".data.league_club").get(3).text();  //เปลี่ยนตัวเข้า
                            jsonPlayedLeagueDetail.put("substituted_on", substituted_on);

                            String substituted_off = eleContent1.select(".data.league_club").get(4).text();  //เปลี่ยนตัวออก 
                            jsonPlayedLeagueDetail.put("substituted_off", substituted_off);

                            String yellow = eleContent1.select(".data.league_club").get(5).text();  //ใบเหลือง
                            jsonPlayedLeagueDetail.put("yellow_cards", yellow);

                            String yellow_red = eleContent1.select(".data.league_club").get(6).text();  //ใบเหลือง/ใบเเดง
                            jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);

                            String red = eleContent1.select(".data.league_club").get(7).text();  //ใบเเดง
                            jsonPlayedLeagueDetail.put("red_cards", red);

                            String penalty_goals = eleContent1.select(".data.league_club").get(8).text();  //ทำประตู(จุดโทษ)
                            jsonPlayedLeagueDetail.put("penalty_goals", penalty_goals);

                            String mpg = eleContent1.select(".mpg").text();  //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)
                            jsonPlayedLeagueDetail.put("minutes_played_goals", mpg);

                            String time = eleContent1.select(".time").text();  //ลงเล่น (เวลา:นาที)
                            jsonPlayedLeagueDetail.put("minutes_played", time);

                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        }
                        // class content สุดท้าย
                        jsonPlayedLeagueDetail = new JSONObject();
                        Element elesContent1Last = elesTable1.select(".content").last();
                        String sum_matches = elesContent1Last.select(".matches.league_club.sum").text();  //ลงเล่น(แมตซ์)
                        jsonPlayedLeagueDetail.put("sum_matches", sum_matches);

                        String sum_goals = elesContent1Last.select(".data.league_club.sum").get(0).text();  //ทำประตู
                        jsonPlayedLeagueDetail.put("sum_goals", sum_goals);

                        String sum_assists = elesContent1Last.select(".data.league_club.sum").get(1).text();  //แอสซิสต์
                        jsonPlayedLeagueDetail.put("sum_assists", sum_assists);

                        String sum_own_goals = elesContent1Last.select(".data.league_club.sum").get(2).text();  //ทำเข้าประตูตนเอง
                        jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);

                        String sum_substituted_on= elesContent1Last.select(".data.league_club.sum").get(3).text();  //เปลี่ยนตัวเข้า
                        jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);

                        String sum_substituted_off= elesContent1Last.select(".data.league_club.sum").get(4).text();  //เปลี่ยนตัวออก 
                        jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);

                        String sum_yellow= elesContent1Last.select(".data.league_club.sum").get(5).text();  //ใบเหลือง
                        jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);

                        String sum_yellow_red= elesContent1Last.select(".data.league_club.sum").get(6).text();  //ใบเหลือง/ใบเเดง
                        jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);

                        String sum_red = elesContent1Last.select(".data.league_club.sum").get(7).text();  //ใบเเดง
                        jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);

                        String sum_penalty_goals = elesContent1Last.select(".data.league_club.sum").get(8).text();  //ทำประตู(จุดโทษ)
                        jsonPlayedLeagueDetail.put("sum_penalty_goals", sum_penalty_goals);

                        String sum_mpg = elesContent1Last.select(".mpg.sum").text();  //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)
                        jsonPlayedLeagueDetail.put("sum_minutes_played_goals", sum_mpg);

                        String sum_time = elesContent1Last.select(".time.sum").text();  //ลงเล่น (เวลา:นาที)
                        jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);

                        arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        json.put("played_league_detail",arrPlayedLeagueDetail);
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
                            String season = eleContent1.select(".season").text();  //ฤดูกาล
                            jsonPlayedLeagueDetail.put("season", season);

                            String competition = eleContent1.select(".competition").text();  //รายการแข่งขัน
                            jsonPlayedLeagueDetail.put("competition", competition);

                            String club = eleContent1.select(".club").text();  //ทีมสโมสร
                            jsonPlayedLeagueDetail.put("club", club);

                            String matches = eleContent1.select(".matches.league_club").text();  //ลงเล่น(แมตซ์)
                            jsonPlayedLeagueDetail.put("matches", matches);

                            String goals = eleContent1.select(".data.league_club").get(0).text();  //ทำประตู
                            jsonPlayedLeagueDetail.put("goals", goals);

                            String assists = eleContent1.select(".data.league_club").get(1).text();  //แอสซิสต์
                            jsonPlayedLeagueDetail.put("assists", assists);

                            String own_goals = eleContent1.select(".data.league_club").get(2).text();  //ทำเข้าประตูตนเอง
                            jsonPlayedLeagueDetail.put("own_goals", own_goals);

                            String substituted_on = eleContent1.select(".data.league_club").get(3).text();  //เปลี่ยนตัวเข้า
                            jsonPlayedLeagueDetail.put("substituted_on", substituted_on);

                            String substituted_off = eleContent1.select(".data.league_club").get(4).text();  //เปลี่ยนตัวออก
                            jsonPlayedLeagueDetail.put("substituted_off", substituted_off);

                            String yellow = eleContent1.select(".data.league_club").get(5).text();  //ใบเหลือง
                            jsonPlayedLeagueDetail.put("yellow_cards", yellow);

                            String yellow_red = eleContent1.select(".data.league_club").get(6).text();  //ใบเหลือง/ใบเเดง
                            jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);

                            String red = eleContent1.select(".data.league_club").get(7).text();  //ใบเเดง
                            jsonPlayedLeagueDetail.put("red_cards", red);

                            String penalty_goals = eleContent1.select(".data.league_club").get(8).text();  //ทำประตู(จุดโทษ)
                            jsonPlayedLeagueDetail.put("penalty_goals", penalty_goals);

                            String mpg = eleContent1.select(".mpg").text();  //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)
                            jsonPlayedLeagueDetail.put("minutes_played_goals", mpg);

                            String time = eleContent1.select(".time").text();  //ลงเล่น (เวลา:นาที)
                            jsonPlayedLeagueDetail.put("minutes_played", time);
                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        }
                        // class content สุดท้าย
                        jsonPlayedLeagueDetail = new JSONObject();
                        Element elesContent1Last = elesTable1.select(".content").last();
                        String sum_matches = elesContent1Last.select(".matches.league_club.sum").text();  //ลงเล่น(แมตซ์)
                        jsonPlayedLeagueDetail.put("sum_matches", sum_matches);

                        String sum_goals = elesContent1Last.select(".data.league_club.sum").get(0).text();  //ทำประตู
                        jsonPlayedLeagueDetail.put("sum_goals", sum_goals);

                        String sum_assists = elesContent1Last.select(".data.league_club.sum").get(1).text();  //แอสซิสต์
                        jsonPlayedLeagueDetail.put("sum_assists", sum_assists);

                        String sum_own_goals = elesContent1Last.select(".data.league_club.sum").get(2).text();  //ทำเข้าประตูตนเอง
                        jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);

                        String sum_substituted_on = elesContent1Last.select(".data.league_club.sum").get(3).text();  //เปลี่ยนตัวเข้า
                        jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);

                        String sum_substituted_off = elesContent1Last.select(".data.league_club.sum").get(4).text();  //เปลี่ยนตัวออก
                        jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);

                        String sum_yellow = elesContent1Last.select(".data.league_club.sum").get(5).text();  //ใบเหลือง
                        jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);

                        String sum_yellow_red = elesContent1Last.select(".data.league_club.sum").get(6).text();  //ใบเหลือง/ใบเเดง
                        jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);

                        String sum_red = elesContent1Last.select(".data.league_club.sum").get(7).text();  //ใบเเดง
                        jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);

                        String sum_penalty_goals = elesContent1Last.select(".data.league_club.sum").get(8).text();  //ทำประตู(จุดโทษ)
                        jsonPlayedLeagueDetail.put("sum_penalty_goals", sum_penalty_goals);

                        String sum_mpg = elesContent1Last.select(".mpg.sum").text();  //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)
                        jsonPlayedLeagueDetail.put("sum_minutes_played_goals", sum_mpg);

                        String sum_time = elesContent1Last.select(".time.sum").text();  //ลงเล่น (เวลา:นาที)
                        jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);
                        arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        json.put("played_league_detail",arrPlayedLeagueDetail);

                        //System.out.println("ข้อมูลการลงเล่นฟุตบอลถ้วยระดับนานาชาติ");
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
                            String season = eleContent1.select(".season").text();  //ฤดูกาล
                            jsonPlayedLeagueDetail.put("season", season);

                            String competition = eleContent1.select(".competition").text();  //รายการแข่งขัน
                            jsonPlayedLeagueDetail.put("competition", competition);

                            String club = eleContent1.select(".club").text();  //ทีมสโมสร
                            jsonPlayedLeagueDetail.put("club", club);

                            String matches = eleContent1.select(".matches.league_club").text();  //ลงเล่น(แมตซ์)
                            jsonPlayedLeagueDetail.put("matches", matches);

                            String goals = eleContent1.select(".data.league_club").get(0).text();  //ทำประตู
                            jsonPlayedLeagueDetail.put("goals", goals);

                            String assists = eleContent1.select(".data.league_club").get(1).text();  //แอสซิสต์
                            jsonPlayedLeagueDetail.put("assists", assists);

                            String own_goals = eleContent1.select(".data.league_club").get(2).text();  //ทำเข้าประตูตนเอง
                            jsonPlayedLeagueDetail.put("own_goals", own_goals);

                            String substituted_on = eleContent1.select(".data.league_club").get(3).text();  //เปลี่ยนตัวเข้า
                            jsonPlayedLeagueDetail.put("substituted_on", substituted_on);

                            String substituted_off = eleContent1.select(".data.league_club").get(4).text();  //เปลี่ยนตัวออก 
                            jsonPlayedLeagueDetail.put("substituted_off", substituted_off);

                            String yellow = eleContent1.select(".data.league_club").get(5).text();  //ใบเหลือง
                            jsonPlayedLeagueDetail.put("yellow_cards", yellow);

                            String yellow_red = eleContent1.select(".data.league_club").get(6).text();  //ใบเหลือง/ใบเเดง
                            jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);

                            String red = eleContent1.select(".data.league_club").get(7).text();  //ใบเเดง
                            jsonPlayedLeagueDetail.put("red_cards", red);

                            String penalty_goals = eleContent1.select(".data.league_club").get(8).text();  //ทำประตู(จุดโทษ)
                            jsonPlayedLeagueDetail.put("penalty_goals", penalty_goals);

                            String mpg = eleContent1.select(".mpg").text();  //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)
                            jsonPlayedLeagueDetail.put("minutes_played_goals", mpg);

                            String time = eleContent1.select(".time").text();  //ลงเล่น (เวลา:นาที)
                            jsonPlayedLeagueDetail.put("minutes_played", time);

                            arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        }
                        // class content สุดท้าย
                        jsonPlayedLeagueDetail = new JSONObject();
                        Element elesContent1Last2 = elesTable2.select(".content").last();
                        String sum_matches2 = elesContent1Last2.select(".matches.league_club.sum").text();  //ลงเล่น(แมตซ์)
                        jsonPlayedLeagueDetail.put("sum_matches", sum_matches2);

                        String sum_goals2 = elesContent1Last2.select(".data.league_club.sum").get(0).text();  //ทำประตู
                        jsonPlayedLeagueDetail.put("sum_goals", sum_goals2);

                        String sum_assists2 = elesContent1Last2.select(".data.league_club.sum").get(1).text();  //แอสซิสต์
                        jsonPlayedLeagueDetail.put("sum_assists", sum_assists2);

                        String sum_own_goals2 = elesContent1Last2.select(".data.league_club.sum").get(2).text();  //ทำเข้าประตูตนเอง
                        jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals2);

                        String sum_substituted_on2 = elesContent1Last2.select(".data.league_club.sum").get(3).text();  //เปลี่ยนตัวเข้า
                        jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on2);

                        String sum_substituted_off2 = elesContent1Last2.select(".data.league_club.sum").get(4).text();  //เปลี่ยนตัวออก 
                        jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off2);

                        String sum_yellow2 = elesContent1Last2.select(".data.league_club.sum").get(5).text();  //ใบเหลือง
                        jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow2);

                        String sum_yellow_red2 = elesContent1Last2.select(".data.league_club.sum").get(6).text();  //ใบเหลือง/ใบเเดง
                        jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red2);

                        String sum_red2 = elesContent1Last2.select(".data.league_club.sum").get(7).text();  //ใบเเดง
                        jsonPlayedLeagueDetail.put("sum_red_cards", sum_red2);

                        String sum_penalty_goals2 = elesContent1Last2.select(".data.league_club.sum").get(8).text();  //ทำประตู(จุดโทษ)
                        jsonPlayedLeagueDetail.put("sum_penalty_goals", sum_penalty_goals2);

                        String sum_mpg2 = elesContent1Last2.select(".mpg.sum").text();  //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)
                        jsonPlayedLeagueDetail.put("sum_minutes_played_goals", sum_mpg2);

                        String sum_time2 = elesContent1Last2.select(".time.sum").text();  //ลงเล่น (เวลา:นาที)
                        jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time2);

                        arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                        json.put("played_international_league_detail",arrPlayedLeagueDetail);
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
                        String competition = eleContent1.select(".competition.national").text();  //รายการแข่งขัน
                        jsonPlayedLeagueDetail.put("competition", competition);

                        String matches = eleContent1.select(".matches").text();  //ลงเล่น(แมตซ์)
                        jsonPlayedLeagueDetail.put("matches", matches);

                        String goals = eleContent1.select(".data").get(0).text();  //ทำประตู
                        jsonPlayedLeagueDetail.put("goals", goals);

                        String assists = eleContent1.select(".data").get(1).text();  //แอสซิสต์
                        jsonPlayedLeagueDetail.put("assists", assists);

                        String own_goals = eleContent1.select(".data").get(2).text();  //ทำเข้าประตูตนเอง
                        jsonPlayedLeagueDetail.put("own_goals", own_goals);

                        String substituted_on = eleContent1.select(".data").get(3).text();  //เปลี่ยนตัวเข้า
                        jsonPlayedLeagueDetail.put("substituted_on", substituted_on);

                        String substituted_off = eleContent1.select(".data").get(4).text();  //เปลี่ยนตัวออก
                        jsonPlayedLeagueDetail.put("substituted_off", substituted_off);

                        String yellow = eleContent1.select(".data").get(5).text();  //ใบเหลือง
                        jsonPlayedLeagueDetail.put("yellow_cards", yellow);

                        String yellow_red = eleContent1.select(".data").get(6).text();  //ใบเหลือง/ใบเเดง
                        jsonPlayedLeagueDetail.put("yellow_red_cards", yellow_red);

                        String red = eleContent1.select(".data").get(7).text();  //ใบเเดง
                        jsonPlayedLeagueDetail.put("red_cards", red);

                        String penalty_goals = eleContent1.select(".data").get(8).text();  //ทำประตู(จุดโทษ)
                        jsonPlayedLeagueDetail.put("penalty_goals", penalty_goals);
                        
                        String mpg = eleContent1.select(".mpg").text();  //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)
                        jsonPlayedLeagueDetail.put("minutes_played_goals", mpg);

                        String time = eleContent1.select(".time").text();  //ลงเล่น (เวลา:นาที)
                        jsonPlayedLeagueDetail.put("minutes_played", time);

                        arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);                  
                    }
                    // class content สุดท้าย
                    jsonPlayedLeagueDetail = new JSONObject();
                    Element elesContent1Last = elesData.select(".content").last();
                    String sum_matches = elesContent1Last.select(".matches.sum").text();  //ลงเล่น(แมตซ์)
                    jsonPlayedLeagueDetail.put("sum_matches", sum_matches);

                    String sum_goals = elesContent1Last.select(".data.sum").get(0).text();  //ทำประตู
                    jsonPlayedLeagueDetail.put("sum_goals", sum_goals);

                    String sum_assists = elesContent1Last.select(".data.sum").get(1).text();  //แอสซิสต์
                    jsonPlayedLeagueDetail.put("sum_assists", sum_assists);

                    String sum_own_goals = elesContent1Last.select(".data.sum").get(2).text();  //ทำเข้าประตูตนเอง
                    jsonPlayedLeagueDetail.put("sum_own_goals", sum_own_goals);

                    String sum_substituted_on = elesContent1Last.select(".data.sum").get(3).text();  //เปลี่ยนตัวเข้า
                    jsonPlayedLeagueDetail.put("sum_substituted_on", sum_substituted_on);

                    String sum_substituted_off = elesContent1Last.select(".data.sum").get(4).text();  //เปลี่ยนตัวออก 
                    jsonPlayedLeagueDetail.put("sum_substituted_off", sum_substituted_off);

                    String sum_yellow = elesContent1Last.select(".data.sum").get(5).text();  //ใบเหลือง
                    jsonPlayedLeagueDetail.put("sum_yellow_cards", sum_yellow);

                    String sum_yellow_red = elesContent1Last.select(".data.sum").get(6).text();  //ใบเหลือง/ใบเเดง
                    jsonPlayedLeagueDetail.put("sum_yellow_red_cards", sum_yellow_red);

                    String sum_red = elesContent1Last.select(".data.sum").get(7).text();  //ใบเเดง
                    jsonPlayedLeagueDetail.put("sum_red_cards", sum_red);

                    String sum_penalty_goals = elesContent1Last.select(".data.sum").get(8).text();  //ทำประตู(จุดโทษ)
                    jsonPlayedLeagueDetail.put("sum_penalty_goals", sum_penalty_goals);

                    String sum_mpg = elesContent1Last.select(".mpg.sum").text();  //ค่าเฉลี่ยต่อหนึ่งการทำประตู(นาที)
                    jsonPlayedLeagueDetail.put("sum_minutes_played_goals", sum_mpg);

                    String sum_time = elesContent1Last.select(".time.sum").text();  //ลงเล่น (เวลา:นาที)
                    jsonPlayedLeagueDetail.put("sum_minutes_played", sum_time);
                    
                    arrPlayedLeagueDetail.put(jsonPlayedLeagueDetail);
                    json.put("played_national_detail",arrPlayedLeagueDetail);
                }   
            }  
        }        
        //inputElasticsearch(json.toString(), "test2"); 
        System.out.println(json.toString());

        System.out.println("");
    }

    String detailPlayerToEnKey(String inputKey){
        String key = "";
        if("วันเกิด".equals(inputKey)){
            key = "birthday";
        }
        if("อายุ".equals(inputKey)){
            key = "age";
        } 
        if("ส่วนสูง".equals(inputKey)){
            key = "height";
        } 
        if("สัญชาติ".equals(inputKey)){
            key = "nationality";
        } 
        if("สวมเสื้อเบอร์".equals(inputKey)){
            key = "squad_nember";
        }
        if("ตำแหน่ง".equals(inputKey)){
            key = "position";
        } 
        if("ถนัดเท้า".equals(inputKey)){
            key = "footed";
        }  
        if("เซ็นสัญญาเมื่อ".equals(inputKey)){
            key = "sign_contract";
        }
        if("สิ้นสุดสัญญา".equals(inputKey)){
            key = "end_contract";
        }  
        if("สโมสรเดิม".equals(inputKey)){
            key = "original_club";
        } 
        if("ปัจจุบันสังกัดทีมชาติ".equals(inputKey)){
            key = "currently_national_team";
        } 
        if("อดีตสังกัดทีมชาติ".equals(inputKey)){
            key = "former_national_team";
        }        
        return key;
    }
    
    String titleToEnKey(String inputKey) {
        String key = "";
        if ("ทำประตูสุงสุด".equals(inputKey)) {
            key = "goals";
        }
        if ("ทำแอสซิสต์สุงสุด".equals(inputKey)) {
            key = "assists";
        }
        if ("ลงเล่นมากที่สุด".equals(inputKey)) {
            key = "minutes_played";
        }
        return key;
    }
    
    public void inputElasticsearch(String body,String index) {
        try {
            HttpResponse<String> response = Unirest.post("http://localhost:9200/"+index+"/text")
                    .header("Content-Type", "application/json")
                    .header("Cache-Control", "no-cache")
                    .body(body)
                    .asString();
        } catch (UnirestException ex) {
            Logger.getLogger(TeamDetail.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("insert finish");
    }   
    public static void main(String[] args) throws IOException, InterruptedException {

        //String url = "http://www.livesoccer888.com/thaipremierleague/teams/Chonburi/Players/index.php";
        
        //String url = "http://www.livesoccer888.com/thaipremierleague/teams/Chonburi/Players/Chanin-Sae-Ear";
        
        //String url = "http://www.livesoccer888.com/thaipremierleague/teams/Chonburi/Players/index.php";
        TeamDetail st = new TeamDetail();
        //st.teamDetail(url);
        //st.playerOfTeam(url);
        //st.thaipremierleaguePlayerProfile(url);
        //st.premierleaguePlayerProfile(url);

        //String url = "http://www.livesoccer888.com/thaipremierleague/players/index.php";
        /*String url = "http://www.livesoccer888.com/premierleague/players/index.php";
        st.statsOfTeam(url);
        for (String link : st.list) {
            st.teamDetail(link);
        }*/
        /*for (String linkPlayer : st.listPlayer) {
            st.thaipremierleaguePlayerProfile(linkPlayer);
            //st.premierleaguePlayerProfile(linkPlayer);
        }*/
        String url = "http://www.livesoccer888.com/premierleague/2015-16/teams/Arsenal/Players/Danny-Welbeck";
        st.premierleaguePlayerProfile(url);
    }
}
