package org.masouras.data.control.render;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.masouras.model.mssql.schema.jpa.control.RendererType;
import org.springframework.stereotype.Component;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Component
public class PdfRendererUsingOpenHtmlToPdf implements PdfRenderer {
    private final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    @Override
    public RendererType getPdfRendererType() {
        return RendererType.OPEN_HTML_TO_PDF;
    }

    @Override
    public byte[] generate(byte[] xml, byte[] xsl) {
        try {
            ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(transformXmlToHtml(xml, xsl), null);
            builder.toStream(pdfOut);
            builder.run();

            return pdfOut.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("OpenHTMLtoPDF generation failed", e);
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
