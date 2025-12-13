package org.masouras.data.domain;

import lombok.Data;
import org.masouras.squad.printing.mssql.schema.jpa.control.ActivityType;
import org.masouras.squad.printing.mssql.schema.jpa.control.ContentType;
import org.masouras.squad.printing.mssql.schema.jpa.control.FileExtensionType;

@Data
public class FileOkDto {
    private final FileExtensionType fileExtensionType;
    private final ContentType contentType;
    private final ActivityType activityType;
}
