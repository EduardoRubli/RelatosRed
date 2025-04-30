package com.relatosred.RedSocial.servicios;


import com.relatosred.RedSocial.entidades.Categoria;
import com.relatosred.RedSocial.entidades.Texto;
import com.relatosred.RedSocial.repositorios.CategoriaRepository;
import com.relatosred.RedSocial.repositorios.TextoRepository;
import com.relatosred.RedSocial.utilidades.HashUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TextoService {

    private TextoRepository textoRepository;
    private CategoriaRepository categoriaRepository;
    private HashUtil hashUtil;

    // Se usa constructor en lugar de @Autowired.
    public TextoService(TextoRepository textoRepository, CategoriaRepository categoriaRepository) {
        this.textoRepository = textoRepository;
        this.categoriaRepository = categoriaRepository;
        this.hashUtil = new HashUtil();
    }

    // Verificar existencia de texto por id.
    public boolean existeTextoPorId(Long idContenido){
        return textoRepository.existsById(idContenido);
    }

    // Obtener texto por id.
    public Texto obtenerTextoPorId(Long idTexto) {
        Optional<Texto> textoOpt = textoRepository.findById(idTexto);

        if (!textoOpt.isPresent()) {
            throw new EntityNotFoundException("Texto no encontrado.");
        }
        return textoOpt.get();
    }

    public String normalizarTexto(String texto) {
        if (texto == null) {
            return null;
        }
        // Convertir a minúsculas.
        String normalizado = texto.toLowerCase();

        // Elimina espacios en blanco y caracteres de puntuación.
        normalizado = normalizado.replaceAll("[\\s,.;\\-_–—()\\[\\]{}]+", "");

        return normalizado;
    }

    @Transactional // Crea nuevo objeto Texto.
    public Texto crearTexto(Texto texto) {
        Texto textoGuardado;

        // Calculamos hash a partir del contenido normalizado.
        String textoNormalizado = normalizarTexto(texto.getContenido());
        String hashSHA256 = hashUtil.calcularSHA256(textoNormalizado);

        if (!textoRepository.existsByHashSHA256(hashSHA256)) {
            // Añadimos hash calculado.
            texto.setHashSHA256(hashSHA256);
            textoGuardado = textoRepository.save(texto);
        } else {
            // Excepción específica para duplicado.
            throw new DataIntegrityViolationException("Ya existe un texto con ese contenido.");
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

        return listaResultados;
    }

    // Filtra textos por criterio (por defecto nota).
    public List<Texto> filtrarTextos(String criterio) {
        // Valor por defecto si no se especifica.
        if (criterio == null || criterio.isBlank()) {
            criterio = "notaMedia";
        }
        List<Texto> listaResultados = new ArrayList<>();
        listaResultados.addAll(textoRepository.filtrarPorCriterio(criterio));

        return listaResultados.stream().distinct().toList();
    }

    // Filtra textos por categoría.
    public List<Texto> filtrarPorCategoria(String categoria) {
        // Valor por defecto si no se especifica.
        if (categoria == null || categoria.isBlank()) {
            throw new IllegalArgumentException("La categoría no puede estar vacía.");
        }

        // Validación de subcategoría.
        Categoria.CategoriaEnum categoriaEnum;
        try {
            categoriaEnum = Categoria.CategoriaEnum.valueOf(categoria.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Categoría '" + categoria + "' no válida.");
        }

        List<Texto> listaResultados = new ArrayList<>();
        listaResultados.addAll(textoRepository.buscarPorCategoria(categoriaEnum));

        return listaResultados.stream().distinct().toList();
    }

    // Filtra textos por categoría y subcategoría.
    public List<Texto> filtrarPorCategoriaYSubcategoria(String categoriaStr, String subcategoriaStr) {
        // Valor por defecto si no se especifica.
        if (categoriaStr == null || categoriaStr.isBlank() ||
                subcategoriaStr == null || subcategoriaStr.isBlank()) {
            throw new IllegalArgumentException("Se requiere categoría y subcategoría.");
        }

        // Validación de categoría.
        Categoria.CategoriaEnum categoria;
        try {
            categoria = Categoria.CategoriaEnum.valueOf(categoriaStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Categoría '" + categoriaStr + "' no válida.");
        }

        // Validación de subcategoría.
        Categoria.SubcategoriaEnum subcategoria;
        try {
            subcategoria = Categoria.SubcategoriaEnum.valueOf(subcategoriaStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Subcategoría '" + subcategoriaStr + "' no válida.");
        }

        List<Texto> listaResultados = new ArrayList<>();
        listaResultados.addAll(textoRepository.buscarPorCategoriaYSubcategoria(categoria, subcategoria));

        return listaResultados.stream().distinct().toList();
    }

    @Transactional // Actualizar texto existente por id.
    public Texto actualizarTexto(Texto texto) {
        Texto textoActualizado = null;

        if (existeTextoPorId(texto.getIdTexto())) {
            
                // Obtenemos nuevo hash SHA-256 para el contenido.
                String textoNormalizado = normalizarTexto(texto.getContenido());
                String nuevoHash = hashUtil.calcularSHA256(textoNormalizado);
                // Obtenemos texto original existente.
                Optional<Texto> txtOptional = textoRepository.findById(texto.getIdTexto());
                Texto textoExistente = txtOptional.get();

                // Si se ha actualizado el contenido, recalculamos el hash.
                if (textoExistente.getHashSHA256() == null ||
                        !textoExistente.getHashSHA256().equals(nuevoHash)) {
                    texto.setHashSHA256(nuevoHash);
                }
                textoActualizado = textoRepository.save(texto);
        } else {
            // RuntimeExcepction interrumpe la ejecución.
            throw new EntityNotFoundException("Texto no encontrado.");
        }

        return textoActualizado;
    }

    @Transactional // Borrado blando.
    public Texto eliminarTexto(Long idTexto) {
        Texto texto = obtenerTextoPorId(idTexto);
        if (texto.getEliminado()) {
            throw new IllegalArgumentException("El texto ya está eliminado.");
        }

        texto.setEliminado(true);

        return texto;
    }
}