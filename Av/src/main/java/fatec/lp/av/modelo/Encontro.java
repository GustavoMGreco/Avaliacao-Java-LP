package fatec.lp.av.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Encontro {

    private int idEncontro;
    private LocalDate dataEncontro;
    private String status;

    private List<Responsabilidade> responsabilidades;

    public Encontro() {
        this.responsabilidades = new ArrayList<>();
    }

    public int getIdEncontro() {
        return idEncontro;
    }

    public void setIdEncontro(int idEncontro) {
        this.idEncontro = idEncontro;
    }

    public LocalDate getDataEncontro() {
        return dataEncontro;
    }

    public void setDataEncontro(LocalDate dataEncontro) {
        this.dataEncontro = dataEncontro;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Responsabilidade> getResponsabilidades() {
        return responsabilidades;
    }

    public void setResponsabilidades(List<Responsabilidade> responsabilidades) {
        this.responsabilidades = responsabilidades;
    }

    public void adicionarResponsabilidade(Responsabilidade resp) {
        this.responsabilidades.add(resp);
    }
}
