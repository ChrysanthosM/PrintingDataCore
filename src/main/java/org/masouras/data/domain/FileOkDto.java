package org.masouras.data.domain;

import lombok.Data;
import org.masouras.data.control.FileExtensionType;
import org.masouras.squad.printing.mssql.schema.jpa.control.ActivityType;
import org.masouras.squad.printing.mssql.schema.jpa.control.ContentType;

@Data
public class FileOkDto {
    private final FileExtensionType fileExtensionType;
    private final ContentType contentType;
    private final ActivityType activityType;
}
