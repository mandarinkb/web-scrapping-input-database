
import java.util.regex.*;

public class RegexMatchCount {
    public int countMatchSrring(String inputString, String regex) {
        Pattern pattern = Pattern.compile(regex);  //"(.*)" + regex + "(.*)"
        Matcher matcher = pattern.matcher(inputString);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
    
    
    public static void main(String[] args) {
        RegexMatchCount rmc = new RegexMatchCount();
        String hello = "HelloxxxHelloxxxHello";
        String regex = "Hello";
        int count = rmc.countMatchSrring(hello, regex);
        System.out.println(count);    // prints 3
    }
}
