package app.controller;

import app.dao.Redis;
import app.function.DateTimes;
import app.service.ServicePremierLeague;
import app.service.ServiceStats;
import app.service.ServiceThaiPremierLeague;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Component
public class Controller {
    
    @Autowired
    private DateTimes dateTimes;

    @Autowired
    private Redis rd;

    @Autowired
    private ServicePremierLeague servicePremierLeague;
    
    @Autowired
    private ServiceStats serviceStats;

    @Autowired
    private ServiceThaiPremierLeague serviceThaiPremierLeague;

    //@Scheduled(cron = "${cron_every_1_min}")  //  ทุกๆ 1 นาที   
    @Scheduled(cron = "#{@cronExpressionValue}") 
    public void runTask() {     
        System.out.println(dateTimes.interDateTime()+" : web scrapping input database runTask start");
        Jedis redis = rd.connect();
        JSONObject json;
        String url = "";
        String type;
        String season;
        String link;
        String baseLogoLink;

        boolean flag = true;

        while (flag) {
            url = redis.rpop("pages");
            if (url == null) {
                System.out.println(dateTimes.interDateTime()+" : web scrapping input database runTask stop");
                return;
            }
            json = new JSONObject(url);
            type = json.getString("type");

            if ("stats".equals(type)) {
                String linkStats = json.getString("link");
                String league = json.getString("league");
                String typeDetail = json.getString("type_detail");
                if ("stats_of_team".equals(typeDetail)) {
                    serviceStats.statsOfTeam(linkStats, league);                         //สถิติต่างๆของสโมสรฟุตบอลในลีก
                }
                if ("team_detail".equals(typeDetail)) {
                    serviceStats.teamDetail(linkStats, league);                          //ผู้เล่นนักเตะทีม และ สถิตินักเตะ
                }
                if ("players_profile".equals(typeDetail)) {
                    if("thaipremierleague".equals(league)){
                        serviceStats.thaipremierleaguePlayerProfile(linkStats);          //ข้อมูลและประวัตินักเตะไทยลีก
                    }
                    if("premierleague".equals(league)){
                        serviceStats.premierleaguePlayerProfile(linkStats);              //ข้อมูลและประวัตินักเตะพรีเมียร์ลีก อังกฤษ
                    }  
                } 
            } else if ("staff".equals(type)) {
              String linkStaff = json.getString("link");
              String index = json.getString("index");
              if("staff_thaipremierleague".equals(index)){
                  serviceThaiPremierLeague.getStaffTeamDetail(linkStaff, url);                      //ข้อมูลผู้จัดการทีมไทยลีก
              }
              if("staff_premierleague".equals(index)){
                  servicePremierLeague.getStaffTeamDetail(linkStaff, url);                       //ข้อมูลผู้จัดการทีมพรีเมียร์ลีก อังกฤษ
              }              
            } 
            else {
                link = json.getString("link");
                season = json.getString("season");
                baseLogoLink = json.getString("base_logo_link");
                if ("results_thaipremierleague".equals(type)) {
                    String detail = json.getString("detail");
                    if ("present_results".equals(detail)) {
                        serviceThaiPremierLeague.resultsPresentContent(link, type, season, baseLogoLink);      //ผลการแข่งขันไทยลีก (ปัจจุบัน)
                    } else {
                        serviceThaiPremierLeague.resultsContent(link, type, season, baseLogoLink);             //ผลการแข่งขันไทยลีก 
                    }
                }
                if ("results_premierleague".equals(type)) {
                    String detail = json.getString("detail");
                    if ("present_results".equals(detail)) {
                        servicePremierLeague.resultsPresentContent(link, type, season, baseLogoLink);       //ผลการแข่งขันพรีเมียร์ลีก อังกฤษ (ปัจจุบัน)
                    } else {
                        servicePremierLeague.resultsContent(link, type, season, baseLogoLink);              //ผลการแข่งขันพรีเมียร์ลีก อังกฤษ 
                    }
                }
                if ("fixtures_thaipremierleague".equals(type)) {    
                    serviceThaiPremierLeague.fixturesContent(link, type, season, baseLogoLink);                //ตารางแข่งขันฟุตบอลไทยลีก
                }
                if ("fixtures_premierleague".equals(type)) {
                    servicePremierLeague.fixturesContent(link, type, season, baseLogoLink);                 //ตารางแข่งขันฟุตบอลพรีเมียร์ลีก อังกฤษ
                }
                if ("league_table_thaipremierleague".equals(type)) {
                    if ("2014".equals(season)) {                       
                        serviceThaiPremierLeague.allTableThaiPremierLeague2014Content(link, type, season, baseLogoLink);    //ตารางคะแนนรวมไทยลีก 2014
                    } else {
                        serviceThaiPremierLeague.allTableThaiPremierLeagueContent(link, type, season, baseLogoLink);        //ตารางคะแนนรวมไทยลีก
                        serviceThaiPremierLeague.homeTableThaiPremierLeagueContent(link, type, season, baseLogoLink);       //ตารางคะแนนเจ้าบ้านไทยลีก
                        serviceThaiPremierLeague.awayTableThaiPremierLeagueContent(link, type, season, baseLogoLink);       //ตารางคะแนนทีมเยือนไทยลีก
                    }
                }
                if ("league_table_premierleague".equals(type)) {
                    if ("2014-2015".equals(season)) {
                        servicePremierLeague.allTablePremierLeague2014_2015Content(link, type, season, baseLogoLink);    //ตารางคะแนนรวมพรีเมียร์ลีก อังกฤษ 2014-2015
                    } else {
                        servicePremierLeague.allTablePremierLeagueContent(link, type, season, baseLogoLink);             //ตารางคะแนนรวมพรีเมียร์ลีก อังกฤษ
                        servicePremierLeague.homeTablePremierLeagueContent(link, type, season, baseLogoLink);            //ตารางคะแนนเจ้าบ้านพรีเมียร์ลีก อังกฤษ
                        servicePremierLeague.awayTablePremierLeagueContent(link, type, season, baseLogoLink);            //ตารางคะแนนทีมเยือนพรีเมียร์ลีก อังกฤษ
                    }
                }                
                if ("statistics_thaipremierleague".equals(type)) { 
                    serviceThaiPremierLeague.statistics(link, type, season, baseLogoLink);                               //รวมสถิติต่างๆไทยลีก
                }                
                if ("statistics_premierleague".equals(type)) {  
                    servicePremierLeague.statistics(link, type, season, baseLogoLink);                                   //รวมสถิติต่างๆพรีเมียร์ลีก อังกฤษ
                }    
                
                if ("teams_thaipremierleague".equals(type)) {
                    String detail = json.getString("detail");
                    serviceThaiPremierLeague.listTeamThaiPremierLeague(link, type, season, detail, baseLogoLink);        //list รายชื่อนักเตะและผู้จัดการทีมไทยลีก
                }
                if ("teams_premierleague".equals(type)) {
                    String detail = json.getString("detail");
                    servicePremierLeague.listTeamPremierLeague(link, type, season, detail, baseLogoLink);                //list รายชื่อนักเตะและผู้จัดการทีมพรีเมียร์ลีก 
                } 
                
            }     
        }
        System.out.println(dateTimes.interDateTime()+" : web scrapping input database runTask stop");
    }
}
