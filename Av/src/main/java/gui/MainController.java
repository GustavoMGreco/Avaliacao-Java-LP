package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.control.ChoiceDialog;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import dao.EncontroDAO;
import modelo.Encontro;
import modelo.Responsabilidade;

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
        EncontroDAO dao = new EncontroDAO();

        List<Encontro> listaEncontros = dao.getListaEncontros();

        if (listaEncontros.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Não há encontros cadastrados para gerar relatório.");
            return;
        }

        ChoiceDialog<Encontro> dialog = new ChoiceDialog<>(listaEncontros.get(0), listaEncontros);
        dialog.setTitle("Gerar Relatório");
        dialog.setHeaderText("Selecione o Encontro");
        dialog.setContentText("Encontro:");

        Optional<Encontro> result = dialog.showAndWait();

        if (result.isPresent()) {
            Encontro selecionadoResumo = result.get();

            try {
                String nomeArquivo = gerarArquivoTxt(selecionadoResumo);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso",
                        "Relatório gerado com sucesso!\nArquivo salvo como: " + nomeArquivo);

            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro de Arquivo", "Erro ao salvar o arquivo: " + e.getMessage());
            }
        }
    }

    private String gerarArquivoTxt(Encontro encontro) throws IOException {
        DateTimeFormatter fmtArquivo = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter fmtCabecalho = DateTimeFormatter.ofPattern("dd/MM");

        String nomeArquivo = "relatorio_encontro_" + encontro.getDataEncontro().format(fmtArquivo) + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            writer.write("Data do Encontro: " + encontro.getDataEncontro().format(fmtCabecalho));
            writer.newLine();
            writer.write("Serviços:");
            writer.newLine();
            writer.newLine();

            if (encontro.getResponsabilidades() != null && !encontro.getResponsabilidades().isEmpty()) {
                for (Responsabilidade resp : encontro.getResponsabilidades()) {
                    String nomeServico = resp.getServico().getNome().toUpperCase();
                    String nomeMae = resp.getMae().getNome();
                    writer.write(nomeServico + ": " + nomeMae);
                    writer.newLine();
                }
            } else {
                writer.write("(Nenhum serviço registrado para este encontro)");
            }
        }
        return nomeArquivo;
    }
}