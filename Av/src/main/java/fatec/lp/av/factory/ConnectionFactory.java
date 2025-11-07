package fatec.lp.av.factory;

import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static final String URL = "jdbc:mysql://localhost:3306/avaliacao_java";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public ConnectionFactory getConnection() {
        try {
            return (ConnectionFactory) DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco de dados: " + e.getMessage(), e);
        }
    }
}
