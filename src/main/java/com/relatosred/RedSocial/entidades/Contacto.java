package com.relatosred.RedSocial.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Contacto", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"idUsuario1", "idUsuario2"})
})
public class Contacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idContacto;

    @ManyToOne
    @JoinColumn(name = "idUsuario1", nullable = false)
    private Usuario usuario1;

    @ManyToOne
    @JoinColumn(name = "idUsuario2", nullable = false)
    private Usuario usuario2;

    @Column(nullable = false)
    private String estado; // "pendiente", "aceptado", "bloqueado"

    private LocalDateTime fechaContacto;

    // Getters y Setters.
    public Long getIdContacto() {
        return idContacto;
    }

    public void setIdContacto(Long idContacto) {
        this.idContacto = idContacto;
    }

    public Usuario getUsuario1() {
        return usuario1;
    }

    public void setUsuario1(Usuario usuario1) {
        this.usuario1 = usuario1;
    }

    public Usuario getUsuario2() {
        return usuario2;
    }

    public void setUsuario2(Usuario usuario2) {
        this.usuario2 = usuario2;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaContacto() {
        return fechaContacto;
    }

    public void setFechaContacto(LocalDateTime fechaContacto) {
        this.fechaContacto = fechaContacto;
    }
}