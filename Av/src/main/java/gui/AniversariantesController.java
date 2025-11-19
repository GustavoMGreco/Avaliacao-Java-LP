package gui;

import dao.MaeDAO;
import modelo.Mae;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class AniversariantesController {

    @FXML private TableView<Mae> tabelaAniversariantes;
    @FXML private TableColumn<Mae, String> colNome;
    @FXML private TableColumn<Mae, String> colTelefone;
    @FXML private TableColumn<Mae, LocalDate> colNascimento;
    @FXML private TableColumn<Mae, String> colEndereco;

    private MaeDAO maeDAO = new MaeDAO();

    @FXML
    public void initialize() {
        // Configurar Colunas
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        colNascimento.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
        colEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));

        carregarAniversariantes();
    }

    private void carregarAniversariantes() {
        try {
            // Chama o metodo específico do DAO para aniversariantes do mês
            tabelaAniversariantes.setItems(FXCollections.observableArrayList(maeDAO.getAniversariantesDoMes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}