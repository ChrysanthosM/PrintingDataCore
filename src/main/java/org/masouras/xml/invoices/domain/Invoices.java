package org.masouras.xml.invoices.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter @Getter
@JacksonXmlRootElement(localName = "invoices")
public class Invoices {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "invoice")
    private List<Invoice> invoice;
}

