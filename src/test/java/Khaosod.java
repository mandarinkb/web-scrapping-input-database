import java.util.regex.*;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Khaosod {
   public static int countPage = 1;
    //public static String nextUrl = "";
    public static String nextUrl = "";

    public int countMatchSrring(String inputString, String regex) {
        Pattern pattern = Pattern.compile(regex);  //"(.*)" + regex + "(.*)"
        Matcher matcher = pattern.matcher(inputString);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
    public static String content(String url) throws IOException {
        Document docContent = Jsoup.connect(url).timeout(60 * 1000).get();/////////////////////////
        String str = docContent.getElementsByClass("td-post-content").outerHtml();

        Document docNew = Jsoup.parse(str);                                            //เชื่อมต่อโดย string   

           // docNew.select("div.td_block_template_1").first().remove();                                // ลบเเท็ก <div class="td-a-rec> ออก

        String content = docNew.getElementsByClass("td-post-content").outerHtml();     //ค้นหาข้อมูลในหน้า html โดยการอ้างจากชื่อ class
        content = Jsoup.parse(content).text();

        return content;
    }

    public void firstPage(String url) throws IOException, InterruptedException {
        Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
        //เก็บค่า next page
        Elements keep = new Elements();     //สร้างมาเพื่อเก็บค่า
        Elements elesNextPage = doc.getElementsByTag("link");
        for (Element ele : elesNextPage) {
            Element img = ele.select("link").first();
            for (Attribute attribute : ele.attributes()) {
                if (attribute.getValue().equalsIgnoreCase("next")) {    //ตรวจสอบค่า ถ้ามีค่า valut = next
                    keep.add(ele);                                      //ให้เก็บค่านั้น
                }
            }
        }
        for (Element ele : keep) {                           //ดึงค่าที่เก็บไว้ออกมา
            Element img = ele.select("link").first();
            String strUrl = img.attr("href");                // ดึงurl ออกมา
            nextUrl = strUrl;
        }
        //System.out.println("++++++++++++++++++++++++++++++++++");
        //System.out.println("++++++++++++++++++++++++++++++++++");
        
        // slide
        String slide = doc.getElementsByClass("td-slider").outerHtml();
        Document docSlide = Jsoup.parse(slide);  //เชื่อมต่อโดย string
        Elements elesSlide = docSlide.getElementsByClass("td_module_slide");

        for (Element ele : elesSlide) {

            Element img = ele.select("img").first();
            String imgUrl = img.attr("src");
            String titleUrl = img.attr("title");  //หุ้นไทย
            
           
            Element date = ele.select("time").first();
            String dateUrl = date.attr("datetime");

            Elements eleUrls = ele.getElementsByClass("td-module-thumb");
            Element eleUrl = eleUrls.select("a").first();
            String strUrl = eleUrl.attr("href");

            String con =Khaosod.content(strUrl);
            System.out.println(con.matches("(.*)ความเคลื่อนไหว(.*)"));
            if(con.matches("(.*)ความเคลื่อนไหว(.*)")){
                int count = countMatchSrring(con, "ความเคลื่อนไหว");
                System.out.println("match string : " + count);
            }
            System.out.println("title : " + titleUrl);
            System.out.println("link : " + strUrl);
            System.out.println("img_link :" + imgUrl);
            System.out.println("date : " + dateUrl);
            System.out.println("content : " + con);

            System.out.println("");
        }

        System.out.println("++++++++++++++end slide page++++++++++++++++++++");

        //ข้าง slide
        String block_inner = doc.getElementsByClass("td_block_inner").outerHtml();//

        Document docBlock_inner = Jsoup.parse(block_inner);  //เชื่อมต่อโดย string
        Elements elesNearSlide = docBlock_inner.getElementsByClass("td_module_6");
        for (Element ele : elesNearSlide) {

            Element title = ele.select("a").first();
            String strUrl = title.attr("href");
            String titleUrl = title.attr("title");
            
            Element img = ele.select("img").first();
            String strImg = img.attr("src");

            Element date = ele.select("time").first();
            String strDate = date.attr("datetime");

            String con = Khaosod.content(strUrl);
            System.out.println(con.matches("(.*)ความเคลื่อนไหว(.*)"));
            if(con.matches("(.*)ความเคลื่อนไหว(.*)")){
                int count = countMatchSrring(con, "ความเคลื่อนไหว");
                System.out.println("match string : " + count);
            }

            System.out.println("title : " + titleUrl);
            System.out.println("link : " + strUrl);
            System.out.println("img_link :" + strImg);
            System.out.println("date : " + strDate);
            System.out.println("content : " + con);

            System.out.println(" ");
            //TimeUnit.SECONDS.sleep(5);                // หน่วงเวลา 5 วินาที  ไม่งั้นอ่านหน้าถัดไปไม่ทัน
        }
        System.out.println("+++++++++++++end right page+++++++++++++++++++++");

        //ตรงกลาง
        String ud_loop_inner = doc.getElementsByClass("ud_loop_inner").outerHtml();
        Document docUd_loop_inner = Jsoup.parse(ud_loop_inner);  //เชื่อมต่อโดย string
        Elements elements = docUd_loop_inner.getElementsByClass("td-block-span4");
        for (Element ele : elements) {

            Element title = ele.select("a").first();
            String strUrl = title.attr("href");
            String titleUrl = title.attr("title");

            
            
            Element img = ele.select("img").first();
            String strImg = img.attr("src");

            Element date = ele.select("time").first();
            String strDate = date.attr("datetime");

            String con = Khaosod.content(strUrl);
            System.out.println(con.matches("(.*)ความเคลื่อนไหว(.*)"));            
            if(con.matches("(.*)ความเคลื่อนไหว(.*)")){
                int count = countMatchSrring(con, "ความเคลื่อนไหว");
                System.out.println("match string : " + count);
            }            

            System.out.println("title : " + titleUrl);
            System.out.println("link : " + strUrl);
            System.out.println("img_link :" + strImg);
            System.out.println("date : " + strDate);
            System.out.println("content : " + con);

            System.out.println(" ");
        }
        System.out.println("++++++++++++++end center page++++++++++++++++++++");
        System.out.println("+++++++++++++++ "+ countPage +" +++++++++++++++++++");
    }

    
    
    
    public void nextPage() throws IOException, InterruptedException {
        String nextPage = nextUrl;
        boolean check = true;

        while (check) {

            System.out.println(nextPage);
            //TimeUnit.SECONDS.sleep(5);                // หน่วงเวลา 5 วินาที  ไม่งั้นอ่านหน้าถัดไปไม่ทัน
            Document doc = Jsoup.connect(nextPage).timeout(60 * 1000).get();/////////////////////////
            //TimeUnit.SECONDS.sleep(3);   // หน่วงเวลา 2 วินาที  ไม่งั้นอ่านหน้าถัดไปไม่ทัน

            //next page
            Elements keep = new Elements();     //สร้างมาเพื่อเก็บค่า

            Elements elesNextPage = doc.getElementsByTag("link");
            for (Element ele : elesNextPage) {
                Element img = ele.select("link").first();
                for (Attribute attribute : ele.attributes()) {
                    if (attribute.getValue().equalsIgnoreCase("next")) {    //ตรวจสอบค่า ถ้ามีค่า valut = next
                        keep.add(ele);                                      //ให้เก็บค่านั้น
                    }
                }
            }
            for (Element ele : keep) {                           //ดึงค่าที่เก็บไว้ออกมา
                Element img = ele.select("link").first();
                String strUrl = img.attr("href");                // ดึงurl ออกมา
                nextPage = strUrl;
            }

            System.out.println("++++++++++++++++++++++++++++++++++");

            //ตรงกลาง
            String ud_loop_inner = doc.getElementsByClass("ud_loop_inner").outerHtml();
            Document docUd_loop_inner = Jsoup.parse(ud_loop_inner);  //เชื่อมต่อโดย string
            Elements elements = docUd_loop_inner.getElementsByClass("td-block-span4");
            for (Element ele : elements) {

                Element title = ele.select("a").first();
                String strUrl = title.attr("href");
                String titleUrl = title.attr("title");

                Element img = ele.select("img").first();
                String strImg = img.attr("src");

                Element date = ele.select("time").first();
                String strDate = date.attr("datetime");

                String con = Khaosod.content(strUrl);
                System.out.println("title : " + titleUrl);
                System.out.println("link : " + strUrl);
                System.out.println("img_link :" + strImg);
                System.out.println("date : " + strDate);
                System.out.println("content : " + con);

                System.out.println(" ");
            }
            ++countPage;
            System.out.println("+++++++++++++++ "+countPage+" +++++++++++++++++++");
            // System.out.println(ud_loop_inner);

            if (keep.isEmpty()) {
                System.out.println("empty value");
                check = false;
                // nextPage = "";
                break;
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        //String url = "https://www.khaosod.co.th/newspaper-column/analysis-today-politics";
        String url = "https://www.khaosod.co.th/stock-monitor";
        Khaosod a = new Khaosod();
        a.firstPage(url);
        //a.nextPage();

    } 
}
