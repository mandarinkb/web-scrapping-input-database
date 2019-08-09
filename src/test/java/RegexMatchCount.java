
import java.util.regex.*;

public class RegexMatchCount {
    public boolean stringMatch(String str , String search){
        return str.matches("(.*)"+search+"(.*)");
    }
    
    //นับคำเหมือนในประโยค
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
        //String sentence = "ตลาดหุ้นไทยปรับตัวลงช่วงต้นสัปดาห์จากแรงเทขายของกลุ่มนักลงทุนสถาบันท่ามกลางคาดการณ์ว่าโอกาสที่เฟดจะปรับลดดอกเบี้ยอย่างรุนแรงลดน้อยลง หุ้นไทยฟื้นตัวกลับมาได้บางส่วนในเวลาต่อมา โดยได้รับปัจจัยหนุนจากรายงานข่าวเกี่ยวกับการเตรียมเปิดการเจรจาการค้ารอบใหม่ระหว่างสหรัฐ และจีน อย่างไรก็ดี ตลาดหุ้นไทยร่วงลงอีกครั้งช่วงปลายสัปดาห์ ตามทิศทางตลาดหุ้นภูมิภาค หลังข้อมูลเศรษฐกิจสหรัฐ ออกมาค่อนข้างดี ทำให้นักลงทุนกังวลว่า เฟดอาจปรับมุมมองเกี่ยวกับการผ่อนคลายนโยบายการเงินในการประชุมช่วงสิ้นเดือนก.ค.";
        String sentence = "ตลาดหุ้น ไทย ปรับ ตัว ลง ช่วง ต้น สัปดาห์ จาก แรง เท ขาย ของ กลุ่ม นัก ลงทุน สถาบัน ท่ามกลาง คาด การณ์ ว่า โอกาส ที่ เฟดจะ ปรับ ลด ดอกเบี้ย อย่าง รุนแรง ลด น้อย ลง   หุ้น ไทย ฟื้นตัว กลับ มา ได้ บาง ส่วน ใน เวลา ต่อ มา   โดย ได้ รับ ปัจจัย หนุน จาก รายงาน ข่าว เกี่ยว กับ การเต รี ยม เปิด การ เจรจา การ ค้า รอบ ใหม่ ระหว่างสหรัฐ   และ จีน   อย่างไรก็ดี   ตลาดหุ้น ไทย ร่วง ลง อีก ครั้ง ช่วง ปลาย สัปดาห์   ตาม ทิศทาง ตลาดหุ้น ภูมิภาค   หลัง ข้อมูล เศรษฐกิจสหรัฐ   ออก มา ค่อนข้าง ดี   ทำให้ นัก ลงทุน กังวล ว่า   เฟดอาจ ปรับ มุม มอง เกี่ยว กับ การ ผ่อนคลาย นโยบาย การเงิน ใน การ ประชุม ช่วง สิ้น เดือน ก . ค . ";
        String regex = "ลง";
        int count = rmc.countMatchSrring(sentence, regex);
        System.out.println(count);    // prints 3
        
        System.out.println(rmc.stringMatch(sentence, regex)); 
        
    }
}
