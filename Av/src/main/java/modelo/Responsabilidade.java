package modelo;

public class Responsabilidade {

    private int idResponsabilidade;
    private Mae mae;
    private Servico servico;
    private String descricaoAtividade;

    public Responsabilidade() {
    }

    public int getIdResponsabilidade() {
        return idResponsabilidade;
    }

    public void setIdResponsabilidade(int idResponsabilidade) {
        this.idResponsabilidade = idResponsabilidade;
    }

    public Mae getMae() {
        return mae;
    }

    public void setMae(Mae mae) {
        this.mae = mae;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public String getDescricaoAtividade() {
        return descricaoAtividade;
    }

    public void setDescricaoAtividade(String descricaoAtividade) {
        this.descricaoAtividade = descricaoAtividade;
    }
}
