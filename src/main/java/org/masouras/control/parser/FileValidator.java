package org.masouras.control.parser;

import org.masouras.domain.FileValidatorResult;
import org.masouras.model.maria.schema.jpa.control.entity.enums.FileExtensionType;

public sealed interface FileValidator permits FileValidatorXML {
    FileExtensionType getFileExtensionType();
    FileValidatorResult getValidatedResult(Object... params);
}
