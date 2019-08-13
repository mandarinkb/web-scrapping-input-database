
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class PlayerDetail {

    private String img_player;
    private String player_name;
    private String birthday;
    private String nationality;
    private String height;
    private String position;
    private String link_performance_detail;

    public PlayerDetail() {
    }

    @Override
    public String toString() {
        return "PlayerDetail{" + "img_player=" + img_player + ", player_name=" + player_name + ", birthday=" + birthday + ", nationality=" + nationality + ", height=" + height + ", position=" + position + ", link_performance_detail=" + link_performance_detail + '}';
    }

    public PlayerDetail(String img_player, String player_name, String birthday, String nationality, String height, String position, String link_performance_detail) {
        this.img_player = img_player;
        this.player_name = player_name;
        this.birthday = birthday;
        this.nationality = nationality;
        this.height = height;
        this.position = position;
        this.link_performance_detail = link_performance_detail;
    }

    public String getImg_player() {
        return img_player;
    }

    public void setImg_player(String img_player) {
        this.img_player = img_player;
    }

    public String getPlayer_name() {
        return player_name;
    }

    public void setPlayer_name(String player_name) {
        this.player_name = player_name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getLink_performance_detail() {
        return link_performance_detail;
    }

    public void setLink_performance_detail(String link_performance_detail) {
        this.link_performance_detail = link_performance_detail;
    }


    public String getPlayerDetail(String index, String nameId) {
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

    public List<PlayerDetail> listPlayerDetail(String playerDetailValue) {
        List<PlayerDetail> list = new ArrayList<>();
        JSONObject objResultsValue = new JSONObject(playerDetailValue);
        JSONObject objHits = objResultsValue.getJSONObject("hits");
        JSONArray arrHits = objHits.getJSONArray("hits");
        for (int i = 0; i < arrHits.length(); i++) {
            JSONObject obj_source = arrHits.getJSONObject(i).getJSONObject("_source");
            String l_img_player = obj_source.getString("img_player");
            String l_player_name = obj_source.getString("player_name");
            String l_birthday = obj_source.getString("birthday");
            String l_nationality = obj_source.getString("nationality");
            String l_height = obj_source.getString("height");
            String l_position = obj_source.getString("position");
            String l_link_performance_detail = obj_source.getString("link_performance_detail");
            PlayerDetail p = new PlayerDetail(l_img_player, l_player_name, l_birthday, l_nationality,
                                              l_height, l_position, l_link_performance_detail);
            list.add(p);
        }
        System.out.println(list);
        return list;
    }

    public static void main(String [] args) {
        PlayerDetail p = new PlayerDetail();
        String str = p.getPlayerDetail("present_players_detail_thaipremierleague", "7006187e2834bd3d9966fbf976341933");
        p.listPlayerDetail(str);
    }
}
