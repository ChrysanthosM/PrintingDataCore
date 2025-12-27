package org.masouras.data.control.converter;

import lombok.experimental.UtilityClass;
import org.masouras.data.domain.FileOkDto;
import org.masouras.data.domain.FileOkRaw;
import org.masouras.model.mssql.schema.jpa.control.ActivityType;
import org.masouras.model.mssql.schema.jpa.control.ContentType;
import org.masouras.model.mssql.schema.jpa.control.FileExtensionType;

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
