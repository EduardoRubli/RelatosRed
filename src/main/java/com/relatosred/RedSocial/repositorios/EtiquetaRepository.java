package com.relatosred.RedSocial.repositorios;

import com.relatosred.RedSocial.entidades.Etiqueta;
import com.relatosred.RedSocial.entidades.Texto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface EtiquetaRepository extends JpaRepository<Etiqueta, Long> {
    boolean existsByNombreAndPermitida(String nombre, Boolean permitida);
    Optional<Etiqueta> findByNombre(String nombre);
    Optional<Etiqueta> findById(Long idEtiqueta);
}
