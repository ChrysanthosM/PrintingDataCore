package org.masouras.config;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.masouras.printing.sqlite.schema.control.ActivityType;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum FileExtensionType {
    XML("xml", ActivityType.NEW_ENTRY_XML),
    ;

    private final String extension;
    private final ActivityType activityType;

    private static final Map<String, FileExtensionType> EXTENSION_MAP = Arrays.stream(values()).collect(Collectors.toMap(FileExtensionType::getExtension, e -> e));
    public static FileExtensionType getFormExtension(@Nullable String extension) {
        return StringUtils.isBlank(extension) ? null : EXTENSION_MAP.getOrDefault(extension.toLowerCase(), null);
    }
}
