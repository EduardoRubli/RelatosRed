package com.relatosred.RedSocial.utilidades;

import com.relatosred.RedSocial.entidades.Etiqueta;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.regex.Pattern;

public class Validador {
    // En Java los enum son estáticos.
    public enum NombresReservados {
        MISTERIO, THRILLER, DRAMA, FANTASIA, CIENCIAFIC, HISTORICO, AVENTURA, FANFIC,
        EROTICO, BIOGRAFIA, CRIMEN, POLICIACO, ROMANCE, PSICOLOGICO, CONSPIRACION, FUTURISTA,
        TERROR, PARANORMAL, JUVENIL, UTOPIA, DISTOPIA, BELICO;
    }

    public Boolean validarEtiqueta(String nombreEtiqueta) {
        if (nombreEtiqueta == null || nombreEtiqueta.length() < 2 || nombreEtiqueta.charAt(0) != '#') {
            return false;
        }
        // Obtiene la subcadena después de la almoadilla.
        String nombreSinHashTag = nombreEtiqueta.substring(1).toUpperCase();

        for (NombresReservados nombre : NombresReservados.values()) {
            if(nombre.name().equals(nombreSinHashTag)) {
                return false;
            }
        }
        return true;
    }

    // Expresión regular para validar emails.
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // Expresión regular para validar nombres, apellidos y alias.
    private static final String NOMBRE_REGEX = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";

    // Validación de emails usando REGEX.
    public static void validarEmail(String email) {
        if (email == null || !Pattern.matches(EMAIL_REGEX, email)) {
            throw new IllegalArgumentException("El email introducido no es válido.");
        }
    }

    // Validación de nombres usando REGEX.
    public static void validarNombre(String texto) {
        if (texto == null || !Pattern.matches(NOMBRE_REGEX, texto)) {
            throw new IllegalArgumentException("El campo contiene caracteres no permitidos. Solo se permiten letras y espacios.");
        }
    }
}
