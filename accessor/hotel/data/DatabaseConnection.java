package accessor.hotel.data;

import accessor.hotel.LogJournal;
import accessor.hotel.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    private Connection connection;
    
    public void connect() {
        if (connection != null) return;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + Paths.DATABASE_PATH);
 
        } catch (SQLException | ClassNotFoundException ex) {
            System.err.println(ex);
            LogJournal.error(ex);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
