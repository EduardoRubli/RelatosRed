package com.relatosred.RedSocial.DTO;

import com.relatosred.RedSocial.entidades.Contacto;
import com.relatosred.RedSocial.entidades.Usuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UsuarioMeDTO {
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String alias;
    private String email;
    private String sexo;
    private LocalDate fechaNac;
    private String avatarURL;
    private String rol;

    // Constructor
    public UsuarioMeDTO(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.alias = usuario.getAlias();
        this.email = usuario.getEmail();
        this.sexo = usuario.getSexo();
        this.fechaNac = usuario.getFechaNac();
        this.avatarURL = usuario.getAvatarURL();
        this.rol = usuario.getRol().name();
    }

    // Getters y setters.
    // ID del usuario actual (IMPORTANTE).
    public Long getidUsuario() { return idUsuario; }
    public void setidUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    // Nombre del usuario actual.
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    // Apellido del usuario actual.
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    // Alias del usuario actual.
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    // Email del usuario actual.
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // Sexo del usuario actual.
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    // Fecha de nacimiento del usuario actual.
    public LocalDate getFechaNac() { return fechaNac; }
    public void setFechaNac(LocalDate fechaNac) {
        this.fechaNac = fechaNac;
    }

    // Ruta del avatar del usuario actual.
    public String getAvatarURL() { return avatarURL; }
    public void setAvatarURL(String avatarURL) { this.avatarURL = avatarURL; }

    // Rol del usuario actual.
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}