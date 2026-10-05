package com.example.louvor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "membro_funcao")
public class MembroFuncao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer membroId;
    private Integer funcaoId;

    public MembroFuncao() {
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

    public Integer getFuncaoId() {
        return funcaoId;
    }

    public void setFuncaoId(Integer funcaoId) {
        this.funcaoId = funcaoId;
    }
}
