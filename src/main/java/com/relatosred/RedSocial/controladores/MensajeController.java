package com.relatosred.RedSocial.controladores;

import com.relatosred.RedSocial.entidades.Mensaje;
import com.relatosred.RedSocial.entidades.Texto;
import com.relatosred.RedSocial.entidades.Usuario;
import com.relatosred.RedSocial.servicios.MensajeService;
import com.relatosred.RedSocial.servicios.TextoService;
import com.relatosred.RedSocial.servicios.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
public class MensajeController {
    private MensajeService mensajeService;
    private TextoService textoService;
    private UsuarioService usuarioService;

    // Usamos el constructor para inyectar dependencias.
    public MensajeController(MensajeService mensajeService, TextoService textoService, UsuarioService usuarioService) {
        this.mensajeService = mensajeService;
        this.textoService = textoService;
        this.usuarioService = usuarioService;
    }

    // Valida que los campos no estén vacíos.
    private Boolean validarCampo(String campo) {
        if (campo == null || campo.trim().isEmpty()) {
            return false;
        }
        return true;
    }
    // Sobrecarga de función.
    private boolean validarCampo(Long campo) {
        return campo != null && campo > 0;
    }

    // Obtener mensaje por id
    @GetMapping("/mensaje/{idMensaje}")
    public ResponseEntity<Mensaje> obtenerMensajePorId(@PathVariable @Positive Long idMensaje) {
        // Las excepciones son gestionadas por GestionExcepciones.
        Mensaje mensaje = mensajeService.obtenerMensajePorId(idMensaje);
        return ResponseEntity.ok(mensaje);
    }

    // Crea mensaje.
    @PostMapping("/mensaje")
    public ResponseEntity<Mensaje> crearMensaje(@RequestParam(value = "contenidoStr", required = true)
                                                    @NotNull(message = "El campo mensaje es obligatorio.") String contenidoStr,
                                                @RequestParam(value = "idEmisor", required = true) @Positive Long idEmisor,
                                                @RequestParam(value = "tipoStr", required = true) String tipoStr,
                                                @RequestParam(value = "tituloStr", required = false) String tituloStr,
                                                @RequestParam(value = "idTexto", required = false) @Positive Long idTexto,
                                                @RequestParam(value = "idPadre", required = false) @Positive Long idPadre,
                                                @RequestParam(value = "idReceptor", required = false) @Positive Long idReceptor){

        Usuario usuario = null;
        if (validarCampo(idEmisor)) {
            usuario = usuarioService.obtenerUsuarioPorId(idEmisor);
        }
        Texto texto = null;
        if (validarCampo(idTexto)) {
            texto = textoService.obtenerTextoPorId(idTexto);
        }
        Mensaje padre = null;
        if (validarCampo(idPadre)) {
            padre = mensajeService.obtenerMensajePorId(idPadre);
        }
        Usuario receptor = null;
        if (validarCampo(idReceptor)) {
            receptor = usuarioService.obtenerUsuarioPorId(idReceptor);
        }
        String titulo = null;
        if (validarCampo(tituloStr)) {
            titulo = tituloStr;
        }
        String contenido = null;
        if (validarCampo(contenidoStr)) {
            contenido = contenidoStr;
        }

        Mensaje.TipoMensaje tipoEnum;
        try {
            tipoEnum = Mensaje.TipoMensaje.valueOf(tipoStr.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Tipo de mensaje no válido.");
        }

        Mensaje mensaje = new Mensaje(usuario, texto, padre, receptor, contenidoStr, titulo, tipoEnum);

        Mensaje creado = mensajeService.crearMensaje(mensaje, idEmisor, idTexto, idPadre);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // Actualiza mensaje.
    @PutMapping("/mensaje/{idMensaje}")
    public ResponseEntity<Mensaje> actualizarMensaje(@PathVariable @Positive Long idMensaje,
                                                     @RequestParam(value = "contenidoStr", required = true) String contenidoStr) {
        if (!validarCampo(idMensaje) || !validarCampo(contenidoStr)) {
            throw new IllegalArgumentException("Los parámetros enviados son incorrectos.");
        }
        Mensaje actualizado = mensajeService.actualizarMensaje(idMensaje, contenidoStr);
        // @RestControllerAdvice en GestorExcepciones.
        return ResponseEntity.ok(actualizado);
    }

    // Lista comentarios y respuestas.
    @GetMapping("/texto/{idTexto}/comentarios")
    public ResponseEntity<List<Mensaje>> listarComentariosPorTexto(@PathVariable @Positive Long idTexto) {
        // Devolver una lista vacía no es incorrecto.
        List<Mensaje> lista = mensajeService.listarComentariosPorTexto(idTexto);
        // @RestControllerAdvice en GestorExcepciones.
        return ResponseEntity.ok(lista);
    }

    // Borrado blando de mensaje (usa PUT).
    @PutMapping("/mensaje/{idMensaje}/eliminar")
    public ResponseEntity<String> eliminarMensaje(@PathVariable @Positive Long idMensaje) {
        Mensaje eliminado = mensajeService.eliminarMensaje(idMensaje);
        // @RestControllerAdvice en GestorExcepciones.
        return ResponseEntity.ok().body("Mensaje eliminado con éxito.");
    }
}