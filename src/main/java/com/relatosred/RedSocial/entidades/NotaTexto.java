package com.relatosred.RedSocial.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "NotaTexto", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"idUsuario", "idTexto"})
})
public class NotaTexto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotaTexto;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "idTexto", nullable = false)
    private Texto texto;

    private Double nota;

    // Constructor vacío
    public NotaTexto() {}

    public NotaTexto(Usuario usuario, Texto texto, Double nota) {
        this.usuario = usuario;
        this.texto = texto;
        this.nota = nota;
    }

    // Getters y Setters.
    public Long getIdNotaTexto() {
        return idNotaTexto;
    }

    public void setIdNotaTexto(Long idNotaTexto) {
        this.idNotaTexto = idNotaTexto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Texto getTexto() {
        return texto;
    }

    public void setTexto(Texto texto) {
        this.texto = texto;
    }

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }
}
