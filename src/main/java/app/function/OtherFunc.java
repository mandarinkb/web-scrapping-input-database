package app.function;

import org.springframework.stereotype.Service;

@Service
public class OtherFunc {

    public String getHomeAway(String home, String away) {
        String mix;
        return mix = home + " - " + away;
    }

    public String getNewLinkImage(String url) {
        url = url.replace("../../../..", "");
        url = url.replace("../..", "");
        url = url.replace("/..", "");
        url = url.replace("..", "");
        return url;
    }

    public String replaceGoalDifference(String gd) {
        return gd = gd.replace("\u2013", "-");  // เปลี่ยน \u2013 เป็น -
    }

    public String statsOfTeamEnKey(String inputKey) {
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

    public String detailPlayerToEnKey(String inputKey) {
        String key = "";
        if ("วันเกิด".equals(inputKey)) {
            key = "birthday";
        }
        if ("อายุ".equals(inputKey)) {
            key = "age";
        }
        if ("ส่วนสูง".equals(inputKey)) {
            key = "height";
        }
        if ("สัญชาติ".equals(inputKey)) {
            key = "nationality";
        }
        if ("สวมเสื้อเบอร์".equals(inputKey)) {
            key = "squad_nember";
        }
        if ("ตำแหน่ง".equals(inputKey)) {
            key = "position";
        }
        if ("ถนัดเท้า".equals(inputKey)) {
            key = "footed";
        }
        if ("เซ็นสัญญาเมื่อ".equals(inputKey)) {
            key = "sign_contract";
        }
        if ("สิ้นสุดสัญญา".equals(inputKey)) {
            key = "end_contract";
        }
        if ("สโมสรเดิม".equals(inputKey)) {
            key = "original_club";
        }
        if ("ปัจจุบันสังกัดทีมชาติ".equals(inputKey)) {
            key = "currently_national_team";
        }
        if ("อดีตสังกัดทีมชาติ".equals(inputKey)) {
            key = "former_national_team";
        }
        return key;
    }

    public String titleToEnKey(String inputKey) {
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
    public String staffTeamDetailToEnKey(String inputKey) {
        String key = "";
        if ("ชื่ออังกฤษ".equals(inputKey)) {
            key = "en_name";
        }
        if ("ชื่อไทย".equals(inputKey)) {
            key = "th_name";
        }
        if ("วันเกิด".equals(inputKey)) {
            key = "birthday";
        }
        if ("อายุ".equals(inputKey)) {
            key = "age";
        }
        if ("สัญชาติ".equals(inputKey)) {
            key = "nationality";
        }
        if ("ตำแหน่งในปัจจุบัน".equals(inputKey)) {
            key = "position";
        }
        if ("ทีม".equals(inputKey)) {
            key = "team";
        }
        if ("สิ้นสุดสัญญา".equals(inputKey)) {
            key = "end_contract";
        }
        if ("รูปแบบแผนที่ชอบ".equals(inputKey)) {
            key = "football_plan";
        }
        return key;
    } 
    public String changeTeamThaiPremierLeague(String season, String team, String club){
        String newTeam = team;
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
    
    public String changeNameScoreAnalyze(String team){
        if ("ราชบุรี มิตรผล".equals(team)) {
            team = "ราชบุรี มิตรผล เอฟซี";
        } else if ("เชียงราย ยูไนเต็ด".equals(team)) {
            team = "สิงห์ เชียงราย ยูไนเต็ด";
        } else if ("เอสซีจี เมืองทอง".equals(team)) {
            team = "เอสซีจี เมืองทอง ยูไนเต็ด";
        } else if ("แบงค็อก ยูไนเต็ด".equals(team)) {
            team = "ทรู แบงค็อก ยูไนเต็ด";
        } else if ("นครราชสีมา เอฟซี".equals(team)) {
            team = "นครราชสีมา มาสด้า เอฟซี";
        } else if ("สมุทรปราการ ซิตี้".equals(team)) {
            team = "สมุทรปราการ ซิตี้ เอฟซี";
        } else if ("พีที ประจวบ".equals(team)) {
            team = "พีที ประจวบ เอฟซี";
        } else if ("แมนฯ ซิตี้".equals(team)) {
            team = "แมนเชสเตอร์ ซิตี้";
        } else if ("แมนฯ ยูไนเต็ด".equals(team)) {
            team = "แมนเชสเตอร์ ยูไนเต็ด";
        } else if ("นิวคาสเซิล".equals(team)) {
            team = "นิวคาสเซิล ยูไนเต็ด";
        } else if ("สเปอร์ส".equals(team)) {
            team = "ท็อตแน่ม ฮ็อทสเปอร์";
        } else if ("เวสต์แฮม".equals(team)) {
            team = "เวสต์แฮม ยูไนเต็ด";
        } else if ("บอร์นมัธ".equals(team)) {
            team = "เอเอฟซี บอร์นมัธ";
        } else if ("ไบรท์ตัน".equals(team)) {
            team = "ไบรท์ตัน แอนด์ โฮฟ อัลเบียน";
        } else if ("วูล์ฟแฮมป์ตัน".equals(team)) {
            team = "วูล์ฟแฮมป์ตัน วันเดอร์เรอร์ส";
        } else if ("เชฟฯ ยูไนเต็ด".equals(team)) {
            team = "เชฟฟิลด์ ยูไนเต็ด";
        }
        return team;
    }
    public String changeMultiNameScoreAnalyze(String homeAway){
        String[] splitStr = homeAway.split(" - ");
        String home = splitStr[0];
        String away = splitStr[1];
        home = changeNameScoreAnalyze(home);
        away = changeNameScoreAnalyze(away);
        return home + " - " + away;
    }   
}
