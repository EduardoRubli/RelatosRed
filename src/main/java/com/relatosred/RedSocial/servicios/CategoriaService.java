package com.relatosred.RedSocial.servicios;

import com.relatosred.RedSocial.entidades.Categoria;
import com.relatosred.RedSocial.repositorios.CategoriaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {

    private CategoriaRepository categoriaRepository;

    // Se usa constructor en lugar de @Autowired.
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public Categoria crearCategoria(Categoria categoria){



        return categoria;
    }
}
