package fatec.lp.av.modelo;

import java.time.LocalDate;

public class Mae {

    private int idMae;
    private String nome;
    private String telefone;
    private String endereco;
    private LocalDate dataNascimento;

    public Mae() {
    }
    public Mae(String nome, String telefone, String endereco, LocalDate dataNascimento) {
        this.nome = nome;
        this.telefone = telefone;
        this.endereco = endereco;
        this.dataNascimento = dataNascimento;
    }

    public int getIdMae() {
        return idMae;
    }

    public void setIdMae(int idMae) {
        this.idMae = idMae;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    @Override
    public String toString() {
        return this.nome; // Facilita o uso em ComboBoxes na GUI
    }
}