package org.masouras.data.control.render;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.configuration.DefaultConfigurationBuilder;
import org.apache.xmlgraphics.util.MimeConstants;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.RendererType;
import org.springframework.stereotype.Component;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

@Component
public class PdfRendererUsingFOP implements PdfRenderer {
    private final FopFactory fopFactory;

    public PdfRendererUsingFOP() throws Exception {
        FopFactoryBuilder builder = new FopFactoryBuilder(new File(".").toURI())
                .setConfiguration(new DefaultConfigurationBuilder().build(getClass().getResourceAsStream("/rendering/config/fop/fop.xconf")));
        fopFactory = builder.build();
    }

    @Override
    public RendererType getPdfRendererType() {
        return RendererType.FOP;
    }

    @Override
    public byte[] generate(byte[] xml, byte[] xsl) {
        try (ByteArrayInputStream xmlStream = new ByteArrayInputStream(xml);
             ByteArrayInputStream xslStream = new ByteArrayInputStream(xsl);
             ByteArrayOutputStream pdfOut = new ByteArrayOutputStream()) {

            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, fopFactory.newFOUserAgent(), pdfOut);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(xslStream));
            transformer.transform(
                    new StreamSource(xmlStream),
                    new SAXResult(fop.getDefaultHandler()));

            return pdfOut.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("FOP PDF generation failed", e);
        }
    }
}
