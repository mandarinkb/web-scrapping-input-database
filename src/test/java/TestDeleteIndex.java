
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TestDeleteIndex {

    public void deleteIndex(String index) {
        try {
            HttpResponse<String> response = Unirest.delete("http://localhost:9200/" + index)
                    .header("Accept", "*/*")
                    .header("cache-control", "no-cache")
                    .asString();
        } catch (UnirestException ex) {
            Logger.getLogger(TestDeleteIndex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }  
    public static void main(String[] args){
        TestDeleteIndex t = new TestDeleteIndex();
        t.deleteIndex("results_premierleague");
    }
    
}
