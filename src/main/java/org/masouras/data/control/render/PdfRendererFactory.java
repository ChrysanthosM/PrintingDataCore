package org.masouras.data.control.render;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PdfRendererFactory {
    private final Map<PdfRendererType, PdfRenderer> renderers;

    public PdfRenderer getRenderer(PdfRendererType pdfRendererType) {
        PdfRenderer renderer = renderers.getOrDefault(pdfRendererType,null);
        if (renderer == null) {
            throw new IllegalArgumentException("Unknown renderer: " + pdfRendererType);
        }
        return renderer;
    }
}
