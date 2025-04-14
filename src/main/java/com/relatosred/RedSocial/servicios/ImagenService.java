package com.relatosred.RedSocial.servicios;

import com.relatosred.RedSocial.entidades.Imagen;
import com.relatosred.RedSocial.entidades.Usuario;
import com.relatosred.RedSocial.repositorios.ImagenRepository;
import com.relatosred.RedSocial.utilidades.HashUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ImagenService {

    private ImagenRepository imagenRepository;

    // Se usa constructor en lugar de @Autowired.
    public ImagenService(ImagenRepository imagenRepository) {
        this.imagenRepository = imagenRepository;
    }

    public Imagen guardarImagen(File archivo, String ruta, String descripcion, String tipo, Usuario usuario)
            throws NoSuchAlgorithmException, IOException {
        String hashMD5 = HashUtil.calcularMD5(archivo);

        // Verificar si ya existe una imagen con el mismo hash
        Optional<Imagen> imagenExistente = imagenRepository.findByHashMD5(hashMD5);
        if (imagenExistente.isPresent()) {
            return imagenExistente.get(); // Devolver la imagen existente
        }

        // Si no existe, guardar la nueva imagen
        Imagen imagen = new Imagen();
        imagen.setRuta(ruta);
        imagen.setDescripcion(descripcion);
        imagen.setFechaSubida(LocalDateTime.now());
        imagen.setTipo(tipo);
        imagen.setUsuario(usuario);
        imagen.setHashMD5(hashMD5);

        return imagenRepository.save(imagen);
    }
}