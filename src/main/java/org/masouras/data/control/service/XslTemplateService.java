package org.masouras.data.control.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.masouras.model.mssql.schema.jpa.control.XslType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class XslTemplateService {
    private final Map<String, byte[]> xslTemplates = new HashMap<>();

    public byte[] getTemplate(XslType xslType) {
        return xslTemplates.get(xslType.getCode());
    }

    @PostConstruct
    public void loadXslTemplates() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        Resource[] resources = resolver.getResources("classpath:rendering/xsl/*.*");
        xslTemplates.putAll(
                Arrays.stream(resources)
                        .filter(Resource::isReadable)
                        .filter(res -> StringUtils.isNotBlank(res.getFilename()))
                        .map(res -> Map.entry(FilenameUtils.getBaseName(res.getFilename()).toUpperCase(), getResourceBytes(res)))
                        .filter(entry -> entry.getValue().isPresent())
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get())));
    }
    private Optional<byte[]> getResourceBytes(Resource res) {
        try (InputStream inputStream = res.getInputStream()) {
            return Optional.of(inputStream.readAllBytes());
        } catch (IOException e) {
            if (log.isWarnEnabled()) log.warn("Failed to load XSL template: {}", res.getFilename(), e);
            return Optional.empty();
        }
    }
}
