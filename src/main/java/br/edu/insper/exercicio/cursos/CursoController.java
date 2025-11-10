package br.edu.insper.exercicio.cursos;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cursos")
@CrossOrigin
public class CursoController {
    private final CursoService service;
    public CursoController(CursoService service) { this.service = service; }

    @GetMapping
    public List<Curso> listar() { return service.listar(); }

    @PostMapping
    public ResponseEntity<Curso> criar(@RequestBody Curso c) {
        return ResponseEntity.status(201).body(service.criar(c));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_admin') or hasAuthority('delete:cursos')")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
