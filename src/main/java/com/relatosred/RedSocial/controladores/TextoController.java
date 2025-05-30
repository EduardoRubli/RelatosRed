package com.relatosred.RedSocial.controladores;

import com.relatosred.RedSocial.entidades.Categoria;
import com.relatosred.RedSocial.entidades.Etiqueta;
import com.relatosred.RedSocial.entidades.Texto;
import com.relatosred.RedSocial.entidades.Usuario;
import com.relatosred.RedSocial.servicios.TextoService;
import com.relatosred.RedSocial.servicios.UsuarioService;
import com.relatosred.RedSocial.servicios.CategoriaService;
import com.relatosred.RedSocial.servicios.EtiquetaService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Validated
public class TextoController {

    private final SpringValidatorAdapter springValidatorAdapter;
    private TextoService textoService;
    private UsuarioService usuarioService;
    private CategoriaService categoriaService;
    private EtiquetaService etiquetaService;

    // Usamos el constructor para inyectar dependencias.
    public TextoController(TextoService textoService, UsuarioService usuarioService,
                           CategoriaService categoriaService, SpringValidatorAdapter springValidatorAdapter,
                           EtiquetaService etiquetaService) {
        this.textoService = textoService;
        this.usuarioService = usuarioService;
        this.categoriaService = categoriaService;
        this.etiquetaService = etiquetaService;
        this.springValidatorAdapter = springValidatorAdapter;
    }

    // Valida que los campos no estén vacíos.
    private Boolean validarCampo(String campo) {
        if (campo == null || campo.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    @GetMapping("/texto/{idTexto}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long idTexto) throws Exception {
        byte[] pdf = textoService.generarPdf(idTexto);
        Texto texto = textoService.obtenerTextoPorId(idTexto);
        // Obtenemos título del relato.
        String titulo = texto.getTitulo();
        // Sustituímos espacios por guiones.
        titulo = titulo.trim().replace(" ", "-");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.builder("attachment")
                        .filename(titulo + ".pdf").build());
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @GetMapping("/categorias/subcategorias")
    public ResponseEntity<Map<String, List<String>>> listarCategoriasYSubcategorias() {
        // Crear un objeto temporal de Categoria para acceder a los métodos
        Categoria categoria = new Categoria();
        // Obtenemos listas de categorías y subcategorías.
        List<String> listaCategorias = categoria.listarCategorias();
        List<String> listaSubcategorias = categoria.listarSubcategorias();

        // Creamos un disccionario para almacenar valores.
        Map<String, List<String>> diccionario = new HashMap<>();

        diccionario.put("categorias", listaCategorias);
        diccionario.put("subcategorias", listaSubcategorias);

        return ResponseEntity.ok(diccionario);
    }

    @GetMapping("/usuario/{idUsuario}/textos")
    public ResponseEntity<List<Texto>> listarTextosPorUsuario(
            @PathVariable @NotNull @Positive Long idUsuario) {
        List<Texto> textos = textoService.listarTextosPorUsuario(idUsuario);
        return ResponseEntity.ok(textos);
    }

    @GetMapping("/texto/{idTexto}/etiquetas")
    public ResponseEntity<Set<Etiqueta>> listarEtiquetas(@PathVariable @NotNull @Positive Long idTexto) {
        Texto texto = textoService.obtenerTextoPorId(idTexto);
        Set<Etiqueta> etiquetas = etiquetaService.listarEtiquetas(texto);
        return ResponseEntity.ok(etiquetas);
    }

    // Obtener texto por id.
    @GetMapping("/texto/{idTexto}")
    // Solo usamos @NotNull y @Positive en parámetros obligatorios.
    public ResponseEntity<Texto> obtenerTextoPorId(@PathVariable @NotNull @Positive Long idTexto) {
        Texto texto = textoService.obtenerTextoPorId(idTexto);
        return ResponseEntity.ok(texto);
    }

    // Convertimos el string de etiquetas, si existe, en etiquetas.
    private void procesarEtiquetas(String etiquetas, Texto texto) {
        String[] etiquetasArr = etiquetas.split(",");

        for (String nombreEtiqueta : etiquetasArr) {
            Etiqueta etiqueta = new Etiqueta(nombreEtiqueta.trim());
            Etiqueta etiquetaCreada = etiquetaService.crearEtiqueta(etiqueta, texto);

            if (etiquetaCreada != null) {
                // Texto se asocia a Etiqueta en CrearEtiqueta.
                texto.addEtiqueta(etiquetaCreada);
            }
        }
    }

    @PutMapping("/etiqueta/{idEtiqueta}/prohibir")
    public ResponseEntity<String> prohibirEtiqueta(@PathVariable @NotNull @Positive Long idEtiqueta) {
        Etiqueta etiqueta = etiquetaService.obtenerEtiquetaPorId(idEtiqueta);
        etiquetaService.prohibirEtiqueta(etiqueta);
        return ResponseEntity.ok("Etiqueta prohibida correctamente.");
    }

    // Desvincula etiqueta de texto.
    @PutMapping("/texto/{idTexto}/etiqueta/{idEtiqueta}/desvincular")
    public ResponseEntity<String> desvincularEtiqueta(@PathVariable @NotNull @Positive Long idTexto,
                                                   @PathVariable @NotNull @Positive Long idEtiqueta) {
        Texto texto = textoService.obtenerTextoPorId(idTexto);
        Etiqueta etiqueta = etiquetaService.obtenerEtiquetaPorId(idEtiqueta);
        etiquetaService.desvincularEtiqueta(etiqueta, texto);
        return ResponseEntity.ok().body("Etiqueta eliminada con éxito.");
    }

    // Endpoint para crear texto.
    @PostMapping("/texto")
    public ResponseEntity<Texto> crearTexto(@RequestParam(value = "idAutor", required = true)
                                        @NotNull @Positive(message = "El campo autor es obligatorio.") Long idAutor,
                                        @RequestParam(value = "titulo", required = true)
                                            @NotBlank(message = "El campo título es obligatorio.") String titulo,
                                        @RequestParam(value = "contenido", required = true)
                                            @NotBlank(message = "El campo contenido es obligatorio.") String contenido,
                                        @RequestParam(value = "estado", required = true)
                                            @NotBlank(message = "El campo estado es obligatorio.") String estadoStr,
                                        @RequestParam(value = "categoria", required = false) String categoria,
                                        @RequestParam(value = "subcategoria", required = false) String subcategoria,
                                        @RequestParam(value = "etiquetas", required = false) String etiquetaStr,
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

        // Si existen etiquetas las procesamos.
        if (validarCampo(etiquetaStr)) {
            procesarEtiquetas(etiquetaStr, texto);
        }

        // Delegamos la creación a TextoService.
        Texto textoCreado = textoService.crearTexto(texto);

        return ResponseEntity.status(HttpStatus.CREATED).body(textoCreado);
    }

    // Filtrado de textos.
    @GetMapping("/textos")
    public ResponseEntity<List<Texto>> filtrarTextos(
            @RequestParam(required = false) String criterio,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String subCategoria,
            @RequestParam(required = false) String subcadena) {

        List<Texto> textos;

        if (subcadena != null && !subcadena.isBlank()) {
            textos = textoService.buscarTextos(subcadena);

        } else if (categoria != null && subCategoria != null
                    && !categoria.isBlank() && !subCategoria.isBlank()) {
            textos = textoService.filtrarPorCategoriaYSubcategoria(categoria, subCategoria);

        } else if ((categoria != null && !categoria.isBlank()) &&
            (criterio != null && !criterio.isBlank())) {
            textos = textoService.filtrarPorCategoriaYCriterio(categoria, criterio);

        } else if (categoria != null && !categoria.isBlank()) {
            textos = textoService.filtrarPorCategoria(categoria);

        }  else {
            // criterio puede ser null (servicio).
            textos = textoService.filtrarTextos(criterio);
        }

        return ResponseEntity.ok(textos);
    }

    // Actualizar texto.
    @PutMapping("/texto/{idTexto}")
    public ResponseEntity<Texto> actualizarTexto(@RequestParam(value = "idTexto", required = true)
                                               @NotNull @Positive(message = "El campo idTexto es obligatorio.") Long idTexto,
                                               @RequestParam(value = "titulo", required = false) String titulo,
                                               @RequestParam(value = "estado", required = false) String estado,
                                               @RequestParam(value = "categoria", required = false) String categoria,
                                               @RequestParam(value = "subcategoria", required = false) String subcategoria,
                                                 @RequestParam(value = "etiquetas", required = false) String etiquetaStr,
                                               @RequestParam(value = "idioma", required = false) String idioma,
                                               @RequestParam(value = "sinopsis", required = false) String sinopsis,
                                               @RequestParam(value = "contenido", required = false) String contenido) {
            // Recuperamos el texto existente.
            Texto textoExistente = textoService.obtenerTextoPorId(idTexto);
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
                textoExistente.setTitulo(titulo);
            }
            if (validarCampo(estado)) {
                Texto.EstadoTexto nuevoEstado =
                        Texto.EstadoTexto.valueOf(estado.toUpperCase());

                // Si el estado es publicado no se admite cambio.
                if (textoExistente.getEstado() != Texto.EstadoTexto.PUBLICADO &&
                        textoExistente.getEstado() != Texto.EstadoTexto.OCULTO) {
                    textoExistente.setEstado(nuevoEstado);
                }
            }
            // Controlamos categoriaObj.
            if (categoriaObj != null) {
                textoExistente.setCategoria(categoriaObj);
            }
            if (validarCampo(etiquetaStr)) {
                // Obtenemos etiquetas antiguas asociadas al texto.
                Set<Etiqueta> etiquetasAntiguas = textoExistente.getEtiquetas();
                // Comparamos con las etiquetas nuevas y obtenemos eliminadas.
                Set<Etiqueta> eliminadas = etiquetaService.compararEtiquetas(etiquetaStr, etiquetasAntiguas);

                for (Etiqueta eliminada : eliminadas) {
                    etiquetaService.desvincularEtiqueta(eliminada, textoExistente);
                }
                // Finalmente añadimos las nuevas etiquetas.
                procesarEtiquetas(etiquetaStr, textoExistente);
            }

            if (validarCampo(idioma)) {
                textoExistente.setIdioma(idioma);
            }
            if (validarCampo(sinopsis)) {
                textoExistente.setSinopsis(sinopsis);
            }
            if (validarCampo(contenido)) {
                textoExistente.setContenido(contenido);
            }

            // Delegamos la actualización a TextoService.
            Texto textoActualizado = textoService.actualizarTexto(textoExistente);
            return ResponseEntity.ok(textoActualizado);
    }

    @PutMapping("/texto/{idTexto}/eliminar") // El eliminado blando usa PUT.
    public ResponseEntity<String> eliminarTexto(@PathVariable @Positive Long idTexto) {
            // Cambia el estado de eliminado a true.
            textoService.eliminarTexto(idTexto);
            return ResponseEntity.ok().body("Texto eliminado con éxito.");
    }
}