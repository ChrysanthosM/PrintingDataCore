package org.masouras.pdf;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;

@Service
public class OpenPdfService {
    public byte[] generatePdf(InputStream xml, InputStream xsl) throws Exception {
        // Step 1: Transform XML with XSLT → HTML
        String htmlContent = getHtmlContent(xml, xsl);
        // Step 2: Render HTML → PDF using OpenPDF + Flying Saucer
        ByteArrayOutputStream pdfOut = getPdfOut(htmlContent);

        return pdfOut.toByteArray();
    }

    private @NonNull ByteArrayOutputStream getPdfOut(String htmlContent) {
        ByteArrayOutputStream pdfOut = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(pdfOut);
        return pdfOut;
    }

    private String getHtmlContent(InputStream xml, InputStream xsl) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xsl));
        StringWriter writer = new StringWriter();
        transformer.transform(new StreamSource(xml), new StreamResult(writer));
        return writer.toString();
    }

}
