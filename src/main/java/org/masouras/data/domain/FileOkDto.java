package org.masouras.data.domain;

import lombok.Data;
import org.masouras.config.FileExtensionType;
import org.masouras.printing.sqlite.schema.control.ActivityType;
import org.masouras.printing.sqlite.schema.control.ContentType;

@Data
public class FileOkDto {
    private final FileExtensionType fileExtensionType;
    private final ContentType contentType;
    private final ActivityType activityType;
}
