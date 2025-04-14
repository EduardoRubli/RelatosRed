package com.relatosred.RedSocial.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Imagen")
public class Imagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idImagen;

    private String ruta;
    private String descripcion;
    private LocalDateTime fechaSubida;
    // Tipo de imagen (avatar, portada, cabecera...).
    private String tipo;
    // Hash de la imagen para evitar duplicados.
    @Column(unique = true, nullable = false)
    private String hashMD5;

    @JsonIgnore
    @ManyToOne // Imagen asociada a un usuario.
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    @JsonIgnore
    @ManyToOne // Imagen asociada a un texto.
    @JoinColumn(name = "idTexto")
    private Texto texto;

    // Getters y Setters.
    public Long getIdImagen() {
        return idImagen;
    }

    public void setIdImagen(Long idImagen) {
        this.idImagen = idImagen;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    public String gethashMD5(){
        return hashMD5;
    }

    public void setHashMD5(String hashMD5){
        this.hashMD5 = hashMD5;
    }
}