package org.masouras.data.control.render;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.RendererType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PdfRendererFactory {
    private final List<PdfRenderer> pdfRenderers;
    private final Map<RendererType, PdfRenderer> pdfRendererMap;

    public PdfRenderer getRenderer(RendererType rendererType) {
        PdfRenderer renderer = pdfRendererMap.getOrDefault(rendererType,null);
        if (renderer == null) {
            throw new IllegalArgumentException("Unknown renderer: " + rendererType.name());
        }
        return renderer;
    }

    @PostConstruct
    private void init() {
        pdfRenderers.forEach(pdfRenderer -> pdfRendererMap.put(pdfRenderer.getPdfRendererType(), pdfRenderer));
    }
}
