package com.relatosred.RedSocial.repositorios;

import com.relatosred.RedSocial.entidades.Categoria;
import com.relatosred.RedSocial.entidades.Texto;
import com.relatosred.RedSocial.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TextoRepository extends JpaRepository<Texto, Long> {
    // Verificar si existe texto por id.
    boolean existsById(Long idTexto);
    boolean existsByAutor(Usuario autor);
    List<Texto> findByAutor(Usuario idAutor);
    // Verificar si existe texto por contenido.
    boolean existsByContenido(String contenidoTexto);
    // Verificar si existe un texto por hash.
    boolean existsByHashSHA256(String hashSHA256);
    // Eliminar texto por id.
    void deleteById(Long idTexto);
    
    // Búsqueda por categoría.
    @Query("SELECT t FROM Texto t WHERE " +
            "t.categoria.categoria = :categoria AND " +
            "t.estado = 'PUBLICADO' AND " +
            "t.eliminado = false " +
            "ORDER BY t.notaMedia DESC")
    List<Texto> buscarPorCategoria(
            @Param("categoria") Categoria.CategoriaEnum categoria
    );

    // Búsqueda por categoría y subcategoría.
    @Query("SELECT t FROM Texto t WHERE " +
            "t.categoria.categoria = :categoria AND " +
            "t.categoria.subcategoria = :subcategoria AND " +
            "t.estado = 'PUBLICADO' AND " +
            "t.eliminado = false " +
            "ORDER BY t.notaMedia DESC")
    List<Texto> buscarPorCategoriaYSubcategoria(
            @Param("categoria") Categoria.CategoriaEnum categoria,
            @Param("subcategoria") Categoria.SubcategoriaEnum subcategoria
    );

    // Búsqueda por título parcial.
    @Query("SELECT t FROM Texto t WHERE " +
            "t.titulo LIKE LOWER(CONCAT('%', :titulo, '%')) AND " +
            "t.estado = 'PUBLICADO' AND " +
            "t.eliminado = false " +
            "ORDER BY t.notaMedia DESC")
    List<Texto> buscarPorTitulo(
            @Param("titulo") String titulo
    );

    // Búsqueda por nombre de autor parcial.
    @Query("SELECT t FROM Texto t WHERE " +
            "t.autor.alias LIKE LOWER(CONCAT('%', :nombreAutor, '%')) AND " +
            "t.estado = 'PUBLICADO' AND " +
            "t.eliminado = false " +
            "ORDER BY t.notaMedia DESC")
    List<Texto> buscarPorAutor(
            @Param("nombreAutor") String nombreAutor
    );

    // Filtrar textos por criterio.
    @Query("SELECT t FROM Texto t WHERE " +
            "t.estado = 'PUBLICADO' AND " +
            "t.eliminado = false " +
            "ORDER BY " +
            "CASE " +
            "   WHEN :criterio = 'notaMedia' THEN t.notaMedia " +
            "   WHEN :criterio = 'fechaPublicacion' THEN t.fechaPublicacion " +
            "   ELSE t.notaMedia " + // Valor por defecto
            "END DESC")
    List<Texto> filtrarPorCriterio(@Param("criterio") String criterio);

    // Filtrar textos por categoría y criterio.
    @Query("SELECT t FROM Texto t WHERE " +
            "t.categoria.categoria = :categoria AND " +
            "t.estado = 'PUBLICADO' AND " +
            "t.eliminado = false " +
            "ORDER BY " +
            "CASE WHEN :criterio = 'notaMedia' THEN t.notaMedia " +
            "   WHEN :criterio = 'fechaPublicacion' THEN t.fechaPublicacion " +
            "   ELSE t.notaMedia END DESC")
    List<Texto> filtrarPorCategoriaYCriterio(
            @Param("categoria") Categoria.CategoriaEnum categoria,
            @Param("criterio") String criterio);
}
