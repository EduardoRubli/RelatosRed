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

    @Column(unique = true, nullable = false)
    private String nombre;  // Ejemplo: "#misterio", "#terror"

    private String color;   // Ejemplo: "#FF5733"

    @JsonIgnore
    @ManyToMany(mappedBy = "etiquetas")
    private Set<Texto> textos;

    // Getters y Setters.

    public Long getIdEtiqueta() {
        return idEtiqueta;
    }

    public void setIdEtiqueta(Long idEtiqueta) {
        this.idEtiqueta = idEtiqueta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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
        if (nombre != null && nombre.equalsIgnoreCase(etiqueta.nombre)){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (nombre == null) {
            return 0;
        }
        return nombre.toLowerCase().hashCode();
    }
}