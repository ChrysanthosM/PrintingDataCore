package org.masouras.data.control.render;

import lombok.RequiredArgsConstructor;
import org.masouras.model.mssql.schema.jpa.control.RendererType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PdfRendererService {
    private final PdfRendererFactory rendererFactory;

    public byte[] generatePdf(RendererType rendererType, byte[] xml, byte[] xsl) {
        return rendererFactory.getRenderer(rendererType).generate(xml, xsl);
    }
}
