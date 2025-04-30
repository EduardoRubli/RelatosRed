package com.relatosred.RedSocial.utilidades;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
    public String calcularMD5(File archivo) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        FileInputStream fis = new FileInputStream(archivo);

        byte[] buffer = new byte[1024];
        int bytesLeidos;
        while ((bytesLeidos = fis.read(buffer)) != -1) {
            md.update(buffer, 0, bytesLeidos);
        }
        fis.close();

        // Convertir a formato hexadecimal
        StringBuilder resultado = new StringBuilder();
        for (byte b : md.digest()) {
            resultado.append(String.format("%02x", b));
        }
        return resultado.toString();
    }

    public String calcularSHA256(String textoNormalizado) {
        try {
            // Instancia de MessageDigest configurada para usar SHA-256.
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashSHA256 = digest.digest(textoNormalizado.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            // Convertimos el array de bytes a hexadecimal.
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : hashSHA256) {
                stringBuilder.append(String.format("%02x", b));
            }

            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible en la JVM.");
        }
    }
}