
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;



public class LeagueTable {  
    private String ranking;
    private String team;
    private int played;
    private int won;
    private int drawn;
    private int lost;
    private int goal_for;
    private int goal_against;
    private String goal_difference;
    private int points;

    public LeagueTable() {
    }

    public LeagueTable(String ranking, String team, int played, int won, int drawn, int lost, int goal_for, int goal_against, String goal_difference, int points) {
        this.ranking = ranking;
        this.team = team;
        this.played = played;
        this.won = won;
        this.drawn = drawn;
        this.lost = lost;
        this.goal_for = goal_for;
        this.goal_against = goal_against;
        this.goal_difference = goal_difference;
        this.points = points;
    }

    @Override
    public String toString() {
        return "LeagueTable{" + "ranking=" + ranking + ", team=" + team + ", played=" + played + ", won=" + won + ", drawn=" + drawn + ", lost=" + lost + ", goal_for=" + goal_for + ", goal_against=" + goal_against + ", goal_difference=" + goal_difference + ", points=" + points + '}';
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public int getPlayed() {
        return played;
    }

    public void setPlayed(int played) {
        this.played = played;
    }

    public int getWon() {
        return won;
    }

    public void setWon(int won) {
        this.won = won;
    }

    public int getDrawn() {
        return drawn;
    }

    public void setDrawn(int drawn) {
        this.drawn = drawn;
    }

    public int getLost() {
        return lost;
    }

    public void setLost(int lost) {
        this.lost = lost;
    }

    public int getGoal_for() {
        return goal_for;
    }

    public void setGoal_for(int goal_for) {
        this.goal_for = goal_for;
    }

    public int getGoal_against() {
        return goal_against;
    }

    public void setGoal_against(int goal_against) {
        this.goal_against = goal_against;
    }

    public String getGoal_difference() {
        return goal_difference;
    }

    public void setGoal_difference(String goal_difference) {
        this.goal_difference = goal_difference;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
    
    public String getLeagueTable(String index, String typeTable) {
        String values = null;
        try {
            HttpResponse<String> response = Unirest.post("http://localhost:9200/"+index+"/_search")
                    .header("Content-Type", "application/json")
                    .header("cache-control", "no-cache")
                    .body("{\"query\": {\"bool\": {\"must\": {\"match_phrase\": {\"type_table\": \""+typeTable+"\"}}}}}")
                    .asString();
            values = response.getBody();
        } catch (UnirestException ex) {
            Logger.getLogger(LeagueTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return values.toString();
    }
    public List<LeagueTable> listLeagueTable(String leagueTableValue) {
        List<LeagueTable> list = new ArrayList<>();
        JSONObject objLeagueTableValue = new JSONObject(leagueTableValue);
        JSONObject objHits = objLeagueTableValue.getJSONObject("hits");
        JSONArray arrHits = objHits.getJSONArray("hits");
        for (int i = 0; i < arrHits.length(); i++) {
            JSONObject obj_source = arrHits.getJSONObject(i).getJSONObject("_source");
            String valueRanking = obj_source.getString("ranking");
            String valueTeam = obj_source.getString("team");
            int valuePlayed = obj_source.getInt("played");
            int valueWon = obj_source.getInt("won");
            int valueDrawn = obj_source.getInt("drawn");
            int valueLost = obj_source.getInt("lost");
            int valueGoalFor = obj_source.getInt("goal_for");
            int valueGoalAgainst = obj_source.getInt("goal_against");
            String valueGoalDifference = obj_source.getString("goal_difference");
            int valuePoints = obj_source.getInt("points");
            LeagueTable l = new LeagueTable(valueRanking, valueTeam, valuePlayed, valueWon, valueDrawn, valueLost, valueGoalFor, valueGoalAgainst, valueGoalDifference, valuePoints);
            list.add(l);
        }
        System.out.println(list);
        return list;
    } 
    
    public static void main (String[] args){
        LeagueTable l = new LeagueTable();
        String str = l.getLeagueTable("league_table_thaipremierleague", "all_table");
        l.listLeagueTable(str);
        
    }
}
