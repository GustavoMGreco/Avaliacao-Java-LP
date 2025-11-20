package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    // Metodo para abrir novas janelas
    private void abrirJanela(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/" + fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao abrir janela",
                    "Não foi possível carregar o arquivo: " + fxmlPath + "\nErro: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    public void abrirCadastroMaes() {
        abrirJanela("MaeView.fxml", "Cadastro de Mães");
    }

    @FXML
    public void abrirEncontros() {
        abrirJanela("ListaEncontrosView.fxml", "Gerenciamento de Encontros");
    }

    @FXML
    public void abrirAniversariantes() {
        abrirJanela("AniversariantesView.fxml", "Aniversariantes do Mês");
    }

    @FXML
    public void abrirRelatorios() {
        // Placeholder para a Tarefa 8 (Gerar Relatório)
        // Se você não for implementar a tela visual, pode colocar a lógica de geração aqui.
        mostrarAlerta(Alert.AlertType.INFORMATION, "Em Desenvolvimento",
                "A funcionalidade de geração de relatórios (.txt) ainda será implementada.");
    }
}