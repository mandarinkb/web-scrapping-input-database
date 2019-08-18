
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PreviewsMatch {

    public void getContentScoreAnalyzePage(String url) {
        String baseLink = url.replace("index.php", "");
        String nextpage;
        boolean check = true;
        List<String> listPage = new ArrayList<>();
        try {
            // first page get link content
            Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
            Elements elements = doc.select(".pre-table._border");
            Elements elesTr = elements.select("tr");
            for (Element ele : elesTr) {
                Elements elesPreviews = ele.select(".pre-views");
                Elements a = elesPreviews.select("a");
                String strUrl = a.attr("href");
                if (!strUrl.isEmpty()) {
                    String linkContent = baseLink + strUrl;
                    listPage.add(linkContent);
                }
            }
            Elements elesNextPage = doc.select("#pager_links");
            Elements elesNext = elesNextPage.select(".next_page");
            if (elesNext.isEmpty()) {
                check = false;
            }
            Elements a = elesNext.select("a");
            String pathNextPage = a.attr("href");
            nextpage = url + pathNextPage;

            // next page get link content
            while (check) {
                Document docNextPage = Jsoup.connect(nextpage).timeout(60 * 1000).get();
                Elements elementsNextPage = docNextPage.select(".pre-table._border");
                Elements elesTrNextPage = elementsNextPage.select("tr");
                for (Element ele : elesTrNextPage) {
                    Elements elesPreviewsNextPage = ele.select(".pre-views");
                    Elements aNextPage = elesPreviewsNextPage.select("a");
                    String strUrl = aNextPage.attr("href");
                    if (!strUrl.isEmpty()) {
                        String linkContent = baseLink + strUrl;
                        listPage.add(linkContent);
                    }
                }
                Elements elesNextPage2 = docNextPage.select("#pager_links");
                Elements elesNext2 = elesNextPage2.select(".next_page");
                if (elesNext2.isEmpty()) {
                    check = false;
                }
                Elements a2 = elesNext2.select("a");
                String pathNextPage2 = a2.attr("href");
                nextpage = url + pathNextPage2;
            }
            // get content from list
            for (String strlink : listPage) {               
                Document docContent = Jsoup.connect(strlink).timeout(60 * 1000).get();
                Elements elementsContent = docContent.select(".col-1");
                String homeAway = elementsContent.select("h1").text();
                String dateThai = elementsContent.select(".datecontents").text();
                homeAway = homeAway.replace("วิเคราะห์บอล ไทยลีก คู่ ", "");  // กรณีไทยลีก
                homeAway = homeAway.replace("วิเคราะห์บอล พรีเมียร์ลีก อังกฤษ คู่ ", "");  // กรณีพรีเมียร์ลีก
                homeAway = homeAway.replace("VS", "-");
                homeAway = changeMultiNameScoreAnalyze(homeAway);  // เปลี่ยนชื่อให้ตรงกับชื่อทีมที่จัดเก็บ
                
                dateThai = dateThai.replace("โพสต์เมื่อ : ", "");
                String date = getInterDate(dateThai);
                // content
                Elements elesContent = docContent.select(".col-1._margt");
                String content = elesContent.select(".txt_preview").text();
                String scoreAnalyze = elesContent.select(".ScoreAnalyzeList").text();

                System.out.println(homeAway);
                System.out.println(dateThai);
                System.out.println(date);
                System.out.println(content);
                System.out.println(scoreAnalyze);
                System.out.println("");
            }

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

    public String changeNameScoreAnalyze(String team) {
        if ("ราชบุรี มิตรผล".equals(team)) {
            team = "ราชบุรี มิตรผล เอฟซี";
        } else if ("เชียงราย ยูไนเต็ด".equals(team)) {
            team = "สิงห์ เชียงราย ยูไนเต็ด";
        } else if ("เอสซีจี เมืองทอง".equals(team)) {
            team = "เอสซีจี เมืองทอง ยูไนเต็ด";
        } else if ("แบงค็อก ยูไนเต็ด".equals(team)) {
            team = "ทรู แบงค็อก ยูไนเต็ด";
        } else if ("นครราชสีมา เอฟซี".equals(team)) {
            team = "นครราชสีมา มาสด้า เอฟซี";
        } else if ("สมุทรปราการ ซิตี้".equals(team)) {
            team = "สมุทรปราการ ซิตี้ เอฟซี";
        } else if ("พีที ประจวบ".equals(team)) {
            team = "พีที ประจวบ เอฟซี";
        } else if ("แมนฯ ซิตี้".equals(team)) {
            team = "แมนเชสเตอร์ ซิตี้";
        } else if ("แมนฯ ยูไนเต็ด".equals(team)) {
            team = "แมนเชสเตอร์ ยูไนเต็ด";
        } else if ("นิวคาสเซิล".equals(team)) {
            team = "นิวคาสเซิล ยูไนเต็ด";
        } else if ("สเปอร์ส".equals(team)) {
            team = "ท็อตแน่ม ฮ็อทสเปอร์";
        } else if ("เวสต์แฮม".equals(team)) {
            team = "เวสต์แฮม ยูไนเต็ด";
        } else if ("บอร์นมัธ".equals(team)) {
            team = "เอเอฟซี บอร์นมัธ";
        } else if ("ไบรท์ตัน".equals(team)) {
            team = "ไบรท์ตัน แอนด์ โฮฟ อัลเบียน";
        } else if ("วูล์ฟแฮมป์ตัน".equals(team)) {
            team = "วูล์ฟแฮมป์ตัน วันเดอร์เรอร์ส";
        } else if ("เชฟฯ ยูไนเต็ด".equals(team)) {
            team = "เชฟฟิลด์ ยูไนเต็ด";
        }
        return team;
    }

    public String changeMultiNameScoreAnalyze(String homeAway) {
        String[] splitStr = homeAway.split(" - ");
        String home = splitStr[0];
        String away = splitStr[1];
        home = changeNameScoreAnalyze(home);
        away = changeNameScoreAnalyze(away);
        return home + " - " + away;
    }

    public static void main(String[] args) {
        PreviewsMatch p = new PreviewsMatch();
        String url = "http://www.livesoccer888.com/thaipremierleague/previews/index.php";
        p.getContentScoreAnalyzePage(url);
    }
}
