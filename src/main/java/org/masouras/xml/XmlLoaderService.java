package org.masouras.xml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class XmlLoaderService {
    private final XmlMapper xmlMapper;

    public <T> T fromXml(String xml, Class<T> type) throws JsonProcessingException {
        return xmlMapper.readValue(xml, type);
    }
}
