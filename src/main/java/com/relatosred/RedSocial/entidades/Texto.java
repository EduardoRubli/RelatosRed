package com.relatosred.RedSocial.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Texto")
public class Texto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTexto;

    @ManyToOne
    @JoinColumn(name = "idAutor", nullable = false)
    private Usuario autor;

    private String titulo;

    @Lob // En MySQL se mapea a TEXT.
    private String contenido;

    private String hashSHA256;

    private LocalDateTime fechaPublicacion;

    @Enumerated(EnumType.STRING)
    // Conviene especificar múltiplos de 4.
    @Column(length = 12, nullable = false)
    private EstadoTexto estado;
    // Estructura enum para estado.
    public enum EstadoTexto {
        BORRADOR, PUBLICADO, OCULTO
    }

    private String idioma;
    private String sinopsis;
    private Double notaMedia;
    // Borrado blando de texto.
    private Boolean eliminado = false;

    @ManyToOne // Categorías.
    @JoinColumn(name = "idCategoria", nullable = false)
    private Categoria categoria;

    @ManyToOne // Imagen de portada.
    @JoinColumn(name = "idImagen")
    private Imagen portada;

    @JsonIgnore // Relación entre texto y notas asignadas.
    @OneToMany(mappedBy = "texto", cascade = CascadeType.ALL)
    private Set<NotaTexto> notasTexto = new HashSet<>();

    @JsonIgnore // Relación entre texto y mensajes asociados.
    @OneToMany(mappedBy = "texto", cascade = CascadeType.ALL)
    private Set<Mensaje> mensajes = new HashSet<>();

    @JsonIgnore // Un texto puede tener imágenes insertadas.
    @OneToMany(mappedBy = "texto", cascade = CascadeType.ALL)
    private Set<Imagen> imagenesInsertadas = new HashSet<>();

    // Etiquetas.
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "TextoEtiqueta",
            joinColumns = @JoinColumn(name = "idTexto"),
            inverseJoinColumns = @JoinColumn(name = "idEtiqueta")
    )
    private Set<Etiqueta> etiquetas = new HashSet<>();


    // Constructor vacío.
    public Texto() {}

    public Texto(Usuario autor, String titulo, String contenido, EstadoTexto estado) {
        this.autor = autor;
        this.titulo = titulo;
        this.contenido = contenido;
        this.fechaPublicacion = LocalDateTime.now();
        this.estado = estado;
        // No visible si está sin publicar o moderado.
        if (estado == EstadoTexto.BORRADOR ||
        estado == EstadoTexto.OCULTO) this.eliminado = true;
    }

    // Getters y Setters.
    public Long getIdTexto() {
        return idTexto;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getHashSHA256() {
        return hashSHA256;
    }
    public void setHashSHA256(String hashSHA256) {
        this.hashSHA256 = hashSHA256;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public EstadoTexto getEstado() {
        return estado;
    }

    public void setEstado(EstadoTexto estado) {
        this.estado = estado;
        // Al actualizar el estado se actualiza eliminado.
        if (estado == EstadoTexto.BORRADOR || estado == EstadoTexto.OCULTO) {
            this.eliminado = true;
        }
    }

    // Obtiene el estado oculto.
    public boolean getEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean valor){
        this.eliminado = valor;
    }

    // Obtiene la categoría (categoría + subcategoría).
    public Categoria getCategoria() {
        return categoria;
    }

    // Setea la categoría de un texto desde el controlador.
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public Double getNotaMedia() { return notaMedia; }

    public void setNotaMedia(Double notaMedia) {
        this.notaMedia = notaMedia;
    }

    public Set<NotaTexto> getNotasTexto() {
        return notasTexto;
    }

    public void setNotasTexto(Set<NotaTexto> notasTexto) {
        this.notasTexto = notasTexto;
    }

}