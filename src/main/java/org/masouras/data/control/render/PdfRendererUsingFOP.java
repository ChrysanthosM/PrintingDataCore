package org.masouras.data.control.render;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.xmlgraphics.util.MimeConstants;
import org.masouras.squad.printing.mssql.schema.jpa.control.RendererType;
import org.springframework.stereotype.Component;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

@Component
public class PdfRendererUsingFOP implements PdfRenderer {
    @Override
    public RendererType getPdfRendererType() {
        return RendererType.FOP;
    }

    @Override
    public byte[] generate(byte[] xml, byte[] xsl) {
        try (ByteArrayInputStream xmlStream = new ByteArrayInputStream(xml);
             ByteArrayInputStream xslStream = new ByteArrayInputStream(xsl);
             ByteArrayOutputStream pdfOut = new ByteArrayOutputStream()) {

            // Transform XML + XSL → XSL-FO
            ByteArrayOutputStream foOut = new ByteArrayOutputStream();

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer(new StreamSource(xslStream));
            transformer.transform(new StreamSource(xmlStream), new StreamResult(foOut));

            // XSL-FO → PDF
            FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, pdfOut);

            Transformer foTransformer = tf.newTransformer();
            Source src = new StreamSource(new ByteArrayInputStream(foOut.toByteArray()));
            Result res = new SAXResult(fop.getDefaultHandler());

            foTransformer.transform(src, res);

            return pdfOut.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("FOP PDF generation failed", e);
        }
    }
}
