import java.util.Locale;
import java.text.BreakIterator;

//import com.ibm.icu.text.BreakIterator;
//import com.ibm.icu.text.DictionaryBasedBreakIterator;

public class ThaiWordbreak {

    public static void printEachForward(BreakIterator boundary, String source) {
        StringBuffer strout = new StringBuffer();
        int start = boundary.first();
        for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {
            strout.append(source.substring(start, end) + "|");
        }
        System.out.println(strout.toString());
    }

    public static void main(String[] args) {
        Locale thaiLocale = new Locale("th");
        //String input = "ตลาดหุ้นไทยปรับตัวลงช่วงต้นสัปดาห์จากแรงเทขายของกลุ่มนักลงทุนสถาบันท่ามกลางคาดการณ์ว่าโอกาสที่เฟดจะปรับลดดอกเบี้ยอย่างรุนแรงลดน้อยลง หุ้นไทยฟื้นตัวกลับมาได้บางส่วนในเวลาต่อมา โดยได้รับปัจจัยหนุนจากรายงานข่าวเกี่ยวกับการเตรียมเปิดการเจรจาการค้ารอบใหม่ระหว่างสหรัฐ และจีน อย่างไรก็ดี ตลาดหุ้นไทยร่วงลงอีกครั้งช่วงปลายสัปดาห์ ตามทิศทางตลาดหุ้นภูมิภาค หลังข้อมูลเศรษฐกิจสหรัฐ ออกมาค่อนข้างดี ทำให้นักลงทุนกังวลว่า เฟดอาจปรับมุมมองเกี่ยวกับการผ่อนคลายนโยบายการเงินในการประชุมช่วงสิ้นเดือนก.ค.";
        String input = "สาวตากลมนั่งตากลม";
        BreakIterator boundary = BreakIterator.getWordInstance(thaiLocale);
        //BreakIterator boundary = DictionaryBasedBreakIterator.getWordInstance(thaiLocale);
        boundary.setText(input);
        printEachForward(boundary, input);
    }
}
