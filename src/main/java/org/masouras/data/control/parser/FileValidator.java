package org.masouras.data.control.parser;

import org.masouras.data.domain.FileValidatorResult;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.FileExtensionType;

public sealed interface FileValidator permits FileValidatorXML {
    FileExtensionType getFileExtensionType();
    FileValidatorResult getValidatedResult(Object... params);
}
