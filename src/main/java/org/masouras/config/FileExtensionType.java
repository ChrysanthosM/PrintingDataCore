package org.masouras.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.masouras.printing.sqlite.schema.control.ActivityType;

@RequiredArgsConstructor
@Getter
public enum FileExtensionType {
    XML("xml", ActivityType.NEW_ENTRY_XML),
    ;

    private final String extension;
    private final ActivityType activityType;

}
