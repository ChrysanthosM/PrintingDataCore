package org.masouras.xml.invoices.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class Footer {
    @JacksonXmlProperty(localName = "total")
    private double total;
}
