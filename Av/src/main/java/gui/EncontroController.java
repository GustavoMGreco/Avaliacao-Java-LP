package gui;

import dao.EncontroDAO;
import dao.MaeDAO;
import dao.ServicoDAO;
import modelo.Encontro;
import modelo.Mae;
import modelo.Responsabilidade;
import modelo.Servico;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EncontroController {

    @FXML private DatePicker dpDataEncontro;
    @FXML private ComboBox<String> cbStatus;
    @FXML private VBox vboxServicos;
    @FXML private Label lblTitulo;

    private MaeDAO maeDAO = new MaeDAO();
    private ServicoDAO servicoDAO = new ServicoDAO();
    private EncontroDAO encontroDAO = new EncontroDAO();

    private ObservableList<Mae> listaMaes;
    private List<Servico> listaServicos;
    private Encontro encontroAtual; // Encontro sendo editado (null se for novo)

    private Map<Integer, ControlesServico> mapaControles = new HashMap<>();

    private static class ControlesServico {
        ComboBox<Mae> comboMae;
        TextField txtDescricao;

        public ControlesServico(ComboBox<Mae> comboMae, TextField txtDescricao) {
            this.comboMae = comboMae;
            this.txtDescricao = txtDescricao;
        }
    }

    @FXML
    public void initialize() {
        cbStatus.setItems(FXCollections.observableArrayList("Agendado", "Realizado", "Cancelado"));

        listaMaes = FXCollections.observableArrayList(maeDAO.getLista());
        listaServicos = servicoDAO.getLista();

        gerarCamposDeServicos();
    }

    // Metodo chamado pela ListaEncontrosController para passar os dados
    public void setEncontro(Encontro encontro) {
        this.encontroAtual = encontro;

        if (encontro != null) {
            lblTitulo.setText("Editando Encontro #" + encontro.getIdEncontro());
            dpDataEncontro.setValue(encontro.getDataEncontro());
            cbStatus.setValue(encontro.getStatus());

            // Preencher serviços
            for (Responsabilidade resp : encontro.getResponsabilidades()) {
                ControlesServico ctrl = mapaControles.get(resp.getServico().getIdServico());
                if (ctrl != null) {
                    // Selecionar a mãe correta
                    for(Mae m : listaMaes) {
                        if(m.getIdMae() == resp.getMae().getIdMae()) {
                            ctrl.comboMae.setValue(m);
                            break;
                        }
                    }
                    ctrl.txtDescricao.setText(resp.getDescricaoAtividade());
                }
            }
        } else {
            lblTitulo.setText("Novo Encontro");
            cbStatus.setValue("Agendado");
        }
    }

    private void gerarCamposDeServicos() {
        vboxServicos.getChildren().clear();
        mapaControles.clear();

        for (Servico servico : listaServicos) {
            HBox linha = new HBox(15);
            linha.setAlignment(Pos.CENTER_LEFT);
            linha.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #e0e0e0;");

            Label lblServico = new Label(servico.getNome());
            lblServico.setMinWidth(180);
            lblServico.setStyle("-fx-font-weight: bold;");

            ComboBox<Mae> cbMae = new ComboBox<>(listaMaes);
            cbMae.setPromptText("Selecione a Mãe...");
            cbMae.setMinWidth(200);

            TextField txtDescricao = new TextField();
            txtDescricao.setPromptText("Descrição da atividade");
            HBox.setHgrow(txtDescricao, Priority.ALWAYS);

            mapaControles.put(servico.getIdServico(), new ControlesServico(cbMae, txtDescricao));
            linha.getChildren().addAll(lblServico, cbMae, txtDescricao);
            vboxServicos.getChildren().add(linha);
        }
    }

    @FXML
    public void salvarEncontro() {
        if (dpDataEncontro.getValue() == null) {
            mostrarAlerta("Erro", "Selecione uma data.");
            return;
        }

        try {
            boolean isNovo = (encontroAtual == null);
            if (isNovo) {
                encontroAtual = new Encontro();
            }

            encontroAtual.setDataEncontro(dpDataEncontro.getValue());
            encontroAtual.setStatus(cbStatus.getValue());

            // Limpa responsabilidades antigas para recriar (estratégia mais simples para update)
            encontroAtual.getResponsabilidades().clear();

            boolean temMae = false;
            for (Servico servico : listaServicos) {
                ControlesServico ctrl = mapaControles.get(servico.getIdServico());
                Mae mae = ctrl.comboMae.getValue();

                if (mae != null) {
                    Responsabilidade resp = new Responsabilidade();
                    resp.setServico(servico);
                    resp.setMae(mae);
                    resp.setDescricaoAtividade(ctrl.txtDescricao.getText());
                    encontroAtual.adicionarResponsabilidade(resp);
                    temMae = true;
                }
            }

            if (!temMae) {
                mostrarAlerta("Aviso", "Selecione ao menos uma mãe responsável.");
                return;
            }

            if (isNovo) {
                encontroDAO.add(encontroAtual);
            } else {
                encontroDAO.update(encontroAtual);
            }

            fecharJanela();

        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao salvar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void fecharJanela() {
        Stage stage = (Stage) dpDataEncontro.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}