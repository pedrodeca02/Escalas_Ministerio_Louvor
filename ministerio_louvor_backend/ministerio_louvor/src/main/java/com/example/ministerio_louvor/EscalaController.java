package com.example.louvor;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/escalas")
@CrossOrigin(origins = "*")
public class EscalaController {

    private final EscalaRepository escalaRepository;
    private final EscalaMembroRepository escalaMembroRepository;
    private final MembroRepository membroRepository;
    private final FuncaoRepository funcaoRepository;
    private final MembroFuncaoRepository membroFuncaoRepository;
    private final IndisponibilidadeRepository indisponibilidadeRepository;

    public EscalaController(
            EscalaRepository escalaRepository,
            EscalaMembroRepository escalaMembroRepository,
            MembroRepository membroRepository,
            FuncaoRepository funcaoRepository,
            MembroFuncaoRepository membroFuncaoRepository,
            IndisponibilidadeRepository indisponibilidadeRepository) {

        this.escalaRepository = escalaRepository;
        this.escalaMembroRepository = escalaMembroRepository;
        this.membroRepository = membroRepository;
        this.funcaoRepository = funcaoRepository;
        this.membroFuncaoRepository = membroFuncaoRepository;
        this.indisponibilidadeRepository = indisponibilidadeRepository;
    }

    @GetMapping
    public ResponseEntity<List<EscalaResposta>> listar() {
        List<Escala> escalas = escalaRepository.findAll();
        List<EscalaResposta> resposta = new ArrayList<>();

        for (Escala escala : escalas) {
            resposta.add(montarResposta(escala));
        }

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{data}")
    public ResponseEntity<EscalaResposta> buscarPorData(
            @PathVariable LocalDate data) {

        Escala escala = escalaRepository.findByDataEscala(data);

        if (escala == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(montarResposta(escala));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<EscalaResposta> salvar(
            @RequestBody EscalaRequest request) {

        if (request.getDataEscala() == null) {
            return ResponseEntity.badRequest().build();
        }

        if (request.getItens() != null) {
            for (EscalaItemRequest item : request.getItens()) {

                if (item.getMembroId() == null ||
                        item.getFuncao() == null ||
                        item.getFuncao().isBlank()) {

                    return ResponseEntity.badRequest().build();
                }

                if (!membroRepository.existsById(item.getMembroId())) {
                    return ResponseEntity.notFound().build();
                }

                boolean indisponivel =
                        indisponibilidadeRepository
                                .existsByMembroIdAndDataIndisponivel(
                                        item.getMembroId(),
                                        request.getDataEscala()
                                );

                if (indisponivel) {
                    return ResponseEntity.status(409).build();
                }

                Funcao funcao =
                        funcaoRepository.findByNome(item.getFuncao());

                if (funcao == null) {
                    return ResponseEntity.badRequest().build();
                }

                boolean podeFazer =
                        membroFuncaoRepository
                                .existsByMembroIdAndFuncaoId(
                                        item.getMembroId(),
                                        funcao.getId()
                                );

                if (!podeFazer) {
                    return ResponseEntity.status(409).build();
                }
            }
        }

        Escala escala = escalaRepository.findByDataEscala(
                request.getDataEscala()
        );

        if (escala == null) {
            escala = new Escala();
            escala.setDataEscala(request.getDataEscala());
            escala = escalaRepository.save(escala);
        } else {
            escalaMembroRepository.deleteByEscalaId(
                    escala.getId()
            );
        }

        if (request.getItens() != null) {
            for (EscalaItemRequest item : request.getItens()) {

                Funcao funcao =
                        funcaoRepository.findByNome(item.getFuncao());

                EscalaMembro escalaMembro = new EscalaMembro();
                escalaMembro.setEscalaId(escala.getId());
                escalaMembro.setMembroId(item.getMembroId());
                escalaMembro.setFuncaoId(funcao.getId());

                escalaMembroRepository.save(escalaMembro);
            }
        }

        return ResponseEntity.ok(montarResposta(escala));
    }

    @DeleteMapping("/{data}")
    @Transactional
    public ResponseEntity<Void> excluir(
            @PathVariable LocalDate data) {

        Escala escala = escalaRepository.findByDataEscala(data);

        if (escala == null) {
            return ResponseEntity.notFound().build();
        }

        escalaMembroRepository.deleteByEscalaId(escala.getId());
        escalaRepository.deleteById(escala.getId());

        return ResponseEntity.noContent().build();
    }

    private EscalaResposta montarResposta(Escala escala) {
        EscalaResposta resposta = new EscalaResposta();

        resposta.setId(escala.getId());
        resposta.setDataEscala(escala.getDataEscala());

        List<EscalaMembro> participacoes =
                escalaMembroRepository.findByEscalaId(
                        escala.getId()
                );

        List<EscalaItemResposta> itens = new ArrayList<>();

        for (EscalaMembro participacao : participacoes) {
            EscalaItemResposta item = new EscalaItemResposta();

            item.setMembroId(participacao.getMembroId());

            Optional<Membro> membro =
                    membroRepository.findById(
                            participacao.getMembroId()
                    );

            if (membro.isPresent()) {
                item.setMembroNome(membro.get().getNome());
                item.setMencao(membro.get().getMencao());
            }

            Optional<Funcao> funcao =
                    funcaoRepository.findById(
                            participacao.getFuncaoId()
                    );

            if (funcao.isPresent()) {
                item.setFuncao(funcao.get().getNome());
            }

            itens.add(item);
        }

        resposta.setItens(itens);

        return resposta;
    }
}
