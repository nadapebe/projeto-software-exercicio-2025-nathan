package br.edu.insper.exercicio.cursos;

import jakarta.persistence.*;

@Entity
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    @Column(length = 2000)
    private String descricao;
    private Double nota;       // 0..5
    private String professor;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Double getNota() { return nota; }
    public void setNota(Double nota) { this.nota = nota; }

    public String getProfessor() { return professor; }
    public void setProfessor(String professor) { this.professor = professor; }
}
