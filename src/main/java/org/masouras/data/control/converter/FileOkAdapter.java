package org.masouras.data.control.converter;

import lombok.experimental.UtilityClass;
import org.masouras.data.domain.FileOkDto;
import org.masouras.data.domain.FileOkRaw;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.ActivityType;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.ContentType;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.FileExtensionType;

@UtilityClass
public class FileOkAdapter {
    public static FileOkDto toFileOkDto(FileOkRaw fileOkRaw) {
        return new FileOkDto(
                FileExtensionType.getFromCode(fileOkRaw.getFileExtension()),
                ContentType.getFromCode(fileOkRaw.getContentType()),
                ActivityType.getFromCode(fileOkRaw.getActivityType())
        );
    }
}
