package org.masouras.data.control.render;

import org.masouras.model.mssql.schema.jpa.control.entity.enums.RendererType;
import org.openpdf.text.Document;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Component
public class PdfRendererUsingOpenPdf implements PdfRenderer {
    private final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    @Override
    public RendererType getPdfRendererType() {
        return RendererType.OPEN_PDF;
    }

    @Override
    public byte[] generate(byte[] xml, byte[] xsl) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document();
            PdfWriter.getInstance(document, out);

            document.open();
            document.add(new Paragraph(transformXmlToText(xml, xsl)));
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("OpenPDF generation failed", e);
        }
    }

    private String transformXmlToText(byte[] xml, byte[] xsl) {
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
