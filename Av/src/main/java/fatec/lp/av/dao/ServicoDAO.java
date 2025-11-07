package fatec.lp.av.dao;


import fatec.lp.av.factory.ConnectionFactory;
import fatec.lp.av.modelo.Servico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicoDAO {

    private Connection connection;

    public ServicoDAO() {
        this.connection = (Connection) new ConnectionFactory().getConnection();
    }

    public List<Servico> getLista() {
        List<Servico> servicos = new ArrayList<>();
        String sql = "SELECT * FROM servico ORDER BY nome";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Servico servico = new Servico();
                servico.setIdServico(rs.getInt("id_servico"));
                servico.setNome(rs.getString("nome"));
                servicos.add(servico);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar serviços: " + e.getMessage(), e);
        }
        return servicos;
    }

    public Servico getServicoById(int id) {
        String sql = "SELECT * FROM servico WHERE id_servico = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Servico servico = new Servico();
                    servico.setIdServico(rs.getInt("id_servico"));
                    servico.setNome(rs.getString("nome"));
                    return servico;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar serviço por ID: " + e.getMessage(), e);
        }
        return null;
    }

}