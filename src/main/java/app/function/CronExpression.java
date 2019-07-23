package app.function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CronExpression {

    @Autowired
    private Query query;

    public String cronExpressionTask() {
        return query.StrExcuteQuery("SELECT cron_expression FROM schedule WHERE project_name = 'web scrapping input database' AND function_name = 'runTask'");
    }
}
