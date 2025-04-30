package com.relatosred.RedSocial.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "Categoria",
        uniqueConstraints = { @UniqueConstraint(columnNames = {"categoria", "subcategoria"}) },
        indexes = { @Index(columnList = "categoria, subcategoria", unique = true) })
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoria;

    @Enumerated(EnumType.STRING)
    @Column(length = 16, nullable = false)
    private CategoriaEnum categoria;

    public enum CategoriaEnum {
        MISTERIO,
        THRILLER,
        DRAMA,
        FANTASIA,
        CIENCIAFIC,
        HISTORICO,
        AVENTURA,
        FANFIC,
        EROTICO,
        BIOGRAFIA;
    }

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private SubcategoriaEnum subcategoria;

    public enum SubcategoriaEnum {
        CRIMEN,
        POLICIACO,
        ROMANCE,
        PSICOLOGICO,
        CONSPIRACION,
        FUTURISTA,
        TERROR,
        PARANORMAL,
        JUVENIL,
        UTOPIA,
        DISTOPIA,
        BELICO;
    }

    // Constructor vacío.
    public Categoria() {}

    // Constructor para categoría.
    public Categoria(CategoriaEnum categoria) {
        this.categoria = categoria;
        this.subcategoria = null;
    }

    // Constructor para categoría y subcategoría.
    public Categoria(CategoriaEnum categoria, SubcategoriaEnum subcategoria) {
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

    public CategoriaEnum getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaEnum categoria) {
        this.categoria = categoria;
    }

    public SubcategoriaEnum getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(SubcategoriaEnum subcategoria) {
        this.subcategoria = subcategoria;
    }
}