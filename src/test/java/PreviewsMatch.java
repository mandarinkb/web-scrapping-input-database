
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PreviewsMatch {
    //public String firstUrl = "http://www.livesoccer888.com/thaipremierleague/previews/index.php";
    public String firstUrl = "http://www.livesoccer888.com/premierleague/previews/index.php";
    public String nextpage;
    //public String baseLink = "http://www.livesoccer888.com/thaipremierleague/previews/";
    public String baseLink = "http://www.livesoccer888.com/premierleague/previews/";
    public boolean check = true;
    public List<String> listPage = new ArrayList<>();
    public void getLinkPage(String url) {
        //String link = url.replace("index.php", "");
        
        try {
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Elements elements = doc.select(".pre-table._border");
            Elements elesTr = elements.select("tr");
            for (Element ele : elesTr) {
                Elements elesPreviews = ele.select(".pre-views");
                Elements a = elesPreviews.select("a");
                String strUrl = a.attr("href");
                if (!strUrl.isEmpty()) {
                    String newLink = baseLink + strUrl;
                    //System.out.println(newLink);
                    listPage.add(newLink);
                }
            }
            Elements elesNextPage = doc.select("#pager_links");
            Elements elesNext = elesNextPage.select(".next_page");
            if (elesNext.isEmpty()) {
                check = false;
            }
            Elements a = elesNext.select("a");
            String pathNextPage = a.attr("href");

            nextpage = firstUrl +  pathNextPage;
            //System.out.println(pathNextPage);
        } catch (IOException | JSONException e) {
            System.out.print(e.getMessage());
        }
    }
    
    public void getContent(String url) {
        try {
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Elements elements = doc.select(".col-1");
            String team = elements.select("h1").text();
            String thDate = elements.select(".datecontents").text();
            team = team.replace("วิเคราะห์บอล ไทยลีก คู่ ", "");  // กรณีไทยลีก
            team = team.replace("วิเคราะห์บอล พรีเมียร์ลีก อังกฤษ คู่ ", "");  // กรณีพรีเมียร์ลีก
            team = team.replace("VS", "-");
            thDate = thDate.replace("โพสต์เมื่อ : ", "");
            String date = getInterDate(thDate);
            
            System.out.println(team);
            System.out.println(thDate);
            System.out.println(date);

            // content
            Elements elesContent = doc.select(".col-1._margt");
            String content = elesContent.select(".txt_preview").text();
            String scoreAnalyze = elesContent.select(".ScoreAnalyzeList").text();
            System.out.println(content);
            System.out.println(scoreAnalyze);
        } catch (IOException | JSONException e) {
            System.out.print(e.getMessage());
        }
    } 
     public String getInterDate(String inputDate) {
        String[] splitStr = inputDate.split("\\s+");
        //วันที่ 
        String day = splitStr[1];
        if ("1".equals(day)) {
            day = "01";
        } else if ("2".equals(day)) {
            day = "02";
        } else if ("3".equals(day)) {
            day = "03";
        } else if ("4".equals(day)) {
            day = "04";
        } else if ("5".equals(day)) {
            day = "05";
        } else if ("6".equals(day)) {
            day = "06";
        } else if ("7".equals(day)) {
            day = "07";
        } else if ("8".equals(day)) {
            day = "08";
        } else if ("9".equals(day)) {
            day = "09";
        }
        //เดือน
        String month = splitStr[2];
        if ("มกราคม".equals(month)) {
            month = "01";
        } else if ("กุมภาพันธ์".equals(month)) {
            month = "02";
        } else if ("มีนาคม".equals(month)) {
            month = "03";
        } else if ("เมษายน".equals(month)) {
            month = "04";
        } else if ("พฤษภาคม".equals(month)) {
            month = "05";
        } else if ("มิถุนายน".equals(month)) {
            month = "06";
        } else if ("กรกฎาคม".equals(month)) {
            month = "07";
        } else if ("สิงหาคม".equals(month)) {
            month = "08";
        } else if ("กันยายน".equals(month)) {
            month = "09";
        } else if ("ตุลาคม".equals(month)) {
            month = "10";
        } else if ("พฤศจิกายน".equals(month)) {
            month = "11";
        } else if ("ธันวาคม".equals(month)) {
            month = "12";
        }
        //ปี
        int y = Integer.parseInt(splitStr[3]) - 543;//แปลงเป็น คศ
        String year = Integer.toString(y);
        return year + "-" + month + "-" + day;
    }  

    public static void main(String[] args) {
        //String str = "http://www.livesoccer888.com/thaipremierleague/previews/read.php?ID=10349"; //http://www.livesoccer888.com/thaipremierleague/previews/read.php?ID=9377
        PreviewsMatch p = new PreviewsMatch();
        p.getLinkPage(p.firstUrl);
        while(p.check){
            p.getLinkPage(p.nextpage); 
        }

        for(String str : p.listPage){
            p.getContent(str);
            System.out.println("");
        }
    }
}
