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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EncontroController {

    @FXML private DatePicker dpDataEncontro;
    @FXML private ComboBox<String> cbStatus;
    @FXML private VBox vboxServicos;

    private MaeDAO maeDAO = new MaeDAO();
    private ServicoDAO servicoDAO = new ServicoDAO();
    private EncontroDAO encontroDAO = new EncontroDAO();

    private ObservableList<Mae> listaMaes;
    private List<Servico> listaServicos;

    // Mapa para guardar as referências dos controles criados dinamicamente
    // Chave: ID do Serviço, Valor: Um par contendo o ComboBox e o TextField daquela linha
    private Map<Integer, ControlesServico> mapaControles = new HashMap<>();

    // Classe auxiliar interna para agrupar os controles de cada linha
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
        // 1. Configurar Status
        cbStatus.setItems(FXCollections.observableArrayList("Agendado", "Realizado", "Cancelado"));
        cbStatus.setValue("Agendado");

        // 2. Carregar dados auxiliares
        listaMaes = FXCollections.observableArrayList(maeDAO.getLista());
        listaServicos = servicoDAO.getLista();

        // 3. Gerar a interface dinamicamente
        gerarCamposDeServicos();
    }

    private void gerarCamposDeServicos() {
        vboxServicos.getChildren().clear();
        mapaControles.clear();

        if (listaServicos.isEmpty()) {
            vboxServicos.getChildren().add(new Label("Nenhum serviço cadastrado no banco de dados."));
            return;
        }

        for (Servico servico : listaServicos) {
            // Criação do layout da linha
            HBox linha = new HBox(15);
            linha.setAlignment(Pos.CENTER_LEFT);
            linha.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");

            // Nome do Serviço (Label)
            Label lblServico = new Label(servico.getNome());
            lblServico.setMinWidth(180);
            lblServico.setStyle("-fx-font-weight: bold;");

            // ComboBox de Mães
            ComboBox<Mae> cbMae = new ComboBox<>(listaMaes);
            cbMae.setPromptText("Selecione a Mãe...");
            cbMae.setMinWidth(200);

            // Campo de Descrição
            TextField txtDescricao = new TextField();
            txtDescricao.setPromptText("Descrição da atividade (Opcional)");
            HBox.setHgrow(txtDescricao, Priority.ALWAYS); // Ocupa o espaço restante

            // Adiciona ao mapa para recuperarmos depois
            mapaControles.put(servico.getIdServico(), new ControlesServico(cbMae, txtDescricao));

            // Adiciona componentes à linha e a linha ao VBox principal
            linha.getChildren().addAll(lblServico, cbMae, txtDescricao);
            vboxServicos.getChildren().add(linha);
        }
    }

    @FXML
    public void buscarEncontro() {
        LocalDate data = dpDataEncontro.getValue();
        if (data == null) return;

        limparFormularioServicos(); // Limpa seleções anteriores visualmente

        try {
            Encontro encontro = encontroDAO.getEncontroPorData(data);

            if (encontro != null) {
                // Encontro EXISTE: Preencher campos
                cbStatus.setValue(encontro.getStatus());

                // Preencher as responsabilidades
                for (Responsabilidade resp : encontro.getResponsabilidades()) {
                    int idServico = resp.getServico().getIdServico();
                    ControlesServico controles = mapaControles.get(idServico);

                    if (controles != null) {
                        // Selecionar a mãe correta no ComboBox
                        // Precisamos encontrar o objeto Mae na listaMaes que tem o mesmo ID
                        for (Mae m : listaMaes) {
                            if (m.getIdMae() == resp.getMae().getIdMae()) {
                                controles.comboMae.setValue(m);
                                break;
                            }
                        }
                        controles.txtDescricao.setText(resp.getDescricaoAtividade());
                    }
                }
            } else {
                // Encontro NÃO EXISTE: Resetar status para padrão (Novo Encontro)
                cbStatus.setValue("Agendado");
                // Campos já foram limpos pelo limparFormularioServicos()
            }

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao buscar encontro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void salvarEncontro() {
        if (dpDataEncontro.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione uma data para o encontro.");
            return;
        }

        try {
            Encontro encontro = new Encontro();
            encontro.setDataEncontro(dpDataEncontro.getValue());
            encontro.setStatus(cbStatus.getValue());

            // Coletar responsabilidades da tela
            boolean temPeloMenosUmaMae = false;

            for (Servico servico : listaServicos) {
                ControlesServico controles = mapaControles.get(servico.getIdServico());
                Mae maeSelecionada = controles.comboMae.getValue();

                if (maeSelecionada != null) {
                    Responsabilidade resp = new Responsabilidade();
                    resp.setServico(servico);
                    resp.setMae(maeSelecionada);
                    resp.setDescricaoAtividade(controles.txtDescricao.getText());

                    encontro.adicionarResponsabilidade(resp);
                    temPeloMenosUmaMae = true;
                }
            }

            if (!temPeloMenosUmaMae) {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione pelo menos uma mãe responsável para algum serviço.");
                return;
            }

            // Verifica se é Update ou Insert verificando se já existe no banco
            Encontro existente = encontroDAO.getEncontroPorData(encontro.getDataEncontro());

            if (existente != null) {
                // UPDATE
                encontro.setIdEncontro(existente.getIdEncontro()); // Importante: manter o ID
                encontroDAO.update(encontro);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Encontro atualizado com sucesso!");
            } else {
                // INSERT
                encontroDAO.add(encontro);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Encontro criado com sucesso!");
            }

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao salvar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void excluirEncontro() {
        LocalDate data = dpDataEncontro.getValue();
        if (data == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione uma data.");
            return;
        }

        Encontro encontro = encontroDAO.getEncontroPorData(data);
        if (encontro == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Não existe encontro salvo nesta data para excluir.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Exclusão");
        confirm.setHeaderText("Excluir encontro de " + data + "?");
        confirm.setContentText("Se o encontro já ocorreu, ele será apenas cancelado. Se for futuro, será excluído.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (data.isBefore(LocalDate.now())) {
                    // Regra de negócio: Encontros passados são cancelados logicamente
                    encontroDAO.cancelar(encontro.getIdEncontro());
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Info", "Encontro passado marcado como 'Cancelado'.");
                    buscarEncontro(); // Recarrega para mostrar o status novo
                } else {
                    // Encontros futuros são excluídos fisicamente
                    encontroDAO.remove(encontro.getIdEncontro(), data);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Info", "Encontro excluído permanentemente.");
                    limparTela();
                }
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao excluir: " + e.getMessage());
            }
        }
    }

    @FXML
    public void limparTela() {
        dpDataEncontro.setValue(null);
        cbStatus.setValue("Agendado");
        limparFormularioServicos();
    }

    private void limparFormularioServicos() {
        for (ControlesServico ctrl : mapaControles.values()) {
            ctrl.comboMae.setValue(null);
            ctrl.txtDescricao.clear();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}