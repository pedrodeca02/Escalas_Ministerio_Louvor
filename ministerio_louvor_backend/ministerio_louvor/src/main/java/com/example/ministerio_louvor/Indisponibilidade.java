package com.example.louvor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Indisponibilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer membroId;
    private LocalDate dataIndisponivel;
    private String motivo;

    public Indisponibilidade() {
    }

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
