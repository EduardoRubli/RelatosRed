package com.relatosred.RedSocial.utilidades;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.colors.ColorConstants;

public class PieHandler implements IEventHandler {

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        var pdfDoc = docEvent.getDocument();
        var page = docEvent.getPage();
        Rectangle pageSize = page.getPageSize();

        PdfCanvas pdfCanvas = new PdfCanvas(
                page.newContentStreamAfter(),
                page.getResources(),
                pdfDoc
        );

        // Constructor correcto: (PdfCanvas, Rectangle, boolean).
        Canvas canvas = new Canvas(pdfCanvas, pageSize, true);
        canvas.showTextAligned(
                new Paragraph("Descargado de Relatosfera.com")
                        .setFontSize(8)
                        .setFontColor(ColorConstants.DARK_GRAY),
                pageSize.getWidth() / 2,
                20,
                TextAlignment.CENTER
        );
        canvas.close();
    }
}
