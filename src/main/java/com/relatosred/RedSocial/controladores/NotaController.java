package com.relatosred.RedSocial.controladores;

import com.relatosred.RedSocial.entidades.NotaTexto;
import com.relatosred.RedSocial.entidades.Texto;
import com.relatosred.RedSocial.entidades.Usuario;
import com.relatosred.RedSocial.servicios.NotaService;
import com.relatosred.RedSocial.servicios.TextoService;
import com.relatosred.RedSocial.servicios.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

@RestController
@Validated
public class NotaController {

    private NotaService notaService;
    private UsuarioService usuarioService;
    private TextoService textoService;

    // Se usa constructor en lugar de @Autowired.
    public NotaController(NotaService notaService, UsuarioService usuarioService, TextoService textoService) {
        this.notaService = notaService;
        this.usuarioService = usuarioService;
        this.textoService = textoService;
    }

    // Endpoint para puntuar texto.
    @PutMapping("/texto/{idTexto}/nota/{idUsuario}")
    public ResponseEntity<NotaTexto> puntuarTexto(@PathVariable(value = "idTexto", required = true)
                                              @NotNull Long idTexto,
                                                  @PathVariable(value = "idUsuario", required = true)
                                          @NotNull Long idUsuario,
                                          @RequestParam(value = "nota", required = true)
                                              @NotNull @Min(1) @Max(10) Double nota) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(idUsuario);
        Texto texto = textoService.obtenerTextoPorId(idTexto);

        // Creamos un nuevo objeto NotaTexto.
        NotaTexto notaTexto = new NotaTexto(usuario, texto, nota);

        // Delegamos la puntuación a NotaService.
        notaTexto = notaService.puntuarTexto(notaTexto);
        return ResponseEntity.ok(notaTexto);
    }

    // Calcula la nota media.
    @GetMapping("/texto/{idTexto}/nota")
    public ResponseEntity<?> obtenerNotaMedia(@RequestParam(value = "idTexto", required = true)
                                          @NotNull Long idTexto) {
        // Obtiene el texto y devuelve su nota media.
        Texto texto = textoService.obtenerTextoPorId(idTexto);
        return ResponseEntity.ok(texto.getNotaMedia());
    }
}