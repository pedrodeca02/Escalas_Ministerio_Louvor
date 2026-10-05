package com.example.louvor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IndisponibilidadeRepository extends JpaRepository<Indisponibilidade, Integer> {

    List<Indisponibilidade> findByDataIndisponivel(LocalDate dataIndisponivel);

    boolean existsByMembroIdAndDataIndisponivel(Integer membroId, LocalDate dataIndisponivel);

    long countByMembroId(Integer membroId);
}
