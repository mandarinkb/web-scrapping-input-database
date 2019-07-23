package app.function;

import app.dao.Database;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Query {

    @Autowired
    private Database db;

    public String StrExcuteQuery(String sql) {
        String str = "";
        List<String> columnNames = new ArrayList<String>();
        try {
            ResultSet rs = db.getResultSet(db.connectDatase(), sql);
            if (rs != null) {
                ResultSetMetaData columns = rs.getMetaData();
                int i = 0;
                while (i < columns.getColumnCount()) {
                    i++;
                    columnNames.add(columns.getColumnName(i));
                }
                while (rs.next()) {
                    for (i = 0; i < columnNames.size(); i++) {
                        str = rs.getString(columnNames.get(i));
                    }
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        finally {
            db.closeConnect(db.connectDatase());
        }
        return str;
    }

    public int IntExcuteQuery(String sql) {
        int returnIn = 0;
        List<String> columnNames = new ArrayList<String>();
        try {
            ResultSet rs = db.getResultSet(db.connectDatase(), sql);
            if (rs != null) {
                ResultSetMetaData columns = rs.getMetaData();
                int i = 0;
                while (i < columns.getColumnCount()) {
                    i++;
                    columnNames.add(columns.getColumnName(i));
                }
                while (rs.next()) {
                    for (i = 0; i < columnNames.size(); i++) {
                        returnIn = rs.getInt(columnNames.get(i));
                    }
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        finally {
            db.closeConnect(db.connectDatase());
        }
        return returnIn;
    }
}
