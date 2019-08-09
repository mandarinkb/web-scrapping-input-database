
package app.service;

public interface ServiceThaiPremierLeague {
    public void resultsPresentContent(String url, String indexName , String season, String baseLinkImg); //ผลการแข่งขันไทยลีก (ปัจจุบัน)
    public void resultsContent(String url, String indexName , String season, String baseLinkImg);     //ผลการแข่งขันไทยลีก (อดีต)                  
    public void fixturesContent(String url, String indexName ,String season, String baseLinkImg);     //ตารางแข่งขันฟุตบอลไทยลีก
    public void allTableThaiPremierLeagueContent(String url, String indexName ,String season, String baseLinkImg);      //ตารางคะแนนรวมไทยลีก
    public void homeTableThaiPremierLeagueContent(String url, String indexName ,String season, String baseLinkImg);     //ตารางคะแนนเจ้าบ้านไทยลีก
    public void awayTableThaiPremierLeagueContent(String url, String indexName ,String season, String baseLinkImg);     //ตารางคะแนนทีมเยือนไทยลีก
    public void allTableThaiPremierLeague2014Content(String url, String indexName ,String season, String baseLinkImg);  //ตารางคะแนนรวมไทยลีก 2014 
    
    public void statistics(String url, String indexName ,String season, String baseLinkImg);   //รวมสถิติต่างๆไทยลีก
    public void getStaffTeamDetail(String link, String url);     //ข้อมูลผู้จัดการทีม
    public void listTeamThaiPremierLeague(String url, String indexName, String season, String detail ,String baseLink);   //รายชื่อนักเตะและผู้จัดการทีมไทยลีก 
    
    //เพิ่มเติม 8-8-2562
    public void presentTeamThaiPremierLeague(String objRadis);
    public void presentPlayerThaiPremierLeague(String objRadis);
    public void presentPlayerDetailThaiPremierLeague(String objRadis);
}
