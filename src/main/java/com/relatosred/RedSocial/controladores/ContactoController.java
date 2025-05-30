package com.relatosred.RedSocial.controladores;

import com.relatosred.RedSocial.entidades.Contacto;
import com.relatosred.RedSocial.entidades.Usuario;
import com.relatosred.RedSocial.servicios.ContactoService;
import com.relatosred.RedSocial.servicios.UsuarioService;
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

    // Seguir a un usuario.
    @PostMapping("/seguir")
    public ResponseEntity<Contacto> seguir(
            @RequestParam @Positive Long idSeguidor,
            @RequestParam @Positive Long idSeguido) {

        Contacto contacto = contactoService.seguir(idSeguidor, idSeguido);
        return ResponseEntity.status(HttpStatus.CREATED).body(contacto);
    }

    // Dejar de seguir a usuario.
    @DeleteMapping("/dejarDeSeguir")
    public ResponseEntity<Void> dejarDeSeguir(
            @RequestParam @Positive Long idSeguidor,
            @RequestParam @Positive Long idSeguido) {

        contactoService.dejarDeSeguir(idSeguidor, idSeguido);
        return ResponseEntity.noContent().build();
    }

    // Bloquear a un usuario.
    @PutMapping("/bloquear")
    public ResponseEntity<Contacto> bloquear(
            @RequestParam @Positive Long idBloqueador,
            @RequestParam @Positive Long idBloqueado) {

        Contacto contacto = contactoService.bloquearUsuario(idBloqueador, idBloqueado);
        return ResponseEntity.ok(contacto);
    }

    // Desbloquear contacto.
    @PutMapping("/desbloquear")
    public ResponseEntity<Void> desbloquear(
            @RequestParam @Positive Long IdBloqueador,
            @RequestParam @Positive Long idBloqueado) {

        contactoService.desbloquearUsuario(IdBloqueador, idBloqueado);
        return ResponseEntity.noContent().build();
    }

    // Listar usuarios seguidos.
    @GetMapping("/siguiendo")
    public ResponseEntity<List<Usuario>> listarSeguidos(
            @RequestParam @Positive Long idUsuario) {

        List<Usuario> lista = contactoService.listarSeguidos(idUsuario);
        return ResponseEntity.ok(lista);
    }

     // Listar seguidores.
    @GetMapping("/seguidores")
    public ResponseEntity<List<Usuario>> listarSeguidores(
            @RequestParam @Positive Long idUsuario) {

        List<Usuario> lista = contactoService.listarSeguidores(idUsuario);
        return ResponseEntity.ok(lista);
    }
}