package fatec.lp.av.factory;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Connection {
    public static Connection getConnection() {
        try {
            return (Connection) DriverManager.getConnection("jdbc:mysql://localhost/avaliacao_java", "root", "");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
