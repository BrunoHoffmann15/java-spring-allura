package br.com.alura.forum.controller;

import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.controller.form.AtualizacaoTopicoForm;
import br.com.alura.forum.controller.form.TopicoForm;
import br.com.alura.forum.modelo.Curso;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/topicos")
public class TopicosController {
    @Autowired
    private TopicoRepository topicoRepository;
    @Autowired
    private CursoRepository cursoRepository;

    @GetMapping
    public List<TopicoDto> lista(String nomeCurso) {
        List<Topico> topicos = new ArrayList<>();

        if (nomeCurso == null)
            topicos = topicoRepository.findAll();
        else
            topicos = topicoRepository.findByCursoNome(nomeCurso);

        return TopicoDto.converter(topicos);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm form, UriComponentsBuilder uriComponentsBuilder) {
        Curso curso = cursoRepository.findByNome(form.getNomeCurso());
        Topico topico = form.converter(curso);

        topicoRepository.save(topico);

        URI uri = uriComponentsBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(uri).body(new TopicoDto(topico));
    }

    @GetMapping("/{id}")
    public TopicoDto detalhar(@PathVariable Long id) {
        return new TopicoDto(topicoRepository.getReferenceById(id));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicoForm form) {
        Optional<Topico> optionalTopico = topicoRepository.findById(id);

        if (!optionalTopico.isPresent())
            return ResponseEntity.notFound().build();

        Topico topico = optionalTopico.get();

        topico.setMensagem(form.getMensagem());
        topico.setTitulo(form.getTitulo());

        return ResponseEntity.ok().body(new TopicoDto(topico));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> remover(@PathVariable Long id) {
        topicoRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
