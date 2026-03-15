package org.masouras.control.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class XmlConverterService {
    private final XmlMapper xmlMapper;

    public <T> T fromXml(String xml, Class<T> type) throws JsonProcessingException {
        return xmlMapper.readValue(xml, type);
    }

    public byte[] toXmlBytes(Object value) {
        try {
            return xmlMapper.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize object to XML", e);
        }
    }
}
