package org.masouras.data.control.parser;

import org.masouras.data.domain.FileProcessorResult;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.FileExtensionType;

public sealed interface FileProcessor permits FileProcessorXML {
    FileExtensionType getFileExtensionType();
    FileProcessorResult getFileProcessorResult(Object... params);
}
