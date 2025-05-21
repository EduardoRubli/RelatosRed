package com.relatosred.RedSocial.repositorios;

import com.relatosred.RedSocial.entidades.Categoria;
import com.relatosred.RedSocial.entidades.Contacto;
import com.relatosred.RedSocial.entidades.Texto;
import com.relatosred.RedSocial.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContactoRepository extends JpaRepository<Contacto, Long> {
    // Solo verifica una dirección.
    boolean existsByUsuario1AndUsuario2(Usuario usuario1, Usuario usuario2);

    // Todas las relaciones entre dos usuarios.
    @Query("SELECT c FROM Contacto c WHERE " +
            "(c.usuario1 = :u1 AND c.usuario2 = :u2) OR (c.usuario1 = :u2 AND c.usuario2 = :u1)")
    List<Contacto> findByUsuarios(Usuario u1, Usuario u2);

    //Lista contactos de un usuario.
    @Query("SELECT c FROM Contacto c WHERE " +
            "(c.usuario1 = :u OR c.usuario2 = :u) " +
            "AND c.estado = :estado " +
            "ORDER BY c.fechaContacto DESC")
    List<Contacto> findByUsuarioAndEstado(Usuario u, Contacto.EstadoContacto estado);

    // Obtiene contacto por usuarios y estado.
    @Query("SELECT c FROM Contacto c WHERE " +
            "((c.usuario1 = :u1 AND c.usuario2 = :u2) OR (c.usuario1 = :u2 AND c.usuario2 = :u1)) " +
            "AND c.estado = :estado")
    Optional<Contacto> findByUsuariosAndEstado(Usuario u1, Usuario u2, Contacto.EstadoContacto estado);

    // Es lo mismo que "SELECT COUNT(c) > 0 FROM Contacto c".
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Contacto c WHERE " +
            "((c.usuario1 = :u1 AND c.usuario2 = :u2) OR (c.usuario1 = :u2 AND c.usuario2 = :u1)) " +
            "AND c.estado = :estado")
    boolean existsByUsuariosAndEstado(Usuario u1, Usuario u2, Contacto.EstadoContacto estado);
}