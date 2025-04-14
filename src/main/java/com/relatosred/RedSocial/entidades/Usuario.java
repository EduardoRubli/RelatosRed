package com.relatosred.RedSocial.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    private String nombre;
    private String apellido;
    private String alias;
    private String email;
    private String password;
    private LocalDate fechaNac;
    private String sexo;
    private String ciudad;
    private String avatarURL;
    private LocalDateTime fechaReg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol;

    public enum RolUsuario {
        ADMIN, MODERADOR, USUARIO
    }

    // Borrado blando y bloqueo.
    private Boolean eliminado = false;
    private Boolean bloqueado = false;

    // Relación con textos creados.
    @JsonIgnore // @OneToMany usa "fetch = FetchType.LAZY" por defecto.
    @OneToMany(mappedBy = "autor", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Texto> textos = new HashSet<>();

    // Relación con notas asignadas.
    @OneToMany(mappedBy = "usuario", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private Set<NotaTexto> notasTexto = new HashSet<>();

    // Relación con mensajes enviados.
    @OneToMany(mappedBy = "emisor", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private Set<Mensaje> mensajesEnviados = new HashSet<>();

    // Relación con mensajes recibidos.
    @OneToMany(mappedBy = "receptor", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private Set<Mensaje> mensajesRecibidos = new HashSet<>();

    // Relación con tarjetas.
    @OneToMany(mappedBy = "usuario", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private Set<Tarjeta> tarjetas = new HashSet<>();

    @JsonIgnore // Relación con imágenes (por ejemplo, avatares o portadas)
    @OneToMany(mappedBy = "usuario", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Imagen> imagenes = new HashSet<>();

    @JsonIgnore // Solicitudes de contacto enviadas.
    @OneToMany(mappedBy = "usuario1", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Contacto> contactosEnviados = new HashSet<>();

    @JsonIgnore // Solicitudes de contacto recibidas.
    @OneToMany(mappedBy = "usuario2", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Contacto> contactosRecibidos = new HashSet<>();

    // Favoritos.
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "Favorito",
            joinColumns = @JoinColumn(name = "idUsuario"),
            inverseJoinColumns = @JoinColumn(name = "idFavorito"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"idUsuario", "idFavorito"})
    )
    private Set<Usuario> favoritos = new HashSet<>();

    // Constructor vacío.
    public Usuario() {}

    // Constructor básico.
    public Usuario(String nombre, String apellido, String alias,
                   String email, String password, LocalDate fechaNac) {
        this.nombre = nombre;
        this.apellido = apellido;
        // El alias por defecto es nombre + apellido.
        if (alias == null || alias.isBlank()) {
            this.alias = nombre + " " + apellido;
        } else {
            this.alias = alias;
        }
        this.email = email;
        this.password = password;
        this.fechaNac = fechaNac;
        this.rol = RolUsuario.USUARIO;
        this.eliminado = false;
        this.bloqueado = false;
    }

    // Getters y Setters.
    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(LocalDate fechaNac) {
        this.fechaNac = fechaNac;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public LocalDateTime getFechaReg() {
        return fechaReg;
    }

    public void setFechaReg(LocalDateTime fechaReg) {
        this.fechaReg = fechaReg;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    public Set<Texto> getTextos() {
        return textos;
    }

    public void setTextos(Set<Texto> textos) {
        this.textos = textos;
    }

    public Set<NotaTexto> getNotasTexto() {
        return notasTexto;
    }

    public void setNotasTexto(Set<NotaTexto> notasTexto) {
        this.notasTexto = notasTexto;
    }

    public Set<Mensaje> getMensajesEnviados() {
        return mensajesEnviados;
    }

    public void setMensajesEnviados(Set<Mensaje> mensajesEnviados) {
        this.mensajesEnviados = mensajesEnviados;
    }

    public Set<Mensaje> getMensajesRecibidos() {
        return mensajesRecibidos;
    }

    public void setMensajesRecibidos(Set<Mensaje> mensajesRecibidos) {
        this.mensajesRecibidos = mensajesRecibidos;
    }

    public Set<Tarjeta> getTarjetas() {
        return tarjetas;
    }

    public void setTarjetas(Set<Tarjeta> tarjetas) {
        this.tarjetas = tarjetas;
    }

    public Set<Imagen> getImagenes() {
        return imagenes;
    }

    public void setImagenes(Set<Imagen> imagenes) {
        this.imagenes = imagenes;
    }

    public Set<Contacto> getContactosEnviados() {
        return contactosEnviados;
    }

    public void setContactosEnviados(Set<Contacto> contactosEnviados) {
        this.contactosEnviados = contactosEnviados;
    }

    public Set<Contacto> getContactosRecibidos() {
        return contactosRecibidos;
    }

    public void setContactosRecibidos(Set<Contacto> contactosRecibidos) {
        this.contactosRecibidos = contactosRecibidos;
    }

    public Set<Usuario> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(Set<Usuario> favoritos) {
        this.favoritos = favoritos;
    }

    // Obtener estado de eliminado.
    public boolean getEliminado(){
        return eliminado;
    }

    // Setear eliminado blando.
    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    // Obtener estado de bloqueo.
    public boolean getBloqueado(){
        return bloqueado;
    }

    // Setear bloqueo de usuario.
    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }
}