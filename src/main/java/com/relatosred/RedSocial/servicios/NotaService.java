package com.relatosred.RedSocial.servicios;

import com.relatosred.RedSocial.entidades.NotaTexto;
import com.relatosred.RedSocial.entidades.Texto;
import com.relatosred.RedSocial.repositorios.NotaRepository;
import com.relatosred.RedSocial.repositorios.TextoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.Optional;

public class NotaService {

    private NotaRepository notaRepository;
    private TextoRepository textoRepository;

    // Se usa constructor en lugar de @Autowired.
    public NotaService(NotaRepository notaRepository, TextoRepository textoRepository) {
        this.notaRepository = notaRepository;
        this.textoRepository = textoRepository;
    }

    @Transactional
    public NotaTexto puntuarTexto(NotaTexto notaTexto) {
        NotaTexto notaGuardada = null;
        Optional<NotaTexto> notaExistente = notaRepository.findByTextoAndUsuario(
                notaTexto.getTexto(), notaTexto.getUsuario());

        Texto texto = notaTexto.getTexto();
        // Si el texto es null devolvemos excepción.
        if (texto == null) throw new EntityNotFoundException("Texto no encontrado.");

        Double notaMedia = calcularNotaMedia(texto.getNotasTexto());
        // Actualizamos notaMedia.
        texto.setNotaMedia(notaMedia);
        // Guardamos texto con la nueva media.
        textoRepository.save(texto);

        if (notaExistente.isPresent()) {
            notaGuardada = notaExistente.get();
            // Actualizamos la nota del registro existente.
            notaGuardada.setNota(notaTexto.getNota());
            // Actualizamos el registro completo.
            notaGuardada = notaRepository.save(notaGuardada);
        } else {
            notaGuardada = notaRepository.save(notaTexto);
        }

        return notaGuardada;
    }

    public Double calcularNotaMedia(Set<NotaTexto> notasTexto){
        Double notaMedia = 0.0;

        if (notasTexto.size() > 0) {
            for (NotaTexto nota : notasTexto) {
                notaMedia += redondeoAlAlza(nota.getNota());
            }
            notaMedia = notaMedia/notasTexto.size();
        }
        // Devuelve la media redondeada.
        return redondeoADosDecimales(notaMedia);
    }

    // Para redondear al alza las notas medias.
    private Double redondeoAlAlza(Double nota) {
        if (nota < 4) nota = 4.00;
        return nota;
    }

    // Redondeo a dos decimales usando Math.round().
    private Double redondeoADosDecimales(Double nota){
        // Math.round redondea al entero más próximo.
        nota = (double) (Math.round(nota * 100)/100);
        return nota;
    }
}
