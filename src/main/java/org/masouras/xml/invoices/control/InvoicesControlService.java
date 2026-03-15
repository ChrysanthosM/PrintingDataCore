package org.masouras.xml.invoices.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.masouras.control.converter.XmlConverterService;
import org.masouras.xml.invoices.domain.Invoice;
import org.masouras.xml.invoices.domain.Invoices;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoicesControlService {
    private final XmlConverterService xmlConverterService;

    public List<Invoice> getInvoiceList(byte[] fromXmlByteArray) {
        Validate.isTrue(ArrayUtils.isNotEmpty(fromXmlByteArray), "fromXmlByteArray is null");
        String xml = new String(fromXmlByteArray, StandardCharsets.UTF_8);
        return getInvoiceList(xml);
    }
    public List<Invoice> getInvoiceList(String fromXmlString) {
        Validate.notBlank(fromXmlString);
        try {
            return xmlConverterService.fromXml(fromXmlString, Invoices.class).getInvoice();
        } catch (JsonProcessingException e) {
            log.error("getInvoiceList failed with message: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
