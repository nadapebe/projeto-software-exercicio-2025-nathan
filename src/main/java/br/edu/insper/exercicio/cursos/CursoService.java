package br.edu.insper.exercicio.cursos;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CursoService {
    private final CursoRepository repo;
    public CursoService(CursoRepository repo) { this.repo = repo; }

    public List<Curso> listar() { return repo.findAll(); }

    public Curso criar(Curso c) {
        if (c.getNota() != null && (c.getNota() < 0 || c.getNota() > 5))
            throw new IllegalArgumentException("Nota deve estar entre 0 e 5");
        return repo.save(c);
    }

    public void excluir(Integer id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Curso não encontrado");
        repo.deleteById(id);
    }
}
