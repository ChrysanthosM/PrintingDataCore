package org.masouras.xml.invoices.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
@JacksonXmlRootElement(localName = "invoice")
public class Invoice {
    @JacksonXmlProperty(localName = "header")
    private Header header;

    @JacksonXmlProperty(localName = "items")
    private Items items;

    @JacksonXmlProperty(localName = "footer")
    private Footer footer;
}
