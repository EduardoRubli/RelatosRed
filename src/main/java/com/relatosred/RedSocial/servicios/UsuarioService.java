package com.relatosred.RedSocial.servicios;

import com.relatosred.RedSocial.entidades.Usuario;
import com.relatosred.RedSocial.repositorios.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;

    // Se usa constructor en lugar de @Autowired.
    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Verifica la existencia de usuario.
    public boolean existeUsuarioPorEmail(String email){
        return usuarioRepository.existsByEmailIgnoreCase(email);
    }

    // Obtener usuario por email.
    public Usuario obtenerUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado."));
        return usuario;
    }

    // Obtener usuario por id.
    public Usuario obtenerUsuarioPorId(Long idUsuario) {
        Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);
        if (!usuario.isPresent()) {
            throw new EntityNotFoundException("Usuario no encontrado.");
        }
        return usuario.get();
    }

    @Transactional // Crear nuevo usuario tras verificar email.
    public Usuario crearUsuario(Usuario usuario) {
        if (existeUsuarioPorEmail(usuario.getEmail())) {
            throw new DataIntegrityViolationException("Ya existe un usuario con ese email.");
        }
        usuario.setFechaReg(LocalDateTime.now());
        // No se debe persistir con la contraseña sin cifrar.
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    // Obtener usuarios por alias, nombre o email parcial.
    public List<Usuario> buscarUsuarios(String subcadena) {
        List<Usuario> listaResultados = new ArrayList<>();

        listaResultados.addAll(usuarioRepository.findByAliasContainingIgnoreCase(subcadena));
        listaResultados.addAll(usuarioRepository.findByNombreContainingIgnoreCase(subcadena));
        listaResultados.addAll(usuarioRepository.findByApellidoContainingIgnoreCase(subcadena));
        listaResultados.addAll(usuarioRepository.findByEmailContainingIgnoreCase(subcadena));

        // Eliminanos duplicados de la lista.
        return listaResultados.stream().distinct().toList();
    }

    @Transactional // Actualizar usuario existente por email.
    public Usuario actualizarUsuario(Usuario usuario) {
        Usuario usuarioActualizado = null;

        if (existeUsuarioPorEmail(usuario.getEmail())) {
            usuarioActualizado = usuarioRepository.save(usuario);
        } else {
            // RuntimeExcepction interrumpe la ejecución.
            throw new EntityNotFoundException("Usuario no encontrado.");
        }
        return usuarioActualizado;
    }

    @Transactional // "Borrado" blando.
    public Usuario eliminarUsuario(Long idUsuario) {
        Usuario usuario = obtenerUsuarioPorId(idUsuario);

        if (usuario.getEliminado()) {
            throw new IllegalArgumentException("El usuario ya está eliminado.");
        }

        // Realizamos borrado blando.
        usuario.setEliminado(true);
        // "@Transactional" persiste el usuario.
        return usuario;
    }
}
