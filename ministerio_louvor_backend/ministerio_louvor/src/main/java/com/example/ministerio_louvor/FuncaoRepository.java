package com.example.louvor;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncaoRepository extends JpaRepository<Funcao, Integer> {

    Funcao findByNome(String nome);
}
