package com.relatosred.RedSocial.servicios;


import com.relatosred.RedSocial.entidades.Texto;
import com.relatosred.RedSocial.repositorios.TextoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TextoService {

    private TextoRepository textoRepository;

    // Se usa constructor en lugar de @Autowired.
    public TextoService(TextoRepository textoRepository) {
        this.textoRepository = textoRepository;
    }

    // Verificar contenido duplicado.
    public boolean existeTextoPorContenido(String contenido){
        return textoRepository.existsByContenido(contenido);
    }

    // Verificar existencia de texto por id.
    public boolean existeTextoPorId(Long idContenido){
        return textoRepository.existsById(idContenido);
    }

    // Obtener texto por id.
    public Texto obtenerTexto(Long idTexto) {
        Optional<Texto> textoOpt = textoRepository.findById(idTexto);

        if (!textoOpt.isPresent()) {
            throw new RuntimeException("Texto no encontrado.");
        }
        return textoOpt.get();
    }

    @Transactional // Crea nuevo objeto Texto.
    public Texto crearTexto(Texto texto) {
        Texto textoGuardado = null;

        if (!existeTextoPorContenido(texto.getContenido())) {
            textoGuardado = textoRepository.save(texto);
        } else {
            // RuntimeExcepction interrumpe la ejecución.
            throw new RuntimeException("Ya existe un texto con ese contenido.");
        }

        return textoGuardado;
    }

    // Obtiene textos por título parcial o autor.
    public List<Texto> buscarTextos(String subcadena) {
        List<Texto> listaResultados = new ArrayList<>();

        listaResultados.addAll(textoRepository.buscarPorAutor(subcadena));
        listaResultados.addAll(textoRepository.buscarPorTitulo(subcadena));

        // Eliminanos duplicados de la lista.
        listaResultados = listaResultados.stream().distinct().toList();

        if (listaResultados.isEmpty()) { // Controlamos que no haya coincidencias.
            throw new RuntimeException("No se encontraron coincidencias.");
        }

        return listaResultados;
    }

    // Filtra textos por criterio (por defecto nota).
    public List<Texto> filtrarTextos(String criterio) {
        // Valor por defecto si no se especifica.
        if (criterio == null || criterio.isBlank()) {
            criterio = "notaMedia";
        }
        List<Texto> listaResultados = new ArrayList<>();
        listaResultados.addAll(textoRepository.filtrarTextosPorCriterio(criterio));

        // Controlamos que la lista no esté vacía.
        if (listaResultados.isEmpty()) throw new RuntimeException("No hay textos para mostrar.");

        return listaResultados.stream().distinct().toList();
    }

    // Filtra textos por categoría.
    public List<Texto> filtrarPorCategoria(String categoria) {
        // Valor por defecto si no se especifica.
        if (categoria == null || categoria.isBlank()) {
            throw new RuntimeException("La categoría seleccionada no es válida.");
        }
        List<Texto> listaResultados = new ArrayList<>();
        listaResultados.addAll(textoRepository.buscarPorCategoria(categoria));

        // Controlamos que la lista no esté vacía.
        if (listaResultados.isEmpty()) throw new RuntimeException("No hay textos para mostrar.");

        return listaResultados.stream().distinct().toList();
    }

    // Filtra textos por categoría.
    public List<Texto> filtrarPorCategoriaYSubcategoria(String categoria, String subcategoria) {
        // Valor por defecto si no se especifica.
        if (categoria == null || categoria.isBlank()) {
            throw new RuntimeException("La categoría seleccionada no es válida.");
        } else if (subcategoria == null || subcategoria.isBlank()) {
            throw new RuntimeException("La subcategoría seleccionada no es válida.");
        }
        List<Texto> listaResultados = new ArrayList<>();
        listaResultados.addAll(textoRepository.buscarPorCategoriaYSubcategoria(categoria, subcategoria));

        // Controlamos que la lista no esté vacía.
        if (listaResultados.isEmpty()) throw new RuntimeException("No hay textos para mostrar.");

        return listaResultados.stream().distinct().toList();
    }

    @Transactional // Actualizar texto existente por id.
    public Texto actualizarTexto(Texto texto) {
        Texto textoActualizado = null;

        if (existeTextoPorId(texto.getIdTexto())) {
            textoActualizado = textoRepository.save(texto);
        } else {
            // RuntimeExcepction interrumpe la ejecución.
            throw new RuntimeException("Texto no encontrado.");
        }

        return textoActualizado;
    }

    @Transactional // Borrado blando.
    public Texto eliminarTexto(Long idTexto) {
        Texto texto = obtenerTexto(idTexto);
        if (texto.getEliminado()) {
            throw new RuntimeException("El texto ya está eliminado.");
        }

        texto.setEliminado(true);

        return texto;
    }
}