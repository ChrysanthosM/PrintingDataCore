package org.masouras.boundary;

import org.masouras.model.mssql.schema.jpa.control.entity.PrintingDataEntity;

public interface PrintingDataEntityValidator {
    PrintingDataEntity validatePrintingDataEntity(PrintingDataEntity printingDataEntity);
}
