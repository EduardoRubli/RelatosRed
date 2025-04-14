package com.relatosred.RedSocial.repositorios;

import com.relatosred.RedSocial.entidades.Texto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TextoRepository extends JpaRepository<Texto, Long> {
    // Verificar si existe texto por id.
    boolean existsById(Long idTexto);
    // Verificar si existe un texto por contenido.
    boolean existsByContenido(String contenido);
    // Eliminar texto por id.
    void deleteById(Long idTexto);
    
    // Búsqueda por categoría.
    @Query("SELECT t FROM Texto t WHERE " +
            "t.categoria.categoria = :categoria AND " +
            "t.estado = 'publicado' AND " +
            "t.eliminado = false " +
            "ORDER BY t.notaMedia DESC")
    List<Texto> buscarPorCategoria(
            @Param("categoria") String categoria
    );

    // Búsqueda por categoría y subcategoría.
    @Query("SELECT t FROM Texto t WHERE " +
            "t.categoria.categoria = :categoria AND " +
            "t.categoria.subcategoria = :subcategoria AND " +
            "t.estado = 'publicado' AND " +
            "t.eliminado = false " +
            "ORDER BY t.notaMedia DESC")
    List<Texto> buscarPorCategoriaYSubcategoria(
            @Param("categoria") String categoria,
            @Param("subcategoria") String subcategoria
    );

    // Búsqueda por título parcial.
    @Query("SELECT t FROM Texto t WHERE " +
            "t.titulo LIKE LOWER(CONCAT('%', :titulo, '%')) AND " +
            "t.estado = 'publicado' AND " +
            "t.eliminado = false " +
            "ORDER BY t.notaMedia DESC")
    List<Texto> buscarPorTitulo(
            @Param("titulo") String titulo
    );

    // Búsqueda por nombre de autor parcial.
    @Query("SELECT t FROM Texto t WHERE " +
            "t.autor.alias LIKE LOWER(CONCAT('%', :nombreAutor, '%')) AND " +
            "t.estado = 'publicado' AND " +
            "t.eliminado = false " +
            "ORDER BY t.notaMedia DESC")
    List<Texto> buscarPorAutor(
            @Param("nombreAutor") String nombreAutor
    );

    // Filtrar textos por criterio.
    @Query("SELECT t FROM Texto t WHERE " +
            "t.estado = 'publicado' AND " +
            "t.eliminado = false " +
            "ORDER BY " +
            "CASE " +
            "   WHEN :criterio = 'notaMedia' THEN t.notaMedia " +
            "   WHEN :criterio = 'fechaPublicacion' THEN t.fechaPublicacion " +
            "   ELSE t.notaMedia " + // Valor por defecto
            "END DESC")
    List<Texto> filtrarTextosPorCriterio(@Param("criterio") String criterio);
}
