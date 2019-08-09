
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;


public class TeamApi {
    private String team;
    private String teamImg;

    public TeamApi() {
    }

    public TeamApi(String team, String teamImg) {
        this.team = team;
        this.teamImg = teamImg;
    }

    @Override
    public String toString() {
        return "TeamApi{" + "team=" + team + ", teamImg=" + teamImg + '}';
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getTeamImg() {
        return teamImg;
    }

    public void setTeamImg(String teamImg) {
        this.teamImg = teamImg;
    }
    
    public List<TeamApi> listFixtures(String fixturesValue) {
        List<TeamApi> list = new ArrayList<>();
        JSONObject objResultsValue = new JSONObject(fixturesValue);
        JSONObject objHits = objResultsValue.getJSONObject("hits");
        JSONArray arrHits = objHits.getJSONArray("hits");
        for (int i = 0; i < arrHits.length(); i++) {
            JSONObject obj_source = arrHits.getJSONObject(i).getJSONObject("_source");
            String team = obj_source.getString("team");
            String teamImg = obj_source.getString("logo_team");
            TeamApi t = new TeamApi(team, teamImg);
            list.add(t);
        }
        System.out.println(list);
        return list;
    }   
    
    public String searchAllTeam(String index) {
        String str = null;
        try {
            HttpResponse<String> response = Unirest.post("http://localhost:9200/" + index + "/_search")
                    .header("Content-Type", "application/json")
                    .header("cache-control", "no-cache")
                    .body("{\"size\": 100,\"query\": {\"match_all\": {}}}")
                    .asString();
            str = response.getBody();
        } catch (UnirestException ex) {
            Logger.getLogger(TeamApi.class.getName()).log(Level.SEVERE, null, ex);
        }
        return str;
    }

    public static void main(String[] args) {
    TeamApi t = new TeamApi();
    String str = t.searchAllTeam("present_teams_thaipremierleague");
    t.listFixtures(str);
    }
}
