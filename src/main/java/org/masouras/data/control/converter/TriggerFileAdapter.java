package org.masouras.data.control.converter;

import lombok.experimental.UtilityClass;
import org.masouras.data.domain.TriggerFileDto;
import org.masouras.data.domain.TriggerFileRaw;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.ActivityType;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.ContentType;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.FileExtensionType;
import org.masouras.model.mssql.schema.jpa.control.util.EnumUtil;

@UtilityClass
public class TriggerFileAdapter {
    public static TriggerFileDto toTriggerFileDto(TriggerFileRaw triggerFileRaw) {
        return new TriggerFileDto(
                EnumUtil.fromCode(FileExtensionType.class, triggerFileRaw.getFileExtension()),
                EnumUtil.fromCode(ContentType.class, triggerFileRaw.getContentType()),
                EnumUtil.fromCode(ActivityType.class, triggerFileRaw.getActivityType())
        );
    }
}
