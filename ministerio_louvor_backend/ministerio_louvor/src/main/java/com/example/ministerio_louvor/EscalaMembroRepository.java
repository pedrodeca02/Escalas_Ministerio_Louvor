package com.example.louvor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EscalaMembroRepository extends JpaRepository<EscalaMembro, Integer> {

    List<EscalaMembro> findByEscalaId(Integer escalaId);

    List<EscalaMembro> findByMembroId(Integer membroId);

    void deleteByEscalaId(Integer escalaId);
}
