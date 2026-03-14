package org.masouras.xml.invoices.domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class Item {
    @JacksonXmlProperty(localName = "description")
    private String description;

    @JacksonXmlProperty(localName = "quantity")
    private int quantity;

    @JacksonXmlProperty(localName = "price")
    private double price;
}
