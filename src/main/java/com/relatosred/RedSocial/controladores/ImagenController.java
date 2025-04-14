package com.relatosred.RedSocial.controladores;

import com.relatosred.RedSocial.entidades.Imagen;
import com.relatosred.RedSocial.entidades.Usuario;
import com.relatosred.RedSocial.servicios.ImagenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/imagenes")
public class ImagenController {

    private ImagenService imagenService;

    // Se usa constructor en lugar de @Autowired.
    public ImagenController(ImagenService imagenService) {
        this.imagenService = imagenService;
    }

    @PostMapping("/subir")
    public ResponseEntity<Imagen> subirImagen(@RequestParam("archivo") MultipartFile archivo,
                                              @RequestParam("descripcion") String descripcion,
                                              @RequestParam("tipo") String tipo,
                                              @RequestParam("usuarioId") Long usuarioId) {
        try {
            // Convertir MultipartFile a File
            File tempFile = File.createTempFile("imagen", null);
            archivo.transferTo(tempFile);

            // Crear usuario temporal (aquí deberías obtenerlo de la base de datos)
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(usuarioId);

            Imagen imagen = imagenService.guardarImagen(tempFile, archivo.getOriginalFilename(), descripcion, tipo, usuario);

            return ResponseEntity.ok(imagen);
        } catch (IOException | NoSuchAlgorithmException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}