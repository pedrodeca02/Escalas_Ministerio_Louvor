package com.example.louvor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/indisponibilidades")
@CrossOrigin(origins = "*")
public class IndisponibilidadeController {

    private final IndisponibilidadeRepository indisponibilidadeRepository;
    private final MembroRepository membroRepository;

    public IndisponibilidadeController(
            IndisponibilidadeRepository indisponibilidadeRepository,
            MembroRepository membroRepository) {

        this.indisponibilidadeRepository = indisponibilidadeRepository;
        this.membroRepository = membroRepository;
    }

    @GetMapping
    public ResponseEntity<List<IndisponibilidadeResposta>> listar(
            @RequestParam(required = false) LocalDate data) {

        List<Indisponibilidade> indisponibilidades;

        if (data == null) {
            indisponibilidades = indisponibilidadeRepository.findAll();
        } else {
            indisponibilidades =
                    indisponibilidadeRepository.findByDataIndisponivel(data);
        }

        List<IndisponibilidadeResposta> resposta = new ArrayList<>();

        for (Indisponibilidade indisponibilidade : indisponibilidades) {
            resposta.add(montarResposta(indisponibilidade));
        }

        return ResponseEntity.ok(resposta);
    }

    @PostMapping
    public ResponseEntity<IndisponibilidadeResposta> cadastrar(
            @RequestBody IndisponibilidadeRequest request) {

        if (request.getMembroId() == null ||
                request.getDataIndisponivel() == null) {

            return ResponseEntity.badRequest().build();
        }

        if (!membroRepository.existsById(request.getMembroId())) {
            return ResponseEntity.notFound().build();
        }

        boolean jaExiste =
                indisponibilidadeRepository
                        .existsByMembroIdAndDataIndisponivel(
                                request.getMembroId(),
                                request.getDataIndisponivel()
                        );

        if (jaExiste) {
            return ResponseEntity.status(409).build();
        }

        Indisponibilidade indisponibilidade = new Indisponibilidade();
        indisponibilidade.setMembroId(request.getMembroId());
        indisponibilidade.setDataIndisponivel(request.getDataIndisponivel());
        indisponibilidade.setMotivo(request.getMotivo());

        Indisponibilidade salva =
                indisponibilidadeRepository.save(indisponibilidade);

        return ResponseEntity.status(201).body(montarResposta(salva));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        if (!indisponibilidadeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        indisponibilidadeRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private IndisponibilidadeResposta montarResposta(
            Indisponibilidade indisponibilidade) {

        IndisponibilidadeResposta resposta =
                new IndisponibilidadeResposta();

        resposta.setId(indisponibilidade.getId());
        resposta.setMembroId(indisponibilidade.getMembroId());
        resposta.setDataIndisponivel(
                indisponibilidade.getDataIndisponivel()
        );
        resposta.setMotivo(indisponibilidade.getMotivo());

        Optional<Membro> membro =
                membroRepository.findById(indisponibilidade.getMembroId());

        if (membro.isPresent()) {
            resposta.setMembroNome(membro.get().getNome());
        }

        return resposta;
    }
}
