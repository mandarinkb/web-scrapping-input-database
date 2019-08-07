
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;


public class ResulesHomeAway {
    private String dateThai;
    private String homeImg;
    private String homeScore;
    private String awayImg;
    private String awayScore;

    public ResulesHomeAway() {
    }

    public ResulesHomeAway(String dateThai, String homeImg, String homeScore, String awayImg, String awayScore) {
        this.dateThai = dateThai;
        this.homeImg = homeImg;
        this.homeScore = homeScore;
        this.awayImg = awayImg;
        this.awayScore = awayScore;
    }

    @Override
    public String toString() {
        return "ResulesHomeAway{" + "dateThai=" + dateThai + ", homeImg=" + homeImg + ", homeScore=" + homeScore + ", awayImg=" + awayImg + ", awayScore=" + awayScore + '}';
    }

    public String getDateThai() {
        return dateThai;
    }

    public void setDateThai(String dateThai) {
        this.dateThai = dateThai;
    }

    public String getHomeImg() {
        return homeImg;
    }

    public void setHomeImg(String homeImg) {
        this.homeImg = homeImg;
    }

    public String getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(String homeScore) {
        this.homeScore = homeScore;
    }

    public String getAwayImg() {
        return awayImg;
    }

    public void setAwayImg(String awayImg) {
        this.awayImg = awayImg;
    }

    public String getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(String awayScore) {
        this.awayScore = awayScore;
    }
    
    public String getResulesHomeAway(String index , String homeAway, String AwayHome) {
        String values = null;
        try {
            HttpResponse<String> response = Unirest.post("http://localhost:9200/results_premierleague/_search")
                    .header("Content-Type", "application/json")
                    .header("cache-control", "no-cache")
                    .body("{\r\n  \"query\": {\r\n    \"bool\": {\r\n      \"should\": [\r\n        { \"match_phrase\": {\"home_away\": \""+homeAway+"\"}},\r\n        { \"match_phrase\": {\"home_away\": \""+AwayHome+"\"}}\r\n      ]\r\n    }\r\n  }\r\n}\r\n")
                    .asString();
            values = response.getBody();
        } catch (UnirestException ex) {
            Logger.getLogger(ResulesHomeAway.class.getName()).log(Level.SEVERE, null, ex);
        }
      return values.toString();
    }  
    public List<ResulesHomeAway> listResultsHomeAway(String resultsValue) {
        List<ResulesHomeAway> list = new ArrayList<>();
        JSONObject objResultsValue = new JSONObject(resultsValue);
        JSONObject objHits = objResultsValue.getJSONObject("hits");
        JSONArray arrHits = objHits.getJSONArray("hits");
        for (int i = 0; i < arrHits.length(); i++) {
            JSONObject obj_source = arrHits.getJSONObject(i).getJSONObject("_source");
            String date_thai = obj_source.getString("date_thai");
            String home_img = obj_source.getString("home_img");
            String score_home = obj_source.getString("score_home");
            String away_img = obj_source.getString("away_img");
            String score_away = obj_source.getString("score_away");
            ResulesHomeAway r = new ResulesHomeAway(date_thai, home_img, score_home, away_img, score_away);
            list.add(r);
        }
        System.out.println(list);
        return list;
    } 
    public String homeAwayToAwayHome(String homeAway){
        String[] partsTeam = homeAway.split(" - ");
        String home = partsTeam[0];
        String away = partsTeam[1];
        return away +" - "+ home;
    }
    public String md5(String str) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashInBytes = md.digest(str.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        System.out.println(sb.toString());
        return sb.toString();
    }
    public static void main(String[] args) throws NoSuchAlgorithmException{
        ResulesHomeAway r = new ResulesHomeAway();
        //String str = r.getResulesHomeAway("results_premierleague", "การท่าเรือ เอฟซี - เอสซีจี เมืองทอง ยูไนเต็ด", "เอสซีจี เมืองทอง ยูไนเต็ด - การท่าเรือ เอฟซี");
        //r.listResultsHomeAway(str);
        //String s = r.homeAwayToAwayHome("การท่าเรือ เอฟซี - เอสซีจี เมืองทอง ยูไนเต็ด");
        //System.out.println(s);
        String str = "บุรีรัมย์ ยูไนเต็ดการท่าเรือ เอฟซี";
        r.md5(str);
    }
}
