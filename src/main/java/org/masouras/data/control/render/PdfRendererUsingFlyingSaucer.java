package org.masouras.data.control.render;

import org.masouras.model.mssql.schema.jpa.control.entity.enums.RendererType;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Component
public non-sealed class PdfRendererUsingFlyingSaucer implements PdfRenderer {

    @Override
    public RendererType getPdfRendererType() {
        return RendererType.FLYING_SAUCER;
    }

    @Override
    public byte[] generate(Templates templates, byte[] xml) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            ITextRenderer iTextRenderer = new ITextRenderer();
            iTextRenderer.setDocumentFromString(transformXmlToHtml(templates, xml));
            iTextRenderer.layout();
            iTextRenderer.createPDF(outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Flying Saucer PDF generation failed", e);
        }
    }

    private String transformXmlToHtml(Templates templates, byte[] xml) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Transformer transformer = templates.newTransformer();
            transformer.transform(
                    new StreamSource(new ByteArrayInputStream(xml)),
                    new StreamResult(outputStream));

            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("transformXmlToHtml transform failed", e);
        }
    }
}
