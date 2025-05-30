package com.relatosred.RedSocial.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Contacto", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"idSeguidor", "idSeguido"})
})
public class Contacto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idContacto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idSeguidor", nullable = false)
    private Usuario seguidor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idSeguido", nullable = false)
    private Usuario seguido;

    @Column(nullable = false)
    private boolean bloqueado = false;
    // Se debe inicializar en el servicio.
    private LocalDateTime fechaContacto;

    // Constructor vacío.
    public Contacto() {}

    // Getters y Setters.
    public Long getIdContacto() {
        return idContacto;
    }

    public void setIdContacto(Long idContacto) {
        this.idContacto = idContacto;
    }

    public Usuario getSeguidor() { return seguidor; }

    public void setSeguidor(Usuario seguidor) {
        this.seguidor= seguidor;
    }

    public Usuario getSeguido() { return seguido; }

    public void setSeguido(Usuario seguido) {
        this.seguido = seguido;
    }

    public boolean getBloqueado() { return bloqueado; }

    public void setBloqueado(boolean bloqueado) { this.bloqueado = bloqueado; }

    public LocalDateTime getFechaContacto() {
        return fechaContacto;
    }

    public void setFechaContacto(LocalDateTime fechaContacto) {
        this.fechaContacto = fechaContacto;
    }
}