package org.masouras.data.control.render;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.configuration.Configuration;
import org.apache.fop.configuration.DefaultConfigurationBuilder;
import org.apache.xmlgraphics.util.MimeConstants;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.RendererType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;

@Component
public non-sealed class PdfRendererUsingFOP implements PdfRenderer {
    private static final String FOP_CONFIG_XML = """
        <fop version="1.0">
          <renderers>
            <renderer mime="application/pdf">
              <fonts>
                <auto-detect embedding-mode="subset"/>
              </fonts>
            </renderer>
          </renderers>
        </fop>
        """;

    private final FopFactory fopFactory;

    @Autowired
    public PdfRendererUsingFOP() throws Exception {
        DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
        Configuration cfg = cfgBuilder.build(new ByteArrayInputStream(FOP_CONFIG_XML.getBytes(StandardCharsets.UTF_8)));
        FopFactoryBuilder builder = new FopFactoryBuilder(new File(".").toURI()).setConfiguration(cfg);
        this.fopFactory = builder.build();
    }

    @Override
    public RendererType getPdfRendererType() {
        return RendererType.FOP;
    }

    @Override
    public byte[] generate(Templates templates, byte[] xml) {
        try (ByteArrayInputStream xmlStream = new ByteArrayInputStream(xml);
             ByteArrayOutputStream pdfOut = new ByteArrayOutputStream()) {

            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, fopFactory.newFOUserAgent(), pdfOut);

            Transformer transformer = templates.newTransformer();
            transformer.transform(
                    new StreamSource(xmlStream),
                    new SAXResult(fop.getDefaultHandler()));

            return pdfOut.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("FOP PDF generation failed", e);
        }
    }
}
