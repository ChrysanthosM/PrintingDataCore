package org.masouras.data.control.render;

import org.apache.fop.apps.FopFactory;
import org.apache.xmlgraphics.util.MimeConstants;
import org.masouras.model.mssql.schema.jpa.control.RendererType;
import org.springframework.stereotype.Component;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

@Component
public class PdfRendererUsingFOP implements PdfRenderer {
    private final TransformerFactory transformerFactory = TransformerFactory.newInstance();
    private final FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());

    @Override
    public RendererType getPdfRendererType() {
        return RendererType.FOP;
    }

    @Override
    public byte[] generate(byte[] xml, byte[] xsl) {
        try (ByteArrayInputStream xmlStream = new ByteArrayInputStream(xml);
             ByteArrayInputStream xslStream = new ByteArrayInputStream(xsl);
             ByteArrayOutputStream pdfOut = new ByteArrayOutputStream()) {

            ByteArrayOutputStream foOut = new ByteArrayOutputStream();

            // Transform XML + XSL → XSL-FO
            transformerFactory.newTransformer(new StreamSource(xslStream)).transform(
                    new StreamSource(xmlStream),
                    new StreamResult(foOut));

            // XSL-FO → PDF
            transformerFactory.newTransformer().transform(
                    new StreamSource(new ByteArrayInputStream(foOut.toByteArray())),
                    new SAXResult(fopFactory.newFop(MimeConstants.MIME_PDF, fopFactory.newFOUserAgent(), pdfOut).getDefaultHandler()));

            return pdfOut.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("FOP PDF generation failed", e);
        }
    }
}
