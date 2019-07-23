
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class StaticTeam {

    public String getNewLinkImage(String url) {
        url = url.replace("../..", "");
        url = url.replace("/..", "");
        url = url.replace("..", "");
        return url;
    }

    public void content(String url) throws IOException, InterruptedException {
        String BaseLinkPalyer = "http://www.livesoccer888.com/thaipremierleague";
        Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
        Elements elements = doc.getElementsByClass("PlayerLeague");
        for (Element ele : elements) {
            Elements elesLogo = ele.getElementsByClass("logo");
            if (elesLogo.hasClass("logo")) {
                Elements elesChild = elesLogo.select("*");
                for (Element eleLogo : elesChild) {
                    if (eleLogo.tagName().equals("img")) {
                        Element eleImg = eleLogo.select("img").first();
                        String img = eleImg.attr("src");
                        img = getNewLinkImage(img);
                        System.out.println(img);
                    }
                }
            }
            String team = ele.getElementsByClass("team").text();
            System.out.println(team);

            Elements elesUl = ele.select("ul");
            for (Element eleChildren : elesUl) {
                Elements elesLi = eleChildren.select("li");
                Elements elesChildren = elesLi.select("*");
                for (Element eleLiAll : elesChildren) {
                    if (eleLiAll.hasClass("player")) {
                        String player = eleLiAll.select(".player").text();
                        System.out.println(player);
                        String nextEle = eleLiAll.nextElementSibling().text();  // get next tag
                        System.out.println(nextEle);
                    }

                    if (eleLiAll.hasClass("goal")) {
                        String goal = eleLiAll.select(".goal").text();
                        System.out.println(goal);
                        String nextEle = eleLiAll.nextElementSibling().text();
                        System.out.println(nextEle);
                    }
                    if (eleLiAll.hasClass("assist")) {
                        String assist = eleLiAll.select(".assist").text();
                        System.out.println(assist);
                        String nextEle = eleLiAll.nextElementSibling().text();
                        System.out.println(nextEle);
                    }
                    if (eleLiAll.hasClass("shutout")) {
                        String shutout = eleLiAll.select(".shutout").text();
                        System.out.println(shutout);
                        String nextEle = eleLiAll.nextElementSibling().text();
                        System.out.println(nextEle);
                    }
                    if (eleLiAll.hasClass("yellow")) {
                        String yellow = eleLiAll.select(".yellow").text();
                        System.out.println(yellow);
                        String nextEle = eleLiAll.nextElementSibling().text();
                        System.out.println(nextEle);
                    }
                    if (eleLiAll.hasClass("yellowred")) {
                        String yellowred = eleLiAll.select(".yellowred").text();
                        System.out.println(yellowred);
                        String nextEle = eleLiAll.nextElementSibling().text();
                        System.out.println(nextEle);
                    }
                    if (eleLiAll.hasClass("red")) {
                        String red = eleLiAll.select(".red").text();
                        System.out.println(red);
                        String nextEle = eleLiAll.nextElementSibling().text();
                        System.out.println(nextEle);
                    }

                    if (eleLiAll.tagName().equals("a")) { // get player page
                        Element eleA = eleLiAll.select("a").first();
                        String linkPlayers = eleA.attr("href");
                        linkPlayers = getNewLinkImage(linkPlayers);
                        System.out.println(BaseLinkPalyer + linkPlayers);
                    } 
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String url = "http://www.livesoccer888.com/thaipremierleague/players/index.php";
        //String url = "http://www.livesoccer888.com/premierleague/players/index.php";
        StaticTeam st = new StaticTeam();
        st.content(url);
    }

}
