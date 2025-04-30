package com.relatosred.RedSocial.controladores;

import com.relatosred.RedSocial.entidades.Categoria;
import com.relatosred.RedSocial.entidades.Texto;
import com.relatosred.RedSocial.entidades.Usuario;
import com.relatosred.RedSocial.servicios.TextoService;
import com.relatosred.RedSocial.servicios.UsuarioService;
import com.relatosred.RedSocial.servicios.CategoriaService;
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
    private CategoriaService categoriaService;

    // Usamos el constructor para inyectar dependencias.
    public TextoController(TextoService textoService, UsuarioService usuarioService,
                           CategoriaService categoriaService, SpringValidatorAdapter springValidatorAdapter) {
        this.textoService = textoService;
        this.usuarioService = usuarioService;
        this.categoriaService = categoriaService;
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
    public ResponseEntity<Texto> crearTexto(@RequestParam(value = "idAutor", required = true)
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

        // Recuperamos usuario (autor).
        Usuario autor = usuarioService.obtenerUsuarioPorId(idAutor);
        Texto.EstadoTexto estado;

        try { // Validamos el valor del estado.
            estado = Texto.EstadoTexto.valueOf(estadoStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Valor del estado no válido.");
        }

        // Creamos nuevo objeto texto.
        Texto texto = new Texto(autor, titulo, contenido, estado);

        // Categoría y subcategoría.
        Categoria categoriaObj = null;
        if (validarCampo(categoria) && validarCampo(subcategoria)) {
            try {
                Categoria.CategoriaEnum categoriaEnum = Categoria.CategoriaEnum.valueOf(categoria.toUpperCase());
                Categoria.SubcategoriaEnum subcategoriaEnum = Categoria.SubcategoriaEnum.valueOf(subcategoria.toUpperCase());
                // Tras convertir los strings a enum, creamos un objeto Categoría.
                categoriaObj = categoriaService.obtenerOCrearCategoria(categoriaEnum, subcategoriaEnum);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Categoría o subcategoría no válida.");
            }
        } else if (validarCampo(categoria) && !validarCampo(subcategoria)) {
            try {
                Categoria.CategoriaEnum categoriaEnum = Categoria.CategoriaEnum.valueOf(categoria.toUpperCase());
                categoriaObj = categoriaService.obtenerOCrearCategoria(categoriaEnum, null);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Categoría no válida.");
            }
        }

        if (categoriaObj != null) {
            texto.setCategoria(categoriaObj);
        }
        if (validarCampo(sinopsis)) {
            texto.setSinopsis(sinopsis);
        }
        if (validarCampo(idioma)) {
            texto.setIdioma(idioma);
        }

        // Delegamos la creación a TextoService.
        Texto textoCreado = textoService.crearTexto(texto);
        return ResponseEntity.status(HttpStatus.CREATED).body(textoCreado);
    }

    @GetMapping("/textos/filtrar")
    public ResponseEntity<List<Texto>> filtrarTextos(@RequestParam(required = false)
                                               String criterio) {
        List<Texto> textos = textoService.filtrarTextos(criterio);
        // Si no hay resultados se devuelve una lista vacía.
        return ResponseEntity.ok(textos);

    }

    @GetMapping("/textos/filtrarCat")
    public ResponseEntity<List<Texto>> filtrarPorCategoria(@RequestParam(value = "categoria", required = true)
                                                       String categoria) {
        List<Texto> textos = textoService.filtrarPorCategoria(categoria);
        return ResponseEntity.ok(textos);
    }

    @GetMapping("/textos/buscar")
    public ResponseEntity<List<Texto>> buscarTextos(
            @RequestParam(required = true) String subcadena) {
            List<Texto> textos = textoService.buscarTextos(subcadena);
            // En REST una búsqueda vacía no es un error.
            return ResponseEntity.ok(textos);
    }

    // Endpoint para actualizar un texto.
    @PutMapping("/texto/actualizar")
    public ResponseEntity<Texto> actualizarTexto(@RequestParam(value = "idTexto", required = true)
                                                 @NotNull(message = "El campo idTexto es obligatorio.") Long idTexto,
                                               @RequestParam(value = "titulo", required = false) String titulo,
                                               @RequestParam(value = "estado", required = false) String estado,
                                               @RequestParam(value = "categoria", required = false) String categoria,
                                               @RequestParam(value = "subcategoria", required = false) String subcategoria,
                                               @RequestParam(value = "idioma", required = false) String idioma,
                                               @RequestParam(value = "sinopsis", required = false) String sinopsis,
                                               @RequestParam(value = "contenido", required = false) String contenido) {
            // Recuperamos el texto existente.
            Texto texto = textoService.obtenerTextoPorId(idTexto);
            Categoria categoriaObj = null;

            if (validarCampo(categoria) && validarCampo(subcategoria)) {
                try {
                    Categoria.CategoriaEnum categoriaEnum = Categoria.CategoriaEnum.valueOf(categoria.toUpperCase());
                    Categoria.SubcategoriaEnum subcategoriaEnum = Categoria.SubcategoriaEnum.valueOf(subcategoria.toUpperCase());
                    categoriaObj = categoriaService.obtenerOCrearCategoria(categoriaEnum, subcategoriaEnum);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Categoría no válida.");
                }
            } else if (validarCampo(categoria) && !validarCampo(subcategoria)) {
                try {
                    Categoria.CategoriaEnum categoriaEnum = Categoria.CategoriaEnum.valueOf(categoria.toUpperCase());
                    categoriaObj = categoriaService.obtenerOCrearCategoria(categoriaEnum, null);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Categoría no válida.");
                }
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

            // Delegamos la actualización a TextoService.
            Texto textoActualizado = textoService.actualizarTexto(texto);
            return ResponseEntity.ok(textoActualizado);
    }

    @PutMapping("/texto/{idTexto}") // El eliminado blando usa PUT.
    public ResponseEntity<String> eliminarTexto(@PathVariable Long idTexto) {
            // Cambia el estado de eliminado a true.
            textoService.eliminarTexto(idTexto);
            return ResponseEntity.ok().body("Texto eliminado con éxito.");
    }
}