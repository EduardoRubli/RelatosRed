package com.relatosred.RedSocial.repositorios;

import com.relatosred.RedSocial.entidades.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    boolean existsById(Long idMensaje);
    Optional<Mensaje> findById(Long idMensaje);
    // Mensajes raíz asociados a un texto (COMENTARIO).
    List<Mensaje> findByTextoIdTextoAndTipoAndPadreIsNullAndEliminadoFalseOrderByFechaEnvioAsc(Long idTexto, Mensaje.TipoMensaje tipo);
    // Mensajes de respuesta asociados a un mensaje raíz.
    List<Mensaje> findByPadreIdMensajeAndTipoAndEliminadoFalseOrderByFechaEnvioAsc(Long idPadre, Mensaje.TipoMensaje tipo);
}