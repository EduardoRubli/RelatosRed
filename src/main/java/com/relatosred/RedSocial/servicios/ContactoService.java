package com.relatosred.RedSocial.servicios;

import com.relatosred.RedSocial.entidades.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import com.relatosred.RedSocial.repositorios.ContactoRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContactoService {

    private ContactoRepository contactoRepository;
    private UsuarioService usuarioService;

    // El constructor inyecta las dependencias.
    public ContactoService(ContactoRepository contactoRepository,
                           UsuarioService usuarioService) {
        this.contactoRepository = contactoRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public Contacto agregarContacto(Long idUsuario1, Long idUsuario2) {
        if (idUsuario1.equals(idUsuario2)) {
            throw new IllegalArgumentException("No puedes agregarte a ti mismo.");
        }

        Usuario usuario1 = usuarioService.obtenerUsuarioPorId(idUsuario1);
        Usuario usuario2 = usuarioService.obtenerUsuarioPorId(idUsuario2);

        // Comprobaciones de estados
        if (contactoRepository.existsByUsuariosAndEstado(usuario1, usuario2, Contacto.EstadoContacto.PENDIENTE)) {
            throw new EntityExistsException("La solicitud ya existe.");
        }
        if (contactoRepository.existsByUsuariosAndEstado(usuario1, usuario2, Contacto.EstadoContacto.ACEPTADO)) {
            throw new EntityExistsException("El usuario ya es un contacto.");
        }
        if (contactoRepository.existsByUsuariosAndEstado(usuario1, usuario2, Contacto.EstadoContacto.BLOQUEADO)) {
            throw new EntityExistsException("No se permiten solicitudes de contacto.");
        }

        // Reutilización de contacto rechazado.
        Optional<Contacto> contactoRechazadoOpt =
                contactoRepository.findByUsuariosAndEstado(usuario1, usuario2, Contacto.EstadoContacto.RECHAZADO);

        if (contactoRechazadoOpt.isPresent()) {
            Contacto contactoRechazado = contactoRechazadoOpt.get();
            contactoRechazado.setEstado(Contacto.EstadoContacto.PENDIENTE);
            contactoRechazado.setFechaContacto(LocalDateTime.now());
            return contactoRepository.save(contactoRechazado);
        }

        Contacto contacto = new Contacto();
        contacto.setUsuario1(usuario1);
        contacto.setUsuario2(usuario2);
        contacto.setEstado(Contacto.EstadoContacto.PENDIENTE);
        contacto.setFechaContacto(LocalDateTime.now());
        return contactoRepository.save(contacto);
    }

    // Devuelve contactos de un usuario para un estado.
    public List<Usuario> listarContactos(Long idUsuario, String estado) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(idUsuario);
        Contacto.EstadoContacto estadoEnum;

        try {
            estadoEnum = Contacto.EstadoContacto.valueOf(estado.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Estado de contacto no válido.");
        }

        List<Contacto> contactos = contactoRepository.findByUsuarioAndEstado(usuario, estadoEnum);

        List<Usuario> listaUsuarios = new ArrayList<>();
        for (Contacto c : contactos) {
            if (c.getUsuario1().equals(usuario)) {
                listaUsuarios.add(c.getUsuario2());
            } else {
                listaUsuarios.add(c.getUsuario1());
            }
        }
        return listaUsuarios;
    }

    @Transactional
    public void aceptarContacto(Long idUsuario1, Long idUsuario2) {
        Usuario usuario1 = usuarioService.obtenerUsuarioPorId(idUsuario1);
        Usuario usuario2 = usuarioService.obtenerUsuarioPorId(idUsuario2);

        // Buscamos una relación PENDIENTE entre ambos (en cualquier dirección)
        Optional<Contacto> contactoOpt = contactoRepository.findByUsuariosAndEstado(
                usuario1, usuario2, Contacto.EstadoContacto.PENDIENTE);

        if (!contactoOpt.isPresent()) {
            throw new EntityNotFoundException("No existe solicitud pendiente de contacto.");
        }

        Contacto contacto = contactoOpt.get();
        contacto.setEstado(Contacto.EstadoContacto.ACEPTADO);
        contacto.setFechaContacto(LocalDateTime.now());
        contactoRepository.save(contacto);
    }

    @Transactional
    public void eliminarContacto(Long idUsuario1, Long idUsuario2) {
        Usuario usuario1 = usuarioService.obtenerUsuarioPorId(idUsuario1);
        Usuario usuario2 = usuarioService.obtenerUsuarioPorId(idUsuario2);

        List<Contacto> contactos = contactoRepository.findByUsuarios(usuario1, usuario2);

        for (Contacto c : contactos) {
            if (c.getEstado() != Contacto.EstadoContacto.BLOQUEADO &&
                    c.getEstado() != Contacto.EstadoContacto.RECHAZADO) {
                c.setEstado(Contacto.EstadoContacto.RECHAZADO);
                contactoRepository.save(c);
            }
        }
    }

    @Transactional
    public Usuario bloquearContacto(Long idUsuario1, Long idUsuario2) {
        Usuario usuario1 = usuarioService.obtenerUsuarioPorId(idUsuario1);
        Usuario usuario2 = usuarioService.obtenerUsuarioPorId(idUsuario2);

        List<Contacto> contactos = contactoRepository.findByUsuarios(usuario1, usuario2);

        for (Contacto c : contactos) {
            if (c.getEstado() != Contacto.EstadoContacto.BLOQUEADO &&
                    c.getEstado() != Contacto.EstadoContacto.RECHAZADO) {
                c.setEstado(Contacto.EstadoContacto.RECHAZADO);
                contactoRepository.save(c);
            }
            if (c.getUsuario1().equals(usuario1)) {
                c.setEstado(Contacto.EstadoContacto.BLOQUEADO);
                contactoRepository.save(c);
            }
        }

        // Si no existe la relación en sentido correcto se crea.
        if (!contactoRepository.existsByUsuario1AndUsuario2(usuario1, usuario2)) {
            Contacto contacto = new Contacto();
            contacto.setUsuario1(usuario1);
            contacto.setUsuario2(usuario2);
            contacto.setEstado(Contacto.EstadoContacto.BLOQUEADO);
            contacto.setFechaContacto(LocalDateTime.now());
            contactoRepository.save(contacto);
        }
        return usuario2;
    }
}