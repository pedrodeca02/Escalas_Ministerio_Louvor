package com.example.louvor;

import java.time.LocalDate;
import java.util.List;

public class EscalaRequest {

    private LocalDate dataEscala;
    private List<EscalaItemRequest> itens;

    public LocalDate getDataEscala() {
        return dataEscala;
    }

    public void setDataEscala(LocalDate dataEscala) {
        this.dataEscala = dataEscala;
    }

    public List<EscalaItemRequest> getItens() {
        return itens;
    }

    public void setItens(List<EscalaItemRequest> itens) {
        this.itens = itens;
    }
}
