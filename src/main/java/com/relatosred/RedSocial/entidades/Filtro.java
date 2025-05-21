package com.relatosred.RedSocial.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "Filtro")
public class Filtro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFiltro;

    @Column(name="palabra", unique = true, nullable = false)
    private String palabra;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoFiltro tipo;

    public enum TipoFiltro {
        ETIQUETA,
        MENSAJE
    }

    private Boolean activo;

    // Cronstructor vacío.
    public Filtro() {}

    public Filtro(String palabra, TipoFiltro tipo) {
        this.palabra = palabra;
        this.tipo = tipo;
        this.activo = true;
    }

    // Getters y setters.
    public Long getIdFiltro() {
        return idFiltro;
    }

    public String getPalabra() {
        return palabra;
    }

    public void setPalabra(String palabra) {
        this.palabra = palabra;
    }

    public TipoFiltro getTipo() {
        return tipo;
    }

    public void setTipo(TipoFiltro tipo) {
        this.tipo = tipo;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
