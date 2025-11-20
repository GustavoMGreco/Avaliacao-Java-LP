package gui;

import dao.EncontroDAO;
import modelo.Encontro;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class ListaEncontrosController {

    @FXML private TableView<Encontro> tabelaEncontros;
    @FXML private TableColumn<Encontro, Integer> colId;
    @FXML private TableColumn<Encontro, LocalDate> colData;
    @FXML private TableColumn<Encontro, String> colStatus;

    private EncontroDAO encontroDAO = new EncontroDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idEncontro"));
        colData.setCellValueFactory(new PropertyValueFactory<>("dataEncontro"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        carregarLista();
    }

    private void carregarLista() {
        try {
            tabelaEncontros.setItems(FXCollections.observableArrayList(encontroDAO.getListaEncontros()));
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao listar encontros: " + e.getMessage());
        }
    }

    @FXML
    public void novoEncontro() {
        abrirFormulario(null);
    }

    @FXML
    public void editarEncontro() {
        Encontro selecionado = tabelaEncontros.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            // É preciso buscar o encontro completo (com responsabilidades) do banco
            Encontro completo = encontroDAO.getEncontroPorData(selecionado.getDataEncontro());
            abrirFormulario(completo);
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione um encontro para editar.");
        }
    }

    @FXML
    public void excluirEncontro() {
        Encontro selecionado = tabelaEncontros.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione um encontro.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar");
        confirm.setHeaderText("Deseja excluir/cancelar o encontro de " + selecionado.getDataEncontro() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (selecionado.getDataEncontro().isBefore(LocalDate.now())) {
                    encontroDAO.cancelar(selecionado.getIdEncontro());
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Info", "Encontro passado foi marcado como Cancelado.");
                } else {
                    encontroDAO.remove(selecionado.getIdEncontro(), selecionado.getDataEncontro());
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Info", "Encontro futuro excluído.");
                }
                carregarLista();
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", e.getMessage());
            }
        }
    }

    private void abrirFormulario(Encontro encontro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/EncontroView.fxml"));
            Parent root = loader.load();

            EncontroController controller = loader.getController();
            controller.setEncontro(encontro); // Passa o encontro para o formulário

            Stage stage = new Stage();
            stage.setTitle(encontro == null ? "Novo Encontro" : "Editar Encontro");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Bloqueia a janela de trás
            stage.showAndWait();

            carregarLista(); // Atualiza a tabela ao fechar o formulário

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}