
package app.service;


public interface ServicePremierLeague {
    public void resultsPresentContent(String url, String indexName , String season, String baseLinkImg);     //ผลการแข่งขันพรีเมียร์ลีก อังกฤษ (ปัจจุบัน)
    public void resultsContent(String url, String indexName , String season, String baseLinkImg);     //ผลการแข่งขันพรีเมียร์ลีก อังกฤษ  (อดีต)   
    public void fixturesContent(String url, String indexName ,String season, String baseLinkImg);     //ตารางแข่งขันฟุตบอลพรีเมียร์ลีก อังกฤษ
    public void allTablePremierLeagueContent(String url, String indexName ,String season, String baseLinkImg);           //ตารางคะแนนรวมพรีเมียร์ลีก อังกฤษ 
    public void homeTablePremierLeagueContent(String url, String indexName ,String season, String baseLinkImg);          //ตารางคะแนนเจ้าบ้านพรีเมียร์ลีก อังกฤษ
    public void awayTablePremierLeagueContent(String url, String indexName ,String season, String baseLinkImg);          //ตารางคะแนนทีมเยือนพรีเมียร์ลีก อังกฤษ
    public void allTablePremierLeague2014_2015Content(String url, String indexName ,String season, String baseLinkImg);  //ตารางคะแนนรวมพรีเมียร์ลีก อังกฤษ 2014-2015
    
    public void statistics(String url, String indexName ,String season, String baseLinkImg);  //รวมสถิติต่างๆพรีเมียร์ลีก อังกฤษ
    public void getStaffTeamDetail(String link, String url);     //ข้อมูลผู้จัดการทีม
    public void listTeamPremierLeague(String url, String indexName, String season, String detail ,String baseLink);   //รายชื่อนักเตะและผู้จัดการทีมพรีเมียร์ลีก 

    //เพิ่มเติม 8-8-2562
    public void presentTeamPremierLeague(String objRadis);
    public void presentPlayerPremierLeague(String objRadis);
    public void presentPlayerDetailPremierLeague(String objRadis);
    
    //เพิ่มเติม 18-8-2562
    public void getContentScoreAnalyzePage(String objRadis);
}
