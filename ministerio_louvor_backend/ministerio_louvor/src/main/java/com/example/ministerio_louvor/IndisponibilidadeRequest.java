package com.example.louvor;

import java.time.LocalDate;

public class IndisponibilidadeRequest {

    private Integer membroId;
    private LocalDate dataIndisponivel;
    private String motivo;

    public Integer getMembroId() {
        return membroId;
    }

    public void setMembroId(Integer membroId) {
        this.membroId = membroId;
    }

    public LocalDate getDataIndisponivel() {
        return dataIndisponivel;
    }

    public void setDataIndisponivel(LocalDate dataIndisponivel) {
        this.dataIndisponivel = dataIndisponivel;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
