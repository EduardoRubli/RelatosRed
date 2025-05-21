package com.relatosred.RedSocial.repositorios;

import com.relatosred.RedSocial.entidades.Filtro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FiltroRepository extends JpaRepository<Filtro, Long> {
    boolean existsByPalabraAndTipo(String palabra, Filtro.TipoFiltro tipo);
    Filtro findByPalabraAndTipo(String palabra, Filtro.TipoFiltro tipo);
}
