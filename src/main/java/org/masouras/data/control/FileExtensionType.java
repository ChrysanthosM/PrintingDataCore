package org.masouras.data.control;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum FileExtensionType {
    XML("xml"),
    ;

    private final String extension;

    private static final Map<String, FileExtensionType> EXTENSION_MAP = Arrays.stream(values()).collect(Collectors.toMap(FileExtensionType::getExtension, e -> e));
    public static FileExtensionType getFromCode(@Nullable String extension) {
        return StringUtils.isBlank(extension) ? null : EXTENSION_MAP.getOrDefault(extension.toLowerCase(), null);
    }
}
