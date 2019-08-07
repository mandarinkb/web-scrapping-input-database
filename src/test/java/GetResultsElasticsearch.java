
import com.fasterxml.jackson.databind.ObjectMapper;
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


public class GetResultsElasticsearch {
    private String homeTeam;
    private String homeImg;
    private int homeScore;
    private String awayTeam;
    private String awayImg;
    private int awayScore;

    public GetResultsElasticsearch() {
    }

    public GetResultsElasticsearch(String homeTeam, String homeImg, int homeScore, String awayTeam, String awayImg, int awayScore) {
        this.homeTeam = homeTeam;
        this.homeImg = homeImg;
        this.homeScore = homeScore;
        this.awayTeam = awayTeam;
        this.awayImg = awayImg;
        this.awayScore = awayScore;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getHomeImg() {
        return homeImg;
    }

    public void setHomeImg(String homeImg) {
        this.homeImg = homeImg;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public String getAwayImg() {
        return awayImg;
    }

    public void setAwayImg(String awayImg) {
        this.awayImg = awayImg;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    //จะทำเป็น Json ต้องมี ฟังก์ชันนี้ด้วย 
    @Override
    public String toString() {
        return "GetResultsElasticsearch{" + "homeTeam=" + homeTeam + ", homeImg=" + homeImg + ", homeScore=" + homeScore + ", awayTeam=" + awayTeam + ", awayImg=" + awayImg + ", awayScore=" + awayScore + '}';
    }

    public String getResules(String index, String date) {
        String values = null;
        try {
            HttpResponse<String> response = Unirest.post("http://localhost:9200/" + index + "/_search")
                    .header("Content-Type", "application/json")
                    .header("cache-control", "no-cache")
                    .body("{\"query\": {\"match_phrase\": {\"date\": \"" + date + "\"}}}")
                    .asString();
            values = response.getBody();
        } catch (UnirestException ex) {
            Logger.getLogger(GetResultsElasticsearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return values.toString();
    }
    
    public List<String> listResults(String resultsValue) {
        List<String> list = new ArrayList<>();
        JSONObject objResultsValue = new JSONObject(resultsValue);
        JSONObject objHits = objResultsValue.getJSONObject("hits");
        JSONArray arrHits = objHits.getJSONArray("hits");
        for (int i = 0; i < arrHits.length(); i++) {
            try {
                JSONObject obj_source = arrHits.getJSONObject(i).getJSONObject("_source");
                String home = obj_source.getString("home");
                String home_img = obj_source.getString("home_img");
                int score_home = Integer.parseInt(obj_source.getString("score_home"));

                String away = obj_source.getString("away");
                String away_img = obj_source.getString("away_img");
                int score_away = Integer.parseInt(obj_source.getString("score_away"));
                
                GetResultsElasticsearch g = new GetResultsElasticsearch(home, home_img, score_home, away, away_img, score_away);

                // Creating Object of ObjectMapper define in Jakson Api 
                ObjectMapper jacksonObj = new ObjectMapper();
                String jsonStr = jacksonObj.writeValueAsString(g);
                list.add(jsonStr);

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println(list);
        return list;
    }

    public static void main(String[] args){
        GetResultsElasticsearch t = new GetResultsElasticsearch();
        String str =  t.getResules("results_thaipremierleague", "2019-02-23");
        t.listResults(str);
        
    }

}
