package org.masouras.data.control.render;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Component;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Component
public class PdfRendererUsingOpenHtmlToPdf implements PdfRenderer {
    @Override
    public PdfRendererType getPdfRendererType() {
        return PdfRendererType.OPEN_HTML_TO_PDF;
    }

    @Override
    public byte[] generate(byte[] xml, byte[] xsl) {
        try {
            String html = transformXmlToHtml(xml, xsl);

            ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(pdfOut);
            builder.run();

            return pdfOut.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("OpenHTMLtoPDF generation failed", e);
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
