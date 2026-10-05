package com.example.louvor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface EscalaRepository extends JpaRepository<Escala, Integer> {

    Escala findByDataEscala(LocalDate dataEscala);
}
