package org.masouras.data.control.render;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PdfRendererService {
    private final PdfRendererFactory rendererFactory;

    public byte[] generatePdf(PdfRendererType pdfRendererType, byte[] xml, byte[] xsl) {
        return rendererFactory.getRenderer(pdfRendererType).generate(xml, xsl);
    }
}
