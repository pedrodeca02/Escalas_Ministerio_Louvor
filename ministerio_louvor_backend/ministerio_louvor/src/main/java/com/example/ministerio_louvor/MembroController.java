package com.example.louvor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/membros")
@CrossOrigin(origins = "*")
public class MembroController {

    private final MembroRepository membroRepository;
    private final FuncaoRepository funcaoRepository;
    private final MembroFuncaoRepository membroFuncaoRepository;
    private final IndisponibilidadeRepository indisponibilidadeRepository;
    private final EscalaMembroRepository escalaMembroRepository;

    public MembroController(MembroRepository membroRepository,
                            FuncaoRepository funcaoRepository,
                            MembroFuncaoRepository membroFuncaoRepository,
                            IndisponibilidadeRepository indisponibilidadeRepository,
                            EscalaMembroRepository escalaMembroRepository) {

        this.membroRepository = membroRepository;
        this.funcaoRepository = funcaoRepository;
        this.membroFuncaoRepository = membroFuncaoRepository;
        this.indisponibilidadeRepository = indisponibilidadeRepository;
        this.escalaMembroRepository = escalaMembroRepository;
    }

    @GetMapping
    public ResponseEntity<List<MembroResposta>> listar() {
        List<Membro> membros = membroRepository.findAll();
        List<MembroResposta> resposta = new ArrayList<>();

        for (Membro membro : membros) {
            resposta.add(montarResposta(membro));
        }

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembroResposta> buscarPorId(@PathVariable Integer id) {
        Optional<Membro> membroEncontrado = membroRepository.findById(id);

        if (membroEncontrado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(montarResposta(membroEncontrado.get()));
    }

    private MembroResposta montarResposta(Membro membro) {
        MembroResposta resposta = new MembroResposta();

        resposta.setId(membro.getId());
        resposta.setNome(membro.getNome());
        resposta.setMencao(membro.getMencao());

        List<MembroFuncao> vinculos = membroFuncaoRepository.findByMembroId(membro.getId());
        List<String> funcoes = new ArrayList<>();

        for (MembroFuncao vinculo : vinculos) {
            Optional<Funcao> funcao = funcaoRepository.findById(vinculo.getFuncaoId());

            if (funcao.isPresent()) {
                funcoes.add(funcao.get().getNome());
            }
        }

        resposta.setFuncoes(funcoes);

        long quantidadeIndisponibilidades =
                indisponibilidadeRepository.countByMembroId(membro.getId());

        resposta.setQuantidadeIndisponibilidades((int) quantidadeIndisponibilidades);

        List<EscalaMembro> participacoes =
                escalaMembroRepository.findByMembroId(membro.getId());

        Set<Integer> escalasDiferentes = new HashSet<>();

        for (EscalaMembro participacao : participacoes) {
            escalasDiferentes.add(participacao.getEscalaId());
        }

        resposta.setQuantidadeEscalas(escalasDiferentes.size());

        return resposta;
    }
}
