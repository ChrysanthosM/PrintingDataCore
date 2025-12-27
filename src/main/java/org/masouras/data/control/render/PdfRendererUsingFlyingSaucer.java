package org.masouras.data.control.render;

import org.masouras.model.mssql.schema.jpa.control.entity.enums.RendererType;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Component
public class PdfRendererUsingFlyingSaucer implements PdfRenderer {
    private final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    @Override
    public RendererType getPdfRendererType() {
        return RendererType.FLYING_SAUCER;
    }

    @Override
    public byte[] generate(byte[] xml, byte[] xsl) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(transformXmlToHtml(xml, xsl));
            renderer.layout();
            renderer.createPDF(out);

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Flying Saucer PDF generation failed", e);
        }
    }

    private String transformXmlToHtml(byte[] xml, byte[] xsl) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            transformerFactory.newTransformer(new StreamSource(new ByteArrayInputStream(xsl))).transform(
                    new StreamSource(new ByteArrayInputStream(xml)),
                    new StreamResult(out));
            return out.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("XSLT transform failed", e);
        }
    }

}
