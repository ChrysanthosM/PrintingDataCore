package org.masouras.data.control.render;

public interface PdfRenderer {
    PdfRendererType getPdfRendererType();
    byte[] generate(byte[] xml, byte[] xsl);
}
