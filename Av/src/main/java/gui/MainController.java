package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.Util;

import java.io.IOException;

public class MainController {

    // Método genérico para abrir novas janelas
    private static void abrirJanela(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(MainController.class.getResource("/gui/" + fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void abrirCadastroMaes() {
        abrirJanela("MaeView.fxml", "Cadastro de Mães");
    }

    @FXML
    public void abrirEncontros() {
        // Implementar (Tarefa 6 e 7)
        // abrirJanela("EncontroView.fxml", "Gerenciamento de Encontros");
    }

    @FXML
    public void abrirAniversariantes() {
        // Implementar (Tarefa 5)
        // abrirJanela("AniversariantesView.fxml", "Aniversariantes do Mês");
    }

    @FXML
    public void abrirRelatorios() {
        // Implementar (Tarefa 9)
        // abrirJanela("RelatorioView.fxml", "Geração de Relatórios");
    }
}