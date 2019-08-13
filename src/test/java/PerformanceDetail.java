
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;


public class PerformanceDetail {
    private String own_goals;
    private String yellow_red_cards;
    private String yellow_cards;
    private String red_cards;
    private String competition;
    private String matches;
    private String substituted_off;
    private String minutes_played;
    private String club;
    private String season;
    private String goals;
    private String substituted_on;
    private String goals_conceded;
    private String clean_sheets;
    private String assists;
    private String penalty_goals;
    private String minutes_played_goals;

    public PerformanceDetail() {
    }
    public PerformanceDetail(String own_goals, String yellow_red_cards, String yellow_cards, String red_cards, String competition, String matches, String substituted_off, String minutes_played, String club, String season, String goals, String substituted_on, String goals_conceded, String clean_sheets) {
        this.own_goals = own_goals;
        this.yellow_red_cards = yellow_red_cards;
        this.yellow_cards = yellow_cards;
        this.red_cards = red_cards;
        this.competition = competition;
        this.matches = matches;
        this.substituted_off = substituted_off;
        this.minutes_played = minutes_played;
        this.club = club;
        this.season = season;
        this.goals = goals;
        this.substituted_on = substituted_on;
        this.goals_conceded = goals_conceded;
        this.clean_sheets = clean_sheets;
    }
    @Override
    public String toString() {
        return "PerformanceDetail{" + "own_goals=" + own_goals + ", yellow_red_cards=" + yellow_red_cards + ", yellow_cards=" + yellow_cards + ", red_cards=" + red_cards + ", competition=" + competition + ", matches=" + matches + ", substituted_off=" + substituted_off + ", minutes_played=" + minutes_played + ", club=" + club + ", season=" + season + ", goals=" + goals + ", substituted_on=" + substituted_on + ", goals_conceded=" + goals_conceded + ", clean_sheets=" + clean_sheets + '}';
    }

    public String getOwn_goals() {
        return own_goals;
    }

    public void setOwn_goals(String own_goals) {
        this.own_goals = own_goals;
    }

    public String getYellow_red_cards() {
        return yellow_red_cards;
    }

    public void setYellow_red_cards(String yellow_red_cards) {
        this.yellow_red_cards = yellow_red_cards;
    }

    public String getYellow_cards() {
        return yellow_cards;
    }

    public void setYellow_cards(String yellow_cards) {
        this.yellow_cards = yellow_cards;
    }

    public String getRed_cards() {
        return red_cards;
    }

    public void setRed_cards(String red_cards) {
        this.red_cards = red_cards;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }

    public String getMatches() {
        return matches;
    }

    public void setMatches(String matches) {
        this.matches = matches;
    }

    public String getSubstituted_off() {
        return substituted_off;
    }

    public void setSubstituted_off(String substituted_off) {
        this.substituted_off = substituted_off;
    }

    public String getMinutes_played() {
        return minutes_played;
    }

    public void setMinutes_played(String minutes_played) {
        this.minutes_played = minutes_played;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getSubstituted_on() {
        return substituted_on;
    }

    public void setSubstituted_on(String substituted_on) {
        this.substituted_on = substituted_on;
    }

    public String getGoals_conceded() {
        return goals_conceded;
    }

    public void setGoals_conceded(String goals_conceded) {
        this.goals_conceded = goals_conceded;
    }

    public String getClean_sheets() {
        return clean_sheets;
    }

    public void setClean_sheets(String clean_sheets) {
        this.clean_sheets = clean_sheets;
    }

    public String getAssists() {
        return assists;
    }

    public void setAssists(String assists) {
        this.assists = assists;
    }

    public String getPenalty_goals() {
        return penalty_goals;
    }

    public void setPenalty_goals(String penalty_goals) {
        this.penalty_goals = penalty_goals;
    }

    public String getMinutes_played_goals() {
        return minutes_played_goals;
    }

    public void setMinutes_played_goals(String minutes_played_goals) {
        this.minutes_played_goals = minutes_played_goals;
    }
    
    
    public String getPerformanceDetail(String index, String nameId) {
        String values = null;
        try {
            HttpResponse<String> response = Unirest.post("http://localhost:9200/" + index + "/_search")
                    .header("Content-Type", "application/json")
                    .header("cache-control", "no-cache")
                    .body("{\"size\": 100,\"query\": {\"bool\": {\"must\": {\"match_phrase\": {\"player_name_id\": \"" + nameId + "\"}}}}}")
                    .asString();
            values = response.getBody();
        } catch (UnirestException ex) {
            Logger.getLogger(PlayerDetail.class.getName()).log(Level.SEVERE, null, ex);
        }
        return values.toString();
    }
    
    public List<PerformanceDetail> listPerformanceDetail(String performanceDetailValue ,String inputPosition) {
        List<PerformanceDetail> list = new ArrayList<>();
        JSONObject objResultsValue = new JSONObject(performanceDetailValue);
        JSONObject objHits = objResultsValue.getJSONObject("hits");
        JSONArray arrHits = objHits.getJSONArray("hits");
        for (int i = 0; i < arrHits.length(); i++) {
            JSONObject obj_source = arrHits.getJSONObject(i).getJSONObject("_source");
            JSONArray arrPerDetail = obj_source.getJSONArray("performance_detail");
            for (int j = 0; j < arrPerDetail.length(); j++) { 
               String l_own_goals = arrPerDetail.getJSONObject(j).getString("own_goals"); 
               String l_yellow_red_cards = arrPerDetail.getJSONObject(j).getString("yellow_red_cards"); 
               String l_yellow_cards = arrPerDetail.getJSONObject(j).getString("yellow_cards"); 
               String l_red_cards = arrPerDetail.getJSONObject(j).getString("red_cards"); 
               String l_competition = arrPerDetail.getJSONObject(j).getString("competition"); 
               String l_matches = arrPerDetail.getJSONObject(j).getString("matches"); 
               String l_substituted_off = arrPerDetail.getJSONObject(j).getString("substituted_off"); 
               String l_minutes_played = arrPerDetail.getJSONObject(j).getString("minutes_played"); 
               String l_club = arrPerDetail.getJSONObject(j).getString("club"); 
               String l_season = arrPerDetail.getJSONObject(j).getString("season"); 
               String l_goals = arrPerDetail.getJSONObject(j).getString("goals"); 
               String l_substituted_on = arrPerDetail.getJSONObject(j).getString("substituted_on");
               
                //กรณีผู้รักษาประตู
                if ("ผู้รักษาประตู".equals(inputPosition)) {
                    String l_goals_conceded = arrPerDetail.getJSONObject(j).getString("goals_conceded");
                    String l_clean_sheets = arrPerDetail.getJSONObject(j).getString("clean_sheets");
                    PerformanceDetail p = new PerformanceDetail(l_own_goals, l_yellow_red_cards, l_yellow_cards, l_red_cards, l_competition, l_matches,
                                                                l_substituted_off, l_minutes_played, l_club, l_season, l_goals, l_substituted_on, l_goals_conceded, l_clean_sheets);
                    list.add(p);
                } else { //กรณีผู้เล่นอื่นๆ
                    String l_assists = arrPerDetail.getJSONObject(j).getString("assists");
                    String l_penalty_goals = arrPerDetail.getJSONObject(j).getString("penalty_goals");
                    String l_minutes_played_goals = arrPerDetail.getJSONObject(j).getString("minutes_played_goals");
                }

               
             
               //list.add(p);
            }
        }
        System.out.println(list);
        return list;
    }    
    
    public static void main(String [] args){
        PerformanceDetail p = new PerformanceDetail();
        String str = p.getPerformanceDetail("present_players_detail_thaipremierleague", "6cfbad460857a6a1483ada3903b85953");
        p.listPerformanceDetail(str,"ผู้รักษาประตู");
    }
}
