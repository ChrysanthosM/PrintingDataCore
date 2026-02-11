package org.masouras.data.control.parser;

import org.masouras.data.domain.FileValidatorResult;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.FileExtensionType;

public interface FileValidator {
    FileExtensionType getFileExtensionType();
    FileValidatorResult getValidatedResult(Object... params);
}
