package com.relatosred.RedSocial.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "Mensaje")
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMensaje;

    @JsonIgnore
    @ManyToOne // Usuario que envía el mensaje.
    @JoinColumn(name = "idEmisor", nullable = false)
    private Usuario emisor;

    @JsonIgnore
    @ManyToOne // El receptor, solo para mensajes privados.
    @JoinColumn(name = "idReceptor")
    private Usuario receptor;

    @JsonIgnore
    @ManyToOne // Relación entre mensajes y texto.
    @JoinColumn(name = "idTexto")
    private Texto texto;

    @JsonIgnore
    @ManyToOne // Mensaje al que responde.
    @JoinColumn(name = "idPadre")
    private Mensaje padre;

    @JsonIgnore // Respuestas asociadas a mensaje.
    @OneToMany(mappedBy = "padre", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaEnvio ASC")
    private Set<Mensaje> respuestas = new HashSet<>();

    // Contenido del mensaje.
    @Lob
    private String contenido;

    // Fecha y hora en que se envía el mensaje.
    private LocalDateTime fechaEnvio;

    // Tipos: "comentario", "resComentario", "privado", "resPrivado".
    private String tipo;

    // Título para el comentario raíz.
    private String titulo;

    // Getters y Setters.

    public Long getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(Long idMensaje) {
        this.idMensaje = idMensaje;
    }

    public Usuario getEmisor() {
        return emisor;
    }

    public void setEmisor(Usuario emisor) {
        this.emisor = emisor;
    }

    public Usuario getReceptor() {
        return receptor;
    }

    public void setReceptor(Usuario receptor) {
        this.receptor = receptor;
    }

    public Texto getTexto() {
        return texto;
    }

    public void setTexto(Texto texto) {
        this.texto = texto;
    }

    public Mensaje getPadre() {
        return padre;
    }

    public void setPadre(Mensaje padre) {
        this.padre = padre;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}