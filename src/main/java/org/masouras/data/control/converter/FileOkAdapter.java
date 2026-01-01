package org.masouras.data.control.converter;

import lombok.experimental.UtilityClass;
import org.masouras.data.domain.FileOkDto;
import org.masouras.data.domain.FileOkRaw;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.ActivityType;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.ContentType;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.FileExtensionType;
import org.masouras.model.mssql.schema.jpa.control.util.EnumUtil;

@UtilityClass
public class FileOkAdapter {
    public static FileOkDto toFileOkDto(FileOkRaw fileOkRaw) {
        return new FileOkDto(
                EnumUtil.fromCode(FileExtensionType.class, fileOkRaw.getFileExtension()),
                EnumUtil.fromCode(ContentType.class, fileOkRaw.getContentType()),
                EnumUtil.fromCode(ActivityType.class, fileOkRaw.getActivityType())
        );
    }
}
