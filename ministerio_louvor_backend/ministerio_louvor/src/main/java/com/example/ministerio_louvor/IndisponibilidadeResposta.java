package com.example.louvor;

import java.time.LocalDate;

public class IndisponibilidadeResposta {

    private Integer id;
    private Integer membroId;
    private String membroNome;
    private LocalDate dataIndisponivel;
    private String motivo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMembroId() {
        return membroId;
    }

    public void setMembroId(Integer membroId) {
        this.membroId = membroId;
    }

    public String getMembroNome() {
        return membroNome;
    }

    public void setMembroNome(String membroNome) {
        this.membroNome = membroNome;
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
