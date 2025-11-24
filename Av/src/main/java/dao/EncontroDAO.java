package dao;

import factory.ConnectionFactory;
import modelo.Encontro;
import modelo.Mae;
import modelo.Responsabilidade;
import modelo.Servico;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EncontroDAO {

    private Connection connection;

    public EncontroDAO() {
        this.connection = (Connection) new ConnectionFactory().getConnection();
    }

    public void add(Encontro encontro) {
        String sqlEncontro = "INSERT INTO encontro (data_encontro, status) VALUES (?, ?)";
        String sqlResponsabilidade = "INSERT INTO responsabilidade (id_mae, id_servico, id_encontro, descricao_atividade) VALUES (?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmtEncontro = connection.prepareStatement(sqlEncontro, Statement.RETURN_GENERATED_KEYS)) {
                stmtEncontro.setDate(1, Date.valueOf(encontro.getDataEncontro()));
                stmtEncontro.setString(2, encontro.getStatus());
                stmtEncontro.executeUpdate();

                try (ResultSet rs = stmtEncontro.getGeneratedKeys()) {
                    if (rs.next()) {
                        encontro.setIdEncontro(rs.getInt(1));
                    }
                }
            }

            try (PreparedStatement stmtResp = connection.prepareStatement(sqlResponsabilidade)) {
                for (Responsabilidade resp : encontro.getResponsabilidades()) {
                    stmtResp.setInt(1, resp.getMae().getIdMae());
                    stmtResp.setInt(2, resp.getServico().getIdServico());
                    stmtResp.setInt(3, encontro.getIdEncontro());
                    stmtResp.setString(4, resp.getDescricaoAtividade());
                    stmtResp.addBatch();
                }
                stmtResp.executeBatch();
            }

            connection.commit();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException("Erro no rollback: " + ex.getMessage(), ex);
            }
            throw new RuntimeException("Erro ao adicionar encontro: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(Encontro encontro) {
        if (encontro.getDataEncontro().isBefore(LocalDate.now())) {
            throw new RuntimeException("Não é permitido alterar encontros que já ocorreram.");
        }

        String sqlUpdateEnc = "UPDATE encontro SET data_encontro=?, status=? WHERE id_encontro=?";
        String sqlDeleteResp = "DELETE FROM responsabilidade WHERE id_encontro=?";
        String sqlInsertResp = "INSERT INTO responsabilidade (id_mae, id_servico, id_encontro, descricao_atividade) VALUES (?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(sqlUpdateEnc)) {
                stmt.setDate(1, Date.valueOf(encontro.getDataEncontro()));
                stmt.setString(2, encontro.getStatus());
                stmt.setInt(3, encontro.getIdEncontro());
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = connection.prepareStatement(sqlDeleteResp)) {
                stmt.setInt(1, encontro.getIdEncontro());
                stmt.executeUpdate();
            }

            try (PreparedStatement stmtResp = connection.prepareStatement(sqlInsertResp)) {
                for (Responsabilidade resp : encontro.getResponsabilidades()) {
                    stmtResp.setInt(1, resp.getMae().getIdMae());
                    stmtResp.setInt(2, resp.getServico().getIdServico());
                    stmtResp.setInt(3, encontro.getIdEncontro());
                    stmtResp.setString(4, resp.getDescricaoAtividade());
                    stmtResp.addBatch();
                }
                stmtResp.executeBatch();
            }

            connection.commit();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Erro ao alterar encontro: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void remove(int idEncontro, LocalDate dataEncontro) {
        //if (dataEncontro.isBefore(LocalDate.now())) {
        //    throw new RuntimeException("Não é permitido excluir fisicamente encontros passados. Use o cancelamento.");
        //}

        String sqlDelResp = "DELETE FROM responsabilidade WHERE id_encontro=?";
        String sqlDelEnc = "DELETE FROM encontro WHERE id_encontro=?";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(sqlDelResp)) {
                stmt.setInt(1, idEncontro);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = connection.prepareStatement(sqlDelEnc)) {
                stmt.setInt(1, idEncontro);
                stmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Erro ao excluir encontro: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void cancelar(int idEncontro) {
        String sql = "UPDATE encontro SET status='Cancelado' WHERE id_encontro=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idEncontro);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cancelar encontro: " + e.getMessage(), e);
        }
    }


    public Encontro getEncontroPorData(LocalDate data) {
        Encontro encontro = null;
        String sqlEnc = "SELECT * FROM encontro WHERE data_encontro = ?";

        try (PreparedStatement stmtEnc = connection.prepareStatement(sqlEnc)) {
            stmtEnc.setDate(1, Date.valueOf(data));

            try (ResultSet rsEnc = stmtEnc.executeQuery()) {
                if (rsEnc.next()) {
                    encontro = new Encontro();
                    encontro.setIdEncontro(rsEnc.getInt("id_encontro"));
                    encontro.setDataEncontro(rsEnc.getDate("data_encontro").toLocalDate());
                    encontro.setStatus(rsEnc.getString("status"));

                    encontro.setResponsabilidades(getResponsabilidadesPorEncontro(encontro.getIdEncontro()));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar encontro por data: " + e.getMessage(), e);
        }
        return encontro;
    }


    private List<Responsabilidade> getResponsabilidadesPorEncontro(int idEncontro) {
        List<Responsabilidade> responsabilidades = new ArrayList<>();
        String sql = "SELECT r.id_responsabilidade, r.descricao_atividade, " +
                "m.id_mae, m.nome, m.telefone, m.endereco, m.data_nascimento, " +
                "s.id_servico, s.nome AS nome_servico " +
                "FROM responsabilidade r " +
                "JOIN mae m ON r.id_mae = m.id_mae " +
                "JOIN servico s ON r.id_servico = s.id_servico " +
                "WHERE r.id_encontro = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idEncontro);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Mae mae = new Mae();
                    mae.setIdMae(rs.getInt("id_mae"));
                    mae.setNome(rs.getString("nome"));
                    mae.setTelefone(rs.getString("telefone"));
                    mae.setEndereco(rs.getString("endereco"));
                    mae.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());

                    Servico servico = new Servico();
                    servico.setIdServico(rs.getInt("id_servico"));
                    servico.setNome(rs.getString("nome_servico"));

                    Responsabilidade resp = new Responsabilidade();
                    resp.setIdResponsabilidade(rs.getInt("id_responsabilidade"));
                    resp.setDescricaoAtividade(rs.getString("descricao_atividade"));
                    resp.setMae(mae);
                    resp.setServico(servico);

                    responsabilidades.add(resp);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar responsabilidades: " + e.getMessage(), e);
        }
        return responsabilidades;
    }

    public List<Encontro> getListaEncontros() {
        List<Encontro> encontros = new ArrayList<>();
        String sql = "SELECT * FROM encontro ORDER BY data_encontro DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Encontro encontro = new Encontro();
                encontro.setIdEncontro(rs.getInt("id_encontro"));
                encontro.setDataEncontro(rs.getDate("data_encontro").toLocalDate());
                encontro.setStatus(rs.getString("status"));

                encontro.setResponsabilidades(getResponsabilidadesPorEncontro(encontro.getIdEncontro()));

                encontros.add(encontro);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar encontros: " + e.getMessage(), e);
        }
        return encontros;
    }
}
