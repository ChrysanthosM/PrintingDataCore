package org.masouras.data.control.parser;

import org.masouras.data.domain.FileProcessorResult;
import org.masouras.model.mssql.schema.jpa.control.entity.enums.FileExtensionType;

public interface FileProcessor {
    FileExtensionType getFileExtensionType();
    FileProcessorResult getFileProcessorResult(Object... params);
}
