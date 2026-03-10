package org.masouras.control.parser;

import org.masouras.domain.FileProcessorResult;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.FileExtensionType;

public sealed interface FileProcessor permits FileProcessorXML {
    FileExtensionType getFileExtensionType();
    FileProcessorResult getFileProcessorResult(Object... params);
}
