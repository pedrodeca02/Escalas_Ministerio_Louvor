package com.example.louvor;

import java.time.LocalDate;
import java.util.List;

public class EscalaResposta {

    private Integer id;
    private LocalDate dataEscala;
    private List<EscalaItemResposta> itens;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDataEscala() {
        return dataEscala;
    }

    public void setDataEscala(LocalDate dataEscala) {
        this.dataEscala = dataEscala;
    }

    public List<EscalaItemResposta> getItens() {
        return itens;
    }

    public void setItens(List<EscalaItemResposta> itens) {
        this.itens = itens;
    }
}
