
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


public class GetFixturesElasticsearch {
    private String homeTeam;
    private String homeImg;
    private String awayTeam;
    private String awayImg;
    private String time;

    public GetFixturesElasticsearch() {
    }

    public GetFixturesElasticsearch(String homeTeam, String homeImg, String awayTeam, String awayImg, String time) {
        this.homeTeam = homeTeam;
        this.homeImg = homeImg;
        this.awayTeam = awayTeam;
        this.awayImg = awayImg;
        this.time = time;
    }

    @Override
    public String toString() {
        return "GetFixturesElasticsearch{" + "homeTeam=" + homeTeam + ", homeImg=" + homeImg + ", awayTeam=" + awayTeam + ", awayImg=" + awayImg + ", time=" + time + '}';
    }

    public String getHomeTeam() {
        return homeTeam;
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

    public String getAwayImg() {
        return awayImg;
    }

    public void setAwayImg(String awayImg) {
        this.awayImg = awayImg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
      
    public String getFixtures(String index, String date) {
        String values = null;
        try {
            HttpResponse<String> response = Unirest.post("http://localhost:9200/" + index + "/_search")
                    .header("Content-Type", "application/json")
                    .header("cache-control", "no-cache")
                    .body("{\"query\": {\"match_phrase\": {\"date\": \"" + date + "\"}}}")
                    .asString();
            values = response.getBody();
        } catch (UnirestException ex) {
            Logger.getLogger(GetFixturesElasticsearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return values.toString();
    }
 
    public List<GetFixturesElasticsearch> listFixtures(String fixturesValue) {
        List<GetFixturesElasticsearch> list = new ArrayList<>();
        JSONObject objResultsValue = new JSONObject(fixturesValue);
        JSONObject objHits = objResultsValue.getJSONObject("hits");
        JSONArray arrHits = objHits.getJSONArray("hits");
        for (int i = 0; i < arrHits.length(); i++) {
            JSONObject obj_source = arrHits.getJSONObject(i).getJSONObject("_source");
            String home = obj_source.getString("home");
            String home_img = obj_source.getString("home_img");
            String away = obj_source.getString("away");
            String away_img = obj_source.getString("away_img");
            String time = obj_source.getString("time");
            GetFixturesElasticsearch g = new GetFixturesElasticsearch(home, home_img, away, away_img, time);
            list.add(g);
        }
        System.out.println(list);
        return list;
    }
    
    
    public static void main(String[] args){
        GetFixturesElasticsearch g = new GetFixturesElasticsearch();
        String str =  g.getFixtures("fixtures_thaipremierleague", "2019-02-24");
        g.listFixtures(str);
    }
}
