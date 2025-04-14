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
    @Transient // No se persiste.
    private Double notaUsuario;
    // Borrado blando de texto.
    private Boolean eliminado = false;

    @ManyToOne
    @JoinColumn(name = "idCategoria")
    private Categoria categoria;

    // Imagen de portada.
    @ManyToOne
    @JoinColumn(name = "idImagen")
    private Imagen portada;

    @JsonIgnore // Relación entre texto y nota asignada.
    @OneToMany(mappedBy = "texto", cascade = CascadeType.ALL)
    private Set<NotaTexto> notasTexto = new HashSet<>();

    @JsonIgnore // Relación entre texto y mensajes asociados.
    @OneToMany(mappedBy = "texto", cascade = CascadeType.ALL)
    private Set<Mensaje> mensajes = new HashSet<>();

    @JsonIgnore // Un texto puede tener muchas imágenes insertadas.
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

    public Double getNotaMedia() {
        double suma = 0.00;
        if (notaMedia == null && !notasTexto.isEmpty()) {
            // Opcional: lógica para calcular el promedio dinámicamente si notaMedia es nulo
            for (NotaTexto nota : notasTexto) {
                suma += nota.getNota();
            }
            notaMedia = suma / notasTexto.size();
        }
        return notaMedia;
    }

    public void setNotaMedia(Double notaMedia) {
        this.notaMedia = notaMedia;
    }
    
    public void setNotaUsuario(Double notaUsuario) {
        this.notaUsuario = notaUsuario;
    }
}