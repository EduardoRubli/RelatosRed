package com.relatosred.RedSocial.servicios;

import com.relatosred.RedSocial.entidades.Etiqueta;
import com.relatosred.RedSocial.entidades.Filtro;
import com.relatosred.RedSocial.entidades.Texto;
import com.relatosred.RedSocial.repositorios.EtiquetaRepository;
import com.relatosred.RedSocial.repositorios.FiltroRepository;
import com.relatosred.RedSocial.repositorios.TextoRepository;
import static com.relatosred.RedSocial.entidades.Filtro.TipoFiltro.ETIQUETA;
import com.relatosred.RedSocial.utilidades.Validador;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EtiquetaService {

    private EtiquetaRepository etiquetaRepository;
    private FiltroRepository filtroRepository;
    private TextoRepository textoRepository;
    private Validador validador;

    // El constructor inyecta las dependencias.
    public EtiquetaService(EtiquetaRepository etiquetaRepository,
                           FiltroRepository filtroRepository,
                           TextoRepository textoRepository) {
        this.etiquetaRepository = etiquetaRepository;
        this.filtroRepository = filtroRepository;
        this.textoRepository = textoRepository;
        this.validador = new Validador();
    }

    // Obtener etiqueta por id.
    public Etiqueta obtenerEtiquetaPorId(Long idEtiqueta){
        Optional<Etiqueta> etiquetaOpt = etiquetaRepository.findById(idEtiqueta);

        if (!etiquetaOpt.isPresent()) {
            throw new EntityNotFoundException("Etiqueta no encontrada.");
        }
        return etiquetaOpt.get();
    }

    @Transactional(readOnly = true)
    public Set<Etiqueta> listarEtiquetas(Texto texto){
        Set<Etiqueta> etiquetas = texto.getEtiquetas();
        Set<Etiqueta> etiquetasFiltradas = new HashSet<>();
        for (Etiqueta e : etiquetas) {
            // Mostramos solo las permitidas.
            if (e.getPermitida()) {
                etiquetasFiltradas.add(e);
            }
        }
        return etiquetasFiltradas;
    }

    // Devuelve las etiquetas eliminadas al actualizar.
    public Set<Etiqueta> compararEtiquetas(String etiquetasNuevas,
                                           Set<Etiqueta> etiquetasAntiguas) {
        Set<Etiqueta> eliminadas = new HashSet<>();
        String[] etiquetasNuevasArr = etiquetasNuevas.split(",");

        Set<String> nuevasNormalizadas = new HashSet<>();
        for (String etiquetaNueva : etiquetasNuevasArr) {
            nuevasNormalizadas.add(normalizarNombre(etiquetaNueva));
        }

        // Agrega a eliminadas las que NO están entre las nuevas.
        for (Etiqueta etiquetaAntigua : etiquetasAntiguas) {
            if (!nuevasNormalizadas.contains(etiquetaAntigua.getNombre())) {
                eliminadas.add(etiquetaAntigua);
            }
        }
        return eliminadas;
    }

    @Transactional // Crea o actualiza una etiqueta existente.
    public Etiqueta crearEtiqueta(Etiqueta etiqueta, Texto texto) {
        // Convertimos el formato a "#NombreEtiqueta".
        String nombreNormalizado = normalizarNombre(etiqueta.getNombre());

        // Si la etiqueta no se permite devuelve null.
        if (!validador.validarEtiqueta(nombreNormalizado) ||
                etiquetaRepository.existsByNombreAndPermitida(nombreNormalizado, false)) {
            return null;
        }

        Optional<Etiqueta> etiquetaExistenteOpt = etiquetaRepository.findByNombre(nombreNormalizado);

        if (etiquetaExistenteOpt.isPresent()) {
            // Obtenemos etiqueta existente.
            Etiqueta etiquetaExistente = etiquetaExistenteOpt.get();
            if (!etiquetaExistente.getTextos().contains(texto)) {
                // Asociamos el texto a la etiqueta existente.
                etiquetaExistente.addTexto(texto);
                // Actualizamos el contador de textos (popularidad).
                etiquetaExistente.setPopularidad(etiquetaExistente.getTextos().size());
            }
            return etiquetaRepository.save(etiquetaExistente);
        } else {
            etiqueta.setNombre(nombreNormalizado);
            // Asociamos el texto a la nueva etiqueta.
            etiqueta.addTexto(texto);
            // Seteamos a 1 la popularidad.
            etiqueta.setPopularidad(1);
            return etiquetaRepository.save(etiqueta);
        }
    }

    public String normalizarNombre(String nombre) {
        // 1) se convierte el texto a minúsculas.
        String normalizado = nombre.toLowerCase();

        // 2) Se eliminan los caracteres no permitidos y tildes.
        normalizado = normalizado.replaceAll("[^a-záàäâéèëêíìïîóòöôúùüûñç]", " ").trim()
                .replaceAll("[áàäâ]", "a")
                .replaceAll("[éèëê]", "e")
                .replaceAll("[íìïî]", "i")
                .replaceAll("[óòöô]", "o")
                .replaceAll("[úùüû]", "u")
                // Reduce espacios consecutivos a uno solo.
                .replaceAll("\\s+", " ");

        // Capitalizamos la primera letra de cada palabra.
        String[] palabras = normalizado.split(" ");
        // Es importante incluir la almoadilla aquí.
        StringBuilder resultado = new StringBuilder("#");

        for (String palabra : palabras) {
            if (!palabra.isBlank()) {
                // charAt() devuelve el caracter correspondiente al índice.
                resultado.append(Character.toUpperCase(palabra.charAt(0)))
                        // substring devuelve la subcadena a partir del índice.
                        .append(palabra.substring(1));
            }
        }

        return resultado.toString().trim();
    }

    @Transactional // Desvincula etiqueta de texto.
    public void desvincularEtiqueta(Etiqueta etiqueta, Texto texto) {
        // Eliminamos etiqueta asociada a texto.
        Boolean etiquetaEliminada = texto.getEtiquetas().remove(etiqueta);
        // Persistimos texto sin etiqueta.
        if (etiquetaEliminada) textoRepository.save(texto);

        // Eliminamos texto asociado a etiqueta.
        Boolean textoEliminado = etiqueta.getTextos().remove(texto);
        // Persistimos etiqueta sin texto.
        if (textoEliminado) etiquetaRepository.save(etiqueta);
    }

    @Transactional // Hace uso de la entidad Filtro.
    public void prohibirEtiqueta(Etiqueta etiqueta) {
        if (etiqueta.getPermitida().equals(false)){
            throw new EntityExistsException("La etiqueta ya esta prohibida.");
        }

        etiqueta.setPermitida(false);
        etiquetaRepository.save(etiqueta);

        String nombre = etiqueta.getNombre();
        // Creamos un nuevo filtro para la etiqueta.
        Filtro filtro = new Filtro(nombre, Filtro.TipoFiltro.ETIQUETA);
        // Si existe un filtro y no está activo, lo activamos.
        if (filtroRepository.existsByPalabraAndTipo(nombre, Filtro.TipoFiltro.ETIQUETA)) {
            Filtro filtroExistente = filtroRepository.findByPalabraAndTipo(nombre, Filtro.TipoFiltro.ETIQUETA);
            if (filtroExistente.getActivo().equals(false)) {
                filtroExistente.setActivo(true);
                filtroRepository.save(filtroExistente);
            }
        } else {
            // Guardamos el nuevo filtro.
            filtroRepository.save(filtro);
        }
    }
}
