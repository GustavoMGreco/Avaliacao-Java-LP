package dao;

import factory.ConnectionFactory;
import modelo.Mae;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaeDAO {

    private Connection connection;

    public MaeDAO() {
        this.connection = (Connection) new ConnectionFactory().getConnection();
    }


    public void add(Mae mae) {
        String sql = "INSERT INTO mae (nome, telefone, endereco, data_nascimento) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, mae.getNome());
            stmt.setString(2, mae.getTelefone());
            stmt.setString(3, mae.getEndereco());
            stmt.setDate(4, Date.valueOf(mae.getDataNascimento()));
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar mãe: " + e.getMessage(), e);
        }
    }

    public void update(Mae mae) {
        String sql = "UPDATE mae SET nome=?, telefone=?, endereco=?, data_nascimento=? WHERE id_mae=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, mae.getNome());
            stmt.setString(2, mae.getTelefone());
            stmt.setString(3, mae.getEndereco());
            stmt.setDate(4, Date.valueOf(mae.getDataNascimento()));
            stmt.setInt(5, mae.getIdMae());
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar mãe: " + e.getMessage(), e);
        }
    }

    public void remove(int idMae) {
        String sql = "DELETE FROM mae WHERE id_mae=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idMae);
            stmt.execute();
        } catch (SQLException e) {
            // Tratar erro de violação de FK (mãe associada a uma responsabilidade)
            throw new RuntimeException("Erro ao remover mãe. Verifique se ela está associada a um encontro.", e);
        }
    }

    public List<Mae> getLista() {
        List<Mae> maes = new ArrayList<>();
        String sql = "SELECT * FROM mae ORDER BY nome";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Mae mae = new Mae();
                mae.setIdMae(rs.getInt("id_mae"));
                mae.setNome(rs.getString("nome"));
                mae.setTelefone(rs.getString("telefone"));
                mae.setEndereco(rs.getString("endereco"));
                mae.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                maes.add(mae);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar mães: " + e.getMessage(), e);
        }
        return maes;
    }

    public Mae getMaeById(int id) {
        String sql = "SELECT * FROM mae WHERE id_mae = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Mae mae = new Mae();
                    mae.setIdMae(rs.getInt("id_mae"));
                    mae.setNome(rs.getString("nome"));
                    mae.setTelefone(rs.getString("telefone"));
                    mae.setEndereco(rs.getString("endereco"));
                    mae.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                    return mae;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar mãe por ID: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Mae> getAniversariantesDoMes() {
        List<Mae> aniversariantes = new ArrayList<>();
        String sql = "SELECT * FROM mae WHERE MONTH(data_nascimento) = MONTH(CURDATE()) ORDER BY DAY(data_nascimento), nome";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Mae mae = new Mae();
                mae.setIdMae(rs.getInt("id_mae"));
                mae.setNome(rs.getString("nome"));
                mae.setTelefone(rs.getString("telefone"));
                mae.setEndereco(rs.getString("endereco"));
                mae.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                aniversariantes.add(mae);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar aniversariantes: " + e.getMessage(), e);
        }
        return aniversariantes;
    }
}
