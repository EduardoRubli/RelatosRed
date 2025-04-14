package com.relatosred.RedSocial.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Tarjeta")
public class Tarjeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTarjeta;

    private String tipo;
    private String numero;
    private String titular;
    private int CVV;
    private LocalDate fechaCad;
    private boolean activa;
    private LocalDateTime fechaReg;

    @JsonIgnore
    @ManyToOne // Usuario asociado a la tarjeta.
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    // Getters y Setters.
    public Long getIdTarjeta() {
        return idTarjeta;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public int getCVV() {
        return CVV;
    }

    public void setCVV(int CVV) {
        this.CVV = CVV;
    }

    public LocalDate getFechaCad() {
        return fechaCad;
    }

    public void setFechaCad(LocalDate fechaCad) {
        this.fechaCad = fechaCad;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public LocalDateTime getFechaReg() {
        return fechaReg;
    }

    public void setFechaReg(LocalDateTime fechaReg) {
        this.fechaReg = fechaReg;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public boolean equals(Object obj) {
        // Verifica si el objeto es el mismo.
        if (this == obj) {
            return true;
        }

        // Verifica si el objeto es de la misma clase.
        if (obj == null || !(obj instanceof Tarjeta)) {
            return false;
        }

        // Realiza el casting seguro
        Tarjeta tarjeta = (Tarjeta) obj;

        // Verifica si el número es nulo.
        if (this.numero == null) {
            return tarjeta.numero == null;
        } else {
            return numero.equals(tarjeta.numero);
        }
    }

    @Override
    public int hashCode() {
        // Si el número es nulo devuelve 0.
        if (numero == null) {
            return 0;
        } else {
            return numero.hashCode();
        }
    }
}
