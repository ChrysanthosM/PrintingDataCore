package org.masouras.control.render;

import lombok.extern.slf4j.Slf4j;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.configuration.Configuration;
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

@Slf4j
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
    private static final byte[] FOP_CONFIG_XML_BYTES = FOP_CONFIG_XML.getBytes(StandardCharsets.UTF_8);


    private final FopFactory fopFactory;

    @Autowired
    public PdfRendererUsingFOP() throws Exception {
        fopFactory = FopFactory.newInstance(new File(".").toURI(), new ByteArrayInputStream(FOP_CONFIG_XML_BYTES));
        Configuration config = fopFactory.getUserConfig();
        if (log.isInfoEnabled()) log.info("FOP Configuration loaded: {}", config);
    }

    @Override
    public RendererType getPdfRendererType() {
        return RendererType.FOP;
    }

    @Override
    public byte[] generate(Templates templates, byte[] xml) {
        try (ByteArrayInputStream xmlStream = new ByteArrayInputStream(xml);
             ByteArrayOutputStream pdfOut = new ByteArrayOutputStream()) {
            Fop fop = getFop(pdfOut);

            Transformer transformer = templates.newTransformer();
            transformer.transform(
                    new StreamSource(xmlStream),
                    new SAXResult(fop.getDefaultHandler()));

            return pdfOut.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("FOP PDF generation failed", e);
        }
    }
    private Fop getFop(ByteArrayOutputStream pdfOut) throws FOPException {
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
        Fop fop;
        synchronized (fopFactory) {
            fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, pdfOut);
        }
        return fop;
    }
}
