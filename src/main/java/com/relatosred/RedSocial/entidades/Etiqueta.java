package com.relatosred.RedSocial.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "Etiqueta")
public class Etiqueta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEtiqueta;

    @Column(unique = true, nullable = false, length=30)
    private String nombre;

    // Contador para medir popularidad.
    @Column(nullable = false)
    private Integer popularidad;

    @Column(nullable = false)
    private Boolean permitida;

    @JsonIgnore
    @ManyToMany(mappedBy = "etiquetas")
    private Set<Texto> textos;

    // Constructor vacío.
    public Etiqueta() {}

    public Etiqueta(String nombre) {
        this.nombre = nombre;
        this.popularidad = 0;
        this.permitida = true;
    }

    // Getters y Setters.
    public Long getIdEtiqueta() {
        return idEtiqueta;
    }

    public void setIdEtiqueta(Long idEtiqueta) {
        this.idEtiqueta = idEtiqueta;
    }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getPopularidad() {
        return popularidad;
    }

    public void setPopularidad(Integer popularidad) {
        this.popularidad = popularidad;
    }

    public Boolean getPermitida() { return permitida; }

    public void setPermitida(Boolean permitida){ this.permitida = permitida; }

    public Set<Texto> getTextos() { return textos; }

    // Setea la lista completa de textos.
    public void setTextos(Set<Texto> textos) {
        this.textos = textos;
    }

    // Añade un texto a la lista.
    public void addTexto(Texto texto) {
        this.textos.add(texto);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        Etiqueta etiqueta = (Etiqueta) obj;
        // Si existe idEtiqueta comparamos por id.
        if (this.idEtiqueta != null && etiqueta.idEtiqueta != null) {
            return this.idEtiqueta.equals(etiqueta.idEtiqueta);
        }
        // Si no, comparamos etiqueta por nombre.
        if (nombre != null && etiqueta.nombre != null) {
            return this.nombre.equals(etiqueta.nombre);
        }
        return false;
    }

    @Override
    public int hashCode() {
        // Si tiene ID, usamos su hash.
        if (this.idEtiqueta != null) {
            return this.idEtiqueta.hashCode();
        }

        // Si no tiene ID usamos nombre.
        if (this.nombre != null) {
            return this.nombre.toLowerCase().hashCode();
        }
        return 0;
    }
}