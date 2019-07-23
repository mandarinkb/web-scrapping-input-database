
package app.function;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Elasticsearch {
    @Value("${elasticsearch_ip}")
    private String elasticsearch_ip;  
    
    public void inputElasticsearch(String body,String index) {
        try {
            HttpResponse<String> response = Unirest.post("http://"+elasticsearch_ip+":9200/"+index+"/text")
                    .header("Content-Type", "application/json")
                    .header("Cache-Control", "no-cache")
                    .body(body)
                    .asString();
        } catch (UnirestException ex) {
            Logger.getLogger(Elasticsearch.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteIndex(String index) {
        try {
            HttpResponse<String> response = Unirest.delete("http://"+elasticsearch_ip+":9200/"+index)
                    .header("Accept", "*/*")
                    .header("cache-control", "no-cache")
                    .asString();
        } catch (UnirestException ex) {
            Logger.getLogger(Elasticsearch.class.getName()).log(Level.SEVERE, null, ex);
        }
    }  
}
