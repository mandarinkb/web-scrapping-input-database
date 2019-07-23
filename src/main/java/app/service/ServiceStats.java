package app.service;


public interface ServiceStats {
    public void statsOfTeam(String url ,String league);       // สถิติต่างๆของสโมสรฟุตบอลในลีก
    public void teamDetail(String url, String league);        //ผู้เล่นนักเตะทีม และ สถิตินักเตะ
    public void thaipremierleaguePlayerProfile(String url);   //ข้อมูลและประวัตินักเตะไทยลีก
    public void premierleaguePlayerProfile(String url);       //ข้อมูลและประวัตินักเตะพรีเมียร์ลีก อังกฤษ
}
