package com.relatosred.RedSocial.controladores;

import com.relatosred.RedSocial.entidades.Categoria;
import com.relatosred.RedSocial.entidades.Texto;
import com.relatosred.RedSocial.entidades.Usuario;
import com.relatosred.RedSocial.servicios.TextoService;
import com.relatosred.RedSocial.servicios.UsuarioService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
public class TextoController {

    private final SpringValidatorAdapter springValidatorAdapter;
    private TextoService textoService;
    private UsuarioService usuarioService;

    // Se usa constructor en lugar de @Autowired.
    public TextoController(TextoService textoService, UsuarioService usuarioService, SpringValidatorAdapter springValidatorAdapter) {
        this.textoService = textoService;
        this.usuarioService = usuarioService;
        this.springValidatorAdapter = springValidatorAdapter;
    }

    // Valida que los campos no estén vacíos.
    private Boolean validarCampo(String campo) {
        if (campo == null || campo.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    // Endpoint para crear un texto.
    @PostMapping("/texto/crear")
    public ResponseEntity<?> crearTexto(@RequestParam(value = "idAutor", required = true)
                                            @NotNull(message = "El campo autor es obligatorio.") Long idAutor,
                                        @RequestParam(value = "titulo", required = true)
                                            @NotBlank(message = "El campo título es obligatorio.") String titulo,
                                        @RequestParam(value = "contenido", required = true)
                                            @NotBlank(message = "El campo contenido es obligatorio.") String contenido,
                                        @RequestParam(value = "estado", required = true)
                                            @NotBlank(message = "El campo estado es obligatorio.") String estadoStr,
                                        @RequestParam(value = "categoria", required = false) String categoria,
                                        @RequestParam(value = "subcategoria", required = false) String subcategoria,
                                        @RequestParam(value = "idioma", required = false) String idioma,
                                        @RequestParam(value = "sinopsis", required = false) String sinopsis) {
        try {
            // Recuperamos usuario (autor).
            Usuario autor = usuarioService.obtenerUsuarioPorId(idAutor);

            // Normalizamos el valor recogido de estado.
            String estadoNormalizado = estadoStr.trim().toUpperCase();
            Texto.EstadoTexto estado;

            try { // Validamos el valor del estado.
                estado = Texto.EstadoTexto.valueOf(estadoNormalizado);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Valor del estado no válido.");
            }

            // Creamos nuevo objeto texto usando constructor.
            Texto texto = new Texto(autor, titulo, contenido, estado);
            Categoria categoriaObj = null;

            if (validarCampo(categoria) && validarCampo(subcategoria)) {
                categoriaObj = new Categoria(categoria, subcategoria);
            } else if (validarCampo(categoria) && !validarCampo(subcategoria)) {
                categoriaObj = new Categoria(categoria);
            }

            // Controlamos categoriaObj.
            if (categoriaObj != null) {
                texto.setCategoria(categoriaObj);
            }
            if (validarCampo(sinopsis)) {
                texto.setSinopsis(sinopsis);
            }
            if (validarCampo(idioma)) {
                texto.setIdioma(idioma);
            }
            // Delegamos la actualización al servicio.
            Texto textoCreado = textoService.crearTexto(texto);
            return ResponseEntity.ok(textoCreado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: \n" + e.getMessage());
        }
    }

    @GetMapping("/textos/filtrar")
    public ResponseEntity<?> filtrarTextos(@RequestParam(required = false)
                                               String criterio) {
        try {
            List<Texto> textos = textoService.filtrarTextos(criterio);
            return ResponseEntity.ok(textos);
        } catch (RuntimeException e) {
            if (e.getMessage().startsWith("No hay textos para mostrar.")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado: \n"
                        + e.getMessage());
            }
        }
    }

    @GetMapping("/textos/filtrarCat")
    public ResponseEntity<?> filtrarPorCategoria(@RequestParam(value = "categoria", required = true)
                                                       String categoria) {
        try {
            List<Texto> textos = textoService.filtrarPorCategoria(categoria);
            return ResponseEntity.ok(textos);
        } catch (RuntimeException e) {
            if (e.getMessage().startsWith("No hay textos para mostrar.")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado: \n"
                        + e.getMessage());
            }
        }
    }

    @GetMapping("/textos/buscar")
    public ResponseEntity<?> buscarTextos(
            @RequestParam(required = true) String subcadena) {
        try {
            List<Texto> textos = textoService.buscarTextos(subcadena);
            return ResponseEntity.ok(textos);
        } catch (RuntimeException e) {
            if (e.getMessage().startsWith("No se encontraron coincidencias.")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado: \n"
                        + e.getMessage());
            }
        }
    }

    // Endpoint para actualizar un texto.
    @PutMapping("/texto/actualizar")
    public ResponseEntity<?> actualizarTexto(@RequestParam(value = "idTexto", required = true)
                                                 @NotNull(message = "El campo idTexto es obligatorio.") Long idTexto,
                                               @RequestParam(value = "titulo", required = false) String titulo,
                                               @RequestParam(value = "estado", required = false) String estado,
                                               @RequestParam(value = "categoria", required = false) String categoria,
                                               @RequestParam(value = "subcategoria", required = false) String subcategoria,
                                               @RequestParam(value = "idioma", required = false) String idioma,
                                               @RequestParam(value = "sinopsis", required = false) String sinopsis,
                                               @RequestParam(value = "contenido", required = false) String contenido) {
        try {
            Texto texto = null;
            // Recuperamos el texto existente.
            texto = textoService.obtenerTexto(idTexto);
            Categoria categoriaObj = null;

            if (validarCampo(categoria) && validarCampo(subcategoria)) {
                categoriaObj = new Categoria(categoria, subcategoria);
            } else if (validarCampo(categoria) && !validarCampo(subcategoria)) {
                categoriaObj = new Categoria(categoria);
            }

            // Actualizamos los campos si se han pasado valores.
            if (validarCampo(titulo)) {
                texto.setTitulo(titulo);
            }
            if (validarCampo(estado)) {
                Texto.EstadoTexto nuevoEstado =
                        Texto.EstadoTexto.valueOf(estado.toUpperCase());

                // Si el estado es publicado no se admite cambio.
                if (texto.getEstado() != Texto.EstadoTexto.PUBLICADO &&
                texto.getEstado() != Texto.EstadoTexto.OCULTO) {
                    texto.setEstado(nuevoEstado);
                }
            }
            // Controlamos categoriaObj.
            if (categoriaObj != null) {
                texto.setCategoria(categoriaObj);
            }
            if (validarCampo(idioma)) {
                texto.setIdioma(idioma);
            }
            if (validarCampo(sinopsis)) {
                texto.setSinopsis(sinopsis);
            }
            if (validarCampo(contenido)) {
                texto.setContenido(contenido);
            }

            // Delegamos la actualización al servicio.
            Texto textoActualizado = textoService.actualizarTexto(texto);
            return ResponseEntity.ok(textoActualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/texto/{idTexto}") // El eliminado blando usa PUT.
    public ResponseEntity<?> eliminarTexto(@PathVariable Long idTexto) {
        try {
            textoService.eliminarTexto(idTexto);
            return ResponseEntity.ok().body("Texto eliminado con éxito.");
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Texto no encontrado.")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().equals("El texto ya está eliminado.")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado: " + e.getMessage());
        }
    }
}