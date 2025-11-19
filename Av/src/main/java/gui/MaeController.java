package gui;

import dao.MaeDAO;
import modelo.Mae;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.util.Optional;

public class MaeController {

    // --- Elementos da Interface (Devem bater com os fx:id do FXML) ---
    @FXML private TextField txtNome;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtEndereco;
    @FXML private DatePicker dpNascimento;

    @FXML private TableView<Mae> tabelaMaes;
    @FXML private TableColumn<Mae, Integer> colId;
    @FXML private TableColumn<Mae, String> colNome;
    @FXML private TableColumn<Mae, String> colTelefone;
    @FXML private TableColumn<Mae, LocalDate> colNascimento;

    // --- Atributos de Lógica ---
    private MaeDAO maeDAO = new MaeDAO();
    private ObservableList<Mae> listaMaes;
    private Mae maeSelecionada; // Guarda a mãe que está sendo editada no momento

    // --- Método Inicial (Executado ao abrir a tela) ---
    @FXML
    public void initialize() {
        // Configura as colunas para buscarem os atributos da classe Mae
        colId.setCellValueFactory(new PropertyValueFactory<>("idMae")); // getidMae()
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome")); // getNome()
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone")); // getTelefone()
        colNascimento.setCellValueFactory(new PropertyValueFactory<>("dataNascimento")); // getDataNascimento()

        // Carrega os dados do banco
        atualizarTabela();

        // Adiciona evento: Ao clicar em uma linha da tabela, preenche os campos
        tabelaMaes.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarMae(newValue)
        );
    }

    // --- Ações dos Botões ---

    @FXML
    public void salvarMae() {
        // 1. Validação simples
        if (txtNome.getText().isEmpty() || dpNascimento.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Obrigatórios", "Por favor, preencha nome e data de nascimento.");
            return;
        }

        // 2. Criação do objeto com dados da tela
        Mae mae = new Mae();
        mae.setNome(txtNome.getText());
        mae.setTelefone(txtTelefone.getText());
        mae.setEndereco(txtEndereco.getText());
        mae.setDataNascimento(dpNascimento.getValue()); // Assumindo que seu Model usa LocalDate

        try {
            if (maeSelecionada == null) {
                // MODO INSERÇÃO (Nova Mãe)
                maeDAO.add(mae); // Método do seu DAO que faz o INSERT
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Mãe cadastrada com sucesso!");
            } else {
                // MODO EDIÇÃO (Atualizar Mãe existente)
                mae.setIdMae(maeSelecionada.getIdMae()); // Mantém o ID original
                maeDAO.update(mae); // Método do seu DAO que faz o UPDATE
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Dados atualizados com sucesso!");
            }

            limparCampos();
            atualizarTabela();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao salvar no banco: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void excluirMae() {
        if (maeSelecionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleção Necessária", "Selecione uma mãe na tabela para excluir.");
            return;
        }

        // Confirmação antes de excluir
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Você tem certeza que deseja excluir " + maeSelecionada.getNome() + "?");
        alert.setContentText("Esta ação não pode ser desfeita.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                maeDAO.remove(maeSelecionada.getIdMae()); // Método do seu DAO que faz o DELETE
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Registro excluído.");
                limparCampos();
                atualizarTabela();
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível excluir. Verifique se ela não possui vínculos com Encontros.");
            }
        }
    }

    @FXML
    public void limparCampos() {
        txtNome.clear();
        txtTelefone.clear();
        txtEndereco.clear();
        dpNascimento.setValue(null);
        maeSelecionada = null; // Importante: Volta para o modo de "Nova Inserção"
        tabelaMaes.getSelectionModel().clearSelection();
    }

    // --- Métodos Auxiliares ---

    private void atualizarTabela() {
        try {
            // Busca lista do DAO e converte para ObservableList do JavaFX
            listaMaes = FXCollections.observableArrayList(maeDAO.getLista());
            tabelaMaes.setItems(listaMaes);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Conexão", "Erro ao carregar lista de mães: " + e.getMessage());
        }
    }

    private void selecionarMae(Mae mae) {
        if (mae != null) {
            maeSelecionada = mae; // Marca que estamos editando esta mãe
            txtNome.setText(mae.getNome());
            txtTelefone.setText(mae.getTelefone());
            txtEndereco.setText(mae.getEndereco());
            // Conversão de data se necessário, aqui assume que Model já é LocalDate
            dpNascimento.setValue(mae.getDataNascimento());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}