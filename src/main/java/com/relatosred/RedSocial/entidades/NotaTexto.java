package com.relatosred.RedSocial.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "NotaTexto", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"idUsuario", "idTexto"})
})
public class NotaTexto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTextoNota;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "idTexto", nullable = false)
    private Texto texto;

    private double nota;

    // Constructor vacío
    public NotaTexto() {}

    public NotaTexto(Usuario usuario, Texto texto, double nota) {
        this.usuario = usuario;
        this.texto = texto;
        this.nota = nota;
    }

    // Getters y Setters.
    public Long getIdTextoNota() {
        return idTextoNota;
    }

    public void setIdTextoNota(Long idTextoNota) {
        this.idTextoNota = idTextoNota;
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
