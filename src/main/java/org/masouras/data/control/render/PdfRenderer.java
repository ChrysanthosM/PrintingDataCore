package org.masouras.data.control.render;

import org.masouras.model.mssql.schema.jpa.control.RendererType;

public interface PdfRenderer {
    RendererType getPdfRendererType();
    byte[] generate(byte[] xml, byte[] xsl);
}
