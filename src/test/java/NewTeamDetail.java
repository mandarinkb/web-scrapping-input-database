
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;


public class NewTeamDetail {
    private String squadNember;
    private String imgPlayer;
    private String playerName;
    private String position;

    public NewTeamDetail() {
    }

    public NewTeamDetail(String squadNember, String imgPlayer, String playerName, String position) {
        this.squadNember = squadNember;
        this.imgPlayer = imgPlayer;
        this.playerName = playerName;
        this.position = position;
    }

    @Override
    public String toString() {
        return "NewTeamDetail{" + "squadNember=" + squadNember + ", imgPlayer=" + imgPlayer + ", playerName=" + playerName + ", position=" + position + '}';
    }

    public String getSquadNember() {
        return squadNember;
    }

    public void setSquadNember(String squadNember) {
        this.squadNember = squadNember;
    }

    public String getImgPlayer() {
        return imgPlayer;
    }

    public void setImgPlayer(String imgPlayer) {
        this.imgPlayer = imgPlayer;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
    public List<NewTeamDetail> listFixtures(String fixturesValue) {
        List<NewTeamDetail> list = new ArrayList<>();
        JSONObject objResultsValue = new JSONObject(fixturesValue);
        JSONObject objHits = objResultsValue.getJSONObject("hits");
        JSONArray arrHits = objHits.getJSONArray("hits");
        
        for (int i = 0; i < arrHits.length(); i++) {
            JSONObject obj_source = arrHits.getJSONObject(i).getJSONObject("_source");
            JSONArray arrPlayers = obj_source.getJSONArray("players");
            for (int j = 0; j < arrPlayers.length(); j++) {
               String squad_nember = arrPlayers.getJSONObject(j).getString("squad_nember"); 
               String img_player = arrPlayers.getJSONObject(j).getString("img_player"); 
               String player_name = arrPlayers.getJSONObject(j).getString("player_name"); 
               String local_position = arrPlayers.getJSONObject(j).getString("position"); 
               NewTeamDetail t = new NewTeamDetail(squad_nember, img_player, player_name, local_position);
               list.add(t);
            }
        }
        System.out.println(list);
        return list;
    }      
    
    public String searchAllPlayersOfTeam(String index, String team) {
        String str = null;
        try {
            HttpResponse<String> response = Unirest.post("http://localhost:9200/"+index+"/_search")
                    .header("Content-Type", "application/json")
                    .header("cache-control", "no-cache")
                    .body("{\"size\": 100,\"query\": {\"bool\": {\"must\": {\"match_phrase\": {\"team_id\": \""+team+"\"}}}}}")
                    .asString();
            str = response.getBody();
        } catch (UnirestException ex) {
            Logger.getLogger(NewTeamDetail.class.getName()).log(Level.SEVERE, null, ex);
        }
        return str;
    }
    public static void main(String[] args){
      NewTeamDetail n = new NewTeamDetail();
      
      String str = n.searchAllPlayersOfTeam("present_teams_detail_thaipremierleague","685b49b426a7f9e46650ebe875f84095");
      n.listFixtures(str);
    }
    
}
