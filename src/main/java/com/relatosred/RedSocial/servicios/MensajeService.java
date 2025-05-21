package com.relatosred.RedSocial.servicios;

import com.relatosred.RedSocial.entidades.Etiqueta;
import com.relatosred.RedSocial.entidades.Mensaje;
import com.relatosred.RedSocial.entidades.Texto;
import com.relatosred.RedSocial.entidades.Usuario;
import com.relatosred.RedSocial.repositorios.*;
import com.relatosred.RedSocial.utilidades.Validador;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.thymeleaf.util.StringUtils.substring;

@Service
public class MensajeService {
    private MensajeRepository mensajeRepository;
    private FiltroRepository filtroRepository;
    private TextoRepository textoRepository;
    private UsuarioRepository usuarioRepository;
    private Validador validador;

    // El constructor inyecta las dependencias.
    public MensajeService(MensajeRepository mensajeRepository,
                           FiltroRepository filtroRepository,
                           TextoRepository textoRepository,
                          UsuarioRepository usuarioRepository) {
        this.mensajeRepository = mensajeRepository;
        this.filtroRepository = filtroRepository;
        this.textoRepository = textoRepository;
        this.usuarioRepository = usuarioRepository;
        this.validador = new Validador();
    }

    // Obtiene un mensaje por id.
    public Mensaje obtenerMensajePorId(Long idMensaje) {
        Optional<Mensaje> mensajeOpt = mensajeRepository.findById(idMensaje);

        if (!mensajeOpt.isPresent()) {
            throw new EntityNotFoundException("Mensaje no encontrado.");
        }
        return mensajeOpt.get();
    }

    private String obtenerFecha() {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        return ahora.format(formatter);
    }

    @Transactional // Crea un mensaje (COMENTARIO o RESCOMENTARIO).
    public Mensaje crearMensaje(Mensaje mensaje, Long idEmisor, Long idTexto, Long idPadre) {
        // .orElseThrow(() -> ...) trabaja con el tipo Optional.
        Usuario emisor = usuarioRepository.findById(idEmisor)
                .orElseThrow(() -> new EntityNotFoundException("Emisor no encontrado"));
        mensaje.setEmisor(emisor);

        if (idTexto != null) {
            // Si el Optional está vacío se lanza EntityNotFoundException.
            Texto texto = textoRepository.findById(idTexto)
                    .orElseThrow(() -> new EntityNotFoundException("Texto no encontrado"));
            mensaje.setTexto(texto);
            mensaje.setTitulo("Comentario número " + texto.getNumMensajes() + " - " + obtenerFecha());
        }

       if (idPadre != null) {
           // La sintaxis 'orElseThrow' evita NullPinterException.
            Mensaje padre = mensajeRepository.findById(idPadre)
                    .orElseThrow(() -> new EntityNotFoundException("Mensaje padre no encontrado"));
            // No se permite responder a respuestas.
            if (padre.getTipo() != Mensaje.TipoMensaje.COMENTARIO) {
                throw new IllegalArgumentException("Solo se puede responder a comentarios raíz.");
            }
            mensaje.setPadre(padre);
            int indiceFinal = padre.getTitulo().length()-19;
            // El título indica a qué comentario se responde.
            mensaje.setTitulo("RE: " + substring(padre.getTitulo(),0, indiceFinal) +
                    " - " + obtenerFecha());
        }
        return mensajeRepository.save(mensaje);
    }

    @Transactional // Actualizar mensaje.
    public Mensaje actualizarMensaje(Long idMensaje, String nuevoContenido) {
        Mensaje mensajeExistente = mensajeRepository.findById(idMensaje)
                .orElseThrow(() -> new IllegalArgumentException("Mensaje no encontrado"));
        mensajeExistente.setContenido(nuevoContenido);
        // No actualizamos fechaEnvio ni otros campos.
        return mensajeRepository.save(mensajeExistente);
    }

    @Transactional(readOnly = true) // Lista ordenada de comentarios.
    public List<Mensaje> listarComentariosPorTexto(Long idTexto) {
        List<Mensaje> listaFinal = new ArrayList<>();

        // Obtenemos comentarios raíz ordenados por fecha.
        List<Mensaje> comentariosRaiz = mensajeRepository
                .findByTextoIdTextoAndTipoAndPadreIsNullAndEliminadoFalseOrderByFechaEnvioAsc(
                        idTexto, Mensaje.TipoMensaje.COMENTARIO);

        for (Mensaje comentario : comentariosRaiz) {
            listaFinal.add(comentario);

            // Obtenemos respuestas de comentario raíz.
            List<Mensaje> respuestas = mensajeRepository
                    .findByPadreIdMensajeAndTipoAndEliminadoFalseOrderByFechaEnvioAsc(
                            comentario.getIdMensaje(), Mensaje.TipoMensaje.RESCOMENTARIO);

            listaFinal.addAll(respuestas);
        }
        return listaFinal;
    }

    @Transactional // Borrado blando.
    public Mensaje eliminarMensaje(Long idMensaje) {
        Mensaje mensaje = obtenerMensajePorId(idMensaje);
        if (mensaje.getEliminado()) {
            throw new IllegalArgumentException("El mensaje ya está eliminado.");
        }

        mensaje.setEliminado(true);

        return mensaje;
    }
}
