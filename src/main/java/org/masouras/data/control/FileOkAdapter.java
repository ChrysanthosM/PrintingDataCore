package org.masouras.data.control;

import lombok.experimental.UtilityClass;
import org.masouras.data.domain.FileOkDto;
import org.masouras.data.domain.FileOkRaw;
import org.masouras.squad.printing.mssql.schema.jpa.control.ActivityType;
import org.masouras.squad.printing.mssql.schema.jpa.control.ContentType;

@UtilityClass
public class FileOkAdapter {
    public static FileOkDto toFileOkDto(FileOkRaw fileOkRaw) {
        return new FileOkDto(
                org.masouras.squad.printing.mssql.schema.jpa.control.FileExtensionType.getFromCode(fileOkRaw.getFileExtension()),
                ContentType.getFromCode(fileOkRaw.getContentType()),
                ActivityType.getFromCode(fileOkRaw.getActivityType())
        );
    }
}
