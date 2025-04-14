package com.relatosred.RedSocial.utilidades;

import java.util.regex.Pattern;

public class Validador {

    // Expresión regular para validar email
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // Expresión regular para validar nombres, apellidos y alias (solo letras y acentos permitidos)
    private static final String NOMBRE_REGEX = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";

    /**
     * Valida la estructura de un email.
     *
     * @param email El email a validar.
     * @throws IllegalArgumentException Si el email no tiene una estructura válida.
     */
    public static void validarEmail(String email) {
        if (email == null || !Pattern.matches(EMAIL_REGEX, email)) {
            throw new IllegalArgumentException("El correo electrónico no tiene una estructura válida.");
        }
    }

    /**
     * Valida que un nombre, apellido o alias contenga solo letras y espacios.
     *
     * @param texto El texto a validar (nombre, apellido o alias).
     * @throws IllegalArgumentException Si el texto contiene caracteres no permitidos.
     */
    public static void validarNombre(String texto) {
        if (texto == null || !Pattern.matches(NOMBRE_REGEX, texto)) {
            throw new IllegalArgumentException("El campo contiene caracteres no permitidos. Solo se permiten letras y espacios.");
        }
    }

    /**
     * Ejemplo de uso de la clase ValidadorCampos.
     */
    /*public static void main(String[] args) {
        try {
            // Validación de email
            validarEmail("usuario@ejemplo.com"); // Válido
            validarEmail("usuario@ejemplo"); // Inválido

            // Validación de nombres
            validarNombre("Juan Pérez"); // Válido
            validarNombre("Juan#Pérez"); // Inválido
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }*/
}
