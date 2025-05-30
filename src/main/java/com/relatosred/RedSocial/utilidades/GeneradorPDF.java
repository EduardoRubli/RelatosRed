package com.relatosred.RedSocial.utilidades;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class GeneradorPDF {

    public static byte[] crearDesdeTexto(String titulo, String autor,
                                         LocalDate fecha, String contenido) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new PieHandler());
        Document doc = new Document(pdf);

        doc.setFont(PdfFontFactory.createFont());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        String fechaStr = fecha.format(formatter);

        // Título grande.
        Paragraph tituloParrafo = new Paragraph(titulo)
                .setFontSize(36)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(150)
                .setMarginBottom(20);
        doc.add(tituloParrafo);

        // Línea separadora.
        LineSeparator separator = new LineSeparator(new SolidLine())
                .setStrokeColor(new DeviceGray(0.7f))
                .setMinWidth(1)
                .setWidth(UnitValue.createPercentValue(50))
                .setMarginBottom(20);
        doc.add(new Paragraph().add(separator).setTextAlignment(TextAlignment.CENTER));

        // Autor.
        Paragraph autorParrafo = new Paragraph("Autor: " + autor)
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        doc.add(autorParrafo);

        // Fecha en cursiva.
        Paragraph fechaParrafo = new Paragraph(fechaStr)
                .setFontSize(12)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(100);
        doc.add(fechaParrafo);

        // Salto de página a contenido.
        doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

        // Contenido del relato.
        for (String linea : contenido.split("\n")) {
            doc.add(new Paragraph(linea)
                    .setFontSize(12)
                    .setMarginBottom(4));
        }

        doc.close();
        return baos.toByteArray();
    }
}
