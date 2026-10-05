package com.example.louvor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembroFuncaoRepository extends JpaRepository<MembroFuncao, Integer> {

    List<MembroFuncao> findByMembroId(Integer membroId);

    boolean existsByMembroIdAndFuncaoId(Integer membroId, Integer funcaoId);
}
