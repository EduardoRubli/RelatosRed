package com.relatosred.RedSocial.repositorios;

import com.relatosred.RedSocial.entidades.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    boolean existsByCategoria(String categoria);
    boolean existsByCategoriaAndSubcategoria(String categoria, String subcategoria);
    Optional<Categoria> findByCategoriaAndSubcategoria(Categoria.CategoriaEnum categoria,
                                                       Categoria.SubcategoriaEnum subcategoria);
}
