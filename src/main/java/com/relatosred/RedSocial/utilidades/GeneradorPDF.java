package com.relatosred.RedSocial.utilidades;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class GeneradorPDF {
    private static java.util.List<String> dividirLinea(String linea, PDType1Font font, float fontSize, float maxWidth) throws IOException {
        java.util.List<String> resultado = new java.util.ArrayList<>();
        String[] palabras = linea.split(" ");
        StringBuilder actual = new StringBuilder();
        for (String palabra : palabras) {
            String prueba = actual.length() == 0 ? palabra : actual + " " + palabra;
            float ancho = font.getStringWidth(prueba) / 1000 * fontSize;
            if (ancho > maxWidth) {
                if (actual.length() > 0) {
                    resultado.add(actual.toString());
                    actual = new StringBuilder(palabra);
                } else {
                    resultado.add(palabra); // palabra sola es demasiado larga
                    actual = new StringBuilder();
                }
            } else {
                if (actual.length() > 0) actual.append(" ");
                actual.append(palabra);
            }
        }
        if (actual.length() > 0) resultado.add(actual.toString());
        return resultado;
    }

    private static void dibujarPie(PDPageContentStream cs, PDPage pagina)
            throws IOException {
        final float margin = 50;
        final float fontSize = 12f;
        final float yPie = margin -8; // Un poco arriba del margen inferior

        // Color gris para pie de página.
        cs.setNonStrokingColor(0.5f, 0.5f, 0.5f);
        cs.setFont(PDType1Font.HELVETICA, fontSize);
        float anchoTexto = PDType1Font.HELVETICA.getStringWidth("Descargado de relatosfera.com") / 1000 * fontSize;
        float x = (pagina.getMediaBox().getWidth() - anchoTexto) / 2;

        cs.beginText();
        cs.newLineAtOffset(x, yPie);
        cs.showText("Descargado de relatosfera.com");
        cs.endText();
    }

    public static byte[] crearDesdeTexto(
            String titulo,
            String autor,
            LocalDate fecha,
            String contenido
    ) throws IOException {
        // Crear documento y streams
        PDDocument document = new PDDocument();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Formatter de fecha en español
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        String fechaStr = fecha.format(formatter);

        // ======= Portada =======
        PDPage portada = new PDPage(PDRectangle.LETTER);
        document.addPage(portada);

        try (PDPageContentStream cs = new PDPageContentStream(document, portada)) {
            float pageWidth = portada.getMediaBox().getWidth();
            float y = portada.getMediaBox().getHeight() - 150;

            // Título grande
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, 36);
            float titleWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(titulo) / 1000 * 36;
            cs.newLineAtOffset((pageWidth - titleWidth) / 2, y);
            cs.showText(titulo);
            cs.endText();

            // Línea separadora
            y -= 50;
            float lineWidth = pageWidth * 0.5f;
            float xStart = (pageWidth - lineWidth) / 2;
            cs.setStrokingColor(128);
            cs.setLineWidth(1);
            cs.moveTo(xStart, y);
            cs.lineTo(xStart + lineWidth, y);
            cs.stroke();

            // Autor
            y -= 30;
            String autorText = "Autor: " + autor;
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, 18);
            float autorWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(autorText) / 1000 * 18;
            cs.newLineAtOffset((pageWidth - autorWidth) / 2, y);
            cs.showText(autorText);
            cs.endText();

            // Fecha
            y -= 20;
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_OBLIQUE, 12);
            float fechaWidth = PDType1Font.HELVETICA_OBLIQUE.getStringWidth(fechaStr) / 1000 * 12;
            cs.newLineAtOffset((pageWidth - fechaWidth) / 2, y);
            cs.showText(fechaStr);
            cs.endText();
        }

        // ======= Contenido =======
        PDPage pagina = new PDPage(PDRectangle.LETTER);
        document.addPage(pagina);
        final float margin = 50;
        final float leading = 14;
        final float startY = pagina.getMediaBox().getHeight() - margin;
        float cursorY = startY;

        PDType1Font font = PDType1Font.HELVETICA;
        float fontSize = 12f;
        final float width = pagina.getMediaBox().getWidth() - 2 * margin;

        PDPageContentStream cs = new PDPageContentStream(document, pagina);
        cs.beginText();
        cs.setFont(font, fontSize);
        cs.newLineAtOffset(margin, cursorY);

        boolean cambioPagina = false;

        for (String linea : contenido.split("\\r?\\n")) {
            if (linea.trim().isEmpty()) {
                // Línea vacía → salto doble.
                cursorY -= leading;
                cs.newLineAtOffset(0, -leading);
                continue;
            }

            java.util.List<String> subLineas = dividirLinea(linea, font, fontSize, width);
            for (String subLinea : subLineas) {
                final float espacioExtraPie = 25f;
                if (cursorY - leading < margin + espacioExtraPie) {
                    cs.endText();

                    dibujarPie(cs, pagina);
                    cs.close();

                    pagina = new PDPage(PDRectangle.LETTER);
                    document.addPage(pagina);
                    cs = new PDPageContentStream(document, pagina);
                    cursorY = startY;
                    cs.beginText();
                    cs.setFont(font, fontSize);
                    cs.newLineAtOffset(margin, cursorY);
                    // Control de cambio de página.
                    cambioPagina = true;
                }
                cs.showText(subLinea);
                cs.newLineAtOffset(0, -leading);
                cursorY -= leading;
            }
        }
        cs.endText();
        if (cambioPagina) {
            dibujarPie(cs, pagina);
        }
        cs.close();

        // Guardar y cerrar
        document.save(baos);
        document.close();
        return baos.toByteArray();
    }
}
