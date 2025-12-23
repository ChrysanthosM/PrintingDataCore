package org.masouras.data.control.render;

import org.masouras.squad.printing.mssql.schema.jpa.control.RendererType;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Component
public class PdfRendererUsingFlyingSaucer implements PdfRenderer {
    @Override
    public RendererType getPdfRendererType() {
        return RendererType.FLYING_SAUCER;
    }

    @Override
    public byte[] generate(byte[] xml, byte[] xsl) {
        try {
            String html = transformXmlToHtml(xml, xsl);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(out);

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Flying Saucer PDF generation failed", e);
        }
    }

    private String transformXmlToHtml(byte[] xml, byte[] xsl) {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(new ByteArrayInputStream(xsl)));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            transformer.transform(new StreamSource(new ByteArrayInputStream(xml)), new StreamResult(out));

            return out.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("XSLT transform failed", e);
        }
    }

}
