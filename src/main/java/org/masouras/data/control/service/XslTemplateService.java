package org.masouras.data.control.service;

import com.google.common.base.Preconditions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
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
    private final Map<String, Templates> xslTemplates = new HashMap<>();

    public Templates getTemplate(String xslFileName) {
        Preconditions.checkArgument(StringUtils.isNotBlank(xslFileName));
        return xslTemplates.get(xslFileName);
    }

    @PostConstruct
    public void loadXslTemplates() {
        Optional<Resource[]> resources = getRenderingResources();
        if (resources.isEmpty()) return;

        xslTemplates.putAll(
                Arrays.stream(resources.get())
                        .filter(Resource::isReadable)
                        .filter(res -> StringUtils.isNotBlank(res.getFilename()))
                        .map(res -> Map.entry(FilenameUtils.getBaseName(res.getFilename()).toUpperCase(), getResourceBytes(res)))
                        .filter(entry -> entry.getValue().isPresent())
                        .map(res -> Map.entry(res.getKey(), compileTemplates(res.getKey(), res.getValue().get())))
                        .filter(entry -> entry.getValue().isPresent())
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get())));
    }
    private Optional<Resource[]> getRenderingResources() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources("classpath*:rendering/xsl/*.*");
            return ArrayUtils.isEmpty(resources) ? Optional.empty() : Optional.of(resources);
        } catch (IOException e) {
            log.error("No XSL templates found under classpath*:rendering/xsl/ (folder missing)");
            return Optional.empty();
        }
    }

    private Optional<byte[]> getResourceBytes(Resource res) {
        try (InputStream inputStream = res.getInputStream()) {
            return Optional.of(inputStream.readAllBytes());
        } catch (IOException e) {
            if (log.isWarnEnabled()) log.warn("Failed to load XSL template: {}", res.getFilename(), e);
            return Optional.empty();
        }
    }

    private Optional<Templates> compileTemplates(String filaName, byte[] xsl) {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            return Optional.of(factory.newTemplates(new StreamSource(new ByteArrayInputStream(xsl))));
        } catch (TransformerConfigurationException e) {
            if (log.isWarnEnabled()) log.warn("Failed to compile XSL template: {}", filaName, e);
            return Optional.empty();
        }
    }
}
