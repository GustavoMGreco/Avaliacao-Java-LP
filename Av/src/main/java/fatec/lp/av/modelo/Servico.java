package fatec.lp.av.modelo;

public class Servico {

    private int idServico;
    private String nome;

    public Servico() {
    }

    public int getIdServico() {
        return idServico;
    }

    public void setIdServico(int idServico) {
        this.idServico = idServico;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return this.nome; // Facilita o uso em ComboBoxes
    }
}
