package org.masouras.data.control.render;

import lombok.RequiredArgsConstructor;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.RendererType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PdfRendererFactory {
    private final Map<RendererType, PdfRenderer> renderers;

    public PdfRenderer getRenderer(RendererType rendererType) {
        PdfRenderer renderer = renderers.getOrDefault(rendererType,null);
        if (renderer == null) {
            throw new IllegalArgumentException("Unknown renderer: " + rendererType);
        }
        return renderer;
    }
}
