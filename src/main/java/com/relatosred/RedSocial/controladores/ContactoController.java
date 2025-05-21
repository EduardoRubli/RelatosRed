package com.relatosred.RedSocial.controladores;

import com.relatosred.RedSocial.entidades.Contacto;
import com.relatosred.RedSocial.entidades.Usuario;
import com.relatosred.RedSocial.servicios.ContactoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
public class ContactoController {

    private final ContactoService contactoService;

    public ContactoController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    // Solicitar contacto.
    @PostMapping("/contacto")
    public ResponseEntity<Contacto> agregarContacto(
                                    @RequestParam @NotNull @Positive Long idUsuario1,
                                    @RequestParam @NotNull @Positive Long idUsuario2) {
        Contacto contacto = contactoService.agregarContacto(idUsuario1, idUsuario2);
        return ResponseEntity.status(HttpStatus.CREATED).body(contacto);
    }

    // Listar contactos.
    @GetMapping("/contactos")
    public ResponseEntity<List<Usuario>> listarContactos(
                                        @RequestParam @NotNull @Positive Long idUsuario,
                                        @RequestParam(defaultValue = "ACEPTADO") String estado) {
        List<Usuario> usuarios = contactoService.listarContactos(idUsuario, estado);
        return ResponseEntity.ok(usuarios);
    }

    // Aceptar contacto.
    @PutMapping("/contacto/aceptar")
    public ResponseEntity<String> aceptarContacto(
                                  @RequestParam @NotNull @Positive Long idUsuario1,
                                  @RequestParam @NotNull @Positive Long idUsuario2) {
        contactoService.aceptarContacto(idUsuario1, idUsuario2);
        return ResponseEntity.ok("Contacto aceptado correctamente.");
    }

    // Eliminar contacto.
    @PutMapping("/contacto/eliminar")
    public ResponseEntity<String> eliminarContacto(
                                  @RequestParam @NotNull @Positive Long idUsuario1,
                                  @RequestParam @NotNull @Positive Long idUsuario2) {
        contactoService.eliminarContacto(idUsuario1, idUsuario2);
        return ResponseEntity.ok("Contacto eliminado o rechazado.");
    }

    // Bloquear contacto.
    @PutMapping("/contacto/bloquear")
    public ResponseEntity<Usuario> bloquearContacto(
                                   @RequestParam @NotNull @Positive Long idUsuario1,
                                   @RequestParam @NotNull @Positive Long idUsuario2) {
        Usuario bloqueado = contactoService.bloquearContacto(idUsuario1, idUsuario2);
        return ResponseEntity.ok(bloqueado);
    }
}