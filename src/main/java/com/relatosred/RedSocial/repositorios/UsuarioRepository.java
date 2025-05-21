package com.relatosred.RedSocial.repositorios;

import com.relatosred.RedSocial.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Devuelve usuario con email dado.
    Optional<Usuario> findByEmailIgnoreCase(String email);
    //Usuario findByIdUsuario(Long idUsuario);
    Optional<Usuario> findById(Long id);
    // Verifica si existe usuario con email.
    boolean existsByEmailIgnoreCase(String email);
    // Métodos para búsqueda de usuarios.
    List<Usuario> findByAliasContainingIgnoreCase(String alias);
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
    List<Usuario> findByApellidoContainingIgnoreCase(String apellido);
    List<Usuario> findByEmailContainingIgnoreCase(String email);
}