package org.masouras.xml.invoices.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.masouras.xml.XmlLoaderService;
import org.masouras.xml.invoices.domain.Invoice;
import org.masouras.xml.invoices.domain.Invoices;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoicesControlService {
    private final XmlLoaderService xmlLoaderService;

    public List<Invoice> getInvoiceList(String fromXmlString) throws JsonProcessingException {
        Validate.notBlank(fromXmlString);
        Invoices invoices = xmlLoaderService.fromXml(fromXmlString, Invoices.class);
        return invoices.getInvoice();
    }
}
