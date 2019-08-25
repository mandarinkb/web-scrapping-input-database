package app;

import app.function.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  //เปิดใช้งาน
public class App {
    @Autowired
    private CronExpression cron;
   
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
    @Bean
    public String cronExpressionValue() {
        return cron.cronExpressionTask();
    }     
}
