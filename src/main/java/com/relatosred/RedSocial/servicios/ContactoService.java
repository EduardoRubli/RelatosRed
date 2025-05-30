package com.relatosred.RedSocial.servicios;

import com.relatosred.RedSocial.entidades.*;
import com.relatosred.RedSocial.repositorios.UsuarioRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import com.relatosred.RedSocial.repositorios.ContactoRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContactoService {

    private ContactoRepository contactoRepository;
    private UsuarioRepository usuarioRepository;
    private UsuarioService usuarioService;

    // El constructor inyecta las dependencias.
    public ContactoService(ContactoRepository contactoRepository,
                           UsuarioRepository usuarioRepository,
                           UsuarioService usuarioService) {
        this.contactoRepository = contactoRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public Contacto seguir(Long idSeguidor, Long idSeguido) {
        if (idSeguidor.equals(idSeguido)) {
            throw new IllegalArgumentException("No puedes seguirte a ti mismo.");
        }

        // Verificamos que existan seguidor y seguido.
        Usuario seguidor = usuarioRepository.findById(idSeguidor)
                .orElseThrow(() -> new NoSuchElementException("Seguidor no existe."));
        Usuario seguido = usuarioRepository.findById(idSeguido)
                .orElseThrow(() -> new NoSuchElementException("Usuario a seguir no existe."));

        // Verificamos si existe relación.
        Optional<Contacto> contactoOpt =
                contactoRepository.findBySeguidorAndSeguido(seguidor, seguido);

        // Si ya existe, no duplicar.
        if (contactoOpt.isPresent()) {
            Contacto contacto = contactoOpt.get();
            if (contacto.getBloqueado()) {
                contacto.setBloqueado(false);
                return contactoRepository.save(contacto);
            } else {
                throw new IllegalStateException("Ya sigues a este usuario.");
            }
        } else {
            Contacto nuevoContacto = new Contacto();
            nuevoContacto.setSeguidor(seguidor);
            nuevoContacto.setSeguido(seguido);
            nuevoContacto.setBloqueado(false);
            nuevoContacto.setFechaContacto(LocalDateTime.now());
            return contactoRepository.save(nuevoContacto);
        }
    }

    @Transactional
    public void dejarDeSeguir(Long idSeguidor, Long idSeguido) {
        if (idSeguidor.equals(idSeguido)) {
            throw new IllegalArgumentException("No puedes dejar de seguirte a ti mismo.");
        }

        // Verificamos que existan seguidor y seguido.
        Usuario seguidor = usuarioRepository.findById(idSeguidor)
                .orElseThrow(() -> new NoSuchElementException("Seguidor no existe."));
        Usuario seguido = usuarioRepository.findById(idSeguido)
                .orElseThrow(() -> new NoSuchElementException("Usuario a dejar de seguir no existe."));

        // Obtenemos relación de seguimiento.
        Optional<Contacto> contactoOpt = contactoRepository.findBySeguidorAndSeguido(seguidor, seguido);

        if (contactoOpt.isPresent()) {
            // Si existe, eliminamos el contacto.
            contactoRepository.delete(contactoOpt.get());
        } else {
            // Si no existe la relación, lanzamos excepción.
            throw new IllegalStateException("No sigues a este usuario.");
        }
    }

    @Transactional
    public Contacto bloquearUsuario(Long idBloqueador, Long idBloqueado) {
        Usuario bloqueador = usuarioRepository.findById(idBloqueador)
                .orElseThrow(() -> new NoSuchElementException("Usuario bloqueador no encontrado."));
        Usuario bloqueado = usuarioRepository.findById(idBloqueado)
                .orElseThrow(() -> new NoSuchElementException("Usuario a bloquear no encontrado"));

        // Si ya existe un bloqueo en esa dirección salimos.
        if (contactoRepository.existsBySeguidorAndSeguidoAndBloqueado(bloqueador, bloqueado, true)) {
            return contactoRepository.findBySeguidorAndSeguido(bloqueador, bloqueado).get();
        }

        Optional<Contacto> existente = contactoRepository.findBySeguidorAndSeguido(bloqueador, bloqueado);

        if (existente.isPresent()) {
            // Si ya existe, se actualiza como bloqueado.
            Contacto contacto = existente.get();
            contacto.setBloqueado(true);
            return contactoRepository.save(contacto);
        } else {
            // Si no existe, se crea una relación unilateral de "bloqueo".
            Contacto nuevoBloqueo = new Contacto();
            nuevoBloqueo.setSeguidor(bloqueador);
            nuevoBloqueo.setSeguido(bloqueado);
            nuevoBloqueo.setBloqueado(true);
            nuevoBloqueo.setFechaContacto(LocalDateTime.now());
            return contactoRepository.save(nuevoBloqueo);
        }
    }

    @Transactional
    public void desbloquearUsuario(Long idBloqueador, Long idBloqueado) {
        Usuario bloqueador = usuarioRepository.findById(idBloqueador)
                .orElseThrow(() -> new NoSuchElementException("Usuario bloqueador no encontrado."));
        Usuario bloqueado = usuarioRepository.findById(idBloqueado)
                .orElseThrow(() -> new NoSuchElementException("Usuario a desbloquear no encontrado."));

        Optional<Contacto> existenteOpt = contactoRepository.findBySeguidorAndSeguido(bloqueador, bloqueado);

        if (existenteOpt.isPresent()) {
            Contacto existente = existenteOpt.get();
            if (existente.getBloqueado()) {
                contactoRepository.delete(existente);
            } else {
                throw new IllegalStateException("El usuario no está bloqueado.");
            }
        }
    }

    // Listar usuarios seguidos.
    @Transactional(readOnly = true)
    public List<Usuario> listarSeguidos(Long idSeguidor) {
        Usuario seguidor = usuarioRepository.findById(idSeguidor)
                .orElseThrow(() -> new NoSuchElementException("Seguidor no existe."));

        List<Contacto> contactos = contactoRepository.findBySeguidorAndBloqueadoFalse(seguidor);
        List<Usuario> seguidos  = new ArrayList<>();
        for (Contacto c : contactos) {
            seguidos.add(c.getSeguido());
        }
        return seguidos;
    }

    // Listar usuarios que nos siguen.
    @Transactional(readOnly = true)
    public List<Usuario> listarSeguidores(Long idSeguido) {
        Usuario seguido = usuarioRepository.findById(idSeguido)
                .orElseThrow(() -> new NoSuchElementException("Usuario no existe."));

        List<Contacto> contactos = contactoRepository.findBySeguidoAndBloqueadoFalse(seguido);
        List<Usuario> seguidores = new ArrayList<>();
        for (Contacto c : contactos) {
            seguidores.add(c.getSeguidor());
        }
        return seguidores;
    }
}