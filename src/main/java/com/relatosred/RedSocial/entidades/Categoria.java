package com.relatosred.RedSocial.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoria;

    private String categoria; // Nombre de la categoría principal.
    private String subcategoria; // Nombre de la subcategoría.

    @JsonIgnore
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Texto> textos = new HashSet<>();

    // Constructor vacío.
    public Categoria() {}

    // Constructor para categoría.
    public Categoria(String categoria) {
        this.categoria = categoria;
        this.subcategoria = null;
    }

    // Constructor para categoría y subcategoría.
    public Categoria(String categoria, String subcategoria) {
        this.categoria = categoria;
        this.subcategoria = subcategoria;
    }

    // Getters y Setters.
    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(String subcategoria) {
        this.subcategoria = subcategoria;
    }
}