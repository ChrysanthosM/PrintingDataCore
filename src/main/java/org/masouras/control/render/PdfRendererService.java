package org.masouras.control.render;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.RendererType;
import org.springframework.stereotype.Service;

import javax.xml.transform.Templates;

@Service
@RequiredArgsConstructor
public class PdfRendererService {
    private final PdfRendererFactory rendererFactory;

    @Timed("generatePdf")
    @Counted("generatePdf")
    public byte[] generatePdf(RendererType rendererType, Templates templates, byte[] xml) {
        return rendererFactory.getRenderer(rendererType).generate(templates, xml);
    }
}
