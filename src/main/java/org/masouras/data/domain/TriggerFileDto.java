package org.masouras.data.domain;

import lombok.Data;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.ActivityType;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.ContentType;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.FileExtensionType;

@Data
public class TriggerFileDto {
    private final FileExtensionType fileExtensionType;
    private final ContentType contentType;
    private final ActivityType activityType;
}
