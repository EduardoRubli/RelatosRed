package com.relatosred.RedSocial.repositorios;

import com.relatosred.RedSocial.entidades.NotaTexto;
import com.relatosred.RedSocial.entidades.Texto;
import com.relatosred.RedSocial.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotaRepository extends JpaRepository<NotaTexto, Long> {
    Optional<NotaTexto> findByTextoAndUsuario(Texto texto, Usuario usuario);
    boolean existsByTexto(Texto texto);
    boolean existsByUsuario(Usuario usuario);
}