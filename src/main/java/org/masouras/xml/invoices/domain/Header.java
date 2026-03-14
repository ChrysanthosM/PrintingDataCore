package org.masouras.xml.invoices.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class Header {
    @JacksonXmlProperty(localName = "company")
    private String company;

    @JacksonXmlProperty(localName = "date")
    private String date;

    @JacksonXmlProperty(localName = "invoiceNumber")
    private String invoiceNumber;
}
