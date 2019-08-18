package app.service;

import app.dao.Redis;
import app.function.DateTimes;
import app.function.Elasticsearch;
import app.function.Md5;
import app.function.OtherFunc;
import java.io.IOException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class ServiceThaiPremierLeagueImp implements ServiceThaiPremierLeague {

    @Autowired
    private DateTimes dateTimes;

    @Autowired
    private Elasticsearch els;

    @Autowired
    private OtherFunc func;
    
    @Autowired
    private Md5 md5;

    @Autowired
    private Redis rd;
  
    @Override
    public void resultsPresentContent(String url, String indexName, String season, String baseLinkImg) {
        Jedis redis = rd.connect();
        Document doc;
        try {
            doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Elements elements = doc.getElementsByClass("ui-tabs");
            List<String> list = new ArrayList<>();
            String strUrl;
            // เก็บ id component ของเดือน
            for (Element ele : elements) {
                Elements elesId = ele.getElementsByClass("ui-tabs-nav");
                for (Element eleGetMonth : elesId) {
                    Elements a = eleGetMonth.select("a");
                    for (Element href : a) {
                        strUrl = href.attr("href");
                        strUrl = strUrl.replace("#", ""); //ตัด # ออก
                        list.add(strUrl); // เก็บใส่ list ไว้ 
                    }
                }
            }
            // จบเก็บ id component ของเดือน   
            JSONObject json;
            String daymatches = null;
            String home;
            String score;
            String scoreHome = null;
            String scoreAway = null;
            String away;
            String homeAway;
            String daymatchesInter;
            String homeImgUrl = null;
            String awayImgUrl = null;

            for (String idMonth : list) {
                Element ele = doc.getElementById(idMonth);
                Elements eles = ele.getElementsByClass("league-result");
                for (Element eleResult : eles) {
                    Elements elesChildren = eleResult.select("*"); // select all child tags of the form
                    for (Element eleChild : elesChildren) {
                        if (eleChild.hasClass("daymatches")) {
                            daymatches = eleChild.getElementsByClass("daymatches").text();
                        } else if (eleChild.hasClass("matches")) {
                            Elements elesMatches = eleChild.getElementsByClass("matches");
                            for (Element eleMatch : elesMatches) {

                                json = new JSONObject();
                                home = eleMatch.getElementsByClass("home").text();
                                score = eleMatch.getElementsByClass("score").text();
                                away = eleMatch.getElementsByClass("away").text();

                                if (!score.isEmpty() && !"เลื่อน".equals(score) && !"ยกเลิก".equals(score)) {
                                    //เก็บ logo เจ้าบ้าน 
                                    Elements elesHome = eleMatch.getElementsByClass("home");
                                    Element eleHomeImg = elesHome.select("img").first();
                                    homeImgUrl = func.getNewLinkImage(eleHomeImg.attr("src"));

                                    //เก็บ logo ทีมเยือน 
                                    Elements elesAway = eleMatch.getElementsByClass("away");
                                    Element eleAwayImg = elesAway.select("img").first();
                                    awayImgUrl = func.getNewLinkImage(eleAwayImg.attr("src"));

                                    scoreHome = score.substring(0, score.indexOf('-'));     //ตัดเอาตัวเลขก่อน - (?-)
                                    scoreAway = score.substring(score.lastIndexOf('-') + 1);  //ตัดเอาตัวเลขหลัง - (-?)
                                }
                                if (!home.isEmpty()) {
                                    homeAway = func.getHomeAway(home, away);
                                    daymatchesInter = dateTimes.getInterDate(daymatches);
                                    json.put("link", url);                                 //ลิ้งก์
                                    json.put("season", season);                            //ฤดูกาล
                                    json.put("date", daymatchesInter);                     //วันที่
                                    json.put("date_thai", daymatches);                     //วันที่ไทย
                                    json.put("home", home);                                //เจ้าบ้าน
                                    json.put("score", score);                              //ผลการเเข่งขัน
                                    json.put("away", away);                                //ทีมเยือน
                                    json.put("score_home", scoreHome);                     //score เจ้าบ้าน
                                    json.put("score_away", scoreAway);                     //score ทีมเยือน
                                    json.put("home_away", homeAway);                       //เจ้าบ้าน  ทีมเยือน
                                    json.put("home_img", baseLinkImg + homeImgUrl);        //โลโก้เจ้าบ้าน
                                    json.put("away_img", baseLinkImg + awayImgUrl);        //โลโก้ทีมเยือน
                                    els.inputElasticsearch(json.toString(), "present_"+indexName);
                                    System.out.println(dateTimes.thaiDateTime() + " : insert present results ThaiPremierLeague complete");
                                }
                            }
                        }
                    }
                }
            }
            redis.close();
        } catch (IOException ex) {
            Logger.getLogger(ServiceThaiPremierLeagueImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void resultsContent(String url, String indexName, String season, String baseLinkImg) {
        Jedis redis = rd.connect();
        Document doc;
        try {
            doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Elements elements = doc.getElementsByClass("ui-tabs");
            List<String> list = new ArrayList<>();
            String strUrl;
            // เก็บ id component ของเดือน
            for (Element ele : elements) {
                Elements elesId = ele.getElementsByClass("ui-tabs-nav");
                for (Element eleGetMonth : elesId) {
                    Elements a = eleGetMonth.select("a");
                    for (Element href : a) {
                        strUrl = href.attr("href");
                        strUrl = strUrl.replace("#", ""); //ตัด # ออก
                        list.add(strUrl); // เก็บใส่ list ไว้ 
                    }
                }
            }
            // จบเก็บ id component ของเดือน   
            JSONObject json;
            String daymatches = null;
            String home;
            String score;
            String scoreHome = null;
            String scoreAway = null;
            String away;
            String homeAway;
            String daymatchesInter;
            String homeImgUrl = null;
            String awayImgUrl = null;

            for (String idMonth : list) {
                Element ele = doc.getElementById(idMonth);
                Elements eles = ele.getElementsByClass("league-result");
                for (Element eleResult : eles) {
                    Elements elesChildren = eleResult.select("*"); // select all child tags of the form
                    for (Element eleChild : elesChildren) {
                        if (eleChild.hasClass("daymatches")) {
                            daymatches = eleChild.getElementsByClass("daymatches").text();
                        } else if (eleChild.hasClass("matches")) {
                            Elements elesMatches = eleChild.getElementsByClass("matches");
                            for (Element eleMatch : elesMatches) {

                                json = new JSONObject();
                                home = eleMatch.getElementsByClass("home").text();
                                score = eleMatch.getElementsByClass("score").text();
                                away = eleMatch.getElementsByClass("away").text();

                                if (!score.isEmpty() && !"เลื่อน".equals(score) && !"ยกเลิก".equals(score)) {
                                    //เก็บ logo เจ้าบ้าน 
                                    Elements elesHome = eleMatch.getElementsByClass("home");
                                    Element eleHomeImg = elesHome.select("img").first();
                                    homeImgUrl = func.getNewLinkImage(eleHomeImg.attr("src"));

                                    //เก็บ logo ทีมเยือน 
                                    Elements elesAway = eleMatch.getElementsByClass("away");
                                    Element eleAwayImg = elesAway.select("img").first();
                                    awayImgUrl = func.getNewLinkImage(eleAwayImg.attr("src"));

                                    scoreHome = score.substring(0, score.indexOf('-'));     //ตัดเอาตัวเลขก่อน - (?-)
                                    scoreAway = score.substring(score.lastIndexOf('-') + 1);  //ตัดเอาตัวเลขหลัง - (-?)
                                }
                                if (!home.isEmpty()) {
                                    homeAway = func.getHomeAway(home, away);
                                    daymatchesInter = dateTimes.getInterDate(daymatches);
                                    String homeAwayId = null;
                                    try{
                                        homeAwayId = md5.encrypt(home+away);
                                    }catch(Exception e){
                                        System.out.println(e.getMessage());
                                    }
                                    json.put("home_away_id", homeAwayId);
                                    json.put("link", url);                                 //ลิ้งก์
                                    json.put("season", season);                            //ฤดูกาล
                                    json.put("date", daymatchesInter);                     //วันที่
                                    json.put("date_thai", daymatches);                     //วันที่ไทย
                                    json.put("home", home);                                //เจ้าบ้าน
                                    json.put("score", score);                              //ผลการเเข่งขัน
                                    json.put("away", away);                                //ทีมเยือน
                                    json.put("score_home", scoreHome);                     //score เจ้าบ้าน
                                    json.put("score_away", scoreAway);                     //score ทีมเยือน
                                    json.put("home_away", homeAway);                       //เจ้าบ้าน  ทีมเยือน
                                    json.put("home_img", baseLinkImg + homeImgUrl);        //โลโก้เจ้าบ้าน
                                    json.put("away_img", baseLinkImg + awayImgUrl);        //โลโก้ทีมเยือน
                                    els.inputElasticsearch(json.toString(), indexName);
                                    System.out.println(dateTimes.thaiDateTime() + " : insert results ThaiPremierLeague complete");
                                }
                            }
                        }
                    }
                }
            }
            redis.close();
        } catch (IOException ex) {
            Logger.getLogger(ServiceThaiPremierLeagueImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void fixturesContent(String url, String indexName, String season, String baseLinkImg) {
        try {
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Elements elements = doc.getElementsByClass("ui-tabs");
            List<String> list = new ArrayList<>();
            String strUrl;
            // เก็บ id component ของเดือน
            for (Element ele : elements) {
                Elements elesId = ele.getElementsByClass("ui-tabs-nav");
                for (Element eleGetMonth : elesId) {
                    Elements a = eleGetMonth.select("a");
                    for (Element href : a) {
                        strUrl = href.attr("href");
                        strUrl = strUrl.replace("#", ""); //ตัด # ออก
                        list.add(strUrl); // เก็บใส่ list ไว้ 
                    }
                }
            }
            // จบเก็บ id component ของเดือน   
            JSONObject json;
            String daymatches = null;
            String home;
            String time;
            String away;
            String homeAway;
            String daymatchesInter;
            String homeImgUrl;
            String awayImgUrl;

            for (String idMonth : list) {
                Element ele = doc.getElementById(idMonth);
                Elements eles = ele.getElementsByClass("league-fixture");
                for (Element eleResult : eles) {
                    Elements elesChildren = eleResult.select("*"); // select all child tags of the form
                    for (Element eleChild : elesChildren) {
                        if (eleChild.hasClass("daymatches")) {
                            daymatches = eleChild.getElementsByClass("daymatches").text();
                        } else if (eleChild.hasClass("matches")) {
                            Elements elesMatches = eleChild.getElementsByClass("matches");
                            for (Element eleMatch : elesMatches) {
                                json = new JSONObject();
                                home = eleMatch.getElementsByClass("home").text();
                                time = eleMatch.getElementsByClass("time").text();
                                away = eleMatch.getElementsByClass("away").text();

                                if (!home.isEmpty()) {
                                    //เก็บ logo เจ้าบ้าน 
                                    Elements elesHome = eleMatch.getElementsByClass("home");
                                    Element eleHomeImg = elesHome.select("img").first();
                                    homeImgUrl = func.getNewLinkImage(eleHomeImg.attr("src"));

                                    //เก็บ logo ทีมเยือน 
                                    Elements elesAway = eleMatch.getElementsByClass("away");
                                    Element eleAwayImg = elesAway.select("img").first();
                                    awayImgUrl = func.getNewLinkImage(eleAwayImg.attr("src"));

                                    homeAway = func.getHomeAway(home, away);
                                    daymatchesInter = dateTimes.getInterDate(daymatches);
                                    json.put("link", url);                               //ลิ้งก์
                                    json.put("season", season);                          //ฤดูกาล
                                    json.put("date", daymatchesInter);                   //วันที่                 
                                    json.put("date_thai", daymatches);                   //วันที่ไทย
                                    json.put("home", home);                              //เจ้าบ้าน
                                    json.put("time", time);                              //เวลาแข่งขัน
                                    json.put("away", away);                              //ทีมเยือน
                                    json.put("home_away", homeAway);                     //เจ้าบ้าน  ทีมเยือน
                                    json.put("home_img", baseLinkImg + homeImgUrl);      //โลโก้เจ้าบ้าน
                                    json.put("away_img", baseLinkImg + awayImgUrl);      //โลโก้ทีมเยือน
                                    els.inputElasticsearch(json.toString(), indexName);
                                    System.out.println(dateTimes.thaiDateTime() + " : insert fixtures ThaiPremierLeague complete");
                                }
                            }
                        }
                    }
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(ServicePremierLeagueImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void allTableThaiPremierLeagueContent(String url, String indexName, String season, String baseLinkImg) {
        try {
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Element ele = doc.getElementById("alltable");
            JSONObject json = null;

            Elements eles = ele.getElementsByClass("TableTeam");
            Element eleTr = eles.select("tr").first();  //เลือก <tr> แรก
            eleTr.remove();    //ลบ <tr> แรก

            Elements elesTrTag = eles.select("tr");
            for (Element eleResult : elesTrTag) {
                json = new JSONObject();
                json.put("type_table", "all_table");                                       //ประเภทตารางรวม

                Elements elesRank = eleResult.getElementsByClass("rank");
                Elements elesChildren = elesRank.select("*");
                for (Element eleRank : elesChildren) {
                    if (eleRank.hasClass("movement")) {
                        Element eleMovement = eleRank.select("span").first();
                        String movementClass = eleMovement.attr("class"); //เก็บชื่อ class
                        json.put("movement", movementClass);                               //สถานะลำดับ เช่น ขึ้น ลง คงที่   movement up,movement down,movement none  

                        String movementBefor = eleMovement.attr("title"); //เก็บชื่อ title
                        json.put("movement_befor", movementBefor);                         //อันดับก่อนหน้านี้
                    }
                }

                String nranking = eleResult.getElementsByClass("nranking").text();
                json.put("ranking", nranking);                                             //อันดับ

                Elements elesTeams = eleResult.getElementsByClass("team");
                if (elesTeams.hasClass("team")) {
                    Elements elesTeam = elesTeams.select("*");
                    for (Element eleTeam : elesTeam) {
                        if (eleTeam.tagName().equals("img")) {
                            Element eleImg = eleTeam.select("img").first();
                            String img = eleImg.attr("src");
                            img = func.getNewLinkImage(img);
                            img = baseLinkImg + img;  //++
                            String team = eleImg.attr("alt");
                            json.put("logo_team", img); //                                //โลโก้ทีม
                            json.put("team", team);                                       //ทีม  
                        }
                    }
                }

                String played = eleResult.getElementsByClass("pld").text();
                int playedInt = Integer.parseInt(played);
                json.put("played", playedInt);                                            //แข่ง

                String won = eleResult.getElementsByClass("w").text();
                int wonInt = Integer.parseInt(won);
                json.put("won", wonInt);                                                  //ชนะ

                String drawn = eleResult.getElementsByClass("d").text();
                int drawnInt = Integer.parseInt(drawn);
                json.put("drawn", drawnInt);                                              //เสมอ

                String lost = eleResult.getElementsByClass("l").text();
                int lostInt = Integer.parseInt(lost);
                json.put("lost", lostInt);                                                //แพ้

                String goalFor = eleResult.getElementsByClass("f").text();
                int goalForInt = Integer.parseInt(goalFor);
                json.put("goal_for", goalForInt);                                        //ได้

                String goalAgainst = eleResult.getElementsByClass("a").text();
                int goalAgainstInt = Integer.parseInt(goalAgainst);
                json.put("goal_against", goalAgainstInt);                                //เสีย

                String goalDifference = eleResult.getElementsByClass("gd").text();
                goalDifference = func.replaceGoalDifference(goalDifference);
                json.put("goal_difference", goalDifference);                             //ผลต่าง

                String points = eleResult.getElementsByClass("pts").text();
                int pointsInt = Integer.parseInt(points);
                json.put("points", pointsInt);                                           //คะแนน

                json.put("link", url);                                                   //ลิ้งก์                                    
                json.put("season", season);                                              //ฤดูกาล
                els.inputElasticsearch(json.toString(), indexName);
                System.out.println(dateTimes.thaiDateTime() + " : insert all-table ThaiPremierLeague complete");
            }
        } catch (IOException ex) {
            Logger.getLogger(ServicePremierLeagueImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void homeTableThaiPremierLeagueContent(String url, String indexName, String season, String baseLinkImg) {
        try {
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Element ele = doc.getElementById("hometable");
            JSONObject json = null;

            Elements eles = ele.getElementsByClass("TableTeam");
            Element eleTr = eles.select("tr").first();  //เลือก <tr> แรก
            eleTr.remove();    //ลบ <tr> แรก

            Elements elesTrTag = eles.select("tr");
            for (Element eleResult : elesTrTag) {
                json = new JSONObject();
                json.put("type_table", "home_table");                                           //ประเภทตารางเจ้าบ้าน

                String nranking = eleResult.getElementsByClass("nranking").text();
                json.put("ranking", nranking);                                                  //อันดับ

                Elements elesTeams = eleResult.getElementsByClass("team");
                if (elesTeams.hasClass("team")) {
                    Elements elesTeam = elesTeams.select("*");
                    for (Element eleTeam : elesTeam) {
                        if (eleTeam.tagName().equals("img")) {
                            Element eleImg = eleTeam.select("img").first();
                            String img = eleImg.attr("src");
                            img = func.getNewLinkImage(img);
                            img = baseLinkImg + img;
                            String team = eleImg.attr("alt");
                            json.put("logo_team", img);                                         //โลโก้ทีม
                            json.put("team", team);                                             //ทีม  
                        }
                    }
                }

                String played = eleResult.getElementsByClass("pld").text();
                int playedInt = Integer.parseInt(played);
                json.put("played", playedInt);                                                  //แข่ง

                String won = eleResult.getElementsByClass("w").text();
                int wonInt = Integer.parseInt(won);
                json.put("won", wonInt);                                                        //ชนะ

                String drawn = eleResult.getElementsByClass("d").text();
                int drawnInt = Integer.parseInt(drawn);
                json.put("drawn", drawnInt);                                                    //เสมอ

                String lost = eleResult.getElementsByClass("l").text();
                int lostInt = Integer.parseInt(lost);
                json.put("lost", lostInt);                                                      //แพ้

                String goalFor = eleResult.getElementsByClass("f").text();
                int goalForInt = Integer.parseInt(goalFor);
                json.put("goal_for", goalForInt);                                               //ได้

                String goalAgainst = eleResult.getElementsByClass("a").text();
                int goalAgainstInt = Integer.parseInt(goalAgainst);
                json.put("goal_against", goalAgainstInt);                                      //เสีย

                String goalDifference = eleResult.getElementsByClass("gd").text();
                goalDifference = func.replaceGoalDifference(goalDifference);
                json.put("goal_difference", goalDifference);                                   //ผลต่าง

                String points = eleResult.getElementsByClass("pts").text();
                int pointsInt = Integer.parseInt(points);
                json.put("points", pointsInt);                                                 //คะแนน

                json.put("link", url);                                                         //ลิ้งก์ 
                json.put("season", season);                                                    //ฤดูกาล
                els.inputElasticsearch(json.toString(), indexName);
                System.out.println(dateTimes.thaiDateTime() + " : insert home-table ThaiPremierLeague complete");
            }
        } catch (IOException ex) {
            Logger.getLogger(ServicePremierLeagueImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void awayTableThaiPremierLeagueContent(String url, String indexName, String season, String baseLinkImg) {
        try {
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Element ele = doc.getElementById("awaytable");
            JSONObject json = null;

            Elements eles = ele.getElementsByClass("TableTeam");
            Element eleTr = eles.select("tr").first();  //เลือก <tr> แรก
            eleTr.remove();    //ลบ <tr> แรก

            Elements elesTrTag = eles.select("tr");
            for (Element eleResult : elesTrTag) {
                json = new JSONObject();
                json.put("type_table", "away_table");                                          //ประเภทตารางทีมเยือน

                String nranking = eleResult.getElementsByClass("nranking").text();
                json.put("ranking", nranking);                                                 //อันดับ

                Elements elesTeams = eleResult.getElementsByClass("team");
                if (elesTeams.hasClass("team")) {
                    Elements elesTeam = elesTeams.select("*");
                    for (Element eleTeam : elesTeam) {
                        if (eleTeam.tagName().equals("img")) {
                            Element eleImg = eleTeam.select("img").first();
                            String img = eleImg.attr("src");
                            img = func.getNewLinkImage(img);
                            img = baseLinkImg + img;
                            String team = eleImg.attr("alt");
                            json.put("logo_team", img);                                        //โลโก้ทีม
                            json.put("team", team);                                            //ทีม  
                        }
                    }
                }

                String played = eleResult.getElementsByClass("pld").text();
                int playedInt = Integer.parseInt(played);
                json.put("played", playedInt);                                                 //แข่ง

                String won = eleResult.getElementsByClass("w").text();
                int wonInt = Integer.parseInt(won);
                json.put("won", wonInt);                                                       //ชนะ

                String drawn = eleResult.getElementsByClass("d").text();
                int drawnInt = Integer.parseInt(drawn);
                json.put("drawn", drawnInt);                                                   //เสมอ

                String lost = eleResult.getElementsByClass("l").text();
                int lostInt = Integer.parseInt(lost);
                json.put("lost", lostInt);                                                     //แพ้

                String goalFor = eleResult.getElementsByClass("f").text();
                int goalForInt = Integer.parseInt(goalFor);
                json.put("goal_for", goalForInt);                                              //ได้

                String goalAgainst = eleResult.getElementsByClass("a").text();
                int goalAgainstInt = Integer.parseInt(goalAgainst);
                json.put("goal_against", goalAgainstInt);                                      //เสีย

                String goalDifference = eleResult.getElementsByClass("gd").text();
                goalDifference = func.replaceGoalDifference(goalDifference);
                json.put("goal_difference", goalDifference);                                   //ผลต่าง

                String points = eleResult.getElementsByClass("pts").text();
                int pointsInt = Integer.parseInt(points);
                json.put("points", pointsInt);                                                 //คะแนน

                json.put("link", url);                                                         //ลิ้งก์ 
                json.put("season", season);                                                    //ฤดูกาล
                els.inputElasticsearch(json.toString(), indexName);
                System.out.println(dateTimes.thaiDateTime() + " : insert away-table ThaiPremierLeague complete");
            }

        } catch (IOException ex) {
            Logger.getLogger(ServicePremierLeagueImp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void allTableThaiPremierLeague2014Content(String url, String indexName, String season, String baseLinkImg) {
        try {
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Element ele = doc.getElementById("wrapper");
            JSONObject json = null;

            Elements eles = ele.getElementsByClass("TableTeam");
            Element eleTr = eles.select("tr").first();  //เลือก <tr> แรก
            eleTr.remove();    //ลบ <tr> แรก

            Elements elesTrTag = eles.select("tr");
            for (Element eleResult : elesTrTag) {
                json = new JSONObject();
                json.put("type_table", "all_table");                                          //ประเภทตารางเจ้าบ้าน

                String nranking = eleResult.getElementsByClass("nranking").text();
                json.put("ranking", nranking);                                                //อันดับ

                Elements elesTeams = eleResult.getElementsByClass("team");
                if (elesTeams.hasClass("team")) {
                    Elements elesTeam = elesTeams.select("*");
                    for (Element eleTeam : elesTeam) {
                        if (eleTeam.tagName().equals("img")) {
                            Element eleImg = eleTeam.select("img").first();
                            String img = eleImg.attr("src");
                            img = func.getNewLinkImage(img);
                            String team = eleImg.attr("alt");
                            json.put("logo_team_img", img);                                   //โลโก้ทีม
                            json.put("team", team);                                           //ทีม  
                        }
                    }
                }

                String played = eleResult.getElementsByClass("pld").text();
                int playedInt = Integer.parseInt(played);
                json.put("played", playedInt);                                                //แข่ง

                String won = eleResult.getElementsByClass("w").text();
                int wonInt = Integer.parseInt(won);
                json.put("won", wonInt);                                                      //ชนะ

                String drawn = eleResult.getElementsByClass("d").text();
                int drawnInt = Integer.parseInt(drawn);
                json.put("drawn", drawnInt);                                                  //เสมอ

                String lost = eleResult.getElementsByClass("l").text();
                int lostInt = Integer.parseInt(lost);
                json.put("lost", lostInt);                                                    //แพ้

                String goalFor = eleResult.getElementsByClass("f").text();
                int goalForInt = Integer.parseInt(goalFor);
                json.put("goal_for", goalForInt);                                             //ได้

                String goalAgainst = eleResult.getElementsByClass("a").text();
                int goalAgainstInt = Integer.parseInt(goalAgainst);
                json.put("goal_against", goalAgainstInt);                                     //เสีย

                String goalDifference = eleResult.getElementsByClass("gd").text();
                goalDifference = func.replaceGoalDifference(goalDifference);
                json.put("goal_difference", goalDifference);                                  //ผลต่าง

                String points = eleResult.getElementsByClass("pts").text();
                int pointsInt = Integer.parseInt(points);
                json.put("points", pointsInt);                                               //คะแนน

                json.put("link", url);                                                       //ลิ้งก์ 
                json.put("season", season);                                                  //ฤดูกาล
                els.inputElasticsearch(json.toString(), indexName);
                System.out.println(dateTimes.thaiDateTime() + " : insert all-table ThaiPremierLeague2014 complete");
            }

        } catch (IOException ex) {
            Logger.getLogger(ServicePremierLeagueImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void statistics(String url, String indexName, String season, String baseLinkImg) {
        String BaseLinkPalyer = "";
        if ("statistics_thaipremierleague".equals(indexName)) {
            BaseLinkPalyer = "http://www.livesoccer888.com/thaipremierleague";
        }
        if ("statistics_premierleague".equals(indexName)) {
            BaseLinkPalyer = "http://www.livesoccer888.com/premierleague";
        }
        try {
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Elements elements = doc.select(".content.main-content.left-content");

            List<String> listStats = new ArrayList<>();
            String strUrl;
            // เก็บ id component
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
                if ("topscores".equals(idStats)) {
                    json.put("statistics", "goals");                                 //ดาวซัลโว
                    json.put("link", url + "#topscores");                              //link
                }
                if ("topassists".equals(idStats)) {
                    json.put("statistics", "assists");                               //แอสซิสต์
                    json.put("link", url + "#topassists");                             //link
                }
                if ("cleansheets".equals(idStats)) {
                    json.put("statistics", "clean_sheets");                          //คลีนชีท
                    json.put("link", url + "#cleansheets");                            //link
                }
                if ("most-yellowcard".equals(idStats)) {
                    json.put("statistics", "yellow_cards");                          //ใบเหลือง
                    json.put("link", url + "#most-yellowcard");                        //link
                }
                if ("most-yellowredcard".equals(idStats)) {
                    json.put("statistics", "yellow_red_cards");                      //เหลือง/แดง
                    json.put("link", url + "#most-yellowredcard");                     //link
                }
                if ("most-redcard".equals(idStats)) {
                    json.put("statistics", "red_cards");                             //ใบแดง
                    json.put("link", url + "#most-redcard");                           //link
                }
                if ("best-minutes-per-goal".equals(idStats)) {
                    json.put("statistics", "minutes_played_goals");                  //ค่าเฉลี่ย : ประตู
                    json.put("link", url + "#best-minutes-per-goal");                  //link
                }
                Element ele = doc.getElementById(idStats);
                String notice = ele.select(".notice").text();
                json.put("date_update_th", notice);                                  //อัพเดตล่าสุดเมื่อ(แบบเดิม)

                String date_update = notice.replace("อัพเดตล่าสุดเมื่อ ", "");
                date_update = dateTimes.ddmmyyyyToyyyymmdd(date_update);
                json.put("date_update", date_update);                                //อัพเดตล่าสุดเมื่อ(แก้ไขใหม่)

                String title = ele.select(".statistics-title").text();  //title
                json.put("title", title);                                            //หัวข้อ

                Elements elesData = ele.getElementsByClass("data");
                String rank = null;
                for (Element eleData : elesData) {
                    jsonDetail = new JSONObject();
                    String rankValue = eleData.select(".rank").text();
                    if (rankValue.isEmpty()) {
                        jsonDetail.put("rank", rank);                                //อันดับ
                    } else {
                        rank = rankValue;
                        jsonDetail.put("rank", rank);                                //อันดับ
                    }

                    Elements eleName = eleData.select(".name");
                    Elements a = eleName.select("a");
                    String linkProfile = a.attr("href");
                    linkProfile = BaseLinkPalyer + func.getNewLinkImage(linkProfile);
                    jsonDetail.put("link_profile", linkProfile);                     //ลิ้งก็โปรไฟล์

                    String name = eleData.select(".name").text();
                    jsonDetail.put("name", name);                                    //ชื่อ

                    Elements logoTeam = eleData.select(".inimage");
                    Elements img = logoTeam.select("img");
                    String logo = img.attr("src");
                    logo = baseLinkImg + func.getNewLinkImage(logo);
                    jsonDetail.put("logo_team", logo);                               //โลโก้ทีม

                    String team = eleData.select(".inteam").text();
                    jsonDetail.put("team", team);                                    //ทีม

                    String stats = eleData.select(".stats").text();
                    if ("topscores".equals(idStats)) {
                        jsonDetail.put("goals", stats);                             //จำนวนดาวซัลโว
                    }
                    if ("topassists".equals(idStats)) {
                        jsonDetail.put("assists", stats);                           //จำนวนแอสซิสต์
                    }
                    if ("cleansheets".equals(idStats)) {
                        jsonDetail.put("clean_sheets", stats);                      //จำนวนคลีนชีท
                    }
                    if ("most-yellowcard".equals(idStats)) {
                        jsonDetail.put("yellow_cards", stats);                      //จำนวนใบเหลือง
                    }
                    if ("most-yellowredcard".equals(idStats)) {
                        jsonDetail.put("yellow_red_cards", stats);                  //จำนวนเหลือง/แดง
                    }
                    if ("most-redcard".equals(idStats)) {
                        jsonDetail.put("red_cards", stats);                         //จำนวนใบแดง
                    }
                    if ("best-minutes-per-goal".equals(idStats)) {
                        jsonDetail.put("minutes_played_goals", stats);              //จำนวนค่าเฉลี่ยนาที
                        String goal = eleData.select(".goal").text();
                        jsonDetail.put("goal", goal);                               //จำนวนประตู
                    }
                    arr.put(jsonDetail);
                }
                json.put("detail", arr);
                els.inputElasticsearch(json.toString(), indexName);
                System.out.println(dateTimes.thaiDateTime() + " : insert " + indexName + " complete");
            }
        } catch (IOException | JSONException e) {
            e.getMessage();
        }
    }

    @Override
    public void getStaffTeamDetail(String link, String url) {
        JSONObject json = new JSONObject();
        JSONObject jsonValue = new JSONObject(url);
        String teamValue = jsonValue.getString("team");
        String logoTeamValue = jsonValue.getString("logo_team");
        String index = jsonValue.getString("index");

        json.put("link", link);                                                 //ลิ้งก์
        json.put("team", teamValue);                                            //ทีม 
        json.put("logo_team", logoTeamValue);                                   //โลโก้ทีม
        String baseLink = "http://www.livesoccer888.com";
        try {
            Document doc = Jsoup.connect(link).timeout(60 * 1000).get();
            Elements elements = doc.select(".data_profile");

            Elements elesImg = elements.select(".image");
            Elements elesImgProfile = elesImg.select("img");
            String imgProfile = elesImgProfile.attr("src");
            imgProfile = baseLink + func.getNewLinkImage(imgProfile);
            json.put("img_profile", imgProfile);                                //รูปผู้จัดการทีม

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
            String[] partsEnName = enName.split(" : ");
            String keyEnName = partsEnName[0];
            keyEnName = func.staffTeamDetailToEnKey(keyEnName);
            String valueEnName = partsEnName[1];
            json.put(keyEnName, valueEnName);                                   //ชื่อภาษาอังกฤษ

            String thName = elements.select("p").last().text();
            String[] partsThName = thName.split(" : ");
            String keyThName = partsThName[0];
            keyThName = func.staffTeamDetailToEnKey(keyThName);
            String valueThName = partsThName[1];
            json.put(keyThName, valueThName);                                   //ชื่อภาษาไทย

            Elements elesUl = elements.select("ul");
            Elements elesUlChild = elesUl.select("*");
            int count = 0;
            String value = "";
            String key = "";
            for (Element ele : elesUlChild) {
                if (count > 0) {                                                //ไม่เอาค่าแรก
                    if (ele.tagName().equals("li")) {
                        value = ele.select("li").text();                        //ค่าที่ต้องการ
                    }
                    if (ele.tagName().equals("b")) {                            //เลือกหัวข้อของค่าที่ต้องการ
                        key = ele.select("b").text();
                        key = key.replace(" :", "");                            //ลบ _: ออก
                        String keyEn = func.staffTeamDetailToEnKey(key);

                        value = value.replace(key, "");
                        if (!value.isEmpty()) {                                 //เลือกเอาเฉพาะที่มีค่า
                            String firstChar = value.substring(0, 1);           //ตัดเอาตัวอักษรตัวแรก
                            if (!firstChar.isEmpty()) {
                                value = value.replace(" : ", "");               //ลบ _:_ ออก
                                json.put(keyEn, value);
                            }
                        }
                    }
                }
                count++;
            }

            //ประวัติข้อมูลการทำงาน
            JSONObject jsonDetailTransfer;
            JSONArray arrDetailTransfer = new JSONArray();
            Elements elementsContent = doc.select(".content.main-content.left-content._margt10");
            Element elesDataTransfer = elementsContent.select(".data_transfer").first(); //
            Elements elesContent = elesDataTransfer.select(".content");
            for (Element ele : elesContent) {
                jsonDetailTransfer = new JSONObject();
                String club = ele.select(".club").text();
                jsonDetailTransfer.put("club", club);                           //ทีมชาติ & สโมสร

                String appointed = ele.select(".appointed").text();
                jsonDetailTransfer.put("appointed", appointed);                 //แต่งตั้ง

                String inchange_until = ele.select(".inchange_until").text();
                jsonDetailTransfer.put("inchange_until", inchange_until);       //สิ้นสุด

                String function = ele.select(".function").text();
                jsonDetailTransfer.put("function", function);                   //ตำแหน่ง

                String matches = ele.select(".matches").text();
                jsonDetailTransfer.put("matches", matches);                     //คุมทีม

                arrDetailTransfer.put(jsonDetailTransfer);
            }
            json.put("staff_detail", arrDetailTransfer);
            //จบประวัติข้อมูลการทำงาน

            //ดูข้อมูลแบบละเอียดทั้งหมด
            Element elesDataTransferLast = elementsContent.select(".data_transfer").last();
            Elements elesFoot = elesDataTransferLast.select(".foot");
            Elements elesA = elesFoot.select("a");
            String linkDetail = elesA.attr("href");
            String newLinkDetail = baseLink + func.getNewLinkImage(linkDetail);
            //จบดูข้อมูลแบบละเอียดทั้งหมด

            JSONArray arrPerformanceDetail = new JSONArray();
            JSONObject jsonPerformanceDetail;
            Document doc2 = Jsoup.connect(newLinkDetail).timeout(60 * 1000).get();
            Elements elements2 = doc2.select(".data_played-full");
            Elements elesContentDetail = elements2.select(".content");

            if (!elesContentDetail.isEmpty()) {  //กรณีที่มีข้อมูลการคุมทีม
                //ข้อมูลการคุมทีม
                for (Element ele : elesContentDetail) {
                    jsonPerformanceDetail = new JSONObject();
                    String season = ele.select(".season").text();
                    jsonPerformanceDetail.put("season", season);                //ฤดูกาล

                    String competition = ele.select(".competition").text();
                    jsonPerformanceDetail.put("competition", competition);      //รายการแข่งขัน

                    String clubs = ele.select(".clubs").text();
                    jsonPerformanceDetail.put("clubs", clubs);                  //ทีมสโมสร

                    String matches = ele.select(".matches").text();
                    jsonPerformanceDetail.put("matches", matches);              //คุมทีม (แมตช์)

                    String win = ele.select(".data_").get(0).text();
                    jsonPerformanceDetail.put("win", win);                      //ชนะ

                    String draw = ele.select(".data_").get(1).text();
                    jsonPerformanceDetail.put("draw", draw);                    //เสมอ

                    String lose = ele.select(".data_").get(2).text();
                    jsonPerformanceDetail.put("lose", lose);                    //แพ้

                    String points = ele.select(".data_").get(3).text();
                    jsonPerformanceDetail.put("points", points);                //คะแนนรวม

                    String ranking = ele.select(".data_").get(4).text();
                    jsonPerformanceDetail.put("ranking", ranking);              //อันดับของทีม

                    String players_used = ele.select(".data").text();
                    jsonPerformanceDetail.put("players_used", players_used);    //ใช้นักเตะไปทั้งหมด

                    arrPerformanceDetail.put(jsonPerformanceDetail);
                }
                json.put("staff_performance_detail", arrPerformanceDetail);
            }
            els.inputElasticsearch(json.toString(), index);
            System.out.println(dateTimes.thaiDateTime() + " : insert " + index + " complete");
        } catch (IOException | JSONException e) {
            e.getMessage();
        }
    }

    @Override
    public void listTeamThaiPremierLeague(String url, String indexName, String season, String detail, String baseLink) {
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
                logo = baseLink + func.getNewLinkImage(logo);
                json.put("logo_team", logo);

                String team = ele.select(".getCodeTeam").text();
                json.put("team", team);
                try {
                    String teamId = md5.encrypt(team);
                    json.put("team_id", teamId);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
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
                    imgProfile = baseLink + func.getNewLinkImage(imgProfile);
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
                                playerImg = baseLink + func.getNewLinkImage(playerImg);
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
                        linkProfile = baseLink + func.getNewLinkImage(linkProfile);
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
                                    String editTeam  = func.changeTeamThaiPremierLeague(season, team, club);
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

                                        jsonPlayedLeagueDetail.put("average_statistics", 8.25);
                                        jsonDetail.put("performance_detail", jsonPlayedLeagueDetail);
                                    }
                                } else {  //กรณีนักเตะผู้เล่นตำแหน่งอื่นๆ
                                    String subSeason = eleContent.select(".season").text();            //ฤดูกาล
                                    String club = eleContent.select(".club").text();                //ทีมสโมสร

                                    //System.out.println(plink);
                                    jsonDetail.put("link_performance_detail", plink);
                                    String editTeam  = func.changeTeamThaiPremierLeague(season, team, club);
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

                                        jsonPlayedLeagueDetail.put("average_statistics", 8.25);
                                        jsonDetail.put("performance_detail", jsonPlayedLeagueDetail);
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
                                        String editTeam  = func.changeTeamThaiPremierLeague(season, team, club);
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

                                            jsonPlayedLeagueDetail.put("average_statistics", 8.25);
                                            jsonDetail.put("performance_detail", jsonPlayedLeagueDetail);
                                        }
                                    } else {  //กรณีนักเตะผู้เล่นตำแหน่งอื่นๆ
                                        String subSeason = eleContent.select(".season").text();            //ฤดูกาล
                                        String club = eleContent.select(".club").text();                //ทีมสโมสร

                                        //System.out.println(plink);
                                        jsonDetail.put("link_performance_detail", plink);
                                        String editTeam  = func.changeTeamThaiPremierLeague(season, team, club);
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

                                            jsonPlayedLeagueDetail.put("average_statistics", 8.25);
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
                els.inputElasticsearch(json.toString(), "part_players_detail_thaipremierleague");
                System.out.println(dateTimes.thaiDateTime() + " : insert part_players_detail_thaipremierleague complete");            
            }
        } catch (IOException | JSONException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void presentTeamThaiPremierLeague(String objRadis) {
        Jedis redis = rd.connect();
        JSONObject obj = new JSONObject(objRadis);
        String url = obj.getString("link");
        String baseLink = obj.getString("base_logo_link");
        String season = obj.getString("season");
        
        JSONObject json ;
        String newLink = url.replace("index.php", "");
        try {
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Elements elements = doc.select(".content.main-content.left-content");
            //String title = elements.select(".title-page").text();
            
            Elements eles = elements.select(".MatchTeamFull");
            for (Element ele : eles) {
                json = new JSONObject();
                json.put("link", url);
                json.put("season", season);
                json.put("base_logo_link", baseLink);
                
                Elements a = ele.select("a");
                String strUrl = a.attr("href");
                String linkPage = newLink + strUrl;
                json.put("link_team", linkPage);

                Elements logoTeam = ele.select(".MatchLogoDivFull");            //logo
                Elements img = logoTeam.select("img");
                String logo = img.attr("src");
                logo = baseLink + func.getNewLinkImage(logo);
                json.put("logo_team", logo);

                String team = ele.select(".getCodeTeam").text();
                json.put("team", team);

                try {
                    String teamId = md5.encrypt(team);
                    json.put("team_id", teamId);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                
                json.put("type", "present_teams_detail_thaipremierleague");     // ประแกาศเพื่อส่งต่อไปยังฟังก็ชันถัดไป
                redis.rpush("pages", json.toString());
                els.inputElasticsearch(json.toString(),"present_teams_thaipremierleague");
                System.out.println(dateTimes.thaiDateTime() + " : insert present_teams_thaipremierleague complete");   
            }
        } catch (IOException | JSONException e) {
            System.out.print(e.getMessage());
        }
        redis.close();
    }

    @Override
    public void presentPlayerThaiPremierLeague(String objRadis) {
        Jedis redis = rd.connect();
        JSONObject obj = new JSONObject(objRadis);
        String baseLink = obj.getString("base_logo_link");
        String teamId = obj.getString("team_id");
        String team = obj.getString("team");
        String logoTeam = obj.getString("logo_team");
        String linkTeam = obj.getString("link_team");
        String season = obj.getString("season");
        try {
            String link = linkTeam.replace("index.php", "");
            // หา link เพื่อไปยังนักเตะทีมนั้นๆ
            Document docLink = Jsoup.connect(linkTeam).timeout(60 * 1000).get();
            Element elements = docLink.select(".read-all-box").first();
            Elements elesA = elements.select("a");
            String hrefValue = elesA.attr("href");
            String newLink = link + hrefValue;

            JSONObject json = new JSONObject();
            json.put("team_id", teamId);
            json.put("team", team);
            json.put("logo_team", logoTeam);
            
            json.put("link", linkTeam);
            json.put("season", season);
            
            // หารายชื่อนักเตะ
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
                jsonDetailPlayers.put("player_team_id", teamId);
                jsonDetailPlayers.put("player_team", team);
                jsonDetailPlayers.put("player_logo_team", logoTeam);
                jsonDetailPlayers.put("season", season);
                
                String linkProfile = ele.attr("data-href");
                linkProfile = baseLink + func.getNewLinkImage(linkProfile);
                jsonDetailPlayers.put("link_profile", linkProfile);             //ลิ้งก์โปรไฟล์

                String name = ele.attr("title");
                jsonDetailPlayers.put("player_name", name);                     //ชื่อนักเตะ  

                try {
                    String nameId = md5.encrypt(name);
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
                            img = baseLink + func.getNewLinkImage(img);
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
                
                arrDetailPlayers.put(jsonDetailPlayers);
                redis.rpush("pages", jsonDetailPlayers.toString());
            }
            json.put("players", arrDetailPlayers);
            els.inputElasticsearch(json.toString(), "present_teams_detail_thaipremierleague");
            System.out.println(dateTimes.thaiDateTime() + " : insert present_teams_detail_thaipremierleague complete"); 
        } catch (IOException | JSONException e) {
            System.out.println(e.getMessage());
        } 
        redis.close();
    }

    @Override
    public void presentPlayerDetailThaiPremierLeague(String objRadis) {
        
        JSONObject obj = new JSONObject(objRadis);
        String position = obj.getString("position");
        String linkProfile = obj.getString("link_profile");
        String playerTeam = obj.getString("player_team");
        String playerTeamId = obj.getString("player_team_id");
        String playerlogoTeam = obj.getString("player_logo_team");
        String playerNameId = obj.getString("player_name_id");
        String season = obj.getString("season");
        String imgPlayer = obj.getString("img_player");
        
        JSONObject json = new JSONObject();
        json.put("team", playerTeam);
        json.put("team_id", playerTeamId);
        json.put("logo_team", playerlogoTeam);
        json.put("player_name_id", playerNameId);
        json.put("link", linkProfile);
        json.put("season", season);
        json.put("img_player", imgPlayer);
        try {
            Document docLinkProfile = Jsoup.connect(linkProfile).timeout(60 * 1000).get();
            Element elesDataPlayer = docLinkProfile.select(".data_played").first();
            Elements elesFoot = elesDataPlayer.select(".foot");
            Element eleImgFoot = elesFoot.select("a").first();
            String imgData = eleImgFoot.attr("href");
            String[] arrStr = imgData.split("/");
            String performanceDetailLink = linkProfile + "/" + arrStr[1];

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
                String[] arrayLi = li.split(" : ");;
                String keyJson = func.playerDetailEnKey(arrayLi[0]); 
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
            }
            els.inputElasticsearch(json.toString(), "present_players_detail_thaipremierleague");
            System.out.println(dateTimes.thaiDateTime() + " : insert present_players_detail_thaipremierleague complete");
        } catch (IOException | JSONException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void getContentScoreAnalyzePage(String objRadis) {
        JSONObject obj = new JSONObject(objRadis);
        String url = obj.getString("link");
        String season = obj.getString("season");
        
        String baseLink = url.replace("index.php", "");
        String nextpage;
        boolean check = true;
        List<String> listPage = new ArrayList<>();
        try {
            // first page get link content
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Elements elements = doc.select(".pre-table._border");
            Elements elesTr = elements.select("tr");
            for (Element ele : elesTr) {
                Elements elesPreviews = ele.select(".pre-views");
                Elements a = elesPreviews.select("a");
                String strUrl = a.attr("href");
                if (!strUrl.isEmpty()) {
                    String linkContent = baseLink + strUrl;
                    listPage.add(linkContent);
                }
            }
            Elements elesNextPage = doc.select("#pager_links");
            Elements elesNext = elesNextPage.select(".next_page");
            if (elesNext.isEmpty()) {
                check = false;
            }
            Elements a = elesNext.select("a");
            String pathNextPage = a.attr("href");
            nextpage = url + pathNextPage;

            // next page get link content
            while (check) {
                Document docNextPage = Jsoup.connect(nextpage).timeout(60 * 1000).get();
                Elements elementsNextPage = docNextPage.select(".pre-table._border");
                Elements elesTrNextPage = elementsNextPage.select("tr");
                for (Element ele : elesTrNextPage) {
                    Elements elesPreviewsNextPage = ele.select(".pre-views");
                    Elements aNextPage = elesPreviewsNextPage.select("a");
                    String strUrl = aNextPage.attr("href");
                    if (!strUrl.isEmpty()) {
                        String linkContent = baseLink + strUrl;
                        listPage.add(linkContent);
                    }
                }
                Elements elesNextPage2 = docNextPage.select("#pager_links");
                Elements elesNext2 = elesNextPage2.select(".next_page");
                if (elesNext2.isEmpty()) {
                    check = false;
                }
                Elements a2 = elesNext2.select("a");
                String pathNextPage2 = a2.attr("href");
                nextpage = url + pathNextPage2;
            }
            // get content from list
            for (String contentlink : listPage) {
                JSONObject json = new JSONObject();
                Document docContent = Jsoup.connect(contentlink).timeout(60 * 1000).get();
                Elements elementsContent = docContent.select(".col-1");
                String homeAway = elementsContent.select("h1").text();
                String dateThai = elementsContent.select(".datecontents").text();
                homeAway = homeAway.replace("วิเคราะห์บอล ไทยลีก คู่ ", "");  // กรณีไทยลีก
                homeAway = homeAway.replace("วิเคราะห์บอล พรีเมียร์ลีก อังกฤษ คู่ ", "");  // กรณีพรีเมียร์ลีก
                homeAway = homeAway.replace("VS", "-");
                homeAway = func.changeMultiNameScoreAnalyze(homeAway);  // เปลี่ยนชื่อให้ตรงกับชื่อทีมที่จัดเก็บ

                dateThai = dateThai.replace("โพสต์เมื่อ : ", "");
                String date = dateTimes.getInterDate(dateThai);
                // content
                Elements elesContent = docContent.select(".col-1._margt");
                String content = elesContent.select(".txt_preview").text();
                String scoreAnalyze = elesContent.select(".ScoreAnalyzeList").text();

                json.put("link", contentlink);
                json.put("season", season);
                json.put("home_away", homeAway);
                try {
                    json.put("home_away_id", md5.encrypt(homeAway));
                } catch (NoSuchAlgorithmException | JSONException e) {
                    System.out.println(e.getMessage());
                }
                json.put("date_thai", dateThai);
                json.put("date", date);
                json.put("content", content);
                json.put("score_analyze", scoreAnalyze);
                els.inputElasticsearch(json.toString(), "score_analyze_thaipremierleague");
                System.out.println(dateTimes.thaiDateTime() + " : insert score_analyze_thaipremierleague complete");
            }
        } catch (IOException | JSONException e) {
            System.out.print(e.getMessage());
        }
    }

}
