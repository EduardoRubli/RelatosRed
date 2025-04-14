package com.relatosred.RedSocial.utilidades;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {
    public static String calcularMD5(File archivo) throws NoSuchAlgorithmException, IOException {
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
}