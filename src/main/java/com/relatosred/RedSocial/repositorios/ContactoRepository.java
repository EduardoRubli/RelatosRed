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
    boolean existsBySeguidorAndSeguido(Usuario seguidor, Usuario seguido);
    // Verificar si existe un vínculo de bloqueo.
    boolean existsBySeguidorAndSeguidoAndBloqueado(Usuario seguidor, Usuario seguido, Boolean bloqueado);
    // Obtiene la relación de seguimiento (con o sin bloqueo).
    Optional<Contacto> findBySeguidorAndSeguido(Usuario seguidor, Usuario seguido);
    // Obtiene lista de seguidores de un usuario.
    List<Contacto> findBySeguidoAndBloqueadoFalse(Usuario seguido);
    // Obtiene lista de usuarios seguidos por un usuario.
    List<Contacto> findBySeguidorAndBloqueadoFalse(Usuario seguidor);
}