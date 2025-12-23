package org.masouras.data.control.render;

import org.masouras.squad.printing.mssql.schema.jpa.control.RendererType;

public interface PdfRenderer {
    RendererType getPdfRendererType();
    byte[] generate(byte[] xml, byte[] xsl);
}
