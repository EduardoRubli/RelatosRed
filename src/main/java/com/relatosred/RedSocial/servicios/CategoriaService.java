package com.relatosred.RedSocial.servicios;

import com.relatosred.RedSocial.entidades.Categoria;
import com.relatosred.RedSocial.repositorios.CategoriaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CategoriaService {

    private CategoriaRepository categoriaRepository;

    // Se usa constructor en lugar de @Autowired.
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public Categoria obtenerOCrearCategoria(Categoria.CategoriaEnum categoriaEnum,
                                            Categoria.SubcategoriaEnum subcategoriaEnum) {
        // Busca la categoría existente.
        Optional<Categoria> categoriaExistente =
                categoriaRepository.findByCategoriaAndSubcategoria(categoriaEnum, subcategoriaEnum);

        if (categoriaExistente.isPresent()) {
            // Devuelve categoría existente.
            return categoriaExistente.get();
        } else {
            // Crea y persiste una nueva categoría.
            Categoria nuevaCategoria = new Categoria(categoriaEnum, subcategoriaEnum);
            return categoriaRepository.save(nuevaCategoria);
        }
    }
}
