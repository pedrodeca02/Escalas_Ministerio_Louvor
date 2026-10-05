package com.example.louvor;

import java.util.List;

public class MembroResposta {

    private Integer id;
    private String nome;
    private String mencao;
    private List<String> funcoes;
    private Integer quantidadeIndisponibilidades;
    private Integer quantidadeEscalas;

    public MembroResposta() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMencao() {
        return mencao;
    }

    public void setMencao(String mencao) {
        this.mencao = mencao;
    }

    public List<String> getFuncoes() {
        return funcoes;
    }

    public void setFuncoes(List<String> funcoes) {
        this.funcoes = funcoes;
    }

    public Integer getQuantidadeIndisponibilidades() {
        return quantidadeIndisponibilidades;
    }

    public void setQuantidadeIndisponibilidades(Integer quantidadeIndisponibilidades) {
        this.quantidadeIndisponibilidades = quantidadeIndisponibilidades;
    }

    public Integer getQuantidadeEscalas() {
        return quantidadeEscalas;
    }

    public void setQuantidadeEscalas(Integer quantidadeEscalas) {
        this.quantidadeEscalas = quantidadeEscalas;
    }
}
