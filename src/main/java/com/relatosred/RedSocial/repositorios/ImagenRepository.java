package com.relatosred.RedSocial.repositorios;

import com.relatosred.RedSocial.entidades.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ImagenRepository extends JpaRepository<Imagen, Long> {
    Optional<Imagen> findByHashMD5(String hashMD5);
}