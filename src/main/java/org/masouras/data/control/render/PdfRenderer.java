package org.masouras.data.control.render;

import org.masouras.model.mssql.schema.jpa.control.entity.enums.RendererType;

import javax.xml.transform.Templates;

public sealed interface PdfRenderer permits PdfRendererUsingFOP, PdfRendererUsingFlyingSaucer {
    RendererType getPdfRendererType();
    byte[] generate(Templates templates, byte[] xml);
}
